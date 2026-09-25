package com.example.chat.ui

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chat.model.ChatConversation
import com.example.chat.model.RelationshipStatus
import com.example.chat.model.SocialUser
import com.example.chat.repository.SearchResults
import com.example.chat.viewmodel.FriendsTab
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
fun FriendsHomeScreen(
    selectedTab: FriendsTab,
    conversations: List<ChatConversation>,
    friends: List<SocialUser>,
    followers: List<SocialUser>,
    following: List<SocialUser>,
    requests: List<SocialUser>,
    suggested: List<SocialUser>,
    notificationsCount: Int,
    searchQuery: String,
    isSearching: Boolean,
    searchResults: SearchResults,
    onTabSelected: (FriendsTab) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onClearSearch: () -> Unit,
    onOpenConversation: (ChatConversation) -> Unit,
    onOpenUserProfile: (SocialUser) -> Unit,
    onOpenUserChat: (SocialUser) -> Unit,
    onStartAudioCall: (SocialUser) -> Unit,
    onStartVideoCall: (SocialUser) -> Unit,
    onFollowToggle: (SocialUser) -> Unit,
    onAcceptRequest: (String) -> Unit,
    onDeclineRequest: (String) -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPrivacySettings: () -> Unit,
    onBack: () -> Unit
) {
    var showSearchBar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .testTag("friends_home_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("btn_friends_back")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexoraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "NEXORA Friends",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NexoraTextPrimary
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Search Toggle
                IconButton(
                    onClick = {
                        showSearchBar = !showSearchBar
                        if (!showSearchBar) onClearSearch()
                    },
                    modifier = Modifier.testTag("btn_friends_search_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Friends & Channels",
                        tint = if (showSearchBar) NexoraCyanAccent else NexoraTextSecondary
                    )
                }

                // Social Notifications
                IconButton(
                    onClick = onOpenNotifications,
                    modifier = Modifier.testTag("btn_friends_notifications")
                ) {
                    BadgedBox(
                        badge = {
                            if (notificationsCount > 0) {
                                Badge(
                                    containerColor = NexoraCyanAccent,
                                    contentColor = Color.Black
                                ) {
                                    Text(
                                        text = "$notificationsCount",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = NexoraTextSecondary
                        )
                    }
                }

                // Privacy Settings
                IconButton(
                    onClick = onOpenPrivacySettings,
                    modifier = Modifier.testTag("btn_friends_privacy")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Privacy & Safety",
                        tint = NexoraTextSecondary
                    )
                }
            }
        }

        // Search Bar (Universal NEXORA search format)
        if (showSearchBar) {
            Surface(
                color = NexoraSurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        placeholder = {
                            Text(
                                text = "Search People, Channels, Friends...",
                                fontSize = 13.sp,
                                color = NexoraTextMuted
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        ),
                        modifier = Modifier
                            .testTag("input_friends_search")
                            .weight(1f)
                    )
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = NexoraTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // If searching, show Universal Search Results directly
        if (isSearching) {
            UniversalSearchResultsView(
                results = searchResults,
                query = searchQuery,
                onOpenProfile = onOpenUserProfile,
                onOpenChat = onOpenUserChat,
                onFollowToggle = onFollowToggle
            )
        } else {
            // Main Section Tabs
            val tabs = FriendsTab.values().toList()
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = NexoraDarkBackground,
                contentColor = NexoraTextPrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                        color = NexoraCyanAccent
                    )
                },
                divider = { HorizontalDivider(color = NexoraSurfaceBorder) }
            ) {
                tabs.forEach { tab ->
                    val badgeCount = when (tab) {
                        FriendsTab.REQUESTS -> requests.size
                        FriendsTab.CHATS -> conversations.sumOf { it.unreadCount }
                        else -> 0
                    }

                    Tab(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (selectedTab == tab) NexoraCyanAccent else NexoraTextSecondary
                                )
                                if (badgeCount > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = NexoraCyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "$badgeCount",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }

            // Tab Content
            when (selectedTab) {
                FriendsTab.CHATS -> {
                    RecentChatsList(
                        conversations = conversations,
                        onOpenConversation = onOpenConversation,
                        onOpenProfile = onOpenUserProfile
                    )
                }
                FriendsTab.FRIENDS -> {
                    FriendsList(
                        friends = friends,
                        onOpenProfile = onOpenUserProfile,
                        onOpenChat = onOpenUserChat,
                        onStartAudioCall = onStartAudioCall,
                        onStartVideoCall = onStartVideoCall
                    )
                }
                FriendsTab.FOLLOWERS -> {
                    FollowersList(
                        followers = followers,
                        onOpenProfile = onOpenUserProfile,
                        onOpenChat = onOpenUserChat,
                        onFollowToggle = onFollowToggle
                    )
                }
                FriendsTab.FOLLOWING -> {
                    FollowingList(
                        following = following,
                        onOpenProfile = onOpenUserProfile,
                        onOpenChat = onOpenUserChat,
                        onFollowToggle = onFollowToggle
                    )
                }
                FriendsTab.REQUESTS -> {
                    RequestsList(
                        requests = requests,
                        onAccept = onAcceptRequest,
                        onDecline = onDeclineRequest,
                        onOpenProfile = onOpenUserProfile
                    )
                }
                FriendsTab.SUGGESTED -> {
                    SuggestedList(
                        suggested = suggested,
                        onOpenProfile = onOpenUserProfile,
                        onFollow = onFollowToggle
                    )
                }
            }
        }
    }
}

// ==========================================
// UNIVERSAL SEARCH RESULTS VIEW
// ==========================================
@Composable
private fun UniversalSearchResultsView(
    results: SearchResults,
    query: String,
    onOpenProfile: (SocialUser) -> Unit,
    onOpenChat: (SocialUser) -> Unit,
    onFollowToggle: (SocialUser) -> Unit
) {
    val totalCount = results.people.size + results.channels.size + results.friends.size

    if (totalCount == 0) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = NexoraTextMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "No results for \"$query\"",
                    style = MaterialTheme.typography.titleMedium,
                    color = NexoraTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Try searching by personal name, @handle, or video channel name.",
                    style = MaterialTheme.typography.bodySmall,
                    color = NexoraTextMuted
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }

            // PEOPLE SECTION
            if (results.people.isNotEmpty()) {
                item {
                    Text(
                        text = "PEOPLE (${results.people.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NexoraCyanAccent,
                        letterSpacing = 1.sp
                    )
                }
                items(results.people) { person ->
                    UserCard(
                        user = person,
                        onOpenProfile = { onOpenProfile(person) },
                        onOpenChat = { onOpenChat(person) },
                        onFollowToggle = { onFollowToggle(person) }
                    )
                }
            }

            // CHANNELS SECTION
            if (results.channels.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CHANNELS (${results.channels.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NexoraIndigoLight,
                        letterSpacing = 1.sp
                    )
                }
                items(results.channels) { userWithChannel ->
                    ChannelSearchCard(
                        user = userWithChannel,
                        onOpenProfile = { onOpenProfile(userWithChannel) }
                    )
                }
            }

            // FRIENDS SECTION
            if (results.friends.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "FRIENDS (${results.friends.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        letterSpacing = 1.sp
                    )
                }
                items(results.friends) { friend ->
                    UserCard(
                        user = friend,
                        onOpenProfile = { onOpenProfile(friend) },
                        onOpenChat = { onOpenChat(friend) },
                        onFollowToggle = { onFollowToggle(friend) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// RECENT CHATS LIST
// ==========================================
@Composable
private fun RecentChatsList(
    conversations: List<ChatConversation>,
    onOpenConversation: (ChatConversation) -> Unit,
    onOpenProfile: (SocialUser) -> Unit
) {
    if (conversations.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.Forum,
            title = "No recent chats",
            subtitle = "Select a friend from the Friends tab or search for creators to start chatting."
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }
            items(conversations) { conv ->
                val friend = conv.participantUser
                val avatarColor = remember(friend.avatarColorHex) {
                    try {
                        Color(android.graphics.Color.parseColor(friend.avatarColorHex))
                    } catch (_: Exception) {
                        NexoraIndigoPrimary
                    }
                }

                Surface(
                    onClick = { onOpenConversation(conv) },
                    shape = RoundedCornerShape(14.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .testTag("chat_item_${friend.id}")
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar with online status
                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.clickable { onOpenProfile(friend) }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(avatarColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = friend.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                            }
                            if (friend.isOnline) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF10B981))
                                        .border(2.dp, NexoraSurfaceDark, CircleShape)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = friend.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraTextPrimary
                                )
                                Text(
                                    text = conv.lastMessage?.formattedTime ?: "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraTextMuted
                                )
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = conv.lastMessage?.content ?: "Start conversation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (conv.unreadCount > 0) NexoraTextPrimary else NexoraTextSecondary,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f)
                                )

                                if (conv.unreadCount > 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = CircleShape,
                                        color = NexoraCyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "${conv.unreadCount}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// FRIENDS LIST
// ==========================================
@Composable
private fun FriendsList(
    friends: List<SocialUser>,
    onOpenProfile: (SocialUser) -> Unit,
    onOpenChat: (SocialUser) -> Unit,
    onStartAudioCall: (SocialUser) -> Unit,
    onStartVideoCall: (SocialUser) -> Unit
) {
    if (friends.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.People,
            title = "No friends yet",
            subtitle = "Search for people or channels to connect with on NEXORA."
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }
            items(friends) { friend ->
                UserCard(
                    user = friend,
                    onOpenProfile = { onOpenProfile(friend) },
                    onOpenChat = { onOpenChat(friend) },
                    extraActions = {
                        IconButton(
                            onClick = { onStartAudioCall(friend) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = NexoraTextSecondary)
                        }
                        IconButton(
                            onClick = { onStartVideoCall(friend) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Videocam, contentDescription = "Video Call", tint = NexoraCyanAccent)
                        }
                    }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// FOLLOWERS LIST
// ==========================================
@Composable
private fun FollowersList(
    followers: List<SocialUser>,
    onOpenProfile: (SocialUser) -> Unit,
    onOpenChat: (SocialUser) -> Unit,
    onFollowToggle: (SocialUser) -> Unit
) {
    if (followers.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.PersonAdd,
            title = "No followers yet",
            subtitle = "When people follow your profile or channel, they will appear here."
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }
            items(followers) { user ->
                UserCard(
                    user = user,
                    onOpenProfile = { onOpenProfile(user) },
                    onOpenChat = { onOpenChat(user) },
                    onFollowToggle = { onFollowToggle(user) }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// FOLLOWING LIST
// ==========================================
@Composable
private fun FollowingList(
    following: List<SocialUser>,
    onOpenProfile: (SocialUser) -> Unit,
    onOpenChat: (SocialUser) -> Unit,
    onFollowToggle: (SocialUser) -> Unit
) {
    if (following.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.PersonAdd,
            title = "Not following anyone yet",
            subtitle = "Discover interesting creators, friends, and channels to follow."
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }
            items(following) { user ->
                UserCard(
                    user = user,
                    onOpenProfile = { onOpenProfile(user) },
                    onOpenChat = { onOpenChat(user) },
                    onFollowToggle = { onFollowToggle(user) }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// REQUESTS LIST
// ==========================================
@Composable
private fun RequestsList(
    requests: List<SocialUser>,
    onAccept: (String) -> Unit,
    onDecline: (String) -> Unit,
    onOpenProfile: (SocialUser) -> Unit
) {
    if (requests.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.PersonAdd,
            title = "No pending requests",
            subtitle = "You have answered all incoming friend requests."
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }
            items(requests) { user ->
                Surface(
                    onClick = { onOpenProfile(user) },
                    shape = RoundedCornerShape(14.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(NexoraIndigoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = user.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = NexoraTextPrimary
                                )
                                Text(
                                    text = "${user.handle} • ${user.mutualFriendsCount} mutual friends",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NexoraTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                onClick = { onAccept(user.id) },
                                shape = RoundedCornerShape(10.dp),
                                color = NexoraCyanAccent,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Confirm",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.Black
                                    )
                                }
                            }

                            Surface(
                                onClick = { onDecline(user.id) },
                                shape = RoundedCornerShape(10.dp),
                                color = NexoraSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Delete",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = NexoraTextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// SUGGESTED PEOPLE LIST
// ==========================================
@Composable
private fun SuggestedList(
    suggested: List<SocialUser>,
    onOpenProfile: (SocialUser) -> Unit,
    onFollow: (SocialUser) -> Unit
) {
    if (suggested.isEmpty()) {
        EmptyStateView(
            icon = Icons.Default.People,
            title = "No suggestions",
            subtitle = "You are currently connected with everyone nearby."
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(6.dp)) }
            items(suggested) { user ->
                UserCard(
                    user = user,
                    onOpenProfile = { onOpenProfile(user) },
                    onOpenChat = { onOpenProfile(user) },
                    onFollowToggle = { onFollow(user) }
                )
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

// ==========================================
// GENERIC USER CARD
// ==========================================
@Composable
private fun UserCard(
    user: SocialUser,
    onOpenProfile: () -> Unit,
    onOpenChat: () -> Unit,
    onFollowToggle: (() -> Unit)? = null,
    extraActions: (@Composable () -> Unit)? = null
) {
    val avatarColor = remember(user.avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(user.avatarColorHex))
        } catch (_: Exception) {
            NexoraIndigoPrimary
        }
    }

    Surface(
        onClick = onOpenProfile,
        shape = RoundedCornerShape(14.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier
            .testTag("user_card_${user.id}")
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = user.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
                if (user.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(2.dp, NexoraSurfaceDark, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    if (user.isPrivateProfile) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Private Profile",
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Text(
                    text = "${user.handle} • ${user.followersCount} followers",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextSecondary
                )
            }

            // Quick actions
            if (extraActions != null) {
                extraActions()
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Message Button
            Surface(
                onClick = onOpenChat,
                shape = CircleShape,
                color = NexoraCyanAccent.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.3f)),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Message",
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// CHANNEL SEARCH CARD
// ==========================================
@Composable
private fun ChannelSearchCard(
    user: SocialUser,
    onOpenProfile: () -> Unit
) {
    Surface(
        onClick = onOpenProfile,
        shape = RoundedCornerShape(14.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraIndigoLight.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(NexoraIndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartDisplay,
                    contentDescription = null,
                    tint = NexoraIndigoLight,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.channelName ?: "Channel",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Text(
                    text = "Owner: ${user.name}" + (if (!user.channelHandle.isNullOrBlank()) " • ${user.channelHandle}" else ""),
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraIndigoLight
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NexoraSurfaceElevated
            ) {
                Text(
                    text = "Channel",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NexoraCyanAccent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ==========================================
// EMPTY STATE COMPONENT
// ==========================================
@Composable
private fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(NexoraSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = NexoraTextMuted,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 18.sp
            )
        }
    }
}
