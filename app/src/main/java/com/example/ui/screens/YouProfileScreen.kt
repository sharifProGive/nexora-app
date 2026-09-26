package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.UserEntity
import com.example.security.SecurityUtils
import com.example.ui.components.NexoraTextField
import com.example.ui.theme.NexoraTheme
import com.example.upload.model.UploadTargetType
import com.example.upload.model.UploadTask
import com.example.upload.service.UploadManager
import com.example.upload.ui.CentralizedUploadHost
import com.example.upload.ui.rememberCentralizedUploadController
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YouProfileScreen(
    user: UserEntity,
    onOpenFullProfile: () -> Unit,
    onOpenChannelPage: () -> Unit,
    onCreateChannel: (channelName: String, channelHandle: String, channelBio: String?) -> Unit,
    onTogglePrivacy: (isPrivate: Boolean) -> Unit,
    onUpdateProfile: (name: String, bio: String, avatarIndex: Int) -> Unit,
    onLogout: () -> Unit,
    onOpenSettings: () -> Unit = {},
    installedVersionName: String = "1.0.0",
    installedVersionCode: Int = 1,
    updateCheckResult: com.example.update.model.UpdateCheckResult? = null,
    onCheckForUpdates: () -> Unit = {},
    onOpenUpdateScreen: () -> Unit = {},
    onOpenAdminDialog: () -> Unit = {},
    onOpenFriendsChat: () -> Unit = {},
    onOpenEditChannel: () -> Unit = {}
) {
    val context = LocalContext.current
    val uploadManager = remember { UploadManager.getInstance(context) }
    val activeUploads by uploadManager.activeTasks.collectAsState()
    val uploadController = rememberCentralizedUploadController()

    var selectedProfileTab by remember { mutableIntStateOf(0) }
    var showCreateChannelDialog by remember { mutableStateOf(false) }
    var showEditProfileSheet by remember { mutableStateOf(false) }

    val tabs = listOf("VIDEOS", "PHOTOS", "ABOUT")
    val colors = NexoraTheme.colors

    val avatarColor = remember(user.avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(user.avatarColorHex))
        } catch (_: Exception) {
            colors.primary
        }
    }

    Column(
        modifier = Modifier
            .testTag("you_profile_screen")
            .fillMaxSize()
            .background(colors.background)
    ) {
        // Top App Bar: Compact & Clean "You" + Upload Manager + Settings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "You",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    letterSpacing = 0.5.sp
                ),
                color = colors.textPrimary
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Upload Manager access icon
                IconButton(
                    onClick = { uploadController.openUploadManager() },
                    modifier = Modifier
                        .testTag("btn_top_upload_manager")
                        .size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = "Upload Manager",
                            tint = if (activeUploads.isNotEmpty()) colors.accent else colors.textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        if (activeUploads.isNotEmpty()) {
                            Surface(
                                shape = CircleShape,
                                color = colors.accent,
                                modifier = Modifier
                                    .size(10.dp)
                                    .align(Alignment.TopEnd)
                            ) {}
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Dedicated, clearly tappable Settings Gear icon
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .testTag("btn_top_settings")
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = colors.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Card (Compact & Modern YouTube-Style)
            Surface(
                onClick = onOpenFullProfile,
                shape = RoundedCornerShape(14.dp),
                color = colors.surface,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.surfaceBorder),
                modifier = Modifier
                    .testTag("card_user_profile_header")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Profile Avatar with Centralized Upload Change Picture trigger
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(avatarColor)
                                .border(1.5.dp, colors.surfaceBorder, CircleShape)
                                .clickable {
                                    uploadController.requestMedia(UploadTargetType.PROFILE_PICTURE) { mediaItem ->
                                        val task = UploadTask(
                                            id = UUID.randomUUID().toString(),
                                            targetType = UploadTargetType.PROFILE_PICTURE,
                                            title = "Personal Profile Picture",
                                            mediaUri = mediaItem.uri.toString(),
                                            mimeType = mediaItem.mimeType,
                                            fileSizeBytes = mediaItem.fileSizeBytes
                                        )
                                        uploadManager.enqueueUpload(task)
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!user.profilePictureUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = user.profilePictureUri,
                                    contentDescription = user.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Text(
                                    text = user.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 24.sp,
                                    color = Color.White
                                )
                            }

                            // Small camera icon indicator at bottom
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .align(Alignment.BottomEnd)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(1.dp, colors.accent, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change Picture",
                                    tint = Color.White,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Open Full Profile",
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Text(
                                text = user.handle,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = colors.primaryLight
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            // Privacy badge
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (user.isPrivateProfile) Icons.Default.Lock else Icons.Default.Public,
                                    contentDescription = null,
                                    tint = if (user.isPrivateProfile) colors.textMuted else colors.accent,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (user.isPrivateProfile) "Private Profile" else "Public Profile",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (user.isPrivateProfile) colors.textMuted else colors.accent
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = user.bio.ifBlank { "Member of the nexora community." },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                        color = colors.textSecondary,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stats: Followers / Following
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${user.followersCount} Followers",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "${user.followingCount} Following",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Tap to view profile",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = colors.textMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons: Edit Profile (Compact height: 38dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    onClick = { showEditProfileSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    color = colors.surfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.surfaceBorder),
                    modifier = Modifier
                        .testTag("btn_edit_profile")
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                    }
                }

                Surface(
                    onClick = onOpenSettings,
                    shape = RoundedCornerShape(10.dp),
                    color = colors.surfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.surfaceBorder),
                    modifier = Modifier
                        .testTag("btn_privacy_settings")
                        .weight(1f)
                        .height(38.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Settings",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // CHAT WITH FRIENDS (COMPACT SOCIAL CARD)
            // ==========================================
            Surface(
                onClick = onOpenFriendsChat,
                shape = RoundedCornerShape(12.dp),
                color = colors.surface,
                border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.accent.copy(alpha = 0.4f)),
                modifier = Modifier
                    .testTag("card_chat_with_friends")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(colors.accent.copy(alpha = 0.15f))
                            .border(0.8.dp, colors.accent.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat with Friends",
                            tint = colors.accent,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Chat with Friends",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = colors.accent.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "SOCIAL",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.accent,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Connect, message, and call friends & creators.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = colors.textSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Friends & Chat",
                        tint = colors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // VIDEO CHANNEL STATUS & MANAGEMENT CARD
            // ==========================================
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceElevated,
                border = androidx.compose.foundation.BorderStroke(
                    0.8.dp,
                    if (user.hasChannel) colors.accent.copy(alpha = 0.4f) else colors.surfaceBorder
                ),
                modifier = Modifier
                    .testTag("card_channel_status")
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (user.hasChannel) colors.accent.copy(alpha = 0.15f)
                                    else colors.primary.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = if (user.hasChannel) colors.accent else colors.primaryLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Video Channel",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (user.hasChannel) "Active: ${user.channelName} (${user.channelHandle})" else "No Video Channel Created",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = if (user.hasChannel) colors.accent else colors.textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (user.hasChannel) {
                            "Personal Profile (${user.name}) and Video Channel (${user.channelName}) are separate."
                        } else {
                            "Create a Channel to publish videos & shorts while keeping your profile intact."
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (!user.hasChannel) {
                        Surface(
                            onClick = { showCreateChannelDialog = true },
                            shape = RoundedCornerShape(10.dp),
                            color = colors.primary,
                            modifier = Modifier
                                .testTag("btn_create_channel")
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Create Channel",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                onClick = onOpenEditChannel,
                                shape = RoundedCornerShape(10.dp),
                                color = colors.surface,
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.accent),
                                modifier = Modifier
                                    .testTag("btn_edit_channel")
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = colors.accent,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Edit Channel",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = colors.accent
                                    )
                                }
                            }

                            Surface(
                                onClick = onOpenChannelPage,
                                shape = RoundedCornerShape(10.dp),
                                color = colors.accent,
                                modifier = Modifier
                                    .testTag("btn_view_channel")
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartDisplay,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "View Channel",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tabs: Profile Videos & Profile Photos (Compact TabRow)
            TabRow(
                selectedTabIndex = selectedProfileTab,
                containerColor = colors.background,
                contentColor = colors.textPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedProfileTab]),
                        color = colors.accent
                    )
                },
                divider = { HorizontalDivider(color = colors.surfaceBorder.copy(alpha = 0.5f), thickness = 0.5.dp) }
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedProfileTab == index,
                        onClick = { selectedProfileTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (selectedProfileTab == index) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (selectedProfileTab == index) colors.accent else colors.textSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedProfileTab) {
                0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = colors.textMuted,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No personal videos uploaded",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Personal clips posted to your profile appear here.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = colors.textSecondary
                            )
                        }
                    }
                }
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = colors.textMuted,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No photos shared yet",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Photos and album highlights will appear here.",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = colors.textSecondary
                            )
                        }
                    }
                }
                2 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = colors.surface,
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, colors.surfaceBorder),
                            modifier = Modifier
                                .testTag("card_system_updates")
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = colors.accent,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "NEXORA System",
                                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                                            color = colors.textPrimary
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = colors.primary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "v$installedVersionName",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                                            color = colors.primaryLight,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Build: $installedVersionCode • Engine: Verified & Play Ready\nEncryption: Salted PBKDF2 & AES-256 GCM",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = colors.textSecondary,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = onCheckForUpdates,
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceElevated),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .testTag("btn_check_for_updates")
                                            .weight(1f)
                                            .height(36.dp)
                                    ) {
                                        Text(
                                            text = "Check Updates",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                            color = colors.textPrimary
                                        )
                                    }

                                    Button(
                                        onClick = onOpenAdminDialog,
                                        colors = ButtonDefaults.buttonColors(containerColor = colors.primary.copy(alpha = 0.2f)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .testTag("btn_admin_update_sim")
                                            .weight(1f)
                                            .height(36.dp)
                                    ) {
                                        Text(
                                            text = "Admin Config",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 11.sp),
                                            color = colors.accent
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    // Modal: Create Video Channel
    if (showCreateChannelDialog) {
        var newChannelName by remember { mutableStateOf("") }
        var newChannelHandle by remember { mutableStateOf("") }
        var newChannelBio by remember { mutableStateOf("") }
        var channelError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showCreateChannelDialog = false },
            containerColor = colors.surface,
            titleContentColor = colors.textPrimary,
            textContentColor = colors.textSecondary,
            title = {
                Text(text = "Create Video Channel", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "A Video Channel is a separate entity for publishing videos and shorts. Your Personal Profile '${user.name}' remains separate and intact.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    NexoraTextField(
                        value = newChannelName,
                        onValueChange = {
                            newChannelName = it
                            channelError = null
                        },
                        label = "Channel Name",
                        placeholder = "Enter channel name",
                        testTag = "input_create_channel_name"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    NexoraTextField(
                        value = newChannelHandle,
                        onValueChange = { input ->
                            val clean = if (input.isEmpty() || input.startsWith("@")) input else "@$input"
                            newChannelHandle = clean.filter { c -> c == '@' || c.isLetterOrDigit() || c == '_' }
                            channelError = null
                        },
                        label = "Channel Handle",
                        placeholder = "Enter @handle",
                        testTag = "input_create_channel_handle"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    NexoraTextField(
                        value = newChannelBio,
                        onValueChange = {
                            newChannelBio = it
                            channelError = null
                        },
                        label = "Channel Description",
                        placeholder = "Write about your channel",
                        testTag = "input_create_channel_bio"
                    )

                    if (channelError != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = channelError!!,
                            color = colors.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newChannelName.trim().isEmpty()) {
                            channelError = "Please enter a channel name."
                            return@Button
                        }
                        val cleanHandle = SecurityUtils.formatHandle(newChannelHandle.trim())
                        if (!SecurityUtils.isValidHandle(cleanHandle)) {
                            channelError = "Please enter a valid channel handle (e.g. @handle)."
                            return@Button
                        }
                        onCreateChannel(
                            newChannelName.trim(),
                            cleanHandle,
                            newChannelBio.trim().ifBlank { "" }
                        )
                        showCreateChannelDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_confirm_create_channel")
                ) {
                    Text("Create Channel", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateChannelDialog = false }) {
                    Text("Cancel", color = colors.textMuted)
                }
            }
        )
    }

    // Modal Bottom Sheet: Edit Personal Profile
    if (showEditProfileSheet) {
        var editName by remember { mutableStateOf(user.name) }
        var editBio by remember { mutableStateOf(user.bio) }
        var editAvatarIndex by remember { mutableIntStateOf(user.avatarIndex) }

        ModalBottomSheet(
            onDismissRequest = { showEditProfileSheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = colors.surface,
            contentColor = colors.textPrimary,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(colors.surfaceBorder)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Edit Personal Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                NexoraTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = "Profile Name",
                    placeholder = "Your name",
                    testTag = "input_edit_profile_name"
                )

                Spacer(modifier = Modifier.height(12.dp))

                NexoraTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = "Bio",
                    placeholder = "A brief description...",
                    testTag = "input_edit_profile_bio"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onUpdateProfile(editName, editBio, editAvatarIndex)
                        showEditProfileSheet = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .testTag("btn_save_profile_edits")
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Centralized Upload Host for Profile Picture & Media transfers
    CentralizedUploadHost(controller = uploadController)
}
