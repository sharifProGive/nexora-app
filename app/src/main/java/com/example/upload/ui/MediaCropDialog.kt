package com.example.upload.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.upload.model.MediaFileMetadata
import com.example.upload.model.UploadTargetType
import com.example.upload.service.MediaFileInspector
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCropDialog(
    metadata: MediaFileMetadata,
    targetType: UploadTargetType,
    onDismiss: () -> Unit,
    onSaveCropped: (savedUri: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var rotationDegrees by remember { mutableIntStateOf(0) }
    var isSaving by remember { androidx.compose.runtime.mutableStateOf(false) }

    val isAvatar = (targetType == UploadTargetType.PROFILE_PICTURE || targetType == UploadTargetType.CHANNEL_PICTURE)
    val isBanner = (targetType == UploadTargetType.CHANNEL_BANNER)

    fun handleSave() {
        isSaving = true
        coroutineScope.launch {
            try {
                // Decode bitmap from source URI
                context.contentResolver.openInputStream(metadata.uri)?.use { stream ->
                    val original = BitmapFactory.decodeStream(stream)
                    if (original != null) {
                        val matrix = Matrix().apply {
                            postRotate(rotationDegrees.toFloat())
                        }
                        val rotated = Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
                        val stagedFile = MediaFileInspector.saveBitmapToInternalCache(
                            context = context,
                            bitmap = rotated,
                            prefix = if (isAvatar) "avatar" else "banner"
                        )
                        isSaving = false
                        onSaveCropped(stagedFile.absolutePath)
                        return@launch
                    }
                }
            } catch (_: Exception) { }

            isSaving = false
            // Fallback to original URI if bitmap transform encountered memory limit
            onSaveCropped(metadata.uri.toString())
        }
    }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .testTag("dialog_media_crop")
            .fillMaxWidth(0.95f)
            .clip(RoundedCornerShape(24.dp))
            .background(NexoraSurfaceDark)
            .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isBanner) "Adjust & Crop Banner" else "Crop & Adjust Picture",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isAvatar) "Drag to move • Pinch to zoom" else "Aspect ratio guide: 16:9 widescreen",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraIndigoLight
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = NexoraTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Crop Viewport Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (isBanner) 16f / 9f else 1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(1.5.dp, NexoraCyanAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.8f, 3.5f)
                            offsetX += pan.x
                            offsetY += pan.y
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Image with pan, zoom, and rotation
                AsyncImage(
                    model = metadata.uri,
                    contentDescription = "Crop Image",
                    contentScale = if (isBanner) ContentScale.Crop else ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            rotationZ = rotationDegrees.toFloat()
                        )
                )

                // Aspect guide overlay
                if (isAvatar) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(0.9f)
                            .clip(CircleShape)
                            .border(2.dp, NexoraCyanAccent, CircleShape)
                    )
                } else if (isBanner) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.5.dp, NexoraCyanAccent.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Crop Controls Row: Zoom, Move, Rotate, Reset
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Zoom Out
                IconButton(
                    onClick = { scale = (scale - 0.25f).coerceAtLeast(0.8f) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NexoraSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomOut,
                        contentDescription = "Zoom Out",
                        tint = NexoraTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Zoom In
                IconButton(
                    onClick = { scale = (scale + 0.25f).coerceAtMost(3.5f) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NexoraSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.ZoomIn,
                        contentDescription = "Zoom In",
                        tint = NexoraTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Rotate 90°
                IconButton(
                    onClick = { rotationDegrees = (rotationDegrees + 90) % 360 },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NexoraSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "Rotate 90°",
                        tint = NexoraTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Reset / Center
                IconButton(
                    onClick = {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                        rotationDegrees = 0
                    },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(NexoraSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.Default.Crop,
                        contentDescription = "Reset Crop",
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons: Cancel and Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_crop_cancel")
                ) {
                    Text("Cancel", color = NexoraTextSecondary)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = { handleSave() },
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexoraCyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_crop_save")
                ) {
                    Text(if (isSaving) "Saving..." else "Save", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
