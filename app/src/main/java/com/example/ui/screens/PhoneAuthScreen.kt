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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.NexoraSurfaceBorder
import com.example.ui.theme.NexoraSurfaceDark
import com.example.ui.theme.NexoraSurfaceElevated
import com.example.ui.theme.NexoraTextMuted
import com.example.ui.theme.NexoraTextPrimary
import com.example.ui.theme.NexoraTextSecondary

data class CountryItem(
    val code: String,
    val name: String,
    val dialCode: String,
    val flag: String
)

val SUPPORTED_COUNTRIES = listOf(
    CountryItem("IN", "India", "+91", "🇮🇳"),
    CountryItem("US", "United States", "+1", "🇺🇸"),
    CountryItem("GB", "United Kingdom", "+44", "🇬🇧"),
    CountryItem("CA", "Canada", "+1", "🇨🇦"),
    CountryItem("AU", "Australia", "+61", "🇦🇺"),
    CountryItem("AE", "United Arab Emirates", "+971", "🇦🇪"),
    CountryItem("SA", "Saudi Arabia", "+966", "🇸🇦"),
    CountryItem("SG", "Singapore", "+65", "🇸🇬"),
    CountryItem("MY", "Malaysia", "+60", "🇲🇾"),
    CountryItem("ID", "Indonesia", "+62", "🇮🇩"),
    CountryItem("BD", "Bangladesh", "+880", "🇧🇩"),
    CountryItem("PK", "Pakistan", "+92", "🇵🇰"),
    CountryItem("LK", "Sri Lanka", "+94", "🇱🇰"),
    CountryItem("NP", "Nepal", "+977", "🇳🇵"),
    CountryItem("DE", "Germany", "+49", "🇩🇪"),
    CountryItem("FR", "France", "+33", "🇫🇷"),
    CountryItem("IT", "Italy", "+39", "🇮🇹"),
    CountryItem("ES", "Spain", "+34", "🇪🇸"),
    CountryItem("NL", "Netherlands", "+31", "🇳🇱"),
    CountryItem("CH", "Switzerland", "+41", "🇨🇭"),
    CountryItem("SE", "Sweden", "+46", "🇸🇪"),
    CountryItem("NO", "Norway", "+47", "🇳🇴"),
    CountryItem("DK", "Denmark", "+45", "🇩🇰"),
    CountryItem("FI", "Finland", "+358", "🇫🇮"),
    CountryItem("IE", "Ireland", "+353", "🇮🇪"),
    CountryItem("BR", "Brazil", "+55", "🇧🇷"),
    CountryItem("MX", "Mexico", "+52", "🇲🇽"),
    CountryItem("AR", "Argentina", "+54", "🇦🇷"),
    CountryItem("CO", "Colombia", "+57", "🇨🇴"),
    CountryItem("CL", "Chile", "+56", "🇨🇱"),
    CountryItem("ZA", "South Africa", "+27", "🇿🇦"),
    CountryItem("NG", "Nigeria", "+234", "🇳🇬"),
    CountryItem("KE", "Kenya", "+254", "🇰🇪"),
    CountryItem("EG", "Egypt", "+20", "🇪🇬"),
    CountryItem("GH", "Ghana", "+233", "🇬🇭"),
    CountryItem("JP", "Japan", "+81", "🇯🇵"),
    CountryItem("KR", "South Korea", "+82", "🇰🇷"),
    CountryItem("PH", "Philippines", "+63", "🇵🇭"),
    CountryItem("VN", "Vietnam", "+84", "🇻🇳"),
    CountryItem("TH", "Thailand", "+66", "🇹🇭"),
    CountryItem("QA", "Qatar", "+974", "🇶🇦"),
    CountryItem("KW", "Kuwait", "+965", "🇰🇼"),
    CountryItem("OM", "Oman", "+968", "🇴🇲"),
    CountryItem("BH", "Bahrain", "+973", "🇧🇭"),
    CountryItem("TR", "Turkey", "+90", "🇹🇷"),
    CountryItem("NZ", "New Zealand", "+64", "🇳🇿"),
    CountryItem("PL", "Poland", "+48", "🇵🇱"),
    CountryItem("AT", "Austria", "+43", "🇦🇹"),
    CountryItem("BE", "Belgium", "+32", "🇧🇪"),
    CountryItem("PT", "Portugal", "+351", "🇵🇹"),
    CountryItem("GR", "Greece", "+30", "🇬🇷")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSubmitPhone: (String) -> Unit
) {
    // Default country is India (+91)
    var selectedCountry by remember { mutableStateOf(SUPPORTED_COUNTRIES.first()) }
    var phoneInput by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }
    var showCountryPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .testTag("phone_auth_screen")
            .fillMaxSize()
            .background(NexoraDarkBackground)
    ) {
        NexoraTopBar(
            title = "Continue with Phone Number",
            subtitle = "Step 1: Country & Mobile Number",
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
                    text = "Enter Phone Number",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select your country calling code and enter your phone number. We will send a 6-digit verification OTP via SMS.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NexoraTextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Country Selector
                Text(
                    text = "Country / Region",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = NexoraTextSecondary,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NexoraSurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(1.dp, NexoraSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showCountryPicker = true }
                        .testTag("btn_select_country")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = selectedCountry.flag,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = selectedCountry.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = NexoraTextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${selectedCountry.dialCode})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = NexoraCyanAccent
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select country",
                            tint = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Phone Input Field (Starts completely empty, manually entered by user)
                NexoraTextField(
                    value = phoneInput,
                    onValueChange = { input ->
                        // Allow digits only
                        val digits = input.filter { it.isDigit() }
                        phoneInput = digits
                        localError = null
                    },
                    label = "Mobile Number (${selectedCountry.dialCode})",
                    placeholder = "Enter phone number",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Number,
                    isError = (errorMessage != null || localError != null),
                    errorMessage = localError ?: errorMessage,
                    helperText = "Enter phone number without country code.",
                    testTag = "input_phone_number"
                )

                Spacer(modifier = Modifier.height(20.dp))

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
                            text = "Standard carrier SMS rates apply. nexora will never share your phone number.",
                            style = MaterialTheme.typography.labelSmall,
                            color = NexoraTextSecondary
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                NexoraPrimaryButton(
                    text = "Send OTP",
                    isLoading = isLoading,
                    testTag = "btn_submit_phone",
                    onClick = {
                        val digits = phoneInput.trim()
                        if (digits.length < 6) {
                            localError = "Please enter a valid phone number (at least 6 digits)."
                            return@NexoraPrimaryButton
                        }
                        val fullPhoneNumber = "${selectedCountry.dialCode}$digits"
                        if (!SecurityUtils.isValidPhone(fullPhoneNumber)) {
                            localError = "Please enter a valid phone number."
                            return@NexoraPrimaryButton
                        }
                        onSubmitPhone(fullPhoneNumber)
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "A 6-digit OTP will be dispatched immediately.",
                    style = MaterialTheme.typography.labelSmall,
                    color = NexoraTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    // Country Picker Dialog
    if (showCountryPicker) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredCountries = remember(searchQuery) {
            if (searchQuery.isBlank()) {
                SUPPORTED_COUNTRIES
            } else {
                val q = searchQuery.trim().lowercase()
                SUPPORTED_COUNTRIES.filter {
                    it.name.lowercase().contains(q) ||
                            it.dialCode.lowercase().contains(q) ||
                            it.code.lowercase().contains(q)
                }
            }
        }

        BasicAlertDialog(
            onDismissRequest = { showCountryPicker = false },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(18.dp))
                .background(NexoraSurfaceDark)
                .border(1.dp, NexoraSurfaceBorder, RoundedCornerShape(18.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Country",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = NexoraTextPrimary
                    )
                    IconButton(
                        onClick = { showCountryPicker = false },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NexoraTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search field
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search country or code (+91, +1...)", color = NexoraTextMuted, fontSize = 14.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = NexoraTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = NexoraSurfaceElevated,
                        unfocusedContainerColor = NexoraSurfaceElevated,
                        focusedTextColor = NexoraTextPrimary,
                        unfocusedTextColor = NexoraTextPrimary,
                        cursorColor = NexoraCyanAccent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = NexoraSurfaceBorder)

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 360.dp)
                ) {
                    items(filteredCountries, key = { it.code + it.dialCode }) { country ->
                        val isSelected = country.code == selectedCountry.code && country.dialCode == selectedCountry.dialCode
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) NexoraSurfaceElevated else Color.Transparent)
                                .clickable {
                                    selectedCountry = country
                                    showCountryPicker = false
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = country.flag, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = country.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) NexoraCyanAccent else NexoraTextPrimary
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = country.dialCode,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) NexoraCyanAccent else NexoraTextSecondary
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = NexoraCyanAccent,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
