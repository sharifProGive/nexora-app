package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("NEXORA", appName)
  }

  @Test
  fun `test secure password hashing and verification`() {
    val salt = SecurityUtils.generateSalt()
    val password = "NexoraSecurePass2026!"
    val hash = SecurityUtils.hashPassword(password, salt)

    assertTrue(SecurityUtils.verifyPassword(password, salt, hash))
    assertFalse(SecurityUtils.verifyPassword("WrongPassword!", salt, hash))
  }

  @Test
  fun `test handle formatting and validation`() {
    assertEquals("@sharif", SecurityUtils.formatHandle("sharif"))
    assertEquals("@sharif", SecurityUtils.formatHandle("@Sharif"))
    assertTrue(SecurityUtils.isValidHandle("@sharif"))
    assertTrue(SecurityUtils.isValidHandle("@skyline_gamer"))
    assertFalse(SecurityUtils.isValidHandle("@sh")) // too short
    assertFalse(SecurityUtils.isValidHandle("@sharif with spaces"))
  }
}
