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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreatorContentEntity
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentPreviewPlayerDialog(
    content: CreatorContentEntity,
    onDismiss: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NexoraSurfaceDark,
        titleContentColor = NexoraTextPrimary,
        modifier = Modifier.padding(16.dp),
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("content_preview_player"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Video Screen Box
                val isShort = content.contentType == "SHORT"
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isShort) 340.dp else 200.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF06B6D4))
                            )
                        )
                        .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(14.dp))
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { isPlaying = !isPlaying }, modifier = Modifier.size(54.dp)) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Text(
                            text = if (isPlaying) "Playing (${content.speed}x)" else "Paused",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    // Overlays
                    content.sticker?.let { st ->
                        Text(text = st, fontSize = 28.sp, modifier = Modifier.align(Alignment.TopEnd).padding(12.dp))
                    }
                    content.textOverlay?.let { txt ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp)
                        ) {
                            Text(
                                text = txt,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (content.captionsEnabled) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                        ) {
                            Text("CC: [Audio Playing]", color = NexoraCyanAccent, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                    content.audioTrack?.let { track ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(track, color = Color.White, fontSize = 10.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title and Channel Details
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = content.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${content.channelName} • ${content.channelHandle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyanAccent
                    )
                    if (content.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = content.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary
                        )
                    }
                    if (content.hashtags.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = content.hashtags,
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraIndigoLight
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status: ${content.status}", fontSize = 11.sp, color = NexoraCyanAccent)
                        Text("Visibility: ${content.visibility}", fontSize = 11.sp, color = NexoraTextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Close", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
            }
        }
    )
}
