package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.security.SecurityUtils
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraTextField
import com.example.ui.components.NexoraTopBar
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HandleSelectionScreen(
    currentHandle: String,
    accountName: String,
    isChecking: Boolean,
    isAvailable: Boolean?,
    validationMessage: String?,
    suggestions: List<String>,
    onBack: () -> Unit,
    onHandleChanged: (String) -> Unit,
    onSelectSuggestion: (String) -> Unit,
    onConfirmHandle: () -> Unit
) {
    var input by remember {
        mutableStateOf(
            if (currentHandle.isNotBlank()) currentHandle else "@" + accountName.lowercase().filter { it.isLetterOrDigit() || it == '_' }
        )
    }

    LaunchedEffect(Unit) {
        if (input.isNotBlank()) {
            onHandleChanged(input)
        }
    }

    Column(
        modifier = Modifier
            .testTag("handle_selection_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Claim Username",
            subtitle = "Step 5: Unique NEXORA Handle",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Choose your @username",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Unlike your profile name, your handle is globally unique and identifies you across all NEXORA services.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                NexoraTextField(
                    value = input,
                    onValueChange = {
                        val formatted = SecurityUtils.formatHandle(it)
                        input = formatted
                        onHandleChanged(formatted)
                    },
                    label = "NEXORA Handle",
                    placeholder = "@username",
                    leadingIcon = Icons.Default.AlternateEmail,
                    isError = isAvailable == false,
                    testTag = "input_handle"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Availability Status Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    if (isChecking) {
                        CircularProgressIndicator(
                            color = NexoraCyanAccent,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Checking handle availability...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextMuted
                        )
                    } else if (isAvailable == true) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NexoraSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$input is available!",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraSuccess
                        )
                    } else if (isAvailable == false) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = NexoraError,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = validationMessage ?: "This username is already taken. Please choose another username.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NexoraError
                        )
                    }
                }

                // Available Alternatives / Suggestions
                if (suggestions.isNotEmpty() && isAvailable == false) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "SUGGESTED ALTERNATIVES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = NexoraCyanAccent
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        suggestions.forEach { suggestion ->
                            Surface(
                                onClick = {
                                    input = suggestion
                                    onSelectSuggestion(suggestion)
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = NexoraSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, NexoraIndigoLight.copy(alpha = 0.4f)),
                                modifier = Modifier.testTag("suggestion_$suggestion")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = NexoraIndigoLight
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Comparison Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Identity Overview",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraCyanAccent
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Name: $accountName (Display)",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextPrimary
                        )
                        Text(
                            text = "Handle: $input (Unique Identity)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraIndigoLight
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                NexoraPrimaryButton(
                    text = "Next",
                    enabled = isAvailable == true,
                    testTag = "btn_confirm_handle",
                    onClick = onConfirmHandle
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Your handle can be tagged in comments, chats, and future channels.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
