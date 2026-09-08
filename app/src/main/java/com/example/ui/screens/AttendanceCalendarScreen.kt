package com.example.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceStatus
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun AttendanceCalendarScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val isHindi = state.isHindi
    val months = listOf("July 2026", "August 2026", "September 2026")
    var currentMonthIndex by remember { mutableStateOf(2) } // September 2026
    var selectedDay by remember { mutableStateOf(5) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        // Month Navigation Header
        Card(
            shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = RoyalPurple800),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = {
                            if (currentMonthIndex > 0) currentMonthIndex--
                        },
                        enabled = currentMonthIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Month",
                            tint = if (currentMonthIndex > 0) BrilliantGold else Color.Gray
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = months[currentMonthIndex],
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (isHindi) "ऐतिहासिक उपस्थिति संशोधन उपलब्ध" else "Historical Overrides Enabled",
                            fontSize = 11.sp,
                            color = BrilliantGold
                        )
                    }

                    IconButton(
                        onClick = {
                            if (currentMonthIndex < months.size - 1) currentMonthIndex++
                        },
                        enabled = currentMonthIndex < months.size - 1
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Month",
                            tint = if (currentMonthIndex < months.size - 1) BrilliantGold else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Days row (e.g. 1 to 7)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    (1..7).forEach { day ->
                        val isSelected = day == selectedDay
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BrilliantGold else Color.White.copy(alpha = 0.1f))
                                .clickable { selectedDay = day },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$day",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) RoyalPurple900 else Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // Student List for selected date with P / A / L / LT Overrides
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isHindi) "दिनांक: $selectedDay ${months[currentMonthIndex]}" else "Date: $selectedDay ${months[currentMonthIndex]}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary
                    )

                    Text(
                        text = if (isHindi) "उपस्थिति बटन दबाकर तुरंत बदलें" else "Tap P/A/L to Override",
                        fontSize = 11.sp,
                        color = RoyalPurple600
                    )
                }
            }

            items(state.students) { student ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "${student.name} • Roll ${student.rollNo}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Text(
                                text = "${student.standard}-${student.section} • Overrides: Present (${student.attendancePercentage}%)",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        // Override Status Pill Buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            AttendanceStatusPill("P", EmeraldGreen) {
                                viewModel.updateStudentAttendance(student.id, AttendanceStatus.PRESENT)
                            }
                            AttendanceStatusPill("A", CrimsonRed) {
                                viewModel.updateStudentAttendance(student.id, AttendanceStatus.ABSENT)
                            }
                            AttendanceStatusPill("L", AmberGlow) {
                                viewModel.updateStudentAttendance(student.id, AttendanceStatus.LEAVE)
                            }
                            AttendanceStatusPill("LT", ElectricBlue) {
                                viewModel.updateStudentAttendance(student.id, AttendanceStatus.LATE)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceStatusPill(label: String, color: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .size(width = 28.dp, height = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}
