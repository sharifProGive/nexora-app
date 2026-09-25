package com.example.update.model

/**
 * Remote version configuration defining the latest available NEXORA release,
 * minimum supported version requirements, download endpoint, and release notes.
 */
data class RemoteVersionConfig(
    val latestVersion: String,
    val latestVersionCode: Int,
    val minimumSupportedVersion: String,
    val minimumSupportedVersionCode: Int,
    val updateRequired: Boolean,
    val updateDownloadUrl: String,
    val releaseNotes: List<String>,
    val releaseDate: String,
    val packageChecksum: String? = null,
    val packageSizeMb: Double = 28.5,
    val packageIdentifier: String = "com.aistudio.nexora.app"
) {
    /**
     * Determines whether an update is available given current installed version code.
     */
    fun hasNewerVersion(installedVersionCode: Int): Boolean {
        return latestVersionCode > installedVersionCode
    }

    /**
     * Determines whether the update is strictly mandatory for the installed version.
     * An update is mandatory if:
     * 1) updateRequired is set to true on the remote configuration, OR
     * 2) the installed version is below minimumSupportedVersionCode.
     */
    fun isMandatoryFor(installedVersionCode: Int): Boolean {
        if (!hasNewerVersion(installedVersionCode)) return false
        return updateRequired || (installedVersionCode < minimumSupportedVersionCode)
    }

    companion object {
        /**
         * Default production configuration matching the initial release.
         */
        fun defaultProduction(): RemoteVersionConfig {
            return RemoteVersionConfig(
                latestVersion = "1.0.0",
                latestVersionCode = 1,
                minimumSupportedVersion = "1.0.0",
                minimumSupportedVersionCode = 1,
                updateRequired = false,
                updateDownloadUrl = "https://cdn.nexora.app/releases/nexora-v1.0.0.apk",
                releaseNotes = listOf(
                    "Welcome to NEXORA — Next-generation social, creator, and media platform.",
                    "Dedicated Home video discovery feed and vertical Shorts streaming.",
                    "Unified Personal Profile with separate Video Channel creation architecture.",
                    "Full PBKDF2 cryptography and two-factor authentication."
                ),
                releaseDate = "September 2026",
                packageChecksum = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
                packageSizeMb = 28.5,
                packageIdentifier = "com.aistudio.nexora.app"
            )
        }

        /**
         * Next-version candidate configuration for mandatory / optional update scenarios.
         */
        fun nextRelease(isMandatory: Boolean): RemoteVersionConfig {
            return RemoteVersionConfig(
                latestVersion = "1.1.0",
                latestVersionCode = 2,
                minimumSupportedVersion = if (isMandatory) "1.1.0" else "1.0.0",
                minimumSupportedVersionCode = if (isMandatory) 2 else 1,
                updateRequired = isMandatory,
                updateDownloadUrl = "https://cdn.nexora.app/releases/nexora-v1.1.0-release.apk",
                releaseNotes = listOf(
                    "NEXORA Studio Video Pipeline Engine integration.",
                    "High-definition 60fps vertical shorts streaming optimizations.",
                    "Enhanced creator channel privacy controls & audience analytics.",
                    "Network performance boosts and ultra-low latency playback."
                ),
                releaseDate = "October 2026",
                packageChecksum = "f4c2810a992bc9381e4b495991b7852b855639149afbf4c8996fb92427ae41e4",
                packageSizeMb = 31.2,
                packageIdentifier = "com.aistudio.nexora.app"
            )
        }
    }
}
