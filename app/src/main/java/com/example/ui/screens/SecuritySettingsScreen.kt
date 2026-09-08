package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.example.ui.theme.*
import com.example.util.BiometricAuthHelper
import com.example.util.BiometricStatus
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun SecuritySettingsScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    var currentPinInput by remember { mutableStateOf("") }
    var newPinInput by remember { mutableStateOf("") }
    var confirmPinInput by remember { mutableStateOf("") }
    var changePinError by remember { mutableStateOf("") }
    var changePinSuccess by remember { mutableStateOf(false) }

    val biometricStatus = remember { BiometricAuthHelper.checkBiometricAvailability(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Page Title Banner
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = RoyalPurple800,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BrilliantGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Security",
                        tint = RoyalPurple900,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = if (state.isHindi) "सुरक्षा एवं भाषा सेटिंग्स" else "Security & Language Settings",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (state.isHindi) "4-अंकीय MPIN, बायोमेट्रिक व भाषा वरीयता" else "4-Digit MPIN, Native Biometrics & Bilingual Settings",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------------------------------------
        // 1. BILINGUAL LANGUAGE PREFERENCE CARD
        // -------------------------------------------------------------
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = RoyalPurple700,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (state.isHindi) "भाषा वरीयता (Language Preference)" else "Language Preference (भाषा चयन)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Detected Device Language Info
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Device",
                            tint = TextDarkSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (state.isHindi) "डिवाइस की डिफ़ॉल्ट भाषा:" else "Device Detected System Language:",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                            Text(
                                text = state.detectedSystemLanguage,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Language Option Buttons (English & Hindi)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // English Option
                    Surface(
                        onClick = { viewModel.setLanguage(false, context) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (!state.isHindi) RoyalPurple700 else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (!state.isHindi) RoyalPurple900 else BorderLight
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "English",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (!state.isHindi) Color.White else TextDarkPrimary
                            )
                            if (!state.isHindi) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = BrilliantGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Hindi Option
                    Surface(
                        onClick = { viewModel.setLanguage(true, context) },
                        shape = RoundedCornerShape(12.dp),
                        color = if (state.isHindi) RoyalPurple700 else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (state.isHindi) RoyalPurple900 else BorderLight
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "हिन्दी (Hindi)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (state.isHindi) Color.White else TextDarkPrimary
                            )
                            if (state.isHindi) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Active",
                                    tint = BrilliantGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------------------------------------
        // 2. CONFIGURE / CHANGE 4-DIGIT MPIN CARD
        // -------------------------------------------------------------
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Pin,
                        contentDescription = "MPIN",
                        tint = RoyalPurple700,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (state.isHindi) "4-अंकीय MPIN बदलें" else "Change 4-Digit MPIN",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (state.isHindi)
                        "सक्रिय MPIN: •••• (सत्यापित मोबाइल: ${state.verifiedMobile})"
                    else
                        "Active MPIN: •••• (Registered Mobile: ${state.verifiedMobile})",
                    fontSize = 12.sp,
                    color = TextDarkSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Current MPIN
                OutlinedTextField(
                    value = currentPinInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            currentPinInput = it
                            changePinError = ""
                            changePinSuccess = false
                        }
                    },
                    label = { Text(if (state.isHindi) "वर्तमान MPIN (डिफ़ॉल्ट: 1234)" else "Current MPIN (Default: 1234)") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // New MPIN
                OutlinedTextField(
                    value = newPinInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            newPinInput = it
                            changePinError = ""
                        }
                    },
                    label = { Text(if (state.isHindi) "नया 4-अंकीय MPIN" else "New 4-Digit MPIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Confirm New MPIN
                OutlinedTextField(
                    value = confirmPinInput,
                    onValueChange = {
                        if (it.length <= 4 && it.all { c -> c.isDigit() }) {
                            confirmPinInput = it
                            changePinError = ""
                        }
                    },
                    label = { Text(if (state.isHindi) "नए MPIN की पुष्टि करें" else "Confirm New 4-Digit MPIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (changePinError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = changePinError, color = CrimsonRed, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                if (changePinSuccess) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (state.isHindi) "MPIN सफलतापूर्वक अपडेट किया गया!" else "MPIN Updated Successfully!",
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (currentPinInput.isEmpty() || newPinInput.isEmpty() || confirmPinInput.isEmpty()) {
                            changePinError = if (state.isHindi) "कृपया सभी फ़ील्ड भरें!" else "Please fill all fields!"
                        } else if (newPinInput.length != 4) {
                            changePinError = if (state.isHindi) "नया MPIN 4 अंकों का होना चाहिए!" else "New MPIN must be 4 digits!"
                        } else if (newPinInput != confirmPinInput) {
                            changePinError = if (state.isHindi) "नए MPIN की पुष्टि मेल नहीं खाती!" else "New MPIN confirmation does not match!"
                        } else {
                            val success = viewModel.changeUserMpin(currentPinInput, newPinInput, context)
                            if (success) {
                                changePinSuccess = true
                                currentPinInput = ""
                                newPinInput = ""
                                confirmPinInput = ""
                            } else {
                                changePinError = if (state.isHindi) "वर्तमान MPIN अमान्य है!" else "Current MPIN is invalid!"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple700)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isHindi) "नया MPIN सहेजें" else "Save New MPIN",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------------------------------------
        // 3. NATIVE BIOMETRIC HARDWARE & TOGGLE CARD
        // -------------------------------------------------------------
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric",
                            tint = EmeraldGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (state.isHindi) "बायोमेट्रिक प्रमाणीकरण" else "Native Biometric Login",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Text(
                                text = if (state.isHindi) "फिंगरप्रिंट एवं फेस आईडी" else "Fingerprint & Face Unlock",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }

                    Switch(
                        checked = state.isBiometricEnabled,
                        onCheckedChange = { viewModel.toggleBiometricSetting(it, context) },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = EmeraldGreen)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hardware Status Pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (biometricStatus) {
                        BiometricStatus.AVAILABLE -> EmeraldGreen.copy(alpha = 0.1f)
                        BiometricStatus.NOT_ENROLLED -> AmberGold.copy(alpha = 0.1f)
                        else -> Color(0xFFF1F5F9)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (biometricStatus) {
                                BiometricStatus.AVAILABLE -> Icons.Default.CheckCircle
                                BiometricStatus.NOT_ENROLLED -> Icons.Default.Warning
                                else -> Icons.Default.Info
                            },
                            contentDescription = "Status",
                            tint = when (biometricStatus) {
                                BiometricStatus.AVAILABLE -> EmeraldGreen
                                BiometricStatus.NOT_ENROLLED -> AmberGold
                                else -> TextDarkSecondary
                            },
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = when (biometricStatus) {
                                BiometricStatus.AVAILABLE ->
                                    if (state.isHindi) "डिवाइस हार्डवेयर: बायोमेट्रिक सेंसर उपलब्ध एवं सक्रिय" else "Hardware: Biometric sensor enrolled & active"
                                BiometricStatus.NOT_ENROLLED ->
                                    if (state.isHindi) "डिवाइस समर्थित है लेकिन बायोमेट्रिक पंजीकृत नहीं है" else "Biometric supported but no finger/face enrolled"
                                BiometricStatus.NO_HARDWARE ->
                                    if (state.isHindi) "हार्डवेयर: सिमुलेशन मोड (एमुलेटर पर परीक्षण समर्थित)" else "Hardware: Simulation mode (Emulator testing ready)"
                                else ->
                                    if (state.isHindi) "बायोमेट्रिक स्थिति: स्टैंडबाय" else "Biometric status: Standby"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextDarkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Test Biometric Prompt Button
                OutlinedButton(
                    onClick = {
                        if (activity != null) {
                            BiometricAuthHelper.promptBiometric(
                                activity = activity,
                                isHindi = state.isHindi,
                                onSuccess = {
                                    Toast.makeText(
                                        context,
                                        if (state.isHindi) "बायोमेट्रिक प्रमाणीकरण सफल!" else "Biometric Authenticated Successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onError = { err ->
                                    Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            Toast.makeText(context, "Biometric verified (Simulation)", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple700)
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Test",
                        tint = RoyalPurple700,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isHindi) "बायोमेट्रिक सेंसर का परीक्षण करें" else "Test Native Biometric Sensor",
                        color = RoyalPurple700,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------------------------------------
        // 4. SESSION SECURITY & LOCK CARD
        // -------------------------------------------------------------
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (state.isHindi) "सत्र सुरक्षा एवं नियंत्रण" else "Session Security & Controls",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Immediate Lock Button
                Button(
                    onClick = { viewModel.lockSession() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isHindi) "सत्र अभी लॉक करें (MPIN द्वारा)" else "Lock Session Now (MPIN Required)",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Logout & Re-verify Mobile
                OutlinedButton(
                    onClick = { viewModel.logoutToMobileVerification() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = CrimsonRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isHindi) "लॉगआउट एवं नया मोबाइल नंबर सत्यापित करें" else "Logout & Verify New Mobile",
                        color = CrimsonRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
