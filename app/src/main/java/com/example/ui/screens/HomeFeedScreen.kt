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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraLogoBadge
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
import com.example.ui.theme.NexoraVioletAccent

data class VideoItem(
    val id: String,
    val title: String,
    val channelName: String,
    val channelAvatarColor: Color,
    val views: String,
    val uploadTime: String,
    val duration: String,
    val gradientColors: List<Color>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeFeedScreen(
    onNavigateSearch: () -> Unit = {},
    onNavigateNotifications: () -> Unit = {}
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var selectedVideoForOptions by remember { mutableStateOf<VideoItem?>(null) }
    var showEmptyStateDemoToggle by remember { mutableStateOf(false) } // Default: Empty State ("No videos available") as mandated

    val filterChips = listOf("All", "Trending", "Gaming", "Tech", "Animation", "Creators", "Music")

    val sampleVideos = listOf(
        VideoItem(
            id = "vid_1",
            title = "Next-Gen Unreal Engine 5.5 Architectural Lighting & Physics Breakdown",
            channelName = "Skyline Pro Gamer",
            channelAvatarColor = NexoraCyanAccent,
            views = "142K views",
            uploadTime = "2 hours ago",
            duration = "14:28",
            gradientColors = listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF0369A1))
        ),
        VideoItem(
            id = "vid_2",
            title = "Building High-Throughput Distributed Microservices with Kotlin Coroutines",
            channelName = "Nexus Architecture",
            channelAvatarColor = NexoraIndigoLight,
            views = "89K views",
            uploadTime = "1 day ago",
            duration = "22:15",
            gradientColors = listOf(Color(0xFF18181B), Color(0xFF312E81), Color(0xFF4C1D95))
        ),
        VideoItem(
            id = "vid_3",
            title = "Cyberpunk 2077 Path Tracing Overhaul at 4K Ultrawide 120FPS",
            channelName = "Vortex Hardware",
            channelAvatarColor = NexoraVioletAccent,
            views = "520K views",
            uploadTime = "3 days ago",
            duration = "18:40",
            gradientColors = listOf(Color(0xFF09090B), Color(0xFF581C87), Color(0xFF831843))
        )
    )

    Column(
        modifier = Modifier
            .testTag("home_feed_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* Brand tap */ }
            ) {
                NexoraLogoBadge(size = 36)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "NEXORA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.5.sp
                        ),
                        color = NexoraTextPrimary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Search Button
                IconButton(
                    onClick = onNavigateSearch,
                    modifier = Modifier
                        .testTag("btn_home_search")
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search videos",
                        tint = NexoraTextPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Notifications Button
                IconButton(
                    onClick = onNavigateNotifications,
                    modifier = Modifier
                        .testTag("btn_home_notifications")
                        .size(40.dp)
                ) {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = NexoraCyanAccent,
                                contentColor = Color.Black
                            ) { Text("2", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = NexoraTextPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            items(filterChips.size) { index ->
                val isSelected = selectedFilterIndex == index
                Surface(
                    onClick = { selectedFilterIndex = index },
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) NexoraCyanAccent else NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) NexoraCyanAccent else NexoraSurfaceBorder
                    ),
                    modifier = Modifier.testTag("filter_chip_$index")
                ) {
                    Text(
                        text = filterChips[index],
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) Color.Black else NexoraTextSecondary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Optional tester toggle pill to switch between empty state and video preview layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = { showEmptyStateDemoToggle = !showEmptyStateDemoToggle },
                shape = RoundedCornerShape(8.dp),
                color = NexoraSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(0.6.dp, NexoraSurfaceBorder),
                modifier = Modifier.testTag("btn_toggle_empty_feed")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (showEmptyStateDemoToggle) "Showing Feed Preview (Tap for Empty State)" else "Feed: Clean Empty State (Tap to preview feed)",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Feed Content: ONLY video content
        if (!showEmptyStateDemoToggle) {
            // Clean Empty State Design as required: “No videos available”
            Box(
                modifier = Modifier
                    .testTag("home_empty_state")
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(NexoraSurfaceDark)
                            .border(1.5.dp, NexoraSurfaceBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(44.dp)) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        NexoraIndigoPrimary.copy(alpha = 0.4f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "No videos available",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = NexoraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "There are no video uploads in the feed right now. New video releases from creators will appear here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        } else {
            // Vertically scrollable feed supporting all video attributes
            LazyColumn(
                modifier = Modifier
                    .testTag("home_video_feed_list")
                    .fillMaxSize()
            ) {
                items(sampleVideos, key = { it.id }) { video ->
                    VideoCard(
                        video = video,
                        onOptionsClick = { selectedVideoForOptions = video }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Video Options Bottom Sheet
    if (selectedVideoForOptions != null) {
        val video = selectedVideoForOptions!!
        ModalBottomSheet(
            onDismissRequest = { selectedVideoForOptions = null },
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
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${video.channelName} • ${video.views}",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted
                )

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = NexoraSurfaceBorder, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(10.dp))

                VideoOptionRow(
                    icon = Icons.Default.BookmarkBorder,
                    label = "Save to Watch Later",
                    onClick = { selectedVideoForOptions = null }
                )
                VideoOptionRow(
                    icon = Icons.Default.Share,
                    label = "Share Video",
                    onClick = { selectedVideoForOptions = null }
                )
                VideoOptionRow(
                    icon = Icons.Default.VisibilityOff,
                    label = "Not Interested",
                    onClick = { selectedVideoForOptions = null }
                )
                VideoOptionRow(
                    icon = Icons.Default.Flag,
                    label = "Report Video",
                    onClick = { selectedVideoForOptions = null }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun VideoCard(
    video: VideoItem,
    onOptionsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Video playback */ }
            .padding(bottom = 18.dp)
    ) {
        // Thumbnail with duration pill
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(Brush.linearGradient(video.gradientColors))
        ) {
            // Stylized backdrop pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val step = size.width / 8
                for (i in 0..8) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.03f),
                        start = Offset(i * step, 0f),
                        end = Offset(i * step + 40f, size.height),
                        strokeWidth = 1f
                    )
                }
            }

            // Center Play Icon Aura
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(30.dp)
                )
            }

            // Duration Pill
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Text(
                    text = video.duration,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }

        // Video Info Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Channel Profile Image / Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(video.channelAvatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = video.channelName.first().toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = NexoraTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${video.channelName} • ${video.views} • ${video.uploadTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexoraTextSecondary
                )
            }

            // More Options Button
            IconButton(
                onClick = onOptionsClick,
                modifier = Modifier
                    .testTag("btn_video_options_${video.id}")
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = NexoraTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun VideoOptionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = NexoraIndigoLight,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = NexoraTextPrimary
        )
    }
}
