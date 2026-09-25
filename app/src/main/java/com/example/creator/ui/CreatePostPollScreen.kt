package com.example.creator.ui

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.runtime.mutableStateListOf
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
import com.example.creator.viewmodel.CreatorViewModel
import com.example.data.model.CreatorContentEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraErrorRed
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import org.json.JSONArray
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostPollScreen(
    initialModeIsPoll: Boolean = false,
    user: UserEntity,
    viewModel: CreatorViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val channel = uiState.channel

    var selectedTab by remember { mutableIntStateOf(if (initialModeIsPoll) 1 else 0) }

    // Post state
    var postText by remember { mutableStateOf("") }
    var selectedImagePreset by remember { mutableStateOf<String?>(null) }
    var postVisibility by remember { mutableStateOf("PUBLIC") }

    // Poll state
    var pollQuestion by remember { mutableStateOf("") }
    val pollOptions = remember { mutableStateListOf("Option 1", "Option 2") }
    var pollDuration by remember { mutableStateOf("3 days") }
    var showPollPreview by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .testTag("create_post_poll_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("btn_back_post_poll")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NexoraTextPrimary
                )
            }

            Text(
                text = if (selectedTab == 0) "Create Post" else "Create Community Poll",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Surface(
                onClick = {
                    if (selectedTab == 0) {
                        if (postText.isNotBlank()) {
                            val post = CreatorContentEntity(
                                id = UUID.randomUUID().toString(),
                                channelId = channel?.channelId ?: user.id,
                                channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
                                channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty(),
                                userId = user.id,
                                contentType = "POST",
                                title = postText.take(50),
                                postText = postText,
                                postImageUri = selectedImagePreset,
                                visibility = postVisibility,
                                status = "DRAFT",
                                uploadProgress = 0
                            )
                            viewModel.saveAsDraft(post) { onNavigateBack() }
                        }
                    } else {
                        if (pollQuestion.isNotBlank()) {
                            val optionsArray = JSONArray(pollOptions).toString()
                            val poll = CreatorContentEntity(
                                id = UUID.randomUUID().toString(),
                                channelId = channel?.channelId ?: user.id,
                                channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
                                channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty(),
                                userId = user.id,
                                contentType = "POLL",
                                title = pollQuestion,
                                pollQuestion = pollQuestion,
                                pollOptionsJson = optionsArray,
                                pollDuration = pollDuration,
                                status = "DRAFT",
                                uploadProgress = 0
                            )
                            viewModel.saveAsDraft(poll) { onNavigateBack() }
                        }
                    }
                },
                shape = RoundedCornerShape(8.dp),
                color = NexoraSurfaceElevated,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = "Save Draft",
                    color = NexoraTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = NexoraDarkBackground,
            contentColor = NexoraTextPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NexoraCyanAccent
                )
            },
            divider = { HorizontalDivider(color = NexoraSurfaceBorder) }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("COMMUNITY POST", fontWeight = FontWeight.Bold, color = if (selectedTab == 0) NexoraCyanAccent else NexoraTextSecondary) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("CREATOR POLL", fontWeight = FontWeight.Bold, color = if (selectedTab == 1) NexoraCyanAccent else NexoraTextSecondary) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (selectedTab == 0) {
                // ================= POST FLOW =================
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val displayChannelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty()
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(NexoraCyanAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayChannelName.firstOrNull()?.toString()?.uppercase() ?: "C",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = displayChannelName.ifEmpty { "Channel" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = "Posting to Channel",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraCyanAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = postText,
                    onValueChange = { postText = it },
                    placeholder = { Text("Share an update, announcement, or thought with your subscribers...") },
                    minLines = 4,
                    modifier = Modifier
                        .testTag("input_post_text")
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary,
                        focusedBorderColor = NexoraCyanAccent,
                        unfocusedBorderColor = NexoraSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Image Attachment
                Text("ATTACH IMAGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("No Image", "Banner 1", "Art Preview", "Thumbnail").forEach { imgOpt ->
                        val isSelected = (imgOpt == "No Image" && selectedImagePreset == null) || (selectedImagePreset == imgOpt)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedImagePreset = if (imgOpt == "No Image") null else imgOpt },
                            label = { Text(imgOpt) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NexoraCyanAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Visibility
                Text("VISIBILITY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PUBLIC", "SUBSCRIBERS_ONLY").forEach { v ->
                        FilterChip(
                            selected = postVisibility == v,
                            onClick = { postVisibility = v },
                            label = { Text(v.replace("_", " ")) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NexoraCyanAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    onClick = {
                        if (postText.isNotBlank()) {
                            val newPost = CreatorContentEntity(
                                id = UUID.randomUUID().toString(),
                                channelId = channel?.channelId ?: user.id,
                                channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
                                channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty(),
                                userId = user.id,
                                contentType = "POST",
                                title = postText.take(50),
                                postText = postText,
                                postImageUri = selectedImagePreset,
                                visibility = postVisibility,
                                status = "PUBLISHED",
                                uploadProgress = 100
                            )
                            viewModel.startUpload(newPost) {
                                onNavigateBack()
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (postText.isNotBlank()) NexoraCyanAccent else NexoraSurfaceElevated,
                    modifier = Modifier
                        .testTag("btn_publish_post")
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "POST",
                            fontWeight = FontWeight.Bold,
                            color = if (postText.isNotBlank()) Color.Black else NexoraTextMuted
                        )
                    }
                }
            } else {
                // ================= POLL FLOW =================
                Text("POLL QUESTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = pollQuestion,
                    onValueChange = { pollQuestion = it },
                    placeholder = { Text("Ask your community a question...") },
                    singleLine = true,
                    modifier = Modifier
                        .testTag("input_poll_question")
                        .fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary,
                        focusedBorderColor = NexoraCyanAccent,
                        unfocusedBorderColor = NexoraSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("POLL OPTIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))

                pollOptions.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = option,
                            onValueChange = { pollOptions[index] = it },
                            placeholder = { Text("Option ${index + 1}") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = NexoraTextPrimary,
                                unfocusedTextColor = NexoraTextPrimary,
                                focusedBorderColor = NexoraCyanAccent,
                                unfocusedBorderColor = NexoraSurfaceBorder
                            )
                        )
                        if (pollOptions.size > 2) {
                            IconButton(onClick = { pollOptions.removeAt(index) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove Option", tint = NexoraErrorRed, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                if (pollOptions.size < 5) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        onClick = { pollOptions.add("Option ${pollOptions.size + 1}") },
                        shape = RoundedCornerShape(8.dp),
                        color = NexoraSurfaceElevated,
                        modifier = Modifier.testTag("btn_add_poll_option")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add Option", color = NexoraCyanAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Poll Duration
                Text("POLL DURATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("24 hours", "3 days", "7 days", "Never").forEach { dur ->
                        FilterChip(
                            selected = pollDuration == dur,
                            onClick = { pollDuration = dur },
                            label = { Text(dur) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NexoraCyanAccent,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Preview Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("PREVIEW", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraIndigoLight)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = pollQuestion.ifBlank { "Your question preview appears here" },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        pollOptions.forEach { opt ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NexoraSurfaceElevated,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(
                                    text = opt.ifBlank { "Option" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = NexoraTextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Surface(
                    onClick = {
                        if (pollQuestion.isNotBlank() && pollOptions.all { it.isNotBlank() }) {
                            val optionsArray = JSONArray(pollOptions).toString()
                            val votesArray = JSONArray(List(pollOptions.size) { 0 }).toString()
                            val pollContent = CreatorContentEntity(
                                id = UUID.randomUUID().toString(),
                                channelId = channel?.channelId ?: user.id,
                                channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
                                channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty(),
                                userId = user.id,
                                contentType = "POLL",
                                title = pollQuestion,
                                pollQuestion = pollQuestion,
                                pollOptionsJson = optionsArray,
                                pollVotesJson = votesArray,
                                pollDuration = pollDuration,
                                status = "PUBLISHED",
                                uploadProgress = 100
                            )
                            viewModel.startUpload(pollContent) {
                                onNavigateBack()
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (pollQuestion.isNotBlank()) NexoraCyanAccent else NexoraSurfaceElevated,
                    modifier = Modifier
                        .testTag("btn_publish_poll")
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "PUBLISH POLL",
                            fontWeight = FontWeight.Bold,
                            color = if (pollQuestion.isNotBlank()) Color.Black else NexoraTextMuted
                        )
                    }
                }
            }
        }
    }
}
