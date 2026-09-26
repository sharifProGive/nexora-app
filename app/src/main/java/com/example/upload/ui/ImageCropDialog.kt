package com.example.upload.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.upload.model.MediaItem
import com.example.upload.model.UploadTargetType
import com.example.upload.service.MediaFileHelper
import kotlinx.coroutines.launch

@Composable
fun ImageCropDialog(
    mediaItem: MediaItem,
    targetType: UploadTargetType,
    onDismiss: () -> Unit,
    onCropComplete: (MediaItem) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var zoomScale by remember { mutableFloatStateOf(1f) }
    var rotationDegrees by remember { mutableFloatStateOf(0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var isProcessing by remember { mutableStateOf(false) }

    val isCircularTarget = targetType == UploadTargetType.PROFILE_PICTURE || targetType == UploadTargetType.CHANNEL_PICTURE
    val cropAspectRatio = when (targetType) {
        UploadTargetType.CHANNEL_BANNER -> 16f / 5f
        UploadTargetType.THUMBNAIL, UploadTargetType.CUSTOM_THUMBNAIL -> 16f / 9f
        UploadTargetType.PROFILE_PICTURE, UploadTargetType.CHANNEL_PICTURE -> 1f
        else -> 1f
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .testTag("dialog_image_crop")
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(20.dp)),
            color = NexoraSurfaceDark,
            contentColor = NexoraTextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Crop & Adjust",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = if (isCircularTarget) "1:1 Avatar Frame" else if (targetType == UploadTargetType.CHANNEL_BANNER) "Banner Aspect Ratio" else "16:9 Landscape Frame",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraCyanAccent
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_crop")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Interactive Crop Viewport Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (targetType == UploadTargetType.CHANNEL_BANNER) 16f / 5f else 1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexoraDarkBackground)
                        .clipToBounds()
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                zoomScale = (zoomScale * zoom).coerceIn(1f, 3.5f)
                                panOffsetX += pan.x
                                panOffsetY += pan.y
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Transformed image
                    AsyncImage(
                        model = mediaItem.uri,
                        contentDescription = "Crop source image",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = zoomScale
                                scaleY = zoomScale
                                translationX = panOffsetX
                                translationY = panOffsetY
                                rotationZ = rotationDegrees
                            }
                    )

                    // Crop stencil overlay
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasW = size.width
                        val canvasH = size.height

                        if (isCircularTarget) {
                            val radius = (canvasW.coerceAtMost(canvasH) / 2f) * 0.9f
                            val center = Offset(canvasW / 2f, canvasH / 2f)

                            // Outline guide circle
                            drawCircle(
                                color = Color(0xFF06B6D4),
                                radius = radius,
                                center = center,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        } else {
                            // Rectangular guideline
                            drawRect(
                                color = Color(0xFF06B6D4),
                                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                                size = Size(canvasW - 8.dp.toPx(), canvasH - 8.dp.toPx()),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Adjustment Controls: Zoom & Rotate
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceElevated,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ZoomIn, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Zoom: ${(zoomScale * 100).toInt()}%", fontSize = 11.sp, color = NexoraTextSecondary)
                            }

                            Row {
                                IconButton(
                                    onClick = { rotationDegrees = (rotationDegrees + 90f) % 360f },
                                    modifier = Modifier.size(32.dp).testTag("btn_rotate_crop")
                                ) {
                                    Icon(Icons.Default.RotateRight, contentDescription = "Rotate", tint = NexoraCyanAccent, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                IconButton(
                                    onClick = {
                                        zoomScale = 1f
                                        rotationDegrees = 0f
                                        panOffsetX = 0f
                                        panOffsetY = 0f
                                    },
                                    modifier = Modifier.size(32.dp).testTag("btn_reset_crop")
                                ) {
                                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = NexoraTextMuted, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        Slider(
                            value = zoomScale,
                            onValueChange = { zoomScale = it },
                            valueRange = 1f..3f,
                            colors = SliderDefaults.colors(
                                thumbColor = NexoraCyanAccent,
                                activeTrackColor = NexoraCyanAccent,
                                inactiveTrackColor = NexoraSurfaceBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_cancel_crop")
                    ) {
                        Text("Cancel", fontSize = 12.sp, color = NexoraTextSecondary)
                    }

                    Button(
                        onClick = {
                            isProcessing = true
                            scope.launch {
                                val cropped = MediaFileHelper.cropAndSaveImage(
                                    context = context,
                                    sourceUri = mediaItem.uri,
                                    rotationDegrees = rotationDegrees,
                                    zoomScale = zoomScale,
                                    panOffsetX = panOffsetX,
                                    panOffsetY = panOffsetY,
                                    aspectRatio = cropAspectRatio
                                )
                                isProcessing = false
                                onCropComplete(cropped)
                            }
                        },
                        enabled = !isProcessing,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("btn_apply_crop"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexoraCyanAccent,
                            contentColor = Color.Black
                        )
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Apply Crop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
