package com.example.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.regex.Pattern
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object SecurityUtils {

    private val random = SecureRandom()
    private const val ITERATIONS = 10000
    private const val KEY_LENGTH = 256

    fun generateSalt(): String {
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun hashPassword(password: String, saltBase64: String): String {
        val salt = Base64.decode(saltBase64, Base64.NO_WRAP)
        val spec = PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verifyPassword(password: String, saltBase64: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, saltBase64)
        return computedHash == expectedHash
    }

    fun generateSixDigitOtp(): String {
        val num = random.nextInt(900000) + 100000
        return num.toString()
    }

    fun hashOtp(otp: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(otp.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verifyOtp(inputOtp: String, storedHash: String): Boolean {
        val computed = hashOtp(inputOtp)
        return computed == storedHash
    }

    // Validations
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}$"
        return Pattern.compile(emailRegex).matcher(email.trim()).matches()
    }

    fun isValidPhone(phone: String): Boolean {
        val digits = phone.filter { it.isDigit() }
        return digits.length in 8..15
    }

    fun formatHandle(input: String, preserveCase: Boolean = false): String {
        var clean = input.trim()
        if (!clean.startsWith("@")) {
            clean = "@$clean"
        }
        return if (preserveCase) clean else clean.lowercase()
    }

    fun isValidHandle(handle: String): Boolean {
        var clean = handle.trim()
        if (!clean.startsWith("@")) clean = "@$clean"
        // Must start with @, followed by 3-30 characters: letters, numbers, underscore
        val regex = "^@[A-Za-z0-9_]{3,30}$"
        return Pattern.compile(regex).matcher(clean).matches()
    }

    fun validatePasswordStrength(password: String): PasswordValidationResult {
        if (password.length < 4) {
            return PasswordValidationResult(
                isValid = false,
                errorMessage = "Password must be at least 4 characters."
            )
        }
        return PasswordValidationResult(isValid = true, errorMessage = null)
    }

    fun generateAlternativeHandles(baseHandle: String, isTakenFn: suspend (String) -> Boolean): List<String> {
        val raw = baseHandle.removePrefix("@").lowercase().filter { it.isLetterOrDigit() || it == '_' }
        val candidateSuffixes = listOf("_nex", "99", "_official", "hq", "_pro", "_vip")
        val candidatePrefixes = listOf("iam", "the_")

        val suggestions = mutableListOf<String>()
        for (prefix in candidatePrefixes) {
            suggestions.add("@$prefix$raw")
        }
        for (suffix in candidateSuffixes) {
            suggestions.add("@$raw$suffix")
        }
        return suggestions.take(4)
    }
}

data class PasswordValidationResult(
    val isValid: Boolean,
    val errorMessage: String?
)
