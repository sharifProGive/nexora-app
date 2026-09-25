package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraOtpField
import com.example.ui.components.NexoraPrimaryButton
import com.example.ui.components.NexoraSimulatedNotificationBanner
import com.example.ui.components.NexoraTopBar
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

@Composable
fun VerificationCodeScreen(
    targetIdentifier: String,
    verificationType: String, // REGISTRATION or LOGIN_2FA
    codePreview: String?,
    resendCountdown: Int,
    attemptsRemaining: Int,
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onVerifyCode: (String) -> Unit,
    onResendCode: () -> Unit
) {
    var otpInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .testTag("verification_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = if (verificationType == "LOGIN_2FA") "Security Verification" else "Verify Identity",
            subtitle = "Step 2: Enter 6-digit code",
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Enter the 6-digit verification code",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "We sent a 6-digit verification code to:\n$targetIdentifier",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Simulated delivery notification banner
                if (codePreview != null) {
                    NexoraSimulatedNotificationBanner(
                        code = codePreview,
                        target = targetIdentifier,
                        onAutoFill = {
                            otpInput = codePreview
                            onVerifyCode(codePreview)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 6-digit OTP UI: _ _ _ _ _ _
                NexoraOtpField(
                    otpValue = otpInput,
                    onOtpChange = { input ->
                        otpInput = input
                        if (input.length == 6) {
                            onVerifyCode(input)
                        }
                    },
                    isError = errorMessage != null,
                    testTag = "input_otp_code"
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        color = NexoraError,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "$attemptsRemaining attempts remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (attemptsRemaining <= 2) NexoraError else NexoraTextMuted
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Resend Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Didn't receive the code? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NexoraTextSecondary
                    )

                    if (resendCountdown > 0) {
                        Text(
                            text = "Resend in ${resendCountdown}s",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = NexoraCyanAccent
                        )
                    } else {
                        TextButton(
                            onClick = onResendCode,
                            modifier = Modifier.testTag("btn_resend_code")
                        ) {
                            Text(
                                text = "Resend Code",
                                color = NexoraIndigoLight,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                NexoraPrimaryButton(
                    text = "Verify & Continue",
                    enabled = otpInput.length == 6,
                    isLoading = isLoading,
                    testTag = "btn_submit_otp",
                    onClick = { onVerifyCode(otpInput) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Codes expire after 5 minutes for your account security.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
