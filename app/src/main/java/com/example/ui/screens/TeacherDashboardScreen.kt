package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.FeeApprovalStatus
import com.example.data.HomeworkItem
import com.example.data.Student
import com.example.data.UserRole
import com.example.ui.components.AdMobBannerComponent
import com.example.ui.components.StaffSalaryLedgerWidget
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherDashboardScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    val teacher = state.currentUser
    val school = state.currentSchool

    var selectedClass by remember { mutableStateOf("Class 9 - Section B") }
    var classDropdownExpanded by remember { mutableStateOf(false) }
    val assignedClasses = listOf("Class 8 - Section A", "Class 9 - Section B", "Class 10 - Section A")

    var showFeeDialogForStudent by remember { mutableStateOf<Student?>(null) }
    var feeAmountInput by remember { mutableStateOf("2500") }
    var feeCategoryInput by remember { mutableStateOf("School Fee (Term 1)") }

    var showHomeworkDialog by remember { mutableStateOf(false) }
    var newHwSubject by remember { mutableStateOf("Mathematics") }
    var newHwTitle by remember { mutableStateOf("Polynomials Exercise 3.2") }
    var newHwDesc by remember { mutableStateOf("Complete questions 1 to 10 with step-by-step formulas.") }
    var newHwDueDate by remember { mutableStateOf("Tomorrow, 08:30 AM") }

    var showClassGroupMessageDialog by remember { mutableStateOf(false) }
    var showDocSyncDialog by remember { mutableStateOf(false) }
    var showTimetableDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Multi-School Teacher Mapping Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalPurple800),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(BrilliantGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apartment,
                                    contentDescription = "Multi School",
                                    tint = BrilliantGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "बहु-विद्यालय शिक्षक मैपिंग" else "Multi-School Teacher Mapping",
                                    color = BrilliantGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isHindi) "3 स्वतंत्र विद्यालयों से संबद्ध" else "Linked across 3 Independent Schools",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrilliantGold
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = RoyalPurple900,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "सक्रिय विद्यालय: ${school.nameHi}" else "Active Campus: ${school.nameEn}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Persistent AdMob Banner (Mandatory on Free-tier Teacher Dashboard)
        item {
            AdMobBannerComponent(
                userRole = UserRole.TEACHER,
                trialDaysRemaining = 0,
                hasPaidSubscription = false,
                viewModel = viewModel
            )
        }

        // Class Selector: Direct Dropdown to select Assigned Class and Section
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isHindi) "कक्षा एवं सेक्शन चयनकर्ता" else "ASSIGNED CLASS & SECTION SELECTOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = classDropdownExpanded,
                        onExpandedChange = { classDropdownExpanded = !classDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedClass,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = classDropdownExpanded,
                            onDismissRequest = { classDropdownExpanded = false }
                        ) {
                            assignedClasses.forEach { cls ->
                                DropdownMenuItem(
                                    text = { Text(cls, fontWeight = FontWeight.SemiBold) },
                                    onClick = {
                                        selectedClass = cls
                                        classDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fast Operational Grid on Home Screen:
        // Mirroring the 8 Teacher Sidebar Menu Options:
        // 'Mark Student Attendance', 'Add/Register New Students', 'Classroom WhatsApp-Style Chat Groups',
        // 'AI Exam OCR Scanner', 'Homework/Notice Uploader', 'Collect Fee Tokens', 'My Read-Only Salary Slip', 'Logout'
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "शिक्षक त्वरित परिचालन ग्रिड (QUICK ACTIONS)" else "TEACHER OPERATIONAL QUICK-ACTION GRID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkSecondary,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Mark Student Attendance & Add/Register New Students
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherOpCard(
                        title = if (isHindi) "छात्र उपस्थिति दर्ज करें" else "Mark Student Attendance",
                        subtitle = if (isHindi) "मासिक कैलेंडर व संशोधन" else "Monthly Calendar View",
                        icon = Icons.Default.FactCheck,
                        color = EmeraldGreen,
                        onClick = { viewModel.navigateTo("ATTENDANCE") },
                        modifier = Modifier.weight(1f)
                    )
                    TeacherOpCard(
                        title = if (isHindi) "नए छात्र जोड़ें / पंजीकृत करें" else "Add/Register Students",
                        subtitle = if (isHindi) "प्रधानाचार्य डैशबोर्ड स्वतः सिंक" else "Instant Principal Sync",
                        icon = Icons.Default.PersonAdd,
                        color = RoyalPurple700,
                        onClick = { viewModel.navigateTo("REGISTER_STUDENT") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Classroom WhatsApp-Style Chat Groups & Homework/Notice Uploader
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherOpCard(
                        title = if (isHindi) "कक्षा व्हाट्सएप चैट ग्रुप्स" else "Classroom WhatsApp Groups",
                        subtitle = if (isHindi) "अभिभावकों व छात्रों को संदेश" else "Broadcast to Class Parents",
                        icon = Icons.Default.Forum,
                        color = EmeraldGreen,
                        badgeCount = state.classGroupMessages.count { it.className == selectedClass },
                        onClick = { showClassGroupMessageDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    TeacherOpCard(
                        title = if (isHindi) "गृहकार्य / सूचना अपलोडर" else "Homework/Notice Uploader",
                        subtitle = if (isHindi) "असाइनमेंट व नियत तिथि" else "Tasks & Attachments",
                        icon = Icons.Default.Assignment,
                        color = ElectricBlue,
                        badgeCount = state.homeworkList.size,
                        onClick = { showHomeworkDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: AI Exam OCR Scanner & Document Cloud Sync
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherOpCard(
                        title = if (isHindi) "AI परीक्षा ओसीआर स्कैनर" else "AI Exam OCR Scanner",
                        subtitle = if (isHindi) "उत्तर-पुस्तिका स्कैन व ग्रेड" else "OCR Grading & Scores",
                        icon = Icons.Default.DocumentScanner,
                        color = RoyalPurple800,
                        onClick = { viewModel.navigateTo("EXAM_SCANNER") },
                        modifier = Modifier.weight(1f)
                    )
                    TeacherOpCard(
                        title = if (isHindi) "दस्तावेज़ क्लाउड सिंक" else "Document Cloud Sync",
                        subtitle = if (isHindi) "आधार, जन्म प्रमाण पत्र व टीसी" else "Student Records & Drive Sync",
                        icon = Icons.Default.CloudSync,
                        color = ElectricBlue,
                        onClick = { showDocSyncDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 4: My Read-Only Salary Slip & Collect Fee Tokens
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherOpCard(
                        title = if (isHindi) "मेरी वेतन पर्ची (केवल-पठनीय)" else "My Read-Only Salary Slip",
                        subtitle = if (isHindi) "वेतन पर्ची, अग्रिम व शेष देय" else "Monthly Salary & Slips",
                        icon = Icons.Default.Receipt,
                        color = RoyalPurple700,
                        onClick = { viewModel.navigateTo("TEACHER_SALARY") },
                        modifier = Modifier.weight(1f)
                    )
                    TeacherOpCard(
                        title = if (isHindi) "शुल्क टोकन संग्रह" else "Collect Fee Tokens",
                        subtitle = if (isHindi) "प्रधानाचार्य अनुमोदन हेतु" else "Log Student Payments",
                        icon = Icons.Default.CurrencyRupee,
                        color = AmberGlow,
                        onClick = { showFeeDialogForStudent = state.students.firstOrNull() },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 5: Class Timetable & Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeacherOpCard(
                        title = if (isHindi) "कक्षा समय सारिणी" else "Class Timetable",
                        subtitle = if (isHindi) "दैनिक घंटी व विषय आवंटन" else "Daily Periods & Schedule",
                        icon = Icons.Default.CalendarMonth,
                        color = AmberGlow,
                        onClick = { showTimetableDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    TeacherOpCard(
                        title = if (isHindi) "सुरक्षित लॉगआउट" else "Logout",
                        subtitle = if (isHindi) "सत्र समाप्त करें एवं लॉक करें" else "Secure Lock & Gateway",
                        icon = Icons.Default.Lock,
                        color = TextDarkSecondary,
                        onClick = { viewModel.logout() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Dedicated Staff Salary Card/Widget: Prominent on Home Screen (Read-Only)
        item {
            StaffSalaryLedgerWidget(
                state = state,
                isDriverRole = false
            )
        }

        // Relational Chain Real-Time Sync Indicator
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldGreen.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync",
                        tint = EmeraldGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isHindi) "पारस्परिक श्रृंखला सक्रिय: शिक्षक -> प्रधानाचार्य स्वतः सिंक" else "Relational Chain Active: Instant Teacher -> Principal Sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                        Text(
                            text = if (isHindi) "नया पंजीकृत छात्र तुरंत प्रधानाचार्य के डैशबोर्ड में दिखाई देगा" else "New student registrations instantly reflect on Principal Executive Hub",
                            fontSize = 10.sp,
                            color = TextDarkSecondary
                        )
                    }
                }
            }
        }

        // Student Roster & Teacher Fee Collection Trigger
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = if (isHindi) "छात्र रोस्टर ($selectedClass)" else "STUDENT ROSTER ($selectedClass)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )
                    Text(
                        text = if (isHindi) "फीस संग्रह करें (प्रधानाचार्य अनुमोदन हेतु जाएगा)" else "Collect Fee (Dispatches to Principal for Approval)",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = RoyalPurpleLight
                ) {
                    Text(
                        text = "${state.students.size} Students",
                        color = RoyalPurple700,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }

        items(state.students) { student ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (student.isRte) EmeraldGreen.copy(alpha = 0.15f) else RoyalPurpleLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.rollNo,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.isRte) EmeraldGreen else RoyalPurple800
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                if (student.isRte) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldGreen
                                    ) {
                                        Text(
                                            text = "RTE FREE",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Roll #${student.rollNo} • ${student.medium} • Att: ${student.attendancePercentage}%",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                            if (student.isRte) {
                                Text(
                                    text = if (isHindi) "RTE श्रेणी: फीस देय शून्य (₹0)" else "RTE Category: Fee Dues ₹0",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldGreen
                                )
                            } else {
                                Text(
                                    text = "Dues: ₹${student.previousYearDues + student.currentSessionFees}",
                                    fontSize = 10.sp,
                                    color = CrimsonRed
                                )
                            }
                        }
                    }

                    if (!student.isRte) {
                        Surface(
                            onClick = {
                                showFeeDialogForStudent = student
                                feeAmountInput = "2500"
                            },
                            shape = RoundedCornerShape(8.dp),
                            color = AmberGlow
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddCard,
                                    contentDescription = "Collect",
                                    tint = RoyalPurple900,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "फीस लें" else "Collect",
                                    color = RoyalPurple900,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Teacher Fee Collection Logging Dialog
    showFeeDialogForStudent?.let { student ->
        Dialog(onDismissRequest = { showFeeDialogForStudent = null }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = if (isHindi) "छात्र शुल्क संग्रह टोकन दर्ज करें" else "Log Student Fee Collection",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900
                    )
                    Text(
                        text = "Student: ${student.name} (Roll #${student.rollNo})",
                        fontSize = 12.sp,
                        color = TextDarkSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = feeAmountInput,
                        onValueChange = { feeAmountInput = it },
                        label = { Text("Amount Collected (₹)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = feeCategoryInput,
                        onValueChange = { feeCategoryInput = it },
                        label = { Text("Fee Category") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isHindi)
                            "नोट: यह राशि तुरंत प्रधानाचार्य की स्क्रीन पर अनुमोदन हेतु प्रदर्शित होगी। स्वीकृति मिलते ही अभिभावक को WhatsApp रसीद जाएगी।"
                        else
                            "Note: This token will appear in Principal's Approval Queue. Upon verification, WhatsApp receipt is dispatched to parents.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showFeeDialogForStudent = null }) {
                            Text("Cancel")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val amt = feeAmountInput.toLongOrNull() ?: 2500L
                                viewModel.logFeeCollectionByTeacher(
                                    studentId = student.id,
                                    studentName = student.name,
                                    standard = student.standard,
                                    amount = amt,
                                    feeCategory = feeCategoryInput,
                                    context = context
                                )
                                showFeeDialogForStudent = null
                            },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                        ) {
                            Text("Submit for Approval", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    // Daily Homework Dialog
    if (showHomeworkDialog) {
        Dialog(onDismissRequest = { showHomeworkDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isHindi) "दैनिक गृहकार्य प्रबंधन" else "Daily Homework Hub",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple900
                        )
                        IconButton(onClick = { showHomeworkDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    state.homeworkList.forEach { hw ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${hw.subject}: ${hw.title}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = "${hw.description} • Due: ${hw.dueDate}",
                                    fontSize = 10.sp,
                                    color = TextDarkSecondary
                                )
                            }
                            IconButton(onClick = { viewModel.toggleHomework(hw.id) }) {
                                Icon(
                                    imageVector = if (hw.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = "Toggle",
                                    tint = if (hw.isCompleted) EmeraldGreen else TextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showHomeworkDialog = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done", color = Color.White)
                    }
                }
            }
        }
    }

    // ==================== CLASS GROUP MESSAGING DIALOG ====================
    if (showClassGroupMessageDialog) {
        var groupMsgInput by remember { mutableStateOf("") }
        val classMessages = state.classGroupMessages.filter { it.className == selectedClass }

        Dialog(onDismissRequest = { showClassGroupMessageDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(RoyalPurpleLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = "Chat",
                                    tint = RoyalPurple800,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "कक्षा समूह संदेश" else "Class Group Messaging",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = "$selectedClass • Direct Parents Channel",
                                    fontSize = 10.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        IconButton(onClick = { showClassGroupMessageDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Message history list
                    Text(
                        text = if (isHindi) "हालिया कक्षा संदेश:" else "Recent Class Broadcasts:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (classMessages.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isHindi) "इस कक्षा हेतु कोई संदेश नहीं भेजा गया।" else "No messages sent yet for this class.",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            }
                        } else {
                            classMessages.forEach { msg ->
                                Surface(
                                    color = RoyalPurple50,
                                    shape = RoundedCornerShape(10.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurpleLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(msg.senderName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = RoyalPurple900)
                                            Text(msg.formattedTime, fontSize = 9.sp, color = TextMuted)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(msg.message, fontSize = 12.sp, color = TextDarkPrimary)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Compose New Message Input
                    OutlinedTextField(
                        value = groupMsgInput,
                        onValueChange = { groupMsgInput = it },
                        label = { Text(if (isHindi) "नया संदेश लिखें (असाइनमेंट, प्रोजेक्ट या सूचना)" else "Compose notice or assignment details") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (groupMsgInput.isNotBlank()) {
                                viewModel.sendClassGroupMessage(
                                    className = selectedClass,
                                    message = groupMsgInput,
                                    context = context
                                )
                                groupMsgInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "समूह में प्रसारित करें" else "Broadcast to Class Parents",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    if (showDocSyncDialog) {
        Dialog(onDismissRequest = { showDocSyncDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ElectricBlue.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = "Sync", tint = ElectricBlue, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "दस्तावेज़ क्लाउड सिंक" else "Document Cloud Sync",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = if (isHindi) "आधार, जन्म प्रमाण पत्र व टीसी सिंक" else "Aadhaar, Birth Cert & TC Cloud Sync",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        IconButton(onClick = { showDocSyncDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sync Status Card
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldGreen.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "Google Cloud / Firebase Storage: कनेक्टेड" else "Google Cloud / Firebase Storage: CONNECTED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = if (isHindi) "32 दस्तावेज़ सुरक्षित क्लाउड पर संग्रहीत हैं।" else "32 documents securely encrypted & synced.",
                                    fontSize = 10.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isHindi) "हाल के छात्र दस्तावेज़ ($selectedClass):" else "Student Documents ($selectedClass):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val docs = listOf(
                        Triple("Aarav Sharma", "Aadhaar Card (UIDAI)", "SYNCED"),
                        Triple("Priya Verma", "Birth Certificate (Nagar Nigam)", "SYNCED"),
                        Triple("Rohan Singh", "Previous School TC / Marksheet", "PENDING_SYNC"),
                        Triple("Ananya Mishra", "Caste / Category Certificate", "SYNCED"),
                        Triple("Aditya Patel", "Immunization & Medical Record", "PENDING_SYNC")
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(docs) { (name, docType, status) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                        Text(docType, fontSize = 11.sp, color = TextDarkSecondary)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (status == "SYNCED") EmeraldGreen.copy(alpha = 0.15f) else AmberGlow.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (status == "SYNCED") EmeraldGreen else Color(0xFFB45309),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            android.widget.Toast.makeText(context, "All student documents synced to Firebase Cloud Storage!", android.widget.Toast.LENGTH_SHORT).show()
                            showDocSyncDialog = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isHindi) "सभी दस्तावेज़ क्लाउड पर सिंक करें" else "Sync All to Cloud Drive", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showTimetableDialog) {
        Dialog(onDismissRequest = { showTimetableDialog = false }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .padding(vertical = 16.dp)
            ) {
                var selectedDay by remember { mutableStateOf("Monday") }
                val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AmberGlow.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = "Timetable", tint = AmberGlow, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "कक्षा समय सारिणी" else "Class Schedule",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = "$selectedClass • $selectedDay",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        IconButton(onClick = { showTimetableDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Day selector tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        days.forEach { d ->
                            FilterChip(
                                selected = selectedDay == d,
                                onClick = { selectedDay = d },
                                label = { Text(d, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalPurple800,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Periods for class and day
                    val dayPeriods = state.timetablePeriods.filter {
                        (it.standard.contains("9") || selectedClass.contains(it.standard)) && it.dayOfWeek == selectedDay
                    }

                    if (dayPeriods.isEmpty()) {
                        val samplePeriods = listOf(
                            Triple("Period 1 (08:30 - 09:15 AM)", "Mathematics", "Room 102"),
                            Triple("Period 2 (09:15 - 10:00 AM)", "Physics & Lab", "Science Lab 1"),
                            Triple("Period 3 (10:15 - 11:00 AM)", "English Literature", "Room 102"),
                            Triple("Period 4 (11:00 - 11:45 AM)", "Hindi Grammar", "Room 102"),
                            Triple("Period 5 (12:30 - 01:15 PM)", "Social Science & History", "Room 102"),
                            Triple("Period 6 (01:15 - 02:00 PM)", "Computer Applications", "IT Lab 2")
                        )

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(samplePeriods) { (time, sub, room) ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(sub, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                            Text(time, fontSize = 11.sp, color = TextDarkSecondary)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = RoyalPurpleLight
                                        ) {
                                            Text(
                                                text = room,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RoyalPurple800,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(dayPeriods) { p ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = CircleShape,
                                                color = RoyalPurple800,
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("P${p.periodNumber}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(p.subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                                Text("${p.startTime} - ${p.endTime}", fontSize = 10.sp, color = TextDarkSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showTimetableDialog = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple900),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isHindi) "बंद करें" else "Close", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherOpCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(ElectricBlue)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$badgeCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkPrimary
            )

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextDarkSecondary,
                maxLines = 1
            )
        }
    }
}
