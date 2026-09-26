package com.example.ui.screens

import android.accounts.AccountManager
import android.app.Activity
import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.security.SecurityUtils
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraTextField
import com.example.ui.components.NexoraTopBar
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

fun deriveDisplayNameFromAccount(accountEmail: String): String {
    val local = accountEmail.substringBefore("@").trim()
    val parts = local.split('.', '_', '-', '+')
        .map { part -> part.filter { it.isLetter() } }
        .filter { it.isNotBlank() }
    return if (parts.isNotEmpty()) {
        parts.joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    } else {
        local.replaceFirstChar { c -> c.uppercase() }
    }
}

@Composable
fun GoogleAuthScreen(
    onBack: () -> Unit,
    onAccountSelected: (name: String, email: String) -> Unit
) {
    var isAuthenticating by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("Opening official Google Account Chooser...") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showManualFallback by remember { mutableStateOf(false) }
    var manualGoogleEmail by remember { mutableStateOf("") }
    var manualName by remember { mutableStateOf("") }

    val accountChooserLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isAuthenticating = false
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val accountEmail = result.data?.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
            if (!accountEmail.isNullOrBlank()) {
                val displayName = deriveDisplayNameFromAccount(accountEmail)
                onAccountSelected(displayName, accountEmail)
            } else {
                errorMessage = "No Google account was returned. Tap below to retry."
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            errorMessage = "Google account selection was dismissed."
        }
    }

    fun launchOfficialGoogleChooser() {
        errorMessage = null
        isAuthenticating = true
        statusMessage = "Opening official Google Account Chooser..."
        try {
            val intent = AccountManager.newChooseAccountIntent(
                null,
                null,
                arrayOf("com.google"),
                null,
                null,
                null,
                null
            )
            accountChooserLauncher.launch(intent)
        } catch (_: ActivityNotFoundException) {
            isAuthenticating = false
            showManualFallback = true
            errorMessage = "Official Google Account Chooser is not available on this emulator. Enter your Google account email below to continue."
        } catch (e: Exception) {
            isAuthenticating = false
            showManualFallback = true
            errorMessage = "Could not open system account chooser. Enter your Google account email below."
        }
    }

    LaunchedEffect(Unit) {
        launchOfficialGoogleChooser()
    }

    Column(
        modifier = Modifier
            .testTag("google_auth_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Continue with Google",
            subtitle = "Official Android Google Account Chooser",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Info Box on Google Password Protection
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Zero-Knowledge Protection",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = NexoraCyanAccent
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "nexora never asks for or stores your Google account password.",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Official Google Authentication",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Using the official Android system Google account chooser. Select your device account to authenticate securely.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isAuthenticating) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NexoraSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                color = NexoraCyanAccent,
                                strokeWidth = 2.5.dp,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Text(
                                text = statusMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexoraTextPrimary
                            )
                        }
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = errorMessage!!,
                        color = NexoraTextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                if (showManualFallback) {
                    Spacer(modifier = Modifier.height(20.dp))

                    NexoraTextField(
                        value = manualGoogleEmail,
                        onValueChange = {
                            manualGoogleEmail = it
                            errorMessage = null
                        },
                        label = "Google Account Email",
                        placeholder = "Enter your Google email",
                        leadingIcon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email,
                        testTag = "input_manual_google_email"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NexoraTextField(
                        value = manualName,
                        onValueChange = { manualName = it },
                        label = "Display Name (Optional)",
                        placeholder = "Enter your name",
                        leadingIcon = null,
                        keyboardType = KeyboardType.Text,
                        testTag = "input_manual_google_name"
                    )
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                if (showManualFallback) {
                    NexoraPrimaryButton(
                        text = "Continue with Google Account",
                        testTag = "btn_submit_manual_google",
                        onClick = {
                            val cleanEmail = manualGoogleEmail.trim()
                            if (!SecurityUtils.isValidEmail(cleanEmail)) {
                                errorMessage = "Please enter a valid Google email address."
                                return@NexoraPrimaryButton
                            }
                            val displayName = manualName.trim().ifBlank { deriveDisplayNameFromAccount(cleanEmail) }
                            onAccountSelected(displayName, cleanEmail)
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                NexoraPrimaryButton(
                    text = if (isAuthenticating) "Opening Chooser..." else "Choose Google Account",
                    enabled = !isAuthenticating,
                    testTag = "btn_open_google_chooser",
                    onClick = { launchOfficialGoogleChooser() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = NexoraTextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Protected by official Android Account Services",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraTextMuted
                    )
                }
            }
        }
    }
}
