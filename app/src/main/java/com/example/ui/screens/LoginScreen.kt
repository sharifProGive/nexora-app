package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraTextField
import com.example.ui.components.NexoraTopBar
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

@Composable
fun LoginScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onNavigateSignUp: () -> Unit,
    onAttemptLogin: (provider: String, identifier: String, password: String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Email, 1: Phone, 2: Google
    var identifierInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    val tabTitles = listOf("Email", "Phone", "Google")

    Column(
        modifier = Modifier
            .testTag("login_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Sign In to NEXORA",
            subtitle = "Access your main personal profile",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Method Tabs
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = NexoraSurfaceDark,
                    contentColor = NexoraIndigoLight,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = NexoraCyanAccent,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                localError = null
                                // Auto-fill sample for quick test if desired
                                if (index == 0 && identifierInput.isBlank()) {
                                    identifierInput = "sharif.sample@nexora.io"
                                    passwordInput = "Nexora2026!"
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) NexoraTextPrimary else NexoraTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("login_tab_$title")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (selectedTab) {
                    0 -> { // Email Login
                        NexoraTextField(
                            value = identifierInput,
                            onValueChange = {
                                identifierInput = it
                                localError = null
                            },
                            label = "Registered Email",
                            placeholder = "e.g. sharif.sample@nexora.io",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            testTag = "login_email_input"
                        )
                    }
                    1 -> { // Phone Login
                        NexoraTextField(
                            value = identifierInput,
                            onValueChange = {
                                identifierInput = it
                                localError = null
                            },
                            label = "Registered Phone Number",
                            placeholder = "+1 555 123 4567",
                            leadingIcon = Icons.Default.Phone,
                            keyboardType = KeyboardType.Phone,
                            testTag = "login_phone_input"
                        )
                    }
                    2 -> { // Google Login
                        NexoraTextField(
                            value = identifierInput,
                            onValueChange = {
                                identifierInput = it
                                localError = null
                            },
                            label = "Registered Google Account Email",
                            placeholder = "e.g. sharif.official@gmail.com",
                            leadingIcon = Icons.Default.Email,
                            keyboardType = KeyboardType.Email,
                            testTag = "login_google_email_input"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                NexoraTextField(
                    value = passwordInput,
                    onValueChange = {
                        passwordInput = it
                        localError = null
                    },
                    label = "NEXORA Account Password",
                    placeholder = "Enter your NEXORA password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = (errorMessage != null || localError != null),
                    errorMessage = localError ?: errorMessage,
                    testTag = "login_password_input"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Security notice regarding the 2FA requirement
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
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(NexoraCyanAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = NexoraCyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "After password verification, a 6-digit verification code will be sent to confirm your identity.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                val providerName = when (selectedTab) {
                    0 -> "EMAIL"
                    1 -> "PHONE"
                    else -> "GOOGLE"
                }

                NexoraPrimaryButton(
                    text = "Sign In",
                    isLoading = isLoading,
                    testTag = "btn_submit_login",
                    onClick = {
                        if (identifierInput.isBlank()) {
                            localError = "Please enter your identifier."
                        } else if (passwordInput.isBlank()) {
                            localError = "Please enter your NEXORA password."
                        } else {
                            onAttemptLogin(providerName, identifierInput, passwordInput)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraTextSecondary
                    )
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraCyanAccent,
                        modifier = Modifier
                            .testTag("nav_to_signup_button")
                            .clickable { onNavigateSignUp() }
                            .padding(4.dp)
                    )
                }
            }
        }
    }
}
