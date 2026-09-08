package com.example.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GovtLocationFilter
import com.example.data.GovtLocationRepository
import com.example.data.GovtStateHierarchy
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrincipalRegistrationScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel,
    onBackToLogin: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val isHindi = state.isHindi

    // Form inputs state
    var selectedPhotoSource by remember { mutableStateOf<String?>("Default Camera Capture") }
    var photoAttached by remember { mutableStateOf(true) }

    var firstName by remember { mutableStateOf("Dr. Alok") }
    var lastName by remember { mutableStateOf("Tripathi") }
    var dob by remember { mutableStateOf("1978-08-15") }
    var gender by remember { mutableStateOf("Male") } // "Male", "Female", "Other"

    var mobile by remember { mutableStateOf("9876543210") }
    var email by remember { mutableStateOf("principal.alok@academy.edu.in") }
    var schoolNameEn by remember { mutableStateOf("Kendriya Vidya Niketan") }
    var schoolNameHi by remember { mutableStateOf("केन्द्रीय विद्या निकेतन") }
    var schoolCode by remember { mutableStateOf("KVN-DL-2026") }
    var selectedManagementCategory by remember { mutableStateOf("Private Unaided (CBSE/ICSE/State Board)") }

    // Cascading Address Structure
    var selectedState by remember { mutableStateOf("Delhi (NCT)") }
    var selectedDistrict by remember { mutableStateOf("South Delhi") }
    var selectedBlock by remember { mutableStateOf("Hauz Khas Block") }
    var isRural by remember { mutableStateOf(false) } // true: Rural, false: Urban
    var panchayat by remember { mutableStateOf("Shahpur Jat Gram") }
    var village by remember { mutableStateOf("Shahpur Village") }
    var nagarPalika by remember { mutableStateOf("South Delhi Municipal Corp (SDMC)") }
    var wardNumber by remember { mutableStateOf("Ward 42-A") }

    // Dialog selector states
    var showStateSelectorDialog by remember { mutableStateOf(false) }
    var showDistrictSelectorDialog by remember { mutableStateOf(false) }
    var showBlockSelectorDialog by remember { mutableStateOf(false) }
    var showCategorySelectorDialog by remember { mutableStateOf(false) }

    // Workflow state: lock inputs upon submission and show success dialog
    var isSubmittedLocked by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showPhotoOptionsDialog by remember { mutableStateOf(false) }

    // Validation error states
    var mobileError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var firstNameError by remember { mutableStateOf<String?>(null) }
    var schoolNameError by remember { mutableStateOf<String?>(null) }

    // Date picker setup
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedMonth = (month + 1).toString().padStart(2, '0')
            val formattedDay = dayOfMonth.toString().padStart(2, '0')
            dob = "$year-$formattedMonth-$formattedDay"
        },
        1980, 7, 15
    )

    fun validateForm(): Boolean {
        var isValid = true

        if (firstName.trim().isBlank()) {
            firstNameError = if (isHindi) "प्रथम नाम आवश्यक है" else "First Name is required"
            isValid = false
        } else {
            firstNameError = null
        }

        val cleanMobile = mobile.replace("\\D".toRegex(), "")
        if (cleanMobile.length < 10) {
            mobileError = if (isHindi) "10 अंकों का मान्य मोबाइल नंबर दर्ज करें" else "Enter valid 10-digit mobile number"
            isValid = false
        } else {
            mobileError = null
        }

        if (email.isBlank() || !email.contains("@") || !email.contains(".")) {
            emailError = if (isHindi) "मान्य ईमेल आईडी दर्ज करें" else "Enter a valid email address"
            isValid = false
        } else {
            emailError = null
        }

        if (schoolNameEn.trim().isBlank()) {
            schoolNameError = if (isHindi) "स्कूल का नाम आवश्यक है" else "School name is required"
            isValid = false
        } else {
            schoolNameError = null
        }

        return isValid
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(16.dp)
    ) {
        if (onBackToLogin != null) {
            item {
                Surface(
                    onClick = onBackToLogin,
                    shape = RoundedCornerShape(12.dp),
                    color = RoyalPurple900,
                    shadowElevation = 2.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back to Login",
                            tint = BrilliantGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "← मुख्य लॉगिन पर वापस जाएं" else "← Back to Login Screen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header with Bilingual Titles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RoyalPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "School Principal",
                                tint = RoyalPurple700,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isHindi) "प्रधानाचार्य पंजीकरण फॉर्म" else "Register as School Principal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalPurple900
                            )
                            Text(
                                text = if (isHindi) "School Principal Registration • Official Institution Onboarding" else "प्रधानाचार्य पंजीकरण • आधिकारिक संस्थागत ऑनबोर्डिंग",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        if (isSubmittedLocked) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldGreenLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "जमा किया गया" else "Submitted",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Notice Banner regarding approval workflow
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFBF8EE),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrilliantGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Notice",
                                tint = BrilliantGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "सुरक्षित सत्यापन प्रक्रिया (Verification Protocol)" else "Manual Verification Protocol",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isHindi) "पंजीकरण के बाद प्रोफ़ाइल सुपर एडमिन सत्यापन हेतु भेजी जाएगी। अनुमोदन पर संस्थागत पोर्टल सक्रिय होगा।" else "After submission, profile is sent to GMPTP AI Super Admin for manual verification. Your institutional portal will activate once approved.",
                                    fontSize = 10.sp,
                                    color = TextDarkSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 1: Personal Profile Fields
                    Text(
                        text = if (isHindi) "१. व्यक्तिगत प्रोफ़ाइल (Personal Profile)" else "1. PERSONAL PROFILE FIELDS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple800,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Photo Upload Component
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, if (photoAttached) RoyalPurpleLight else BorderLight, RoundedCornerShape(12.dp))
                            .clickable(enabled = !isSubmittedLocked) {
                                showPhotoOptionsDialog = true
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(if (photoAttached) RoyalPurple800 else Color(0xFFE2E8F0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (photoAttached) Icons.Default.AccountCircle else Icons.Default.CameraAlt,
                                    contentDescription = "Principal Photo",
                                    tint = if (photoAttached) BrilliantGold else TextMuted,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "फोटो अपलोड (Photo Upload)" else "Principal Photo Upload",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (photoAttached) {
                                        if (isHindi) "फोटो चयनित: $selectedPhotoSource" else "Attached: $selectedPhotoSource"
                                    } else {
                                        if (isHindi) "लाइव कैमरा या गैलरी से फोटो अपलोड करें" else "Tap to choose Camera or Gallery"
                                    },
                                    fontSize = 10.sp,
                                    color = if (photoAttached) EmeraldGreen else TextMuted
                                )
                            }

                            if (!isSubmittedLocked) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = RoyalPurpleLight
                                ) {
                                    Text(
                                        text = if (isHindi) "बदलें" else "Choose",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurple700,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // First Name & Last Name (Separate text fields)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = {
                                if (!isSubmittedLocked) {
                                    firstName = it
                                    firstNameError = null
                                }
                            },
                            enabled = !isSubmittedLocked,
                            label = { Text(if (isHindi) "प्रथम नाम (First Name) *" else "First Name (प्रथम नाम) *") },
                            isError = firstNameError != null,
                            supportingText = firstNameError?.let { { Text(it, color = CrimsonRed, fontSize = 10.sp) } },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = lastName,
                            onValueChange = {
                                if (!isSubmittedLocked) lastName = it
                            },
                            enabled = !isSubmittedLocked,
                            label = { Text(if (isHindi) "अंतिम नाम (Last Name)" else "Last Name (अंतिम नाम)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date of Birth (DOB) Date Picker & Grid
                    OutlinedTextField(
                        value = dob,
                        onValueChange = { if (!isSubmittedLocked) dob = it },
                        enabled = !isSubmittedLocked,
                        readOnly = true,
                        label = { Text(if (isHindi) "जन्म तिथि (DOB) *" else "Date of Birth / DOB (जन्म तिथि) *") },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (!isSubmittedLocked) datePickerDialog.show()
                                },
                                enabled = !isSubmittedLocked
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = "Select DOB",
                                    tint = RoyalPurple700
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isSubmittedLocked) {
                                datePickerDialog.show()
                            }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Gender Selector (Radio Buttons: Male, Female, Other)
                    Text(
                        text = if (isHindi) "लिंग (Gender Selector): *" else "Gender Selector (लिंग): *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val genderOptions = listOf(
                            Triple("Male", "Male", "पुरुष"),
                            Triple("Female", "Female", "महिला"),
                            Triple("Other", "Other", "अन्य")
                        )

                        genderOptions.forEach { (key, en, hi) ->
                            val isSelected = gender == key
                            Surface(
                                onClick = {
                                    if (!isSubmittedLocked) gender = key
                                },
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) RoyalPurple700 else Color(0xFFF1F5F9),
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { if (!isSubmittedLocked) gender = key },
                                        enabled = !isSubmittedLocked,
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color.White,
                                            unselectedColor = TextMuted
                                        ),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "$hi / $en" else "$en / $hi",
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextDarkPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 2: Contact & Institution Fields
                    Text(
                        text = if (isHindi) "२. संपर्क एवं संस्थान विवरण (Contact & Institution)" else "2. CONTACT & INSTITUTION FIELDS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple800,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mobile Number (10-digit validated input)
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = {
                            if (!isSubmittedLocked) {
                                mobile = it
                                mobileError = null
                            }
                        },
                        enabled = !isSubmittedLocked,
                        label = { Text(if (isHindi) "मोबाइल नंबर (Mobile Number - 10 Digits) *" else "Mobile Number (मोबाइल नंबर - 10 Digits) *") },
                        leadingIcon = {
                            Text(
                                text = "+91 ",
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        },
                        isError = mobileError != null,
                        supportingText = {
                            if (mobileError != null) {
                                Text(mobileError!!, color = CrimsonRed, fontSize = 10.sp)
                            } else {
                                Text(
                                    if (isHindi) "बैकएंड प्रमाणीकरण और एसएमएस लॉगिन हेतु उपयोग होगा" else "Linked to backend chain system & MPIN login",
                                    fontSize = 9.sp,
                                    color = TextMuted
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Gmail ID (Email format validated input)
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            if (!isSubmittedLocked) {
                                email = it
                                emailError = null
                            }
                        },
                        enabled = !isSubmittedLocked,
                        label = { Text(if (isHindi) "ईमेल आईडी (Gmail / Official ID) *" else "Gmail ID / Official Email (ईमेल आईडी) *") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                tint = RoyalPurple700,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it, color = CrimsonRed, fontSize = 10.sp) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // School Name in English
                    OutlinedTextField(
                        value = schoolNameEn,
                        onValueChange = {
                            if (!isSubmittedLocked) {
                                schoolNameEn = it
                                schoolNameError = null
                            }
                        },
                        enabled = !isSubmittedLocked,
                        label = { Text(if (isHindi) "स्कूल का नाम - इंग्लिश में (School Name in English) *" else "School Name in English (स्कूल का नाम - इंग्लिश में) *") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Domain, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                        },
                        isError = schoolNameError != null,
                        supportingText = schoolNameError?.let { { Text(it, color = CrimsonRed, fontSize = 10.sp) } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // School Name in Hindi
                    OutlinedTextField(
                        value = schoolNameHi,
                        onValueChange = {
                            if (!isSubmittedLocked) schoolNameHi = it
                        },
                        enabled = !isSubmittedLocked,
                        label = { Text(if (isHindi) "स्कूल का नाम - हिंदी में (School Name in Hindi)" else "School Name in Hindi (स्कूल का नाम - हिंदी में)") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Affiliation / School Code & UDISE+ Standard
                    OutlinedTextField(
                        value = schoolCode,
                        onValueChange = {
                            if (!isSubmittedLocked) schoolCode = it
                        },
                        enabled = !isSubmittedLocked,
                        label = { Text(if (isHindi) "स्कूल संबद्धता / UDISE+ कोड (School / UDISE+ Code)" else "School / UDISE+ Code (संबद्धता कोड)") },
                        supportingText = {
                            Text(
                                text = if (isHindi) "मानक UDISE+ 11-अंकीय कोड अथवा बोर्ड संबद्धता संख्या" else "Standard 11-digit UDISE+ code or official board affiliation code",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Tag, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Institutional Management Category
                    Text(
                        text = if (isHindi) "संस्थान प्रबंधन श्रेणी (Management Category): *" else "Institutional Management Category: *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { if (!isSubmittedLocked) showCategorySelectorDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = selectedManagementCategory,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextDarkPrimary
                                )
                            }
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select", tint = RoyalPurple700)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 3: Cascading Address Fields (Government Portal Architecture)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "३. पदानुक्रमित पता संरचना (Government Portal Hierarchy)" else "3. CASCADING ADDRESS FIELDS (GOVT PORTAL)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple800,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = if (isHindi) "राज्य ➔ जिला ➔ ब्लॉक ➔ ग्रामीण/शहरी ➔ स्थानीय निकाय" else "State ➔ District ➔ Block ➔ Area ➔ Local Body",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurple800.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = "UDISE+ Hierarchy",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple800,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamic Breadcrumb Stepper
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedState,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple900
                            )
                            Text(text = " ➔ ", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = selectedDistrict,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple800
                            )
                            Text(text = " ➔ ", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = selectedBlock,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkPrimary
                            )
                            Text(text = " ➔ ", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = if (isRural) (if (isHindi) "ग्रामीण" else "Rural") else (if (isHindi) "शहरी" else "Urban"),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrilliantGoldDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step 1: Cascading State Selector
                    Text(
                        text = if (isHindi) "चरण १: राज्य का चयन करें (State) *" else "Step 1: Select State (राज्य) *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { if (!isSubmittedLocked) showStateSelectorDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.Place, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = selectedState,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = if (isHindi) "क्लिक करके राज्य बदलें (स्वचालित जिला फिल्टर)" else "Tap to change state (cascades districts)",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select State", tint = RoyalPurple700)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step 2: Cascading District Selector
                    Text(
                        text = if (isHindi) "चरण २: जिला का चयन करें (District in $selectedState) *" else "Step 2: Select District ($selectedState) *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { if (!isSubmittedLocked) showDistrictSelectorDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.LocationCity, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = selectedDistrict,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = if (isHindi) "$selectedState के अंतर्गत जिले" else "Districts affiliated under $selectedState",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select District", tint = RoyalPurple700)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step 3: Cascading Block / Tehsil Selector
                    Text(
                        text = if (isHindi) "चरण ३: ब्लॉक / तहसील का चयन करें (Block in $selectedDistrict) *" else "Step 3: Select Block / Tehsil ($selectedDistrict) *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        onClick = { if (!isSubmittedLocked) showBlockSelectorDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(imageVector = Icons.Default.Explore, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = selectedBlock,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = if (isHindi) "$selectedDistrict के अंतर्गत प्रशासनिक ब्लॉक" else "Administrative blocks under $selectedDistrict",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Select Block", tint = RoyalPurple700)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 4: Rural vs Urban Selector
                    Text(
                        text = if (isHindi) "चरण ४: क्षेत्र वर्गीकरण (Area Classification): *" else "Step 4: Area Classification (क्षेत्र वर्गीकरण): *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { if (!isSubmittedLocked) isRural = true },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isRural) RoyalPurple700 else Color(0xFFF1F5F9),
                            border = if (isRural) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Nature,
                                    contentDescription = null,
                                    tint = if (isRural) BrilliantGold else TextDarkSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "ग्रामीण (Rural)" else "Rural / ग्रामीण",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRural) Color.White else TextDarkPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Surface(
                            onClick = { if (!isSubmittedLocked) isRural = false },
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isRural) RoyalPurple700 else Color(0xFFF1F5F9),
                            border = if (!isRural) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = null,
                                    tint = if (!isRural) BrilliantGold else TextDarkSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "शहरी (Urban)" else "Urban / शहरी",
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isRural) Color.White else TextDarkPrimary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Step 5: Cascading Dynamic Local Units based on Rural/Urban
                    Text(
                        text = if (isHindi) "चरण ५: स्थानीय निकाय विवरण (Local Unit Details): *" else "Step 5: Local Unit Details (स्थानीय निकाय): *",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    if (isRural) {
                        OutlinedTextField(
                            value = panchayat,
                            onValueChange = { if (!isSubmittedLocked) panchayat = it },
                            enabled = !isSubmittedLocked,
                            label = { Text(if (isHindi) "ग्राम पंचायत (Gram Panchayat) *" else "Gram Panchayat (ग्राम पंचायत) *") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Nature, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = village,
                            onValueChange = { if (!isSubmittedLocked) village = it },
                            enabled = !isSubmittedLocked,
                            label = { Text(if (isHindi) "गाँव / मजरा (Village / Habitation) *" else "Village / Habitation (गाँव) *") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.HolidayVillage, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        OutlinedTextField(
                            value = nagarPalika,
                            onValueChange = { if (!isSubmittedLocked) nagarPalika = it },
                            enabled = !isSubmittedLocked,
                            label = { Text(if (isHindi) "नगर पालिका / निगम (Municipality) *" else "Municipality / Nagar Palika (नगर पालिका) *") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Apartment, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = wardNumber,
                            onValueChange = { if (!isSubmittedLocked) wardNumber = it },
                            enabled = !isSubmittedLocked,
                            label = { Text(if (isHindi) "वार्ड संख्या / प्रभाग (Ward Number) *" else "Ward Number (वार्ड संख्या) *") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Numbers, contentDescription = null, tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Locked Submission Indicator or Submit Button
                    if (isSubmittedLocked) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Locked",
                                        tint = RoyalPurple800,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "आवेदन सुरक्षित रूप से लॉक कर दिया गया है" else "Application Form Locked Upon Submission",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = TextDarkPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isHindi) "प्रोफ़ाइल सुपर एडमिन सत्यापन के लिए कतार में है।" else "Profile has been transmitted to GMPTP AI Super Admin queue.",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { showSuccessDialog = true },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (isHindi) "स्थिति देखें" else "View Status", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { isSubmittedLocked = false },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(if (isHindi) "संपादित करें" else "Unlock / Edit", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                if (validateForm()) {
                                    // 1. Lock form inputs
                                    isSubmittedLocked = true

                                    // 2. Dispatch to ViewModel (Super Admin approval queue)
                                    viewModel.registerPrincipal(
                                        schoolNameEn = schoolNameEn,
                                        schoolNameHi = schoolNameHi,
                                        schoolCode = schoolCode,
                                        principalName = "$firstName $lastName".trim(),
                                        mobile = mobile,
                                        email = email,
                                        location = GovtLocationFilter(
                                            state = selectedState,
                                            district = selectedDistrict,
                                            block = selectedBlock,
                                            isRural = isRural,
                                            panchayat = if (isRural) panchayat else "",
                                            village = if (isRural) village else "",
                                            nagarPalika = if (!isRural) nagarPalika else "",
                                            wardNumber = if (!isRural) wardNumber else ""
                                        ),
                                        directActivate = false,
                                        context = context
                                    )

                                    // 3. Display exact requested success dialog
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(
                                        context,
                                        if (isHindi) "कृपया सभी आवश्यक फ़ील्ड सही तरीके से भरें।" else "Please fill all required fields correctly.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Register",
                                tint = BrilliantGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "संस्थागत पंजीकरण जमा करें" else "Submit Institutional Registration",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Cascading State Selector Dialog
    if (showStateSelectorDialog) {
        AlertDialog(
            onDismissRequest = { showStateSelectorDialog = false },
            title = {
                Text(
                    text = if (isHindi) "राज्य का चयन करें (Select State)" else "Select State (राज्य)",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "राज्य बदलने पर जिले व ब्लॉक स्वतः रीसेट हो जाएंगे:" else "Selecting a state automatically cascades affiliated districts & blocks:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    GovtLocationRepository.statesList.forEach { stateItem ->
                        val isSelected = stateItem.stateName == selectedState
                        Surface(
                            onClick = {
                                selectedState = stateItem.stateName
                                val districts = GovtLocationRepository.getDistrictsForState(stateItem.stateName)
                                selectedDistrict = districts.firstOrNull() ?: ""
                                selectedBlock = GovtLocationRepository.getBlocksForDistrict(stateItem.stateName, selectedDistrict).firstOrNull() ?: ""
                                showStateSelectorDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RoyalPurple700 else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalPurple700 else BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = stateItem.stateName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else TextDarkPrimary
                                    )
                                    Text(
                                        text = stateItem.stateNameHi,
                                        fontSize = 10.sp,
                                        color = if (isSelected) BrilliantGold else TextMuted
                                    )
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = BrilliantGold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showStateSelectorDialog = false }) {
                    Text(if (isHindi) "बंद करें" else "Close", color = RoyalPurple800)
                }
            }
        )
    }

    // Cascading District Selector Dialog
    if (showDistrictSelectorDialog) {
        val availableDistricts = GovtLocationRepository.getDistrictsForState(selectedState)
        AlertDialog(
            onDismissRequest = { showDistrictSelectorDialog = false },
            title = {
                Text(
                    text = if (isHindi) "जिला चुनें ($selectedState)" else "Select District ($selectedState)",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "$selectedState राज्य के अधीन जिले:" else "Districts affiliated under $selectedState:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    availableDistricts.forEach { distName ->
                        val isSelected = distName == selectedDistrict
                        Surface(
                            onClick = {
                                selectedDistrict = distName
                                selectedBlock = GovtLocationRepository.getBlocksForDistrict(selectedState, distName).firstOrNull() ?: ""
                                showDistrictSelectorDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RoyalPurple700 else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalPurple700 else BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = distName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else TextDarkPrimary
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = BrilliantGold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDistrictSelectorDialog = false }) {
                    Text(if (isHindi) "बंद करें" else "Close", color = RoyalPurple800)
                }
            }
        )
    }

    // Cascading Block / Tehsil Selector Dialog
    if (showBlockSelectorDialog) {
        val availableBlocks = GovtLocationRepository.getBlocksForDistrict(selectedState, selectedDistrict)
        AlertDialog(
            onDismissRequest = { showBlockSelectorDialog = false },
            title = {
                Text(
                    text = if (isHindi) "ब्लॉक / तहसील चुनें ($selectedDistrict)" else "Select Block / Tehsil ($selectedDistrict)",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isHindi) "$selectedDistrict जिले के अंतर्गत ब्लॉक:" else "Administrative blocks under $selectedDistrict:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    availableBlocks.forEach { blockName ->
                        val isSelected = blockName == selectedBlock
                        Surface(
                            onClick = {
                                selectedBlock = blockName
                                showBlockSelectorDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RoyalPurple700 else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalPurple700 else BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = blockName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) Color.White else TextDarkPrimary
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = BrilliantGold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBlockSelectorDialog = false }) {
                    Text(if (isHindi) "बंद करें" else "Close", color = RoyalPurple800)
                }
            }
        )
    }

    // Category Selector Dialog
    if (showCategorySelectorDialog) {
        AlertDialog(
            onDismissRequest = { showCategorySelectorDialog = false },
            title = {
                Text(
                    text = if (isHindi) "संस्थान श्रेणी का चयन करें" else "Select Institutional Category",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    GovtLocationRepository.schoolManagementCategories.forEach { category ->
                        val isSelected = category == selectedManagementCategory
                        Surface(
                            onClick = {
                                selectedManagementCategory = category
                                showCategorySelectorDialog = false
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) RoyalPurple700 else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) RoyalPurple700 else BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = category,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else TextDarkPrimary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Selected", tint = BrilliantGold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategorySelectorDialog = false }) {
                    Text(if (isHindi) "बंद करें" else "Close", color = RoyalPurple800)
                }
            }
        )
    }

    // Interactive Photo Source Selection Dialog (Camera / Gallery)
    if (showPhotoOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = {
                Text(
                    text = if (isHindi) "प्रधानाचार्य फोटो का चयन करें" else "Upload Principal Photo",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900,
                    fontSize = 15.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isHindi) "लाइव कैमरा या डिवाइस गैलरी में से किसी एक को चुनें:" else "Choose source for principal identification photo:",
                        fontSize = 12.sp,
                        color = TextDarkSecondary
                    )

                    Surface(
                        onClick = {
                            selectedPhotoSource = "Live Camera Capture"
                            photoAttached = true
                            showPhotoOptionsDialog = false
                            Toast.makeText(context, if (isHindi) "लाइव कैमरा फोटो संलग्न की गई" else "Photo captured via Live Camera", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", tint = RoyalPurple700)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(if (isHindi) "लाइव कैमरा कैप्चर (Live Camera)" else "Live Camera Capture", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(if (isHindi) "डिवाइस कैमरा से तुरंत तस्वीर लें" else "Take a real-time face photo", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }

                    Surface(
                        onClick = {
                            selectedPhotoSource = "Device Gallery Upload"
                            photoAttached = true
                            showPhotoOptionsDialog = false
                            Toast.makeText(context, if (isHindi) "गैलरी से फोटो संलग्न की गई" else "Photo loaded from Device Gallery", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = RoyalPurple700)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(if (isHindi) "गैलरी से चुनें (Gallery Upload)" else "Select from Gallery", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(if (isHindi) "डिवाइस स्टोरेज से फोटो चुनें" else "Choose existing photo from storage", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showPhotoOptionsDialog = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel", color = TextMuted)
                }
            }
        )
    }

    // Workflow Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(36.dp)
                    )
                }
            },
            title = {
                Text(
                    text = if (isHindi) "पंजीकरण सफल! (Registration Successful!)" else "Registration Successful!",
                    fontWeight = FontWeight.ExtraBold,
                    color = RoyalPurple900,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Registration Successful! Your profile is sent to GMPTP AI Super Admin for manual verification. Your institutional portal will activate once approved.",
                        fontSize = 13.sp,
                        color = TextDarkPrimary,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 18.sp
                    )

                    if (isHindi) {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BorderLight)
                        Text(
                            text = "पंजीकरण सफल! आपकी प्रोफ़ाइल मैन्युअल सत्यापन हेतु GMPTP AI सुपर एडमिन को भेज दी गई है। अनुमोदन के उपरांत आपका संस्थागत पोर्टल सक्रिय हो जाएगा।",
                            fontSize = 12.sp,
                            color = TextDarkSecondary,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF8FAFC),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "School: $schoolNameEn",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Text(
                                text = "Principal: $firstName $lastName • $mobile",
                                fontSize = 10.sp,
                                color = TextDarkSecondary
                            )
                            Text(
                                text = "Location: $selectedBlock, $selectedDistrict, $selectedState • Status: Pending Verification",
                                fontSize = 10.sp,
                                color = BrilliantGoldDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(if (isHindi) "ठीक है (Understood)" else "OK / Understood", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
