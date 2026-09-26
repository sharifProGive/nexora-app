package com.example.data.repository

import com.example.data.database.NexoraDatabase
import com.example.data.model.ActiveSessionEntity
import com.example.data.model.ChannelEntity
import com.example.data.model.UserEntity
import com.example.data.model.VerificationCodeEntity
import com.example.security.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import java.util.UUID

class AuthRepository(private val database: NexoraDatabase) {

    private val userDao = database.userDao()
    private val verificationDao = database.verificationDao()
    private val sessionDao = database.sessionDao()

    suspend fun initSeedDataIfNeeded() = withContext(Dispatchers.IO) {
        // Clean initialization without hardcoded mock users
    }

    suspend fun getActiveUser(): UserEntity? = withContext(Dispatchers.IO) {
        val activeSession = sessionDao.getActiveSession() ?: return@withContext null
        userDao.getUserById(activeSession.userId)
    }

    fun observeActiveSession(): Flow<ActiveSessionEntity?> = sessionDao.observeActiveSession()

    suspend fun isHandleTaken(handle: String): Boolean = withContext(Dispatchers.IO) {
        val formatted = SecurityUtils.formatHandle(handle)
        userDao.countUsersWithHandle(formatted) > 0
    }

    suspend fun isNameTaken(name: String): Boolean = withContext(Dispatchers.IO) {
        userDao.countUsersWithName(name.trim()) > 0
    }

    suspend fun getAvailableHandleSuggestions(baseNameOrHandle: String): List<String> = withContext(Dispatchers.IO) {
        val clean = baseNameOrHandle.replace("@", "").lowercase().filter { it.isLetterOrDigit() }
        val candidates = listOf(
            "@${clean}_nex",
            "@the_$clean",
            "@${clean}99",
            "@iam$clean",
            "@nex_$clean"
        )
        candidates.filter { !isHandleTaken(it) }
    }

    suspend fun sendVerificationCode(
        target: String,
        type: String = "REGISTRATION"
    ): Result<VerificationCodeEntity> = withContext(Dispatchers.IO) {
        val cleanTarget = target.trim().lowercase()
        // Invalidate previous active codes for this target
        verificationDao.markAllUsedForTarget(cleanTarget)

        val plainCode = SecurityUtils.generateSixDigitOtp()
        val codeHash = SecurityUtils.hashOtp(plainCode)
        val expiresAt = System.currentTimeMillis() + (5 * 60 * 1000) // 5 minutes validity

        val entity = VerificationCodeEntity(
            targetIdentifier = cleanTarget,
            codeHash = codeHash,
            plainCodePreview = plainCode, // Presented on simulation banner for instant testing
            expiresAt = expiresAt,
            attemptsCount = 0,
            maxAttempts = 5,
            isUsed = false,
            type = type
        )

        val rowId = verificationDao.insertCode(entity)
        Result.success(entity.copy(id = rowId))
    }

    suspend fun verifyCode(
        target: String,
        inputCode: String
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        val cleanTarget = target.trim().lowercase()
        val latest = verificationDao.getLatestActiveCode(cleanTarget)
            ?: return@withContext Result.failure(Exception("No active verification code found. Please request a new one."))

        if (System.currentTimeMillis() > latest.expiresAt) {
            return@withContext Result.failure(Exception("Verification code has expired. Please request a new code."))
        }

        if (latest.attemptsCount >= latest.maxAttempts) {
            return@withContext Result.failure(Exception("Maximum verification attempts exceeded. Please request a new code."))
        }

        val isValid = SecurityUtils.verifyOtp(inputCode.trim(), latest.codeHash)
        if (!isValid) {
            verificationDao.updateCode(latest.copy(attemptsCount = latest.attemptsCount + 1))
            val remaining = latest.maxAttempts - (latest.attemptsCount + 1)
            return@withContext Result.failure(Exception("Incorrect 6-digit code. $remaining attempts remaining."))
        }

        // Mark as used
        verificationDao.updateCode(latest.copy(isUsed = true))
        Result.success(true)
    }

    suspend fun createNexoraAccount(
        authProvider: String, // GOOGLE, EMAIL, PHONE
        identifier: String,
        accountName: String,
        handle: String,
        nexoraPassword: String,
        bio: String = "",
        avatarIndex: Int = 0,
        isPrivateProfile: Boolean = false,
        googleId: String? = null
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val formattedHandle = SecurityUtils.formatHandle(handle)

        // Check if name is taken
        if (isNameTaken(accountName)) {
            return@withContext Result.failure(Exception("This profile name is already unavailable. Please choose another name."))
        }

        // Check if handle is taken
        if (isHandleTaken(formattedHandle)) {
            return@withContext Result.failure(Exception("This username is already taken. Please choose another username."))
        }

        // Check existing identifier if duplicate registration
        val existing = userDao.getUserByIdentifier(identifier.trim().lowercase())
        if (existing != null) {
            return@withContext Result.failure(Exception("An account with this ${authProvider.lowercase()} already exists. Please log in instead."))
        }

        val salt = SecurityUtils.generateSalt()
        val passwordHash = SecurityUtils.hashPassword(nexoraPassword, salt)
        val userId = UUID.randomUUID().toString()

        val newUser = UserEntity(
            id = userId,
            authProvider = authProvider,
            identifier = identifier.trim().lowercase(),
            email = if (authProvider == "EMAIL" || authProvider == "GOOGLE") identifier.trim().lowercase() else null,
            phone = if (authProvider == "PHONE") identifier.trim() else null,
            googleId = googleId,
            name = accountName.trim(),
            handle = formattedHandle,
            passwordHash = passwordHash,
            passwordSalt = salt,
            bio = bio.ifBlank { "Member of the NEXORA community." },
            avatarIndex = avatarIndex,
            avatarColorHex = getAvatarColorForIndex(avatarIndex),
            createdAt = System.currentTimeMillis(),
            isVerified = true,
            isPrivateProfile = isPrivateProfile,
            followersCount = 0,
            followingCount = 0,
            hasChannel = false,
            channelName = null,
            channelHandle = null,
            channelBio = null,
            videoChannelsCount = 0,
            isCreator = false
        )

        userDao.insertUser(newUser)

        // Create active session
        sessionDao.terminateAllSessions()
        val session = ActiveSessionEntity(
            sessionId = UUID.randomUUID().toString(),
            userId = userId,
            sessionToken = UUID.randomUUID().toString(),
            authMethod = authProvider,
            loginTimestamp = System.currentTimeMillis(),
            isActive = true
        )
        sessionDao.insertSession(session)

        Result.success(newUser)
    }

    suspend fun updateProfilePrivacy(userId: String, isPrivate: Boolean): Result<UserEntity> = withContext(Dispatchers.IO) {
        userDao.updatePrivacy(userId, isPrivate)
        val updated = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("User not found"))
        Result.success(updated)
    }

    suspend fun createVideoChannel(
        userId: String,
        channelName: String,
        channelHandle: String,
        channelBio: String?
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        val formattedHandle = SecurityUtils.formatHandle(channelHandle, preserveCase = true)
        val cleanBio = channelBio?.trim()?.ifBlank { "" } ?: ""
        val cleanName = channelName.trim()
        userDao.updateChannel(
            userId = userId,
            hasChannel = true,
            channelName = cleanName,
            channelHandle = formattedHandle,
            channelBio = cleanBio,
            channelsCount = 1
        )

        // Keep channelDao in sync with the exact user-entered channel data
        val channelDao = database.channelDao()
        val existing = channelDao.getChannelByUserId(userId)
        val channel = existing?.copy(
            name = cleanName,
            handle = formattedHandle,
            description = cleanBio,
            updatedAt = System.currentTimeMillis()
        ) ?: ChannelEntity(
            channelId = UUID.randomUUID().toString(),
            userId = userId,
            name = cleanName,
            handle = formattedHandle,
            description = cleanBio,
            avatarColorHex = "#06B6D4",
            category = "Gaming",
            language = "English",
            countryRegion = "United States"
        )
        channelDao.insertChannel(channel)

        val updated = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("User not found"))
        Result.success(updated)
    }

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        bio: String,
        avatarIndex: Int
    ): Result<UserEntity> = withContext(Dispatchers.IO) {
        userDao.updateProfile(userId, name.trim(), bio.trim(), avatarIndex)
        val updated = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("User not found"))
        Result.success(updated)
    }

    suspend fun findUserForLogin(identifierOrHandle: String): UserEntity? = withContext(Dispatchers.IO) {
        val clean = identifierOrHandle.trim().lowercase()
        userDao.getUserByIdentifier(clean)
            ?: userDao.getUserByHandle(clean)
            ?: userDao.getUserByEmail(clean)
            ?: userDao.getUserByPhone(clean)
    }

    suspend fun verifyPasswordForUser(user: UserEntity, passwordInput: String): Boolean = withContext(Dispatchers.IO) {
        SecurityUtils.verifyPassword(passwordInput, user.passwordSalt, user.passwordHash)
    }

    suspend fun completeLoginSession(userId: String, authMethod: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserById(userId)
            ?: return@withContext Result.failure(Exception("User not found"))

        sessionDao.terminateAllSessions()
        val session = ActiveSessionEntity(
            sessionId = UUID.randomUUID().toString(),
            userId = userId,
            sessionToken = UUID.randomUUID().toString(),
            authMethod = authMethod,
            loginTimestamp = System.currentTimeMillis(),
            isActive = true
        )
        sessionDao.insertSession(session)
        Result.success(user)
    }

    suspend fun logout(): Unit = withContext(Dispatchers.IO) {
        sessionDao.terminateAllSessions()
    }

    suspend fun getUserById(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(userId)
    }

    private fun getAvatarColorForIndex(index: Int): String {
        val colors = listOf("#6366F1", "#06B6D4", "#A855F7", "#EC4899", "#10B981", "#F59E0B", "#3B82F6", "#8B5CF6")
        return colors.getOrElse(index % colors.size) { "#6366F1" }
    }
}
