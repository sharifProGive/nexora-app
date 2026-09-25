package com.example.chat.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gif
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.chat.model.MessageType
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraVioletAccent

data class AttachmentOption(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val messageType: MessageType
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onSendAttachment: (type: MessageType, name: String, size: String, url: String?) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Android Photo Picker (zero broad storage permissions needed)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSendAttachment(MessageType.IMAGE, "photo_${System.currentTimeMillis()}.jpg", "2.4 MB", uri.toString())
        }
    }

    // Camera permission request launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onSendAttachment(MessageType.IMAGE, "camera_capture_${System.currentTimeMillis()}.jpg", "3.1 MB", null)
        }
    }

    val options = listOf(
        AttachmentOption("photo", "Photo", Icons.Default.PhotoLibrary, NexoraCyanAccent, MessageType.IMAGE),
        AttachmentOption("video", "Video", Icons.Default.Videocam, Color(0xFFEC4899), MessageType.VIDEO),
        AttachmentOption("camera", "Camera", Icons.Default.CameraAlt, Color(0xFF3B82F6), MessageType.IMAGE),
        AttachmentOption("file", "File / Doc", Icons.Default.Description, Color(0xFFF59E0B), MessageType.FILE),
        AttachmentOption("audio", "Audio", Icons.Default.Audiotrack, NexoraVioletAccent, MessageType.AUDIO),
        AttachmentOption("contact", "Contact", Icons.Default.ContactPhone, Color(0xFF10B981), MessageType.CONTACT),
        AttachmentOption("location", "Location", Icons.Default.LocationOn, Color(0xFFEF4444), MessageType.LOCATION),
        AttachmentOption("gif", "GIF", Icons.Default.Gif, Color(0xFF8B5CF6), MessageType.IMAGE),
        AttachmentOption("sticker", "Sticker", Icons.Default.InsertEmoticon, Color(0xFF06B6D4), MessageType.STICKER)
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = NexoraSurfaceDark,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("attachment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 28.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "Share Content",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = NexoraTextPrimary,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(options) { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .testTag("attachment_opt_${item.id}")
                            .clickable {
                                when (item.id) {
                                    "photo", "gif" -> {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    "video" -> {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                        )
                                    }
                                    "camera" -> {
                                        val hasCam = ContextCompat.checkSelfPermission(
                                            context, Manifest.permission.CAMERA
                                        ) == PackageManager.PERMISSION_GRANTED
                                        if (hasCam) {
                                            onSendAttachment(MessageType.IMAGE, "capture.jpg", "2.8 MB", null)
                                        } else {
                                            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                        }
                                    }
                                    "file" -> {
                                        onSendAttachment(MessageType.FILE, "NEXORA_Creator_Brief.pdf", "1.2 MB", null)
                                    }
                                    "audio" -> {
                                        onSendAttachment(MessageType.AUDIO, "studio_soundtrack.mp3", "4.6 MB", null)
                                    }
                                    "contact" -> {
                                        onSendAttachment(MessageType.CONTACT, "NEXORA Support Hub", "Contact Card", null)
                                    }
                                    "location" -> {
                                        onSendAttachment(MessageType.LOCATION, "Live Location", "GPS Coordinates", null)
                                    }
                                    "sticker" -> {
                                        onSendAttachment(MessageType.STICKER, "Neon Nexora Glow", "Sticker", null)
                                    }
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(item.color.copy(alpha = 0.15f))
                                .border(1.dp, item.color.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = item.color,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = NexoraTextPrimary
                        )
                    }
                }
            }
        }
    }
}
