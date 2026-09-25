package com.example.chat.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMissed
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.VideoCameraFront
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.model.NotificationType
import com.example.chat.model.SocialNotification
import com.example.chat.model.SocialUser
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialNotificationsSheet(
    notifications: List<SocialNotification>,
    onAcceptRequest: (userId: String) -> Unit,
    onDeclineRequest: (userId: String) -> Unit,
    onOpenUserChat: (SocialUser) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexoraSurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("social_notifications_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Social Notifications",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NexoraTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (notifications.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No new notifications",
                            style = MaterialTheme.typography.titleMedium,
                            color = NexoraTextSecondary
                        )
                        Text(
                            text = "Friend requests and activity updates will appear here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(notifications) { notif ->
                        NotificationCard(
                            notification = notif,
                            onAccept = { onAcceptRequest(notif.actorUser.id) },
                            onDecline = { onDeclineRequest(notif.actorUser.id) },
                            onTap = {
                                onDismiss()
                                onOpenUserChat(notif.actorUser)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: SocialNotification,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onTap: () -> Unit
) {
    val (icon, iconColor) = when (notification.type) {
        NotificationType.FOLLOW, NotificationType.REQUEST_ACCEPTED -> Icons.Default.PersonAdd to NexoraCyanAccent
        NotificationType.FRIEND_REQUEST -> Icons.Default.PersonAdd to Color(0xFFF59E0B)
        NotificationType.NEW_MESSAGE -> Icons.Default.Chat to NexoraIndigoLight
        NotificationType.MISSED_AUDIO_CALL -> Icons.Default.CallMissed to Color(0xFFEF4444)
        NotificationType.MISSED_VIDEO_CALL -> Icons.Default.VideoCameraFront to Color(0xFFEF4444)
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NexoraSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTap() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextSecondary
                    )
                }
            }

            // Friend Request Action Buttons
            if (notification.type == NotificationType.FRIEND_REQUEST) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        onClick = onAccept,
                        shape = RoundedCornerShape(8.dp),
                        color = NexoraCyanAccent,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Accept",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }

                    Surface(
                        onClick = onDecline,
                        shape = RoundedCornerShape(8.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "Decline",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NexoraTextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}
