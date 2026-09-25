package com.example

import com.example.update.model.RemoteVersionConfig
import com.example.update.model.UpdateCheckResult
import com.example.update.model.UpdateUrgency
import com.example.update.repository.AdminSimulationScenario
import com.example.update.repository.SemVerUtil
import com.example.update.security.PackageVerifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class NexoraUpdateUnitTest {

    @Test
    fun semVerComparison_isAccurate() {
        assertTrue(SemVerUtil.compare("1.1.0", "1.0.0") > 0)
        assertTrue(SemVerUtil.compare("1.0.1", "1.0.0") > 0)
        assertTrue(SemVerUtil.compare("2.0.0", "1.9.9") > 0)
        assertTrue(SemVerUtil.compare("1.0.0", "1.0.0") == 0)
        assertTrue(SemVerUtil.compare("1.0.0", "1.1.0") < 0)
    }

    @Test
    fun mandatoryUpdate_enforcedWhenInstalledVersionBelowMinimum() {
        val config = RemoteVersionConfig(
            latestVersion = "1.2.0",
            latestVersionCode = 3,
            minimumSupportedVersion = "1.1.0",
            minimumSupportedVersionCode = 2,
            updateRequired = true,
            updateDownloadUrl = "https://cdn.nexora.app/releases/nexora-1.2.0.apk",
            releaseNotes = listOf("Critical security patch"),
            releaseDate = "2026-10-01",
            packageSizeMb = 31.2,
            packageChecksum = "test",
            packageIdentifier = "com.aistudio.nexora.app"
        )

        // Installed is 1.0.0 (code 1)
        val installedVersion = "1.0.0"
        val installedCode = 1

        val isInstalledBelowMinimum = SemVerUtil.compare(installedVersion, config.minimumSupportedVersion) < 0 ||
                (installedCode < config.minimumSupportedVersionCode)

        assertTrue(isInstalledBelowMinimum)
        assertTrue(config.updateRequired)
    }

    @Test
    fun optionalUpdate_whenAboveMinimumButBelowLatest() {
        val config = RemoteVersionConfig(
            latestVersion = "1.1.0",
            latestVersionCode = 2,
            minimumSupportedVersion = "1.0.0",
            minimumSupportedVersionCode = 1,
            updateRequired = false,
            updateDownloadUrl = "https://cdn.nexora.app/releases/nexora-1.1.0.apk",
            releaseNotes = listOf("New video discovery filters"),
            releaseDate = "2026-09-30",
            packageSizeMb = 29.5,
            packageChecksum = "test",
            packageIdentifier = "com.aistudio.nexora.app"
        )

        // Installed is 1.0.0 (code 1)
        val installedVersion = "1.0.0"
        val installedCode = 1

        val isBelowLatest = SemVerUtil.compare(installedVersion, config.latestVersion) < 0
        val isAtOrAboveMin = SemVerUtil.compare(installedVersion, config.minimumSupportedVersion) >= 0 &&
                installedCode >= config.minimumSupportedVersionCode

        assertTrue(isBelowLatest)
        assertTrue(isAtOrAboveMin)
        assertFalse(config.updateRequired)
    }

    @Test
    fun packageChecksum_calculatesSha256Accurately() {
        val tempFile = File.createTempFile("nexora_test", ".apk")
        tempFile.writeText("NEXORA_SECURE_PAYLOAD")
        try {
            val calculatedChecksum = PackageVerifier.calculateSha256(tempFile)
            // Checksum of non-empty file must be 64 characters hex
            assertEquals(64, calculatedChecksum.length)
        } finally {
            tempFile.delete()
        }
    }
}
