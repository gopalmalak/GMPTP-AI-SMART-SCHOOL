package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.UserRole
import com.example.ui.components.AdMobBannerComponent
import com.example.ui.components.StaffSalaryLedgerWidget
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

data class BusStopItem(
    val name: String,
    val time: String,
    val studentCount: Int,
    var isCompleted: Boolean = false
)

@Composable
fun DriverDashboardScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    val driver = state.currentUser
    val bus = state.buses.firstOrNull() ?: return

    var showSosConfirmation by remember { mutableStateOf(false) }

    var stops by remember {
        mutableStateOf(
            listOf(
                BusStopItem("Stop 1: South Extension Part 2", "07:15 AM", 6, true),
                BusStopItem("Stop 2: AIIMS Ring Road Flyover", "07:25 AM", 8, true),
                BusStopItem("Stop 3: Hauz Khas Market Gate 2", "07:35 AM", 7, false),
                BusStopItem("Stop 4: Green Park Metro Station", "07:45 AM", 5, false),
                BusStopItem("Final Destination: School Campus Main Gate", "08:00 AM", 0, false)
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Principal Emergency Broadcast Notice (If any for all school or drivers)
        val driverAnnouncements = state.announcements.filter { it.targetAudience == "ALL_SCHOOL" || it.targetAudience == "DRIVERS" }
        if (driverAnnouncements.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CrimsonRed.copy(alpha = 0.1f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, CrimsonRed),
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
                                Icon(Icons.Default.Warning, contentDescription = "Alert", tint = CrimsonRed, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "🚨 प्रधानाचार्य आपातकालीन निर्देश" else "🚨 PRINCIPAL EMERGENCY DISPATCH",
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
                                    text = "LIVE DISPATCH",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        driverAnnouncements.forEach { ann ->
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
                            Text(
                                text = "Dispatched: ${ann.formattedTime} • By: ${ann.senderName}",
                                fontSize = 9.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }

        // Vehicle Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalPurple900, RoyalPurple800)
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BrilliantGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = "Bus",
                                    tint = RoyalPurple900,
                                    modifier = Modifier.size(30.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = bus.vehicleNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = bus.routeName,
                                    fontSize = 12.sp,
                                    color = BrilliantGold
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (bus.isLive) EmeraldGreen else Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (bus.isLive) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = if (bus.isLive) "LIVE ON ROUTE" else "PARKED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "सवार छात्र संख्या" else "Students Onboard",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = "${bus.onboardCount} / ${bus.totalCapacity}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (isHindi) "वर्तमान गति" else "Current Speed",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = "${bus.speedKmh} km/h",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrilliantGold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Driver Logistics Quick-Action Grid (Mirroring all 5 Driver Sidebar Menu Options)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isHindi) "चालक त्वरित ग्रिड (QUICK ACTIONS)" else "DRIVER LOGISTICS QUICK-ACTION GRID",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkSecondary,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: Start/Stop Route Broadcast & Assigned Passenger Checklist
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DriverActionCard(
                        title = if (bus.isLive) (if (isHindi) "रूट प्रसारण बंद करें" else "Stop Route Broadcast") else (if (isHindi) "रूट प्रसारण शुरू करें" else "Start Route Broadcast"),
                        subtitle = if (bus.isLive) "GPS Active (Live)" else "Click to stream GPS",
                        icon = if (bus.isLive) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                        color = if (bus.isLive) CrimsonRed else EmeraldGreen,
                        onClick = { viewModel.toggleDriverGpsRoute(bus.routeId, context) },
                        modifier = Modifier.weight(1f)
                    )
                    DriverActionCard(
                        title = if (isHindi) "यात्री चेकलिस्ट" else "Passenger Checklist",
                        subtitle = if (isHindi) "छात्र बोर्डिंग स्थिति" else "Attendance & Drops",
                        icon = Icons.Default.Checklist,
                        color = RoyalPurple800,
                        onClick = { /* already viewable on console */ },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: My Route Details & My Read-Only Salary Slip (Lock View)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DriverActionCard(
                        title = if (isHindi) "मार्ग विवरण (मानचित्र)" else "My Route Details",
                        subtitle = if (isHindi) "स्टॉपेज व रूट मैप" else "${bus.routeName} (${bus.vehicleNumber})",
                        icon = Icons.Default.DirectionsBus,
                        color = ElectricBlue,
                        onClick = { viewModel.navigateTo("GPS") },
                        modifier = Modifier.weight(1f)
                    )
                    DriverActionCard(
                        title = if (isHindi) "मेरी वेतन पर्ची (लॉक)" else "Salary Slip (Lock View)",
                        subtitle = if (isHindi) "मासिक विवरण केवल-पठनीय" else "Read-Only Salary Ledger",
                        icon = Icons.Default.Receipt,
                        color = AmberGlow,
                        onClick = { viewModel.navigateTo("DRIVER_SALARY") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Logout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DriverActionCard(
                        title = if (isHindi) "सुरक्षित लॉगआउट" else "Logout",
                        subtitle = if (isHindi) "सत्र समाप्त करें एवं लॉक करें" else "Secure Session Lock",
                        icon = Icons.Default.Lock,
                        color = TextDarkSecondary,
                        onClick = { viewModel.logout() },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // High-Contrast Zero Distraction GPS Streaming Console
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isHindi) "जीपीएस प्रसारण एवं ट्रिप नियंत्रण" else "LIVE GPS BROADCAST CONSOLE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (bus.isLive)
                            "Broadcasting: Lat ${bus.currentLatitude}, Long ${bus.currentLongitude}"
                        else
                            "Broadcasting is currently stopped. Parents see last parked location.",
                        fontSize = 12.sp,
                        color = if (bus.isLive) EmeraldGreen else TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Big Start Trip / End Trip Toggle Button (Min 64dp touch target for car mounts)
                    Button(
                        onClick = { viewModel.toggleDriverGpsRoute(bus.routeId, context) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (bus.isLive) CrimsonRed else EmeraldGreen
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    ) {
                        Icon(
                            imageVector = if (bus.isLive) Icons.Default.StopCircle else Icons.Default.PlayCircle,
                            contentDescription = "GPS Toggle",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (bus.isLive)
                                (if (isHindi) "ट्रिप समाप्त करें (STOP TRIP)" else "END TRIP (STOP BROADCAST)")
                            else
                                (if (isHindi) "ट्रिप प्रारंभ करें (START TRIP)" else "START TRIP (STREAM GPS)"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Emergency SOS Alert Button (One-tap instant broadcast)
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CrimsonRed.copy(alpha = 0.08f)),
                border = androidx.compose.foundation.BorderStroke(2.dp, CrimsonRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(CrimsonRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "SOS",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = if (isHindi) "आपातकालीन SOS बटन" else "EMERGENCY SOS BUTTON",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CrimsonRed
                            )
                            Text(
                                text = if (isHindi) "प्रधानाचार्य एवं अभिभावकों को तत्काल अलर्ट भेजें" else "Instantly alerts Principal & all Parents",
                                fontSize = 11.sp,
                                color = TextDarkPrimary
                            )
                        }
                    }

                    Button(
                        onClick = { showSosConfirmation = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text(
                            text = "SOS",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Dedicated Driver Personal Salary Card/Widget (Strict Read-Only Staff Ledger)
        item {
            StaffSalaryLedgerWidget(
                state = state,
                isDriverRole = true
            )
        }

        // Route Checklist & Stop-by-Stop Onboard Counter
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
                        Text(
                            text = if (isHindi) "रूट चेकलिस्ट एवं स्टॉप" else "ROUTE STOPS CHECKLIST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            letterSpacing = 0.6.sp
                        )

                        Text(
                            text = "${stops.count { it.isCompleted }} / ${stops.size} Done",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    stops.forEachIndexed { idx, stop ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(if (stop.isCompleted) EmeraldGreen else Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (stop.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Done",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "${idx + 1}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkSecondary
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = stop.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (stop.isCompleted) TextDarkSecondary else TextDarkPrimary
                                    )
                                    Text(
                                        text = "${stop.time} • ${stop.studentCount} Students to board",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    val updated = stops.toMutableList()
                                    val current = updated[idx]
                                    val newState = !current.isCompleted
                                    updated[idx] = current.copy(isCompleted = newState)
                                    stops = updated
                                    viewModel.toggleBusStudentCheck(bus.routeId, if (newState) current.studentCount else -current.studentCount)
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (stop.isCompleted) Color(0xFFF1F5F9) else EmeraldGreen
                            ) {
                                Text(
                                    text = if (stop.isCompleted) "Undo" else "Reached",
                                    color = if (stop.isCompleted) TextDarkSecondary else Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        if (idx < stops.size - 1) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = BorderLight)
                        }
                    }
                }
            }
        }

        // AdMob Banner (Driver non-paying tier)
        item {
            AdMobBannerComponent(
                userRole = UserRole.DRIVER,
                trialDaysRemaining = 0,
                hasPaidSubscription = false,
                viewModel = viewModel
            )
        }
    }

    // Emergency SOS Confirmation Modal
    if (showSosConfirmation) {
        Dialog(onDismissRequest = { showSosConfirmation = false }) {
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
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CrimsonRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = CrimsonRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isHindi) "आपातकालीन SOS प्रसारित करें?" else "Broadcast Emergency SOS?",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CrimsonRed
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isHindi)
                            "यह विद्यालय प्रधानाचार्य एवं सभी पंजीकृत माता-पिताओं को तात्कालिक पुश नोटिफिकेशन एवं वाहन जीपीएस लोकेशन भेजेगा।"
                        else
                            "This will instantly broadcast high-priority push notifications and live GPS coordinates to the School Principal and all registered parents.",
                        fontSize = 12.sp,
                        color = TextDarkSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showSosConfirmation = false },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = TextDarkSecondary)
                        }

                        Button(
                            onClick = {
                                viewModel.sendEmergencySos(context)
                                showSosConfirmation = false
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CONFIRM SOS", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
