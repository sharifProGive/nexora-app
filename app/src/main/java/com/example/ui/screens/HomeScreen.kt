package com.example.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.components.NexoraAvatarBadge
import com.example.ui.components.NexoraLogoBadge
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.ui.theme.NexoraVioletAccent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    user: UserEntity?,
    onLogout: () -> Unit
) {
    val creationDateFormatted = remember(user?.createdAt) {
        if (user != null) {
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(user.createdAt))
        } else "Recent"
    }

    Column(
        modifier = Modifier
            .testTag("home_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // App Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                NexoraLogoBadge(size = 38)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "NEXORA",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = NexoraTextPrimary
                    )
                    Text(
                        text = "Foundation Step 1",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraCyanAccent
                    )
                }
            }

            IconButton(
                onClick = onLogout,
                modifier = Modifier
                    .testTag("btn_logout")
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(NexoraSurfaceElevated)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    tint = NexoraError
                )
            }
        }

        HorizontalDivider(color = NexoraSurfaceBorder, thickness = 1.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Main Personal Profile Card
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier
                    .testTag("main_profile_card")
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = NexoraIndigoPrimary)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NexoraAvatarBadge(
                            avatarIndex = user?.avatarIndex ?: 0,
                            name = user?.name ?: "User",
                            size = 76
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user?.name ?: "Sharif",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "Verified Identity",
                                    tint = NexoraCyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Text(
                                text = user?.handle ?: "@sharif",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NexoraIndigoLight
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Member since $creationDateFormatted",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bio
                    Text(
                        text = user?.bio?.ifBlank { "Member of the NEXORA community." }
                            ?: "Digital explorer building on NEXORA.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = NexoraSurfaceBorder, thickness = 0.8.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Account stats & details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "AUTH METHOD",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextMuted
                            )
                            Text(
                                text = user?.authProvider ?: "EMAIL",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraCyanAccent
                            )
                        }

                        Column {
                            Text(
                                text = "MAIN IDENTITY",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextMuted
                            )
                            Text(
                                text = "Unified Profile",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraSuccess
                            )
                        }

                        Column {
                            Text(
                                text = "CHANNELS",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextMuted
                            )
                            Text(
                                text = "${user?.videoChannelsCount ?: 0} Linked",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                        }
                    }
                }
            }

            // Step 13: Future-Ready Account Architecture Section
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NexoraIndigoPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                tint = NexoraIndigoLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Future-Ready Account Architecture",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                            Text(
                                text = "Step 1: One Account + One Main Profile Foundation",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraCyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Your NEXORA account has been architected to support future multi-channel expansion:",
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Comparison Preview
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(0.8.dp, NexoraSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "CURRENT NEXORA PROFILE (Main Identity):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextMuted
                            )
                            Text(
                                text = "${user?.name ?: "Sharif"}  •  ${user?.handle ?: "@sharif"}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = NexoraSurfaceBorder, thickness = 0.5.dp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "FUTURE SEPARATE VIDEO CHANNEL (Prepared for Step 2):",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextMuted
                            )
                            Text(
                                text = "Skyline Pro Gamer  •  @skylineprogamer",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraIndigoLight
                            )
                        }
                    }
                }
            }

            // Planned Sections Foundation Preview
            Text(
                text = "UPCOMING NEXORA REALMS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = NexoraTextMuted
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UpcomingFeatureChip(
                    icon = Icons.Default.Movie,
                    title = "NEXORA Video",
                    subtitle = "Phase 2",
                    modifier = Modifier.weight(1f)
                )
                UpcomingFeatureChip(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "NEXORA Chat",
                    subtitle = "Phase 3",
                    modifier = Modifier.weight(1f)
                )
                UpcomingFeatureChip(
                    icon = Icons.Default.People,
                    title = "NEXORA Social",
                    subtitle = "Phase 4",
                    modifier = Modifier.weight(1f)
                )
            }

            // Security Details Card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NexoraSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PBKDF2 Hashed • Verified OTP Session",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = "Zero plain-text password storage. Encrypted local Room storage active.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }
                }
            }

            // Logout Button
            Button(
                onClick = onLogout,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexoraSurfaceDark,
                    contentColor = NexoraError
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraError.copy(alpha = 0.4f)),
                modifier = Modifier
                    .testTag("btn_logout_bottom")
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sign Out of NEXORA",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun UpcomingFeatureChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = modifier.height(72.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NexoraCyanAccent.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = NexoraTextMuted,
                fontSize = 10.sp
            )
        }
    }
}
