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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.NexoraPrimaryButton
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
fun PolicyScreen(
    onBack: () -> Unit,
    onAgreeAndContinue: () -> Unit
) {
    var hasAgreed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .testTag("policy_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Account Terms & Policies",
            subtitle = "Step 2: Community & Privacy Rules",
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
                ProgressiveStepBar(currentStep = 2)

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "NEXORA Rules & Guidelines",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Please read and acknowledge the fundamental NEXORA platform policies before continuing account creation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Policy Card 1: Community Safety
                PolicyItemCard(
                    icon = Icons.Default.Gavel,
                    title = "1. Community Guidelines & Safety",
                    description = "NEXORA maintains high standards for community respect. Harassment, hateful conduct, illegal media, and deceptive impersonation result in immediate account termination."
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Policy Card 2: Identity & Video Channel Separation
                PolicyItemCard(
                    icon = Icons.Default.Hub,
                    title = "2. Personal Profile vs Video Channels",
                    description = "Your NEXORA Personal Profile is your permanent account identity. Creating separate Video Channels (now or later) never deletes or replaces your Personal Profile. Both entities remain distinct."
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Policy Card 3: Privacy & Security
                PolicyItemCard(
                    icon = Icons.Default.Security,
                    title = "3. Zero Plaintext Password Storage",
                    description = "Passwords are cryptographically salted and hashed using PBKDF2 (10,000 rounds). NEXORA will never request your Google or external email passwords. You control Public or Private profile discoverability."
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Policy Card 4: Video Uploads & Channel Prerequisite
                PolicyItemCard(
                    icon = Icons.Default.VerifiedUser,
                    title = "4. Video Upload Eligibility",
                    description = "To upload public videos or shorts, a dedicated Video Channel must be created under your account. Personal profile content remains distinct from public channel distributions."
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Checkbox Agreement
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NexoraSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { hasAgreed = !hasAgreed }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = hasAgreed,
                            onCheckedChange = { hasAgreed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = NexoraCyanAccent,
                                checkmarkColor = Color.Black,
                                uncheckedColor = NexoraTextMuted
                            ),
                            modifier = Modifier.testTag("checkbox_agree_policies")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I have read and agree to the NEXORA Terms of Service, Community Guidelines, and Privacy Policy.",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = NexoraTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                NexoraPrimaryButton(
                    text = "I Agree & Continue",
                    enabled = hasAgreed,
                    testTag = "btn_agree_and_continue",
                    onClick = onAgreeAndContinue
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Required agreement prior to identity creation.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProgressiveStepBar(currentStep: Int) {
    val steps = listOf("AUTH", "POLICIES", "NAME", "PRIVACY", "COMPLETE")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(NexoraSurfaceDark)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, stepName ->
            val stepNumber = index + 1
            val isPassed = stepNumber < currentStep
            val isCurrent = stepNumber == currentStep

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isPassed -> NexoraCyanAccent
                                isCurrent -> NexoraIndigoPrimary
                                else -> NexoraSurfaceElevated
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPassed) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(13.dp)
                        )
                    } else {
                        Text(
                            text = "$stepNumber",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else NexoraTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stepName,
                    fontSize = 10.sp,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isCurrent -> NexoraCyanAccent
                        isPassed -> NexoraTextPrimary
                        else -> NexoraTextMuted
                    }
                )
            }

            if (index < steps.size - 1) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = NexoraSurfaceBorder,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun PolicyItemCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(NexoraIndigoPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = NexoraIndigoLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NexoraTextSecondary
                )
            }
        }
    }
}
