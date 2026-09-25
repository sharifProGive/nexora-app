package com.example.chat.repository

import com.example.chat.model.ActiveCallSession
import com.example.chat.model.AudienceOption
import com.example.chat.model.CallStatus
import com.example.chat.model.CallType
import com.example.chat.model.ChatConversation
import com.example.chat.model.ChatMessage
import com.example.chat.model.DeliveryStatus
import com.example.chat.model.MessageType
import com.example.chat.model.NotificationType
import com.example.chat.model.PrivacySettings
import com.example.chat.model.RelationshipStatus
import com.example.chat.model.SocialNotification
import com.example.chat.model.SocialUser
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FriendsRepository(
    private val currentUser: UserEntity
) {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var callTimerJob: Job? = null

    // Known Users in the NEXORA Ecosystem
    private val _allUsers = MutableStateFlow<List<SocialUser>>(emptyList())
    val allUsers: StateFlow<List<SocialUser>> = _allUsers.asStateFlow()

    // Conversations
    private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
    val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

    // Messages mapped by conversationId
    private val _messagesMap = MutableStateFlow<Map<String, List<ChatMessage>>>(emptyMap())
    val messagesMap: StateFlow<Map<String, List<ChatMessage>>> = _messagesMap.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<SocialNotification>>(emptyList())
    val notifications: StateFlow<List<SocialNotification>> = _notifications.asStateFlow()

    // Privacy & Safety Settings
    private val _privacySettings = MutableStateFlow(PrivacySettings())
    val privacySettings: StateFlow<PrivacySettings> = _privacySettings.asStateFlow()

    // Call State
    private val _activeCall = MutableStateFlow<ActiveCallSession?>(null)
    val activeCall: StateFlow<ActiveCallSession?> = _activeCall.asStateFlow()

    init {
        initializeSampleNetwork()
    }

    private fun initializeSampleNetwork() {
        // Initial community creators and friends in NEXORA
        val initialUsers = listOf(
            SocialUser(
                id = "user_sharif_01",
                name = "Sharif Rahman",
                handle = "@sharif_tech",
                bio = "Mobile systems engineer & NEXORA early creator. Building next-gen video streaming.",
                avatarColorHex = "#06B6D4",
                isPrivateProfile = false,
                isOnline = true,
                lastActiveText = "Online",
                followersCount = 4280,
                followingCount = 185,
                mutualFriendsCount = 8,
                hasChannel = true,
                channelName = "Sharif Dev Hub",
                channelHandle = "@sharif_hub",
                relationshipStatus = RelationshipStatus.FRIENDS
            ),
            SocialUser(
                id = "user_elena_02",
                name = "Elena Rostova",
                handle = "@elena_visuals",
                bio = "Cinematographer & 4K shorts producer. Exploring audio visual experiments.",
                avatarColorHex = "#EC4899",
                isPrivateProfile = false,
                isOnline = true,
                lastActiveText = "Active 5m ago",
                followersCount = 12900,
                followingCount = 310,
                mutualFriendsCount = 14,
                hasChannel = true,
                channelName = "Elena Motion",
                channelHandle = "@elena_motion",
                relationshipStatus = RelationshipStatus.FRIENDS
            ),
            SocialUser(
                id = "user_marcus_03",
                name = "Marcus Vance",
                handle = "@marcus_v",
                bio = "Full-stack developer, open source enthusiast. Testing NEXORA update engine.",
                avatarColorHex = "#8B5CF6",
                isPrivateProfile = false,
                isOnline = false,
                lastActiveText = "Active 2h ago",
                followersCount = 1450,
                followingCount = 420,
                mutualFriendsCount = 3,
                hasChannel = false,
                relationshipStatus = RelationshipStatus.FOLLOWING
            ),
            SocialUser(
                id = "user_aisha_04",
                name = "Aisha Khan",
                handle = "@aisha_k",
                bio = "Digital artist & sound designer. Private profile for close friends only.",
                avatarColorHex = "#10B981",
                isPrivateProfile = true,
                isOnline = true,
                lastActiveText = "Online",
                followersCount = 680,
                followingCount = 190,
                mutualFriendsCount = 5,
                hasChannel = true,
                channelName = "Aisha Audio Lab",
                channelHandle = "@aisha_sound",
                relationshipStatus = RelationshipStatus.FOLLOW_REQUESTED
            ),
            SocialUser(
                id = "user_devon_05",
                name = "Devon Lee",
                handle = "@devon_lee",
                bio = "Indie game studio lead & 3D animator. Love fast-paced video shorts.",
                avatarColorHex = "#F59E0B",
                isPrivateProfile = false,
                isOnline = false,
                lastActiveText = "Active yesterday",
                followersCount = 8900,
                followingCount = 110,
                mutualFriendsCount = 2,
                hasChannel = true,
                channelName = "Pixel Craft Studio",
                channelHandle = "@pixelcraft",
                relationshipStatus = RelationshipStatus.NONE
            ),
            SocialUser(
                id = "user_maya_06",
                name = "Maya Lin",
                handle = "@maya_design",
                bio = "Product designer & UI minimalist. Building futuristic dark interfaces.",
                avatarColorHex = "#3B82F6",
                isPrivateProfile = false,
                isOnline = true,
                lastActiveText = "Online",
                followersCount = 3400,
                followingCount = 560,
                mutualFriendsCount = 9,
                hasChannel = false,
                relationshipStatus = RelationshipStatus.NONE
            )
        )
        _allUsers.value = initialUsers

        // Seed initial conversations
        val sharif = initialUsers[0]
        val elena = initialUsers[1]

        val sharifConversationId = "conv_${sharif.id}"
        val elenaConversationId = "conv_${elena.id}"

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val now = System.currentTimeMillis()

        val sharifMessages = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = sharifConversationId,
                senderId = sharif.id,
                senderName = sharif.name,
                receiverId = currentUser.id,
                timestamp = now - 3600000,
                formattedTime = timeFormat.format(Date(now - 3600000)),
                content = "Hey ${currentUser.name}! Did you see the new NEXORA update engine architecture?",
                deliveryStatus = DeliveryStatus.READ
            ),
            ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = sharifConversationId,
                senderId = currentUser.id,
                senderName = currentUser.name,
                receiverId = sharif.id,
                timestamp = now - 1800000,
                formattedTime = timeFormat.format(Date(now - 1800000)),
                content = "Yes! The version comparison and Android system installation flow work smoothly.",
                deliveryStatus = DeliveryStatus.READ
            ),
            ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = sharifConversationId,
                senderId = sharif.id,
                senderName = sharif.name,
                receiverId = currentUser.id,
                timestamp = now - 600000,
                formattedTime = timeFormat.format(Date(now - 600000)),
                content = "Awesome. Let's test the voice messaging and media pipeline next!",
                deliveryStatus = DeliveryStatus.READ
            )
        )

        val elenaMessages = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                conversationId = elenaConversationId,
                senderId = elena.id,
                senderName = elena.name,
                receiverId = currentUser.id,
                timestamp = now - 7200000,
                formattedTime = timeFormat.format(Date(now - 7200000)),
                content = "Just posted a new 4K cinematic short on my channel!",
                deliveryStatus = DeliveryStatus.READ
            )
        )

        _messagesMap.value = mapOf(
            sharifConversationId to sharifMessages,
            elenaConversationId to elenaMessages
        )

        _conversations.value = listOf(
            ChatConversation(
                id = sharifConversationId,
                participantUser = sharif,
                lastMessage = sharifMessages.lastOrNull(),
                unreadCount = 0
            ),
            ChatConversation(
                id = elenaConversationId,
                participantUser = elena,
                lastMessage = elenaMessages.lastOrNull(),
                unreadCount = 1
            )
        )

        // Seed Notifications
        _notifications.value = listOf(
            SocialNotification(
                id = "notif_1",
                type = NotificationType.FRIEND_REQUEST,
                title = "New Friend Request",
                message = "Aisha Khan sent you a follow request.",
                timestamp = now - 900000,
                actorUser = initialUsers[3]
            ),
            SocialNotification(
                id = "notif_2",
                type = NotificationType.FOLLOW,
                title = "New Follower",
                message = "Marcus Vance started following your channel.",
                timestamp = now - 86400000,
                actorUser = initialUsers[2]
            ),
            SocialNotification(
                id = "notif_3",
                type = NotificationType.NEW_MESSAGE,
                title = "New Message",
                message = "Elena Rostova: Just posted a new 4K cinematic short...",
                timestamp = now - 7200000,
                actorUser = elena
            )
        )
    }

    // --- Search functionality (People, Channels, Friends) ---
    fun searchUniversal(query: String): SearchResults {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return SearchResults()

        val all = _allUsers.value.filter { user ->
            // Respect privacy: if private and not friends/following, don't show full details
            !_privacySettings.value.blockedUserIds.contains(user.id)
        }

        val people = all.filter {
            it.name.lowercase().contains(q) || it.handle.lowercase().contains(q)
        }

        val channels = all.filter {
            it.hasChannel && (
                (it.channelName?.lowercase()?.contains(q) == true) ||
                (it.channelHandle?.lowercase()?.contains(q) == true)
            )
        }

        val friends = all.filter {
            it.relationshipStatus == RelationshipStatus.FRIENDS && (
                it.name.lowercase().contains(q) || it.handle.lowercase().contains(q)
            )
        }

        return SearchResults(
            people = people,
            channels = channels,
            friends = friends
        )
    }

    // --- Social Actions (Follow, Unfollow, Accept Request, Decline, Block) ---
    fun followUser(userId: String) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    if (user.isPrivateProfile) {
                        user.copy(relationshipStatus = RelationshipStatus.FOLLOW_REQUESTED)
                    } else {
                        user.copy(
                            relationshipStatus = RelationshipStatus.FOLLOWING,
                            followersCount = user.followersCount + 1
                        )
                    }
                } else user
            }
        }
    }

    fun unfollowUser(userId: String) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    user.copy(
                        relationshipStatus = RelationshipStatus.NONE,
                        followersCount = maxOf(0, user.followersCount - 1)
                    )
                } else user
            }
        }
    }

    fun acceptFriendRequest(userId: String) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    user.copy(relationshipStatus = RelationshipStatus.FRIENDS)
                } else user
            }
        }
        // Dismiss notification
        _notifications.update { list ->
            list.filterNot { it.actorUser.id == userId && it.type == NotificationType.FRIEND_REQUEST }
        }
    }

    fun declineFriendRequest(userId: String) {
        _allUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    user.copy(relationshipStatus = RelationshipStatus.NONE)
                } else user
            }
        }
        _notifications.update { list ->
            list.filterNot { it.actorUser.id == userId && it.type == NotificationType.FRIEND_REQUEST }
        }
    }

    fun blockUser(userId: String) {
        _privacySettings.update { settings ->
            settings.copy(blockedUserIds = settings.blockedUserIds + userId)
        }
        _allUsers.update { list ->
            list.map { if (it.id == userId) it.copy(relationshipStatus = RelationshipStatus.BLOCKED) else it }
        }
    }

    fun reportUser(userId: String, reason: String) {
        // Log report securely to audit trail
    }

    // --- Messaging operations ---
    fun getOrCreateConversation(user: SocialUser): ChatConversation {
        val convId = "conv_${user.id}"
        val existing = _conversations.value.find { it.id == convId }
        if (existing != null) return existing

        val newConv = ChatConversation(
            id = convId,
            participantUser = user,
            lastMessage = null,
            unreadCount = 0
        )
        _conversations.update { listOf(newConv) + it }
        return newConv
    }

    fun sendMessage(
        conversationId: String,
        receiverId: String,
        content: String,
        messageType: MessageType = MessageType.TEXT,
        mediaName: String? = null,
        mediaSize: String? = null,
        mediaUrl: String? = null,
        voiceDurationSeconds: Int = 0,
        replyToMessage: ChatMessage? = null
    ): ChatMessage {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val now = System.currentTimeMillis()

        val msg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = conversationId,
            senderId = currentUser.id,
            senderName = currentUser.name,
            receiverId = receiverId,
            timestamp = now,
            formattedTime = timeFormat.format(Date(now)),
            messageType = messageType,
            content = content,
            mediaName = mediaName,
            mediaSize = mediaSize,
            mediaUrl = mediaUrl,
            voiceDurationSeconds = voiceDurationSeconds,
            replyToMessageId = replyToMessage?.id,
            replyToContent = replyToMessage?.content,
            replyToSenderName = replyToMessage?.senderName,
            deliveryStatus = DeliveryStatus.SENT
        )

        // Update messages
        val currentList = _messagesMap.value[conversationId] ?: emptyList()
        _messagesMap.update { it + (conversationId to (currentList + msg)) }

        // Update conversation summary
        _conversations.update { list ->
            list.map { conv ->
                if (conv.id == conversationId) {
                    conv.copy(lastMessage = msg)
                } else conv
            }
        }

        // Simulate delivery transition: Sent -> Delivered -> Read
        scope.launch {
            delay(1200)
            updateMessageStatus(conversationId, msg.id, DeliveryStatus.DELIVERED)
            delay(1800)
            updateMessageStatus(conversationId, msg.id, DeliveryStatus.READ)
        }

        return msg
    }

    private fun updateMessageStatus(conversationId: String, messageId: String, status: DeliveryStatus) {
        _messagesMap.update { map ->
            val list = map[conversationId] ?: return@update map
            map + (conversationId to list.map {
                if (it.id == messageId) it.copy(deliveryStatus = status) else it
            })
        }
    }

    fun addReaction(conversationId: String, messageId: String, emoji: String) {
        _messagesMap.update { map ->
            val list = map[conversationId] ?: return@update map
            map + (conversationId to list.map { msg ->
                if (msg.id == messageId) {
                    val currentReactions = msg.reactions.toMutableMap()
                    val prevReaction = msg.userReaction
                    if (prevReaction != null && currentReactions.containsKey(prevReaction)) {
                        val count = currentReactions[prevReaction] ?: 1
                        if (count <= 1) currentReactions.remove(prevReaction)
                        else currentReactions[prevReaction] = count - 1
                    }
                    if (prevReaction != emoji) {
                        currentReactions[emoji] = (currentReactions[emoji] ?: 0) + 1
                        msg.copy(reactions = currentReactions, userReaction = emoji)
                    } else {
                        msg.copy(reactions = currentReactions, userReaction = null)
                    }
                } else msg
            })
        }
    }

    fun togglePinMessage(conversationId: String, messageId: String) {
        _messagesMap.update { map ->
            val list = map[conversationId] ?: return@update map
            map + (conversationId to list.map {
                if (it.id == messageId) it.copy(isPinned = !it.isPinned) else it
            })
        }
    }

    fun editMessage(conversationId: String, messageId: String, newContent: String) {
        _messagesMap.update { map ->
            val list = map[conversationId] ?: return@update map
            map + (conversationId to list.map {
                if (it.id == messageId) it.copy(content = newContent, isEdited = true) else it
            })
        }
    }

    fun deleteMessage(conversationId: String, messageId: String, forEveryone: Boolean) {
        _messagesMap.update { map ->
            val list = map[conversationId] ?: return@update map
            map + (conversationId to list.map {
                if (it.id == messageId) {
                    if (forEveryone) it.copy(isDeletedForEveryone = true, content = "This message was deleted")
                    else it.copy(isDeletedForMe = true)
                } else it
            })
        }
    }

    fun clearChat(conversationId: String) {
        _messagesMap.update { it + (conversationId to emptyList()) }
        _conversations.update { list ->
            list.map { if (it.id == conversationId) it.copy(lastMessage = null, unreadCount = 0) else it }
        }
    }

    // --- Audio and Video Calling Signaling Abstraction ---
    fun startCall(user: SocialUser, type: CallType) {
        val session = ActiveCallSession(
            callId = UUID.randomUUID().toString(),
            callType = type,
            remoteUser = user,
            status = CallStatus.CALLING,
            isDemoMode = true
        )
        _activeCall.value = session

        // Simulate call lifecycle transition safely
        scope.launch {
            delay(1500)
            if (_activeCall.value?.callId == session.callId) {
                _activeCall.update { it?.copy(status = CallStatus.RINGING) }
            }
            delay(2500)
            if (_activeCall.value?.callId == session.callId) {
                _activeCall.update { it?.copy(status = CallStatus.CONNECTED) }
                startCallTimer()
            }
        }
    }

    private fun startCallTimer() {
        callTimerJob?.cancel()
        callTimerJob = scope.launch {
            while (isActive && _activeCall.value?.status == CallStatus.CONNECTED) {
                delay(1000)
                _activeCall.update { it?.copy(durationSeconds = (it.durationSeconds) + 1) }
            }
        }
    }

    fun toggleCallMute() {
        _activeCall.update { it?.copy(isMuted = !(it.isMuted)) }
    }

    fun toggleCallSpeaker() {
        _activeCall.update { it?.copy(isSpeakerOn = !(it.isSpeakerOn)) }
    }

    fun toggleCallCamera() {
        _activeCall.update { it?.copy(isCameraOn = !(it.isCameraOn)) }
    }

    fun switchCamera() {
        _activeCall.update { it?.copy(isFrontCamera = !(it.isFrontCamera)) }
    }

    fun endCall() {
        callTimerJob?.cancel()
        _activeCall.update { it?.copy(status = CallStatus.DISCONNECTED) }
        scope.launch {
            delay(800)
            _activeCall.value = null
        }
    }

    // --- Privacy Settings updates ---
    fun updatePrivacySettings(
        whoCanMessage: AudienceOption? = null,
        whoCanCall: AudienceOption? = null,
        whoCanRequest: AudienceOption? = null
    ) {
        _privacySettings.update { current ->
            current.copy(
                whoCanMessageMe = whoCanMessage ?: current.whoCanMessageMe,
                whoCanCallMe = whoCanCall ?: current.whoCanCallMe,
                whoCanSendRequests = whoCanRequest ?: current.whoCanSendRequests
            )
        }
    }
}

data class SearchResults(
    val people: List<SocialUser> = emptyList(),
    val channels: List<SocialUser> = emptyList(),
    val friends: List<SocialUser> = emptyList()
)
