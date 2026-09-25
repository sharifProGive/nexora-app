package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
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
fun NexoraLogoBadge(
    modifier: Modifier = Modifier,
    size: Int = 54
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .shadow(16.dp, CircleShape, spotColor = NexoraIndigoPrimary)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
            .border(
                1.5.dp,
                Brush.linearGradient(
                    colors = listOf(NexoraCyanAccent, NexoraIndigoPrimary, NexoraVioletAccent)
                ),
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "N",
            color = Color.White,
            fontSize = (size * 0.54f).sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun NexoraTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .testTag("nav_back_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NexoraSurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NexoraTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
            } else {
                NexoraLogoBadge(size = 38)
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    ),
                    color = NexoraTextPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = NexoraTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun NexoraPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    testTag: String = "primary_button"
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NexoraIndigoPrimary,
            contentColor = Color.White,
            disabledContainerColor = NexoraIndigoPrimary.copy(alpha = 0.35f),
            disabledContentColor = Color.White.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(54.dp)
            .shadow(if (enabled) 12.dp else 0.dp, RoundedCornerShape(14.dp), spotColor = NexoraIndigoPrimary)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(22.dp)
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}

@Composable
fun NexoraOutlinedCardButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    testTag: String = "card_button"
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = NexoraSurfaceDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = NexoraTextPrimary
            )
        }
    }
}

@Composable
fun NexoraTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    isError: Boolean = false,
    errorMessage: String? = null,
    helperText: String? = null,
    testTag: String = "text_field"
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            placeholder = { Text(placeholder, color = NexoraTextMuted) },
            leadingIcon = leadingIcon?.let {
                {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = if (isError) NexoraError else NexoraIndigoLight
                    )
                }
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = NexoraTextSecondary
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            singleLine = true,
            isError = isError,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = NexoraSurfaceDark,
                unfocusedContainerColor = NexoraSurfaceDark,
                focusedBorderColor = NexoraIndigoPrimary,
                unfocusedBorderColor = NexoraSurfaceBorder,
                focusedLabelColor = NexoraIndigoLight,
                unfocusedLabelColor = NexoraTextSecondary,
                focusedTextColor = NexoraTextPrimary,
                unfocusedTextColor = NexoraTextPrimary,
                cursorColor = NexoraCyanAccent,
                errorBorderColor = NexoraError
            ),
            modifier = Modifier
                .testTag(testTag)
                .fillMaxWidth()
        )

        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = NexoraError,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        } else if (helperText != null) {
            Text(
                text = helperText,
                color = NexoraTextMuted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun NexoraOtpField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    testTag: String = "otp_input"
) {
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Invisible input field for soft keyboard typing
        BasicTextField(
            value = otpValue,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }.take(6)
                onOtpChange(digitsOnly)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier
                .focusRequester(focusRequester)
                .matchParentSize()
                .padding(0.dp),
            decorationBox = { /* invisible */ }
        )

        // Visual 6 digit tiles
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { focusRequester.requestFocus() }
        ) {
            for (i in 0 until 6) {
                val char = otpValue.getOrNull(i)?.toString() ?: ""
                val isFocused = otpValue.length == i

                Box(
                    modifier = Modifier
                        .size(48.dp, 58.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NexoraSurfaceDark)
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = when {
                                isError -> NexoraError
                                isFocused -> NexoraCyanAccent
                                char.isNotEmpty() -> NexoraIndigoPrimary
                                else -> NexoraSurfaceBorder
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char.ifEmpty { "•" },
                        color = if (char.isNotEmpty()) NexoraTextPrimary else NexoraTextMuted,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun NexoraSimulatedNotificationBanner(
    code: String,
    target: String,
    onAutoFill: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF14243A),
        border = androidx.compose.foundation.BorderStroke(1.dp, NexoraCyanAccent.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NexoraCyanAccent.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = NexoraCyanAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Code dispatched to $target",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = NexoraCyanAccent
                )
                Text(
                    text = "Your verification OTP is: $code",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onAutoFill,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NexoraCyanAccent,
                    contentColor = Color.Black
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier.testTag("autofill_otp_button")
            ) {
                Text(
                    text = "Fill $code",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun NexoraAvatarBadge(
    avatarIndex: Int,
    name: String,
    size: Int = 84,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF6366F1), Color(0xFF06B6D4), Color(0xFFA855F7), Color(0xFFEC4899),
        Color(0xFF10B981), Color(0xFFF59E0B), Color(0xFF3B82F6), Color(0xFF8B5CF6)
    )
    val color = colors.getOrElse(avatarIndex % colors.size) { Color(0xFF6366F1) }
    val initial = name.trim().firstOrNull()?.uppercase() ?: "N"

    Box(
        modifier = modifier
            .size(size.dp)
            .shadow(12.dp, CircleShape, spotColor = color)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(color, color.copy(alpha = 0.6f), Color(0xFF0F172A))
                )
            )
            .border(2.5.dp, Brush.linearGradient(listOf(color, NexoraCyanAccent)), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = Color.White,
            fontSize = (size * 0.44f).sp,
            fontWeight = FontWeight.Black
        )
    }
}
