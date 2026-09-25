package com.example.chat.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.model.ActiveCallSession
import com.example.chat.model.CallStatus
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@Composable
fun AudioCallScreen(
    callSession: ActiveCallSession,
    onToggleMute: () -> Unit,
    onToggleSpeaker: () -> Unit,
    onEndCall: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    val avatarColor = remember(callSession.remoteUser.avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(callSession.remoteUser.avatarColorHex))
        } catch (_: Exception) {
            NexoraIndigoPrimary
        }
    }

    val minutes = callSession.durationSeconds / 60
    val seconds = callSession.durationSeconds % 60
    val durationText = "%02d:%02d".format(minutes, seconds)

    Box(
        modifier = Modifier
            .testTag("audio_call_screen")
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        NexoraDarkBackground,
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header / Demo mode info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraCyanAccent.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.3f)),
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "NEXORA Audio Calling • Live WebRTC Architecture",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NexoraCyanAccent,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = callSession.remoteUser.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Text(
                    text = callSession.remoteUser.handle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraIndigoLight
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (callSession.status == CallStatus.CONNECTED) durationText else callSession.status.label,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = if (callSession.status == CallStatus.CONNECTED) FontWeight.Bold else FontWeight.Medium
                    ),
                    color = if (callSession.status == CallStatus.CONNECTED) NexoraCyanAccent else NexoraTextMuted
                )
            }

            // Avatar with animated pulse
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(240.dp)
            ) {
                if (callSession.status == CallStatus.CALLING || callSession.status == CallStatus.RINGING) {
                    Box(
                        modifier = Modifier
                            .size(190.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(NexoraCyanAccent.copy(alpha = 0.15f))
                    )
                }

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(avatarColor)
                        .border(3.dp, NexoraCyanAccent.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = callSession.remoteUser.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }

            // Call Controls: Mute, Speaker, End Call
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            onClick = onToggleMute,
                            shape = CircleShape,
                            color = if (callSession.isMuted) Color(0xFFEF4444).copy(alpha = 0.2f) else NexoraSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (callSession.isMuted) Color(0xFFEF4444) else NexoraSurfaceBorder
                            ),
                            modifier = Modifier
                                .testTag("btn_call_mute")
                                .size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (callSession.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mute",
                                    tint = if (callSession.isMuted) Color(0xFFEF4444) else NexoraTextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (callSession.isMuted) "Muted" else "Mute",
                            fontSize = 11.sp,
                            color = NexoraTextSecondary
                        )
                    }

                    // Speaker Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            onClick = onToggleSpeaker,
                            shape = CircleShape,
                            color = if (callSession.isSpeakerOn) NexoraCyanAccent.copy(alpha = 0.2f) else NexoraSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (callSession.isSpeakerOn) NexoraCyanAccent else NexoraSurfaceBorder
                            ),
                            modifier = Modifier
                                .testTag("btn_call_speaker")
                                .size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (callSession.isSpeakerOn) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                    contentDescription = "Speaker",
                                    tint = if (callSession.isSpeakerOn) NexoraCyanAccent else NexoraTextPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (callSession.isSpeakerOn) "Speaker On" else "Speaker",
                            fontSize = 11.sp,
                            color = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // End Call Button
                Surface(
                    onClick = onEndCall,
                    shape = CircleShape,
                    color = Color(0xFFEF4444),
                    modifier = Modifier
                        .testTag("btn_call_end")
                        .size(68.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End Call",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "End Call",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = NexoraTextSecondary
                )
            }
        }
    }
}
