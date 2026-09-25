package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.database.NexoraDatabase
import com.example.data.model.UserEntity
import com.example.data.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NexoraProfileChannelTest {

    private lateinit var db: NexoraDatabase
    private lateinit var repository: AuthRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, NexoraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = AuthRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testPersonalProfileAndVideoChannelSeparation() = runBlocking {
        // 1. Create Personal Profile
        val userResult = repository.createNexoraAccount(
            authProvider = "GOOGLE",
            identifier = "sharif@example.com",
            accountName = "Sharif",
            handle = "@sharif",
            nexoraPassword = "SecurePass123!",
            bio = "Personal profile of Sharif",
            avatarIndex = 1,
            isPrivateProfile = false
        )

        assertTrue(userResult.isSuccess)
        val user: UserEntity = userResult.getOrThrow()
        assertNotNull(user)
        assertEquals("Sharif", user.name)
        assertEquals("@sharif", user.handle)
        assertFalse("Channel should not exist initially", user.hasChannel)
        assertFalse("Default profile should not be private", user.isPrivateProfile)

        // 2. Toggle Privacy to Private
        val privacyResult = repository.updateProfilePrivacy(user.id, true)
        assertTrue(privacyResult.isSuccess)
        val updatedPrivacyUser = privacyResult.getOrThrow()
        assertTrue("Profile should now be private", updatedPrivacyUser.isPrivateProfile)

        // 3. Create Video Channel as a separate entity
        val channelResult = repository.createVideoChannel(
            userId = user.id,
            channelName = "Skyline Pro Gamer",
            channelHandle = "@skylineprogamer",
            channelBio = "Pro gaming clips and tutorials."
        )

        assertTrue(channelResult.isSuccess)
        val channelUser = channelResult.getOrThrow()

        // Verify Personal Profile is intact
        assertEquals("Personal profile name must remain intact", "Sharif", channelUser.name)
        assertEquals("Personal handle must remain intact", "@sharif", channelUser.handle)
        assertEquals("Personal bio must remain intact", "Personal profile of Sharif", channelUser.bio)

        // Verify Video Channel entity properties
        assertTrue("Channel flag must be true", channelUser.hasChannel)
        assertEquals("Skyline Pro Gamer", channelUser.channelName)
        assertEquals("@skylineprogamer", channelUser.channelHandle)
        assertEquals("Pro gaming clips and tutorials.", channelUser.channelBio)
    }

    @Test
    fun testNameUniquenessCheck() = runBlocking {
        repository.createNexoraAccount(
            authProvider = "EMAIL",
            identifier = "creator@nexora.io",
            accountName = "CyberNova",
            handle = "@cybernova",
            nexoraPassword = "SecurePass123!",
            bio = "Testing name uniqueness",
            avatarIndex = 0,
            isPrivateProfile = false
        )

        assertTrue("Identical name should be taken", repository.isNameTaken("CyberNova"))
        assertFalse("Unique name should not be taken", repository.isNameTaken("CyberNova2"))
    }
}
