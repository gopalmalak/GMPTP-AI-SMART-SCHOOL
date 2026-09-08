package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun LiveBusTrackingScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val isHindi = state.isHindi
    val bus = state.buses.firstOrNull() ?: return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Telemetry Status Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = bus.vehicleNumber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Text(
                                text = bus.routeName,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (bus.isLive) EmeraldGreen else Color(0xFF94A3B8)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (bus.isLive) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (bus.isLive) "LIVE GPS ACTIVE" else "INACTIVE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TelemetryPill(
                            label = if (isHindi) "अनुमानित समय" else "ETA to Stop",
                            value = "${bus.etaMinutes} mins",
                            color = BrilliantGold,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryPill(
                            label = if (isHindi) "वर्तमान गति" else "Speed",
                            value = "${bus.speedKmh} km/h",
                            color = EmeraldGreen,
                            modifier = Modifier.weight(1f)
                        )
                        TelemetryPill(
                            label = if (isHindi) "सवार छात्र" else "Students",
                            value = "${bus.onboardCount}",
                            color = ElectricBlue,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Live Simulated Map Container
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Custom Canvas Route Simulation
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height

                        // Draw Grid Map Streets
                        drawLine(
                            color = Color(0xFF334155),
                            start = Offset(0f, h * 0.3f),
                            end = Offset(w, h * 0.3f),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = Color(0xFF334155),
                            start = Offset(0f, h * 0.7f),
                            end = Offset(w, h * 0.7f),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = Color(0xFF334155),
                            start = Offset(w * 0.3f, 0f),
                            end = Offset(w * 0.3f, h),
                            strokeWidth = 6f
                        )
                        drawLine(
                            color = Color(0xFF334155),
                            start = Offset(w * 0.7f, 0f),
                            end = Offset(w * 0.7f, h),
                            strokeWidth = 6f
                        )

                        // Main Bus Route Path (Vibrant Gold line)
                        val busPath = androidx.compose.ui.graphics.Path().apply {
                            moveTo(w * 0.15f, h * 0.85f)
                            lineTo(w * 0.3f, h * 0.7f)
                            lineTo(w * 0.3f, h * 0.3f)
                            lineTo(w * 0.65f, h * 0.3f)
                            lineTo(w * 0.85f, h * 0.15f)
                        }

                        drawPath(
                            path = busPath,
                            color = BrilliantGold,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 8f,
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 10f), 0f)
                            )
                        )

                        // School Pin (Destination)
                        drawCircle(
                            color = Color.White,
                            radius = 16f,
                            center = Offset(w * 0.85f, h * 0.15f)
                        )
                        drawCircle(
                            color = RoyalPurple600,
                            radius = 12f,
                            center = Offset(w * 0.85f, h * 0.15f)
                        )

                        // Bus Live Marker
                        val busPos = Offset(w * 0.48f, h * 0.3f)
                        drawCircle(
                            color = EmeraldGreen.copy(alpha = 0.3f),
                            radius = 28f,
                            center = busPos
                        )
                        drawCircle(
                            color = EmeraldGreen,
                            radius = 16f,
                            center = busPos
                        )
                    }

                    // Map overlay labels
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(14.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Next: ${bus.nextStop}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Driver info card overlay at bottom
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoyalPurple900.copy(alpha = 0.92f))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(BrilliantGold),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Driver",
                                        tint = RoyalPurple900,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = bus.driverName,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = bus.driverPhone,
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldGreen
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Call Driver",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
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

@Composable
private fun TelemetryPill(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, BorderLight, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
    }
}
