package com.example.upload.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.upload.model.MediaFileMetadata
import com.example.upload.model.UploadTargetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPreviewDialog(
    metadata: MediaFileMetadata,
    targetType: UploadTargetType,
    onDismiss: () -> Unit,
    onContinue: () -> Unit
) {
    val isVideo = metadata.mimeType.startsWith("video/")
    val isImage = metadata.mimeType.startsWith("image/")

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .testTag("dialog_media_preview")
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(22.dp))
            .background(NexoraSurfaceDark)
            .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(22.dp))
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
                        text = "Selected Media",
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
                        contentDescription = "Cancel",
                        tint = NexoraTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Media Preview Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NexoraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(if (targetType == UploadTargetType.SHORT) 9f / 12f else 16f / 10f)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isImage || isVideo) {
                        AsyncImage(
                            model = metadata.uri,
                            contentDescription = "Media Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (isVideo) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Video",
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    if (!isImage && !isVideo) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = metadata.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextPrimary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Relevant File Metadata Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NexoraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "File Details",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraIndigoLight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MetadataRow(label = "File Name", value = metadata.name)
                    MetadataRow(label = "File Type", value = metadata.mimeType)
                    MetadataRow(label = "File Size", value = metadata.formattedSize)

                    if (metadata.formattedDuration != null) {
                        MetadataRow(label = "Duration", value = metadata.formattedDuration)
                    }

                    if (metadata.resolutionFormatted != null) {
                        MetadataRow(label = "Resolution", value = metadata.resolutionFormatted)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NexoraSuccess,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Validated & ready to proceed",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NexoraSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons: Cancel and Continue
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_preview_cancel")
                ) {
                    Text("Cancel", color = NexoraTextSecondary)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onContinue,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NexoraCyanAccent,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("btn_preview_continue")
                ) {
                    Text(
                        text = if (targetType == UploadTargetType.PROFILE_PICTURE ||
                            targetType == UploadTargetType.CHANNEL_PICTURE ||
                            targetType == UploadTargetType.CHANNEL_BANNER
                        ) "Next: Crop & Adjust" else "Continue",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = NexoraTextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = NexoraTextPrimary,
            maxLines = 1
        )
    }
}
