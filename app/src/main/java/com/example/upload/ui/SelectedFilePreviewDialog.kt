package com.example.upload.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraErrorRed
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSuccessGreen
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.upload.model.FileValidationResult
import com.example.upload.model.MediaItem
import com.example.upload.model.UploadTargetType

@Composable
fun SelectedFilePreviewDialog(
    mediaItem: MediaItem,
    validationResult: FileValidationResult,
    targetType: UploadTargetType,
    onDismiss: () -> Unit,
    onChangeFile: () -> Unit,
    onCropRequested: ((MediaItem) -> Unit)? = null,
    onProceed: (MediaItem) -> Unit
) {
    val isValid = validationResult is FileValidationResult.Valid
    val isImage = !mediaItem.isVideo
    val canCrop = isImage && onCropRequested != null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .testTag("dialog_file_preview")
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(20.dp)),
            color = NexoraSurfaceDark,
            contentColor = NexoraTextPrimary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Media Preview",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = targetType.title,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraCyanAccent
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_preview")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Media Visual Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(if (targetType == UploadTargetType.CHANNEL_BANNER) 16f / 5f else 16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(NexoraDarkBackground)
                        .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (isImage) {
                        AsyncImage(
                            model = mediaItem.uri,
                            contentDescription = mediaItem.fileName,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Video card presentation with stylized thumbnail preview
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0E7490))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(NexoraCyanAccent.copy(alpha = 0.2f))
                                        .border(1.5.dp, NexoraCyanAccent, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play Video",
                                        tint = NexoraCyanAccent,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                if (mediaItem.durationMs > 0) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color.Black.copy(alpha = 0.7f)
                                    ) {
                                        Text(
                                            text = mediaItem.formattedDuration,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Format Badge Overlay
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = mediaItem.mimeType.substringAfter('/').uppercase(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NexoraCyanAccent,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Validation Status Card
                if (isValid) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraSuccessGreen.copy(alpha = 0.12f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSuccessGreen.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NexoraSuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Ready to Upload",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NexoraSuccessGreen
                                )
                                Text(
                                    text = "File passed integrity, format, and size requirements.",
                                    fontSize = 11.sp,
                                    color = NexoraTextSecondary
                                )
                            }
                        }
                    }
                } else {
                    val invalidReason = (validationResult as? FileValidationResult.Invalid)?.reason
                        ?: "File is not eligible for this upload target."
                    Card(
                        colors = CardDefaults.cardColors(containerColor = NexoraErrorRed.copy(alpha = 0.14f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraErrorRed.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = NexoraErrorRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "File Validation Failed",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NexoraErrorRed
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = invalidReason,
                                    fontSize = 11.sp,
                                    color = NexoraTextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // File Details Grid
                Text(
                    text = "FILE DETAILS",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NexoraCyanAccent
                )
                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        FileDetailRow(
                            icon = Icons.Default.Folder,
                            label = "File Name",
                            value = mediaItem.fileName
                        )
                        HorizontalDivider(color = NexoraSurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
                        FileDetailRow(
                            icon = Icons.Default.Image,
                            label = "File Size",
                            value = mediaItem.formattedSize
                        )
                        HorizontalDivider(color = NexoraSurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
                        FileDetailRow(
                            icon = if (mediaItem.isVideo) Icons.Default.Videocam else Icons.Default.Image,
                            label = "Format / MIME",
                            value = mediaItem.mimeType
                        )
                        if (mediaItem.durationMs > 0) {
                            HorizontalDivider(color = NexoraSurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
                            FileDetailRow(
                                icon = Icons.Default.Timer,
                                label = "Duration",
                                value = mediaItem.formattedDuration
                            )
                        }
                        if (mediaItem.formattedResolution.isNotBlank()) {
                            HorizontalDivider(color = NexoraSurfaceBorder.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 6.dp))
                            FileDetailRow(
                                icon = Icons.Default.Crop,
                                label = "Resolution",
                                value = mediaItem.formattedResolution
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onChangeFile,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_change_file"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = NexoraTextPrimary
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change", fontSize = 12.sp)
                    }

                    if (canCrop) {
                        Button(
                            onClick = { onCropRequested?.invoke(mediaItem) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_crop_adjust"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexoraSurfaceElevated,
                                contentColor = NexoraCyanAccent
                            )
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Crop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { onProceed(mediaItem) },
                        enabled = isValid,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("btn_proceed_media"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexoraCyanAccent,
                            contentColor = Color.Black,
                            disabledContainerColor = NexoraSurfaceElevated,
                            disabledContentColor = NexoraTextMuted
                        )
                    ) {
                        Text(
                            text = if (targetType.requiresCrop && canCrop) "Use As Is" else "Continue",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_cancel_preview")
                ) {
                    Text("Discard", color = NexoraTextMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun FileDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NexoraTextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = NexoraTextSecondary
            )
        }
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = NexoraTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}
