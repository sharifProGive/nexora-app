package com.example.update.service

import android.app.Activity
import android.content.Context
import com.example.update.installer.AndroidPackageInstaller
import com.example.update.model.DownloadProgressState
import com.example.update.model.RemoteVersionConfig
import com.example.update.model.UpdateCheckResult
import com.example.update.repository.AdminSimulationScenario
import com.example.update.repository.UpdateRepository
import com.example.update.security.PackageVerifier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class InAppApkUpdateProvider(
    private val context: Context,
    private val repository: UpdateRepository,
    private val verifier: PackageVerifier = PackageVerifier(context),
    private val installer: AndroidPackageInstaller = AndroidPackageInstaller(context)
) : UpdateService {

    override val providerType: UpdateProviderType = UpdateProviderType.NEXORA_PACKAGE

    @Volatile
    private var isDownloadCancelled = false

    override suspend fun checkUpdate(): UpdateCheckResult {
        return repository.checkUpdate()
    }

    override fun cancelDownload() {
        isDownloadCancelled = true
    }

    override fun downloadUpdate(config: RemoteVersionConfig): Flow<DownloadProgressState> = flow {
        isDownloadCancelled = false
        emit(DownloadProgressState.Preparing)
        delay(400)

        // 1. Storage check
        val totalBytes = (config.packageSizeMb * 1024 * 1024).toLong()
        if (!verifier.hasSufficientStorage(totalBytes)) {
            emit(
                DownloadProgressState.Failed(
                    errorMessage = "Insufficient storage space. At least ${(config.packageSizeMb * 2).toInt()} MB free space required to stage update.",
                    canRetry = true,
                    isStorageIssue = true
                )
            )
            return@flow
        }

        // 2. Network Failure Simulation check
        if (repository.getActiveScenario() == AdminSimulationScenario.SIMULATE_NETWORK_FAILURE) {
            delay(800)
            emit(
                DownloadProgressState.Failed(
                    errorMessage = "Connection timed out while reaching CDN update server (Simulated network failure). Please check connection and retry.",
                    canRetry = true
                )
            )
            return@flow
        }

        val isSlowConnection = repository.getActiveScenario() == AdminSimulationScenario.SIMULATE_SLOW_CONNECTION
        val delayPerStep = if (isSlowConnection) 600L else 250L
        val downloadSpeed = if (isSlowConnection) "680 KB/s (Slow connection)" else "4.8 MB/s"

        // 3. Staged Download Increments
        val increments = listOf(10, 25, 35, 50, 65, 80, 92, 100)
        for (percent in increments) {
            if (isDownloadCancelled) {
                emit(DownloadProgressState.Cancelled("Download cancelled by user."))
                return@flow
            }

            val downloadedBytes = (totalBytes * (percent / 100.0)).toLong()
            emit(
                DownloadProgressState.Downloading(
                    progressPercent = percent,
                    downloadedBytes = downloadedBytes,
                    totalBytes = totalBytes,
                    downloadSpeed = downloadSpeed,
                    isSlowNetwork = isSlowConnection
                )
            )
            delay(delayPerStep)
        }

        if (isDownloadCancelled) {
            emit(DownloadProgressState.Cancelled("Download cancelled by user."))
            return@flow
        }

        // 4. Staging APK file to internal updates cache
        emit(DownloadProgressState.Verifying("Validating package identity and cryptographic checksum..."))
        delay(500)

        val destinationDir = repository.getUpdatesDirectory()
        val apkFile = File(destinationDir, "nexora-update-v${config.latestVersion}.apk")

        try {
            // Write payload to file
            FileOutputStream(apkFile).use { fos ->
                val header = "NEXORA_PACKAGE_V${config.latestVersion}_BUILD_${config.latestVersionCode}\n"
                fos.write(header.toByteArray())
                // Write dummy buffer matching size
                val buffer = ByteArray(4096)
                var written = header.length
                while (written < totalBytes.coerceAtMost(1024 * 1024 * 2L).toInt()) {
                    fos.write(buffer)
                    written += buffer.size
                }
            }
        } catch (e: IOException) {
            emit(
                DownloadProgressState.Failed(
                    errorMessage = "Failed to write update file to storage: ${e.localizedMessage}",
                    canRetry = true
                )
            )
            return@flow
        }

        // 5. Security & Identity Verification
        val validation = verifier.verifyPackage(
            apkFile = apkFile,
            config = config,
            installedVersionCode = repository.getInstalledVersionCode()
        )

        if (!validation.isValid) {
            emit(
                DownloadProgressState.Failed(
                    errorMessage = validation.errorMessage ?: "Security verification failed. Package rejected.",
                    canRetry = true
                )
            )
            return@flow
        }

        // 6. Ready to install!
        emit(
            DownloadProgressState.ReadyToInstall(
                apkFile = apkFile,
                config = config,
                archiveVersionName = validation.archiveVersionName,
                archiveVersionCode = validation.archiveVersionCode
            )
        )
    }.flowOn(Dispatchers.IO)

    override fun installUpdate(activity: Activity, apkFile: File): Result<Unit> {
        return installer.launchSystemInstallConfirmation(activity, apkFile)
    }
}
