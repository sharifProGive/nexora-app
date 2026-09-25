package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creator.model.SocialLinksJsonHelper
import com.example.creator.ui.ContentPreviewPlayerDialog
import com.example.creator.ui.EditContentDetailsDialog
import com.example.creator.viewmodel.CreatorViewModel
import com.example.data.model.CreatorContentEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraErrorRed
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSuccessGreen
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

private val BANNER_GRADIENTS = listOf(
    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF4C1D95), Color(0xFF06B6D4)),
    listOf(Color(0xFF18181B), Color(0xFF27272A), Color(0xFF3F3F46), Color(0xFF71717A)),
    listOf(Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981)),
    listOf(Color(0xFF450A0A), Color(0xFF7F1D1D), Color(0xFFB91C1C), Color(0xFFF97316)),
    listOf(Color(0xFF172554), Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF38BDF8))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelPageScreen(
    user: UserEntity,
    viewModel: CreatorViewModel,
    onBackToProfile: () -> Unit,
    onOpenEditChannel: () -> Unit,
    onOpenUploadVideo: () -> Unit,
    onOpenCreateShort: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val channel = uiState.channel
    val publishedContent by viewModel.publishedContent.collectAsState()
    val drafts by viewModel.draftsList.collectAsState()

    var selectedChannelTab by remember { mutableIntStateOf(0) }
    // PART 16 Content Types: 1. VIDEOS, 2. SHORTS, 3. LIVE, 4. PLAYLISTS, 5. POSTS
    val tabs = listOf("VIDEOS", "SHORTS", "LIVE", "PLAYLISTS", "POSTS")

    // Content management dialogs
    var contentToDelete by remember { mutableStateOf<CreatorContentEntity?>(null) }
    var contentToEdit by remember { mutableStateOf<CreatorContentEntity?>(null) }
    var contentToPreview by remember { mutableStateOf<CreatorContentEntity?>(null) }

    val channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty()
    val channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty()
    val channelBio = channel?.description?.takeIf { it.isNotBlank() } ?: user.channelBio.orEmpty()
    val avatarColorHex = channel?.avatarColorHex ?: "#06B6D4"
    val bannerGradientIndex = channel?.bannerGradientIndex ?: 0
    val currentBannerGradient = BANNER_GRADIENTS.getOrElse(bannerGradientIndex) { BANNER_GRADIENTS.first() }

    val avatarColor = remember(avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(avatarColorHex))
        } catch (_: Exception) {
            NexoraCyanAccent
        }
    }

    val socialLinks = remember(channel?.socialLinksJson) {
        SocialLinksJsonHelper.fromJson(channel?.socialLinksJson)
    }

    val videosList = remember(publishedContent) { publishedContent.filter { it.contentType == "VIDEO" } }
    val shortsList = remember(publishedContent) { publishedContent.filter { it.contentType == "SHORT" } }
    val postsList = remember(publishedContent) { publishedContent.filter { it.contentType == "POST" || it.contentType == "POLL" } }

    Column(
        modifier = Modifier
            .testTag("channel_page_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // Channel Top Bar with Back Button to return to Personal Profile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToProfile,
                    modifier = Modifier.testTag("btn_back_to_profile")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Personal Profile",
                        tint = NexoraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = channelName.ifEmpty { "Video Channel" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Text(
                        text = "Video Channel",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyanAccent
                    )
                }
            }

            Row {
                IconButton(
                    onClick = onOpenEditChannel,
                    modifier = Modifier.testTag("btn_top_edit_channel")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Channel",
                        tint = NexoraCyanAccent
                    )
                }
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Channel",
                        tint = NexoraTextSecondary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Channel Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(Brush.horizontalGradient(currentBannerGradient))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (i in 0..12) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(i * 35f, 0f),
                            end = Offset(i * 35f + 50f, size.height),
                            strokeWidth = 2f
                        )
                    }
                }
            }

            // Channel Header Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Channel Avatar
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                            .border(2.dp, NexoraSurfaceBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = channelName.firstOrNull()?.toString()?.uppercase() ?: "C",
                            fontWeight = FontWeight.Black,
                            fontSize = 28.sp,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = channelName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        if (channelHandle.isNotBlank()) {
                            Text(
                                text = channelHandle,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                color = NexoraTextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        val totalUploads = publishedContent.size
                        Text(
                            text = "${if (channel?.showSubscriberCount != false) "${channel?.subscriberCount ?: 0} subscribers · " else ""}$totalUploads uploads",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }
                }

                if (channelBio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = channelBio,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextSecondary
                    )
                }

                // Social Links row
                if (socialLinks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        socialLinks.take(3).forEach { link ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = NexoraSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, NexoraSurfaceBorder)
                            ) {
                                Text(
                                    text = "${link.platform}: ${link.title}",
                                    color = NexoraCyanAccent,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Separation Architecture Notice
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Hub,
                            contentDescription = null,
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Channel owned by Personal Profile '${user.name}'. Both entities exist independently.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraIndigoLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons: Edit Channel, Upload Video, Personal Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        onClick = onOpenEditChannel,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraCyanAccent,
                        modifier = Modifier
                            .testTag("btn_channel_edit_channel")
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit Channel", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                        }
                    }

                    Surface(
                        onClick = onOpenUploadVideo,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier
                            .testTag("btn_channel_upload_video")
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = NexoraTextPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold), color = NexoraTextPrimary)
                        }
                    }

                    Surface(
                        onClick = onBackToProfile,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier
                            .testTag("btn_switch_to_personal_profile")
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = NexoraTextSecondary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Profile", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold), color = NexoraTextSecondary)
                        }
                    }
                }

                // Drafts Banner (PART 11 & PART 16)
                if (drafts.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Drafts, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${drafts.size} Unpublished Draft${if (drafts.size > 1) "s" else ""}",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraTextPrimary
                                )
                            }
                            Text(
                                text = "Saved locally",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraCyanAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Channel Content Tabs (PART 16: VIDEOS, SHORTS, LIVE, PLAYLISTS, POSTS)
            TabRow(
                selectedTabIndex = selectedChannelTab,
                containerColor = NexoraDarkBackground,
                contentColor = NexoraTextPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedChannelTab]),
                        color = NexoraCyanAccent
                    )
                },
                divider = { HorizontalDivider(color = NexoraSurfaceBorder) }
            ) {
                tabs.forEachIndexed { index, tabName ->
                    Tab(
                        selected = selectedChannelTab == index,
                        onClick = { selectedChannelTab = index },
                        text = {
                            Text(
                                text = tabName,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedChannelTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedChannelTab == index) NexoraCyanAccent else NexoraTextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedChannelTab) {
                    0 -> {
                        // VIDEOS TAB
                        if (videosList.isEmpty()) {
                            EmptyContentState(
                                icon = Icons.Default.VideoLibrary,
                                title = "No videos yet",
                                description = "Upload your first long video to build your audience.",
                                buttonLabel = "Upload Video",
                                onAction = onOpenUploadVideo,
                                testTag = "empty_videos_state"
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                videosList.forEach { video ->
                                    CreatorContentItemCard(
                                        content = video,
                                        onPreview = { contentToPreview = video },
                                        onEdit = { contentToEdit = video },
                                        onChangeVisibility = { newVis -> viewModel.updateContentVisibility(video.id, newVis) },
                                        onDelete = { contentToDelete = video }
                                    )
                                }
                            }
                        }
                    }

                    1 -> {
                        // SHORTS TAB
                        if (shortsList.isEmpty()) {
                            EmptyContentState(
                                icon = Icons.Default.SmartDisplay,
                                title = "No Shorts yet",
                                description = "Capture vertical moments or upload short videos.",
                                buttonLabel = "Create Short",
                                onAction = onOpenCreateShort,
                                testTag = "empty_shorts_state"
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                shortsList.forEach { short ->
                                    CreatorContentItemCard(
                                        content = short,
                                        onPreview = { contentToPreview = short },
                                        onEdit = { contentToEdit = short },
                                        onChangeVisibility = { newVis -> viewModel.updateContentVisibility(short.id, newVis) },
                                        onDelete = { contentToDelete = short }
                                    )
                                }
                            }
                        }
                    }

                    2 -> {
                        // LIVE TAB (LOCKED AS PER REQUIREMENTS)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(NexoraSurfaceElevated),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NexoraTextMuted, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Live access is currently unavailable.",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No past live streams found. Live streaming will unlock when your channel meets community criteria.",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }

                    3 -> {
                        // PLAYLISTS TAB
                        val playlists = remember(publishedContent) {
                            publishedContent.mapNotNull { it.playlist }.distinct()
                        }
                        if (playlists.isEmpty()) {
                            EmptyContentState(
                                icon = Icons.Default.PlaylistPlay,
                                title = "No playlists yet",
                                description = "Organize your videos into playlists when uploading.",
                                buttonLabel = "Upload Video",
                                onAction = onOpenUploadVideo,
                                testTag = "empty_playlists_state"
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                playlists.forEach { plName ->
                                    val count = publishedContent.count { it.playlist == plName }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = NexoraSurfaceDark,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(14.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.PlaylistPlay, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(plName, fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                                                Text("$count video${if (count > 1) "s" else ""}", fontSize = 11.sp, color = NexoraTextSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    4 -> {
                        // POSTS TAB
                        if (postsList.isEmpty()) {
                            EmptyContentState(
                                icon = Icons.Default.PostAdd,
                                title = "No posts yet",
                                description = "Share thoughts, polls, and announcements with your community.",
                                buttonLabel = "New Post",
                                onAction = { /* Handled via + Create modal */ },
                                testTag = "empty_posts_state"
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                postsList.forEach { post ->
                                    CreatorContentItemCard(
                                        content = post,
                                        onPreview = { contentToPreview = post },
                                        onEdit = { contentToEdit = post },
                                        onChangeVisibility = { newVis -> viewModel.updateContentVisibility(post.id, newVis) },
                                        onDelete = { contentToDelete = post }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // PART 17 — DELETE CONFIRMATION DIALOG:
    // "Are you sure you want to delete this content? This action cannot be undone."
    // CANCEL / DELETE
    if (contentToDelete != null) {
        val target = contentToDelete!!
        AlertDialog(
            onDismissRequest = { contentToDelete = null },
            containerColor = NexoraSurfaceDark,
            titleContentColor = NexoraTextPrimary,
            textContentColor = NexoraTextSecondary,
            title = {
                Text("Delete Content?", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("Are you sure you want to delete this content? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteContent(target.id)
                        contentToDelete = null
                    },
                    modifier = Modifier.testTag("btn_confirm_delete_content")
                ) {
                    Text("DELETE", color = NexoraErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { contentToDelete = null },
                    modifier = Modifier.testTag("btn_cancel_delete_content")
                ) {
                    Text("CANCEL", color = NexoraTextSecondary)
                }
            }
        )
    }

    // EDIT DETAILS DIALOG
    if (contentToEdit != null) {
        val target = contentToEdit!!
        EditContentDetailsDialog(
            content = target,
            onDismiss = { contentToEdit = null },
            onSave = { title, description, hashtags, visibility, isMadeForKids, location, allowComments, category, playlist, isAiGenerated ->
                viewModel.updateContentDetails(
                    id = target.id,
                    title = title,
                    description = description,
                    hashtags = hashtags,
                    visibility = visibility,
                    isMadeForKids = isMadeForKids,
                    location = location,
                    allowComments = allowComments,
                    category = category,
                    playlist = playlist,
                    isAiGenerated = isAiGenerated
                )
                contentToEdit = null
            }
        )
    }

    // PREVIEW DIALOG
    if (contentToPreview != null) {
        ContentPreviewPlayerDialog(
            content = contentToPreview!!,
            onDismiss = { contentToPreview = null }
        )
    }
}

@Composable
private fun CreatorContentItemCard(
    content: CreatorContentEntity,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onChangeVisibility: (String) -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        onClick = onPreview,
        shape = RoundedCornerShape(14.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail / Icon Box
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 54.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                        )
                    )
                    .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (content.contentType) {
                        "SHORT" -> Icons.Default.SmartDisplay
                        "POLL" -> Icons.Default.Poll
                        "POST" -> Icons.Default.PostAdd
                        else -> Icons.Default.PlayArrow
                    },
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(22.dp)
                )

                if (content.isAiGenerated) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        modifier = Modifier.align(Alignment.TopEnd).padding(3.dp)
                    ) {
                        Text("AI", fontSize = 8.sp, color = NexoraCyanAccent, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 3.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = content.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = content.visibility,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (content.visibility) {
                            "PUBLIC" -> NexoraSuccessGreen
                            "PRIVATE" -> NexoraErrorRed
                            else -> NexoraCyanAccent
                        }
                    )
                    Text(" • ", fontSize = 11.sp, color = NexoraTextMuted)
                    Text(
                        text = "${content.viewsCount} views",
                        fontSize = 11.sp,
                        color = NexoraTextSecondary
                    )
                }
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Content Options", tint = NexoraTextSecondary)
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(NexoraSurfaceElevated)
                ) {
                    DropdownMenuItem(
                        text = { Text("View", color = NexoraTextPrimary) },
                        onClick = {
                            showMenu = false
                            onPreview()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Details", color = NexoraTextPrimary) },
                        onClick = {
                            showMenu = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Make Public", color = NexoraTextPrimary) },
                        onClick = {
                            showMenu = false
                            onChangeVisibility("PUBLIC")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Make Unlisted", color = NexoraTextPrimary) },
                        onClick = {
                            showMenu = false
                            onChangeVisibility("UNLISTED")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Make Private", color = NexoraTextPrimary) },
                        onClick = {
                            showMenu = false
                            onChangeVisibility("PRIVATE")
                        }
                    )
                    HorizontalDivider(color = NexoraSurfaceBorder)
                    DropdownMenuItem(
                        text = { Text("Delete", color = NexoraErrorRed, fontWeight = FontWeight.Bold) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyContentState(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    buttonLabel: String,
    onAction: () -> Unit,
    testTag: String = ""
) {
    Column(
        modifier = Modifier
            .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier)
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(NexoraSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(28.dp))
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = NexoraTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = NexoraTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(18.dp))

        Surface(
            onClick = onAction,
            shape = RoundedCornerShape(10.dp),
            color = NexoraCyanAccent
        ) {
            Text(
                text = buttonLabel,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}
