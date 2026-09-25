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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.UserEntity
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullPersonalProfileScreen(
    user: UserEntity,
    onBack: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val profileTabs = listOf("VIDEOS", "PHOTOS", "ABOUT")

    val formattedDate = remember(user.createdAt) {
        val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
        sdf.format(Date(user.createdAt))
    }

    val avatarColor = remember(user.avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(user.avatarColorHex))
        } catch (_: Exception) {
            NexoraIndigoPrimary
        }
    }

    Column(
        modifier = Modifier
            .testTag("full_personal_profile_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_back_from_full_profile")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexoraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Personal Profile",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
            }

            IconButton(
                onClick = onEditProfileClick,
                modifier = Modifier.testTag("btn_edit_profile_header")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = NexoraCyanAccent
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Profile Header Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(avatarColor)
                            .border(2.5.dp, NexoraSurfaceBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.first().toString().uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 32.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = user.name,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                        }

                        Text(
                            text = user.handle,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = NexoraIndigoLight
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Privacy Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (user.isPrivateProfile) NexoraSurfaceElevated else NexoraCyanAccent.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(
                                0.8.dp,
                                if (user.isPrivateProfile) NexoraSurfaceBorder else NexoraCyanAccent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (user.isPrivateProfile) Icons.Default.Lock else Icons.Default.Public,
                                    contentDescription = null,
                                    tint = if (user.isPrivateProfile) NexoraTextMuted else NexoraCyanAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (user.isPrivateProfile) "PRIVATE PROFILE" else "PUBLIC PROFILE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.isPrivateProfile) NexoraTextMuted else NexoraCyanAccent
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bio
                Text(
                    text = user.bio.ifBlank { "Member of the NEXORA community." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Stats Row: Followers, Following, Joined
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(NexoraSurfaceDark)
                        .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user.followersCount}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = "Followers",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(NexoraSurfaceBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${user.followingCount}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = "Following",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(NexoraSurfaceBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraCyanAccent
                        )
                        Text(
                            text = "Joined",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tabs: Videos, Photos, About
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
                profileTabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedTab == index) NexoraCyanAccent else NexoraTextSecondary
                            )
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // Profile Videos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = NexoraTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No personal videos uploaded",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Personal videos shared on your profile will appear here.",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                1 -> {
                    // Profile Photos
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = NexoraTextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No photos shared yet",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Personal pictures and profile moments will appear here.",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                2 -> {
                    // About
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Account Information",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NexoraSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = "Auth Provider: ${user.authProvider}", style = MaterialTheme.typography.bodySmall, color = NexoraTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Identifier: ${user.identifier}", style = MaterialTheme.typography.bodySmall, color = NexoraTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Security: PBKDF2 Salted Encryption", style = MaterialTheme.typography.bodySmall, color = NexoraTextSecondary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Channel Status: ${if (user.hasChannel) "Active (${user.channelName})" else "No Channel Created"}", style = MaterialTheme.typography.bodySmall, color = NexoraCyanAccent)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
