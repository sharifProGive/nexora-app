package com.example.upload.ui

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraErrorRed
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSuccessGreen
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.upload.model.UploadSettings
import com.example.upload.model.UploadStatus
import com.example.upload.model.UploadTargetType
import com.example.upload.model.UploadTask
import com.example.upload.service.UploadManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadManagerDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val uploadManager = remember { UploadManager.getInstance(context) }

    val activeTasks by uploadManager.activeTasks.collectAsState()
    val completedTasks by uploadManager.completedTasks.collectAsState()
    val failedTasks by uploadManager.failedTasks.collectAsState()
    val settings by uploadManager.settings.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NexoraSurfaceDark,
        contentColor = NexoraTextPrimary,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(NexoraSurfaceBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .testTag("upload_manager_sheet")
                .fillMaxWidth()
                .fillMaxSize(0.85f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Upload,
                            contentDescription = null,
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Upload Manager",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = if (activeTasks.isNotEmpty()) "${activeTasks.size} active transfer(s)" else "All transfers up to date",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (activeTasks.isNotEmpty()) NexoraCyanAccent else NexoraTextSecondary
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        modifier = Modifier.testTag("btn_upload_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Upload Settings",
                            tint = NexoraTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_upload_manager")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NexoraTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Navigation Tabs (Active, Completed, Failed)
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = NexoraSurfaceDark,
                contentColor = NexoraCyanAccent,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NexoraCyanAccent
                    )
                },
                divider = { HorizontalDivider(color = NexoraSurfaceBorder) }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Active", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal)
                            if (activeTasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = NexoraCyanAccent,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = activeTasks.size.toString(),
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

                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Completed", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal)
                            if (completedTasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = NexoraSurfaceElevated,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = completedTasks.size.toString(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = NexoraTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                )

                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Failed", fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal)
                            if (failedTasks.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = NexoraErrorRed,
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = failedTasks.size.toString(),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Content
            when (selectedTabIndex) {
                0 -> {
                    // ACTIVE TRANSFERS
                    if (activeTasks.isEmpty()) {
                        EmptyStateView(
                            title = "No Active Uploads",
                            description = "Your uploads and media processing tasks will appear here in real-time."
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(activeTasks, key = { it.id }) { task ->
                                ActiveUploadItemCard(
                                    task = task,
                                    onCancel = { uploadManager.cancelUpload(task.id) }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // COMPLETED TRANSFERS
                    if (completedTasks.isEmpty()) {
                        EmptyStateView(
                            title = "No Completed Uploads",
                            description = "Videos, shorts, and pictures uploaded in this session will be recorded here."
                        )
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        completedTasks.forEach { uploadManager.removeUpload(it.id) }
                                    }
                                ) {
                                    Text("Clear All Completed", color = NexoraCyanAccent, fontSize = 11.sp)
                                }
                            }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(completedTasks, key = { it.id }) { task ->
                                    CompletedUploadItemCard(
                                        task = task,
                                        onRemove = { uploadManager.removeUpload(task.id) }
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // FAILED TRANSFERS
                    if (failedTasks.isEmpty()) {
                        EmptyStateView(
                            title = "No Failed Transfers",
                            description = "No failed or interrupted uploads. Everything is running smoothly."
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(failedTasks, key = { it.id }) { task ->
                                FailedUploadItemCard(
                                    task = task,
                                    onRetry = { uploadManager.retryUpload(task.id) },
                                    onRemove = { uploadManager.removeUpload(task.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // UPLOAD SETTINGS BOTTOM SHEET
    if (showSettingsSheet) {
        UploadSettingsBottomSheet(
            currentSettings = settings,
            onDismiss = { showSettingsSheet = false },
            onUpdateSettings = {
                uploadManager.updateSettings(it)
                showSettingsSheet = false
            }
        )
    }
}

@Composable
private fun ActiveUploadItemCard(
    task: UploadTask,
    onCancel: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NexoraCyanAccent.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = task.targetType.title,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NexoraCyanAccent,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.status == UploadStatus.PROCESSING) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = NexoraCyanAccent,
                            strokeWidth = 1.5.dp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = task.status.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (task.status == UploadStatus.PROCESSING) NexoraCyanAccent else NexoraIndigoLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cancel,
                            contentDescription = "Cancel",
                            tint = NexoraTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = task.title.ifBlank { "Media Upload" },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { task.progressPercent / 100f },
                color = NexoraCyanAccent,
                trackColor = NexoraSurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${task.progressPercent}% • ${task.formattedSize}",
                    fontSize = 11.sp,
                    color = NexoraTextSecondary
                )
                if (task.uploadSpeed.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = NexoraCyanAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = task.uploadSpeed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NexoraCyanAccent
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompletedUploadItemCard(
    task: UploadTask,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NexoraSuccessGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = NexoraSuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = task.title.ifBlank { "Uploaded Item" },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = NexoraTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${task.targetType.title} • ${task.formattedSize}",
                        fontSize = 11.sp,
                        color = NexoraTextSecondary
                    )
                }
            }

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove",
                    tint = NexoraTextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun FailedUploadItemCard(
    task: UploadTask,
    onRetry: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NexoraSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraErrorRed.copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = NexoraErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Upload Failed",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NexoraErrorRed
                    )
                }

                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = NexoraTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.title.ifBlank { "Media Upload" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NexoraTextPrimary
            )

            if (!task.errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = task.errorMessage,
                    fontSize = 11.sp,
                    color = NexoraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onRetry,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = NexoraCyanAccent)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Retry", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyStateView(
    title: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(NexoraSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Upload,
                    contentDescription = null,
                    tint = NexoraTextMuted,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = NexoraTextSecondary,
                modifier = Modifier.padding(horizontal = 32.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UploadSettingsBottomSheet(
    currentSettings: UploadSettings,
    onDismiss: () -> Unit,
    onUpdateSettings: (UploadSettings) -> Unit
) {
    var wifiOnly by remember { mutableStateOf(currentSettings.wifiOnly) }
    var uploadQuality by remember { mutableStateOf(currentSettings.defaultUploadQuality) }
    var defaultVisibility by remember { mutableStateOf(currentSettings.defaultVisibility) }
    var defaultAudience by remember { mutableStateOf(currentSettings.defaultAudience) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NexoraSurfaceDark,
        contentColor = NexoraTextPrimary
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Upload Preferences",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = NexoraTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Wi-Fi only
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Upload over Wi-Fi only", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = NexoraTextPrimary)
                    Text("Prevent cellular data usage when uploading videos", fontSize = 11.sp, color = NexoraTextSecondary)
                }
                Switch(
                    checked = wifiOnly,
                    onCheckedChange = { wifiOnly = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = NexoraCyanAccent)
                )
            }

            HorizontalDivider(color = NexoraSurfaceBorder, modifier = Modifier.padding(vertical = 12.dp))

            // Default Visibility
            Text("DEFAULT VISIBILITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NexoraCyanAccent)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("PUBLIC", "UNLISTED", "PRIVATE").forEach { vis ->
                    OutlinedButton(
                        onClick = { defaultVisibility = vis },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (defaultVisibility == vis) NexoraCyanAccent else Color.Transparent,
                            contentColor = if (defaultVisibility == vis) Color.Black else NexoraTextPrimary
                        )
                    ) {
                        Text(vis, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = NexoraSurfaceBorder, modifier = Modifier.padding(vertical = 12.dp))

            // Default Upload Quality
            Text("UPLOAD QUALITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NexoraCyanAccent)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("ORIGINAL_1080P", "HIGH_720P", "DATA_SAVER_480P").forEach { q ->
                    val label = when (q) {
                        "ORIGINAL_1080P" -> "1080p HD"
                        "HIGH_720P" -> "720p"
                        else -> "Data Saver"
                    }
                    OutlinedButton(
                        onClick = { uploadQuality = q },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (uploadQuality == q) NexoraCyanAccent else Color.Transparent,
                            contentColor = if (uploadQuality == q) Color.Black else NexoraTextPrimary
                        )
                    ) {
                        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onUpdateSettings(
                        currentSettings.copy(
                            wifiOnly = wifiOnly,
                            defaultUploadQuality = uploadQuality,
                            defaultVisibility = defaultVisibility,
                            defaultAudience = defaultAudience
                        )
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NexoraCyanAccent, contentColor = Color.Black),
                modifier = Modifier.fillMaxWidth().height(44.dp)
            ) {
                Text("Save Preferences", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
