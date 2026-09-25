package com.example.creator.ui

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creator.model.PRESET_AUDIO_TRACKS
import com.example.creator.model.PRESET_FILTERS
import com.example.creator.model.PRESET_STICKERS
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
import com.example.ui.theme.NexoraVioletAccent
import kotlinx.coroutines.delay
import java.util.UUID

enum class ShortCreationStep {
    SOURCE_SELECTION,
    EDITOR,
    DETAILS,
    THUMBNAIL,
    POLICY,
    UPLOAD_PROGRESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShortScreen(
    user: UserEntity,
    viewModel: CreatorViewModel,
    initialDraft: CreatorContentEntity? = null,
    onNavigateBack: () -> Unit,
    onPreviewShort: (CreatorContentEntity) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val channel = uiState.channel

    var currentStep by remember {
        mutableStateOf(if (initialDraft != null) ShortCreationStep.DETAILS else ShortCreationStep.SOURCE_SELECTION)
    }

    // Source state
    var selectedVideoClip by remember {
        mutableStateOf(initialDraft?.videoPath ?: "clip_cyber_gameplay.mp4")
    }
    var isRecordingSimulated by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }

    // Editor adjustments
    var isPlaying by remember { mutableStateOf(true) }
    var trimStart by remember { mutableFloatStateOf(initialDraft?.trimStartPercent ?: 0f) }
    var trimEnd by remember { mutableFloatStateOf(initialDraft?.trimEndPercent ?: 1f) }
    var rotationDegrees by remember { mutableIntStateOf(initialDraft?.rotationDegrees ?: 0) }
    var speed by remember { mutableFloatStateOf(initialDraft?.speed ?: 1.0f) }
    var volume by remember { mutableFloatStateOf(initialDraft?.volume ?: 1.0f) }
    var isMuted by remember { mutableStateOf(initialDraft?.isMuted ?: false) }
    var selectedFilter by remember { mutableStateOf(initialDraft?.filterApplied ?: "Normal") }
    var selectedAudioTrack by remember { mutableStateOf(initialDraft?.audioTrack ?: "None") }
    var voiceOverAdded by remember { mutableStateOf(false) }
    var textOverlay by remember { mutableStateOf(initialDraft?.textOverlay ?: "") }
    var selectedSticker by remember { mutableStateOf(initialDraft?.sticker ?: "") }
    var captionsEnabled by remember { mutableStateOf(initialDraft?.captionsEnabled ?: false) }

    // Details state
    var title by remember { mutableStateOf(initialDraft?.title ?: "") }
    var description by remember { mutableStateOf(initialDraft?.description ?: "") }
    var hashtags by remember { mutableStateOf(initialDraft?.hashtags ?: "#shorts #gaming #nexora") }
    var isMadeForKids by remember { mutableStateOf(initialDraft?.isMadeForKids ?: false) }
    var visibility by remember { mutableStateOf(initialDraft?.visibility ?: "PUBLIC") }
    var location by remember { mutableStateOf(initialDraft?.location ?: "") }
    var allowComments by remember { mutableStateOf(initialDraft?.allowComments ?: true) }
    var isAiGenerated by remember { mutableStateOf(initialDraft?.isAiGenerated ?: false) }

    // Thumbnail state
    var selectedThumbnailFrameIndex by remember { mutableIntStateOf(0) }
    var customThumbnailLabel by remember { mutableStateOf("Default Frame 1") }

    // Policy state
    var policyAgreed by remember { mutableStateOf(false) }

    // Exit Draft Prompt
    var showDraftDialog by remember { mutableStateOf(false) }

    // Recording timer loop
    LaunchedEffect(isRecordingSimulated) {
        if (isRecordingSimulated) {
            recordingSeconds = 0
            while (isRecordingSimulated && recordingSeconds < 60) {
                delay(1000)
                recordingSeconds++
            }
            if (isRecordingSimulated) {
                isRecordingSimulated = false
                currentStep = ShortCreationStep.EDITOR
            }
        }
    }

    val contentEntity = remember(
        channel, title, description, hashtags, visibility, isMadeForKids,
        location, allowComments, isAiGenerated, selectedFilter, speed,
        selectedAudioTrack, textOverlay, selectedSticker, isMuted, volume,
        captionsEnabled, rotationDegrees, trimStart, trimEnd
    ) {
        CreatorContentEntity(
            id = initialDraft?.id ?: UUID.randomUUID().toString(),
            channelId = channel?.channelId ?: user.id,
            channelName = channel?.name?.takeIf { it.isNotBlank() } ?: user.channelName.orEmpty(),
            channelHandle = channel?.handle?.takeIf { it.isNotBlank() } ?: user.channelHandle.orEmpty(),
            userId = user.id,
            contentType = "SHORT",
            title = title.ifBlank { "Untitled Short" },
            description = description,
            hashtags = hashtags,
            videoPath = selectedVideoClip,
            thumbnailUri = "thumb_$selectedThumbnailFrameIndex.jpg",
            visibility = visibility,
            isMadeForKids = isMadeForKids,
            location = location.ifBlank { null },
            allowComments = allowComments,
            isAiGenerated = isAiGenerated,
            status = "DRAFT",
            durationSeconds = 15,
            filterApplied = selectedFilter,
            speed = speed,
            audioTrack = selectedAudioTrack.takeIf { it != "None" },
            textOverlay = textOverlay.ifBlank { null },
            sticker = selectedSticker.ifBlank { null },
            isMuted = isMuted,
            volume = volume,
            captionsEnabled = captionsEnabled,
            rotationDegrees = rotationDegrees,
            trimStartPercent = trimStart,
            trimEndPercent = trimEnd
        )
    }

    fun handleBackPress() {
        if (currentStep == ShortCreationStep.SOURCE_SELECTION) {
            onNavigateBack()
        } else if (currentStep == ShortCreationStep.UPLOAD_PROGRESS) {
            if (uiState.uploadStep == UploadStatusStep.COMPLETED) {
                viewModel.resetUploadState()
                onNavigateBack()
            } else {
                showDraftDialog = true
            }
        } else {
            showDraftDialog = true
        }
    }

    Column(
        modifier = Modifier
            .testTag("create_short_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { handleBackPress() },
                modifier = Modifier.testTag("btn_back_short_creation")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NexoraTextPrimary
                )
            }

            Text(
                text = when (currentStep) {
                    ShortCreationStep.SOURCE_SELECTION -> "Create Short"
                    ShortCreationStep.EDITOR -> "Edit Short"
                    ShortCreationStep.DETAILS -> "Short Details"
                    ShortCreationStep.THUMBNAIL -> "Select Thumbnail"
                    ShortCreationStep.POLICY -> "Policy Agreement"
                    ShortCreationStep.UPLOAD_PROGRESS -> "Uploading Short"
                },
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            // Step Indicator Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NexoraSurfaceElevated,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(
                    text = "${currentStep.ordinal + 1}/6",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NexoraCyanAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        HorizontalDivider(color = NexoraSurfaceBorder)

        // Step Contents
        when (currentStep) {
            ShortCreationStep.SOURCE_SELECTION -> {
                SourceSelectionStep(
                    isRecording = isRecordingSimulated,
                    recordingSeconds = recordingSeconds,
                    onStartRecord = { isRecordingSimulated = true },
                    onStopRecord = {
                        isRecordingSimulated = false
                        currentStep = ShortCreationStep.EDITOR
                    },
                    onSelectFromDevice = {
                        selectedVideoClip = "clip_selected_from_device.mp4"
                        currentStep = ShortCreationStep.EDITOR
                    }
                )
            }

            ShortCreationStep.EDITOR -> {
                ShortEditorStep(
                    isPlaying = isPlaying,
                    onTogglePlay = { isPlaying = !isPlaying },
                    trimStart = trimStart,
                    trimEnd = trimEnd,
                    onTrimChange = { s, e -> trimStart = s; trimEnd = e },
                    rotationDegrees = rotationDegrees,
                    onRotate = { rotationDegrees = (rotationDegrees + 90) % 360 },
                    speed = speed,
                    onSpeedChange = { speed = it },
                    volume = volume,
                    onVolumeChange = { volume = it },
                    isMuted = isMuted,
                    onMuteToggle = { isMuted = !isMuted },
                    selectedFilter = selectedFilter,
                    onFilterChange = { selectedFilter = it },
                    selectedAudioTrack = selectedAudioTrack,
                    onAudioTrackChange = { selectedAudioTrack = it },
                    voiceOverAdded = voiceOverAdded,
                    onToggleVoiceOver = { voiceOverAdded = !voiceOverAdded },
                    textOverlay = textOverlay,
                    onTextOverlayChange = { textOverlay = it },
                    selectedSticker = selectedSticker,
                    onStickerChange = { selectedSticker = it },
                    captionsEnabled = captionsEnabled,
                    onCaptionsToggle = { captionsEnabled = !captionsEnabled },
                    onNext = { currentStep = ShortCreationStep.DETAILS }
                )
            }

            ShortCreationStep.DETAILS -> {
                ShortDetailsStep(
                    title = title,
                    onTitleChange = { title = it },
                    description = description,
                    onDescriptionChange = { description = it },
                    hashtags = hashtags,
                    onHashtagsChange = { hashtags = it },
                    isMadeForKids = isMadeForKids,
                    onKidsChange = { isMadeForKids = it },
                    visibility = visibility,
                    onVisibilityChange = { visibility = it },
                    location = location,
                    onLocationChange = { location = it },
                    allowComments = allowComments,
                    onAllowCommentsChange = { allowComments = it },
                    isAiGenerated = isAiGenerated,
                    onAiGeneratedChange = { isAiGenerated = it },
                    onNext = { currentStep = ShortCreationStep.THUMBNAIL }
                )
            }

            ShortCreationStep.THUMBNAIL -> {
                ThumbnailSelectionStep(
                    selectedFrame = selectedThumbnailFrameIndex,
                    customLabel = customThumbnailLabel,
                    onSelectFrame = { selectedThumbnailFrameIndex = it },
                    onUploadCustom = {
                        customThumbnailLabel = "Uploaded Image #${selectedThumbnailFrameIndex + 1}"
                    },
                    onNext = { currentStep = ShortCreationStep.POLICY }
                )
            }

            ShortCreationStep.POLICY -> {
                PolicyConfirmationStep(
                    policyAgreed = policyAgreed,
                    onPolicyAgreedChange = { policyAgreed = it },
                    onBack = { currentStep = ShortCreationStep.THUMBNAIL },
                    onAgreeAndUpload = {
                        currentStep = ShortCreationStep.UPLOAD_PROGRESS
                        viewModel.startUpload(contentEntity)
                    }
                )
            }

            ShortCreationStep.UPLOAD_PROGRESS -> {
                UploadProgressStep(
                    uiState = uiState,
                    onRetry = { viewModel.retryFailedUpload() },
                    onSaveDraft = {
                        viewModel.saveAsDraft(contentEntity) {
                            onNavigateBack()
                        }
                    },
                    onCancel = {
                        viewModel.resetUploadState()
                        onNavigateBack()
                    },
                    onViewShort = {
                        uiState.activeUploadingContent?.let { onPreviewShort(it) }
                    },
                    onDone = {
                        viewModel.resetUploadState()
                        onNavigateBack()
                    }
                )
            }
        }
    }

    // Exit Draft Prompt Dialog
    if (showDraftDialog) {
        AlertDialog(
            onDismissRequest = { showDraftDialog = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Save as Draft?", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "You can save this Short as a draft and continue editing or publishing it later.",
                    color = NexoraTextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDraftDialog = false
                        viewModel.saveAsDraft(contentEntity) {
                            onNavigateBack()
                        }
                    }
                ) {
                    Text("SAVE DRAFT", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDraftDialog = false
                        viewModel.resetUploadState()
                        onNavigateBack()
                    }
                ) {
                    Text("DISCARD", color = NexoraErrorRed)
                }
            }
        )
    }
}

// ----------------------------------------------------
// STEP 1: SOURCE SELECTION
// ----------------------------------------------------
@Composable
private fun SourceSelectionStep(
    isRecording: Boolean,
    recordingSeconds: Int,
    onStartRecord: () -> Unit,
    onStopRecord: () -> Unit,
    onSelectFromDevice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (isRecording) {
            // Camera viewfinder simulation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF090D16))
                    .border(2.dp, NexoraCyanAccent, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Animated recording pulse
                Canvas(modifier = Modifier.size(100.dp)) {
                    drawCircle(color = NexoraErrorRed.copy(alpha = 0.2f), radius = size.minDimension / 2)
                    drawCircle(color = NexoraErrorRed, radius = size.minDimension / 4)
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(NexoraErrorRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "REC 00:${recordingSeconds.toString().padStart(2, '0')} / 00:60",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                onClick = onStopRecord,
                shape = CircleShape,
                color = NexoraErrorRed,
                modifier = Modifier
                    .testTag("btn_stop_recording_short")
                    .size(68.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(24.dp).background(Color.White, RoundedCornerShape(4.dp)))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Tap to stop recording", color = NexoraTextSecondary, fontSize = 12.sp)
        } else {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Add Your Short",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Capture vertical video in-app or choose an existing clip from your device.",
                style = MaterialTheme.typography.bodyMedium,
                color = NexoraTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Option 1: SHOOT / RECORD
            Surface(
                onClick = onStartRecord,
                shape = RoundedCornerShape(14.dp),
                color = NexoraCyanAccent,
                modifier = Modifier
                    .testTag("btn_shoot_record_short")
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Camera, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "SHOOT / RECORD",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Option 2: UPLOAD FROM DEVICE
            Surface(
                onClick = onSelectFromDevice,
                shape = RoundedCornerShape(14.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier
                    .testTag("btn_upload_from_device_short")
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Upload, contentDescription = null, tint = NexoraTextPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "UPLOAD FROM DEVICE",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// STEP 2: VIDEO EDITOR
// ----------------------------------------------------
@Composable
private fun ShortEditorStep(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    trimStart: Float,
    trimEnd: Float,
    onTrimChange: (Float, Float) -> Unit,
    rotationDegrees: Int,
    onRotate: () -> Unit,
    speed: Float,
    onSpeedChange: (Float) -> Unit,
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    isMuted: Boolean,
    onMuteToggle: () -> Unit,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    selectedAudioTrack: String,
    onAudioTrackChange: (String) -> Unit,
    voiceOverAdded: Boolean,
    onToggleVoiceOver: () -> Unit,
    textOverlay: String,
    onTextOverlayChange: (String) -> Unit,
    selectedSticker: String,
    onStickerChange: (String) -> Unit,
    captionsEnabled: Boolean,
    onCaptionsToggle: () -> Unit,
    onNext: () -> Unit
) {
    var showMusicDialog by remember { mutableStateOf(false) }
    var showTextDialog by remember { mutableStateOf(false) }
    var showStickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 9:16 Video Canvas Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    when (selectedFilter) {
                        "Noir" -> Brush.verticalGradient(listOf(Color(0xFF27272A), Color(0xFF09090B)))
                        "Cyber" -> Brush.verticalGradient(listOf(Color(0xFF0369A1), Color(0xFF3B0764)))
                        "Warm" -> Brush.verticalGradient(listOf(Color(0xFFB45309), Color(0xFF78350F)))
                        "Cool" -> Brush.verticalGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F172A)))
                        "Vintage" -> Brush.verticalGradient(listOf(Color(0xFF713F12), Color(0xFF1C1917)))
                        "Vibrant" -> Brush.verticalGradient(listOf(Color(0xFF0284C7), Color(0xFF0D9488)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1B4B)))
                    }
                )
                .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(16.dp))
                .rotate(rotationDegrees.toFloat()),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onTogglePlay, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Text(
                    text = if (isPlaying) "Playing (${speed}x)" else "Paused",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 11.sp
                )
            }

            // Stickers & Text Overlays on Video
            if (selectedSticker.isNotBlank()) {
                Text(
                    text = selectedSticker,
                    fontSize = 32.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                )
            }
            if (textOverlay.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 36.dp)
                ) {
                    Text(
                        text = textOverlay,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            if (captionsEnabled) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                ) {
                    Text(
                        text = "CC: [Auto captions generated]",
                        color = NexoraCyanAccent,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Audio track badge
            if (selectedAudioTrack != "None") {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    modifier = Modifier.align(Alignment.TopStart).padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.MusicNote, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(selectedAudioTrack, color = Color.White, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tool Row: Trim & Cut
        Text("TRIM & CUT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Start: ${(trimStart * 100).toInt()}%", fontSize = 11.sp, color = NexoraTextSecondary)
            Text("End: ${(trimEnd * 100).toInt()}%", fontSize = 11.sp, color = NexoraTextSecondary)
        }
        Slider(
            value = trimEnd,
            onValueChange = { onTrimChange(trimStart, it) },
            valueRange = 0.2f..1f,
            colors = SliderDefaults.colors(thumbColor = NexoraCyanAccent, activeTrackColor = NexoraCyanAccent)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Editor Actions (Rotate, Crop, Mute, Captions)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EditorToolButton(
                icon = Icons.Default.RotateRight,
                label = "${rotationDegrees}°",
                onClick = onRotate,
                modifier = Modifier.weight(1f)
            )
            EditorToolButton(
                icon = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                label = if (isMuted) "Muted" else "Volume",
                onClick = onMuteToggle,
                isActive = !isMuted,
                modifier = Modifier.weight(1f)
            )
            EditorToolButton(
                icon = Icons.Default.Subtitles,
                label = if (captionsEnabled) "CC On" else "Captions",
                onClick = onCaptionsToggle,
                isActive = captionsEnabled,
                modifier = Modifier.weight(1f)
            )
            EditorToolButton(
                icon = Icons.Default.Mic,
                label = if (voiceOverAdded) "Voice +" else "Voice-over",
                onClick = onToggleVoiceOver,
                isActive = voiceOverAdded,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Speed Selector
        Text("PLAYBACK SPEED", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { s ->
                FilterChip(
                    selected = speed == s,
                    onClick = { onSpeedChange(s) },
                    label = { Text("${s}x") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NexoraCyanAccent,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Music, Text, Stickers Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                onClick = { showMusicDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Music", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                }
            }

            Surface(
                onClick = { showTextDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.TextFields, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Text", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                }
            }

            Surface(
                onClick = { showStickerDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.weight(1f).height(44.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("🔥", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Sticker", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Basic Filters
        Text("FILTERS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PRESET_FILTERS.take(5).forEach { f ->
                FilterChip(
                    selected = selectedFilter == f,
                    onClick = { onFilterChange(f) },
                    label = { Text(f, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NexoraCyanAccent,
                        selectedLabelColor = Color.Black
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Button: NEXT
        Surface(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            color = NexoraCyanAccent,
            modifier = Modifier
                .testTag("btn_editor_next")
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("NEXT", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }

    // Music Picker Dialog
    if (showMusicDialog) {
        AlertDialog(
            onDismissRequest = { showMusicDialog = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Choose Audio Track", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PRESET_AUDIO_TRACKS.forEach { track ->
                        Surface(
                            onClick = {
                                onAudioTrackChange(track)
                                showMusicDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedAudioTrack == track) NexoraCyanAccent.copy(alpha = 0.2f) else NexoraSurfaceElevated,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = track,
                                color = if (selectedAudioTrack == track) NexoraCyanAccent else NexoraTextPrimary,
                                modifier = Modifier.padding(12.dp),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showMusicDialog = false }) {
                    Text("Close", color = NexoraCyanAccent)
                }
            }
        )
    }

    // Text Overlay Dialog
    if (showTextDialog) {
        var localText by remember { mutableStateOf(textOverlay) }
        AlertDialog(
            onDismissRequest = { showTextDialog = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Add Text Overlay", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = localText,
                    onValueChange = { localText = it },
                    placeholder = { Text("e.g. Wait for the end!") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onTextOverlayChange(localText)
                    showTextDialog = false
                }) {
                    Text("Apply", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTextDialog = false }) {
                    Text("Cancel", color = NexoraTextSecondary)
                }
            }
        )
    }

    // Sticker Picker Dialog
    if (showStickerDialog) {
        AlertDialog(
            onDismissRequest = { showStickerDialog = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Choose Sticker", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PRESET_STICKERS.forEach { st ->
                        Text(
                            text = st,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onStickerChange(st)
                                    showStickerDialog = false
                                }
                                .padding(4.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onStickerChange("")
                    showStickerDialog = false
                }) {
                    Text("Remove Sticker", color = NexoraErrorRed)
                }
            }
        )
    }
}

@Composable
private fun EditorToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isActive) NexoraCyanAccent.copy(alpha = 0.2f) else NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isActive) NexoraCyanAccent else NexoraSurfaceBorder),
        modifier = modifier.height(44.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isActive) NexoraCyanAccent else NexoraTextPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = NexoraTextPrimary)
        }
    }
}

// ----------------------------------------------------
// STEP 3: DETAILS
// ----------------------------------------------------
@Composable
private fun ShortDetailsStep(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    hashtags: String,
    onHashtagsChange: (String) -> Unit,
    isMadeForKids: Boolean,
    onKidsChange: (Boolean) -> Unit,
    visibility: String,
    onVisibilityChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    allowComments: Boolean,
    onAllowCommentsChange: (Boolean) -> Unit,
    isAiGenerated: Boolean,
    onAiGeneratedChange: (Boolean) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Title
        Text("TITLE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Give your Short a title...") },
            singleLine = true,
            modifier = Modifier.testTag("input_short_title").fillMaxWidth(),
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
            onValueChange = onDescriptionChange,
            placeholder = { Text("Add details about this Short...") },
            minLines = 2,
            modifier = Modifier.testTag("input_short_description").fillMaxWidth(),
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
            onValueChange = onHashtagsChange,
            placeholder = { Text("#shorts #gaming #viral") },
            singleLine = true,
            modifier = Modifier.testTag("input_short_hashtags").fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = NexoraTextPrimary,
                unfocusedTextColor = NexoraTextPrimary,
                focusedBorderColor = NexoraCyanAccent,
                unfocusedBorderColor = NexoraSurfaceBorder
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Audience: Made for Kids (YES / NO)
        Text("AUDIENCE: MADE FOR KIDS?", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = !isMadeForKids,
                onClick = { onKidsChange(false) },
                label = { Text("No, it's not made for kids") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
            )
            FilterChip(
                selected = isMadeForKids,
                onClick = { onKidsChange(true) },
                label = { Text("Yes, made for kids") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Visibility: Public / Unlisted / Private
        Text("VISIBILITY", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("PUBLIC", "UNLISTED", "PRIVATE").forEach { v ->
                FilterChip(
                    selected = visibility == v,
                    onClick = { onVisibilityChange(v) },
                    label = { Text(v) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Location
        Text("LOCATION", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            placeholder = { Text("Add location or skip...") },
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

        // Comments Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Allow Comments", style = MaterialTheme.typography.bodyMedium, color = NexoraTextPrimary)
                Text(if (allowComments) "Comments enabled" else "Comments disabled", style = MaterialTheme.typography.labelSmall, color = NexoraTextMuted)
            }
            Switch(
                checked = allowComments,
                onCheckedChange = onAllowCommentsChange,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = NexoraCyanAccent)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Content Disclosure (YES / NO)
        Text(
            text = "AI CONTENT DISCLOSURE",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = NexoraCyanAccent
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Does this content contain AI-generated or significantly AI-modified content?",
            style = MaterialTheme.typography.bodySmall,
            color = NexoraTextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FilterChip(
                selected = !isAiGenerated,
                onClick = { onAiGeneratedChange(false) },
                label = { Text("No") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
            )
            FilterChip(
                selected = isAiGenerated,
                onClick = { onAiGeneratedChange(true) },
                label = { Text("Yes (AI Modified)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = NexoraCyanAccent, selectedLabelColor = Color.Black)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Button: NEXT
        Surface(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            color = NexoraCyanAccent,
            modifier = Modifier
                .testTag("btn_details_next")
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("NEXT", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

// ----------------------------------------------------
// STEP 4: THUMBNAIL
// ----------------------------------------------------
@Composable
private fun ThumbnailSelectionStep(
    selectedFrame: Int,
    customLabel: String,
    onSelectFrame: (Int) -> Unit,
    onUploadCustom: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Short Thumbnail",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = NexoraTextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Select a key frame or upload a custom cover image.",
            style = MaterialTheme.typography.bodySmall,
            color = NexoraTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Preview of selected thumbnail
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(260.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F172A), Color(0xFF312E81), Color(0xFF06B6D4))
                    )
                )
                .border(2.dp, NexoraCyanAccent, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("FRAME #${selectedFrame + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(customLabel, color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Frame selector row
        Text("SELECT FRAME FROM VIDEO", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = NexoraCyanAccent)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            (0..3).forEach { index ->
                Surface(
                    onClick = { onSelectFrame(index) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (selectedFrame == index) NexoraCyanAccent.copy(alpha = 0.25f) else NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, if (selectedFrame == index) NexoraCyanAccent else NexoraSurfaceBorder),
                    modifier = Modifier.size(54.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("${index + 1}", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            onClick = onUploadCustom,
            shape = RoundedCornerShape(10.dp),
            color = NexoraSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
            modifier = Modifier.fillMaxWidth(0.8f).height(42.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("UPLOAD THUMBNAIL / EDIT", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Surface(
            onClick = onNext,
            shape = RoundedCornerShape(12.dp),
            color = NexoraCyanAccent,
            modifier = Modifier
                .testTag("btn_thumbnail_next")
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("NEXT", fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

// ----------------------------------------------------
// STEP 5: POLICY AGREEMENT
// ----------------------------------------------------
@Composable
private fun PolicyConfirmationStep(
    policyAgreed: Boolean,
    onPolicyAgreedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onAgreeAndUpload: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(36.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Final Policy Confirmation",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = NexoraTextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = NexoraSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "By uploading this content, you confirm that you have the necessary rights to upload it and agree to follow NEXORA's Privacy Policy, Community Guidelines, and Content Policies.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextPrimary,
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPolicyAgreedChange(!policyAgreed) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = policyAgreed,
                onCheckedChange = onPolicyAgreedChange,
                colors = CheckboxDefaults.colors(checkedColor = NexoraCyanAccent, checkmarkColor = Color.Black)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "I agree to the above.",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = NexoraTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                onClick = onBack,
                shape = RoundedCornerShape(12.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("BACK", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                }
            }

            Surface(
                onClick = { if (policyAgreed) onAgreeAndUpload() },
                shape = RoundedCornerShape(12.dp),
                color = if (policyAgreed) NexoraCyanAccent else NexoraSurfaceElevated,
                modifier = Modifier
                    .testTag("btn_agree_and_upload")
                    .weight(1.5f)
                    .height(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "AGREE & UPLOAD",
                        fontWeight = FontWeight.Bold,
                        color = if (policyAgreed) Color.Black else NexoraTextMuted
                    )
                }
            }
        }
    }
}

// ----------------------------------------------------
// STEP 6: UPLOAD PROCESS & STATUS
// ----------------------------------------------------
@Composable
private fun UploadProgressStep(
    uiState: com.example.creator.viewmodel.CreatorUiState,
    onRetry: () -> Unit,
    onSaveDraft: () -> Unit,
    onCancel: () -> Unit,
    onViewShort: () -> Unit,
    onDone: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (uiState.uploadStep) {
            UploadStatusStep.COMPLETED -> {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NexoraSuccessGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = NexoraSuccessGreen, modifier = Modifier.size(44.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Your Short has been published.",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "It is now live on your Video Channel and in the NEXORA Shorts Feed.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        onClick = onViewShort,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraCyanAccent,
                        modifier = Modifier
                            .testTag("btn_view_short_success")
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("VIEW SHORT", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Surface(
                        onClick = onDone,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier
                            .testTag("btn_done_short_success")
                            .weight(1f)
                            .height(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("DONE", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                        }
                    }
                }
            }

            UploadStatusStep.FAILED -> {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(NexoraErrorRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Error, contentDescription = null, tint = NexoraErrorRed, modifier = Modifier.size(44.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Upload failed.",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "A network or encoding error occurred. Your configuration has been saved.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        onClick = onRetry,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraCyanAccent,
                        modifier = Modifier
                            .testTag("btn_retry_upload")
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("RETRY", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Surface(
                        onClick = onSaveDraft,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier
                            .testTag("btn_save_as_draft_failed")
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("SAVE AS DRAFT", fontWeight = FontWeight.Bold, color = NexoraTextPrimary)
                        }
                    }

                    Surface(
                        onClick = onCancel,
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraSurfaceElevated,
                        modifier = Modifier
                            .testTag("btn_cancel_failed_upload")
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("CANCEL", fontWeight = FontWeight.SemiBold, color = NexoraTextSecondary)
                        }
                    }
                }
            }

            else -> {
                // In-Flight Steps
                CircularProgressIndicator(
                    color = NexoraCyanAccent,
                    strokeWidth = 4.dp,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = uiState.uploadStatusMessage,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = { uiState.uploadProgressPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = NexoraCyanAccent,
                    trackColor = NexoraSurfaceElevated
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "${uiState.uploadProgressPercent}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraCyanAccent
                )
            }
        }
    }
}
