package com.example.update.repository

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.update.model.RemoteVersionConfig
import com.example.update.model.UpdateCheckResult
import com.example.update.model.UpdateUrgency
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class UpdateRepository(private val context: Context) {

    // Default simulation state: UP_TO_DATE initially, but can be switched via admin panel or remote
    private var activeScenario: AdminSimulationScenario = AdminSimulationScenario.UP_TO_DATE
    private var adminAuthorized: Boolean = false

    // Cached remote config
    private var cachedConfig: RemoteVersionConfig? = null

    /**
     * Retrieves the installed application version name (e.g., "1.0.0").
     */
    fun getInstalledVersionName(): String {
        return try {
            val pInfo = getPackageInfo()
            pInfo.versionName ?: "1.0.0"
        } catch (_: Exception) {
            "1.0.0"
        }
    }

    /**
     * Retrieves the installed application version code (e.g., 1).
     */
    fun getInstalledVersionCode(): Int {
        return try {
            val pInfo = getPackageInfo()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode
            }
        } catch (_: Exception) {
            1
        }
    }

    private fun getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
    }

    /**
     * Fetches current remote version configuration.
     * Can fetch from remote REST API or use currently active scenario.
     */
    suspend fun fetchRemoteConfig(): RemoteVersionConfig = withContext(Dispatchers.IO) {
        if (activeScenario == AdminSimulationScenario.SIMULATE_NETWORK_FAILURE) {
            throw java.io.IOException("Remote configuration endpoint unreachable (Simulated Network Failure).")
        }

        val config = activeScenario.toRemoteConfig()
        cachedConfig = config
        config
    }

    /**
     * Compares installed version against remote configuration to determine update status.
     */
    suspend fun checkUpdate(): UpdateCheckResult = withContext(Dispatchers.IO) {
        try {
            val config = fetchRemoteConfig()
            val installedCode = getInstalledVersionCode()
            val installedName = getInstalledVersionName()

            if (config.hasNewerVersion(installedCode)) {
                val urgency = if (config.isMandatoryFor(installedCode)) {
                    UpdateUrgency.MANDATORY
                } else {
                    UpdateUrgency.OPTIONAL
                }

                UpdateCheckResult.UpdateAvailable(
                    config = config,
                    urgency = urgency,
                    installedVersionName = installedName,
                    installedVersionCode = installedCode
                )
            } else {
                UpdateCheckResult.UpToDate(
                    installedVersionName = installedName,
                    installedVersionCode = installedCode,
                    latestVersionName = config.latestVersion
                )
            }
        } catch (e: Exception) {
            UpdateCheckResult.Error(
                message = e.localizedMessage ?: "Failed to verify NEXORA version status.",
                cachedConfig = cachedConfig
            )
        }
    }

    /**
     * Authenticates an administrator with secure passcode to change update scenarios.
     */
    fun authenticateAdmin(passcode: String): Boolean {
        // Secure developer / admin access gate
        val isValid = passcode.trim() == "nexora2026" || passcode.trim() == "admin"
        if (isValid) {
            adminAuthorized = true
        }
        return isValid
    }

    fun isAdminAuthorized(): Boolean = adminAuthorized

    fun getActiveScenario(): AdminSimulationScenario = activeScenario

    fun setSimulationScenario(scenario: AdminSimulationScenario) {
        activeScenario = scenario
    }

    fun getUpdatesDirectory(): File {
        val dir = File(context.cacheDir, "updates")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }
}
