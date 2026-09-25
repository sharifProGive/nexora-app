package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlusCreateModal(
    user: UserEntity,
    onDismiss: () -> Unit,
    onNavigateToCreateChannel: () -> Unit,
    onNavigateToCreateShort: () -> Unit = {},
    onNavigateToCreateLongVideo: () -> Unit = {},
    onNavigateToCreatePost: () -> Unit = {},
    onNavigateToCreatePoll: () -> Unit = {}
) {
    var showLiveLockedDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NexoraSurfaceDark,
        contentColor = NexoraTextPrimary,
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
                .testTag("plus_create_modal")
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = if (!user.hasChannel) "Create on NEXORA" else "CREATE",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!user.hasChannel) {
                // Rule: Account has NOT created a Video Channel yet
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Hub,
                                    contentDescription = null,
                                    tint = NexoraCyanAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Channel Required",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Create a channel to start uploading videos.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexoraTextPrimary
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Video uploads are distributed through dedicated Video Channels. Your Personal Profile '${user.name}' remains separate and intact.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Button to create channel
                        Surface(
                            onClick = {
                                onDismiss()
                                onNavigateToCreateChannel()
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = NexoraCyanAccent,
                            modifier = Modifier
                                .testTag("btn_modal_create_channel")
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Create Video Channel",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            } else {
                // Rule: Account HAS a Video Channel
                // Show:
                // 1. SHORT
                // 2. LONG VIDEO
                // 3. LIVE 🔒
                // 4. POLL
                // 5. POST
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraCyanAccent.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.channelName?.firstOrNull()?.toString()?.uppercase() ?: "C",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = user.channelName.orEmpty().ifEmpty { "Video Channel" },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraCyanAccent
                            )
                            if (!user.channelHandle.isNullOrBlank()) {
                                Text(
                                    text = user.channelHandle,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 1. SHORT
                CreateActionRow(
                    icon = Icons.Default.SmartDisplay,
                    title = "SHORT",
                    subtitle = "Capture vertical clip or upload from device",
                    enabled = true,
                    onClick = {
                        onDismiss()
                        onNavigateToCreateShort()
                    },
                    testTag = "btn_create_short_option"
                )

                // 2. LONG VIDEO
                CreateActionRow(
                    icon = Icons.Default.Upload,
                    title = "LONG VIDEO",
                    subtitle = "Upload standard landscape video with full metadata",
                    enabled = true,
                    onClick = {
                        onDismiss()
                        onNavigateToCreateLongVideo()
                    },
                    testTag = "btn_create_long_video_option"
                )

                // 3. LIVE 🔒
                CreateActionRow(
                    icon = Icons.Default.Lock,
                    title = "LIVE 🔒",
                    subtitle = "Live access is currently unavailable",
                    enabled = true,
                    onClick = {
                        showLiveLockedDialog = true
                    },
                    testTag = "btn_create_live_option",
                    tintColor = NexoraTextMuted
                )

                // 4. POLL
                CreateActionRow(
                    icon = Icons.Default.Poll,
                    title = "POLL",
                    subtitle = "Ask your community and gather votes",
                    enabled = true,
                    onClick = {
                        onDismiss()
                        onNavigateToCreatePoll()
                    },
                    testTag = "btn_create_poll_option"
                )

                // 5. POST
                CreateActionRow(
                    icon = Icons.Default.PostAdd,
                    title = "POST",
                    subtitle = "Share text and image updates with your subscribers",
                    enabled = true,
                    onClick = {
                        onDismiss()
                        onNavigateToCreatePost()
                    },
                    testTag = "btn_create_post_option"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // LIVE LOCKED DIALOG (PART 15)
    if (showLiveLockedDialog) {
        AlertDialog(
            onDismissRequest = { showLiveLockedDialog = false },
            containerColor = NexoraSurfaceDark,
            titleContentColor = NexoraTextPrimary,
            textContentColor = NexoraTextSecondary,
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Live Access Locked",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Live access is currently unavailable.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Live streaming is not currently available for this channel. Mobile live streaming will be unlocked once your channel meets eligibility guidelines (50+ subscribers).",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showLiveLockedDialog = false },
                    modifier = Modifier.testTag("btn_close_live_locked")
                ) {
                    Text("OK", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun CreateActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String = "",
    tintColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (enabled) NexoraSurfaceElevated else NexoraSurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor ?: if (enabled) NexoraCyanAccent else NexoraTextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (enabled) NexoraTextPrimary else NexoraTextMuted
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = NexoraTextSecondary
            )
        }
    }
}
