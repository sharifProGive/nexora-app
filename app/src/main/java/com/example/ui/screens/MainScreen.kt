package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.GraphicEq
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.chat.model.CallType
import com.example.chat.model.NotificationType
import com.example.chat.model.RelationshipStatus
import com.example.chat.ui.AttachmentBottomSheet
import com.example.chat.ui.AudioCallScreen
import com.example.chat.ui.ChatScreen
import com.example.chat.ui.FriendProfileScreen
import com.example.chat.ui.FriendsHomeScreen
import com.example.chat.ui.PrivacySettingsDialog
import com.example.chat.ui.SocialNotificationsSheet
import com.example.chat.ui.VideoCallScreen
import com.example.chat.viewmodel.FriendsChatViewModel
import com.example.creator.repository.CreatorRepository
import com.example.creator.ui.ContentPreviewPlayerDialog
import com.example.creator.ui.CreateLongVideoScreen
import com.example.creator.ui.CreatePostPollScreen
import com.example.creator.ui.CreateShortScreen
import com.example.creator.ui.EditChannelScreen
import com.example.creator.viewmodel.CreatorViewModel
import com.example.data.database.NexoraDatabase
import com.example.data.model.UserEntity
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.ui.theme.NexoraVioletAccent
import com.example.ui.viewmodel.BottomNavTab

enum class MainSubScreen {
    TABS,
    FULL_PROFILE,
    CHANNEL_PAGE,
    EDIT_CHANNEL,
    CREATE_SHORT,
    CREATE_LONG_VIDEO,
    CREATE_POST,
    CREATE_POLL,
    FRIENDS_HOME,
    CHAT_SCREEN,
    FRIEND_PROFILE,
    AUDIO_CALL,
    VIDEO_CALL,
    SETTINGS
}

@Composable
fun MainScreen(
    activeUser: UserEntity,
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onCreateChannel: (channelName: String, channelHandle: String, channelBio: String?) -> Unit,
    onTogglePrivacy: (isPrivate: Boolean) -> Unit,
    onUpdateProfile: (name: String, bio: String, avatarIndex: Int) -> Unit,
    onLogout: () -> Unit,
    installedVersionName: String = "1.0.0",
    installedVersionCode: Int = 1,
    updateCheckResult: com.example.update.model.UpdateCheckResult? = null,
    onCheckForUpdates: () -> Unit = {},
    onOpenUpdateScreen: () -> Unit = {},
    onOpenAdminDialog: () -> Unit = {}
) {
    var showPlusModal by remember { mutableStateOf(false) }
    var currentSubScreen by remember { mutableStateOf(MainSubScreen.TABS) }

    val context = LocalContext.current
    val creatorRepository = remember(context) {
        val db = NexoraDatabase.getInstance(context)
        CreatorRepository(db)
    }
    val creatorViewModel = remember(activeUser.id) {
        CreatorViewModel(creatorRepository, activeUser)
    }
    val creatorUiState by creatorViewModel.uiState.collectAsStateWithLifecycle()

    val friendsViewModel = remember(activeUser.id) { FriendsChatViewModel(activeUser) }
    val chatUiState by friendsViewModel.uiState.collectAsStateWithLifecycle()
    val friendsList by friendsViewModel.friendsList.collectAsStateWithLifecycle()
    val followersList by friendsViewModel.followersList.collectAsStateWithLifecycle()
    val followingList by friendsViewModel.followingList.collectAsStateWithLifecycle()
    val requestsList by friendsViewModel.requestsList.collectAsStateWithLifecycle()
    val suggestedList by friendsViewModel.suggestedList.collectAsStateWithLifecycle()
    val conversationsList by friendsViewModel.conversations.collectAsStateWithLifecycle()
    val notificationsList by friendsViewModel.notifications.collectAsStateWithLifecycle()
    val privacySettings by friendsViewModel.privacySettings.collectAsStateWithLifecycle()
    val activeCallSession by friendsViewModel.activeCall.collectAsStateWithLifecycle()
    val activeMessages by friendsViewModel.getMessagesForActiveConversation().collectAsStateWithLifecycle()

    val hideBottomBar = currentSubScreen in listOf(
        MainSubScreen.CHAT_SCREEN,
        MainSubScreen.AUDIO_CALL,
        MainSubScreen.VIDEO_CALL,
        MainSubScreen.CREATE_SHORT,
        MainSubScreen.CREATE_LONG_VIDEO,
        MainSubScreen.CREATE_POST,
        MainSubScreen.CREATE_POLL,
        MainSubScreen.EDIT_CHANNEL,
        MainSubScreen.SETTINGS
    ) || activeCallSession != null

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (!hideBottomBar) {
                // Fixed Bottom Navigation with exactly 5 items: HOME, SHORTS, +, SUBSCRIPTIONS, YOU
                NexoraBottomNavigation(
                    currentTab = currentTab,
                    onTabSelected = { tab ->
                        if (tab == BottomNavTab.PLUS) {
                            showPlusModal = true
                        } else {
                            currentSubScreen = MainSubScreen.TABS
                            onTabSelected(tab)
                        }
                    },
                    userAvatarColorHex = activeUser.avatarColorHex,
                    userInitial = activeUser.name.firstOrNull()?.toString()?.uppercase() ?: "U"
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (hideBottomBar) 0.dp else innerPadding.calculateBottomPadding())
        ) {
            when {
                // If active call session is ongoing, prioritize call screen
                activeCallSession != null && activeCallSession?.callType == CallType.AUDIO -> {
                    AudioCallScreen(
                        callSession = activeCallSession!!,
                        onToggleMute = { friendsViewModel.toggleCallMute() },
                        onToggleSpeaker = { friendsViewModel.toggleCallSpeaker() },
                        onEndCall = {
                            friendsViewModel.endCall()
                            currentSubScreen = if (chatUiState.activeConversation != null) MainSubScreen.CHAT_SCREEN else MainSubScreen.FRIENDS_HOME
                        }
                    )
                }

                activeCallSession != null && activeCallSession?.callType == CallType.VIDEO -> {
                    VideoCallScreen(
                        callSession = activeCallSession!!,
                        onToggleMute = { friendsViewModel.toggleCallMute() },
                        onToggleSpeaker = { friendsViewModel.toggleCallSpeaker() },
                        onToggleCamera = { friendsViewModel.toggleCallCamera() },
                        onSwitchCamera = { friendsViewModel.switchCamera() },
                        onEndCall = {
                            friendsViewModel.endCall()
                            currentSubScreen = if (chatUiState.activeConversation != null) MainSubScreen.CHAT_SCREEN else MainSubScreen.FRIENDS_HOME
                        }
                    )
                }

                currentSubScreen == MainSubScreen.FULL_PROFILE -> {
                    FullPersonalProfileScreen(
                        user = activeUser,
                        onBack = { currentSubScreen = MainSubScreen.TABS },
                        onEditProfileClick = { /* Handled in profile screen */ }
                    )
                }

                currentSubScreen == MainSubScreen.CHANNEL_PAGE -> {
                    ChannelPageScreen(
                        user = activeUser,
                        viewModel = creatorViewModel,
                        onBackToProfile = { currentSubScreen = MainSubScreen.TABS },
                        onOpenEditChannel = { currentSubScreen = MainSubScreen.EDIT_CHANNEL },
                        onOpenUploadVideo = { currentSubScreen = MainSubScreen.CREATE_LONG_VIDEO },
                        onOpenCreateShort = { currentSubScreen = MainSubScreen.CREATE_SHORT }
                    )
                }

                currentSubScreen == MainSubScreen.EDIT_CHANNEL -> {
                    EditChannelScreen(
                        viewModel = creatorViewModel,
                        onNavigateBack = { currentSubScreen = MainSubScreen.CHANNEL_PAGE }
                    )
                }

                currentSubScreen == MainSubScreen.CREATE_SHORT -> {
                    CreateShortScreen(
                        user = activeUser,
                        viewModel = creatorViewModel,
                        onNavigateBack = { currentSubScreen = MainSubScreen.TABS },
                        onPreviewShort = { content -> creatorViewModel.setContentToPreview(content) }
                    )
                }

                currentSubScreen == MainSubScreen.CREATE_LONG_VIDEO -> {
                    CreateLongVideoScreen(
                        user = activeUser,
                        viewModel = creatorViewModel,
                        onNavigateBack = { currentSubScreen = MainSubScreen.TABS },
                        onPreviewVideo = { content -> creatorViewModel.setContentToPreview(content) }
                    )
                }

                currentSubScreen == MainSubScreen.CREATE_POST -> {
                    CreatePostPollScreen(
                        initialModeIsPoll = false,
                        user = activeUser,
                        viewModel = creatorViewModel,
                        onNavigateBack = { currentSubScreen = MainSubScreen.TABS }
                    )
                }

                currentSubScreen == MainSubScreen.CREATE_POLL -> {
                    CreatePostPollScreen(
                        initialModeIsPoll = true,
                        user = activeUser,
                        viewModel = creatorViewModel,
                        onNavigateBack = { currentSubScreen = MainSubScreen.TABS }
                    )
                }

                currentSubScreen == MainSubScreen.FRIENDS_HOME -> {
                    FriendsHomeScreen(
                        selectedTab = chatUiState.selectedTab,
                        conversations = conversationsList,
                        friends = friendsList,
                        followers = followersList,
                        following = followingList,
                        requests = requestsList,
                        suggested = suggestedList,
                        notificationsCount = notificationsList.count { it.type == NotificationType.FRIEND_REQUEST || it.type == NotificationType.NEW_MESSAGE },
                        searchQuery = chatUiState.searchQuery,
                        isSearching = chatUiState.isSearching,
                        searchResults = chatUiState.searchResults,
                        onTabSelected = { friendsViewModel.selectTab(it) },
                        onSearchQueryChanged = { friendsViewModel.setUniversalSearchQuery(it) },
                        onClearSearch = { friendsViewModel.clearSearch() },
                        onOpenConversation = { conv ->
                            friendsViewModel.openConversation(conv)
                            currentSubScreen = MainSubScreen.CHAT_SCREEN
                        },
                        onOpenUserProfile = { user ->
                            friendsViewModel.viewFriendProfile(user)
                            currentSubScreen = MainSubScreen.FRIEND_PROFILE
                        },
                        onOpenUserChat = { user ->
                            friendsViewModel.openChatWithUser(user)
                            currentSubScreen = MainSubScreen.CHAT_SCREEN
                        },
                        onStartAudioCall = { user ->
                            friendsViewModel.startAudioCall(user)
                            currentSubScreen = MainSubScreen.AUDIO_CALL
                        },
                        onStartVideoCall = { user ->
                            friendsViewModel.startVideoCall(user)
                            currentSubScreen = MainSubScreen.VIDEO_CALL
                        },
                        onFollowToggle = { user ->
                            if (user.relationshipStatus == RelationshipStatus.FOLLOWING || user.relationshipStatus == RelationshipStatus.FRIENDS) {
                                friendsViewModel.unfollowUser(user.id)
                            } else {
                                friendsViewModel.followUser(user.id)
                            }
                        },
                        onAcceptRequest = { friendsViewModel.acceptFriendRequest(it) },
                        onDeclineRequest = { friendsViewModel.declineFriendRequest(it) },
                        onOpenNotifications = { friendsViewModel.toggleNotificationsSheet(true) },
                        onOpenPrivacySettings = { friendsViewModel.togglePrivacySettings(true) },
                        onBack = { currentSubScreen = MainSubScreen.TABS }
                    )
                }

                currentSubScreen == MainSubScreen.FRIEND_PROFILE -> {
                    val targetProfile = chatUiState.selectedFriendProfile
                    if (targetProfile != null) {
                        FriendProfileScreen(
                            user = targetProfile,
                            onBack = {
                                friendsViewModel.closeFriendProfile()
                                currentSubScreen = MainSubScreen.FRIENDS_HOME
                            },
                            onMessageClick = { user ->
                                friendsViewModel.openChatWithUser(user)
                                currentSubScreen = MainSubScreen.CHAT_SCREEN
                            },
                            onAudioCallClick = { user ->
                                friendsViewModel.startAudioCall(user)
                                currentSubScreen = MainSubScreen.AUDIO_CALL
                            },
                            onVideoCallClick = { user ->
                                friendsViewModel.startVideoCall(user)
                                currentSubScreen = MainSubScreen.VIDEO_CALL
                            },
                            onFollowToggle = { user ->
                                if (user.relationshipStatus == RelationshipStatus.FOLLOWING || user.relationshipStatus == RelationshipStatus.FRIENDS) {
                                    friendsViewModel.unfollowUser(user.id)
                                } else {
                                    friendsViewModel.followUser(user.id)
                                }
                            },
                            onBlockUser = { user ->
                                friendsViewModel.blockUser(user.id)
                                friendsViewModel.closeFriendProfile()
                                currentSubScreen = MainSubScreen.FRIENDS_HOME
                            },
                            onReportUser = { user ->
                                friendsViewModel.reportUser(user.id, "Violation")
                            }
                        )
                    } else {
                        currentSubScreen = MainSubScreen.FRIENDS_HOME
                    }
                }

                currentSubScreen == MainSubScreen.CHAT_SCREEN -> {
                    val activeConv = chatUiState.activeConversation
                    if (activeConv != null) {
                        ChatScreen(
                            conversation = activeConv,
                            messages = activeMessages,
                            currentUserId = activeUser.id,
                            replyingTo = chatUiState.replyingToMessage,
                            editingMessage = chatUiState.editingMessage,
                            isRecordingVoice = chatUiState.isRecordingVoice,
                            recordingDurationSeconds = chatUiState.recordingDurationSeconds,
                            searchQuery = chatUiState.activeChatSearchQuery,
                            isSearching = chatUiState.isSearchingChat,
                            onBack = {
                                friendsViewModel.closeChat()
                                currentSubScreen = MainSubScreen.FRIENDS_HOME
                            },
                            onSendMessage = { friendsViewModel.sendMessage(it) },
                            onOpenAttachmentMenu = { friendsViewModel.toggleAttachmentSheet(true) },
                            onStartVoiceRecording = { friendsViewModel.startVoiceRecording() },
                            onCancelVoiceRecording = { friendsViewModel.cancelVoiceRecording() },
                            onFinishVoiceRecording = { friendsViewModel.finishAndSendVoiceRecording() },
                            onSetReplyingTo = { friendsViewModel.setReplyingTo(it) },
                            onSetEditingMessage = { friendsViewModel.setEditingMessage(it) },
                            onAddReaction = { msg, emoji -> friendsViewModel.addReaction(msg, emoji) },
                            onTogglePinMessage = { friendsViewModel.togglePinMessage(it) },
                            onDeleteMessage = { msg, forEveryone -> friendsViewModel.deleteMessage(msg, forEveryone) },
                            onClearChat = { friendsViewModel.clearChat() },
                            onStartAudioCall = { user ->
                                friendsViewModel.startAudioCall(user)
                                currentSubScreen = MainSubScreen.AUDIO_CALL
                            },
                            onStartVideoCall = { user ->
                                friendsViewModel.startVideoCall(user)
                                currentSubScreen = MainSubScreen.VIDEO_CALL
                            },
                            onSetSearchQuery = { friendsViewModel.setChatSearchQuery(it) },
                            onBlockUser = { user ->
                                friendsViewModel.blockUser(user.id)
                                friendsViewModel.closeChat()
                                currentSubScreen = MainSubScreen.FRIENDS_HOME
                            }
                        )
                    } else {
                        currentSubScreen = MainSubScreen.FRIENDS_HOME
                    }
                }

                currentSubScreen == MainSubScreen.AUDIO_CALL -> {
                    if (activeCallSession != null) {
                        AudioCallScreen(
                            callSession = activeCallSession!!,
                            onToggleMute = { friendsViewModel.toggleCallMute() },
                            onToggleSpeaker = { friendsViewModel.toggleCallSpeaker() },
                            onEndCall = {
                                friendsViewModel.endCall()
                                currentSubScreen = if (chatUiState.activeConversation != null) MainSubScreen.CHAT_SCREEN else MainSubScreen.FRIENDS_HOME
                            }
                        )
                    } else {
                        currentSubScreen = if (chatUiState.activeConversation != null) MainSubScreen.CHAT_SCREEN else MainSubScreen.FRIENDS_HOME
                    }
                }

                currentSubScreen == MainSubScreen.VIDEO_CALL -> {
                    if (activeCallSession != null) {
                        VideoCallScreen(
                            callSession = activeCallSession!!,
                            onToggleMute = { friendsViewModel.toggleCallMute() },
                            onToggleSpeaker = { friendsViewModel.toggleCallSpeaker() },
                            onToggleCamera = { friendsViewModel.toggleCallCamera() },
                            onSwitchCamera = { friendsViewModel.switchCamera() },
                            onEndCall = {
                                friendsViewModel.endCall()
                                currentSubScreen = if (chatUiState.activeConversation != null) MainSubScreen.CHAT_SCREEN else MainSubScreen.FRIENDS_HOME
                            }
                        )
                    } else {
                        currentSubScreen = if (chatUiState.activeConversation != null) MainSubScreen.CHAT_SCREEN else MainSubScreen.FRIENDS_HOME
                    }
                }

                currentSubScreen == MainSubScreen.SETTINGS -> {
                    SettingsScreen(
                        user = activeUser,
                        installedVersionName = installedVersionName,
                        installedVersionCode = installedVersionCode,
                        onBack = { currentSubScreen = MainSubScreen.TABS },
                        onOpenPersonalProfile = { currentSubScreen = MainSubScreen.FULL_PROFILE },
                        onOpenPrivacySettings = { friendsViewModel.togglePrivacySettings(true) },
                        onLogout = onLogout
                    )
                }

                currentSubScreen == MainSubScreen.TABS -> {
                    when (currentTab) {
                        BottomNavTab.HOME -> {
                            HomeFeedScreen()
                        }
                        BottomNavTab.SHORTS -> {
                            ShortsScreen()
                        }
                        BottomNavTab.PLUS -> {
                            // Modal takes precedence, fallback to Home
                            HomeFeedScreen()
                        }
                        BottomNavTab.SUBSCRIPTIONS -> {
                            SubscriptionsScreen()
                        }
                        BottomNavTab.YOU -> {
                            YouProfileScreen(
                                user = activeUser,
                                onOpenFullProfile = { currentSubScreen = MainSubScreen.FULL_PROFILE },
                                onOpenChannelPage = { currentSubScreen = MainSubScreen.CHANNEL_PAGE },
                                onOpenEditChannel = { currentSubScreen = MainSubScreen.EDIT_CHANNEL },
                                onCreateChannel = onCreateChannel,
                                onTogglePrivacy = onTogglePrivacy,
                                onUpdateProfile = onUpdateProfile,
                                onLogout = onLogout,
                                installedVersionName = installedVersionName,
                                installedVersionCode = installedVersionCode,
                                updateCheckResult = updateCheckResult,
                                onCheckForUpdates = onCheckForUpdates,
                                onOpenUpdateScreen = onOpenUpdateScreen,
                                onOpenAdminDialog = onOpenAdminDialog,
                                onOpenFriendsChat = { currentSubScreen = MainSubScreen.FRIENDS_HOME },
                                onOpenSettings = { currentSubScreen = MainSubScreen.SETTINGS }
                            )
                        }
                    }
                }
            }
        }
    }

    // Plus Creation Bottom Sheet
    if (showPlusModal) {
        PlusCreateModal(
            user = activeUser,
            onDismiss = { showPlusModal = false },
            onNavigateToCreateChannel = {
                onTabSelected(BottomNavTab.YOU)
                currentSubScreen = MainSubScreen.TABS
            },
            onNavigateToCreateShort = {
                showPlusModal = false
                currentSubScreen = MainSubScreen.CREATE_SHORT
            },
            onNavigateToCreateLongVideo = {
                showPlusModal = false
                currentSubScreen = MainSubScreen.CREATE_LONG_VIDEO
            },
            onNavigateToCreatePost = {
                showPlusModal = false
                currentSubScreen = MainSubScreen.CREATE_POST
            },
            onNavigateToCreatePoll = {
                showPlusModal = false
                currentSubScreen = MainSubScreen.CREATE_POLL
            }
        )
    }

    // Content Preview Player Dialog
    if (creatorUiState.contentToPreview != null) {
        ContentPreviewPlayerDialog(
            content = creatorUiState.contentToPreview!!,
            onDismiss = { creatorViewModel.clearContentPreview() }
        )
    }

    // Attachment Sheet
    if (chatUiState.showAttachmentSheet) {
        AttachmentBottomSheet(
            onDismiss = { friendsViewModel.toggleAttachmentSheet(false) },
            onSendAttachment = { type, name, size, url ->
                friendsViewModel.sendMediaAttachment(type, name, size, url)
            }
        )
    }

    // Privacy Settings Dialog
    if (chatUiState.showPrivacySettings) {
        PrivacySettingsDialog(
            settings = privacySettings,
            onUpdateMessageAudience = { friendsViewModel.updatePrivacy(whoCanMessage = it) },
            onUpdateCallAudience = { friendsViewModel.updatePrivacy(whoCanCall = it) },
            onUpdateRequestAudience = { friendsViewModel.updatePrivacy(whoCanRequest = it) },
            onDismiss = { friendsViewModel.togglePrivacySettings(false) }
        )
    }

    // Social Notifications Sheet
    if (chatUiState.showNotificationsSheet) {
        SocialNotificationsSheet(
            notifications = notificationsList,
            onAcceptRequest = { friendsViewModel.acceptFriendRequest(it) },
            onDeclineRequest = { friendsViewModel.declineFriendRequest(it) },
            onOpenUserChat = { user ->
                friendsViewModel.openChatWithUser(user)
                currentSubScreen = MainSubScreen.CHAT_SCREEN
            },
            onDismiss = { friendsViewModel.toggleNotificationsSheet(false) }
        )
    }
}

@Composable
fun NexoraBottomNavigation(
    currentTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    userAvatarColorHex: String,
    userInitial: String
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Column {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.8.dp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(62.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // 1. HOME
                BottomNavItem(
                    label = "Home",
                    iconFilled = Icons.Filled.Home,
                    iconOutlined = Icons.Outlined.Home,
                    isSelected = currentTab == BottomNavTab.HOME,
                    testTag = "tab_home",
                    onClick = { onTabSelected(BottomNavTab.HOME) }
                )

                // 2. SHORTS
                BottomNavItem(
                    label = "Shorts",
                    iconFilled = Icons.Filled.GraphicEq,
                    iconOutlined = Icons.Outlined.GraphicEq,
                    isSelected = currentTab == BottomNavTab.SHORTS,
                    testTag = "tab_shorts",
                    onClick = { onTabSelected(BottomNavTab.SHORTS) }
                )

                // 3. CENTER PLUS BUTTON (+)
                Box(
                    modifier = Modifier
                        .testTag("tab_plus_button")
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    NexoraIndigoPrimary,
                                    NexoraCyanAccent
                                )
                            )
                        )
                        .border(1.5.dp, NexoraCyanAccent.copy(alpha = 0.6f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(BottomNavTab.PLUS)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create on NEXORA",
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // 4. SUBSCRIPTIONS
                BottomNavItem(
                    label = "Subscriptions",
                    iconFilled = Icons.Filled.Subscriptions,
                    iconOutlined = Icons.Outlined.Subscriptions,
                    isSelected = currentTab == BottomNavTab.SUBSCRIPTIONS,
                    testTag = "tab_subscriptions",
                    onClick = { onTabSelected(BottomNavTab.SUBSCRIPTIONS) }
                )

                // 5. YOU (with avatar initial badge)
                Column(
                    modifier = Modifier
                        .testTag("tab_you")
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onTabSelected(BottomNavTab.YOU)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                if (currentTab == BottomNavTab.YOU) NexoraCyanAccent
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                1.5.dp,
                                if (currentTab == BottomNavTab.YOU) NexoraCyanAccent else Color.Transparent,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userInitial,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (currentTab == BottomNavTab.YOU) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "You",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = if (currentTab == BottomNavTab.YOU) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (currentTab == BottomNavTab.YOU) NexoraCyanAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    iconFilled: ImageVector,
    iconOutlined: ImageVector,
    isSelected: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .testTag(testTag)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) iconFilled else iconOutlined,
            contentDescription = label,
            tint = if (isSelected) NexoraCyanAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) NexoraCyanAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
    }
}
