package com.example.ui.update

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.update.model.DownloadProgressState
import com.example.update.model.UpdateCheckResult
import com.example.update.model.UpdateUrgency
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InAppUpdateDialog(
    updateResult: UpdateCheckResult.UpdateAvailable,
    downloadState: DownloadProgressState,
    onUpdateNow: () -> Unit,
    onCancelDownload: () -> Unit,
    onDismiss: () -> Unit,
    onInstall: (Activity) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val config = updateResult.config
    val isMandatory = updateResult.urgency == UpdateUrgency.MANDATORY

    // Automatically trigger official Android installer intent once download finishes and is verified
    LaunchedEffect(downloadState) {
        if (downloadState is DownloadProgressState.ReadyToInstall && activity != null) {
            onInstall(activity)
        }
    }

    BasicAlertDialog(
        onDismissRequest = {
            if (!isMandatory && downloadState !is DownloadProgressState.Downloading) {
                onDismiss()
            }
        },
        modifier = Modifier
            .testTag("inapp_update_dialog")
            .fillMaxWidth(0.92f)
            .clip(RoundedCornerShape(24.dp))
            .background(NexoraSurfaceDark)
            .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            // Header with badge and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = "Update Available",
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isMandatory) "Update Required" else "New Version Available",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "nexora v${config.latestVersion} (Current: v${updateResult.installedVersionName})",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraCyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metadata tag row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, NexoraSurfaceBorder)
                ) {
                    Text(
                        text = "GitHub Release",
                        fontSize = 11.sp,
                        color = NexoraIndigoLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, NexoraSurfaceBorder)
                ) {
                    Text(
                        text = "${config.packageSizeMb} MB",
                        fontSize = 11.sp,
                        color = NexoraTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                if (config.releaseDate.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, NexoraSurfaceBorder)
                    ) {
                        Text(
                            text = config.releaseDate,
                            fontSize = 11.sp,
                            color = NexoraTextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = NexoraSurfaceBorder)

            Spacer(modifier = Modifier.height(12.dp))

            // Body: Release notes or active download state
            when (downloadState) {
                is DownloadProgressState.Idle -> {
                    Text(
                        text = "Release Notes:",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        config.releaseNotes.forEach { note ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = "•",
                                    color = NexoraCyanAccent,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                                Text(
                                    text = note,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexoraTextSecondary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }

                is DownloadProgressState.Preparing -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = NexoraCyanAccent,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Connecting to GitHub Releases...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NexoraTextSecondary
                        )
                    }
                }

                is DownloadProgressState.Downloading -> {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Downloading APK...",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                color = NexoraTextPrimary
                            )
                            Text(
                                text = "${downloadState.progressPercent}%",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraCyanAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { downloadState.progressPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NexoraCyanAccent,
                            trackColor = NexoraSurfaceElevated,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${formatFileSize(downloadState.downloadedBytes)} / ${formatFileSize(downloadState.totalBytes)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextMuted
                            )
                            Text(
                                text = downloadState.downloadSpeed,
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextSecondary
                            )
                        }
                    }
                }

                is DownloadProgressState.Verifying -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = NexoraCyanAccent,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = downloadState.step,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary
                        )
                    }
                }

                is DownloadProgressState.ReadyToInstall -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NexoraSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NexoraSuccess,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Download complete!",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Opening official Android Package Installer...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary
                        )
                    }
                }

                is DownloadProgressState.Failed -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = NexoraError,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Update Error",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraError
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = downloadState.errorMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary
                        )
                    }
                }

                is DownloadProgressState.Cancelled -> {
                    Text(
                        text = "Download was cancelled.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (downloadState) {
                    is DownloadProgressState.Idle -> {
                        if (!isMandatory) {
                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier.testTag("btn_inapp_update_later")
                            ) {
                                Text("Later", color = NexoraTextSecondary)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Button(
                            onClick = onUpdateNow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexoraCyanAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_inapp_update_now")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Update Now", fontWeight = FontWeight.Bold)
                        }
                    }

                    is DownloadProgressState.Downloading -> {
                        OutlinedButton(
                            onClick = onCancelDownload,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_inapp_cancel_download")
                        ) {
                            Text("Cancel", color = NexoraTextSecondary)
                        }
                    }

                    is DownloadProgressState.ReadyToInstall -> {
                        Button(
                            onClick = {
                                if (activity != null) onInstall(activity)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexoraCyanAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_inapp_install_now")
                        ) {
                            Icon(
                                imageVector = Icons.Default.InstallMobile,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Install Now", fontWeight = FontWeight.Bold)
                        }
                    }

                    is DownloadProgressState.Failed -> {
                        TextButton(onClick = onDismiss) {
                            Text("Close", color = NexoraTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = onUpdateNow,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NexoraCyanAccent,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_inapp_retry_download")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry", fontWeight = FontWeight.Bold)
                        }
                    }

                    else -> {
                        TextButton(onClick = onDismiss) {
                            Text("Dismiss", color = NexoraTextSecondary)
                        }
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes / (1024.0 * 1024.0)
    return String.format(Locale.US, "%.1f MB", mb)
}
