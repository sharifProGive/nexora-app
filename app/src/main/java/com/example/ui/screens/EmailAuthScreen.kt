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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.SecurityUtils
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraTextField
import com.example.ui.components.NexoraTopBar
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@Composable
fun EmailAuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSubmitEmail: (String) -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .testTag("email_auth_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Continue with Email",
            subtitle = "Step 1: Enter your email address",
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
                    text = "What is your email address?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We will send a 6-digit verification code to confirm you own this address.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                NexoraTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        localError = null
                    },
                    label = "Email Address",
                    placeholder = "e.g. name@example.com",
                    leadingIcon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    isError = (errorMessage != null || localError != null),
                    errorMessage = localError ?: errorMessage,
                    testTag = "input_email_address"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Security Note
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "NEXORA will never ask for your email account password. Your NEXORA password is set separately later.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextSecondary
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                NexoraPrimaryButton(
                    text = "Next",
                    isLoading = isLoading,
                    testTag = "btn_submit_email",
                    onClick = {
                        val clean = emailInput.trim()
                        if (!SecurityUtils.isValidEmail(clean)) {
                            localError = "Please enter a valid email address (e.g., you@domain.com)."
                        } else {
                            onSubmitEmail(clean)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "A secure 6-digit OTP will be dispatched immediately.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
