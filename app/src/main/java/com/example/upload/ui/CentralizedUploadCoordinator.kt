package com.example.upload.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.upload.model.FileValidationResult
import com.example.upload.model.MediaItem
import com.example.upload.model.UploadTargetType

@Stable
class CentralizedUploadController {
    var showAddMediaSheet by mutableStateOf(false)
        private set

    var showPreviewDialog by mutableStateOf(false)
        private set

    var showCropDialog by mutableStateOf(false)
        private set

    var showUploadManager by mutableStateOf(false)

    var currentTargetType by mutableStateOf(UploadTargetType.PROFILE_PICTURE)
        private set

    var pendingMediaItem by mutableStateOf<MediaItem?>(null)
        private set

    var pendingValidation by mutableStateOf<FileValidationResult?>(null)
        private set

    private var onMediaConfirmedCallback: ((MediaItem) -> Unit)? = null

    fun openUploadManager() {
        showUploadManager = true
    }

    fun dismissUploadManager() {
        showUploadManager = false
    }

    fun requestMedia(
        targetType: UploadTargetType,
        onConfirmed: (MediaItem) -> Unit
    ) {
        currentTargetType = targetType
        onMediaConfirmedCallback = onConfirmed
        pendingMediaItem = null
        pendingValidation = null
        showPreviewDialog = false
        showCropDialog = false
        showAddMediaSheet = true
    }

    fun onMediaSelected(item: MediaItem, validation: FileValidationResult) {
        pendingMediaItem = item
        pendingValidation = validation
        showAddMediaSheet = false
        showPreviewDialog = true
    }

    fun dismissAddMedia() {
        showAddMediaSheet = false
    }

    fun dismissPreview() {
        showPreviewDialog = false
        pendingMediaItem = null
        pendingValidation = null
    }

    fun requestCropFromPreview(item: MediaItem) {
        pendingMediaItem = item
        showPreviewDialog = false
        showCropDialog = true
    }

    fun onCropCompleted(croppedItem: MediaItem) {
        showCropDialog = false
        pendingMediaItem = croppedItem
        // Proceed with cropped item
        onMediaConfirmedCallback?.invoke(croppedItem)
    }

    fun dismissCrop() {
        showCropDialog = false
        // Return to preview
        showPreviewDialog = true
    }

    fun confirmMedia(item: MediaItem) {
        showPreviewDialog = false
        onMediaConfirmedCallback?.invoke(item)
    }
}

@Composable
fun rememberCentralizedUploadController(): CentralizedUploadController {
    return remember { CentralizedUploadController() }
}

@Composable
fun CentralizedUploadHost(
    controller: CentralizedUploadController
) {
    if (controller.showAddMediaSheet) {
        AddMediaBottomSheet(
            targetType = controller.currentTargetType,
            onDismiss = { controller.dismissAddMedia() },
            onMediaSelected = { item, validation ->
                controller.onMediaSelected(item, validation)
            }
        )
    }

    if (controller.showPreviewDialog && controller.pendingMediaItem != null && controller.pendingValidation != null) {
        SelectedFilePreviewDialog(
            mediaItem = controller.pendingMediaItem!!,
            validationResult = controller.pendingValidation!!,
            targetType = controller.currentTargetType,
            onDismiss = { controller.dismissPreview() },
            onChangeFile = {
                controller.dismissPreview()
                controller.requestMedia(controller.currentTargetType) { item ->
                    controller.confirmMedia(item)
                }
            },
            onCropRequested = if (controller.currentTargetType.requiresCrop || !controller.pendingMediaItem!!.isVideo) {
                { item -> controller.requestCropFromPreview(item) }
            } else null,
            onProceed = { item ->
                controller.confirmMedia(item)
            }
        )
    }

    if (controller.showCropDialog && controller.pendingMediaItem != null) {
        ImageCropDialog(
            mediaItem = controller.pendingMediaItem!!,
            targetType = controller.currentTargetType,
            onDismiss = { controller.dismissCrop() },
            onCropComplete = { cropped ->
                controller.onCropCompleted(cropped)
            }
        )
    }

    if (controller.showUploadManager) {
        UploadManagerDialog(
            onDismiss = { controller.dismissUploadManager() }
        )
    }
}
