package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.BusVehicle
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

/**
 * Interactive Live Transport Fleet Radar powered by Google Maps SDK.
 * Renders real-time vehicle coordinates streaming from the Firestore transport collection.
 */
@Composable
fun PrincipalLiveTransportMapWidget(
    state: GmptpUiState,
    viewModel: GmptpViewModel,
    onOpenFullRadar: () -> Unit = {}
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    val buses = state.buses

    var selectedRouteId by remember { mutableStateOf(buses.firstOrNull()?.routeId ?: "") }
    val selectedBus = buses.find { it.routeId == selectedRouteId } ?: buses.firstOrNull()

    var isSatelliteView by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Default center around active vehicles or Delhi NCR
    val initialTarget = remember(buses) {
        val liveBus = buses.firstOrNull { it.isLive } ?: buses.firstOrNull()
        if (liveBus != null) {
            LatLng(liveBus.currentLatitude, liveBus.currentLongitude)
        } else {
            LatLng(28.5672, 77.2100)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialTarget, 12.8f)
    }

    // Auto-focus camera when selectedBus changes
    LaunchedEffect(selectedBus?.routeId, selectedBus?.currentLatitude, selectedBus?.currentLongitude) {
        selectedBus?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLng(
                    LatLng(it.currentLatitude, it.currentLongitude)
                ),
                durationMs = 800
            )
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BorderLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title, Live Status Pill & Fullscreen Toggle
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
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(RoyalPurple800),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = "Live Transport",
                            tint = BrilliantGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isHindi) "लाइव वाहन जीपीएस ट्रैकिंग" else "Live Transport GPS Radar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldGreenLight
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = EmeraldGreen
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (isHindi) "गूगल मैप्स एसडीके • फायरस्टोर डेटा सिंक" else "Google Maps SDK • Firestore Collection Sync",
                            fontSize = 11.sp,
                            color = TextDarkSecondary
                        )
                    }
                }

                // Expand Fullscreen Radar Button
                IconButton(
                    onClick = onOpenFullRadar,
                    modifier = Modifier
                        .size(36.dp)
                        .background(RoyalPurpleLight, RoundedCornerShape(10.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Fullscreen Map",
                        tint = RoyalPurple800,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fleet Overview Metrics Bar
            val liveCount = buses.count { it.isLive }
            val totalStudentsOnboard = buses.sumOf { it.onboardCount }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = RoyalPurpleLight,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isHindi) "कुल वाहन" else "Fleet Size",
                            fontSize = 10.sp,
                            color = RoyalPurple800,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${buses.size} Buses",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple900
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = EmeraldGreenLight,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isHindi) "सक्रिय मार्ग" else "Active on Route",
                            fontSize = 10.sp,
                            color = EmeraldGreen,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$liveCount Live",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF065F46)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1.2f)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = if (isHindi) "सवार छात्र" else "Students In Transit",
                            fontSize = 10.sp,
                            color = AmberGlow,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "$totalStudentsOnboard Students",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Interactive Google Maps SDK Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, BorderLight, RoundedCornerShape(16.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        mapType = if (isSatelliteView) MapType.HYBRID else MapType.NORMAL,
                        isMyLocationEnabled = false
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        compassEnabled = true,
                        myLocationButtonEnabled = false,
                        mapToolbarEnabled = true
                    )
                ) {
                    // Place a dynamic marker for each transport vehicle
                    buses.forEach { bus ->
                        val position = LatLng(bus.currentLatitude, bus.currentLongitude)
                        val markerIcon = if (bus.isLive) {
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        } else {
                            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        }

                        Marker(
                            state = rememberMarkerState(position = position),
                            title = "${bus.vehicleNumber} • ${bus.driverName}",
                            snippet = "${if (bus.isLive) "🟢 Live (${bus.speedKmh} km/h)" else "⚪ Parked"} | ${bus.routeName}",
                            icon = markerIcon,
                            onClick = {
                                selectedRouteId = bus.routeId
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(position, 14.5f),
                                        durationMs = 600
                                    )
                                }
                                false
                            }
                        )
                    }
                }

                // Map Overlay Floating Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Normal / Satellite Map Type Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable { isSatelliteView = !isSatelliteView }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = if (isSatelliteView) Icons.Default.LayersClear else Icons.Default.Layers,
                                contentDescription = "Layer Toggle",
                                tint = RoyalPurple800,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSatelliteView) "Map" else "Satellite",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple800
                            )
                        }
                    }

                    // Recenter Camera Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.92f),
                        shadowElevation = 2.dp,
                        modifier = Modifier.clickable {
                            selectedBus?.let {
                                coroutineScope.launch {
                                    cameraPositionState.animate(
                                        update = CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.currentLatitude, it.currentLongitude),
                                            14f
                                        ),
                                        durationMs = 600
                                    )
                                }
                            }
                        }
                    ) {
                        Box(modifier = Modifier.padding(6.dp)) {
                            Icon(
                                imageVector = Icons.Default.MyLocation,
                                contentDescription = "Recenter",
                                tint = RoyalPurple800,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bottom Left Active Fleet Status Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.72f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(if (selectedBus?.isLive == true) EmeraldGreen else BrilliantGold)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${selectedBus?.vehicleNumber ?: "BUS"} • ${if (selectedBus?.isLive == true) "${selectedBus.speedKmh} km/h" else "Parked"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Vehicle Selector Horizontal Chips
            Text(
                text = if (isHindi) "वाहन चुनें (SELECT VEHICLE)" else "SELECT VEHICLE FLEET",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(buses) { bus ->
                    val isSelected = bus.routeId == selectedRouteId
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedRouteId = bus.routeId
                            coroutineScope.launch {
                                cameraPositionState.animate(
                                    update = CameraUpdateFactory.newLatLngZoom(
                                        LatLng(bus.currentLatitude, bus.currentLongitude),
                                        14.5f
                                    ),
                                    durationMs = 700
                                )
                            }
                        },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (bus.isLive) EmeraldGreen else Color.Gray)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = bus.vehicleNumber,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalPurple800,
                            selectedLabelColor = Color.White,
                            containerColor = RoyalPurpleSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selected Vehicle Detailed Telemetry Card
            selectedBus?.let { bus ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BackgroundCanvas,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = bus.routeName,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${if (isHindi) "चालक" else "Driver"}: ${bus.driverName} (${bus.driverPhone})",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (bus.isLive) EmeraldGreenLight else Color(0xFFF3F4F6)
                            ) {
                                Text(
                                    text = if (bus.isLive) "${bus.speedKmh} km/h" else "PARKED",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (bus.isLive) Color(0xFF065F46) else TextDarkSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Route timeline stops
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Current Stop",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${bus.currentStop}  ➔  ${bus.nextStop}",
                                fontSize = 11.sp,
                                color = TextDarkSecondary,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // ETA and Capacity
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "⏱️ ${if (isHindi) "अनुमानित समय" else "ETA to Next"}: ${bus.etaMinutes} mins",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple800
                            )
                            Text(
                                text = "👥 ${if (isHindi) "सवार छात्र" else "Students"}: ${bus.onboardCount}/${bus.totalCapacity}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Actions: Simulate Movement & Call Driver
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Simulate Live GPS Movement (Pushes directly to Firestore transport collection)
                            OutlinedButton(
                                onClick = { viewModel.simulateTransportMovement(bus.routeId, context) },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1.3f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Simulate",
                                    tint = RoyalPurple800,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "लाइव जीपीएस टेस्ट" else "Simulate GPS Stream",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple800
                                )
                            }

                            // Direct Call Driver Button
                            Button(
                                onClick = {
                                    val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                        data = Uri.parse("tel:${bus.driverPhone.replace(" ", "")}")
                                    }
                                    try {
                                        context.startActivity(dialIntent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "Dialer unavailable", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Call",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "कॉल चालक" else "Call Driver",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Firestore Live Transport Sync Status Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldGreenLight.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
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
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi)
                                "क्लाउड फायरस्टोर /schools/${state.currentSchool.id}/transport से लाइव सिंक सक्रिय"
                            else
                                "Firestore /schools/${state.currentSchool.id}/transport collection active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF065F46),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = if (isHindi) "सिंक करें" else "Sync Now",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = RoyalPurple800,
                        modifier = Modifier
                            .clickable { viewModel.syncAllBusesToFirestore(context) }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Fullscreen Live Transport Radar Dialog for Executive Oversight
 */
@Composable
fun PrincipalTransportRadarDialog(
    state: GmptpUiState,
    viewModel: GmptpViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    val buses = state.buses

    var selectedRouteId by remember { mutableStateOf(buses.firstOrNull()?.routeId ?: "") }
    val selectedBus = buses.find { it.routeId == selectedRouteId } ?: buses.firstOrNull()

    var isSatelliteView by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val initialTarget = remember(buses) {
        val liveBus = buses.firstOrNull { it.isLive } ?: buses.firstOrNull()
        if (liveBus != null) {
            LatLng(liveBus.currentLatitude, liveBus.currentLongitude)
        } else {
            LatLng(28.5672, 77.2100)
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialTarget, 13f)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundCanvas
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Surface(
                    color = RoyalPurple900,
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "वाहन जीपीएस रडार • संपूर्ण दृश्य" else "Live Fleet Radar • Fullscreen Mode",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "${state.currentSchool.nameEn} • ${buses.count { it.isLive }} / ${buses.size} Active Buses",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.15f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Interactive Fullscreen Map
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(
                            mapType = if (isSatelliteView) MapType.HYBRID else MapType.NORMAL,
                            isMyLocationEnabled = false
                        ),
                        uiSettings = MapUiSettings(
                            zoomControlsEnabled = true,
                            compassEnabled = true,
                            myLocationButtonEnabled = false
                        )
                    ) {
                        buses.forEach { bus ->
                            val pos = LatLng(bus.currentLatitude, bus.currentLongitude)
                            val markerColor = if (bus.isLive) {
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            } else {
                                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                            }

                            Marker(
                                state = rememberMarkerState(position = pos),
                                title = "${bus.vehicleNumber} (${bus.driverName})",
                                snippet = "${if (bus.isLive) "🟢 ${bus.speedKmh} km/h" else "⚪ Parked"} - Next: ${bus.nextStop}",
                                icon = markerColor,
                                onClick = {
                                    selectedRouteId = bus.routeId
                                    coroutineScope.launch {
                                        cameraPositionState.animate(
                                            update = CameraUpdateFactory.newLatLngZoom(pos, 15f),
                                            durationMs = 600
                                        )
                                    }
                                    false
                                }
                            )
                        }
                    }

                    // Map Top Floating Action Bar
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 3.dp,
                            modifier = Modifier.clickable { isSatelliteView = !isSatelliteView }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSatelliteView) Icons.Default.LayersClear else Icons.Default.Layers,
                                    contentDescription = "Map Type",
                                    tint = RoyalPurple800,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isSatelliteView) "Normal Map" else "Satellite",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple800
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.95f),
                            shadowElevation = 3.dp,
                            modifier = Modifier.clickable {
                                viewModel.simulateTransportMovement(selectedBus?.routeId, context)
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Simulate",
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "जीपीएस सिमुलेशन" else "Simulate Move",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }
                }

                // Persistent Bottom Fleet Inspector Panel
                Surface(
                    color = CardWhite,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Vehicle Horizontal Selector
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(buses) { bus ->
                                val isSelected = bus.routeId == selectedRouteId
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedRouteId = bus.routeId
                                        coroutineScope.launch {
                                            cameraPositionState.animate(
                                                update = CameraUpdateFactory.newLatLngZoom(
                                                    LatLng(bus.currentLatitude, bus.currentLongitude),
                                                    15f
                                                ),
                                                durationMs = 600
                                            )
                                        }
                                    },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(7.dp)
                                                    .clip(CircleShape)
                                                    .background(if (bus.isLive) EmeraldGreen else Color.Gray)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${bus.vehicleNumber} (${if (bus.isLive) "${bus.speedKmh} km/h" else "Parked"})",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = RoyalPurple800,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        selectedBus?.let { bus ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = bus.routeName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = "Driver: ${bus.driverName} • Contact: ${bus.driverPhone}",
                                        fontSize = 11.sp,
                                        color = TextDarkSecondary
                                    )
                                }

                                Button(
                                    onClick = {
                                        val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                                            data = Uri.parse("tel:${bus.driverPhone.replace(" ", "")}")
                                        }
                                        try {
                                            context.startActivity(dialIntent)
                                        } catch (_: Exception) {
                                            Toast.makeText(context, "Dialer unavailable", Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Call,
                                        contentDescription = "Call",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Call Driver", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Current: ${bus.currentStop}",
                                    fontSize = 11.sp,
                                    color = TextDarkSecondary
                                )
                                Text(
                                    text = "Next: ${bus.nextStop} (ETA: ${bus.etaMinutes}m)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple800
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
