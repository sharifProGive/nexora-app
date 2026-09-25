package com.example.update.model

import java.io.File

/**
 * Update urgency levels.
 */
enum class UpdateUrgency {
    UP_TO_DATE,
    OPTIONAL,
    MANDATORY
}

/**
 * Result of checking remote version against installed application.
 */
sealed interface UpdateCheckResult {
    data class UpdateAvailable(
        val config: RemoteVersionConfig,
        val urgency: UpdateUrgency,
        val installedVersionName: String,
        val installedVersionCode: Int
    ) : UpdateCheckResult

    data class UpToDate(
        val installedVersionName: String,
        val installedVersionCode: Int,
        val latestVersionName: String
    ) : UpdateCheckResult

    data class Error(
        val message: String,
        val cachedConfig: RemoteVersionConfig? = null
    ) : UpdateCheckResult
}

/**
 * Real-time download and verification progression state.
 */
sealed interface DownloadProgressState {
    object Idle : DownloadProgressState

    object Preparing : DownloadProgressState

    data class Downloading(
        val progressPercent: Int,
        val downloadedBytes: Long,
        val totalBytes: Long,
        val downloadSpeed: String = "4.2 MB/s",
        val isSlowNetwork: Boolean = false
    ) : DownloadProgressState {
        val downloadedMb: Double get() = downloadedBytes / (1024.0 * 1024.0)
        val totalMb: Double get() = totalBytes / (1024.0 * 1024.0)
    }

    data class Verifying(
        val step: String = "Verifying cryptographic checksum & package signature..."
    ) : DownloadProgressState

    data class ReadyToInstall(
        val apkFile: File,
        val config: RemoteVersionConfig,
        val archiveVersionName: String,
        val archiveVersionCode: Int
    ) : DownloadProgressState

    data class Failed(
        val errorMessage: String,
        val canRetry: Boolean = true,
        val isStorageIssue: Boolean = false
    ) : DownloadProgressState

    data class Cancelled(
        val reason: String = "Download was cancelled by user."
    ) : DownloadProgressState
}
