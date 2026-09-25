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
import androidx.compose.ui.unit.dp
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
fun NexoraPasswordScreen(
    authProvider: String, // GOOGLE, EMAIL, PHONE
    errorMessage: String?,
    onBack: () -> Unit,
    onSubmitPasswords: (password: String, confirm: String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val hasMinLength = password.length >= 4
    val passwordsMatch = password.isNotEmpty() && password == confirmPassword

    Column(
        modifier = Modifier
            .testTag("nexora_password_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "NEXORA Account Password",
            subtitle = "Step 4: Secure Account Credentials",
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
                    text = "NEXORA Account Password",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Separation Notice
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        val providerText = when (authProvider) {
                            "GOOGLE" -> "Google password"
                            "EMAIL" -> "email password"
                            else -> "phone verification"
                        }
                        Text(
                            text = "This password is encrypted and completely separate from your $providerText.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                NexoraTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        localError = null
                    },
                    label = "NEXORA Account Password",
                    placeholder = "Enter your password",
                    helperText = "Minimum 4 characters.",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    testTag = "input_nexora_password"
                )

                Spacer(modifier = Modifier.height(14.dp))

                NexoraTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        localError = null
                    },
                    label = "Confirm Password",
                    placeholder = "Re-enter your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = (errorMessage != null || localError != null),
                    errorMessage = localError ?: errorMessage,
                    testTag = "input_confirm_password"
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                val canSubmit = hasMinLength && passwordsMatch

                NexoraPrimaryButton(
                    text = "Next",
                    enabled = canSubmit,
                    testTag = "btn_submit_passwords",
                    onClick = {
                        if (password.length < 4) {
                            localError = "Password must be at least 4 characters."
                        } else if (password != confirmPassword) {
                            localError = "Passwords do not match."
                        } else {
                            onSubmitPasswords(password, confirmPassword)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Stored with 10,000-round PBKDF2 cryptographic salt hashing.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
