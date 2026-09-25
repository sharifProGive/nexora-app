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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraTopBar
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

@Composable
fun ProfilePrivacyScreen(
    initialIsPrivate: Boolean,
    onBack: () -> Unit,
    onSelectPrivacy: (isPrivate: Boolean) -> Unit
) {
    var isPrivateSelected by remember { mutableStateOf(initialIsPrivate) }

    Column(
        modifier = Modifier
            .testTag("profile_privacy_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Profile Privacy",
            subtitle = "Step 4: Discoverability Preferences",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Progressive Step Indicator
                ProgressiveStepBar(currentStep = 4)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Choose Profile Privacy",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Control how your Personal Profile appears across NEXORA. You can change this at any time in Profile Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Option 1: PUBLIC PROFILE
                PrivacyOptionCard(
                    isSelected = !isPrivateSelected,
                    title = "PUBLIC PROFILE",
                    tagline = "Recommended for community & creators",
                    accentColor = NexoraCyanAccent,
                    icon = Icons.Default.Public,
                    bulletPoints = listOf(
                        "Profile can appear in public search results",
                        "Other NEXORA users can discover and follow your profile",
                        "Profile information is visible according to community standards",
                        "Public personal content can be recommended by NEXORA discovery"
                    ),
                    testTag = "privacy_option_public",
                    onClick = { isPrivateSelected = false }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Option 2: PRIVATE PROFILE
                PrivacyOptionCard(
                    isSelected = isPrivateSelected,
                    title = "PRIVATE PROFILE",
                    tagline = "Restricted personal visibility",
                    accentColor = NexoraIndigoLight,
                    icon = Icons.Default.Lock,
                    bulletPoints = listOf(
                        "Profile is NOT freely discoverable through public search",
                        "You can still watch and enjoy all videos and shorts",
                        "You control who can interact with your profile",
                        "Personal profile content remains restricted",
                        "Uploading videos to a Channel still requires creating a Channel separately"
                    ),
                    testTag = "privacy_option_private",
                    onClick = { isPrivateSelected = true }
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Architecture Warning Notice
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
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Do not confuse Personal Profile privacy with Video Channel privacy. When you launch a Video Channel, its content distribution is managed independently.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                NexoraPrimaryButton(
                    text = "Continue with ${if (isPrivateSelected) "Private" else "Public"} Profile",
                    testTag = "btn_confirm_privacy",
                    onClick = { onSelectPrivacy(isPrivateSelected) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Configurable anytime in Profile Settings.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun PrivacyOptionCard(
    isSelected: Boolean,
    title: String,
    tagline: String,
    accentColor: Color,
    icon: ImageVector,
    bulletPoints: List<String>,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) NexoraSurfaceElevated else NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 1.dp,
            if (isSelected) accentColor else NexoraSurfaceBorder
        ),
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) accentColor else NexoraTextPrimary
                    )
                    Text(
                        text = tagline,
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraTextSecondary
                    )
                }

                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (isSelected) accentColor else NexoraTextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                bulletPoints.forEach { point ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                        Text(
                            text = point,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) NexoraTextPrimary else NexoraTextSecondary
                        )
                    }
                }
            }
        }
    }
}
