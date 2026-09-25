package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.UserEntity
import com.example.data.repository.AuthRepository
import com.example.security.SecurityUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class BottomNavTab {
    HOME,
    SHORTS,
    PLUS,
    SUBSCRIPTIONS,
    YOU
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val activeUser: UserEntity? = null,

    // Active Bottom Navigation Tab
    val currentBottomTab: BottomNavTab = BottomNavTab.HOME,

    // Registration In-Flight State
    val regProvider: String = "EMAIL", // GOOGLE, EMAIL, PHONE
    val regIdentifier: String = "",
    val regAccountName: String = "",
    val regPassword: String = "",
    val regPasswordConfirm: String = "",
    val regHandle: String = "",
    val regBio: String = "",
    val regAvatarIndex: Int = 0,
    val regGoogleId: String? = null,
    val regIsPrivateProfile: Boolean = false,
    val policyAccepted: Boolean = false,

    // Name Availability Check
    val isNameChecking: Boolean = false,
    val isNameAvailable: Boolean? = null,
    val nameValidationMessage: String? = null,

    // Handle Availability Check
    val isHandleChecking: Boolean = false,
    val isHandleAvailable: Boolean? = null,
    val handleValidationMessage: String? = null,
    val handleSuggestions: List<String> = emptyList(),

    // OTP / Verification State
    val verificationTarget: String = "",
    val verificationType: String = "REGISTRATION", // "REGISTRATION" or "LOGIN_2FA"
    val plainVerificationCodePreview: String? = null, // Demo preview banner for testing
    val verificationExpiresAt: Long = 0L,
    val resendCountdown: Int = 0,
    val verificationAttemptsRemaining: Int = 5,

    // Login In-Flight State
    val loginProvider: String = "EMAIL",
    val loginIdentifier: String = "",
    val loginPassword: String = "",
    val pendingLoginUserId: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            repository.initSeedDataIfNeeded()
            val user = repository.getActiveUser()
            _uiState.update { it.copy(activeUser = user) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearSuccess() {
        _uiState.update { it.copy(successMessage = null) }
    }

    // Google Sign-In Flow initiation
    fun startGoogleAuth(googleAccountName: String, googleEmail: String, onNavigateToSetup: () -> Unit) {
        _uiState.update {
            it.copy(
                regProvider = "GOOGLE",
                regIdentifier = googleEmail,
                regAccountName = googleAccountName,
                regGoogleId = "goog_" + googleEmail.hashCode()
            )
        }
        onNavigateToSetup()
    }

    // Email Flow initiation
    fun startEmailRegistration(email: String, onSuccess: () -> Unit) {
        val clean = email.trim()
        if (!SecurityUtils.isValidEmail(clean)) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid email address.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            // Check if user already exists
            val existing = repository.findUserForLogin(clean)
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "An account with this email already exists. Please sign in instead."
                    )
                }
                return@launch
            }

            val result = repository.sendVerificationCode(clean, "REGISTRATION")
            result.onSuccess { codeEntity ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        regProvider = "EMAIL",
                        regIdentifier = clean,
                        verificationTarget = clean,
                        verificationType = "REGISTRATION",
                        plainVerificationCodePreview = codeEntity.plainCodePreview,
                        verificationExpiresAt = codeEntity.expiresAt,
                        resendCountdown = 60,
                        verificationAttemptsRemaining = 5
                    )
                }
                startResendTimer()
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Phone Flow initiation
    fun startPhoneRegistration(phone: String, onSuccess: () -> Unit) {
        val clean = phone.trim()
        if (!SecurityUtils.isValidPhone(clean)) {
            _uiState.update { it.copy(errorMessage = "Please enter a valid phone number with country code.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val existing = repository.findUserForLogin(clean)
            if (existing != null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "An account with this phone number already exists. Please sign in instead."
                    )
                }
                return@launch
            }

            val result = repository.sendVerificationCode(clean, "REGISTRATION")
            result.onSuccess { codeEntity ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        regProvider = "PHONE",
                        regIdentifier = clean,
                        verificationTarget = clean,
                        verificationType = "REGISTRATION",
                        plainVerificationCodePreview = codeEntity.plainCodePreview,
                        verificationExpiresAt = codeEntity.expiresAt,
                        resendCountdown = 60,
                        verificationAttemptsRemaining = 5
                    )
                }
                startResendTimer()
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Verify 6-digit OTP
    fun verifyCode(code: String, onSuccess: () -> Unit) {
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _uiState.update { it.copy(errorMessage = "Please enter the complete 6-digit verification code.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val target = _uiState.value.verificationTarget
            val result = repository.verifyCode(target, code)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, plainVerificationCodePreview = null) }
                if (_uiState.value.verificationType == "LOGIN_2FA") {
                    // Complete login
                    val userId = _uiState.value.pendingLoginUserId ?: ""
                    val loginRes = repository.completeLoginSession(userId, _uiState.value.loginProvider)
                    loginRes.onSuccess { loggedInUser ->
                        _uiState.update { state -> state.copy(activeUser = loggedInUser, pendingLoginUserId = null) }
                        onSuccess()
                    }.onFailure { err ->
                        _uiState.update { state -> state.copy(errorMessage = err.message) }
                    }
                } else {
                    // Registration verified, continue to Account Name
                    onSuccess()
                }
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = err.message,
                        verificationAttemptsRemaining = maxOf(0, it.verificationAttemptsRemaining - 1)
                    )
                }
            }
        }
    }

    // Resend verification code
    fun resendVerificationCode() {
        if (_uiState.value.resendCountdown > 0) return
        val target = _uiState.value.verificationTarget
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.sendVerificationCode(target, _uiState.value.verificationType)
            result.onSuccess { codeEntity ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        plainVerificationCodePreview = codeEntity.plainCodePreview,
                        verificationExpiresAt = codeEntity.expiresAt,
                        resendCountdown = 60,
                        verificationAttemptsRemaining = 5,
                        successMessage = "A new 6-digit code has been dispatched."
                    )
                }
                startResendTimer()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    private fun startResendTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (_uiState.value.resendCountdown > 0) {
                delay(1000)
                _uiState.update { it.copy(resendCountdown = it.resendCountdown - 1) }
            }
        }
    }

    // Step: Policy Acceptance
    fun acceptPolicy(onContinue: () -> Unit) {
        _uiState.update { it.copy(policyAccepted = true, errorMessage = null) }
        onContinue()
    }

    // Step: Profile Privacy Choice (Public vs Private Profile)
    fun setRegistrationProfilePrivacy(isPrivate: Boolean, onContinue: () -> Unit) {
        _uiState.update { it.copy(regIsPrivateProfile = isPrivate) }
        onContinue()
    }

    // Check Account Name Uniqueness
    fun checkNameAvailability(nameInput: String) {
        val clean = nameInput.trim()
        if (clean.isBlank()) {
            _uiState.update {
                it.copy(
                    regAccountName = clean,
                    isNameChecking = false,
                    isNameAvailable = null,
                    nameValidationMessage = null
                )
            }
            return
        }
        if (clean.length < 2) {
            _uiState.update {
                it.copy(
                    regAccountName = clean,
                    isNameChecking = false,
                    isNameAvailable = false,
                    nameValidationMessage = "Profile Name must be at least 2 characters."
                )
            }
            return
        }

        _uiState.update {
            it.copy(
                regAccountName = clean,
                isNameChecking = true,
                isNameAvailable = null,
                nameValidationMessage = null
            )
        }

        viewModelScope.launch {
            val taken = repository.isNameTaken(clean)
            if (taken) {
                _uiState.update {
                    it.copy(
                        isNameChecking = false,
                        isNameAvailable = false,
                        nameValidationMessage = "This profile name is already unavailable. Please choose another name."
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isNameChecking = false,
                        isNameAvailable = true,
                        nameValidationMessage = "Profile Name is available!"
                    )
                }
            }
        }
    }

    // Step 9: Account Name Setup
    fun setAccountName(name: String, onSuccess: () -> Unit) {
        val clean = name.trim()
        if (clean.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Account Name cannot be empty.") }
            return
        }
        if (clean.length < 2) {
            _uiState.update { it.copy(errorMessage = "Account Name must be at least 2 characters long.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val taken = repository.isNameTaken(clean)
            if (taken) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "This profile name is already unavailable. Please choose another name."
                    )
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    regAccountName = clean,
                    isNameAvailable = true,
                    errorMessage = null
                )
            }
            // Pre-fill candidate handle
            val candidate = "@" + clean.lowercase().filter { it.isLetterOrDigit() || it == '_' }
            checkHandleAvailability(candidate)
            onSuccess()
        }
    }

    // Step 10: NEXORA Password Setup
    fun setNexoraPassword(password: String, confirm: String, onSuccess: () -> Unit) {
        val validation = SecurityUtils.validatePasswordStrength(password)
        if (!validation.isValid) {
            _uiState.update { it.copy(errorMessage = validation.errorMessage) }
            return
        }
        if (password != confirm) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }
        _uiState.update {
            it.copy(
                regPassword = password,
                regPasswordConfirm = confirm,
                errorMessage = null
            )
        }
        onSuccess()
    }

    // Step 11: Handle validation & availability check
    fun checkHandleAvailability(handleInput: String) {
        val formatted = SecurityUtils.formatHandle(handleInput)
        _uiState.update {
            it.copy(
                regHandle = formatted,
                isHandleChecking = true,
                isHandleAvailable = null,
                handleValidationMessage = null
            )
        }

        viewModelScope.launch {
            if (!SecurityUtils.isValidHandle(formatted)) {
                _uiState.update {
                    it.copy(
                        isHandleChecking = false,
                        isHandleAvailable = false,
                        handleValidationMessage = "Must start with @, 3-20 characters (letters, numbers, underscores)."
                    )
                }
                return@launch
            }

            val taken = repository.isHandleTaken(formatted)
            if (taken) {
                val suggestions = repository.getAvailableHandleSuggestions(formatted)
                _uiState.update {
                    it.copy(
                        isHandleChecking = false,
                        isHandleAvailable = false,
                        handleValidationMessage = "This username is already taken. Please choose another username.",
                        handleSuggestions = suggestions
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isHandleChecking = false,
                        isHandleAvailable = true,
                        handleValidationMessage = "Username is available!",
                        handleSuggestions = emptyList()
                    )
                }
            }
        }
    }

    fun selectSuggestedHandle(handle: String) {
        checkHandleAvailability(handle)
    }

    fun confirmHandle(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isHandleAvailable != true) {
            _uiState.update {
                it.copy(errorMessage = state.handleValidationMessage ?: "Please choose a valid and available username.")
            }
            return
        }
        onSuccess()
    }

    // Step 12: Profile Setup (Avatar & Bio) & Account Creation
    fun finalizeAccountCreation(
        bio: String,
        avatarIndex: Int,
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.createNexoraAccount(
                authProvider = state.regProvider,
                identifier = state.regIdentifier,
                accountName = state.regAccountName,
                handle = state.regHandle,
                nexoraPassword = state.regPassword,
                bio = bio.trim(),
                avatarIndex = avatarIndex,
                isPrivateProfile = state.regIsPrivateProfile,
                googleId = state.regGoogleId
            )

            result.onSuccess { createdUser ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeUser = createdUser,
                        successMessage = "Welcome to NEXORA, ${createdUser.name}!"
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Step 1A: Bottom Navigation Tab Switching
    fun switchBottomTab(tab: BottomNavTab) {
        _uiState.update { it.copy(currentBottomTab = tab) }
    }

    // Step 1A: Profile Privacy Update
    fun toggleUserPrivacy(isPrivate: Boolean) {
        val user = _uiState.value.activeUser ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val res = repository.updateProfilePrivacy(user.id, isPrivate)
            res.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeUser = updated,
                        successMessage = if (isPrivate) "Profile privacy changed to Private Profile." else "Profile privacy changed to Public Profile."
                    )
                }
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Step 1A: Channel Creation (Separate Video Channel)
    fun createUserChannel(
        channelName: String,
        channelHandle: String,
        channelBio: String?,
        onSuccess: () -> Unit
    ) {
        val user = _uiState.value.activeUser ?: return
        if (channelName.trim().isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please enter a channel name.") }
            return
        }
        val cleanHandle = SecurityUtils.formatHandle(channelHandle, preserveCase = true)
        if (!SecurityUtils.isValidHandle(cleanHandle)) {
            _uiState.update { it.copy(errorMessage = "Channel handle must be valid (e.g. @handle).") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val res = repository.createVideoChannel(user.id, channelName, cleanHandle, channelBio)
            res.onSuccess { updatedUser ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeUser = updatedUser,
                        successMessage = "Video Channel '${channelName.trim()}' created successfully!"
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Step 1A: Personal Profile Update (Name, Bio, Avatar)
    fun updateUserProfile(
        name: String,
        bio: String,
        avatarIndex: Int,
        onSuccess: () -> Unit
    ) {
        val user = _uiState.value.activeUser ?: return
        if (name.trim().length < 2) {
            _uiState.update { it.copy(errorMessage = "Profile Name must be at least 2 characters.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val res = repository.updateUserProfile(user.id, name, bio, avatarIndex)
            res.onSuccess { updatedUser ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeUser = updatedUser,
                        successMessage = "Personal Profile updated successfully!"
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Login Flow
    fun startLogin(
        provider: String, // EMAIL, PHONE, GOOGLE
        identifier: String,
        passwordInput: String,
        onRequires2FA: () -> Unit,
        onDirectSuccess: () -> Unit
    ) {
        val clean = identifier.trim()
        if (clean.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your $provider identifier.") }
            return
        }
        if (passwordInput.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your NEXORA Account Password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val user = repository.findUserForLogin(clean)
            if (user == null) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No account found matching '$clean'. Check your input or sign up."
                    )
                }
                return@launch
            }

            val passwordMatches = repository.verifyPasswordForUser(user, passwordInput)
            if (!passwordMatches) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Invalid NEXORA Account Password. Please try again."
                    )
                }
                return@launch
            }

            // Step 9: Send 6-digit verification code to registered email or phone for secure 2FA login
            val target = user.email ?: user.phone ?: user.identifier
            val otpRes = repository.sendVerificationCode(target, "LOGIN_2FA")

            otpRes.onSuccess { codeEntity ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginProvider = provider,
                        loginIdentifier = clean,
                        pendingLoginUserId = user.id,
                        verificationTarget = target,
                        verificationType = "LOGIN_2FA",
                        plainVerificationCodePreview = codeEntity.plainCodePreview,
                        verificationExpiresAt = codeEntity.expiresAt,
                        resendCountdown = 60,
                        verificationAttemptsRemaining = 5
                    )
                }
                startResendTimer()
                onRequires2FA()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
            }
        }
    }

    // Logout
    fun logout(onLoggedOut: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _uiState.update {
                it.copy(
                    activeUser = null,
                    regAccountName = "",
                    regPassword = "",
                    regPasswordConfirm = "",
                    regHandle = "",
                    regBio = "",
                    loginPassword = ""
                )
            }
            onLoggedOut()
        }
    }

    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
