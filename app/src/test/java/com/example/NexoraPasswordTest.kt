package com.example

import com.example.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NexoraPasswordTest {

    @Test
    fun testUserSpecifiedPasswordsAreAccepted() {
        val validExamples = listOf(
            "1234",
            "abcd",
            "Sharif",
            "12345678",
            "Sharif123",
            "@abc#",
            "abcd@",
            "987654"
        )

        for (pass in validExamples) {
            val result = SecurityUtils.validatePasswordStrength(pass)
            assertTrue("Expected password '$pass' to be valid", result.isValid)
            assertEquals(null, result.errorMessage)
        }
    }

    @Test
    fun testFewerThanFourCharactersRejected() {
        val shortPasswords = listOf("", "1", "12", "123", "a", "ab", "abc", "@#")

        for (pass in shortPasswords) {
            val result = SecurityUtils.validatePasswordStrength(pass)
            assertFalse("Expected password '$pass' to be invalid", result.isValid)
            assertEquals("Password must be at least 4 characters.", result.errorMessage)
        }
    }
}
