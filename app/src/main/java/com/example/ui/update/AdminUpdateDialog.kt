package com.example.ui.update

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.NexoraCyanAccent
import com.example.ui.theme.NexoraDarkBackground
import com.example.ui.theme.NexoraError
import com.example.ui.theme.NexoraIndigoLight
import com.example.ui.theme.NexoraIndigoPrimary
import com.example.ui.theme.NexoraSuccess
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary
import com.example.update.repository.AdminSimulationScenario

@Composable
fun AdminUpdateDialog(
    isAuthorized: Boolean,
    currentScenario: AdminSimulationScenario,
    installedVersionName: String,
    installedVersionCode: Int,
    onAuthorize: (String) -> Boolean,
    onSelectScenario: (AdminSimulationScenario) -> Unit,
    onDismiss: () -> Unit
) {
    var passcode by remember { mutableStateOf("") }
    var authError by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NexoraSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
            modifier = Modifier
                .testTag("admin_update_dialog")
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = NexoraCyanAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "NEXORA Admin Engine",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = NexoraTextPrimary
                            )
                            Text(
                                text = "Release & Update Management",
                                style = MaterialTheme.typography.labelSmall,
                                color = NexoraTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NexoraTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = NexoraSurfaceBorder)
                Spacer(modifier = Modifier.height(14.dp))

                if (!isAuthorized) {
                    // Protected Access Gate
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NexoraIndigoLight,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Admin Authentication Required",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = NexoraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Enter authorized admin passcode to manage remote version policies.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NexoraTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = passcode,
                            onValueChange = {
                                passcode = it
                                authError = false
                            },
                            placeholder = { Text("Enter Passcode (e.g. nexora2026)", color = NexoraTextMuted) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NexoraCyanAccent,
                                unfocusedBorderColor = NexoraSurfaceBorder,
                                focusedTextColor = NexoraTextPrimary,
                                unfocusedTextColor = NexoraTextPrimary,
                                focusedContainerColor = NexoraSurfaceElevated,
                                unfocusedContainerColor = NexoraSurfaceElevated
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (authError) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Invalid administrator credentials.",
                                color = NexoraError,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val success = onAuthorize(passcode)
                                if (!success) {
                                    authError = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyanAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text("AUTHORIZE ACCESS", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Authorized Admin Management Panel
                    Column {
                        // Environment status
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NexoraSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "INSTALLED BUILD",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = NexoraTextMuted
                                    )
                                    Text(
                                        text = "v$installedVersionName (Build $installedVersionCode)",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = NexoraTextPrimary
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NexoraSuccess.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "ADMIN VERIFIED",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = NexoraSuccess,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "SELECT REMOTE CONFIGURATION SCENARIO",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = NexoraCyanAccent
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        AdminSimulationScenario.values().forEach { scenario ->
                            val isSelected = scenario == currentScenario
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) NexoraIndigoPrimary.copy(alpha = 0.15f) else NexoraSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isSelected) NexoraCyanAccent else NexoraSurfaceBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        onSelectScenario(scenario)
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (isSelected) NexoraCyanAccent else NexoraTextMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = scenario.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (isSelected) NexoraTextPrimary else NexoraTextSecondary
                                        )
                                        Text(
                                            text = scenario.description,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = NexoraTextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = NexoraCyanAccent, contentColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Text("APPLY & CLOSE", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
