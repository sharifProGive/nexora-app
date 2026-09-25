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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.NexoraAvatarBadge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileSetupScreen(
    accountName: String,
    handle: String,
    isPrivateProfile: Boolean = false,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onCompleteProfile: (bio: String, avatarIndex: Int) -> Unit
) {
    var bioInput by remember { mutableStateOf("Digital explorer & creator building on NEXORA.") }
    var selectedAvatarIndex by remember { mutableIntStateOf(0) }

    val formattedDate = remember {
        SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())
    }

    Column(
        modifier = Modifier
            .testTag("profile_setup_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Main Profile Setup",
            subtitle = "Step 6: Personalize your NEXORA Profile",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Profile Avatar Preview
                NexoraAvatarBadge(
                    avatarIndex = selectedAvatarIndex,
                    name = accountName,
                    size = 96
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = accountName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Text(
                    text = handle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = NexoraCyanAccent
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Avatar Theme Selector
                Text(
                    text = "SELECT AVATAR THEME",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 0 until 5) {
                        val isSelected = selectedAvatarIndex == i
                        Box(
                            modifier = Modifier
                                .testTag("avatar_color_picker_$i")
                                .size(42.dp)
                                .clip(CircleShape)
                                .clickable { selectedAvatarIndex = i },
                            contentAlignment = Alignment.Center
                        ) {
                            NexoraAvatarBadge(
                                avatarIndex = i,
                                name = accountName,
                                size = 42
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bio Input
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Bio (About You)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = bioInput,
                        onValueChange = { if (it.length <= 160) bioInput = it },
                        placeholder = { Text("Share a short bio with the community...", color = NexoraTextMuted) },
                        maxLines = 4,
                        minLines = 3,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = NexoraSurfaceDark,
                            unfocusedContainerColor = NexoraSurfaceDark,
                            focusedBorderColor = NexoraIndigoPrimary,
                            unfocusedBorderColor = NexoraSurfaceBorder,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary,
                            cursorColor = NexoraCyanAccent
                        ),
                        modifier = Modifier
                            .testTag("input_bio")
                            .fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Visible on Video, Chat & Social",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                        Text(
                            text = "${bioInput.length}/160",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Metadata Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Account Creation Date:",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary
                            )
                            Text(
                                text = formattedDate,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Profile Architecture:",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary
                            )
                            Text(
                                text = "Personal Profile (Permanent)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraIndigoLight
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Discoverability Privacy:",
                                style = MaterialTheme.typography.bodySmall,
                                color = NexoraTextSecondary
                            )
                            Text(
                                text = if (isPrivateProfile) "Private Profile" else "Public Profile",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isPrivateProfile) NexoraTextMuted else NexoraCyanAccent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                NexoraPrimaryButton(
                    text = "Complete Registration",
                    isLoading = isLoading,
                    testTag = "btn_finalize_registration",
                    onClick = {
                        onCompleteProfile(bioInput, selectedAvatarIndex)
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Creating your permanent NEXORA personal profile.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
