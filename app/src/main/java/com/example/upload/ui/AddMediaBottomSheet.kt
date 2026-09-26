package com.example.upload.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.upload.model.FileValidationResult
import com.example.upload.model.MediaItem
import com.example.upload.model.UploadTargetType
import com.example.upload.model.ValidationResult
import com.example.upload.service.MediaFileHelper
import com.example.upload.service.MediaValidator
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMediaBottomSheet(
    targetType: UploadTargetType,
    onDismiss: () -> Unit,
    onMediaSelected: (MediaItem, FileValidationResult) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isValidating by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<ValidationResult.Invalid?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    fun processUri(uri: Uri) {
        isValidating = true
        coroutineScope.launch {
            val item = MediaFileHelper.resolveMediaItem(context, uri)
            val validation = MediaValidator.validateMedia(item, targetType)
            isValidating = false
            onDismiss()
            onMediaSelected(item, validation)
        }
    }

    // 1. Android Photo Picker for Images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            processUri(uri)
        }
    }

    // 2. Android Photo Picker for Videos
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            processUri(uri)
        }
    }

    // 3. Android System Document / File Picker
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            processUri(uri)
        }
    }

    // 4. Official Camera Take Picture
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            processUri(tempCameraUri!!)
        }
    }

    // 5. Official Camera Capture Video
    val captureVideoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo()
    ) { success ->
        if (success && tempCameraUri != null) {
            processUri(tempCameraUri!!)
        }
    }

    // Helper to create temp URI for Camera
    fun createTempMediaUri(isVideo: Boolean): Uri {
        val cacheDir = File(context.cacheDir, "camera_captures").apply { mkdirs() }
        val ext = if (isVideo) "mp4" else "jpg"
        val file = File(cacheDir, "capture_${System.currentTimeMillis()}.$ext")
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val isVideo = (targetType == UploadTargetType.LONG_VIDEO || targetType == UploadTargetType.SHORT || targetType == UploadTargetType.SHORTS)
            val uri = createTempMediaUri(isVideo)
            tempCameraUri = uri
            if (isVideo) {
                captureVideoLauncher.launch(uri)
            } else {
                takePictureLauncher.launch(uri)
            }
        } else {
            Toast.makeText(context, "Camera permission is required to capture photos or videos.", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera(isVideo: Boolean) {
        val hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            val uri = createTempMediaUri(isVideo)
            tempCameraUri = uri
            if (isVideo) {
                captureVideoLauncher.launch(uri)
            } else {
                takePictureLauncher.launch(uri)
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexoraSurfaceDark,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NexoraSurfaceBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .testTag("bottom_sheet_add_media")
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Add Media",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = targetType.title,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyanAccent
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NexoraTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = NexoraSurfaceBorder.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            if (isValidating) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = NexoraCyanAccent,
                        strokeWidth = 2.5.dp,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Validating file format and dimensions...",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextSecondary
                    )
                }
            } else {
                // Feature-specific Options
                when (targetType) {
                    UploadTargetType.PROFILE_PICTURE,
                    UploadTargetType.CHANNEL_PICTURE -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.CameraAlt,
                            title = "Take Photo",
                            subtitle = "Capture a new picture using camera",
                            onClick = { launchCamera(isVideo = false) }
                        )
                        AddMediaOptionRow(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Choose Photo",
                            subtitle = "Select from official Android Photo Picker",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    UploadTargetType.CHANNEL_BANNER -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Choose Photo",
                            subtitle = "Select high-resolution banner image (16:9)",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    UploadTargetType.LONG_VIDEO -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.Videocam,
                            title = "Record Video",
                            subtitle = "Capture landscape video via camera",
                            onClick = { launchCamera(isVideo = true) }
                        )
                        AddMediaOptionRow(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Choose Video",
                            subtitle = "Select video from Android media picker",
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                        )
                        AddMediaOptionRow(
                            icon = Icons.Default.Folder,
                            title = "Choose File",
                            subtitle = "Browse device storage files and documents",
                            onClick = {
                                documentPickerLauncher.launch("video/*")
                            }
                        )
                    }

                    UploadTargetType.SHORT,
                    UploadTargetType.SHORTS -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.Videocam,
                            title = "Record Short",
                            subtitle = "Capture vertical clip (up to 60s)",
                            onClick = { launchCamera(isVideo = true) }
                        )
                        AddMediaOptionRow(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Upload Video",
                            subtitle = "Choose vertical video from device",
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            }
                        )
                    }

                    UploadTargetType.POST_IMAGE -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.CameraAlt,
                            title = "Take Photo",
                            subtitle = "Snap a picture for community post",
                            onClick = { launchCamera(isVideo = false) }
                        )
                        AddMediaOptionRow(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Choose Photos",
                            subtitle = "Select image from photo gallery",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    UploadTargetType.CUSTOM_THUMBNAIL,
                    UploadTargetType.THUMBNAIL -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.CameraAlt,
                            title = "Take Photo",
                            subtitle = "Take custom photo for thumbnail",
                            onClick = { launchCamera(isVideo = false) }
                        )
                        AddMediaOptionRow(
                            icon = Icons.Default.PhotoLibrary,
                            title = "Choose Photo",
                            subtitle = "Pick landscape cover art (16:9)",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    UploadTargetType.GENERIC_FILE -> {
                        AddMediaOptionRow(
                            icon = Icons.Default.Folder,
                            title = "Choose File",
                            subtitle = "Pick document, archive, or media file",
                            onClick = {
                                documentPickerLauncher.launch("*/*")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    // Validation Error Dialog
    if (validationError != null) {
        val error = validationError!!
        AlertDialog(
            onDismissRequest = { validationError = null },
            containerColor = NexoraSurfaceDark,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = NexoraError,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "File Validation Notice",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = error.reason,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraTextSecondary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Action: ${error.suggestedAction}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = NexoraCyanAccent
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { validationError = null }) {
                    Text("OK", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun AddMediaOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(NexoraSurfaceElevated)
                    .border(0.8.dp, NexoraSurfaceBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = NexoraTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextSecondary
                )
            }
        }
    }
}
