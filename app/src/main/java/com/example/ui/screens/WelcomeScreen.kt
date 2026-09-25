package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraLogoBadge
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

@Composable
fun WelcomeScreen(
    onContinueGoogle: () -> Unit,
    onContinueEmail: () -> Unit,
    onContinuePhone: () -> Unit,
    onNavigateLogin: () -> Unit
) {
    Box(
        modifier = Modifier
            .testTag("welcome_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .widthIn(max = 500.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Hero
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                NexoraLogoBadge(size = 68)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome to NEXORA",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NexoraTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "One single account & main profile for your personal identity, creators, and future digital experiences.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Auth Methods (Google, Email, Phone Number ONLY)
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "CHOOSE SIGN-IN METHOD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = NexoraCyanAccent,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )

                // 1. Continue with Google
                AuthMethodCard(
                    title = "Continue with Google",
                    subtitle = "Fast OIDC auth & separate NEXORA password",
                    accentColor = Color(0xFF4285F4),
                    iconContent = {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "G",
                                color = Color(0xFF4285F4),
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    },
                    testTag = "btn_auth_google",
                    onClick = onContinueGoogle
                )

                // 2. Continue with Email
                AuthMethodCard(
                    title = "Continue with Email",
                    subtitle = "Verify with 6-digit code sent to your inbox",
                    accentColor = NexoraIndigoPrimary,
                    iconContent = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = NexoraIndigoLight,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    testTag = "btn_auth_email",
                    onClick = onContinueEmail
                )

                // 3. Continue with Phone Number
                AuthMethodCard(
                    title = "Continue with Phone Number",
                    subtitle = "Verify with secure 6-digit SMS OTP",
                    accentColor = NexoraCyanAccent,
                    iconContent = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    testTag = "btn_auth_phone",
                    onClick = onContinuePhone
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer - Login & Security Guarantee
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Already have a NEXORA account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraTextSecondary
                    )
                    Text(
                        text = "Log In",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraIndigoLight,
                        modifier = Modifier
                            .testTag("nav_to_login_button")
                            .clickable { onNavigateLogin() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = NexoraTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Protected by PBKDF2 hashing & verified OTP session",
                        style = MaterialTheme.typography.labelSmall,
                        color = NexoraTextMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun AuthMethodCard(
    title: String,
    subtitle: String,
    accentColor: Color,
    iconContent: @Composable () -> Unit,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(72.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = accentColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NexoraSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                iconContent()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = NexoraTextSecondary
                )
            }
        }
    }
}
