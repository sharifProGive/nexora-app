package com.example.chat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.chat.model.AudienceOption
import com.example.chat.model.PrivacySettings
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@Composable
fun PrivacySettingsDialog(
    settings: PrivacySettings,
    onUpdateMessageAudience: (AudienceOption) -> Unit,
    onUpdateCallAudience: (AudienceOption) -> Unit,
    onUpdateRequestAudience: (AudienceOption) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NexoraSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
            modifier = Modifier
                .testTag("dialog_privacy_settings")
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Privacy & Safety",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NexoraTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Who can message me?
                Text(
                    text = "Who can message me?",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AudienceOption.values().forEach { option ->
                    AudienceOptionRow(
                        title = option.label,
                        isSelected = settings.whoCanMessageMe == option,
                        onSelect = { onUpdateMessageAudience(option) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = NexoraSurfaceBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Who can call me?
                Text(
                    text = "Who can call me?",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                AudienceOption.values().forEach { option ->
                    AudienceOptionRow(
                        title = option.label,
                        isSelected = settings.whoCanCallMe == option,
                        onSelect = { onUpdateCallAudience(option) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = NexoraSurfaceBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Who can send friend requests?
                Text(
                    text = "Who can send friend requests?",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                listOf(AudienceOption.EVERYONE, AudienceOption.FOLLOWERS, AudienceOption.NOBODY).forEach { option ->
                    AudienceOptionRow(
                        title = option.label,
                        isSelected = settings.whoCanSendRequests == option,
                        onSelect = { onUpdateRequestAudience(option) }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Safety banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = null,
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Blocked users: ${settings.blockedUserIds.size}. Blocked accounts cannot message, call, or see your activity.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AudienceOptionRow(
    title: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(vertical = 8.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected) NexoraCyanAccent else NexoraTextSecondary
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = NexoraCyanAccent,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
