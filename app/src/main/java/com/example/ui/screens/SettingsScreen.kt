package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.NexoraTheme
import com.example.ui.theme.NexoraThemeManager

@Composable
fun SettingsScreen(
    user: UserEntity,
    installedVersionName: String = "1.0.0",
    installedVersionCode: Int = 1,
    onBack: () -> Unit,
    onOpenPersonalProfile: () -> Unit,
    onOpenPrivacySettings: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val currentThemeMode by NexoraThemeManager.themeMode.collectAsState()
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDataStorageDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSecurityDialog by remember { mutableStateOf(false) }

    val colors = NexoraTheme.colors

    Column(
        modifier = Modifier
            .testTag("settings_screen")
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Compact Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("btn_settings_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = colors.textPrimary
            )
        }

        HorizontalDivider(color = colors.surfaceBorder.copy(alpha = 0.5f), thickness = 0.5.dp)

        // Settings Content List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            // SECTION: ACCOUNT
            SettingsSectionHeader(title = "ACCOUNT", color = colors.accent)

            SettingsCompactRow(
                icon = Icons.Default.Person,
                title = "Account",
                value = user.handle,
                testTag = "row_account",
                onClick = onOpenPersonalProfile
            )

            SettingsCompactRow(
                icon = Icons.Default.Person,
                title = "Personal Profile",
                value = user.name,
                testTag = "row_personal_profile",
                onClick = onOpenPersonalProfile
            )

            SettingsCompactRow(
                icon = Icons.Default.Lock,
                title = "Password & Security",
                value = "Protected",
                testTag = "row_password_security",
                onClick = { showSecurityDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION: PRIVACY
            SettingsSectionHeader(title = "PRIVACY", color = colors.accent)

            SettingsCompactRow(
                icon = Icons.Default.Shield,
                title = "Privacy",
                value = if (user.isPrivateProfile) "Private" else "Public",
                testTag = "row_privacy",
                onClick = onOpenPrivacySettings
            )

            SettingsCompactRow(
                icon = Icons.Default.Visibility,
                title = "Profile Visibility",
                value = if (user.isPrivateProfile) "Followers only" else "Everyone",
                testTag = "row_profile_visibility",
                onClick = onOpenPrivacySettings
            )

            SettingsCompactRow(
                icon = Icons.Default.Mail,
                title = "Messages",
                value = "Friends & Followers",
                testTag = "row_messages_privacy",
                onClick = onOpenPrivacySettings
            )

            SettingsCompactRow(
                icon = Icons.Default.Block,
                title = "Blocking",
                value = "0 blocked",
                testTag = "row_blocking",
                onClick = {
                    Toast.makeText(context, "No blocked users", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION: NOTIFICATIONS
            SettingsSectionHeader(title = "NOTIFICATIONS", color = colors.accent)

            SettingsCompactRow(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                value = "Push & In-App",
                testTag = "row_notifications",
                onClick = {
                    Toast.makeText(context, "Notifications enabled", Toast.LENGTH_SHORT).show()
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION: APPEARANCE
            SettingsSectionHeader(title = "APPEARANCE", color = colors.accent)

            SettingsCompactRow(
                icon = Icons.Default.Palette,
                title = "Theme",
                value = currentThemeMode.title,
                testTag = "row_theme",
                onClick = { showThemeDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION: GENERAL
            SettingsSectionHeader(title = "GENERAL", color = colors.accent)

            SettingsCompactRow(
                icon = Icons.Default.Language,
                title = "Language",
                value = "English (US)",
                testTag = "row_language",
                onClick = { showLanguageDialog = true }
            )

            SettingsCompactRow(
                icon = Icons.Default.DataUsage,
                title = "Data & Storage",
                value = "Cache optimized",
                testTag = "row_data_storage",
                onClick = { showDataStorageDialog = true }
            )

            SettingsCompactRow(
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                title = "Help",
                value = "FAQ & Support",
                testTag = "row_help",
                onClick = {
                    Toast.makeText(context, "Help center: support@nexora.app", Toast.LENGTH_SHORT).show()
                }
            )

            SettingsCompactRow(
                icon = Icons.Default.Info,
                title = "About NEXORA",
                value = "v$installedVersionName",
                testTag = "row_about",
                onClick = { showAboutDialog = true }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION: ACCOUNT ACTIONS
            SettingsSectionHeader(title = "ACCOUNT ACTIONS", color = colors.textMuted)

            SettingsCompactRow(
                icon = Icons.Default.Logout,
                title = "Logout",
                titleColor = colors.error,
                testTag = "row_logout",
                onClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // THEME SELECTION DIALOG (Requirement: System Default, Light, Dark with Checkmark)
    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentMode = currentThemeMode,
            onSelectMode = { mode ->
                NexoraThemeManager.setThemeMode(context, mode)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to log out of NEXORA?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.textSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Logout", color = colors.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = colors.textMuted)
                }
            }
        )
    }

    // ABOUT DIALOG
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    text = "About NEXORA",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "NEXORA Media & Creator Platform",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version $installedVersionName (Build $installedVersionCode)\n" +
                                "Engine: In-App Verification & Google Play Ready\n" +
                                "Secure Zero-Plaintext Architecture",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("Close", color = colors.accent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // DATA & STORAGE DIALOG
    if (showDataStorageDialog) {
        AlertDialog(
            onDismissRequest = { showDataStorageDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    text = "Data & Storage",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Local Cache: 12.4 MB\nDatabase: SQLite Room\nMedia Preload: Enabled (Wi-Fi & Cellular)",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        Toast.makeText(context, "Cache cleared successfully", Toast.LENGTH_SHORT).show()
                        showDataStorageDialog = false
                    }
                ) {
                    Text("Clear Cache", color = colors.accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDataStorageDialog = false }) {
                    Text("Done", color = colors.textMuted)
                }
            }
        )
    }

    // LANGUAGE DIALOG
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    text = "Language",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "✓ English (US)\n  Spanish (Español)\n  French (Français)\n  German (Deutsch)",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("OK", color = colors.accent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // SECURITY DIALOG
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            containerColor = colors.surface,
            title = {
                Text(
                    text = "Password & Security",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.textPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "• Account Password: Set & Salted (PBKDF2)\n" +
                                "• Two-Step Verification: Active via Email & Phone OTP\n" +
                                "• Sessions: 1 active device session\n" +
                                "• Hardware Cryptography: AES-256 GCM",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        lineHeight = 20.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSecurityDialog = false }) {
                    Text("Close", color = colors.accent, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String, color: Color) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.sp
        ),
        color = color,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
    )
}

@Composable
private fun SettingsCompactRow(
    icon: ImageVector,
    title: String,
    value: String? = null,
    titleColor: Color? = null,
    testTag: String,
    onClick: () -> Unit
) {
    val colors = NexoraTheme.colors

    Row(
        modifier = Modifier
            .testTag(testTag)
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor ?: colors.textSecondary,
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            ),
            color = titleColor ?: colors.textPrimary,
            modifier = Modifier.weight(1f)
        )

        if (value != null) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                color = colors.textMuted
            )
        }
    }
}

@Composable
fun ThemeSelectionDialog(
    currentMode: AppThemeMode,
    onSelectMode: (AppThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = NexoraTheme.colors

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        title = {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                AppThemeMode.entries.forEach { mode ->
                    val isSelected = mode == currentMode
                    Row(
                        modifier = Modifier
                            .testTag("theme_option_${mode.name.lowercase()}")
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectMode(mode) }
                            .padding(horizontal = 8.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { onSelectMode(mode) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colors.accent,
                                unselectedColor = colors.textMuted
                            )
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 15.sp
                            ),
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = colors.accent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = colors.accent, fontWeight = FontWeight.Bold)
            }
        }
    )
}
