package com.example.ui.update

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraLogoBadge
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.ui.theme.NexoraVioletAccent
import com.example.update.model.DownloadProgressState
import com.example.update.model.RemoteVersionConfig

@Composable
fun UpdateScreen(
    config: RemoteVersionConfig,
    isMandatory: Boolean,
    installedVersionName: String,
    installedVersionCode: Int,
    downloadState: DownloadProgressState,
    onStartDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    onResetDownload: () -> Unit,
    onInstallApk: (Activity) -> Unit,
    onDismissOptional: () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context as? Activity

    // Prevent back button bypass when update is mandatory
    BackHandler(enabled = isMandatory) {
        // App is locked for mandatory update; do not pop backstack
    }

    val glowRotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        glowRotation.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    Box(
        modifier = Modifier
            .testTag("update_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Premium NEXORA Logo with glowing aura
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                // Subtle orbiting pulse
                Canvas(
                    modifier = Modifier
                        .size(100.dp)
                        .rotate(glowRotation.value)
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                NexoraCyanAccent.copy(alpha = 0.4f),
                                Color.Transparent,
                                NexoraIndigoPrimary.copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension / 2f
                    )
                }

                NexoraLogoBadge(size = 72)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Urgency Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isMandatory) NexoraError.copy(alpha = 0.15f) else NexoraCyanAccent.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isMandatory) NexoraError.copy(alpha = 0.5f) else NexoraCyanAccent.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isMandatory) Icons.Default.Lock else Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = if (isMandatory) NexoraError else NexoraCyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isMandatory) "UPDATE REQUIRED" else "NEW VERSION AVAILABLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        ),
                        color = if (isMandatory) NexoraError else NexoraCyanAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Headline
            Text(
                text = if (isMandatory) "Update Required" else "New Version Available",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                ),
                color = NexoraTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isMandatory)
                    "A new version of NEXORA is available.\nPlease update NEXORA to continue using the app."
                else
                    "A new version of NEXORA is ready. Enjoy faster performance and new creator capabilities.",
                style = MaterialTheme.typography.bodyMedium,
                color = NexoraTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Version Comparison Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "CURRENT VERSION",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = NexoraTextMuted
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "v$installedVersionName",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextSecondary
                        )
                        Text(
                            text = "Build $installedVersionCode",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(24.dp)
                    )

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "NEW VERSION",
                            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.sp),
                            color = NexoraCyanAccent
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "v${config.latestVersion}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = "Build ${config.latestVersionCode} • ${config.packageSizeMb} MB",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraIndigoLight
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Release Notes Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "What's New in v${config.latestVersion}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = config.releaseDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = NexoraSurfaceBorder.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.height(12.dp))

                    config.releaseNotes.forEach { note ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                color = NexoraCyanAccent,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = note,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // DOWNLOAD & INSTALLATION PROGRESS CONTAINER
            // ==========================================
            when (downloadState) {
                is DownloadProgressState.Idle -> {
                    // Pre-download state: Action button
                    Button(
                        onClick = onStartDownload,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NexoraCyanAccent,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .testTag("btn_update_now")
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "UPDATE NOW",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    // For optional updates: allow "LATER" skip
                    if (!isMandatory) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onDismissOptional,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .testTag("btn_skip_update")
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = "LATER",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NexoraTextSecondary
                            )
                        }
                    }
                }

                is DownloadProgressState.Preparing -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = NexoraCyanAccent,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Preparing update...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraTextPrimary
                                )
                                Text(
                                    text = "Allocating storage and contacting release CDN...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraTextSecondary
                                )
                            }
                        }
                    }
                }

                is DownloadProgressState.Downloading -> {
                    val animatedProgress by animateFloatAsState(
                        targetValue = downloadState.progressPercent / 100f,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "progress"
                    )

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .testTag("download_progress_card")
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        tint = NexoraCyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Downloading update...",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = NexoraTextPrimary
                                    )
                                }
                                Text(
                                    text = "${downloadState.progressPercent}%",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                                    color = NexoraCyanAccent
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = NexoraCyanAccent,
                                trackColor = NexoraSurfaceDark,
                                strokeCap = StrokeCap.Round
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "%.1f MB / %.1f MB".format(downloadState.downloadedMb, downloadState.totalMb),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraTextSecondary
                                )
                                Text(
                                    text = downloadState.downloadSpeed,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (downloadState.isSlowNetwork) NexoraError else NexoraIndigoLight
                                )
                            }

                            // Show cancel button ONLY if update is not mandatory
                            if (!isMandatory) {
                                Spacer(modifier = Modifier.height(12.dp))
                                TextButton(
                                    onClick = onCancelDownload,
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Text("Cancel Download", color = NexoraTextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                is DownloadProgressState.Verifying -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraIndigoLight.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = NexoraIndigoLight,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Verifying package integrity...",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraTextPrimary
                                )
                                Text(
                                    text = downloadState.step,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraTextSecondary
                                )
                            }
                        }
                    }
                }

                is DownloadProgressState.ReadyToInstall -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSuccess.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = NexoraSuccess,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Update verified & ready to install",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = NexoraTextPrimary
                                    )
                                    Text(
                                        text = "Signed NEXORA package (Build ${downloadState.archiveVersionCode})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexoraTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Android will now show the official installation confirmation prompt. Tap 'Install' on the system dialog to finish.",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    activity?.let { onInstallApk(it) }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NexoraSuccess,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .testTag("btn_install_update")
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.InstallMobile,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "CONTINUE TO INSTALLATION",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }

                is DownloadProgressState.Failed -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraError.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraError.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = null,
                                    tint = NexoraError,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (downloadState.isStorageIssue) "Storage Space Issue" else "Download Failed",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraError
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = downloadState.errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary
                            )

                            if (downloadState.canRetry) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = onResetDownload,
                                    colors = ButtonDefaults.buttonColors(containerColor = NexoraError),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier
                                        .testTag("btn_retry_update_download")
                                        .fillMaxWidth()
                                        .height(44.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Retry Download", fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                is DownloadProgressState.Cancelled -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Download was cancelled.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Button(
                                onClick = onResetDownload,
                                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyanAccent, contentColor = Color.Black),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Restart Download", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Security statement footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = NexoraTextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Official NEXORA cryptographic package verification enabled.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
