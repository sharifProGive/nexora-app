package com.example.creator.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.creator.model.ChannelSocialLink
import com.example.creator.model.SocialLinksJsonHelper
import com.example.creator.model.UploadStatusStep
import com.example.creator.model.VideoEditConfig
import com.example.creator.repository.CreatorRepository
import com.example.data.model.ChannelEntity
import com.example.data.model.CreatorContentEntity
import com.example.data.model.UserEntity
import com.example.security.SecurityUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class CreatorUiState(
    val isLoading: Boolean = false,
    val channel: ChannelEntity? = null,
    val isHandleChecking: Boolean = false,
    val isHandleAvailable: Boolean? = null,
    val handleValidationMessage: String? = null,
    val feedbackMessage: String? = null,
    val errorMessage: String? = null,

    // Upload Execution State
    val uploadStep: UploadStatusStep = UploadStatusStep.IDLE,
    val uploadProgressPercent: Int = 0,
    val uploadStatusMessage: String = "",
    val activeUploadingContent: CreatorContentEntity? = null,
    val lastFailedContent: CreatorContentEntity? = null,

    // Draft / Exit prompt
    val showDraftPrompt: Boolean = false,

    // Edit content details dialog state
    val contentToEdit: CreatorContentEntity? = null,

    // Content Preview player dialog state
    val contentToPreview: CreatorContentEntity? = null
)

class CreatorViewModel(
    private val repository: CreatorRepository,
    private val activeUser: UserEntity
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatorUiState())
    val uiState: StateFlow<CreatorUiState> = _uiState.asStateFlow()

    private var handleCheckJob: Job? = null
    private var uploadJob: Job? = null

    val channel: StateFlow<ChannelEntity?> = repository.observeChannel(activeUser.id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val publishedContent: StateFlow<List<CreatorContentEntity>> = MutableStateFlow(emptyList())

    val draftsList: StateFlow<List<CreatorContentEntity>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            repository.observeChannel(activeUser.id).collect { ch ->
                if (ch != null) {
                    _uiState.update { it.copy(channel = ch) }
                    observeContent(ch.channelId)
                } else {
                    val initial = repository.getOrCreateChannel(activeUser)
                    _uiState.update { it.copy(channel = initial) }
                    observeContent(initial.channelId)
                }
            }
        }
    }

    private fun observeContent(channelId: String) {
        viewModelScope.launch {
            repository.observePublishedContent(channelId).collect { list ->
                (publishedContent as MutableStateFlow).value = list
            }
        }
        viewModelScope.launch {
            repository.observeDrafts(channelId).collect { drafts ->
                (draftsList as MutableStateFlow).value = drafts
            }
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null, errorMessage = null) }
    }

    fun checkHandleAvailability(handle: String) {
        handleCheckJob?.cancel()
        val currentChannel = _uiState.value.channel ?: return
        val clean = SecurityUtils.formatHandle(handle)

        if (clean.equals(currentChannel.handle, ignoreCase = true)) {
            _uiState.update {
                it.copy(
                    isHandleChecking = false,
                    isHandleAvailable = true,
                    handleValidationMessage = "Current channel handle."
                )
            }
            return
        }

        if (clean.length < 3) {
            _uiState.update {
                it.copy(
                    isHandleChecking = false,
                    isHandleAvailable = false,
                    handleValidationMessage = "Handle must be at least 3 characters."
                )
            }
            return
        }

        handleCheckJob = viewModelScope.launch {
            _uiState.update { it.copy(isHandleChecking = true, handleValidationMessage = "Checking availability...") }
            val available = repository.isHandleAvailable(clean, currentChannel.channelId)
            _uiState.update {
                it.copy(
                    isHandleChecking = false,
                    isHandleAvailable = available,
                    handleValidationMessage = if (available) "$clean is available!" else "$clean is already taken."
                )
            }
        }
    }

    fun saveChannelChanges(
        name: String,
        handle: String,
        description: String,
        profilePicUri: String?,
        bannerUri: String?,
        avatarColorHex: String,
        bannerGradientIndex: Int,
        category: String,
        language: String,
        countryRegion: String,
        contactEmail: String,
        showSubscriberCount: Boolean,
        allowComments: Boolean,
        allowSharing: Boolean,
        showInSearch: Boolean,
        allowRecommendations: Boolean,
        defaultVisibility: String,
        defaultAudience: String,
        defaultCategory: String,
        defaultLanguage: String,
        defaultComments: String,
        socialLinks: List<ChannelSocialLink>,
        onSuccess: () -> Unit
    ) {
        val currentChannel = _uiState.value.channel ?: return
        if (name.trim().length < 2) {
            _uiState.update { it.copy(errorMessage = "Channel name must be at least 2 characters.") }
            return
        }

        val cleanHandle = SecurityUtils.formatHandle(handle, preserveCase = true)
        if (cleanHandle.length < 3) {
            _uiState.update { it.copy(errorMessage = "Channel handle must be at least 3 characters.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val updated = currentChannel.copy(
                name = name.trim(),
                handle = cleanHandle,
                description = description.trim(),
                profilePictureUri = profilePicUri,
                bannerUri = bannerUri,
                avatarColorHex = avatarColorHex,
                bannerGradientIndex = bannerGradientIndex,
                category = category,
                language = language,
                countryRegion = countryRegion,
                contactEmail = contactEmail.trim(),
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
                socialLinksJson = SocialLinksJsonHelper.toJson(socialLinks)
            )

            val res = repository.updateChannel(updated)
            res.onSuccess { savedChannel ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        channel = savedChannel,
                        feedbackMessage = "Changes saved successfully."
                    )
                }
                onSuccess()
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to save channel") }
            }
        }
    }

    fun startUpload(
        content: CreatorContentEntity,
        simulateFailure: Boolean = false,
        onSuccess: () -> Unit = {}
    ) {
        uploadJob?.cancel()
        _uiState.update {
            it.copy(
                uploadStep = UploadStatusStep.PREPARING,
                uploadProgressPercent = 0,
                uploadStatusMessage = "Preparing upload...",
                activeUploadingContent = content,
                lastFailedContent = null
            )
        }

        uploadJob = viewModelScope.launch {
            val res = repository.executeUploadProcess(
                content = content,
                simulateFailure = simulateFailure,
                onProgress = { step, percent, msg ->
                    _uiState.update {
                        it.copy(
                            uploadStep = step,
                            uploadProgressPercent = percent,
                            uploadStatusMessage = msg
                        )
                    }
                }
            )

            res.onSuccess { published ->
                _uiState.update {
                    it.copy(
                        uploadStep = UploadStatusStep.COMPLETED,
                        uploadProgressPercent = 100,
                        uploadStatusMessage = "Your ${if (content.contentType == "SHORT") "Short" else "Video"} has been published.",
                        activeUploadingContent = published
                    )
                }
                onSuccess()
            }.onFailure { err ->
                _uiState.update {
                    it.copy(
                        uploadStep = UploadStatusStep.FAILED,
                        uploadStatusMessage = "Upload failed.",
                        lastFailedContent = content
                    )
                }
            }
        }
    }

    fun retryFailedUpload() {
        val failed = _uiState.value.lastFailedContent ?: return
        startUpload(failed, simulateFailure = false)
    }

    fun saveAsDraft(content: CreatorContentEntity, onSaved: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.saveDraft(content)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    uploadStep = UploadStatusStep.IDLE,
                    feedbackMessage = "Draft saved successfully."
                )
            }
            onSaved()
        }
    }

    fun deleteContent(id: String) {
        viewModelScope.launch {
            repository.deleteContent(id)
            _uiState.update { it.copy(feedbackMessage = "Content deleted.") }
        }
    }

    fun updateContentVisibility(id: String, visibility: String) {
        viewModelScope.launch {
            repository.updateContentVisibility(id, visibility)
            _uiState.update { it.copy(feedbackMessage = "Visibility set to $visibility.") }
        }
    }

    fun setContentToEdit(content: CreatorContentEntity?) {
        _uiState.update { it.copy(contentToEdit = content) }
    }

    fun setContentToPreview(content: CreatorContentEntity?) {
        _uiState.update { it.copy(contentToPreview = content) }
    }

    fun clearContentPreview() {
        setContentToPreview(null)
    }

    fun updateContentDetails(
        id: String,
        title: String,
        description: String,
        hashtags: String,
        visibility: String,
        isMadeForKids: Boolean,
        location: String?,
        allowComments: Boolean,
        category: String,
        playlist: String?,
        isAiGenerated: Boolean
    ) {
        viewModelScope.launch {
            repository.updateContentDetails(
                id = id,
                title = title,
                description = description,
                hashtags = hashtags,
                visibility = visibility,
                isMadeForKids = isMadeForKids,
                location = location,
                allowComments = allowComments,
                category = category,
                playlist = playlist,
                isAiGenerated = isAiGenerated
            )
            _uiState.update {
                it.copy(
                    contentToEdit = null,
                    feedbackMessage = "Content details updated successfully."
                )
            }
        }
    }

    fun resetUploadState() {
        _uiState.update {
            it.copy(
                uploadStep = UploadStatusStep.IDLE,
                uploadProgressPercent = 0,
                uploadStatusMessage = "",
                activeUploadingContent = null,
                lastFailedContent = null
            )
        }
    }
}

class CreatorViewModelFactory(
    private val repository: CreatorRepository,
    private val user: UserEntity
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreatorViewModel(repository, user) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
