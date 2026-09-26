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
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.concurrent.TimeUnit

class InAppApkUpdateProvider(
    private val context: Context,
    private val repository: UpdateRepository,
    private val verifier: PackageVerifier = PackageVerifier(context),
    private val installer: AndroidPackageInstaller = AndroidPackageInstaller(context),
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
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
        delay(300)

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

        // 2. Network Failure Simulation check (if testing admin scenarios)
        if (repository.getActiveScenario() == AdminSimulationScenario.SIMULATE_NETWORK_FAILURE) {
            delay(600)
            emit(
                DownloadProgressState.Failed(
                    errorMessage = "Connection timed out while reaching update server. Please check connection and retry.",
                    canRetry = true
                )
            )
            return@flow
        }

        val destinationDir = repository.getUpdatesDirectory()
        val apkFile = File(destinationDir, "nexora-update-v${config.latestVersion}.apk")

        // 3. Download via OkHttp if remote HTTP(S) URL is provided
        val isHttpUrl = config.updateDownloadUrl.startsWith("http://") || config.updateDownloadUrl.startsWith("https://")
        val isSimulatedScenario = repository.getActiveScenario() != AdminSimulationScenario.UP_TO_DATE &&
                config.updateDownloadUrl.contains("cdn.nexora.app")

        var downloadSucceeded = false

        if (isHttpUrl && !isSimulatedScenario) {
            try {
                val request = Request.Builder()
                    .url(config.updateDownloadUrl)
                    .header("User-Agent", "nexora-app-updater")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException("HTTP error ${response.code}: ${response.message}")
                    }
                    val body = response.body ?: throw IOException("Empty response payload from server")
                    val contentLength = body.contentLength().takeIf { it > 0 } ?: totalBytes

                    body.byteStream().use { input ->
                        FileOutputStream(apkFile).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead = 0L
                            var read: Int
                            var lastEmitTime = System.currentTimeMillis()
                            var lastBytes = 0L
                            var speedStr = "4.2 MB/s"

                            while (input.read(buffer).also { read = it } != -1) {
                                if (isDownloadCancelled) {
                                    emit(DownloadProgressState.Cancelled("Download cancelled by user."))
                                    return@flow
                                }
                                output.write(buffer, 0, read)
                                bytesRead += read

                                val now = System.currentTimeMillis()
                                if (now - lastEmitTime >= 250 || bytesRead == contentLength) {
                                    val timeDeltaSec = (now - lastEmitTime) / 1000.0
                                    if (timeDeltaSec > 0.1) {
                                        val bytesDelta = bytesRead - lastBytes
                                        val speedBytesSec = (bytesDelta / timeDeltaSec).toLong()
                                        speedStr = if (speedBytesSec > 1024 * 1024) {
                                            String.format(Locale.US, "%.1f MB/s", speedBytesSec / (1024.0 * 1024.0))
                                        } else {
                                            "${speedBytesSec / 1024} KB/s"
                                        }
                                        lastEmitTime = now
                                        lastBytes = bytesRead
                                    }
                                    val percent = if (contentLength > 0) ((bytesRead * 100) / contentLength).toInt().coerceIn(0, 99) else 50
                                    emit(
                                        DownloadProgressState.Downloading(
                                            progressPercent = percent,
                                            downloadedBytes = bytesRead,
                                            totalBytes = contentLength,
                                            downloadSpeed = speedStr,
                                            isSlowNetwork = false
                                        )
                                    )
                                }
                            }
                        }
                    }
                    downloadSucceeded = true
                }
            } catch (e: Exception) {
                // If real HTTP call fails (e.g. placeholder release URL or rate limit), log and check if we should fall back to staged test buffer
                if (repository.getActiveScenario() == AdminSimulationScenario.UP_TO_DATE) {
                    emit(
                        DownloadProgressState.Failed(
                            errorMessage = "Download failed: ${e.localizedMessage ?: "Network error"}",
                            canRetry = true
                        )
                    )
                    return@flow
                }
            }
        }

        // 4. Staged fallback for simulation/testing mode if direct HTTP was not used or in simulation mode
        if (!downloadSucceeded) {
            val isSlowConnection = repository.getActiveScenario() == AdminSimulationScenario.SIMULATE_SLOW_CONNECTION
            val delayPerStep = if (isSlowConnection) 400L else 180L
            val downloadSpeed = if (isSlowConnection) "680 KB/s (Slow connection)" else "5.2 MB/s"

            val increments = listOf(10, 25, 40, 55, 70, 85, 95, 100)
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

            try {
                FileOutputStream(apkFile).use { fos ->
                    val header = "NEXORA_PACKAGE_V${config.latestVersion}_BUILD_${config.latestVersionCode}\n"
                    fos.write(header.toByteArray())
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
        }

        // 5. Verification Phase
        emit(DownloadProgressState.Verifying("Validating package identity and cryptographic checksum..."))
        delay(350)

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
