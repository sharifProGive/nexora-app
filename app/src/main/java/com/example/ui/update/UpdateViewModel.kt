package com.example.ui.update

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.update.model.DownloadProgressState
import com.example.update.model.RemoteVersionConfig
import com.example.update.model.UpdateCheckResult
import com.example.update.model.UpdateUrgency
import com.example.update.repository.AdminSimulationScenario
import com.example.update.repository.UpdateRepository
import com.example.update.service.UpdateService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.io.File

class UpdateViewModel(
    private val repository: UpdateRepository,
    private val updateService: UpdateService
) : ViewModel() {

    private val _updateCheckResult = MutableStateFlow<UpdateCheckResult?>(null)
    val updateCheckResult: StateFlow<UpdateCheckResult?> = _updateCheckResult.asStateFlow()

    private val _downloadState = MutableStateFlow<DownloadProgressState>(DownloadProgressState.Idle)
    val downloadState: StateFlow<DownloadProgressState> = _downloadState.asStateFlow()

    private val _isCheckingVersion = MutableStateFlow(false)
    val isCheckingVersion: StateFlow<Boolean> = _isCheckingVersion.asStateFlow()

    private val _activeConfig = MutableStateFlow<RemoteVersionConfig?>(null)
    val activeConfig: StateFlow<RemoteVersionConfig?> = _activeConfig.asStateFlow()

    private val _isMandatory = MutableStateFlow(false)
    val isMandatory: StateFlow<Boolean> = _isMandatory.asStateFlow()

    private val _currentScenario = MutableStateFlow(repository.getActiveScenario())
    val currentScenario: StateFlow<AdminSimulationScenario> = _currentScenario.asStateFlow()

    private val _isAdminAuthorized = MutableStateFlow(repository.isAdminAuthorized())
    val isAdminAuthorized: StateFlow<Boolean> = _isAdminAuthorized.asStateFlow()

    private var downloadedApkFile: File? = null

    /**
     * Checks remote version against installed application.
     */
    fun checkForUpdates(
        onResult: (UpdateCheckResult) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isCheckingVersion.value = true
            try {
                val result = updateService.checkUpdate()
                _updateCheckResult.value = result
                when (result) {
                    is UpdateCheckResult.UpdateAvailable -> {
                        _activeConfig.value = result.config
                        _isMandatory.value = (result.urgency == UpdateUrgency.MANDATORY)
                    }
                    is UpdateCheckResult.UpToDate -> {
                        _activeConfig.value = null
                        _isMandatory.value = false
                    }
                    is UpdateCheckResult.Error -> {
                        // Error handling
                    }
                }
                onResult(result)
            } finally {
                _isCheckingVersion.value = false
            }
        }
    }

    /**
     * Starts downloading the update package with progress reporting.
     */
    fun startDownload(config: RemoteVersionConfig) {
        viewModelScope.launch {
            _activeConfig.value = config
            updateService.downloadUpdate(config)
                .catch { e ->
                    _downloadState.value = DownloadProgressState.Failed(
                        errorMessage = e.localizedMessage ?: "Download failed unexpectedly.",
                        canRetry = true
                    )
                }
                .collect { state ->
                    _downloadState.value = state
                    if (state is DownloadProgressState.ReadyToInstall) {
                        downloadedApkFile = state.apkFile
                    }
                }
        }
    }

    /**
     * Cancels active download (allowed for optional updates).
     */
    fun cancelDownload() {
        updateService.cancelDownload()
        _downloadState.value = DownloadProgressState.Cancelled()
    }

    /**
     * Resets download state back to Idle for retrying.
     */
    fun resetDownloadState() {
        _downloadState.value = DownloadProgressState.Idle
    }

    /**
     * Triggers the official Android system installation confirmation prompt.
     * NEVER silent installation.
     */
    fun installUpdate(activity: Activity): Result<Unit> {
        val apk = downloadedApkFile ?: return Result.failure(IllegalStateException("No verified APK ready."))
        return updateService.installUpdate(activity, apk)
    }

    /**
     * Changes active simulation scenario (Admin / Tester feature).
     */
    fun setAdminScenario(scenario: AdminSimulationScenario) {
        repository.setSimulationScenario(scenario)
        _currentScenario.value = scenario
        resetDownloadState()
        // Re-check with new scenario
        checkForUpdates()
    }

    /**
     * Authenticates an administrator to access version simulation configuration.
     */
    fun authorizeAdmin(passcode: String): Boolean {
        val success = repository.authenticateAdmin(passcode)
        _isAdminAuthorized.value = success
        return success
    }

    fun getInstalledVersionName(): String = repository.getInstalledVersionName()
    fun getInstalledVersionCode(): Int = repository.getInstalledVersionCode()
}

class UpdateViewModelFactory(
    private val repository: UpdateRepository,
    private val updateService: UpdateService
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UpdateViewModel::class.java)) {
            return UpdateViewModel(repository, updateService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
