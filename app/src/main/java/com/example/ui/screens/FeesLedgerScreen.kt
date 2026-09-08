package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FeeApprovalStatus
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun FeesLedgerScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    val school = state.currentSchool
    var selectedTab by remember { mutableStateOf(0) } // 0: Student Fees & RTE, 1: Teacher Approvals, 2: Staff Salary Advances

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        // Tab Selector Bar
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = RoyalPurple700
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isHindi) "छात्र बहीखाता व RTE" else "Student Dues & RTE", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    val pendingCount = state.feeRecords.count { it.status == FeeApprovalStatus.PENDING_VERIFICATION }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isHindi) "अनुमोदन" else "Approvals", fontWeight = FontWeight.Bold)
                        if (pendingCount > 0) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                                shape = CircleShape,
                                color = CrimsonRed
                            ) {
                                Text(
                                    text = "$pendingCount",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text(if (isHindi) "वेतन अग्रिम" else "Salary Advance", fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Summary Ledger Split Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isHindi) "खाता बही विभाजन (स्कूल बनाम परिवहन)" else "FEE LEDGER SEGREGATION",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary,
                                    letterSpacing = 0.6.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(RoyalPurpleLight)
                                            .padding(10.dp)
                                    ) {
                                        Text(text = if (isHindi) "विद्यालय शुल्क" else "School Fees", fontSize = 10.sp, color = RoyalPurple700)
                                        Text(text = "₹${school.schoolFeesCollected}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = RoyalPurple900)
                                    }

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFFEF3C7))
                                            .padding(10.dp)
                                    ) {
                                        Text(text = if (isHindi) "परिवहन शुल्क" else "Transport Fees", fontSize = 10.sp, color = AmberGlow)
                                        Text(text = "₹${school.transportFeesCollected}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isHindi) "RTE श्रेणी छात्र (100% शून्य शुल्क):" else "RTE Category Students (100% Zero Fee):",
                                        fontSize = 11.sp,
                                        color = TextDarkSecondary
                                    )
                                    Text(
                                        text = "${school.rteStudentsCount} Students",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Surface(
                                    onClick = { viewModel.navigateTo("REGISTER_STUDENT") },
                                    shape = RoundedCornerShape(10.dp),
                                    color = RoyalPurple800,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isHindi) "+ नया छात्र पंजीकृत करें (बहु-स्तरीय फीस बहीखाता)" else "+ Register Student & Multi-Tier Fee Ledger",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Student Records with Previous vs Current split & RTE toggle
                    item {
                        Text(
                            text = if (isHindi) "छात्र शुल्क एवं RTE स्थिति प्रबंधन" else "STUDENT LEDGER & RTE TOGGLE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }

                    items(state.students) { student ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "${student.name} • Roll: ${student.rollNo}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkPrimary
                                        )
                                        Text(
                                            text = "${student.standard}-${student.section}",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }

                                    // Explicit RTE Checkbox Toggle
                                    Surface(
                                        onClick = { viewModel.toggleRteStudent(student.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (student.isRte) EmeraldGreen.copy(alpha = 0.15f) else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (student.isRte) EmeraldGreen else Color(0xFFCBD5E1)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = student.isRte,
                                                onCheckedChange = { viewModel.toggleRteStudent(student.id) },
                                                colors = CheckboxDefaults.colors(checkedColor = EmeraldGreen),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "RTE Student",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (student.isRte) EmeraldGreen else TextDarkSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                if (student.isRte) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EmeraldGreen
                                    ) {
                                        Text(
                                            text = if (isHindi) "RTE श्रेणी - 100% निःशुल्क शिक्षा (फीस: ₹0)" else "RTE Category - 100% Free Education (Fees: ₹0)",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                } else {
                                    // 4-way split breakdown
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        LedgerMiniBox(
                                            title = if (isHindi) "पिछला स्कूल" else "Prev School",
                                            amount = "₹${student.previousYearDues}",
                                            modifier = Modifier.weight(1f)
                                        )
                                        LedgerMiniBox(
                                            title = if (isHindi) "वर्तमान स्कूल" else "Curr School",
                                            amount = "₹${student.currentSessionFees}",
                                            modifier = Modifier.weight(1f)
                                        )
                                        LedgerMiniBox(
                                            title = if (isHindi) "पिछला वाहन" else "Prev Vahan",
                                            amount = "₹${student.previousYearTransportDues}",
                                            modifier = Modifier.weight(1f)
                                        )
                                        LedgerMiniBox(
                                            title = if (isHindi) "वर्तमान वाहन" else "Curr Vahan",
                                            amount = "₹${student.currentSessionTransportFees}",
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Teacher-to-Principal Fee Collection Approval Workflow
                    item {
                        Text(
                            text = if (isHindi) "अध्यापक शुल्क संग्रह सत्यापन कतार" else "TEACHER FEE COLLECTION APPROVALS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    items(state.feeRecords) { fee ->
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = fee.studentName,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = TextDarkPrimary
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = RoyalPurpleLight
                                            ) {
                                                Text(
                                                    text = fee.standard,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = RoyalPurple800,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "₹${fee.amount} • ${fee.feeCategory}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RoyalPurple800
                                        )
                                        Text(
                                            text = "Collected by: ${fee.collectedByTeacher} • ${fee.date}",
                                            fontSize = 11.sp,
                                            color = TextDarkSecondary
                                        )
                                    }

                                    // Status Badge on Top-Right
                                    if (fee.status == FeeApprovalStatus.PENDING_VERIFICATION) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AmberGlow.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, AmberGlow.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = if (isHindi) "सत्यापन लंबित" else "Pending Verification",
                                                color = Color(0xFFB45309),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldGreen.copy(alpha = 0.15f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = if (isHindi) "स्वीकृत ✓" else "Approved ✓",
                                                color = EmeraldGreen,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                if (fee.status == FeeApprovalStatus.PENDING_VERIFICATION) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Button(
                                        onClick = { viewModel.approveFeeByPrincipal(fee.id, context) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Approve & Send Receipt",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Approve & Send Receipt / स्वीकृत करें",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Staff & Driver Financials: Advanced Salary Management & Ledgers
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = if (isHindi) "कर्मचारी वेतन एवं अग्रिम प्रणाली (Staff Ledgers)" else "STAFF & DRIVER SALARY & ADVANCE LEDGER",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary,
                                    letterSpacing = 0.6.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Jump Buttons to Dedicated Full Ledgers
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        onClick = { viewModel.navigateTo("TEACHER_SALARY") },
                                        shape = RoundedCornerShape(10.dp),
                                        color = RoyalPurple800,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = if (isHindi) "शिक्षक वेतन बहीखाता" else "Teacher Salary",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = "${state.teacherSalaries.size} Faculty Active",
                                                    fontSize = 10.sp,
                                                    color = Color.White.copy(alpha = 0.75f)
                                                )
                                            }
                                        }
                                    }

                                    Surface(
                                        onClick = { viewModel.navigateTo("DRIVER_SALARY") },
                                        shape = RoundedCornerShape(10.dp),
                                        color = AmberGlow,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = TextDarkPrimary, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = if (isHindi) "चालक वेतन बहीखाता" else "Driver Salary",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextDarkPrimary
                                                )
                                                Text(
                                                    text = "${state.driverSalaries.size} Drivers Active",
                                                    fontSize = 10.sp,
                                                    color = TextDarkPrimary.copy(alpha = 0.75f)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = if (isHindi) "हालिया वेतन अग्रिम व पर्ची स्थिति:" else "Recent Salary Records & Advance Slips:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDarkPrimary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                state.teacherSalaries.take(3).forEach { sal ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF8FAFC))
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = sal.teacherName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                                            Text(text = "${sal.subjectOrDesignation} • ${sal.monthYear}", fontSize = 11.sp, color = TextDarkSecondary)
                                            Text(
                                                text = "Base: ₹${sal.baseMonthlySalary} | Adv: ₹${sal.advanceSalaryPaid} | Due: ₹${sal.remainingBalance}",
                                                fontSize = 10.sp,
                                                color = if (sal.remainingBalance > 0) CrimsonRed else EmeraldGreen,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Surface(
                                            onClick = { viewModel.navigateTo("TEACHER_SALARY") },
                                            shape = RoundedCornerShape(6.dp),
                                            color = RoyalPurpleLight
                                        ) {
                                            Text(
                                                text = if (isHindi) "खाता बही" else "Ledger",
                                                color = RoyalPurple700,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
    }
}

@Composable
private fun LedgerMiniBox(title: String, amount: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, BorderLight, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Text(text = title, fontSize = 9.sp, color = TextMuted)
        Text(text = amount, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
    }
}
