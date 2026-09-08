package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.UserRole
import com.example.ui.components.AdMobBannerComponent
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun ParentStudentDashboardScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    val student = state.students.firstOrNull() ?: return
    val school = state.currentSchool
    val bus = state.buses.firstOrNull()
    var showQrDialog by remember { mutableStateOf(false) }
    var showReportCardDialog by remember { mutableStateOf(false) }
    var showNoticesDialog by remember { mutableStateOf(false) }
    var showAnnouncementsDialog by remember { mutableStateOf(false) }
    var showStudentTimetableDialog by remember { mutableStateOf(false) }
    var showAllHomeworkDialog by remember { mutableStateOf(false) }

    val totalDues = if (student.isRte) 0L else (
        student.previousYearDues +
        student.currentSessionFees +
        student.previousYearTransportDues +
        student.currentSessionTransportFees
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Enhanced Student Profile Header Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalPurple900, RoyalPurple700)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(BrilliantGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = student.name.take(1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = RoyalPurple900
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = student.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${student.standard} - Sec ${student.section} • Roll #${student.rollNo}",
                                    fontSize = 12.sp,
                                    color = BrilliantGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${if (student.medium == "HINDI") "हिंदी माध्यम" else "English Medium"} • ${student.gender}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (student.isRte) EmeraldGreen else Color.White.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (student.isRte) "RTE ZERO" else "GENERAL",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(color = Color.White.copy(alpha = 0.15f))

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Father: ${student.fatherName}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "Mother: ${student.motherName}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "DOB: ${student.dob}",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "Phone: ${student.fatherPhone}",
                            fontSize = 11.sp,
                            color = BrilliantGold
                        )
                    }
                }
            }
        }

        // Family Portal Quick-Action Grid (Mirroring all 8 Parent/Student Sidebar Menu Options)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "अभिभावक व छात्र त्वरित ग्रिड (QUICK ACTIONS)" else "PARENT & STUDENT QUICK-ACTION GRID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkSecondary,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Live Student Attendance Meter & Class Chat & Notes Group Access
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParentActionCard(
                        title = if (isHindi) "लाइव छात्र उपस्थिति मीटर" else "Live Attendance Meter",
                        subtitle = if (isHindi) "दैनिक उपस्थिति व प्रतिशत" else "${student.attendancePercentage}% Attendance",
                        icon = Icons.Default.Speed,
                        color = EmeraldGreen,
                        onClick = { /* already viewable right below */ },
                        modifier = Modifier.weight(1f)
                    )
                    ParentActionCard(
                        title = if (isHindi) "कक्षा चैट व नोट्स समूह" else "Class Chat & Notes",
                        subtitle = if (isHindi) "शिक्षक संदेश व सहपाठी ग्रुप" else "Subject Channels & Chat",
                        icon = Icons.Default.Forum,
                        color = RoyalPurple800,
                        badgeCount = state.classGroupMessages.size,
                        onClick = { viewModel.navigateTo("CLASS_CHAT") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Urgent School Announcements & Fee Ledger & UPI Payment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParentActionCard(
                        title = if (isHindi) "अत्यावश्यक विद्यालय घोषणाएँ" else "Urgent Announcements",
                        subtitle = if (isHindi) "व्हाट्सएप नोटिस व अवकाश" else "Official Circulars",
                        icon = Icons.Default.Campaign,
                        color = CrimsonRed,
                        badgeCount = state.announcements.size,
                        onClick = { showAnnouncementsDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    ParentActionCard(
                        title = if (isHindi) "फीस बहीखाता एवं UPI" else "Fee Ledger & UPI",
                        subtitle = if (isHindi) "ऑनलाइन रसीद व भुगतान" else "₹$totalDues Total Dues",
                        icon = Icons.Default.AccountBalanceWallet,
                        color = AmberGlow,
                        onClick = { showQrDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Live School Bus Location Map & Report Card
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParentActionCard(
                        title = if (isHindi) "लाइव स्कूल बस मैप" else "Live School Bus Map",
                        subtitle = if (isHindi) "रीयल-टाइम जीपीएस ट्रैकिंग" else if (bus?.isLive == true) "GPS Active" else "Stationary",
                        icon = Icons.Default.DirectionsBus,
                        color = EmeraldGreen,
                        onClick = { viewModel.navigateTo("GPS") },
                        modifier = Modifier.weight(1f)
                    )
                    ParentActionCard(
                        title = if (isHindi) "प्रगति रिपोर्ट कार्ड" else "Report Card",
                        subtitle = if (isHindi) "वार्षिक परीक्षा परिणाम" else "${state.reportCard.grade} (${state.reportCard.percentage}%)",
                        icon = Icons.Default.Assessment,
                        color = RoyalPurple700,
                        onClick = { showReportCardDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 4: School Notices & Class Timetable
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParentActionCard(
                        title = if (isHindi) "विद्यालय सूचनाएं" else "School Notices",
                        subtitle = if (isHindi) "डिजिटल सूचना पट्ट" else "${state.noticesList.size} Bulletins",
                        icon = Icons.Default.Notifications,
                        color = ElectricBlue,
                        onClick = { showNoticesDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    ParentActionCard(
                        title = if (isHindi) "कक्षा समय सारिणी" else "Class Timetable",
                        subtitle = if (isHindi) "दैनिक घंटी व विषय" else "Daily Periods & Routine",
                        icon = Icons.Default.CalendarMonth,
                        color = AmberGlow,
                        onClick = { showStudentTimetableDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 5: Homework Hub & Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ParentActionCard(
                        title = if (isHindi) "गृहकार्य एवं असाइनमेंट" else "Homework & Tasks",
                        subtitle = if (isHindi) "दैनिक गृहकार्य सूची" else "${state.homeworkList.size} Active Tasks",
                        icon = Icons.Default.Assignment,
                        color = RoyalPurple800,
                        badgeCount = state.homeworkList.count { !it.isCompleted },
                        onClick = { showAllHomeworkDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    ParentActionCard(
                        title = if (isHindi) "सुरक्षित लॉगआउट" else "Logout",
                        subtitle = if (isHindi) "सत्र समाप्ति एवं लॉक" else "Secure Lock & Gateway",
                        icon = Icons.Default.Lock,
                        color = TextDarkSecondary,
                        onClick = { viewModel.logout() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Attendance Gauge & Academic Overview
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Circular Attendance Gauge Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isHindi) "सत्र उपस्थिति मीटर" else "ATTENDANCE GAUGE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = Color(0xFFE2E8F0),
                                    startAngle = -90f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = EmeraldGreen,
                                    startAngle = -90f,
                                    sweepAngle = (student.attendancePercentage * 3.6f),
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Text(
                                text = "${student.attendancePercentage}%",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = EmeraldGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (student.attendancePercentage >= 90) "Excellent (90%+)" else "Good",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDarkPrimary
                        )
                    }
                }

                // Mid-Term Report Card Summary Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    modifier = Modifier.weight(1.2f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = if (isHindi) "परीक्षा रिपोर्ट कार्ड" else "ACADEMIC REPORT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = state.reportCard.grade,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalPurple800
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoyalPurpleLight
                            ) {
                                Text(
                                    text = "${state.reportCard.percentage}% Score",
                                    color = RoyalPurple800,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${state.reportCard.marksObtained} / ${state.reportCard.totalMarks} Marks",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDarkPrimary
                        )
                        Text(
                            text = state.reportCard.remarks,
                            fontSize = 10.sp,
                            color = TextDarkSecondary,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Latest Homework Updates
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = "Homework",
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "दैनिक गृहकार्य अपडेट" else "LATEST HOMEWORK UPDATES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                        }

                        Text(
                            text = "${state.homeworkList.count { !it.isCompleted }} Pending",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = OrangeWarning
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    state.homeworkList.take(2).forEach { hw ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
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
                                    text = "Due: ${hw.dueDate} • By: ${hw.assignedBy}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Icon(
                                imageVector = if (hw.isCompleted) Icons.Default.CheckCircle else Icons.Default.Pending,
                                contentDescription = "Status",
                                tint = if (hw.isCompleted) EmeraldGreen else AmberGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Principal Emergency Broadcasts Banner
        val parentAnnouncements = state.announcements.filter { it.targetAudience == "ALL_SCHOOL" || it.targetAudience == "PARENTS" }
        if (parentAnnouncements.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CrimsonRed.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonRed.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Campaign, contentDescription = "Alert", tint = CrimsonRed, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "प्रधानाचार्य आपातकालीन घोषणा" else "PRINCIPAL EMERGENCY NOTICE",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonRed
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = CrimsonRed
                            ) {
                                Text(
                                    text = "WHATSAPP DISPATCHED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        parentAnnouncements.forEach { ann ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = ann.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = ann.message,
                                    fontSize = 11.sp,
                                    color = TextDarkPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "By ${ann.senderName} • ${ann.formattedTime}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                    if (ann.attachmentName != null) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = RoyalPurpleLight
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.AttachFile, contentDescription = "PDF", tint = RoyalPurple800, modifier = Modifier.size(12.dp))
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(ann.attachmentName, fontSize = 9.sp, color = RoyalPurple800, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Class Group Teacher Broadcasts
        val classMessages = state.classGroupMessages.filter { it.className.contains(student.standard) || it.className.contains("9") }
        if (classMessages.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Forum, contentDescription = "Class", tint = RoyalPurple800, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "कक्षा शिक्षक संदेश (${student.standard})" else "CLASS TEACHER BROADCASTS (${student.standard})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary,
                                    letterSpacing = 0.6.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoyalPurpleLight
                            ) {
                                Text(
                                    text = "${classMessages.size} New",
                                    color = RoyalPurple800,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        classMessages.forEach { msg ->
                            Surface(
                                color = RoyalPurple50,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(msg.senderName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                        Text(msg.formattedTime, fontSize = 9.sp, color = TextMuted)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(msg.message, fontSize = 11.sp, color = TextDarkPrimary)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Digital Notices
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = "Notices",
                            tint = RoyalPurple700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "डिजिटल सूचना पट्ट" else "DIGITAL SCHOOL NOTICES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            letterSpacing = 0.6.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    state.noticesList.forEach { notice ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (notice.priority == "HIGH") CrimsonRed else RoyalPurple700)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) notice.titleHi else notice.titleEn,
                                fontSize = 12.sp,
                                color = TextDarkPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = notice.date,
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Live Bus Tracking Map Preview
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { viewModel.navigateTo("GPS") },
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (bus?.isLive == true) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
                                contentDescription = "Bus GPS",
                                tint = if (bus?.isLive == true) EmeraldGreen else TextDarkSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "लाइव स्कूल बस ट्रैकिंग" else "Live School Bus GPS Tracking",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                if (bus?.isLive == true) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen)
                                    )
                                }
                            }
                            Text(
                                text = if (bus?.isLive == true)
                                    "GPS: ${bus.currentLatitude}, ${bus.currentLongitude} • ${bus.speedKmh} km/h • ETA: ${bus.etaMinutes} mins"
                                else
                                    "Vehicle parked safely at School Yard",
                                fontSize = 11.sp,
                                color = if (bus?.isLive == true) EmeraldGreen else TextMuted
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = TextDarkSecondary
                    )
                }
            }
        }

        // Comprehensive Fee Ledger Breakdown
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isHindi) "विस्तृत फीस बहीखाता" else "COMPREHENSIVE FEE LEDGER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            letterSpacing = 0.6.sp
                        )

                        Text(
                            text = "Session 2026-27",
                            fontSize = 11.sp,
                            color = RoyalPurple700,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (student.isRte) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldGreen.copy(alpha = 0.12f))
                                .border(1.dp, EmeraldGreen, RoundedCornerShape(10.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = if (isHindi) "RTE श्रेणी - पूर्णतः निःशुल्क शिक्षा" else "RTE Category - Free Education (Zero Balance)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = if (isHindi) "शिक्षा का अधिकार (RTE) के तहत विद्यालय व परिवहन शुल्क ₹0 मान्य है।" else "Under Right To Education Act, all current & past school and vahan fees are ₹0.",
                                    fontSize = 11.sp,
                                    color = TextDarkPrimary
                                )
                            }
                        }
                    } else {
                        FeeRowItem(label = "Previous Year School Fees", amount = "₹${student.previousYearDues}")
                        FeeRowItem(label = "Previous Year Vahan / Transport Fees", amount = "₹${student.previousYearTransportDues}")
                        FeeRowItem(label = "Current Session School Fees", amount = "₹${student.currentSessionFees}")
                        FeeRowItem(label = "Current Session Vahan Fees", amount = "₹${student.currentSessionTransportFees}")

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isHindi) "कुल शेष बकाया राशि" else "Remaining Total Balance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Text(
                                text = "₹$totalDues",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (totalDues > 0) CrimsonRed else EmeraldGreen
                            )
                        }
                    }
                }
            }
        }

        // Financial Invoice Grid: Itemized Term Invoices with Status & 1-Tap Actions
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, contentDescription = "Invoices", tint = RoyalPurple800, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "वित्तीय इनवॉइस एवं रसीद ग्रिड" else "FINANCIAL INVOICE GRID",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurpleLight
                        ) {
                            Text(
                                text = "4 INVOICES",
                                color = RoyalPurple800,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val invoices = listOf(
                        InvoiceItem("INV-2026-001", "Term 1 School Tuition Fee", "10 Apr 2026", 12500, "PAID"),
                        InvoiceItem("INV-2026-002", "Annual Lab & Computer Activity", "15 Jun 2026", 3500, "PAID"),
                        InvoiceItem("INV-2026-003", "Term 2 School Tuition Fee", "10 Sep 2026", 12500, if (totalDues > 0) "PENDING" else "PAID"),
                        InvoiceItem("INV-2026-004", "School Vahan / Transport (Q2)", "01 Oct 2026", 6000, if (student.currentSessionTransportFees > 0) "DUE" else "PAID")
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        invoices.forEach { inv ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(inv.invoiceNumber, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = when (inv.status) {
                                                    "PAID" -> EmeraldGreen.copy(alpha = 0.15f)
                                                    "PENDING" -> AmberGlow.copy(alpha = 0.15f)
                                                    else -> CrimsonRed.copy(alpha = 0.15f)
                                                }
                                            ) {
                                                Text(
                                                    text = inv.status,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (inv.status) {
                                                        "PAID" -> EmeraldGreen
                                                        "PENDING" -> AmberGlow
                                                        else -> CrimsonRed
                                                    },
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(inv.title, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDarkPrimary)
                                        Text("Date: ${inv.date}", fontSize = 10.sp, color = TextDarkSecondary)
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("₹${inv.amount}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = RoyalPurple900)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        if (inv.status == "PAID") {
                                            OutlinedButton(
                                                onClick = { viewModel.downloadFeeReceipt(student.id, context) },
                                                shape = RoundedCornerShape(6.dp),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(12.dp), tint = EmeraldGreen)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Receipt", fontSize = 10.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Button(
                                                onClick = { viewModel.initiateUpiPayment("Google Pay", inv.amount.toLong(), context) },
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(28.dp)
                                            ) {
                                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text("Pay Now", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // In-App UPI Payment Module (GPay, PhonePe, QR Code Generator, Download Receipt)
        if (!student.isRte && totalDues > 0) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "ऑनलाइन UPI भुगतान गेटवे" else "IN-APP UPI PAYMENT MODULE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // GPay & PhonePe action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { viewModel.initiateUpiPayment("Google Pay", totalDues, context) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A73E8)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = "GPay", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("GPay", fontWeight = FontWeight.Bold, color = Color.White)
                            }

                            Button(
                                onClick = { viewModel.initiateUpiPayment("PhonePe", totalDues, context) },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5F259F)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AccountBalance, contentDescription = "PhonePe", tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("PhonePe", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // QR Code Generator & Download Receipt Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showQrDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.QrCode2, contentDescription = "QR", tint = RoyalPurple800)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Show UPI QR", color = RoyalPurple800, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { viewModel.downloadFeeReceipt(student.id, context) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Receipt", tint = EmeraldGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Get Receipt", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Persistent AdMob Banner (Mandatory for free Parent/Student tier)
        item {
            AdMobBannerComponent(
                userRole = UserRole.PARENT_STUDENT,
                trialDaysRemaining = 0,
                hasPaidSubscription = false,
                viewModel = viewModel
            )
        }
    }

    // QR Code Dialog
    if (showQrDialog) {
        Dialog(onDismissRequest = { showQrDialog = false }) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan & Pay UPI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900
                    )
                    Text(
                        text = "${school.nameEn} • Amount: ₹$totalDues",
                        fontSize = 12.sp,
                        color = TextDarkSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, BorderLight, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = "QR Code",
                            tint = RoyalPurple900,
                            modifier = Modifier.size(130.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showQrDialog = false },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close", color = Color.White)
                    }
                }
            }
        }
    }

    if (showReportCardDialog) {
        AlertDialog(
            onDismissRequest = { showReportCardDialog = false },
            title = {
                Text(
                    text = if (isHindi) "वार्षिक प्रगति रिपोर्ट कार्ड" else "Official Progress Report Card",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Overall Grade: ${state.reportCard.grade} (${state.reportCard.percentage}%)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = EmeraldGreen)
                    Text(text = "Mathematics: 96/100 • Science: 92/100", fontSize = 13.sp)
                    Text(text = "Social Science: 90/100 • English: 95/100", fontSize = 13.sp)
                    Text(text = "Attendance Record: ${student.attendancePercentage}% (Eligible for Academic Distinction)", fontSize = 12.sp, color = TextDarkSecondary)
                    Text(text = "Class Teacher Remarks: Outstanding academic curiosity, disciplined conduct and active participation in lab sessions.", fontSize = 12.sp, color = RoyalPurple800, fontWeight = FontWeight.Medium)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showReportCardDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                ) {
                    Text(if (isHindi) "बंद करें" else "Close", color = Color.White)
                }
            }
        )
    }

    if (showNoticesDialog) {
        AlertDialog(
            onDismissRequest = { showNoticesDialog = false },
            title = {
                Text(
                    text = if (isHindi) "विद्यालय सूचनाएं" else "Official School Notices",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900
                )
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.noticesList) { notice ->
                        Surface(
                            color = RoyalPurple50,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isHindi) notice.titleHi else notice.titleEn,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurple900
                                    )
                                    Text(notice.date, fontSize = 10.sp, color = TextMuted)
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isHindi) notice.contentHi else notice.contentEn,
                                    fontSize = 11.sp,
                                    color = TextDarkPrimary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showNoticesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                ) {
                    Text(if (isHindi) "बंद करें" else "Close", color = Color.White)
                }
            }
        )
    }

    if (showAnnouncementsDialog) {
        AlertDialog(
            onDismissRequest = { showAnnouncementsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Campaign, contentDescription = "Alert", tint = CrimsonRed)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "अत्यावश्यक विद्यालय घोषणाएँ" else "Urgent School Announcements",
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed
                    )
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.announcements) { ann ->
                        Surface(
                            color = CrimsonRed.copy(alpha = 0.06f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(ann.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(ann.message, fontSize = 11.sp, color = TextDarkPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("By ${ann.senderName} • ${ann.formattedTime}", fontSize = 10.sp, color = TextMuted)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAnnouncementsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text(if (isHindi) "बंद करें" else "Close", color = Color.White)
                }
            }
        )
    }

    if (showStudentTimetableDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showStudentTimetableDialog = false }) {
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
                                    text = if (isHindi) "कक्षा समय सारिणी" else "Class Timetable",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = "${student.standard}-${student.section} • $selectedDay",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        IconButton(onClick = { showStudentTimetableDialog = false }) {
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

                    val dayPeriods = state.timetablePeriods.filter {
                        (it.standard == student.standard || it.standard.contains("9") || student.standard.isEmpty()) && it.dayOfWeek == selectedDay
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
                                                Text("${p.startTime} - ${p.endTime} • ${p.teacherName}", fontSize = 10.sp, color = TextDarkSecondary)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showStudentTimetableDialog = false },
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

    if (showAllHomeworkDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showAllHomeworkDialog = false }) {
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
                                    .background(RoyalPurple800.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Assignment, contentDescription = "Homework", tint = RoyalPurple800, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "गृहकार्य एवं असाइनमेंट" else "Homework & Assignments",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = "${student.standard}-${student.section} • ${state.homeworkList.size} Total",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        IconButton(onClick = { showAllHomeworkDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state.homeworkList.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isHindi) "कोई लंबित गृहकार्य नहीं है" else "No pending homework tasks assigned",
                                color = TextDarkSecondary,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.homeworkList) { hw ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFFF8FAFC),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(hw.subject, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                             Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (hw.isCompleted) EmeraldGreen.copy(alpha = 0.15f) else AmberGlow.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = if (hw.isCompleted) "COMPLETED" else "PENDING",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (hw.isCompleted) EmeraldGreen else Color(0xFFB45309),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(hw.description, fontSize = 11.sp, color = TextDarkPrimary)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Due: ${hw.dueDate}", fontSize = 10.sp, color = CrimsonRed, fontWeight = FontWeight.Medium)
                                            Button(
                                                onClick = { viewModel.toggleHomeworkCompletion(hw.id) },
                                                shape = RoundedCornerShape(6.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (hw.isCompleted) TextDarkSecondary else EmeraldGreen
                                                ),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.height(26.dp)
                                            ) {
                                                Text(
                                                    text = if (hw.isCompleted) "Undo" else "Mark Done",
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showAllHomeworkDialog = false },
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
fun ParentActionCard(
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
                            .background(CrimsonRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeCount.toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkPrimary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextDarkSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FeeRowItem(label: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = TextDarkSecondary)
        Text(text = amount, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
    }
}

data class InvoiceItem(
    val invoiceNumber: String,
    val title: String,
    val date: String,
    val amount: Int,
    val status: String
)
