package com.example.chat.model

enum class RelationshipStatus {
    NONE,
    FOLLOWING,
    FOLLOW_REQUESTED,
    FRIENDS,
    BLOCKED
}

enum class MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    VOICE,
    LOCATION,
    CONTACT,
    STICKER,
    CALL_LOG
}

enum class DeliveryStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

enum class NotificationType {
    FOLLOW,
    FRIEND_REQUEST,
    REQUEST_ACCEPTED,
    NEW_MESSAGE,
    MISSED_AUDIO_CALL,
    MISSED_VIDEO_CALL
}

enum class AudienceOption(val label: String) {
    EVERYONE("Everyone"),
    FOLLOWERS("Followers"),
    FRIENDS("Friends only"),
    NOBODY("Nobody")
}

data class SocialUser(
    val id: String,
    val name: String,
    val handle: String,
    val bio: String = "",
    val avatarColorHex: String = "#6366F1",
    val avatarUrl: String? = null,
    val isPrivateProfile: Boolean = false,
    val isOnline: Boolean = true,
    val lastActiveText: String = "Online",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val mutualFriendsCount: Int = 0,
    val hasChannel: Boolean = false,
    val channelName: String? = null,
    val channelHandle: String? = null,
    val relationshipStatus: RelationshipStatus = RelationshipStatus.NONE
)

data class ChatMessage(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val receiverId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String,
    val messageType: MessageType = MessageType.TEXT,
    val content: String,
    val mediaUrl: String? = null,
    val mediaName: String? = null,
    val mediaSize: String? = null,
    val uploadProgress: Float? = null,
    val isUploading: Boolean = false,
    val uploadFailed: Boolean = false,
    val voiceDurationSeconds: Int = 0,
    val deliveryStatus: DeliveryStatus = DeliveryStatus.SENT,
    val replyToMessageId: String? = null,
    val replyToContent: String? = null,
    val replyToSenderName: String? = null,
    val reactions: Map<String, Int> = emptyMap(),
    val userReaction: String? = null,
    val isPinned: Boolean = false,
    val isEdited: Boolean = false,
    val isDeletedForMe: Boolean = false,
    val isDeletedForEveryone: Boolean = false
)

data class ChatConversation(
    val id: String,
    val participantUser: SocialUser,
    val lastMessage: ChatMessage? = null,
    val unreadCount: Int = 0,
    val isTyping: Boolean = false,
    val isMuted: Boolean = false,
    val isPinned: Boolean = false,
    // Group Chat Architecture readiness
    val isGroup: Boolean = false,
    val groupName: String? = null,
    val groupAdminId: String? = null,
    val groupMemberCount: Int = 0
)

data class SocialNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actorUser: SocialUser,
    val isRead: Boolean = false
)

data class PrivacySettings(
    val whoCanMessageMe: AudienceOption = AudienceOption.EVERYONE,
    val whoCanCallMe: AudienceOption = AudienceOption.EVERYONE,
    val whoCanSendRequests: AudienceOption = AudienceOption.EVERYONE,
    val blockedUserIds: Set<String> = emptySet(),
    val restrictedUserIds: Set<String> = emptySet()
)

enum class CallType {
    AUDIO,
    VIDEO
}

enum class CallStatus(val label: String) {
    IDLE("Idle"),
    CALLING("Calling..."),
    RINGING("Ringing..."),
    CONNECTED("Connected"),
    DECLINED("Call Declined"),
    MISSED("Missed Call"),
    DISCONNECTED("Call Ended")
}

data class ActiveCallSession(
    val callId: String,
    val callType: CallType,
    val remoteUser: SocialUser,
    val status: CallStatus = CallStatus.CALLING,
    val isMuted: Boolean = false,
    val isSpeakerOn: Boolean = false,
    val isCameraOn: Boolean = true,
    val isFrontCamera: Boolean = true,
    val durationSeconds: Int = 0,
    val isDemoMode: Boolean = true
)
