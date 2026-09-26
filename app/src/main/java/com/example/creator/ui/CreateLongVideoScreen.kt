package com.example.creator.ui

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.creator.model.UploadStatusStep
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
import com.example.upload.model.MediaItem
import com.example.upload.model.UploadStatus
import com.example.upload.model.UploadTargetType
import com.example.upload.model.UploadTask
import com.example.upload.service.UploadManager
import com.example.upload.ui.CentralizedUploadHost
import com.example.upload.ui.rememberCentralizedUploadController
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLongVideoScreen(
    user: UserEntity,
    viewModel: CreatorViewModel,
    initialDraft: CreatorContentEntity? = null,
    initialMediaItem: MediaItem? = null,
    onNavigateBack: () -> Unit,
    onPreviewVideo: (CreatorContentEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val channel = uiState.channel

    val context = LocalContext.current
    val uploadManager = remember { UploadManager.getInstance(context) }
    val uploadController = rememberCentralizedUploadController()
    val allTasks by uploadManager.allTasks.collectAsState()

    var selectedMediaItem by remember { mutableStateOf<MediaItem?>(initialMediaItem) }
    var customThumbnailUri by remember { mutableStateOf<String?>(null) }
    var activeTaskId by remember { mutableStateOf<String?>(null) }

    val currentUploadTask = remember(allTasks, activeTaskId) {
        allTasks.firstOrNull { it.id == activeTaskId }
    }

    var title by remember { mutableStateOf(initialDraft?.title ?: "") }
    var description by remember { mutableStateOf(initialDraft?.description ?: "") }
    var hashtags by remember { mutableStateOf(initialDraft?.hashtags ?: "#nexora #gaming #video") }
    var visibility by remember { mutableStateOf(initialDraft?.visibility ?: (channel?.defaultVisibility ?: "PUBLIC")) }
    var isMadeForKids by remember { mutableStateOf(initialDraft?.isMadeForKids ?: (channel?.defaultAudience == "MADE_FOR_KIDS")) }
    var category by remember { mutableStateOf(initialDraft?.category ?: (channel?.defaultCategory ?: "Gaming")) }
    var language by remember { mutableStateOf(initialDraft?.language ?: (channel?.defaultLanguage ?: "English")) }
    var location by remember { mutableStateOf(initialDraft?.location ?: "") }
    var playlist by remember { mutableStateOf(initialDraft?.playlist ?: "Default Playlist") }
    var allowComments by remember { mutableStateOf(initialDraft?.allowComments ?: (channel?.defaultComments != "DISABLE")) }
    var isAiGenerated by remember { mutableStateOf(initialDraft?.isAiGenerated ?: false) }

    // Advanced placeholders
    var subtitlesLanguage by remember { mutableStateOf("None") }
    var chaptersText by remember { mutableStateOf("") }
    var scheduleOption by remember { mutableStateOf("Publish Now") }

    var selectedThumbnailIndex by remember { mutableIntStateOf(0) }
    var policyAgreed by remember { mutableStateOf(false) }

    var isUploading by remember { mutableStateOf(false) }
    var showDraftDialog by remember { mutableStateOf(false) }

    val contentEntity = remember(
        channel, title, description, hashtags, visibility, isMadeForKids,
        category, language, location, playlist, allowComments, isAiGenerated,
        subtitlesLanguage, chaptersText, scheduleOption, selectedThumbnailIndex
    ) {
        CreatorContentEntity(
            id = initialDraft?.id ?: UUID.randomUUID().toString(),
            channelId = channel?.channelId ?: user.id,
            channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
            channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty(),
            userId = user.id,
            contentType = "VIDEO",
            title = title.ifBlank { "Untitled Video" },
            description = description,
            hashtags = hashtags,
            videoPath = "landscape_video_source.mp4",
            thumbnailUri = "thumb_landscape_$selectedThumbnailIndex.jpg",
            visibility = visibility,
            isMadeForKids = isMadeForKids,
            location = location.ifBlank { null },
            allowComments = allowComments,
            isAiGenerated = isAiGenerated,
            language = language,
            category = category,
            playlist = playlist.ifBlank { null },
            subtitlesInfo = if (subtitlesLanguage != "None") subtitlesLanguage else null,
            chaptersInfo = chaptersText.ifBlank { null },
            status = "DRAFT",
            durationSeconds = 180
        )
    }

    Column(
        modifier = Modifier
            .testTag("create_long_video_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    if (isUploading) {
                        if (uiState.uploadStep == UploadStatusStep.COMPLETED) {
                            viewModel.resetUploadState()
                            onNavigateBack()
                        } else {
                            showDraftDialog = true
                        }
                    } else {
                        showDraftDialog = true
                    }
                },
                modifier = Modifier.testTag("btn_back_long_video")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NexoraTextPrimary
                )
            }

            Text(
                text = "Upload Video",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Surface(
                onClick = {
                    viewModel.saveAsDraft(contentEntity) {
                        onNavigateBack()
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

        HorizontalDivider(color = NexoraSurfaceBorder)

        if (isUploading) {
            // Upload progress view
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                val isCompleted = (currentUploadTask?.status == UploadStatus.COMPLETED) || (uiState.uploadStep == UploadStatusStep.COMPLETED)
                val isFailed = (currentUploadTask?.status == UploadStatus.FAILED) || (uiState.uploadStep == UploadStatusStep.FAILED)
                val progress = currentUploadTask?.progressPercent ?: uiState.uploadProgressPercent
                val statusText = if (currentUploadTask != null) {
                    when (currentUploadTask.status) {
                        UploadStatus.QUEUED -> "Queued for upload..."
                        UploadStatus.UPLOADING -> "Uploading (${currentUploadTask.uploadSpeed})..."
                        UploadStatus.PROCESSING -> "Processing video on nexora..."
                        UploadStatus.COMPLETED -> "Published!"
                        UploadStatus.FAILED, UploadStatus.CANCELLED -> "Upload failed"
                    }
                } else uiState.uploadStatusMessage

                if (isCompleted) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(NexoraSuccessGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = NexoraSuccessGreen, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Your Video has been published.", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NexoraTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        val chName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty()
                        if (chName.isNotBlank()) {
                            Text("Published to $chName", color = NexoraTextSecondary, fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                onClick = { uiState.activeUploadingContent?.let { onPreviewVideo(it) } },
                                shape = RoundedCornerShape(12.dp),
                                color = NexoraCyanAccent,
                                modifier = Modifier.testTag("btn_view_long_video_success").height(46.dp).weight(1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("VIEW VIDEO", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                            Surface(
                                onClick = {
                                    viewModel.resetUploadState()
                                    onNavigateBack()
                                },
                                shape = RoundedCornerShape(12.dp),
                                color = NexoraSurfaceDark,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                                modifier = Modifier.testTag("btn_done_long_video_success").height(46.dp).weight(1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("DONE", color = NexoraTextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else if (isFailed) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = NexoraErrorRed, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("Upload failed.", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = NexoraTextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(currentUploadTask?.errorMessage ?: "Connection error.", color = NexoraTextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(20.dp))
                        Surface(
                            onClick = {
                                activeTaskId?.let { uploadManager.retryUpload(it) }
                                viewModel.retryFailedUpload()
                            },
                            shape = RoundedCornerShape(10.dp),
                            color = NexoraCyanAccent,
                            modifier = Modifier.fillMaxWidth(0.7f).height(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) { Text("RETRY", color = Color.Black, fontWeight = FontWeight.Bold) }
                        }
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = NexoraCyanAccent, strokeWidth = 4.dp, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(statusText, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = NexoraTextPrimary)
                        if (currentUploadTask != null && currentUploadTask.uploadSpeed.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(currentUploadTask.uploadSpeed, color = NexoraCyanAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth(0.8f).height(8.dp).clip(RoundedCornerShape(4.dp)),
                            color = NexoraCyanAccent,
                            trackColor = NexoraSurfaceElevated
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("$progress%", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(24.dp))
                        OutlinedButton(
                            onClick = { uploadController.openUploadManager() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraTextPrimary)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Upload Manager", fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            // Details & settings form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // 16:9 Video Preview Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF312E81))
                            )
                        )
                        .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            uploadController.requestMedia(UploadTargetType.LONG_VIDEO) { item ->
                                selectedMediaItem = item
                                if (title.isBlank()) {
                                    title = item.fileName.substringBeforeLast('.')
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedMediaItem != null) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.VideoFile, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = selectedMediaItem!!.fileName,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${selectedMediaItem!!.formattedSize} • ${if (selectedMediaItem!!.formattedDuration.isNotBlank()) selectedMediaItem!!.formattedDuration else "Standard Duration"}${if (selectedMediaItem!!.formattedResolution.isNotBlank()) " • " + selectedMediaItem!!.formattedResolution else ""}",
                                color = NexoraCyanAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Text("Tap to change video", color = NexoraTextSecondary, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Icon(Icons.Default.Upload, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select Video to Upload", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Record Video, Choose Video, or Pick File", color = NexoraTextSecondary, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text("VIDEO TITLE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("Add a title that describes your video") },
                    singleLine = true,
                    modifier = Modifier.testTag("input_long_video_title").fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary,
                        focusedBorderColor = NexoraCyanAccent,
                        unfocusedBorderColor = NexoraSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Description
                Text("DESCRIPTION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Tell viewers about your video...") },
                    minLines = 3,
                    modifier = Modifier.testTag("input_long_video_description").fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary,
                        focusedBorderColor = NexoraCyanAccent,
                        unfocusedBorderColor = NexoraSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Hashtags
                Text("HASHTAGS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = hashtags,
                    onValueChange = { hashtags = it },
                    placeholder = { Text("#gaming #tutorial #nexora") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary,
                        focusedBorderColor = NexoraCyanAccent,
                        unfocusedBorderColor = NexoraSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Thumbnail Selection
                Text("THUMBNAIL", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (0..2).forEach { idx ->
                        Surface(
                            onClick = {
                                selectedThumbnailIndex = idx
                                customThumbnailUri = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (customThumbnailUri == null && selectedThumbnailIndex == idx) NexoraCyanAccent.copy(alpha = 0.2f) else NexoraSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (customThumbnailUri == null && selectedThumbnailIndex == idx) NexoraCyanAccent else NexoraSurfaceBorder),
                            modifier = Modifier.weight(1f).height(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("Frame ${idx + 1}", fontSize = 12.sp, color = NexoraTextPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Custom Thumbnail option with Centralized Upload
                    Surface(
                        onClick = {
                            uploadController.requestMedia(UploadTargetType.THUMBNAIL) { item ->
                                customThumbnailUri = item.uri.toString()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        color = if (customThumbnailUri != null) NexoraCyanAccent.copy(alpha = 0.25f) else NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (customThumbnailUri != null) NexoraCyanAccent else NexoraSurfaceBorder),
                        modifier = Modifier.weight(1.3f).height(44.dp).testTag("btn_custom_thumbnail")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (customThumbnailUri != null) "✓ Custom" else "+ Custom",
                                fontSize = 11.sp,
                                color = if (customThumbnailUri != null) NexoraCyanAccent else NexoraTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Visibility
                Text("VISIBILITY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("PUBLIC", "UNLISTED", "PRIVATE").forEach { v ->
                        FilterChip(
                            selected = visibility == v,
                            onClick = { visibility = v },
                            label = { Text(v) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Audience
                Text("AUDIENCE: MADE FOR KIDS?", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = !isMadeForKids,
                        onClick = { isMadeForKids = false },
                        label = { Text("No, not made for kids") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = isMadeForKids,
                        onClick = { isMadeForKids = true },
                        label = { Text("Yes, made for kids") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Category & Language
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("CATEGORY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = category,
                            onValueChange = { category = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("LANGUAGE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = language,
                            onValueChange = { language = it },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Playlist & Location
                OutlinedTextField(
                    value = playlist,
                    onValueChange = { playlist = it },
                    label = { Text("Add to Playlist") },
                    placeholder = { Text("e.g. Full Playthrough") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location (optional)") },
                    placeholder = { Text("e.g. Los Angeles, CA") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Prepared Advanced Features (Subtitles, Chapters, Schedule)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("ADVANCED CREATOR TOOLS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraIndigoLight)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtitles
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Subtitles / CC", style = MaterialTheme.typography.bodyMedium, color = NexoraTextPrimary)
                            Text("Auto English", style = MaterialTheme.typography.labelSmall, color = NexoraCyanAccent)
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        // Chapters
                        OutlinedTextField(
                            value = chaptersText,
                            onValueChange = { chaptersText = it },
                            label = { Text("Timestamps / Chapters") },
                            placeholder = { Text("00:00 Intro, 01:20 Boss Fight") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = NexoraTextPrimary, unfocusedTextColor = NexoraTextPrimary)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AI Disclosure
                Text("AI CONTENT DISCLOSURE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Does this content contain AI-generated or significantly AI-modified content?", style = MaterialTheme.typography.bodySmall, color = NexoraTextSecondary)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = !isAiGenerated,
                        onClick = { isAiGenerated = false },
                        label = { Text("No") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
                    )
                    FilterChip(
                        selected = isAiGenerated,
                        onClick = { isAiGenerated = true },
                        label = { Text("Yes (AI Modified)") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Policy Agreement Box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "By uploading this content, you confirm that you have the necessary rights to upload it and agree to follow nexora's Privacy Policy, Community Guidelines, and Content Policies.",
                            fontSize = 12.sp,
                            color = NexoraTextSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.clickable { policyAgreed = !policyAgreed },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = policyAgreed,
                                onCheckedChange = { policyAgreed = it },
                                colors = CheckboxDefaults.colors(checkedColor = NexoraCyanAccent, checkmarkColor = Color.Black)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("I agree to the above.", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold), color = NexoraTextPrimary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Upload Button
                Surface(
                    onClick = {
                        if (policyAgreed) {
                            val contentId = UUID.randomUUID().toString()
                            val uploadContent = contentEntity.copy(
                                id = contentId,
                                videoPath = selectedMediaItem?.uri?.toString() ?: "video_upload.mp4",
                                thumbnailUri = customThumbnailUri ?: "thumb_landscape_$selectedThumbnailIndex.jpg",
                                status = "UPLOADING",
                                uploadProgress = 0,
                                durationSeconds = (selectedMediaItem?.durationMs?.div(1000))?.toInt() ?: 180
                            )
                            viewModel.startUpload(uploadContent)

                            val taskId = UUID.randomUUID().toString()
                            val task = UploadTask(
                                id = taskId,
                                targetType = UploadTargetType.LONG_VIDEO,
                                title = title.ifBlank { "Untitled Video" },
                                mediaUri = selectedMediaItem?.uri?.toString() ?: "",
                                thumbnailUri = customThumbnailUri,
                                mimeType = selectedMediaItem?.mimeType ?: "video/mp4",
                                fileSizeBytes = selectedMediaItem?.fileSizeBytes ?: 1024 * 1024 * 10L,
                                associatedContentId = contentId,
                                channelId = channel?.channelId ?: user.id,
                                channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
                                description = description,
                                visibility = visibility,
                                audience = if (isMadeForKids) "MADE_FOR_KIDS" else "NOT_FOR_KIDS",
                                category = category,
                                tags = hashtags
                            )
                            activeTaskId = taskId
                            uploadManager.enqueueUpload(task)
                            isUploading = true
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = if (policyAgreed) NexoraCyanAccent else NexoraSurfaceElevated,
                    modifier = Modifier
                        .testTag("btn_publish_long_video")
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("AGREE & UPLOAD", fontWeight = FontWeight.Bold, color = if (policyAgreed) Color.Black else NexoraTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showDraftDialog) {
        AlertDialog(
            onDismissRequest = { showDraftDialog = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Save Video as Draft?", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Your video settings and metadata will be saved as a draft.", color = NexoraTextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showDraftDialog = false
                    viewModel.saveAsDraft(contentEntity) { onNavigateBack() }
                }) {
                    Text("SAVE DRAFT", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDraftDialog = false
                    viewModel.resetUploadState()
                    onNavigateBack()
                }) {
                    Text("DISCARD", color = NexoraErrorRed)
                }
            }
        )
    }

    // Centralized Upload Host for Video & Custom Thumbnail
    CentralizedUploadHost(controller = uploadController)
}
