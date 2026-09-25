package com.example.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat.model.ActiveCallSession
import com.example.chat.model.AudienceOption
import com.example.chat.model.CallType
import com.example.chat.model.ChatConversation
import com.example.chat.model.ChatMessage
import com.example.chat.model.MessageType
import com.example.chat.model.PrivacySettings
import com.example.chat.model.RelationshipStatus
import com.example.chat.model.SocialNotification
import com.example.chat.model.SocialUser
import com.example.chat.repository.FriendsRepository
import com.example.chat.repository.SearchResults
import com.example.data.model.UserEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class FriendsTab(val label: String) {
    CHATS("Recent Chats"),
    FRIENDS("Friends"),
    FOLLOWERS("Followers"),
    FOLLOWING("Following"),
    REQUESTS("Requests"),
    SUGGESTED("Suggested")
}

data class FriendsChatUiState(
    val selectedTab: FriendsTab = FriendsTab.CHATS,
    val searchQuery: String = "",
    val isSearching: Boolean = false,
    val searchResults: SearchResults = SearchResults(),
    val selectedFriendProfile: SocialUser? = null,
    val activeConversation: ChatConversation? = null,
    val replyingToMessage: ChatMessage? = null,
    val editingMessage: ChatMessage? = null,
    val isRecordingVoice: Boolean = false,
    val recordingDurationSeconds: Int = 0,
    val showAttachmentSheet: Boolean = false,
    val showPrivacySettings: Boolean = false,
    val showNotificationsSheet: Boolean = false,
    val activeChatSearchQuery: String = "",
    val isSearchingChat: Boolean = false
)

class FriendsChatViewModel(
    currentUser: UserEntity
) : ViewModel() {

    private val repository = FriendsRepository(currentUser)

    private val _uiState = MutableStateFlow(FriendsChatUiState())
    val uiState: StateFlow<FriendsChatUiState> = _uiState.asStateFlow()

    private var voiceRecordingJob: Job? = null

    val allUsers: StateFlow<List<SocialUser>> = repository.allUsers
    val conversations: StateFlow<List<ChatConversation>> = repository.conversations
    val notifications: StateFlow<List<SocialNotification>> = repository.notifications
    val privacySettings: StateFlow<PrivacySettings> = repository.privacySettings
    val activeCall: StateFlow<ActiveCallSession?> = repository.activeCall

    // Derived user subsets for tabs
    val friendsList: StateFlow<List<SocialUser>> = repository.allUsers
        .combine(repository.privacySettings) { users, privacy ->
            users.filter { it.relationshipStatus == RelationshipStatus.FRIENDS && !privacy.blockedUserIds.contains(it.id) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followersList: StateFlow<List<SocialUser>> = repository.allUsers
        .combine(repository.privacySettings) { users, privacy ->
            users.filter { (it.relationshipStatus == RelationshipStatus.FRIENDS || it.relationshipStatus == RelationshipStatus.FOLLOWING) && !privacy.blockedUserIds.contains(it.id) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val followingList: StateFlow<List<SocialUser>> = repository.allUsers
        .combine(repository.privacySettings) { users, privacy ->
            users.filter { (it.relationshipStatus == RelationshipStatus.FOLLOWING || it.relationshipStatus == RelationshipStatus.FRIENDS) && !privacy.blockedUserIds.contains(it.id) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val requestsList: StateFlow<List<SocialUser>> = repository.allUsers
        .combine(repository.privacySettings) { users, privacy ->
            users.filter { it.relationshipStatus == RelationshipStatus.FOLLOW_REQUESTED && !privacy.blockedUserIds.contains(it.id) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val suggestedList: StateFlow<List<SocialUser>> = repository.allUsers
        .combine(repository.privacySettings) { users, privacy ->
            users.filter { it.relationshipStatus == RelationshipStatus.NONE && !privacy.blockedUserIds.contains(it.id) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _privacySettingsFlow = repository.privacySettings

    fun selectTab(tab: FriendsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun setUniversalSearchQuery(query: String) {
        _uiState.update {
            it.copy(
                searchQuery = query,
                isSearching = query.isNotBlank(),
                searchResults = if (query.isNotBlank()) repository.searchUniversal(query) else SearchResults()
            )
        }
    }

    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "", isSearching = false, searchResults = SearchResults()) }
    }

    fun viewFriendProfile(user: SocialUser) {
        _uiState.update { it.copy(selectedFriendProfile = user) }
    }

    fun closeFriendProfile() {
        _uiState.update { it.copy(selectedFriendProfile = null) }
    }

    fun openChatWithUser(user: SocialUser) {
        val conv = repository.getOrCreateConversation(user)
        _uiState.update {
            it.copy(
                activeConversation = conv,
                selectedFriendProfile = null,
                replyingToMessage = null,
                editingMessage = null,
                activeChatSearchQuery = "",
                isSearchingChat = false
            )
        }
    }

    fun openConversation(conversation: ChatConversation) {
        _uiState.update {
            it.copy(
                activeConversation = conversation,
                selectedFriendProfile = null,
                replyingToMessage = null,
                editingMessage = null,
                activeChatSearchQuery = "",
                isSearchingChat = false
            )
        }
    }

    fun closeChat() {
        _uiState.update {
            it.copy(
                activeConversation = null,
                replyingToMessage = null,
                editingMessage = null,
                isRecordingVoice = false,
                showAttachmentSheet = false
            )
        }
        cancelVoiceRecording()
    }

    // Active conversation messages stream
    fun getMessagesForActiveConversation(): StateFlow<List<ChatMessage>> {
        val convId = _uiState.value.activeConversation?.id ?: ""
        return repository.messagesMap
            .combine(_uiState) { map, state ->
                val list = (map[convId] ?: emptyList()).filterNot { it.isDeletedForMe }
                if (state.activeChatSearchQuery.isNotBlank()) {
                    list.filter { it.content.contains(state.activeChatSearchQuery, ignoreCase = true) }
                } else {
                    list
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun sendMessage(text: String) {
        val conv = _uiState.value.activeConversation ?: return
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return

        val editing = _uiState.value.editingMessage
        if (editing != null) {
            repository.editMessage(conv.id, editing.id, trimmed)
            _uiState.update { it.copy(editingMessage = null) }
            return
        }

        val replyTo = _uiState.value.replyingToMessage
        repository.sendMessage(
            conversationId = conv.id,
            receiverId = conv.participantUser.id,
            content = trimmed,
            messageType = MessageType.TEXT,
            replyToMessage = replyTo
        )
        _uiState.update { it.copy(replyingToMessage = null) }
    }

    fun sendMediaAttachment(
        type: MessageType,
        name: String,
        size: String,
        url: String? = null
    ) {
        val conv = _uiState.value.activeConversation ?: return
        repository.sendMessage(
            conversationId = conv.id,
            receiverId = conv.participantUser.id,
            content = when (type) {
                MessageType.IMAGE -> "Photo attachment"
                MessageType.VIDEO -> "Video attachment"
                MessageType.AUDIO -> "Audio file"
                MessageType.FILE -> "Document: $name"
                MessageType.LOCATION -> "Shared Location: 37.7749° N, 122.4194° W"
                MessageType.CONTACT -> "Shared Contact: $name"
                MessageType.STICKER -> "NEXORA Neon Sticker"
                else -> name
            },
            messageType = type,
            mediaName = name,
            mediaSize = size,
            mediaUrl = url
        )
        _uiState.update { it.copy(showAttachmentSheet = false) }
    }

    // Voice recording
    fun startVoiceRecording() {
        _uiState.update { it.copy(isRecordingVoice = true, recordingDurationSeconds = 0) }
        voiceRecordingJob?.cancel()
        voiceRecordingJob = viewModelScope.launch {
            while (isActive && _uiState.value.isRecordingVoice) {
                delay(1000)
                _uiState.update { it.copy(recordingDurationSeconds = it.recordingDurationSeconds + 1) }
            }
        }
    }

    fun cancelVoiceRecording() {
        voiceRecordingJob?.cancel()
        _uiState.update { it.copy(isRecordingVoice = false, recordingDurationSeconds = 0) }
    }

    fun finishAndSendVoiceRecording() {
        voiceRecordingJob?.cancel()
        val duration = _uiState.value.recordingDurationSeconds
        val conv = _uiState.value.activeConversation
        if (conv != null && duration > 0) {
            repository.sendMessage(
                conversationId = conv.id,
                receiverId = conv.participantUser.id,
                content = "Voice note ($duration sec)",
                messageType = MessageType.VOICE,
                voiceDurationSeconds = duration
            )
        }
        _uiState.update { it.copy(isRecordingVoice = false, recordingDurationSeconds = 0) }
    }

    fun setReplyingTo(message: ChatMessage?) {
        _uiState.update { it.copy(replyingToMessage = message, editingMessage = null) }
    }

    fun setEditingMessage(message: ChatMessage?) {
        _uiState.update { it.copy(editingMessage = message, replyingToMessage = null) }
    }

    fun addReaction(message: ChatMessage, emoji: String) {
        val conv = _uiState.value.activeConversation ?: return
        repository.addReaction(conv.id, message.id, emoji)
    }

    fun togglePinMessage(message: ChatMessage) {
        val conv = _uiState.value.activeConversation ?: return
        repository.togglePinMessage(conv.id, message.id)
    }

    fun deleteMessage(message: ChatMessage, forEveryone: Boolean) {
        val conv = _uiState.value.activeConversation ?: return
        repository.deleteMessage(conv.id, message.id, forEveryone)
    }

    fun clearChat() {
        val conv = _uiState.value.activeConversation ?: return
        repository.clearChat(conv.id)
    }

    fun toggleAttachmentSheet(show: Boolean) {
        _uiState.update { it.copy(showAttachmentSheet = show) }
    }

    fun togglePrivacySettings(show: Boolean) {
        _uiState.update { it.copy(showPrivacySettings = show) }
    }

    fun toggleNotificationsSheet(show: Boolean) {
        _uiState.update { it.copy(showNotificationsSheet = show) }
    }

    fun setChatSearchQuery(query: String) {
        _uiState.update { it.copy(activeChatSearchQuery = query, isSearchingChat = query.isNotBlank()) }
    }

    // Call functions
    fun startAudioCall(user: SocialUser) {
        repository.startCall(user, CallType.AUDIO)
    }

    fun startVideoCall(user: SocialUser) {
        repository.startCall(user, CallType.VIDEO)
    }

    fun toggleCallMute() = repository.toggleCallMute()
    fun toggleCallSpeaker() = repository.toggleCallSpeaker()
    fun toggleCallCamera() = repository.toggleCallCamera()
    fun switchCamera() = repository.switchCamera()
    fun endCall() = repository.endCall()

    // Friend actions
    fun followUser(userId: String) = repository.followUser(userId)
    fun unfollowUser(userId: String) = repository.unfollowUser(userId)
    fun acceptFriendRequest(userId: String) = repository.acceptFriendRequest(userId)
    fun declineFriendRequest(userId: String) = repository.declineFriendRequest(userId)
    fun blockUser(userId: String) = repository.blockUser(userId)
    fun reportUser(userId: String, reason: String) = repository.reportUser(userId, reason)

    fun updatePrivacy(
        whoCanMessage: AudienceOption? = null,
        whoCanCall: AudienceOption? = null,
        whoCanRequest: AudienceOption? = null
    ) {
        repository.updatePrivacySettings(whoCanMessage, whoCanCall, whoCanRequest)
    }
}
