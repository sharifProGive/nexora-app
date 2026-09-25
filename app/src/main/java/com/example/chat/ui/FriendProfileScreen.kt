package com.example.chat.ui

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.model.RelationshipStatus
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

@Composable
fun FriendProfileScreen(
    user: SocialUser,
    onBack: () -> Unit,
    onMessageClick: (SocialUser) -> Unit,
    onAudioCallClick: (SocialUser) -> Unit,
    onVideoCallClick: (SocialUser) -> Unit,
    onFollowToggle: (SocialUser) -> Unit,
    onBlockUser: (SocialUser) -> Unit,
    onReportUser: (SocialUser) -> Unit
) {
    var showMoreMenu by remember { mutableStateOf(false) }

    val avatarColor = remember(user.avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(user.avatarColorHex))
        } catch (_: Exception) {
            NexoraIndigoPrimary
        }
    }

    Column(
        modifier = Modifier
            .testTag("friend_profile_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_friend_profile_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NexoraTextPrimary
                )
            }

            Text(
                text = "Profile",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Box {
                IconButton(onClick = { showMoreMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = NexoraTextSecondary
                    )
                }

                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false },
                    modifier = Modifier.background(NexoraSurfaceDark)
                ) {
                    DropdownMenuItem(
                        text = { Text("Report User", color = Color(0xFFEF4444)) },
                        leadingIcon = {
                            Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFEF4444))
                        },
                        onClick = {
                            showMoreMenu = false
                            onReportUser(user)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Block User", color = Color(0xFFEF4444)) },
                        leadingIcon = {
                            Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444))
                        },
                        onClick = {
                            showMoreMenu = false
                            onBlockUser(user)
                        }
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Big Avatar with online indicator
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(avatarColor)
                        .border(3.dp, NexoraSurfaceBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(3.dp, NexoraDarkBackground, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = user.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Text(
                text = user.handle,
                style = MaterialTheme.typography.bodyMedium,
                color = NexoraIndigoLight
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Privacy & Online Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NexoraSurfaceElevated
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (user.isPrivateProfile) Icons.Default.Lock else Icons.Default.Public,
                            contentDescription = null,
                            tint = if (user.isPrivateProfile) NexoraTextMuted else NexoraCyanAccent,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (user.isPrivateProfile) "Private Account" else "Public Profile",
                            fontSize = 11.sp,
                            color = if (user.isPrivateProfile) NexoraTextMuted else NexoraCyanAccent
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NexoraSurfaceElevated
                ) {
                    Text(
                        text = user.lastActiveText,
                        fontSize = 11.sp,
                        color = if (user.isOnline) Color(0xFF10B981) else NexoraTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bio
            Text(
                text = user.bio.ifBlank { "Member of the NEXORA community." },
                style = MaterialTheme.typography.bodyMedium,
                color = NexoraTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Card (Followers, Following, Mutual Friends)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = NexoraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user.followersCount}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(text = "Followers", style = MaterialTheme.typography.labelSmall, color = NexoraTextMuted)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user.followingCount}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(text = "Following", style = MaterialTheme.typography.labelSmall, color = NexoraTextMuted)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user.mutualFriendsCount}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraCyanAccent
                        )
                        Text(text = "Mutual", style = MaterialTheme.typography.labelSmall, color = NexoraTextMuted)
                    }
                }
            }

            // Creator Channel Card (if user has channel)
            if (user.hasChannel && user.channelName != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user.channelName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                            Text(
                                text = user.channelHandle ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraCyanAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action: MESSAGE button
            Surface(
                onClick = { onMessageClick(user) },
                shape = RoundedCornerShape(14.dp),
                color = NexoraCyanAccent,
                modifier = Modifier
                    .testTag("btn_profile_message")
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MESSAGE",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Actions: Follow/Following + Audio Call + Video Call
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Follow / Following Button
                Surface(
                    onClick = { onFollowToggle(user) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (user.relationshipStatus == RelationshipStatus.FOLLOWING || user.relationshipStatus == RelationshipStatus.FRIENDS)
                        NexoraSurfaceElevated else NexoraIndigoPrimary,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .testTag("btn_profile_follow_toggle")
                        .weight(1f)
                        .height(44.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        val isFollowing = user.relationshipStatus == RelationshipStatus.FOLLOWING || user.relationshipStatus == RelationshipStatus.FRIENDS
                        Icon(
                            imageVector = if (isFollowing) Icons.Default.Check else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = if (isFollowing) NexoraCyanAccent else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = when (user.relationshipStatus) {
                                RelationshipStatus.FRIENDS -> "Friends"
                                RelationshipStatus.FOLLOWING -> "Following"
                                RelationshipStatus.FOLLOW_REQUESTED -> "Requested"
                                else -> "Follow"
                            },
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = if (isFollowing) NexoraCyanAccent else Color.White
                        )
                    }
                }

                // Audio Call Quick Button
                Surface(
                    onClick = { onAudioCallClick(user) },
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .testTag("btn_profile_audio_call")
                        .size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Audio Call",
                            tint = NexoraTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Video Call Quick Button
                Surface(
                    onClick = { onVideoCallClick(user) },
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .testTag("btn_profile_video_call")
                        .size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Video Call",
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
