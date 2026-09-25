package com.example.chat.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.InsertEmoticon
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.chat.model.ChatConversation
import com.example.chat.model.ChatMessage
import com.example.chat.model.DeliveryStatus
import com.example.chat.model.MessageType
import com.example.chat.model.SocialUser
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
import com.example.ui.theme.NexoraVioletAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    conversation: ChatConversation,
    messages: List<ChatMessage>,
    currentUserId: String,
    replyingTo: ChatMessage?,
    editingMessage: ChatMessage?,
    isRecordingVoice: Boolean,
    recordingDurationSeconds: Int,
    searchQuery: String,
    isSearching: Boolean,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenAttachmentMenu: () -> Unit,
    onStartVoiceRecording: () -> Unit,
    onCancelVoiceRecording: () -> Unit,
    onFinishVoiceRecording: () -> Unit,
    onSetReplyingTo: (ChatMessage?) -> Unit,
    onSetEditingMessage: (ChatMessage?) -> Unit,
    onAddReaction: (ChatMessage, String) -> Unit,
    onTogglePinMessage: (ChatMessage) -> Unit,
    onDeleteMessage: (ChatMessage, Boolean) -> Unit,
    onClearChat: () -> Unit,
    onStartAudioCall: (SocialUser) -> Unit,
    onStartVideoCall: (SocialUser) -> Unit,
    onSetSearchQuery: (String) -> Unit,
    onBlockUser: (SocialUser) -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var inputText by remember { mutableStateOf("") }
    var showMoreMenu by remember { mutableStateOf(false) }
    var selectedMessageForAction by remember { mutableStateOf<ChatMessage?>(null) }
    var showEmojiQuickBar by remember { mutableStateOf(false) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onStartVoiceRecording()
        }
    }

    // Populate input when editing
    LaunchedEffect(editingMessage) {
        if (editingMessage != null) {
            inputText = editingMessage.content
        }
    }

    // Scroll to bottom when message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val pinnedMessage = remember(messages) {
        messages.lastOrNull { it.isPinned }
    }

    val friend = conversation.participantUser
    val avatarColor = remember(friend.avatarColorHex) {
        try {
            Color(android.graphics.Color.parseColor(friend.avatarColorHex))
        } catch (_: Exception) {
            NexoraIndigoPrimary
        }
    }

    Column(
        modifier = Modifier
            .testTag("chat_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NexoraSurfaceDark)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_chat_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NexoraTextPrimary
                )
            }

            // Avatar with Online dot
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = friend.name.firstOrNull()?.toString()?.uppercase() ?: "U",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (friend.isOnline) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .border(1.5.dp, NexoraSurfaceDark, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = friend.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Text(
                    text = if (conversation.isTyping) "typing..." else friend.lastActiveText,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (conversation.isTyping) NexoraCyanAccent else NexoraTextMuted
                )
            }

            // Audio Call Icon Button
            IconButton(
                onClick = { onStartAudioCall(friend) },
                modifier = Modifier.testTag("btn_chat_audio_call")
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Audio Call",
                    tint = NexoraTextPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Video Call Icon Button
            IconButton(
                onClick = { onStartVideoCall(friend) },
                modifier = Modifier.testTag("btn_chat_video_call")
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Video Call",
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(24.dp)
                )
            }

            // More Options
            Box {
                IconButton(onClick = { showMoreMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = NexoraTextSecondary
                    )
                }

                DropdownMenu(
                    expanded = showMoreMenu,
                    onDismissRequest = { showMoreMenu = false },
                    modifier = Modifier.background(NexoraSurfaceDark)
                ) {
                    DropdownMenuItem(
                        text = { Text("Search Messages", color = NexoraTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NexoraCyanAccent) },
                        onClick = {
                            showMoreMenu = false
                            onSetSearchQuery(if (isSearching) "" else " ")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Clear Chat", color = NexoraTextPrimary) },
                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = NexoraTextMuted) },
                        onClick = {
                            showMoreMenu = false
                            onClearChat()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Block User", color = Color(0xFFEF4444)) },
                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = Color(0xFFEF4444)) },
                        onClick = {
                            showMoreMenu = false
                            onBlockUser(friend)
                        }
                    )
                }
            }
        }

        HorizontalDivider(color = NexoraSurfaceBorder)

        // In-Chat Search Bar
        AnimatedVisibility(visible = isSearching) {
            Surface(
                color = NexoraSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = NexoraCyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSetSearchQuery,
                        placeholder = { Text("Search in this conversation...", color = NexoraTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = NexoraTextPrimary,
                            unfocusedTextColor = NexoraTextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { onSetSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Close search", tint = NexoraTextMuted)
                    }
                }
            }
        }

        // Pinned Message Banner
        if (pinnedMessage != null) {
            Surface(
                color = NexoraSurfaceElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Pinned Message", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NexoraCyanAccent)
                        Text(text = pinnedMessage.content, fontSize = 12.sp, color = NexoraTextSecondary, maxLines = 1)
                    }
                    IconButton(onClick = { onTogglePinMessage(pinnedMessage) }) {
                        Icon(Icons.Default.Close, contentDescription = "Unpin", tint = NexoraTextMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(10.dp)) }

            items(messages, key = { it.id }) { msg ->
                val isMe = msg.senderId == currentUserId
                MessageBubble(
                    message = msg,
                    isMe = isMe,
                    onLongClick = { selectedMessageForAction = msg },
                    onReplyClick = { onSetReplyingTo(msg) }
                )
            }

            item { Spacer(modifier = Modifier.height(10.dp)) }
        }

        // Quick Emoji Picker Bar
        AnimatedVisibility(visible = showEmojiQuickBar) {
            Surface(
                color = NexoraSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("👍", "❤️", "🔥", "😂", "🎉", "😮", "🚀", "👏").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 24.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    val lastMsg = messages.lastOrNull()
                                    if (lastMsg != null) {
                                        onAddReaction(lastMsg, emoji)
                                    } else {
                                        inputText += emoji
                                    }
                                    showEmojiQuickBar = false
                                }
                                .padding(6.dp)
                        )
                    }
                }
            }
        }

        // Replying to message preview banner
        if (replyingTo != null) {
            Surface(
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Reply,
                        contentDescription = "Replying",
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Replying to ${replyingTo.senderName}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NexoraCyanAccent
                        )
                        Text(
                            text = replyingTo.content,
                            fontSize = 12.sp,
                            color = NexoraTextSecondary,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { onSetReplyingTo(null) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel reply",
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Editing message banner
        if (editingMessage != null) {
            Surface(
                color = NexoraSurfaceDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editing",
                        tint = NexoraIndigoLight,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Editing message",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NexoraIndigoLight,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        onSetEditingMessage(null)
                        inputText = ""
                    }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel edit",
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Active Voice Recording Bar
        if (isRecordingVoice) {
            Surface(
                color = NexoraSurfaceDark,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF4444))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Recording Voice: %02d:%02d".format(
                                recordingDurationSeconds / 60,
                                recordingDurationSeconds % 60
                            ),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                    }

                    Row {
                        IconButton(onClick = onCancelVoiceRecording) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel recording",
                                tint = NexoraTextMuted
                            )
                        }

                        IconButton(onClick = onFinishVoiceRecording) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send voice note",
                                tint = NexoraCyanAccent
                            )
                        }
                    }
                }
            }
        } else {
            // Standard Message Composer Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NexoraSurfaceDark)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attachment Button
                IconButton(
                    onClick = onOpenAttachmentMenu,
                    modifier = Modifier.testTag("btn_chat_attachment")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Attach",
                        tint = NexoraCyanAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Text Input Field
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "Type a message...",
                            fontSize = 14.sp,
                            color = NexoraTextMuted
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = NexoraDarkBackground,
                        unfocusedContainerColor = NexoraDarkBackground,
                        focusedBorderColor = NexoraCyanAccent.copy(alpha = 0.5f),
                        unfocusedBorderColor = NexoraSurfaceBorder,
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    trailingIcon = {
                        IconButton(onClick = { showEmojiQuickBar = !showEmojiQuickBar }) {
                            Icon(
                                imageVector = Icons.Default.InsertEmoticon,
                                contentDescription = "Emoji",
                                tint = NexoraTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .testTag("input_chat_message")
                        .weight(1f)
                        .height(50.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Send or Voice Record Action Button
                if (inputText.isNotBlank()) {
                    Surface(
                        onClick = {
                            onSendMessage(inputText)
                            inputText = ""
                        },
                        shape = CircleShape,
                        color = NexoraCyanAccent,
                        modifier = Modifier
                            .testTag("btn_chat_send")
                            .size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else {
                    Surface(
                        onClick = {
                            val hasMic = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED
                            if (hasMic) {
                                onStartVoiceRecording()
                            } else {
                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        },
                        shape = CircleShape,
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier
                            .testTag("btn_chat_voice_record")
                            .size(46.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Record Voice Message",
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Message Actions Bottom Sheet (Reply, React, Copy, Pin, Delete)
    if (selectedMessageForAction != null) {
        val activeMsg = selectedMessageForAction!!
        val isMe = activeMsg.senderId == currentUserId

        ModalBottomSheet(
            onDismissRequest = { selectedMessageForAction = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = NexoraSurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp)
                    .navigationBarsPadding()
            ) {
                // Reaction Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("👍", "❤️", "🔥", "😂", "🎉", "😮").forEach { emoji ->
                        Text(
                            text = emoji,
                            fontSize = 28.sp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    onAddReaction(activeMsg, emoji)
                                    selectedMessageForAction = null
                                }
                                .padding(6.dp)
                        )
                    }
                }

                HorizontalDivider(color = NexoraSurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Actions: Reply
                MessageActionItem(
                    icon = Icons.Default.Reply,
                    title = "Reply",
                    onClick = {
                        onSetReplyingTo(activeMsg)
                        selectedMessageForAction = null
                    }
                )

                // Actions: Copy
                MessageActionItem(
                    icon = Icons.Default.ContentCopy,
                    title = "Copy Text",
                    onClick = {
                        clipboardManager.setText(AnnotatedString(activeMsg.content))
                        selectedMessageForAction = null
                    }
                )

                // Actions: Pin
                MessageActionItem(
                    icon = Icons.Default.PushPin,
                    title = if (activeMsg.isPinned) "Unpin Message" else "Pin Message",
                    onClick = {
                        onTogglePinMessage(activeMsg)
                        selectedMessageForAction = null
                    }
                )

                // Actions: Edit (if sent by me and text)
                if (isMe && activeMsg.messageType == MessageType.TEXT && !activeMsg.isDeletedForEveryone) {
                    MessageActionItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Message",
                        onClick = {
                            onSetEditingMessage(activeMsg)
                            selectedMessageForAction = null
                        }
                    )
                }

                // Actions: Delete for Me
                MessageActionItem(
                    icon = Icons.Default.Delete,
                    title = "Delete for Me",
                    color = NexoraTextSecondary,
                    onClick = {
                        onDeleteMessage(activeMsg, false)
                        selectedMessageForAction = null
                    }
                )

                // Actions: Delete for Everyone (if sent by me)
                if (isMe && !activeMsg.isDeletedForEveryone) {
                    MessageActionItem(
                        icon = Icons.Default.Delete,
                        title = "Delete for Everyone",
                        color = Color(0xFFEF4444),
                        onClick = {
                            onDeleteMessage(activeMsg, true)
                            selectedMessageForAction = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color = NexoraTextPrimary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMe: Boolean,
    onLongClick: () -> Unit,
    onReplyClick: () -> Unit
) {
    var isPlayingVoice by remember { mutableStateOf(false) }

    val bubbleColor = if (isMe) {
        NexoraIndigoPrimary
    } else {
        NexoraSurfaceElevated
    }

    val bubbleShape = if (isMe) {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = bubbleShape,
            color = bubbleColor,
            border = if (!isMe) androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder) else null,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .combinedClickable(
                    onClick = {
                        if (message.messageType == MessageType.VOICE) {
                            isPlayingVoice = !isPlayingVoice
                        }
                    },
                    onLongClick = onLongClick
                )
        ) {
            Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                // Reply quote banner
                if (message.replyToContent != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.25f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clickable { onReplyClick() }
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Text(
                                text = message.replyToSenderName ?: "Reply",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NexoraCyanAccent
                            )
                            Text(
                                text = message.replyToContent,
                                fontSize = 11.sp,
                                color = NexoraTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Message Type Specific Content
                when (message.messageType) {
                    MessageType.TEXT -> {
                        Text(
                            text = message.content,
                            fontSize = 14.sp,
                            color = if (message.isDeletedForEveryone) NexoraTextMuted else Color.White
                        )
                    }

                    MessageType.VOICE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isMe) NexoraCyanAccent else NexoraIndigoPrimary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isPlayingVoice) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play voice note",
                                        tint = if (isMe) Color.Black else Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                // Waveform simulation
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    listOf(10, 18, 14, 22, 16, 26, 12, 20, 15, 8).forEach { barHeight ->
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(barHeight.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(if (isPlayingVoice) NexoraCyanAccent else NexoraTextMuted)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "%02d:%02d".format(
                                        message.voiceDurationSeconds / 60,
                                        message.voiceDurationSeconds % 60
                                    ),
                                    fontSize = 10.sp,
                                    color = NexoraTextSecondary
                                )
                            }
                        }
                    }

                    MessageType.IMAGE -> {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = NexoraCyanAccent,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = message.mediaName ?: "Photo", fontSize = 12.sp, color = Color.White)
                                    Text(text = message.mediaSize ?: "", fontSize = 10.sp, color = NexoraTextMuted)
                                }
                            }
                        }
                    }

                    MessageType.FILE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF59E0B).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = message.mediaName ?: "Document",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = message.mediaSize ?: "PDF",
                                    fontSize = 11.sp,
                                    color = NexoraTextSecondary
                                )
                            }
                        }
                    }

                    MessageType.AUDIO -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NexoraVioletAccent.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Audiotrack,
                                    contentDescription = null,
                                    tint = NexoraVioletAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = message.mediaName ?: "Audio File", fontSize = 13.sp, color = Color.White)
                                Text(text = message.mediaSize ?: "", fontSize = 10.sp, color = NexoraTextMuted)
                            }
                        }
                    }

                    else -> {
                        Text(text = message.content, fontSize = 14.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Time & Status Indicators
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (message.isEdited) {
                        Text(text = "edited", fontSize = 9.sp, color = NexoraTextMuted)
                    }

                    Text(
                        text = message.formattedTime,
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    if (isMe) {
                        when (message.deliveryStatus) {
                            DeliveryStatus.SENDING -> {
                                Text(text = "...", fontSize = 10.sp, color = NexoraTextMuted)
                            }
                            DeliveryStatus.SENT -> {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Sent",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            DeliveryStatus.DELIVERED -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Delivered",
                                    tint = Color.White.copy(alpha = 0.7f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            DeliveryStatus.READ -> {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Read",
                                    tint = NexoraCyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Reaction Badges
        if (message.reactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(start = 4.dp, end = 4.dp, top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.reactions.forEach { (emoji, count) ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NexoraSurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder)
                    ) {
                        Text(
                            text = "$emoji $count",
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
