package com.example.ui.screens

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
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentRegistrationScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi

    // 1. Personal & Family Fields
    var fullName by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var standard by remember { mutableStateOf("9th Class") }
    var classExpanded by remember { mutableStateOf(false) }
    val gradeOptions = listOf(
        "Nursery", "LKG", "UKG",
        "1st Class", "2nd Class", "3rd Class", "4th Class", "5th Class",
        "6th Class", "7th Class", "8th Class", "9th Class", "10th Class",
        "11th Class", "12th Class"
    )
    var section by remember { mutableStateOf("A") }
    var fatherName by remember { mutableStateOf("") }
    var motherName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("2012-05-15") }
    var gender by remember { mutableStateOf("Male") }
    var medium by remember { mutableStateOf("English") }

    // 2. Mobile Numbers (Father, Mother, Student)
    var fatherPhone by remember { mutableStateOf("") }
    var motherPhone by remember { mutableStateOf("") }
    var studentPhone by remember { mutableStateOf("") }

    // 3. RTE Toggle & Multi-Tier Fee Breakdown
    var isRte by remember { mutableStateOf(false) }
    var prevSchoolFee by remember { mutableStateOf("0") }
    var currSchoolFee by remember { mutableStateOf("28000") }
    var prevTransportFee by remember { mutableStateOf("0") }
    var currTransportFee by remember { mutableStateOf("9500") }

    // Live calculations
    val pSchool = if (isRte) 0L else (prevSchoolFee.toLongOrNull() ?: 0L)
    val cSchool = if (isRte) 0L else (currSchoolFee.toLongOrNull() ?: 0L)
    val pTrans = if (isRte) 0L else (prevTransportFee.toLongOrNull() ?: 0L)
    val cTrans = if (isRte) 0L else (currTransportFee.toLongOrNull() ?: 0L)
    val totalFees = pSchool + cSchool + pTrans + cTrans
    val totalCollected = 0L
    val remainingBalance = totalFees - totalCollected

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RoyalPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAddAlt1,
                                contentDescription = null,
                                tint = RoyalPurple800,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isHindi) "छात्र पंजीकरण एवं बहु-स्तरीय फीस खाता बही" else "STUDENT REGISTRATION & MULTI-TIER FEE LEDGER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Text(
                                text = if (isHindi) "CBSE/राज्य बोर्ड प्रारूप • RTE एवं अभिभावक संपर्क विवरण" else "Official School Portal • RTE 100% Zero Fee & Parent Contact Fields",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 1: Basic Information
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isHindi) "१. छात्र विवरण (Student Identity)" else "1. STUDENT PERSONAL DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple800,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text(if (isHindi) "छात्र का पूरा नाम (Full Name) *" else "Full Name (पूरा नाम) *") },
                        placeholder = { Text("e.g. Aarav Sharma") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RoyalPurple700) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rollNo,
                            onValueChange = { rollNo = it },
                            label = { Text(if (isHindi) "अनुक्रमांक (Roll No) *" else "Roll No *") },
                            placeholder = { Text("e.g. 101") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = RoyalPurple700) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        ExposedDropdownMenuBox(
                            expanded = classExpanded,
                            onExpandedChange = { classExpanded = !classExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = standard,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isHindi) "कक्षा (Class / Grade)" else "Class / Grade") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true
                            )
                            ExposedDropdownMenu(
                                expanded = classExpanded,
                                onDismissRequest = { classExpanded = false }
                            ) {
                                gradeOptions.forEach { grade ->
                                    DropdownMenuItem(
                                        text = { Text(grade, fontSize = 13.sp) },
                                        onClick = {
                                            standard = grade
                                            classExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = section,
                            onValueChange = { section = it },
                            label = { Text(if (isHindi) "सेक्शन (Section)" else "Section") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = dob,
                            onValueChange = { dob = it },
                            label = { Text(if (isHindi) "जन्म तिथि (DOB)" else "DOB (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Gender & Medium Selection Chips
                    Text(text = if (isHindi) "लिंग (Gender):" else "Gender:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDarkSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Male", "Female", "Other").forEach { g ->
                            val isSelected = gender == g
                            Surface(
                                onClick = { gender = g },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) RoyalPurple800 else Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = g,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextDarkPrimary,
                                    modifier = Modifier.padding(vertical = 7.dp).fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = if (isHindi) "माध्यम (Medium):" else "Medium of Instruction:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextDarkSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Hindi", "English").forEach { m ->
                            val isSelected = medium == m
                            Surface(
                                onClick = { medium = m },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) RoyalPurple800 else Color(0xFFF1F5F9),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (m == "Hindi") "हिंदी (Hindi)" else "English (अंग्रेज़ी)",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else TextDarkPrimary,
                                    modifier = Modifier.padding(vertical = 7.dp).fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 2: Parents & Contact Numbers
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isHindi) "२. अभिभावक एवं संपर्क विवरण (Parents & Mobile)" else "2. PARENTAGE & CONTACT PHONE NUMBERS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple800,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = fatherName,
                        onValueChange = { fatherName = it },
                        label = { Text(if (isHindi) "पिता का नाम (Father's Name)" else "Father's Name (पिता का नाम)") },
                        placeholder = { Text("e.g. Shri Rajesh Sharma") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RoyalPurple700) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = fatherPhone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) fatherPhone = it },
                        label = { Text(if (isHindi) "पिता का मोबाइल नंबर (Father's Mobile - ऐच्छिक)" else "Father's Mobile Number (पिता का मोबाइल नंबर - Optional)") },
                        placeholder = { Text("10-digit number (Optional)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalPurple700) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = motherName,
                        onValueChange = { motherName = it },
                        label = { Text(if (isHindi) "माता का नाम (Mother's Name)" else "Mother's Name (माता का नाम)") },
                        placeholder = { Text("e.g. Smt. Sunita Sharma") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = RoyalPurple700) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Mother's Mobile Number (माता का मोबाइल नंबर) - 10-digit numeric input (Optional)
                    OutlinedTextField(
                        value = motherPhone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) motherPhone = it },
                        label = { Text(if (isHindi) "माता का मोबाइल नंबर (Mother's Mobile Number)" else "Mother's Mobile Number (माता का मोबाइल नंबर)") },
                        placeholder = { Text("10-digit numeric (Optional / ऐच्छिक)") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Mother's Phone", tint = RoyalPurple700) },
                        supportingText = { Text(if (isHindi) "10-अंकों का नंबर (ऐच्छिक - खाली छोड़ सकते हैं)" else "10-digit numeric input (Optional - can leave blank)", fontSize = 10.sp, color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Student's Mobile Number (छात्र का मोबाइल नंबर) - 10-digit numeric input (Optional)
                    OutlinedTextField(
                        value = studentPhone,
                        onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) studentPhone = it },
                        label = { Text(if (isHindi) "छात्र का मोबाइल नंबर (Student's Mobile Number)" else "Student's Mobile Number (छात्र का मोबाइल नंबर)") },
                        placeholder = { Text("10-digit numeric (Optional / ऐच्छिक)") },
                        leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = "Student's Phone", tint = RoyalPurple700) },
                        supportingText = { Text(if (isHindi) "10-अंकों का नंबर (ऐच्छिक - खाली छोड़ सकते हैं)" else "10-digit numeric input (Optional - can leave blank)", fontSize = 10.sp, color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section 3: RTE Toggle & Multi-Tier Fee Ledger
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "३. बहु-स्तरीय शुल्क बहीखाता (Multi-Tier Fee Ledger)" else "3. MULTI-TIER FEE LEDGER & RTE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple800,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = if (isHindi) "स्कूल एवं वाहन शुल्क (पिछला व वर्तमान)" else "School & Transport Breakdown (Past & Present)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        // RTE Checkbox Toggle
                        Surface(
                            onClick = { isRte = !isRte },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isRte) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isRte) EmeraldGreen else Color(0xFFCBD5E1)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isRte,
                                    onCheckedChange = { isRte = it },
                                    colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "RTE (100% Free)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isRte) EmeraldGreen else TextDarkSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (isRte) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldGreen.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreen)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "RTE छात्र: सभी शुल्क स्वतः ₹0 सेट किए गए हैं" else "RTE Student: All fees set to ₹0 per RTE Act",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                    Text(
                                        text = if (isHindi) "सरकार द्वारा शत-प्रतिशत शुल्क प्रतिपूर्ति देय है।" else "100% fee waiver funded under Government RTE quota.",
                                        fontSize = 11.sp,
                                        color = TextDarkSecondary
                                    )
                                }
                            }
                        }
                    } else {
                        // School Fees (Previous & Current)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = prevSchoolFee,
                                onValueChange = { if (it.all { c -> c.isDigit() }) prevSchoolFee = it },
                                label = { Text(if (isHindi) "पिछला स्कूल शुल्क (Prev ₹)" else "Previous School Fees ₹") },
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = TextDarkSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currSchoolFee,
                                onValueChange = { if (it.all { c -> c.isDigit() }) currSchoolFee = it },
                                label = { Text(if (isHindi) "वर्तमान स्कूल शुल्क (Curr ₹)" else "Current School Fees ₹") },
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = RoyalPurple700) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Transport Fees (Previous & Current)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = prevTransportFee,
                                onValueChange = { if (it.all { c -> c.isDigit() }) prevTransportFee = it },
                                label = { Text(if (isHindi) "पिछला वाहन शुल्क (Prev ₹)" else "Previous Transport ₹") },
                                leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = TextDarkSecondary) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = currTransportFee,
                                onValueChange = { if (it.all { c -> c.isDigit() }) currTransportFee = it },
                                label = { Text(if (isHindi) "वर्तमान वाहन शुल्क (Curr ₹)" else "Current Transport ₹") },
                                leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = AmberGlow) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Auto-calculated Remaining Balance Card
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = if (isRte) Color(0xFFF0FDF4) else Color(0xFFFAF5FF)),
                        modifier = Modifier.fillMaxWidth(),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isRte) EmeraldGreen.copy(alpha = 0.3f) else RoyalPurple700.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (isHindi) "कुल देय शुल्क (Total Fees):" else "Total Applicable Fees:",
                                        fontSize = 11.sp,
                                        color = TextDarkSecondary
                                    )
                                    Text(
                                        text = "₹$totalFees",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (isHindi) "स्वतः परिकलित शेष राशि (Balance):" else "Auto-calculated Remaining:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isRte) EmeraldGreen else RoyalPurple800
                                    )
                                    Text(
                                        text = "₹$remainingBalance",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isRte) EmeraldGreen else RoyalPurple800
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                android.widget.Toast.makeText(
                                    context,
                                    if (isHindi) "कृपया छात्र का पूरा नाम दर्ज करें" else "Please enter student full name",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            viewModel.registerStudent(
                                fullName = fullName.trim(),
                                rollNo = rollNo.trim(),
                                standard = standard.trim(),
                                section = section.trim(),
                                fatherName = fatherName.trim(),
                                motherName = motherName.trim(),
                                dob = dob.trim(),
                                gender = gender,
                                medium = medium,
                                fatherPhone = fatherPhone.trim(),
                                motherPhone = motherPhone.trim(),
                                studentPhone = studentPhone.trim(),
                                isRte = isRte,
                                previousYearSchoolFees = pSchool,
                                currentSessionSchoolFees = cSchool,
                                previousYearTransportFees = pTrans,
                                currentSessionTransportFees = cTrans,
                                context = context
                            )
                            // Navigate to Fees Ledger
                            viewModel.navigateTo("FEES")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "छात्र पंजीकृत करें एवं फीस बहीखाता में जोड़ें" else "Register Student & Save Multi-Tier Ledger",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
