package com.example.update.service

import android.app.Activity
import com.example.update.model.DownloadProgressState
import com.example.update.model.RemoteVersionConfig
import com.example.update.model.UpdateCheckResult
import kotlinx.coroutines.flow.Flow
import java.io.File

enum class UpdateProviderType {
    NEXORA_PACKAGE,
    GOOGLE_PLAY
}

/**
 * Universal abstraction for NEXORA application update mechanisms.
 * Supports both direct in-app package delivery and future Google Play In-App Updates.
 */
interface UpdateService {
    val providerType: UpdateProviderType

    /**
     * Checks if a new release is available against the remote configuration.
     */
    suspend fun checkUpdate(): UpdateCheckResult

    /**
     * Initiates the download or staged acquisition of the update package.
     */
    fun downloadUpdate(config: RemoteVersionConfig): Flow<DownloadProgressState>

    /**
     * Cancels any active download operation (if allowed for non-mandatory updates).
     */
    fun cancelDownload()

    /**
     * Launches the platform-native installation confirmation.
     */
    fun installUpdate(activity: Activity, apkFile: File): Result<Unit>
}
