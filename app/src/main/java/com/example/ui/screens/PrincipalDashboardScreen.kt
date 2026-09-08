package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.FeeApprovalStatus
import com.example.data.UserRole
import com.example.ui.components.AdMobBannerComponent
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun PrincipalDashboardScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val school = state.currentSchool
    val isHindi = state.isHindi

    var showSchoolProfileDialog by remember { mutableStateOf(false) }
    var showFeeAnalyticsDialog by remember { mutableStateOf(false) }
    var showClassAllocationDialog by remember { mutableStateOf(false) }
    var showEmergencyAnnouncementDialog by remember { mutableStateOf(false) }
    var showManageStaffMasterDialog by remember { mutableStateOf(false) }
    var showStudentSuccessDialog by remember { mutableStateOf(false) }
    var showResourceOptimizationDialog by remember { mutableStateOf(false) }
    var showEditStudentDialog by remember { mutableStateOf<com.example.data.Student?>(null) }
    var showPayrollLedgerDialog by remember { mutableStateOf(false) }
    var showLeaveRequestsDialog by remember { mutableStateOf(false) }
    var showTimetableDialog by remember { mutableStateOf(false) }
    var selectedMonthIndex by remember { mutableStateOf(4) } // August by default

    val months = listOf("Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan", "Feb", "Mar")
    val attendanceAverages = listOf(91f, 89f, 94f, 93f, 95f, 92f, 90f, 94f, 91f, 93f, 96f, 94f)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // 14-Day Free Trial HUD
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (school.hasPaidSubscription) RoyalPurple800 else if (school.trialDaysRemaining > 0) RoyalPurple900 else CrimsonRed
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                                .background(BrilliantGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (school.hasPaidSubscription) Icons.Default.Verified else Icons.Default.Timer,
                                contentDescription = "Trial HUD",
                                tint = BrilliantGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (school.hasPaidSubscription)
                                    (if (isHindi) "सक्रिय सदस्यता • विज्ञापन मुक्त" else "Enterprise Plan Active • Zero Ads")
                                else if (school.trialDaysRemaining > 0)
                                    (if (isHindi) "14-दिवसीय निःशुल्क ट्रायल (${school.trialDaysRemaining} दिन शेष)" else "14-Day Free Trial HUD: ${school.trialDaysRemaining} Days Left")
                                else
                                    (if (isHindi) "ट्रायल समाप्त • विज्ञापन सक्रिय" else "Trial Expired • AdMob Ads Active"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (school.hasPaidSubscription)
                                    (if (isHindi) "सभी संस्थागत अधिकार पूर्णतः अनलॉक हैं" else "All institutional modules unlocked")
                                else if (school.trialDaysRemaining > 0)
                                    (if (isHindi) "ट्रायल अवधि में कोई विज्ञापन नहीं दिखाया जाएगा" else "AdMob ads suppressed during active trial")
                                else
                                    (if (isHindi) "विज्ञापन हटाने हेतु प्लान अपग्रेड करें" else "Subscribe to suppress AdMob ads permanently"),
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }

                    if (!school.hasPaidSubscription) {
                        Surface(
                            onClick = { viewModel.navigateTo("SUPER_ADMIN") },
                            shape = RoundedCornerShape(8.dp),
                            color = BrilliantGold
                        ) {
                            Text(
                                text = if (isHindi) "अपग्रेड" else "Upgrade",
                                color = RoyalPurple900,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // AdMob Banner (Aggressive if expired without subscription; Suppressed during trial or paid plan)
        item {
            AdMobBannerComponent(
                userRole = UserRole.PRINCIPAL,
                trialDaysRemaining = school.trialDaysRemaining,
                hasPaidSubscription = school.hasPaidSubscription,
                viewModel = viewModel,
                onUpgradeClick = { viewModel.navigateTo("SUPER_ADMIN") }
            )
        }

        // Card 1: Dashboard Summary Panel (Live counts for Students, Teachers, Classes, and Revenue)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RoyalPurple900),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Dashboard,
                                    contentDescription = "Summary",
                                    tint = BrilliantGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "डैशबोर्ड सारांश पैनल" else "Dashboard Summary Panel",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isHindi) "सजीव संस्थागत मेट्रिक्स एवं वित्तीय स्थिति" else "Live Institutional Metrics & Financials",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "LIVE SYNC",
                                color = EmeraldGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4 Interactive KPI Metric Tiles: Students, Teachers, Classes, Profit/Revenue
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Total Students
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF3E8FF),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.PeopleAlt, contentDescription = "Students", tint = RoyalPurple800, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("${school.totalStudents}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = RoyalPurple900)
                                Text(if (isHindi) "कुल छात्र" else "Total Students", fontSize = 9.sp, color = TextDarkSecondary, maxLines = 1)
                            }
                        }

                        // Total Teachers
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.School, contentDescription = "Teachers", tint = ElectricBlue, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("${school.totalTeachers}", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E40AF))
                                Text(if (isHindi) "कुल शिक्षक" else "Total Teachers", fontSize = 9.sp, color = TextDarkSecondary, maxLines = 1)
                            }
                        }

                        // Total Classes
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFEF3C7),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.Class, contentDescription = "Classes", tint = Color(0xFFB45309), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("14", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFB45309))
                                Text(if (isHindi) "कुल कक्षाएँ" else "Total Classes", fontSize = 9.sp, color = TextDarkSecondary, maxLines = 1)
                            }
                        }

                        // Total Profit / Revenue in ₹
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFECFDF5),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Icon(Icons.Default.CurrencyRupee, contentDescription = "Revenue", tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("₹8.99L", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = EmeraldGreen)
                                Text(if (isHindi) "कुल राजस्व" else "Total Revenue", fontSize = 9.sp, color = TextDarkSecondary, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // STRICT PRINCIPAL DASHBOARD 4 STYLIZED ROUNDED WHITE CARDS
        // Card 9: Attendance Card (Weekly Attendance Trends purple area line graph tracking Mon to Sat percentages)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RoyalPurple900.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Weekly Attendance Trends",
                                    tint = RoyalPurple800,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "साप्ताहिक उपस्थिति रुझान" else "Weekly Attendance Trends",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isHindi) "सोमवार से शनिवार विश्लेषण" else "Mon to Sat Live Tracking",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "93.7% Avg",
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Purple Area Line Graph Canvas
                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    val values = listOf(92f, 95f, 89f, 94f, 96f, 91f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                    ) {
                        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val paddingX = 24.dp.toPx()
                            val stepX = (w - paddingX * 2) / (values.size - 1)
                            val minY = 80f
                            val maxY = 100f

                            val points = values.mapIndexed { index, v ->
                                val x = paddingX + index * stepX
                                val normalizedY = ((v - minY) / (maxY - minY)).coerceIn(0f, 1f)
                                val y = h - (normalizedY * (h - 24.dp.toPx())) - 12.dp.toPx()
                                androidx.compose.ui.geometry.Offset(x, y)
                            }

                            // Fill Area Under Curve
                            val fillPath = androidx.compose.ui.graphics.Path().apply {
                                moveTo(points.first().x, h)
                                points.forEach { lineTo(it.x, it.y) }
                                lineTo(points.last().x, h)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(RoyalPurple700.copy(alpha = 0.32f), Color.Transparent),
                                    startY = 0f,
                                    endY = h
                                )
                            )

                            // Stroke Line
                            val strokePath = androidx.compose.ui.graphics.Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    lineTo(points[i].x, points[i].y)
                                }
                            }
                            drawPath(
                                path = strokePath,
                                color = RoyalPurple800,
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = 3.dp.toPx(),
                                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            )

                            // Draw Point Dots
                            points.forEach { pt ->
                                drawCircle(
                                    color = BrilliantGold,
                                    radius = 4.dp.toPx(),
                                    center = pt
                                )
                                drawCircle(
                                    color = RoyalPurple900,
                                    radius = 2.dp.toPx(),
                                    center = pt
                                )
                            }
                        }
                    }

                    // Labels underneath
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        days.forEachIndexed { idx, day ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = day,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDarkSecondary
                                )
                                Text(
                                    text = "${values[idx].toInt()}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                            }
                        }
                    }
                }
            }
        }

        // Card 2: Fee Dues Card (Circular status ring tracking "% Fees Collected" + rows for Pending Dues, Fees Collected ($20), Pending Dues ($20))
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(AmberGlow.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DonutLarge,
                                    contentDescription = "Fee Dues",
                                    tint = Color(0xFFB45309),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "शुल्क स्थिति एवं संग्रह" else "Fee Dues",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isHindi) "सत्र 2026-27 विश्लेषण" else "Session Collections & Dues",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        Surface(
                            onClick = { viewModel.navigateTo("FEES") },
                            shape = RoundedCornerShape(8.dp),
                            color = RoyalPurple900
                        ) {
                            Text(
                                text = if (isHindi) "बहीखाता खोलें" else "Open Ledger",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Status Ring
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                val stroke = 8.dp.toPx()
                                drawCircle(
                                    color = Color(0xFFE2E8F0),
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke)
                                )
                                drawArc(
                                    color = EmeraldGreen,
                                    startAngle = -90f,
                                    sweepAngle = 360f * 0.07f,
                                    useCenter = false,
                                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                                        width = stroke,
                                        cap = androidx.compose.ui.graphics.StrokeCap.Round
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "7%",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = "Fees Collected",
                                    fontSize = 7.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Rows for "Pending Dues", "Fees Collected ($20)", and "Pending Dues ($20)"
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isHindi) "लंबित शुल्क (Pending Dues):" else "Pending Dues:",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                                Text(
                                    text = "₹4,85,000 / $20",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CrimsonRed
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isHindi) "एकत्रित शुल्क:" else "Fees Collected ($20):",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                                Text(
                                    text = "₹35,000 / $20",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (isHindi) "कुल बकाया:" else "Pending Dues ($20):",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                                Text(
                                    text = "₹4,85,000 / $20",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Card 3: Notice Broadcast Card (Lists "Recent Notices" with titles and automated time tags)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(RoyalPurple800.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "Notice Broadcast",
                                    tint = RoyalPurple800,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "हालिया सूचना प्रसारण" else "Notice Broadcast Card",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isHindi) "सक्रिय डिजिटल सूचना पट्ट" else "Recent Notices Feed",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        Button(
                            onClick = { showEmergencyAnnouncementDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple900),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = BrilliantGold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "+ प्रसारण" else "+ Broadcast",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Recent Notices with titles and automated time tags
                    val recentNotices = listOf(
                        "[Student Notices] Team Needs" to "7 minutes ago",
                        "[Faculty Notice] Staff Meeting at 2:00 PM" to "35 minutes ago",
                        "[Exam Alert] Term Exam Schedule Dispatched" to "2 hours ago"
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        recentNotices.forEach { (title, time) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (title.contains("Student")) AmberGlow else if (title.contains("Faculty")) ElectricBlue else CrimsonRed)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = title,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextDarkPrimary,
                                            maxLines = 1
                                        )
                                    }
                                    Text(
                                        text = time,
                                        fontSize = 10.sp,
                                        color = TextDarkSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Card 4: AI Tools Card (Split links for "Student Success Predictions" and "Resource Optimisation")
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(BrilliantGold.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "AI Tools",
                                    tint = RoyalPurple900,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "AI टूल्स एवं पूर्वानुमान" else "AI Tools Card",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isHindi) "मशीन लर्निंग स्वचालित अंतर्दृष्टि" else "Predictive Analytics Suite",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurple900
                        ) {
                            Text(
                                text = "GMPTP AI",
                                color = BrilliantGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Split Navigation Links
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            onClick = { showStudentSuccessDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFF3E8FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple500.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Psychology,
                                    contentDescription = "Student Success",
                                    tint = RoyalPurple800,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) "छात्र सफलता भविष्यवाणी" else "Student Success Predictions",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = if (isHindi) "जोखिम विश्लेषण व सहायता" else "Risk scoring & intervention",
                                    fontSize = 10.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        Surface(
                            onClick = { showResourceOptimizationDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFECFDF5),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Resource",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isHindi) "संसाधन अनुकूलन" else "Resource Optimisation",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                                Text(
                                    text = if (isHindi) "कक्षा व बस दक्षता विश्लेषण" else "Fleet & room efficiency",
                                    fontSize = 10.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Quick-Action Grid on Home Screen (Mirroring all 9 Principal Sidebar Options)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "प्रधानाचार्य त्वरित क्रियाएँ (QUICK ACTIONS)" else "EXECUTIVE QUICK-ACTION GRID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkSecondary,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Manage Faculty Register (Add Teachers & Drivers) & Master Payroll Ledger
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrincipalActionCard(
                        title = if (isHindi) "संकाय रजिस्टर (स्टाफ प्रबंधन)" else "Manage Faculty Register",
                        subtitle = if (isHindi) "शिक्षक व चालक भर्ती / वेतन" else "Add/Update Teachers & Drivers",
                        icon = Icons.Default.SupervisorAccount,
                        color = RoyalPurple800,
                        onClick = { showManageStaffMasterDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    PrincipalActionCard(
                        title = if (isHindi) "मुख्य वेतन बहीखाता (पेरोल)" else "Master Payroll Ledger",
                        subtitle = if (isHindi) "मासिक वेतन, अग्रिम व शेष" else "Base Salary, Advance & Balance",
                        icon = Icons.Default.ReceiptLong,
                        color = AmberGlow,
                        onClick = { showPayrollLedgerDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: Teacher Attendance Monitor & Manage Leave Requests Hub
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrincipalActionCard(
                        title = if (isHindi) "शिक्षक उपस्थिति मॉनिटर" else "Teacher Attendance Monitor",
                        subtitle = if (isHindi) "उपस्थित, अनुपस्थित, अवकाश लॉग" else "Mark, Log & Override Attendance",
                        icon = Icons.Default.FactCheck,
                        color = EmeraldGreen,
                        onClick = { viewModel.navigateTo("ATTENDANCE") },
                        modifier = Modifier.weight(1f)
                    )
                    PrincipalActionCard(
                        title = if (isHindi) "अवकाश अनुरोध प्रबंधन हब" else "Manage Leave Requests Hub",
                        subtitle = if (isHindi) "स्वीकृत / अस्वीकृत टॉगल" else "Staff & Student Applications",
                        icon = Icons.Default.EventBusy,
                        color = CrimsonRed,
                        badgeCount = state.leaveRequests.count { it.status == "PENDING" },
                        onClick = { showLeaveRequestsDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Create Timetable Module & School Media Gallery Hub
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrincipalActionCard(
                        title = if (isHindi) "समय सारिणी निर्माण मॉड्यूल" else "Create Timetable Module",
                        subtitle = if (isHindi) "कक्षा, विषय व समय आवंटन" else "Periods, Classes & Timings",
                        icon = Icons.Default.CalendarMonth,
                        color = ElectricBlue,
                        onClick = { showTimetableDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                    PrincipalActionCard(
                        title = if (isHindi) "विद्यालय मीडिया गैलरी हब" else "School Media Gallery Hub",
                        subtitle = if (isHindi) "उत्सव, खेल व फ़ोटो एल्बम" else "Upload Events & Photo Albums",
                        icon = Icons.Default.PhotoLibrary,
                        color = RoyalPurple700,
                        onClick = { viewModel.navigateTo("GALLERY") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 4: AI Emergency Announcement Hub (Prominent Full Width Card with WhatsApp Broadcast Tag)
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RoyalPurple900),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEmergencyAnnouncementDialog = true }
                        .border(1.5.dp, BrilliantGold, RoundedCornerShape(16.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrilliantGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "Announcement",
                                    tint = RoyalPurple900,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isHindi) "AI आपातकालीन घोषणा हब" else "AI Emergency Announcement Hub",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = CrimsonRed
                                    ) {
                                        Text(
                                            text = "WHATSAPP API",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = if (isHindi) "पाठ, चित्र/स्कैन दस्तावेज़ व स्वतः व्हाट्सएप ब्रॉडकास्ट" else "Bilingual notices + attachment + bulk WhatsApp dispatch",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = BrilliantGold,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 5: Daily Suvichar Hub & Manage School Profile
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrincipalActionCard(
                        title = if (isHindi) "दैनिक सुविचार हब" else "Daily Suvichar Hub",
                        subtitle = if (isHindi) "AI प्रेरक ग्राफ़िक्स व शेयर" else "AI Quotes & 1-Tap Share",
                        icon = Icons.Default.FormatQuote,
                        color = RoyalPurple800,
                        onClick = { viewModel.navigateTo("SUVICHAR") },
                        modifier = Modifier.weight(1f)
                    )
                    PrincipalActionCard(
                        title = if (isHindi) "विद्यालय प्रोफ़ाइल प्रबंधन" else "Manage School Profile",
                        subtitle = if (isHindi) "संस्थान विवरण व UDISE" else "School Details & UDISE",
                        icon = Icons.Default.AccountBalance,
                        color = AmberGlow,
                        onClick = { showSchoolProfileDialog = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 6: Master Financial Statements & Bilingual Support Chatbot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrincipalActionCard(
                        title = if (isHindi) "मास्टर वित्तीय विवरण" else "Master Financial Statements",
                        subtitle = if (isHindi) "बहीखाता व टोकन सत्यापन" else "Ledgers & Token Audits",
                        icon = Icons.Default.ReceiptLong,
                        color = EmeraldGreen,
                        badgeCount = state.feeRecords.count { it.status == FeeApprovalStatus.PENDING_VERIFICATION },
                        onClick = { viewModel.navigateTo("FEES") },
                        modifier = Modifier.weight(1f)
                    )
                    PrincipalActionCard(
                        title = if (isHindi) "द्विभाषी सहायता चैटबॉट" else "Bilingual Support Chatbot",
                        subtitle = if (isHindi) "AI सहायक 24/7 सहायता" else "24/7 Assistant AI Bot",
                        icon = Icons.Default.SmartToy,
                        color = ElectricBlue,
                        onClick = { viewModel.navigateTo("HELP_BOT") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 7: Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrincipalActionCard(
                        title = if (isHindi) "लॉगआउट" else "Logout",
                        subtitle = if (isHindi) "सुरक्षित सत्र समाप्ति" else "Secure Lock & Gateway",
                        icon = Icons.Default.Lock,
                        color = TextDarkSecondary,
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Interactive Purple Visual Area Chart (Month-wise school attendance averages & Fee targets)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "मासिक विद्यालय उपस्थिति विश्लेषण" else "MONTH-WISE ATTENDANCE ANALYTICS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = "${months[selectedMonthIndex]}: ${attendanceAverages[selectedMonthIndex].toInt()}% School Average",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalPurple800
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "+3.4% vs Prev Session",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Compose Canvas Purple Area Chart
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val width = size.width
                        val height = size.height
                        val stepX = width / (attendanceAverages.size - 1)
                        val minVal = 80f
                        val maxVal = 100f

                        val points = attendanceAverages.mapIndexed { idx, value ->
                            val x = idx * stepX
                            val normalizedY = (value - minVal) / (maxVal - minVal)
                            val y = height - (normalizedY * (height - 20.dp.toPx())) - 10.dp.toPx()
                            Offset(x, y)
                        }

                        // Path for area gradient fill
                        val fillPath = Path().apply {
                            moveTo(0f, height)
                            points.forEachIndexed { i, pt ->
                                if (i == 0) lineTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                            }
                            lineTo(width, height)
                            close()
                        }

                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(RoyalPurple700.copy(alpha = 0.35f), RoyalPurple700.copy(alpha = 0.02f)),
                                startY = 0f,
                                endY = height
                            ),
                            style = Fill
                        )

                        // Path for curve line
                        val linePath = Path().apply {
                            points.forEachIndexed { i, pt ->
                                if (i == 0) moveTo(pt.x, pt.y) else lineTo(pt.x, pt.y)
                            }
                        }

                        drawPath(
                            path = linePath,
                            color = RoyalPurple800,
                            style = Stroke(width = 3.dp.toPx())
                        )

                        // Draw active month highlighted indicator
                        val selPt = points[selectedMonthIndex]
                        drawCircle(
                            color = BrilliantGold,
                            radius = 6.dp.toPx(),
                            center = selPt
                        )
                        drawCircle(
                            color = RoyalPurple900,
                            radius = 3.dp.toPx(),
                            center = selPt
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Month Selector Chips
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(months) { idx, mName ->
                            Surface(
                                onClick = { selectedMonthIndex = idx },
                                shape = RoundedCornerShape(6.dp),
                                color = if (selectedMonthIndex == idx) RoyalPurple800 else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = mName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedMonthIndex == idx) Color.White else TextDarkSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Real-Time Teacher Feeds
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "सजीव शिक्षक गतिविधि फ़ीड" else "LIVE REAL-TIME TEACHER FEEDS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                        }

                        Text(
                            text = "Auto-Syncing",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    state.teacherFeeds.forEachIndexed { idx, feed ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (feed.actionType) {
                                            "ATTENDANCE" -> EmeraldGreen.copy(alpha = 0.15f)
                                            "FEE" -> AmberGlow.copy(alpha = 0.15f)
                                            else -> ElectricBlue.copy(alpha = 0.15f)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (feed.actionType) {
                                        "ATTENDANCE" -> Icons.Default.HowToReg
                                        "FEE" -> Icons.Default.CurrencyRupee
                                        else -> Icons.Default.FactCheck
                                    },
                                    contentDescription = feed.actionType,
                                    tint = when (feed.actionType) {
                                        "ATTENDANCE" -> EmeraldGreen
                                        "FEE" -> AmberGlow
                                        else -> ElectricBlue
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = feed.teacherName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = feed.timeAgo,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                                Text(
                                    text = if (isHindi) feed.messageHi else feed.messageEn,
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        if (idx < state.teacherFeeds.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 4.dp),
                                color = BorderLight
                            )
                        }
                    }
                }
            }
        }

        // ==================== RELATIONAL CHAIN & INSTANT STUDENT SYNC ====================
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(ElectricBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "पारस्परिक श्रृंखला व छात्र बहीखाता (लाइव सिंक)" else "RELATIONAL CHAIN & LIVE STUDENT SYNC",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = if (isHindi) "शिक्षक पंजीकरण -> प्रधानाचार्य डैशबोर्ड स्वतः सिंक" else "Teacher registers -> Instantly populates Principal Hub",
                                    fontSize = 10.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = RoyalPurpleLight
                        ) {
                            Text(
                                text = "${state.students.size} Students",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple900,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    state.students.take(5).forEach { stu ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BackgroundCanvas,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = stu.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = TextDarkPrimary
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        if (stu.isRte) {
                                            Surface(
                                                color = EmeraldGreen.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "RTE",
                                                    color = EmeraldGreen,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = "${stu.standard}-${stu.section} • Roll #${stu.rollNo} • Guardian: ${stu.fatherName}",
                                        fontSize = 11.sp,
                                        color = TextDarkSecondary
                                    )
                                    Text(
                                        text = "Fees Due: ₹${if (stu.isRte) 0 else stu.currentSessionFees + stu.previousYearDues}",
                                        fontSize = 10.sp,
                                        color = if (stu.isRte) EmeraldGreen else CrimsonRed,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                // Principal Absolute Control Action Buttons
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = { showEditStudentDialog = stu },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = RoyalPurple700,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteStudent(stu.id, context) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = CrimsonRed,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isHindi) "पूर्ण अधिकार: किसी भी छात्र को संपादित या हटाएं" else "Principal Control: View, edit, or delete any record",
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                        TextButton(onClick = { viewModel.navigateTo("REGISTER_STUDENT") }) {
                            Text(
                                text = if (isHindi) "+ नया छात्र जोड़ें" else "+ Add Student",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple700
                            )
                        }
                    }
                }
            }
        }

        // ==================== AI EMERGENCY BROADCAST LOGS ====================
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
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
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Broadcasts",
                                tint = CrimsonRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "हालिया आपातकालीन व्हाट्सएप उद्घोषणाएं" else "RECENT EMERGENCY WHATSAPP BROADCASTS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                        }

                        TextButton(onClick = { showEmergencyAnnouncementDialog = true }) {
                            Text(
                                text = if (isHindi) "+ नई उद्घोषणा" else "+ New Notice",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CrimsonRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    state.announcements.take(2).forEach { ann ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (ann.isEmergency) CrimsonRed.copy(alpha = 0.05f) else RoyalPurple50,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (ann.isEmergency) CrimsonRed.copy(alpha = 0.3f) else RoyalPurpleLight
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = ann.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (ann.isEmergency) CrimsonRed else RoyalPurple900
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EmeraldGreen
                                    ) {
                                        Text(
                                            text = "WHATSAPP SENT",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = ann.message,
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Audience: ${ann.totalRecipientsReached} Contacts",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                    Text(
                                        text = ann.formattedTime,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Fee Analytics Dialog
    if (showFeeAnalyticsDialog) {
        Dialog(onDismissRequest = { showFeeAnalyticsDialog = false }) {
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
                            text = if (isHindi) "शुल्क लक्ष्य बनाम वसूली" else "Fee Collection Target vs Pending",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple900
                        )
                        IconButton(onClick = { showFeeAnalyticsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val targetFee = 1500000L
                    val collectedFee = school.schoolFeesCollected + school.transportFeesCollected
                    val pendingFee = targetFee - collectedFee
                    val progressPercent = (collectedFee.toFloat() / targetFee.toFloat()).coerceIn(0f, 1f)

                    Text(
                        text = "Total Session Target: ₹15,00,000",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = EmeraldGreen,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Collected", fontSize = 11.sp, color = TextMuted)
                            Text("₹${collectedFee / 1000}k", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                        }
                        Column {
                            Text("Pending Balance", fontSize = 11.sp, color = TextMuted)
                            Text("₹${pendingFee / 1000}k", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = CrimsonRed)
                        }
                        Column {
                            Text("RTE Exempted", fontSize = 11.sp, color = TextMuted)
                            Text("${school.rteStudentsCount} Students", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            showFeeAnalyticsDialog = false
                            viewModel.navigateTo("FEES")
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Full Fee Ledger & Approvals", color = Color.White)
                    }
                }
            }
        }
    }

    // Class Allocation Dialog
    if (showClassAllocationDialog) {
        Dialog(onDismissRequest = { showClassAllocationDialog = false }) {
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
                            text = if (isHindi) "कक्षा एवं सेक्शन आवंटन" else "Class & Section Allocation",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple900
                        )
                        IconButton(onClick = { showClassAllocationDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    listOf(
                        Triple("Class 8-A", "38 Students", "Prof. Rajesh Sharma"),
                        Triple("Class 9-B", "40 Students", "Prof. Rajesh Sharma"),
                        Triple("Class 10-A", "42 Students", "Dr. Vikas Mehra"),
                        Triple("Class 11-Science", "35 Students", "Priyanka Saxena")
                    ).forEach { (cls, cnt, teacher) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = cls, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                Text(text = cnt, fontSize = 11.sp, color = TextDarkSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = RoyalPurpleLight
                            ) {
                                Text(
                                    text = teacher,
                                    color = RoyalPurple800,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showClassAllocationDialog = false },
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

    // ==================== AI EMERGENCY ANNOUNCEMENT DIALOG (WHATSAPP BROADCAST) ====================
    if (showEmergencyAnnouncementDialog) {
        var noticeTitle by remember { mutableStateOf("") }
        var noticeBody by remember { mutableStateOf("") }
        var targetAudience by remember { mutableStateOf("ALL_SCHOOL") }
        var isEmergencyPriority by remember { mutableStateOf(true) }
        var attachmentFileName by remember { mutableStateOf("School_Official_Notice_Circular.pdf") }

        Dialog(onDismissRequest = { showEmergencyAnnouncementDialog = false }) {
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
                        .padding(20.dp)
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
                                    .background(CrimsonRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = "Alert",
                                    tint = CrimsonRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "AI आपातकालीन उद्घोषणा हब" else "AI Emergency Announcement Engine",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = RoyalPurple900
                                )
                                Text(
                                    text = "Bulk Multi-Channel WhatsApp Broadcast API",
                                    fontSize = 10.sp,
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                        IconButton(onClick = { showEmergencyAnnouncementDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Priority Alert Toggle Banner
                    Surface(
                        color = if (isEmergencyPriority) CrimsonRed.copy(alpha = 0.1f) else RoyalPurple50,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isEmergencyPriority) CrimsonRed else RoyalPurpleLight
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Priority",
                                    tint = if (isEmergencyPriority) CrimsonRed else RoyalPurple700,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "अति-आवश्यक आपातकालीन अलर्ट" else "High Priority Emergency Alert",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEmergencyPriority) CrimsonRed else RoyalPurple900
                                )
                            }
                            Switch(
                                checked = isEmergencyPriority,
                                onCheckedChange = { isEmergencyPriority = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = CrimsonRed, checkedTrackColor = CrimsonRed.copy(alpha = 0.3f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target Audience Chips
                    Text(
                        text = if (isHindi) "लक्षित दर्शक (TARGET AUDIENCE):" else "TARGET AUDIENCE:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Pair("ALL_SCHOOL", if (isHindi) "पूरा विद्यालय" else "All School"),
                            Pair("PARENTS", if (isHindi) "अभिभावक" else "Parents"),
                            Pair("TEACHERS", if (isHindi) "शिक्षक" else "Teachers"),
                            Pair("DRIVERS", if (isHindi) "चालक" else "Drivers")
                        ).forEach { (audKey, audLabel) ->
                            val isSelected = targetAudience == audKey
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) RoyalPurple800 else Color(0xFFF1F5F9),
                                modifier = Modifier.clickable { targetAudience = audKey }
                            ) {
                                Text(
                                    text = audLabel,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextDarkSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Notice Title
                    OutlinedTextField(
                        value = noticeTitle,
                        onValueChange = { noticeTitle = it },
                        label = { Text(if (isHindi) "शीर्षक (उदा. भारी वर्षा अवकाश सूचना)" else "Notice Title (e.g. Weather Holiday Notice)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Notice Message Body
                    OutlinedTextField(
                        value = noticeBody,
                        onValueChange = { noticeBody = it },
                        label = { Text(if (isHindi) "विस्तृत सूचना संदेश (हिंदी / English)" else "Notice Content (English / Hindi bilingual)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Document / Image Attachment Chip
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                attachmentFileName = if (attachmentFileName == "School_Official_Notice_Circular.pdf")
                                    "District_Magistrate_Order_Scan.jpg" else "School_Official_Notice_Circular.pdf"
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.AttachFile, contentDescription = "Attach", tint = RoyalPurple700, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Attached File: $attachmentFileName", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                Text("Scanned PDF / Official Circular (Tap to switch)", fontSize = 10.sp, color = TextMuted)
                            }
                            Icon(Icons.Default.CheckCircle, contentDescription = "Attached", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Big Publish Broadcast Button
                    Button(
                        onClick = {
                            val finalTitle = if (noticeTitle.isBlank()) {
                                if (isHindi) "आपातकालीन विद्यालय सूचना" else "EMERGENCY SCHOOL NOTICE"
                            } else noticeTitle

                            val finalMsg = if (noticeBody.isBlank()) {
                                if (isHindi) "जिला प्रशासन के निर्देशानुसार कल विद्यालय में अवकाश रहेगा।"
                                else "As per District Administration orders, the school campus will remain closed tomorrow."
                            } else noticeBody

                            viewModel.publishSchoolAnnouncement(
                                title = finalTitle,
                                message = finalMsg,
                                targetAudience = targetAudience,
                                isEmergency = isEmergencyPriority,
                                attachmentName = attachmentFileName,
                                context = context
                            )
                            showEmergencyAnnouncementDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Publish", tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "व्हाट्सएप ब्रॉडकास्ट जारी करें (स्वतः प्रेषण)" else "Publish & Dispatch WhatsApp Broadcast API",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    // ==================== MANAGE STAFF & TOTAL PRINCIPAL CONTROL DIALOG ====================
    if (showManageStaffMasterDialog) {
        var staffTab by remember { mutableStateOf(0) } // 0: Teachers, 1: Drivers
        var newStaffName by remember { mutableStateOf("") }
        var newStaffPhone by remember { mutableStateOf("") }
        var newStaffExtra by remember { mutableStateOf("") } // Salary or Route

        Dialog(onDismissRequest = { showManageStaffMasterDialog = false }) {
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
                            Icon(Icons.Default.SupervisorAccount, contentDescription = "Staff", tint = RoyalPurple900)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "स्टाफ व संकाय नियंत्रण" else "Faculty & Fleet Staff Controls",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = RoyalPurple900
                            )
                        }
                        IconButton(onClick = { showManageStaffMasterDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tabs: Teachers vs Drivers
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (staffTab == 0) RoyalPurple800 else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { staffTab = 0 }
                        ) {
                            Text(
                                text = if (isHindi) "शिक्षक संकाय (${state.teachers.size})" else "Teachers (${state.teachers.size})",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (staffTab == 0) Color.White else TextDarkSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (staffTab == 1) RoyalPurple800 else Color(0xFFF1F5F9),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { staffTab = 1 }
                        ) {
                            Text(
                                text = if (isHindi) "वाहन चालक (${state.drivers.size})" else "Drivers (${state.drivers.size})",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (staffTab == 1) Color.White else TextDarkSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Registration Fields
                    Text(
                        text = if (staffTab == 0)
                            (if (isHindi) "+ नया शिक्षक जोड़ें" else "+ Register New Teacher")
                        else
                            (if (isHindi) "+ नया चालक जोड़ें" else "+ Register New Driver"),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = RoyalPurple800
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newStaffName,
                        onValueChange = { newStaffName = it },
                        label = { Text(if (staffTab == 0) "Full Teacher Name" else "Driver Full Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newStaffPhone,
                            onValueChange = { newStaffPhone = it },
                            label = { Text("Mobile (10 Digits)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        OutlinedTextField(
                            value = newStaffExtra,
                            onValueChange = { newStaffExtra = it },
                            label = { Text(if (staffTab == 0) "Base Salary (₹)" else "Route / Bus #") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (newStaffName.isNotBlank() && newStaffPhone.isNotBlank()) {
                                if (staffTab == 0) {
                                    val sal = newStaffExtra.toLongOrNull() ?: 24000L
                                    viewModel.registerNewTeacher(newStaffName, newStaffPhone, sal, context)
                                } else {
                                    val r = if (newStaffExtra.isBlank()) "Route 4 - Sector 12" else newStaffExtra
                                    viewModel.registerNewDriver(newStaffName, newStaffPhone, r, context)
                                }
                                newStaffName = ""
                                newStaffPhone = ""
                                newStaffExtra = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (staffTab == 0) "Add Teacher / शिक्षक जोड़ें" else "Add Driver / चालक जोड़ें",
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (staffTab == 0) "Current Registered Teachers:" else "Current Registered Fleet Drivers:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (staffTab == 0) {
                            state.teachers.forEach { t ->
                                Surface(
                                    color = BackgroundCanvas,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(t.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Mob: ${t.phone} • Base: ₹${t.monthlyBaseSalary}", fontSize = 10.sp, color = TextMuted)
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteTeacher(t.id, context) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        } else {
                            state.drivers.forEach { d ->
                                Surface(
                                    color = BackgroundCanvas,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(d.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Mob: ${d.phone} • Route: ${d.routeAssigned}", fontSize = 10.sp, color = TextMuted)
                                        }
                                        IconButton(
                                            onClick = { viewModel.deleteDriver(d.id, context) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = CrimsonRed, modifier = Modifier.size(16.dp))
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

    // ==================== EDIT STUDENT DETAILS (TOTAL PRINCIPAL AUTHORITY) ====================
    showEditStudentDialog?.let { stu ->
        var editName by remember { mutableStateOf(stu.name) }
        var editClass by remember { mutableStateOf(stu.standard) }
        var editSection by remember { mutableStateOf(stu.section) }
        var editFather by remember { mutableStateOf(stu.fatherName) }
        var editDues by remember { mutableStateOf(stu.currentSessionFees.toString()) }

        AlertDialog(
            onDismissRequest = { showEditStudentDialog = null },
            title = {
                Text(
                    text = if (isHindi) "छात्र विवरण संपादित करें" else "Edit Student Profile",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Student Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editClass,
                            onValueChange = { editClass = it },
                            label = { Text("Class") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = editSection,
                            onValueChange = { editSection = it },
                            label = { Text("Section") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = editFather,
                        onValueChange = { editFather = it },
                        label = { Text("Father's Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDues,
                        onValueChange = { editDues = it },
                        label = { Text("Session Fees (₹)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val feeVal = editDues.toLongOrNull() ?: stu.currentSessionFees
                        viewModel.updateStudentDetails(
                            studentId = stu.id,
                            name = editName,
                            standard = editClass,
                            section = editSection,
                            fatherName = editFather,
                            currentSessionFees = feeVal,
                            context = context
                        )
                        showEditStudentDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                ) {
                    Text("Save / सहेजें", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStudentDialog = null }) {
                    Text("Cancel", color = TextDarkSecondary)
                }
            }
        )
    }

    // AI Student Success Predictions Dialog
    if (showStudentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showStudentSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = RoyalPurple800,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "छात्र सफलता भविष्यवाणी (AI)" else "Student Success Predictions",
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHindi)
                            "मशीन लर्निंग मॉडल उपस्थिति, गृहकार्य एवं आवधिक परीक्षा अंकों के आधार पर स्वचालित रूप से शैक्षणिक प्रदर्शन का पूर्वानुमान लगाता है।"
                        else
                            "Automated predictive scoring evaluating attendance velocity, test trajectories, and homework consistency across classes.",
                        fontSize = 12.sp,
                        color = TextDarkSecondary
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF0FDF4),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isHindi) "उच्च प्रदर्शन संभावना (92%+ अंक):" else "High Performance Probability (92%+):",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "Aarav Sharma (Class 10A), Priya Verma (Class 9B)",
                                fontSize = 11.sp,
                                color = TextDarkPrimary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFFFFBEB),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGlow.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isHindi) "मध्यम हस्तक्षेप अनुशंसित:" else "Moderate Intervention Recommended:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = AmberGlow
                            )
                            Text(
                                text = "Rohan Singh (Class 8A) - Attendance drop detected (-12%)",
                                fontSize = 11.sp,
                                color = TextDarkPrimary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showStudentSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                ) {
                    Text(if (isHindi) "ठीक है" else "Close", color = Color.White)
                }
            }
        )
    }

    // AI Resource Optimisation Dialog
    if (showResourceOptimizationDialog) {
        AlertDialog(
            onDismissRequest = { showResourceOptimizationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "संसाधन अनुकूलन (AI)" else "Resource Optimisation",
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isHindi)
                            "विद्यालय के भौतिक एवं मानव संसाधनों की दक्षता बढ़ाने हेतु AI सुझाव:"
                        else
                            "AI-driven utilization metrics for campus facilities, teacher schedules, and transportation logistics:",
                        fontSize = 12.sp,
                        color = TextDarkSecondary
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Bus Fleet Efficiency: 94.2%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = RoyalPurple900
                            )
                            Text(
                                text = "Optimal route sequencing saves 18.5 km/day on Route 4 & 7.",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Classroom Capacity Utilization: 88%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = EmeraldGreen
                            )
                            Text(
                                text = "Lab scheduling balanced evenly across high-school sections.",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showResourceOptimizationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                ) {
                    Text(if (isHindi) "ठीक है" else "Close", color = Color.White)
                }
            }
        )
    }

    if (showSchoolProfileDialog) {
        AlertDialog(
            onDismissRequest = { showSchoolProfileDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccountBalance, contentDescription = "School", tint = RoyalPurple900)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isHindi) "विद्यालय प्रोफ़ाइल प्रबंधन" else "Manage School Profile",
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isHindi) school.nameHi else school.nameEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple900
                    )
                    Text(text = "School Code / UDISE: ${school.code}", fontSize = 12.sp, color = TextDarkSecondary)
                    Text(text = "Principal: Dr. Alok Tripathi (Ph.D, M.Ed)", fontSize = 12.sp, color = TextDarkPrimary)
                    Text(text = "Affiliation: CBSE Central Board (Affiliation No: 2130842)", fontSize = 12.sp, color = TextDarkSecondary)
                    Text(text = "Total Students Enrolled: ${school.totalStudents}", fontSize = 12.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                    Text(text = "Total Faculty & Staff: ${school.totalTeachers + 4}", fontSize = 12.sp, color = TextDarkSecondary)
                    Text(text = "RTE Quota Students: ${school.rteStudentsCount} (100% Free Education)", fontSize = 12.sp, color = EmeraldGreen)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSchoolProfileDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                ) {
                    Text(if (isHindi) "बंद करें" else "Done", color = Color.White)
                }
            }
        )
    }

    if (showPayrollLedgerDialog) {
        MasterPayrollLedgerDialog(
            state = state,
            isHindi = isHindi,
            onDismiss = { showPayrollLedgerDialog = false },
            viewModel = viewModel
        )
    }

    if (showLeaveRequestsDialog) {
        ManageLeaveRequestsDialog(
            state = state,
            isHindi = isHindi,
            onDismiss = { showLeaveRequestsDialog = false },
            viewModel = viewModel
        )
    }

    if (showTimetableDialog) {
        CreateTimetableDialog(
            state = state,
            isHindi = isHindi,
            onDismiss = { showTimetableDialog = false },
            viewModel = viewModel
        )
    }
}

@Composable
fun MasterPayrollLedgerDialog(
    state: GmptpUiState,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    var selectedMonth by remember { mutableStateOf("August 2026") }
    val months = listOf("April 2026", "May 2026", "June 2026", "July 2026", "August 2026", "September 2026", "October 2026", "November 2026", "December 2026", "January 2027", "February 2027", "March 2027")
    var selectedTab by remember { mutableStateOf(0) } // 0: Teachers, 1: Drivers

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
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
                                .background(AmberGlow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Payroll", tint = AmberGlow, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "मुख्य वेतन बहीखाता (पेरोल)" else "Master Payroll Ledger",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple900
                            )
                            Text(
                                text = if (isHindi) "स्टाफ वेतन, अग्रिम व बकाया प्रबंधन" else "Base Salary, Advances & Outstanding",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Month Selector Scrollable Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    months.forEach { m ->
                        FilterChip(
                            selected = selectedMonth == m,
                            onClick = { selectedMonth = m },
                            label = { Text(m, fontSize = 11.sp, fontWeight = if (selectedMonth == m) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalPurple800,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Teachers vs Drivers Tab
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFFF1F5F9),
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (isHindi) "शिक्षक (${state.teachers.size})" else "Teachers (${state.teachers.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isHindi) "चालक (4)" else "Drivers (4)", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Staff List
                val staffList = if (selectedTab == 0) {
                    state.teachers.map {
                        PayrollItem(
                            id = it.id,
                            name = it.teacherName,
                            role = it.subjectOrDesignation,
                            phone = it.teacherMobile,
                            baseSalary = it.baseMonthlySalary.toInt(),
                            advancePaid = it.advanceSalaryPaid.toInt(),
                            previousYearOutstanding = it.previousYearOutstanding.toInt(),
                            totalPaid = it.totalPaid.toInt()
                        )
                    }
                } else {
                    listOf(
                        PayrollItem("D1", "Ramesh Yadav", "Senior Bus Driver", "9876543210", 18000, 2000, 3000, 16000),
                        PayrollItem("D2", "Suresh Kumar", "Route 2 Driver", "9876543211", 17500, 1500, 0, 16000),
                        PayrollItem("D3", "Manoj Tiwari", "Van Driver", "9876543212", 16000, 1000, 2000, 15000),
                        PayrollItem("D4", "Balram Singh", "Mini Bus Driver", "9876543213", 17000, 0, 1500, 17000)
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(staffList) { staff ->
                        val remainingBalance = (staff.baseSalary + staff.previousYearOutstanding) - (staff.advancePaid + staff.totalPaid)
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(staff.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                        Text("${staff.role} • ${staff.phone}", fontSize = 11.sp, color = TextDarkSecondary)
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (remainingBalance <= 0) EmeraldGreen.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = if (remainingBalance <= 0) "CLEARED" else "DUE: ₹$remainingBalance",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (remainingBalance <= 0) EmeraldGreen else CrimsonRed,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderLight)

                                // Payroll breakdown grid
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(if (isHindi) "मासिक मूल वेतन" else "Base Salary", fontSize = 10.sp, color = TextMuted)
                                        Text("₹${staff.baseSalary}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                    }
                                    Column {
                                        Text(if (isHindi) "अग्रिम वेतन" else "Advance Paid", fontSize = 10.sp, color = TextMuted)
                                        Text("₹${staff.advancePaid}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AmberGlow)
                                    }
                                    Column {
                                        Text(if (isHindi) "पूर्व बकाया वेतन" else "Prev Outstanding", fontSize = 10.sp, color = TextMuted)
                                        Text("₹${staff.previousYearOutstanding}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonRed)
                                    }
                                    Column {
                                        Text(if (isHindi) "कुल भुगतान" else "Total Paid", fontSize = 10.sp, color = TextMuted)
                                        Text("₹${staff.totalPaid}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Action buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            android.widget.Toast.makeText(context, "Advance recorded for ${staff.name}", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(14.dp), tint = AmberGlow)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isHindi) "अग्रिम दर्ज करें" else "+ Advance", fontSize = 11.sp, color = AmberGlow)
                                    }

                                    Button(
                                        onClick = {
                                            android.widget.Toast.makeText(context, "Salary Payment Slip generated for ${staff.name}", android.widget.Toast.LENGTH_SHORT).show()
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.White)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isHindi) "वेतन पर्ची" else "Salary Slip", fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple900),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isHindi) "पूर्ण हुआ" else "Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class PayrollItem(
    val id: String,
    val name: String,
    val role: String,
    val phone: String,
    val baseSalary: Int,
    val advancePaid: Int,
    val previousYearOutstanding: Int,
    val totalPaid: Int
)

@Composable
fun ManageLeaveRequestsDialog(
    state: GmptpUiState,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    var filterRole by remember { mutableStateOf("ALL") } // ALL, TEACHER, STUDENT

    Dialog(onDismissRequest = onDismiss) {
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
                                .background(CrimsonRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.EventBusy, contentDescription = "Leave", tint = CrimsonRed, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "अवकाश अनुरोध प्रबंधन हब" else "Manage Leave Requests Hub",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple900
                            )
                            Text(
                                text = if (isHindi) "शिक्षक व छात्र अवकाश आवेदन" else "Staff & Student Applications",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips: All, Teachers, Students
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterRole == "ALL",
                        onClick = { filterRole = "ALL" },
                        label = { Text("All (${state.leaveRequests.size})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalPurple800, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = filterRole == "TEACHER",
                        onClick = { filterRole = "TEACHER" },
                        label = { Text("Teachers (${state.leaveRequests.count { it.applicantRole.equals("TEACHER", ignoreCase = true) }})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalPurple800, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = filterRole == "STUDENT",
                        onClick = { filterRole = "STUDENT" },
                        label = { Text("Students (${state.leaveRequests.count { it.applicantRole.equals("STUDENT", ignoreCase = true) }})", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalPurple800, selectedLabelColor = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                val filteredLeaves = state.leaveRequests.filter {
                    filterRole == "ALL" || it.applicantRole.equals(filterRole, ignoreCase = true)
                }

                if (filteredLeaves.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(if (isHindi) "कोई अवकाश आवेदन नहीं मिला" else "No leave applications found", color = TextDarkSecondary, fontSize = 13.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredLeaves) { leave ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF8FAFC),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(leave.applicantName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = if (leave.applicantRole.equals("TEACHER", ignoreCase = true)) RoyalPurpleLight else Color(0xFFE0F2FE)
                                            ) {
                                                Text(
                                                    text = leave.applicantRole,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (leave.applicantRole.equals("TEACHER", ignoreCase = true)) RoyalPurple800 else Color(0xFF0284C7),
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        // Leave Type chip (Casual / Emergency)
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (leave.leaveType == "EMERGENCY") CrimsonRed.copy(alpha = 0.15f) else AmberGlow.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = leave.leaveType,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (leave.leaveType == "EMERGENCY") CrimsonRed else Color(0xFFB45309),
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Dates: ${leave.fromDate} to ${leave.toDate}", fontSize = 11.sp, color = TextDarkSecondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Reason: ${leave.reason}", fontSize = 12.sp, color = TextDarkPrimary)

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Action buttons or Status badge
                                    if (leave.status == "PENDING") {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = { viewModel.acceptLeaveRequest(leave.id, context) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Check, contentDescription = "Accept", modifier = Modifier.size(16.dp), tint = Color.White)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isHindi) "स्वीकृत करें" else "Accept", color = Color.White, fontWeight = FontWeight.Bold)
                                            }

                                            Button(
                                                onClick = { viewModel.rejectLeaveRequest(leave.id, context) },
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "Reject", modifier = Modifier.size(16.dp), tint = Color.White)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(if (isHindi) "अस्वीकृत करें" else "Reject", color = Color.White, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (leave.status == "ACCEPTED") EmeraldGreen.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.15f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = if (leave.status == "ACCEPTED") Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                    contentDescription = null,
                                                    tint = if (leave.status == "ACCEPTED") EmeraldGreen else CrimsonRed,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (leave.status == "ACCEPTED") "STATUS: APPROVED" else "STATUS: REJECTED",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = if (leave.status == "ACCEPTED") EmeraldGreen else CrimsonRed
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple900),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isHindi) "पूर्ण हुआ" else "Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CreateTimetableDialog(
    state: GmptpUiState,
    isHindi: Boolean,
    onDismiss: () -> Unit,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    var standard by remember { mutableStateOf("Class 10") }
    var section by remember { mutableStateOf("A") }
    var dayOfWeek by remember { mutableStateOf("Monday") }
    var periodNumber by remember { mutableStateOf(1) }
    var subject by remember { mutableStateOf("Mathematics") }
    var teacherName by remember { mutableStateOf("Prof. Rajesh Sharma") }
    var startTime by remember { mutableStateOf("08:30 AM") }
    var endTime by remember { mutableStateOf("09:15 AM") }

    val classes = listOf("Nursery", "LKG", "UKG", "Class 1", "Class 2", "Class 3", "Class 4", "Class 5", "Class 6", "Class 7", "Class 8", "Class 9", "Class 10", "Class 11", "Class 12")
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardWhite),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
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
                            Icon(Icons.Default.CalendarMonth, contentDescription = "Timetable", tint = ElectricBlue, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isHindi) "समय सारिणी निर्माण मॉड्यूल" else "Create Timetable Module",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple900
                            )
                            Text(
                                text = if (isHindi) "कक्षा, विषय, शिक्षक व समय आवंटन" else "Classes, Periods, Teachers & Timings",
                                fontSize = 11.sp,
                                color = TextDarkSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        // Class selector row
                        Text(if (isHindi) "कक्षा चुनें:" else "Select Class:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkSecondary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            classes.forEach { cls ->
                                FilterChip(
                                    selected = standard == cls,
                                    onClick = { standard = cls },
                                    label = { Text(cls, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RoyalPurple800, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        // Day of week selector
                        Text(if (isHindi) "वार चुनें:" else "Select Day:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkSecondary)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            days.forEach { d ->
                                FilterChip(
                                    selected = dayOfWeek == d,
                                    onClick = { dayOfWeek = d },
                                    label = { Text(d, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = ElectricBlue, selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }

                    item {
                        // Period & Subject inputs
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = periodNumber.toString(),
                                onValueChange = { periodNumber = it.toIntOrNull() ?: 1 },
                                label = { Text("Period #", fontSize = 11.sp) },
                                modifier = Modifier.weight(0.8f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = subject,
                                onValueChange = { subject = it },
                                label = { Text("Subject", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.5f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        // Teacher & Timings
                        OutlinedTextField(
                            value = teacherName,
                            onValueChange = { teacherName = it },
                            label = { Text("Teacher Name", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = { startTime = it },
                                label = { Text("Start Time", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = { endTime = it },
                                label = { Text("End Time", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                viewModel.saveTimetablePeriod(
                                    period = com.example.data.TimetablePeriod(
                                        id = "TT-${System.currentTimeMillis() % 10000}",
                                        standard = standard,
                                        section = section,
                                        dayOfWeek = dayOfWeek,
                                        periodNumber = periodNumber,
                                        subject = subject,
                                        teacherName = teacherName,
                                        startTime = startTime,
                                        endTime = endTime
                                    ),
                                    context = context
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save", tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isHindi) "समय सारिणी सहेजें" else "Save Timetable", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isHindi) "$standard ($dayOfWeek) की वर्तमान समय सारिणी:" else "Current Schedule for $standard ($dayOfWeek):",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple900
                        )
                    }

                    val classPeriods = state.timetablePeriods.filter { it.standard == standard && it.dayOfWeek == dayOfWeek }
                    if (classPeriods.isEmpty()) {
                        item {
                            Text(if (isHindi) "इस दिन के लिए कोई घंटी निर्धारित नहीं है।" else "No periods scheduled for this day yet.", fontSize = 11.sp, color = TextDarkSecondary)
                        }
                    } else {
                        items(classPeriods) { p ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
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
                                            Text("${p.teacherName} • ${p.startTime} - ${p.endTime}", fontSize = 10.sp, color = TextDarkSecondary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
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

@Composable
fun PrincipalActionCard(
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
