package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraTextField
import com.example.ui.components.NexoraTopBar
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@Composable
fun AccountNameScreen(
    initialName: String,
    isChecking: Boolean,
    isAvailable: Boolean?,
    validationMessage: String?,
    errorMessage: String?,
    onBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onSubmitName: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf(initialName) }
    var localError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (nameInput.isNotBlank()) {
            onNameChanged(nameInput)
        }
    }

    Column(
        modifier = Modifier
            .testTag("account_name_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Account Name",
            subtitle = "Step 3: Main Personal Profile Name",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                // Progressive Step Indicator
                ProgressiveStepBar(currentStep = 3)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Personal Profile Name",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Account Name = Main Personal Profile Name. This represents your master account. Video Channels are created separately.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                NexoraTextField(
                    value = nameInput,
                    onValueChange = {
                        nameInput = it
                        localError = null
                        onNameChanged(it)
                    },
                    label = "Account / Personal Profile Name",
                    placeholder = "Enter your profile name",
                    leadingIcon = Icons.Default.Person,
                    isError = isAvailable == false || errorMessage != null || localError != null,
                    errorMessage = localError ?: (if (isAvailable == false) validationMessage else errorMessage),
                    helperText = "The system distinguishes your Personal Profile Name from Video Channel Names.",
                    testTag = "input_account_name"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Availability feedback
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
                            text = "Checking name availability...",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextMuted
                        )
                    } else if (isAvailable == true && nameInput.trim().length >= 2) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = NexoraSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Profile name is available!",
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
                            text = validationMessage ?: "This profile name is already unavailable. Please choose another name.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NexoraError
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Distinction Explanation Card: Personal Profile vs Video Channel
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Hub,
                                    contentDescription = null,
                                    tint = NexoraCyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Personal Profile vs Video Channel",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val previewName = nameInput.trim().ifEmpty { "Your Profile" }
                        Text(
                            text = "Personal Profile: $previewName",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraCyanAccent
                        )
                        Text(
                            text = "Video Channel (Optional): SSSS (@SSSS)",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraIndigoLight
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "A user may have a Personal Profile without having a Video Channel. Creating a Channel later will NEVER delete or replace your Personal Profile.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                NexoraPrimaryButton(
                    text = "Next",
                    enabled = nameInput.trim().length >= 2 && isAvailable != false,
                    testTag = "btn_submit_name",
                    onClick = {
                        val clean = nameInput.trim()
                        if (clean.length < 2) {
                            localError = "Please enter an account name of at least 2 characters."
                        } else if (isAvailable == false) {
                            localError = "This profile name is already unavailable. Please choose another name."
                        } else {
                            onSubmitName(clean)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "You can customize your avatar and profile privacy next.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
