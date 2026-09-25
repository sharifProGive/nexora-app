package com.example.creator.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.creator.model.ChannelSocialLink
import com.example.creator.model.SocialLinksJsonHelper
import com.example.creator.viewmodel.CreatorViewModel
import com.example.data.model.ChannelEntity
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

private val BANNER_GRADIENTS = listOf(
    listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF4C1D95), Color(0xFF06B6D4)), // Cyber Dark
    listOf(Color(0xFF18181B), Color(0xFF27272A), Color(0xFF3F3F46), Color(0xFF71717A)), // Zinc Noir
    listOf(Color(0xFF022C22), Color(0xFF064E3B), Color(0xFF047857), Color(0xFF10B981)), // Emerald Gamer
    listOf(Color(0xFF450A0A), Color(0xFF7F1D1D), Color(0xFFB91C1C), Color(0xFFF97316)), // Crimson Flare
    listOf(Color(0xFF172554), Color(0xFF1E3A8A), Color(0xFF2563EB), Color(0xFF38BDF8))  // Deep Ocean
)

private val AVATAR_COLORS = listOf(
    "#06B6D4", "#6366F1", "#A855F7", "#EC4899", "#10B981", "#F59E0B", "#3B82F6", "#8B5CF6"
)

private val CATEGORIES = listOf(
    "Gaming", "Technology", "Entertainment", "Music", "Education",
    "Comedy", "Lifestyle", "Sports", "News & Politics", "Science"
)

private val LANGUAGES = listOf(
    "English", "Spanish", "French", "German", "Japanese", "Hindi", "Portuguese", "Arabic"
)

private val REGIONS = listOf(
    "United States", "United Kingdom", "Canada", "Germany", "Japan", "India", "Australia", "Global"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChannelScreen(
    viewModel: CreatorViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val channel = uiState.channel

    var name by remember(channel) { mutableStateOf(channel?.name ?: "") }
    var handle by remember(channel) { mutableStateOf(channel?.handle ?: "") }
    var description by remember(channel) { mutableStateOf(channel?.description ?: "") }
    var avatarColorHex by remember(channel) { mutableStateOf(channel?.avatarColorHex ?: "#06B6D4") }
    var bannerGradientIndex by remember(channel) { mutableIntStateOf(channel?.bannerGradientIndex ?: 0) }
    var category by remember(channel) { mutableStateOf(channel?.category ?: "Gaming") }
    var language by remember(channel) { mutableStateOf(channel?.language ?: "English") }
    var countryRegion by remember(channel) { mutableStateOf(channel?.countryRegion ?: "United States") }
    var contactEmail by remember(channel) { mutableStateOf(channel?.contactEmail ?: "") }

    var showSubscriberCount by remember(channel) { mutableStateOf(channel?.showSubscriberCount ?: true) }
    var allowComments by remember(channel) { mutableStateOf(channel?.allowComments ?: true) }
    var allowSharing by remember(channel) { mutableStateOf(channel?.allowSharing ?: true) }
    var showInSearch by remember(channel) { mutableStateOf(channel?.showInSearch ?: true) }
    var allowRecommendations by remember(channel) { mutableStateOf(channel?.allowRecommendations ?: true) }

    // Upload defaults
    var defaultVisibility by remember(channel) { mutableStateOf(channel?.defaultVisibility ?: "PUBLIC") }
    var defaultAudience by remember(channel) { mutableStateOf(channel?.defaultAudience ?: "NOT_FOR_KIDS") }
    var defaultCategory by remember(channel) { mutableStateOf(channel?.defaultCategory ?: "Gaming") }
    var defaultLanguage by remember(channel) { mutableStateOf(channel?.defaultLanguage ?: "English") }
    var defaultComments by remember(channel) { mutableStateOf(channel?.defaultComments ?: "ALLOW") }

    // Social links
    var socialLinks by remember(channel) {
        mutableStateOf(SocialLinksJsonHelper.fromJson(channel?.socialLinksJson))
    }

    var showAddLinkDialog by remember { mutableStateOf(false) }
    var editingLinkIndex by remember { mutableIntStateOf(-1) }
    var linkPlatform by remember { mutableStateOf("Instagram") }
    var linkTitle by remember { mutableStateOf("") }
    var linkUrl by remember { mutableStateOf("") }

    // Dialog for changing profile picture/color
    var showAvatarPicker by remember { mutableStateOf(false) }
    // Dialog for changing banner
    var showBannerPicker by remember { mutableStateOf(false) }

    LaunchedEffect(handle) {
        if (handle.isNotBlank()) {
            viewModel.checkHandleAvailability(handle)
        }
    }

    Column(
        modifier = Modifier
            .testTag("edit_channel_screen")
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
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("btn_back_edit_channel")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexoraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Edit Channel",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Text(
                        text = "Customize channel identity & defaults",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraTextSecondary
                    )
                }
            }

            Surface(
                onClick = {
                    viewModel.saveChannelChanges(
                        name = name,
                        handle = handle,
                        description = description,
                        profilePicUri = channel?.profilePictureUri,
                        bannerUri = channel?.bannerUri,
                        avatarColorHex = avatarColorHex,
                        bannerGradientIndex = bannerGradientIndex,
                        category = category,
                        language = language,
                        countryRegion = countryRegion,
                        contactEmail = contactEmail,
                        showSubscriberCount = showSubscriberCount,
                        allowComments = allowComments,
                        allowSharing = allowSharing,
                        showInSearch = showInSearch,
                        allowRecommendations = allowRecommendations,
                        defaultVisibility = defaultVisibility,
                        defaultAudience = defaultAudience,
                        defaultCategory = defaultCategory,
                        defaultLanguage = defaultLanguage,
                        defaultComments = defaultComments,
                        socialLinks = socialLinks,
                        onSuccess = onNavigateBack
                    )
                },
                shape = RoundedCornerShape(10.dp),
                color = NexoraCyanAccent,
                modifier = Modifier
                    .testTag("btn_save_channel_changes")
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = "SAVE CHANGES",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
        }

        HorizontalDivider(color = NexoraSurfaceBorder)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // ================= CHANNEL BANNER PREVIEW & EDIT =================
            Text(
                text = "CHANNEL BANNER",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraCyanAccent
            )
            Spacer(modifier = Modifier.height(8.dp))

            val currentGradient = BANNER_GRADIENTS.getOrElse(bannerGradientIndex) { BANNER_GRADIENTS.first() }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.horizontalGradient(currentGradient))
                    .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(12.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (i in 0..12) {
                        drawLine(
                            color = Color.White.copy(alpha = 0.05f),
                            start = Offset(i * 35f, 0f),
                            end = Offset(i * 35f + 50f, size.height),
                            strokeWidth = 2f
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        onClick = { showBannerPicker = true },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier.testTag("btn_change_banner")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change Banner", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Surface(
                        onClick = { bannerGradientIndex = 1 },
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.65f),
                        modifier = Modifier.testTag("btn_remove_banner")
                    ) {
                        Text(
                            text = "Reset",
                            color = NexoraTextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= CHANNEL PROFILE PICTURE =================
            Text(
                text = "CHANNEL PROFILE PICTURE",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraCyanAccent
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                val parsedColor = try {
                    Color(android.graphics.Color.parseColor(avatarColorHex))
                } catch (_: Exception) {
                    NexoraCyanAccent
                }

                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(parsedColor)
                        .border(2.dp, NexoraSurfaceBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (name.isNotBlank()) name.first().toString().uppercase() else "C",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            onClick = { showAvatarPicker = true },
                            shape = RoundedCornerShape(8.dp),
                            color = NexoraSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                            modifier = Modifier.testTag("btn_change_profile_picture")
                        ) {
                            Text(
                                text = "Change Color / Icon",
                                color = NexoraTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }

                        Surface(
                            onClick = { avatarColorHex = "#06B6D4" },
                            shape = RoundedCornerShape(8.dp),
                            color = NexoraSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                            modifier = Modifier.testTag("btn_remove_profile_picture")
                        ) {
                            Text(
                                text = "Reset",
                                color = NexoraTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Shown on your channel page and videos.",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ================= CHANNEL NAME =================
            Text(
                text = "CHANNEL NAME",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraCyanAccent
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Channel Name") },
                placeholder = { Text("Enter channel name") },
                singleLine = true,
                modifier = Modifier
                    .testTag("input_channel_name")
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexoraCyanAccent,
                    unfocusedBorderColor = NexoraSurfaceBorder,
                    focusedTextColor = NexoraTextPrimary,
                    unfocusedTextColor = NexoraTextPrimary,
                    focusedContainerColor = NexoraSurfaceDark,
                    unfocusedContainerColor = NexoraSurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ================= CHANNEL HANDLE =================
            Text(
                text = "CHANNEL HANDLE",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraCyanAccent
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = handle,
                onValueChange = {
                    val formatted = if (it.isEmpty() || it.startsWith("@")) it else "@$it"
                    handle = formatted
                },
                label = { Text("Channel @handle") },
                placeholder = { Text("Enter @handle") },
                singleLine = true,
                trailingIcon = {
                    if (uiState.isHandleChecking) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = NexoraCyanAccent, strokeWidth = 2.dp)
                    } else if (uiState.isHandleAvailable == true) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Available", tint = NexoraSuccessGreen, modifier = Modifier.size(20.dp))
                    } else if (uiState.isHandleAvailable == false) {
                        Icon(Icons.Default.Error, contentDescription = "Unavailable", tint = NexoraErrorRed, modifier = Modifier.size(20.dp))
                    }
                },
                modifier = Modifier
                    .testTag("input_channel_handle")
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexoraCyanAccent,
                    unfocusedBorderColor = NexoraSurfaceBorder,
                    focusedTextColor = NexoraTextPrimary,
                    unfocusedTextColor = NexoraTextPrimary,
                    focusedContainerColor = NexoraSurfaceDark,
                    unfocusedContainerColor = NexoraSurfaceDark
                )
            )
            uiState.handleValidationMessage?.let { msg ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (uiState.isHandleAvailable == true) NexoraSuccessGreen else NexoraErrorRed
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ================= ABOUT / DESCRIPTION =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ABOUT (DESCRIPTION)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraCyanAccent
                )
                Text(
                    text = "${description.length} / 1000",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 1000) description = it },
                label = { Text("Channel Description") },
                placeholder = { Text("Write about your channel") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier
                    .testTag("input_channel_description")
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexoraCyanAccent,
                    unfocusedBorderColor = NexoraSurfaceBorder,
                    focusedTextColor = NexoraTextPrimary,
                    unfocusedTextColor = NexoraTextPrimary,
                    focusedContainerColor = NexoraSurfaceDark,
                    unfocusedContainerColor = NexoraSurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            // ================= SOCIAL LINKS =================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SOCIAL LINKS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraCyanAccent
                )

                Surface(
                    onClick = {
                        linkPlatform = "Instagram"
                        linkTitle = ""
                        linkUrl = ""
                        editingLinkIndex = -1
                        showAddLinkDialog = true
                    },
                    shape = RoundedCornerShape(6.dp),
                    color = NexoraSurfaceElevated,
                    modifier = Modifier.testTag("btn_add_social_link")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Link", color = NexoraCyanAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (socialLinks.isEmpty()) {
                Text(
                    text = "No links added yet. Add Instagram, X, Discord, Telegram, or custom websites.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexoraTextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    socialLinks.forEachIndexed { index, item ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = NexoraSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Link, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${item.platform}: ${item.title.ifBlank { item.url }}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = NexoraTextPrimary
                                    )
                                    Text(
                                        text = item.url,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexoraTextSecondary
                                    )
                                }

                                // Reorder buttons
                                if (index > 0) {
                                    IconButton(
                                        onClick = {
                                            val mutable = socialLinks.toMutableList()
                                            val temp = mutable[index]
                                            mutable[index] = mutable[index - 1]
                                            mutable[index - 1] = temp
                                            socialLinks = mutable
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowDropUp, contentDescription = "Move Up", tint = NexoraTextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                if (index < socialLinks.size - 1) {
                                    IconButton(
                                        onClick = {
                                            val mutable = socialLinks.toMutableList()
                                            val temp = mutable[index]
                                            mutable[index] = mutable[index + 1]
                                            mutable[index + 1] = temp
                                            socialLinks = mutable
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Move Down", tint = NexoraTextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }

                                // Delete button
                                IconButton(
                                    onClick = {
                                        val mutable = socialLinks.toMutableList()
                                        mutable.removeAt(index)
                                        socialLinks = mutable
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Link", tint = NexoraErrorRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ================= BASIC CHANNEL SETTINGS =================
            Text(
                text = "BASIC CHANNEL SETTINGS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraCyanAccent
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Category Selector
            DropdownSelectorRow(
                label = "Category",
                currentValue = category,
                options = CATEGORIES,
                onSelected = { category = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Language Selector
            DropdownSelectorRow(
                label = "Language",
                currentValue = language,
                options = LANGUAGES,
                onSelected = { language = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Country / Region Selector
            DropdownSelectorRow(
                label = "Country / Region",
                currentValue = countryRegion,
                options = REGIONS,
                onSelected = { countryRegion = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Email
            OutlinedTextField(
                value = contactEmail,
                onValueChange = { contactEmail = it },
                label = { Text("Business Inquiries / Contact Email") },
                placeholder = { Text("creator@nexora.io") },
                singleLine = true,
                modifier = Modifier
                    .testTag("input_channel_contact_email")
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NexoraCyanAccent,
                    unfocusedBorderColor = NexoraSurfaceBorder,
                    focusedTextColor = NexoraTextPrimary,
                    unfocusedTextColor = NexoraTextPrimary,
                    focusedContainerColor = NexoraSurfaceDark,
                    unfocusedContainerColor = NexoraSurfaceDark
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Channel Switches
            SettingSwitchRow(title = "Show subscriber count", isChecked = showSubscriberCount, onCheckedChange = { showSubscriberCount = it })
            SettingSwitchRow(title = "Allow comments", isChecked = allowComments, onCheckedChange = { allowComments = it })
            SettingSwitchRow(title = "Allow sharing", isChecked = allowSharing, onCheckedChange = { allowSharing = it })
            SettingSwitchRow(title = "Show channel in search", isChecked = showInSearch, onCheckedChange = { showInSearch = it })
            SettingSwitchRow(title = "Allow channel recommendations", isChecked = allowRecommendations, onCheckedChange = { allowRecommendations = it })

            Spacer(modifier = Modifier.height(20.dp))

            // ================= UPLOAD DEFAULTS =================
            Text(
                text = "UPLOAD DEFAULTS",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraCyanAccent
            )
            Spacer(modifier = Modifier.height(10.dp))

            DropdownSelectorRow(
                label = "Default Visibility",
                currentValue = defaultVisibility,
                options = listOf("PUBLIC", "UNLISTED", "PRIVATE"),
                onSelected = { defaultVisibility = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownSelectorRow(
                label = "Default Audience",
                currentValue = if (defaultAudience == "MADE_FOR_KIDS") "Made for Kids" else "Not Made for Kids",
                options = listOf("Not Made for Kids", "Made for Kids"),
                onSelected = { defaultAudience = if (it == "Made for Kids") "MADE_FOR_KIDS" else "NOT_FOR_KIDS" }
            )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownSelectorRow(
                label = "Default Comments",
                currentValue = defaultComments,
                options = listOf("ALLOW", "DISABLE"),
                onSelected = { defaultComments = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Buttons: CANCEL and SAVE CHANGES
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = onNavigateBack,
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .testTag("btn_cancel_edit_channel")
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("CANCEL", color = NexoraTextPrimary, fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    onClick = {
                        viewModel.saveChannelChanges(
                            name = name,
                            handle = handle,
                            description = description,
                            profilePicUri = channel?.profilePictureUri,
                            bannerUri = channel?.bannerUri,
                            avatarColorHex = avatarColorHex,
                            bannerGradientIndex = bannerGradientIndex,
                            category = category,
                            language = language,
                            countryRegion = countryRegion,
                            contactEmail = contactEmail,
                            showSubscriberCount = showSubscriberCount,
                            allowComments = allowComments,
                            allowSharing = allowSharing,
                            showInSearch = showInSearch,
                            allowRecommendations = allowRecommendations,
                            defaultVisibility = defaultVisibility,
                            defaultAudience = defaultAudience,
                            defaultCategory = defaultCategory,
                            defaultLanguage = defaultLanguage,
                            defaultComments = defaultComments,
                            socialLinks = socialLinks,
                            onSuccess = onNavigateBack
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraCyanAccent,
                    modifier = Modifier
                        .testTag("btn_bottom_save_channel_changes")
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Dialog: Add/Edit Social Link
    if (showAddLinkDialog) {
        AlertDialog(
            onDismissRequest = { showAddLinkDialog = false },
            containerColor = NexoraSurfaceDark,
            titleContentColor = NexoraTextPrimary,
            textContentColor = NexoraTextSecondary,
            title = { Text("Add Social Link", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    DropdownSelectorRow(
                        label = "Platform",
                        currentValue = linkPlatform,
                        options = listOf("Instagram", "Facebook", "X", "Telegram", "Discord", "Website", "Other"),
                        onSelected = { linkPlatform = it }
                    )
                    OutlinedTextField(
                        value = linkTitle,
                        onValueChange = { linkTitle = it },
                        label = { Text("Link Title (optional)") },
                        placeholder = { Text("e.g. Follow me on X") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        )
                    )
                    OutlinedTextField(
                        value = linkUrl,
                        onValueChange = { linkUrl = it },
                        label = { Text("URL") },
                        placeholder = { Text("https://...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (linkUrl.isNotBlank()) {
                            val newLink = ChannelSocialLink(
                                platform = linkPlatform,
                                title = linkTitle.ifBlank { linkPlatform },
                                url = linkUrl.trim()
                            )
                            socialLinks = socialLinks + newLink
                            showAddLinkDialog = false
                        }
                    }
                ) {
                    Text("Add", color = NexoraCyanAccent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddLinkDialog = false }) {
                    Text("Cancel", color = NexoraTextSecondary)
                }
            }
        )
    }

    // Dialog: Avatar Palette Picker
    if (showAvatarPicker) {
        AlertDialog(
            onDismissRequest = { showAvatarPicker = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Choose Avatar Color", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    AVATAR_COLORS.forEach { hex ->
                        val col = try { Color(android.graphics.Color.parseColor(hex)) } catch (_: Exception) { NexoraCyanAccent }
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(col)
                                .border(
                                    width = if (avatarColorHex == hex) 3.dp else 1.dp,
                                    color = if (avatarColorHex == hex) Color.White else NexoraSurfaceBorder,
                                    shape = CircleShape
                                )
                                .clickable {
                                    avatarColorHex = hex
                                    showAvatarPicker = false
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarPicker = false }) {
                    Text("Close", color = NexoraCyanAccent)
                }
            }
        )
    }

    // Dialog: Banner Palette Picker
    if (showBannerPicker) {
        AlertDialog(
            onDismissRequest = { showBannerPicker = false },
            containerColor = NexoraSurfaceDark,
            title = { Text("Select Banner Theme", color = NexoraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    BANNER_GRADIENTS.forEachIndexed { index, grad ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.horizontalGradient(grad))
                                .border(
                                    width = if (bannerGradientIndex == index) 2.dp else 0.dp,
                                    color = if (bannerGradientIndex == index) NexoraCyanAccent else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    bannerGradientIndex = index
                                    showBannerPicker = false
                                }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBannerPicker = false }) {
                    Text("Done", color = NexoraCyanAccent)
                }
            }
        )
    }
}

@Composable
private fun SettingSwitchRow(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, color = NexoraTextPrimary)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = NexoraCyanAccent,
                uncheckedThumbColor = NexoraTextMuted,
                uncheckedTrackColor = NexoraSurfaceElevated
            )
        )
    }
}

@Composable
private fun DropdownSelectorRow(
    label: String,
    currentValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = NexoraTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            onClick = { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = NexoraSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentValue, style = MaterialTheme.typography.bodyMedium, color = NexoraTextPrimary)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = NexoraTextSecondary)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(NexoraSurfaceElevated)
            ) {
                options.forEach { opt ->
                    DropdownMenuItem(
                        text = { Text(opt, color = NexoraTextPrimary) },
                        onClick = {
                            onSelected(opt)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
