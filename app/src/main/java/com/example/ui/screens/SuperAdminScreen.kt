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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun SuperAdminScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    var couponInput by remember { mutableStateOf(state.superAdminConfig.activeCouponCode) }
    var dynamicPromptInput by remember { mutableStateOf("Add Inter-School Sports Day Live Medal Tally") }

    var monthlyRateInput by remember { mutableStateOf(state.superAdminConfig.monthlyRate.toString()) }
    var sixMonthRateInput by remember { mutableStateOf(state.superAdminConfig.sixMonthRate.toString()) }
    var yearlyRateInput by remember { mutableStateOf(state.superAdminConfig.yearlyRate.toString()) }

    var admobAppIdInput by remember(state.superAdminConfig.admobAppId) { mutableStateOf(state.superAdminConfig.admobAppId) }
    var admobBannerInput by remember(state.superAdminConfig.admobBannerUnitId) { mutableStateOf(state.superAdminConfig.admobBannerUnitId) }
    var admobInterstitialInput by remember(state.superAdminConfig.admobInterstitialUnitId) { mutableStateOf(state.superAdminConfig.admobInterstitialUnitId) }
    var admobRewardedInput by remember(state.superAdminConfig.admobRewardedUnitId) { mutableStateOf(state.superAdminConfig.admobRewardedUnitId) }

    var showRollbackConfirmation by remember { mutableStateOf(false) }

    if (showRollbackConfirmation) {
        AlertDialog(
            onDismissRequest = { showRollbackConfirmation = false },
            title = {
                Text(
                    text = if (isHindi) "क्लाउड डेटा रोलबैक की पुष्टि" else "Confirm Disaster Rollback",
                    fontWeight = FontWeight.Bold,
                    color = CrimsonRed
                )
            },
            text = {
                Text(
                    text = if (isHindi)
                        "क्या आप वास्तव में अंतिम सुरक्षित क्लाउड स्नैपशॉट पर रोलबैक करना चाहते हैं? सभी विद्यालय एवं छात्र डेटा पूर्व स्थिति में पुनर्स्थापित हो जाएंगे।"
                    else
                        "Are you sure you want to perform a full system rollback to the last verified cloud snapshot? This will restore all schools, student records, and fee ledgers."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showRollbackConfirmation = false
                        viewModel.triggerDataRollback(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text(if (isHindi) "हाँ, रोलबैक करें" else "Proceed Rollback", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showRollbackConfirmation = false }) {
                    Text(if (isHindi) "रद्द करें" else "Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Super Admin Branded Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(RoyalPurple900, Color(0xFF1E1B4B), RoyalPurple800)
                        )
                    )
                    .border(1.5.dp, BrilliantGold, RoundedCornerShape(20.dp))
                    .padding(20.dp)
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
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrilliantGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Super Admin",
                                    tint = RoyalPurple900,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "GMPTP AI Super Admin",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrilliantGold
                                )
                                Text(
                                    text = if (isHindi) "केंद्रीय विद्यालय नेटवर्क नियंत्रक" else "Multi-Tenant Centralized Console",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CrimsonRed
                        ) {
                            Text(
                                text = "MASTER ROOT",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Schools Under Management: ${state.allSchools.size} Active Campuses",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // =========================================================================
        // SUPER ADMIN EXCLUSIVE QUICK-ACTION GRID CARDS (Mirroring all 7 Side Menu Options)
        // =========================================================================
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (isHindi) "सुपर एडमिन कंसोल त्वरित क्रियाएँ" else "SUPER ADMIN CONSOLE QUICK ACTIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkSecondary,
                    letterSpacing = 0.6.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Row 1: App Health & Security Monitor & Principal Approvals Hub
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("SUPER_ADMIN") }
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(EmeraldGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.HealthAndSafety, contentDescription = "Health", tint = EmeraldGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isHindi) "ऐप स्वास्थ्य एवं सुरक्षा मॉनिटर" else "App Health & Security Monitor", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "99.98% सर्वर अपटाइम" else "99.98% Cloud Uptime", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("SUPER_ADMIN") }
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(RoyalPurple700.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.VerifiedUser, contentDescription = "Approvals", tint = RoyalPurple700, modifier = Modifier.size(20.dp))
                                }
                                if (state.pendingApprovals.isNotEmpty()) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = CrimsonRed
                                    ) {
                                        Text("${state.pendingApprovals.size}", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isHindi) "प्रधानाचार्य अनुमोदन हब" else "Principal Approvals Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "लंबित प्रोफाइल समीक्षा" else "Pending Review Queue", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: AdMob Ads Manager & Subscription Plan Configurator
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.toggleAdMobAds() }
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AmberGlow.copy(alpha = 0.12f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Campaign, contentDescription = "Ads", tint = AmberGlow, modifier = Modifier.size(20.dp))
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (state.isAdMobGloballyEnabled) EmeraldGreen.copy(alpha = 0.15f) else CrimsonRed.copy(alpha = 0.15f)
                                ) {
                                    Text(if (state.isAdMobGloballyEnabled) "ON" else "OFF", color = if (state.isAdMobGloballyEnabled) EmeraldGreen else CrimsonRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isHindi) "AdMob विज्ञापन प्रबंधक" else "AdMob Ads Manager", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "ग्लोबल विज्ञापन नियंत्रण" else "Global Monetization Switch", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("SUPER_ADMIN") }
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrilliantGoldDark.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = "Plans", tint = BrilliantGoldDark, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isHindi) "सदस्यता योजना कॉन्फ़िगरेटर" else "Subscription Plan Configurator", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "कूपन व मूल्य निर्धारण" else "Pricing & Discount Coupons", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 3: Global Theme Manager & Database Backup & Rollback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.navigateTo("SUPER_ADMIN") }
                            .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricBlue.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = "Theme", tint = ElectricBlue, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isHindi) "ग्लोबल थीम प्रबंधक" else "Global Theme Manager", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "रॉयल पर्पल व गोल्ड पैलेट" else "Royal Purple & Gold Canvas", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showRollbackConfirmation = true }
                            .border(1.dp, CrimsonRed.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CrimsonRed.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = "Rollback", tint = CrimsonRed, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = if (isHindi) "डेटाबेस बैकअप एवं रोलबैक" else "Database Backup & Rollback", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CrimsonRed)
                            Text(text = if (isHindi) "1-क्लिक क्लाउड डिजास्टर रिकवरी" else "1-Click Cloud Recovery", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 4: Emergency Profile Override Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.navigateTo("STAFF_REPLACE") }
                        .border(1.dp, AmberGlow.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AmberGlow.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SupervisorAccount, contentDescription = "Emergency Override", tint = AmberGlow, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = if (isHindi) "आपातकालीन प्रोफ़ाइल ओवरराइड" else "Emergency Profile Override", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "अनुत्तरदायी स्टाफ खातों को तुरंत बदलें एवं प्रबंधित करें" else "Bypass school boundaries to replace or modify unresponsive staff", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 5: Logout Action Card (Full Width)
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CardWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.logout() }
                        .border(1.dp, BorderLight, RoundedCornerShape(14.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(TextDarkSecondary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lock, contentDescription = "Logout", tint = TextDarkSecondary, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = if (isHindi) "सुरक्षित लॉगआउट" else "Logout", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDarkPrimary)
                            Text(text = if (isHindi) "सत्र समाप्त करें एवं मास्टर लॉक करें" else "Securely lock master console & return to gateway", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                }
            }
        }

        // Live System Health Monitor, Server Uptime & Database Analytics
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.Speed,
                                contentDescription = "Health",
                                tint = EmeraldGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "लाइव सिस्टम स्वास्थ्य एवं सर्वर अपटाइम" else "LIVE SYSTEM HEALTH & SERVER UPTIME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldGreen.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "99.98% UPTIME",
                                color = EmeraldGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Metric 1: Cloud Sync
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Firebase Cloud",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "Connected",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = "Latency: 38ms",
                                    fontSize = 9.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }

                        // Metric 2: Memory & Leaks
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "C++/Java Memory",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "${state.memoryUsageMb} MB",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RoyalPurple800
                                )
                                Text(
                                    text = "0 Leaks • Stable",
                                    fontSize = 9.sp,
                                    color = EmeraldGreen
                                )
                            }
                        }

                        // Metric 3: Active Tenants
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Active Sockets",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "1,420",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberGlow
                                )
                                Text(
                                    text = "Realtime Sync",
                                    fontSize = 9.sp,
                                    color = TextDarkSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Principal Registration Approvals Queue
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
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
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = "Approvals",
                                tint = RoyalPurple700,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "प्रधानाचार्य पंजीकरण अनुमोदन कतार" else "PRINCIPAL REGISTRATION APPROVALS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (state.pendingApprovals.isNotEmpty()) CrimsonRed else EmeraldGreen
                        ) {
                            Text(
                                text = "${state.pendingApprovals.size} PENDING",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (state.pendingApprovals.isEmpty()) {
                        Text(
                            text = if (isHindi) "सभी आवेदन अनुमोदित हैं। कोई नया अनुरोध लंबित नहीं है।" else "All submissions processed. No pending principal approvals.",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        state.pendingApprovals.forEach { pending ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = pending.schoolNameEn,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextDarkPrimary
                                        )
                                        Text(
                                            text = pending.submissionDate,
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Principal: ${pending.principalName} • ${pending.mobile}",
                                        fontSize = 11.sp,
                                        color = TextDarkSecondary
                                    )
                                    Text(
                                        text = "Location: ${pending.district}, ${pending.state} • Code: ${pending.schoolCode}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = { viewModel.approvePrincipalRegistration(pending.id, context) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Approve",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isHindi) "स्वीकृत करें" else "Approve & Trial", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { viewModel.rejectPrincipalRegistration(pending.id, context) },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Reject",
                                                tint = CrimsonRed,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isHindi) "अस्वीकार" else "Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Global Theme Customizer
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Theme",
                            tint = RoyalPurple700,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "ग्लोबल थीम एवं पैलेट कस्टमाइज़र" else "GLOBAL THEME & PALETTE CUSTOMIZER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            letterSpacing = 0.6.sp
                        )
                    }

                    Text(
                        text = if (isHindi) "सभी क्लाइंट ऐप्स पर तत्काल प्रभाव से थीम व एक्सेंट रंग बदलें" else "Change theme templates & accent colors globally across all school installations",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val themes = listOf(
                        Triple("ROYAL_PURPLE_GOLD", "Royal Purple & Gold", Color(0xFF581C87)),
                        Triple("DEEP_NAVY_AMBER", "Deep Navy & Amber", Color(0xFF0F172A)),
                        Triple("IMPERIAL_EMERALD", "Imperial Emerald", Color(0xFF065F46)),
                        Triple("RUBY_SUNSET", "Ruby Sunset", Color(0xFF991B1B))
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        themes.forEach { (key, label, color) ->
                            val isSelected = state.superAdminConfig.activeThemeTemplate == key
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) color else Color(0xFFF1F5F9),
                                border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrilliantGold) else null,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setGlobalThemeTemplate(key, context) }
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label.split(" ").first(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else TextDarkPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 14-Day Free Trial Tracker Across All Schools
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "14-दिवसीय निःशुल्क ट्रायल ट्रैकर" else "14-DAY FREE TRIAL TRACKER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    state.allSchools.forEach { sch ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF8FAFC))
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = sch.nameEn,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = "Code: ${sch.code} • ${sch.district}",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (sch.hasPaidSubscription)
                                    EmeraldGreen.copy(alpha = 0.15f)
                                else if (sch.trialDaysRemaining > 0)
                                    RoyalPurpleLight
                                else
                                    CrimsonRed.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (sch.hasPaidSubscription) "Paid Subscriber" else "${sch.trialDaysRemaining}d Trial Left",
                                    color = if (sch.hasPaidSubscription) EmeraldGreen else if (sch.trialDaysRemaining > 0) RoyalPurple700 else CrimsonRed,
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

        // Global Module & UI Toggles
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "डायनेमिक विकल्प प्रबंधक (मॉड्यूल नियंत्रण)" else "GLOBAL MODULE & UI TOGGLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    state.superAdminConfig.dynamicModules.entries.forEach { entry ->
                        val moduleKey = entry.key
                        val isEnabled = entry.value
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = moduleKey.replace("_", " ").uppercase(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = if (isEnabled) "Active across client apps" else "Disabled by Super Admin",
                                    fontSize = 10.sp,
                                    color = if (isEnabled) EmeraldGreen else TextMuted
                                )
                            }

                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { viewModel.toggleModule(moduleKey, context) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = RoyalPurple700
                                )
                            )
                        }
                    }
                }
            }
        }

        // Hybrid AdMob Management Console
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Campaign,
                                contentDescription = "Ads",
                                tint = AmberGlow,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "हाइब्रिड AdMob विज्ञापन एवं राजस्व प्रणाली" else "HYBRID ADMOB & REVENUE SYSTEM",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextDarkPrimary,
                                    letterSpacing = 0.6.sp
                                )
                                Text(
                                    text = "Google Mobile Ads SDK • Smart Monetization Engine",
                                    fontSize = 10.sp,
                                    color = TextMuted
                                )
                            }
                        }

                        Switch(
                            checked = state.superAdminConfig.isAdMobEnabledGlobally,
                            onCheckedChange = { viewModel.toggleAdMobType("GLOBAL", it, context) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AmberGlow
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real-Time Ad Performance Metrics HUD
                    Surface(
                        shape = RoundedCornerShape(12.dp),
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
                                Text(
                                    text = "TODAY'S AD PERFORMANCE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary,
                                    letterSpacing = 0.5.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (state.superAdminConfig.adTestMode) AmberGlow.copy(alpha = 0.15f) else EmeraldGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (state.superAdminConfig.adTestMode) "TEST MODE ACTIVE" else "LIVE PRODUCTION",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.superAdminConfig.adTestMode) AmberGlow else EmeraldGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Impressions", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = String.format("%,d", state.superAdminConfig.adImpressionsToday),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                }
                                Column {
                                    Text("Clicks", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = String.format("%,d", state.superAdminConfig.adClicksToday),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = RoyalPurple700
                                    )
                                }
                                Column {
                                    Text("eCPM", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = "₹${state.superAdminConfig.adEcpmInr}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                }
                                Column {
                                    Text("Est. Revenue", fontSize = 10.sp, color = TextMuted)
                                    Text(
                                        text = String.format("₹%.2f", state.superAdminConfig.adEstimatedRevenueTodayInr),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldGreen
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "AD FORMATS CONTROLS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Banner Ads (Dashboard Footer)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
                            Text("Standard 320x50 educational sponsor creative", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = state.superAdminConfig.adBannerEnabled,
                            onCheckedChange = { viewModel.toggleAdMobType("BANNER", it, context) },
                            enabled = state.superAdminConfig.isAdMobEnabledGlobally
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Interstitial Ads (Full-screen Overlay)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
                            Text("Timed full-screen takeover with skip timer", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = state.superAdminConfig.adInterstitialEnabled,
                            onCheckedChange = { viewModel.toggleAdMobType("INTERSTITIAL", it, context) },
                            enabled = state.superAdminConfig.isAdMobEnabledGlobally
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Rewarded Video Ads (Value Exchange)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
                            Text("15-second sponsor video to unlock HD Suvichar & RTE reports", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = state.superAdminConfig.adRewardedEnabled,
                            onCheckedChange = { viewModel.toggleAdMobType("REWARDED", it, context) },
                            enabled = state.superAdminConfig.isAdMobEnabledGlobally
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Native Advanced Ads", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
                            Text("In-feed cards matched to app typography & layout", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = state.superAdminConfig.adNativeAdvancedEnabled,
                            onCheckedChange = { viewModel.toggleAdMobType("NATIVE", it, context) },
                            enabled = state.superAdminConfig.isAdMobEnabledGlobally
                        )
                    }

                    Divider(color = Color(0xFFF1F5F9), thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Google AdMob Test Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
                            Text("Use official sample ad unit IDs to prevent policy strikes", fontSize = 10.sp, color = TextMuted)
                        }
                        Switch(
                            checked = state.superAdminConfig.adTestMode,
                            onCheckedChange = { viewModel.toggleAdMobType("TEST_MODE", it, context) }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Interactive Testing Controls
                    Text(
                        text = "LIVE AD UNIT TEST BENCH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.triggerInterstitialAd(force = true)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Test Interstitial", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Interstitial", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.triggerRewardedAd("HD Watermark-Free Export")
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayCircle, contentDescription = "Test Rewarded", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Rewarded", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // AdMob Credentials & Unit IDs Editor
                    Text(
                        text = "ADMOB APP & UNIT ID CONFIGURATION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = admobAppIdInput,
                        onValueChange = { admobAppIdInput = it },
                        label = { Text("AdMob App ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = admobBannerInput,
                        onValueChange = { admobBannerInput = it },
                        label = { Text("Banner Ad Unit ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = admobInterstitialInput,
                        onValueChange = { admobInterstitialInput = it },
                        label = { Text("Interstitial Ad Unit ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = admobRewardedInput,
                        onValueChange = { admobRewardedInput = it },
                        label = { Text("Rewarded Video Ad Unit ID", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.resetAdStatistics(context) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reset Metrics", fontSize = 11.sp, color = CrimsonRed)
                        }

                        Button(
                            onClick = {
                                viewModel.updateAdMobCredentials(
                                    appId = admobAppIdInput,
                                    bannerId = admobBannerInput,
                                    interstitialId = admobInterstitialInput,
                                    rewardedId = admobRewardedInput,
                                    testMode = state.superAdminConfig.adTestMode,
                                    context = context
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple700)
                        ) {
                            Text("Save IDs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audience Exemption Policy Matrix Card
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "STRICT AUDIENCE AD POLICY MATRIX",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("• Principal (14-Day Free Trial): 100% Zero Ads Guarantee", fontSize = 10.sp, color = TextDarkPrimary)
                            Text("• Principal (Paid Active Subscription): 100% Zero Ads Guarantee", fontSize = 10.sp, color = TextDarkPrimary)
                            Text("• Principal (Unpaid Post-Trial): Enforced Upgrade Banner & Interstitials", fontSize = 10.sp, color = CrimsonRed)
                            Text("• Teachers, Parents, Drivers, Students: Freemium Educational Sponsor Ads", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                }
            }
        }

        // Subscription Engine & Rate Configuration
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "सदस्यता दरें एवं कूपन प्रबंधक" else "SUBSCRIPTION RATES & COUPONS ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = monthlyRateInput,
                            onValueChange = { monthlyRateInput = it },
                            label = { Text("1 Mo (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = sixMonthRateInput,
                            onValueChange = { sixMonthRateInput = it },
                            label = { Text("6 Mo (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = yearlyRateInput,
                            onValueChange = { yearlyRateInput = it },
                            label = { Text("1 Yr (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val m = monthlyRateInput.toLongOrNull() ?: 999L
                            val s = sixMonthRateInput.toLongOrNull() ?: 4999L
                            val y = yearlyRateInput.toLongOrNull() ?: 8999L
                            viewModel.updateSubscriptionRates(m, s, y, context)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (isHindi) "सदस्यता दरें अपडेट करें" else "Save Subscription Rates", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = couponInput,
                            onValueChange = { couponInput = it },
                            label = { Text("Promo Coupon Code") },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = { viewModel.applySubscriptionCoupon(couponInput, context) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Text("Issue", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Backup & Disaster Recovery + Memory Engine
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "क्लाउड बैकअप एवं आपदा प्रबंधन (डिजास्टर रिकवरी)" else "BACKUP & DISASTER RECOVERY ENGINE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerCloudBackup(context) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = "Backup",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "क्लाउड स्नैपशॉट" else "Cloud Snapshot",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showRollbackConfirmation = true },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Rollback",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "डेटा रोलबैक" else "Data Rollback",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.runMemoryOptimization(context) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple700),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = "Memory",
                            tint = BrilliantGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "C++ / Java नेटिव मेमोरी क्लीनर चलाएं" else "Run Native C++/Java Memory Optimizer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Emergency Profile Override Shortcut
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "आपातकालीन प्रोफ़ाइल ओवरराइड एवं कर्मचारी प्रतिस्थापन" else "EMERGENCY PROFILE OVERRIDE & STAFF SWAP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed,
                        letterSpacing = 0.6.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isHindi)
                            "अनुत्तरदायी प्रधानाचार्य या शिक्षक को तुरंत बदलने तथा पुराने नंबर का एक्सेस समाप्त करने हेतु ओवरराइड कंसोल खोलें।"
                        else
                            "Instantly reassign or replace unresponsive Principals or staff directly from the master console.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.navigateTo("STAFF_REPLACE") },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupervisorAccount,
                            contentDescription = "Override",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "आपातकालीन प्रतिस्थापन कंसोल खोलें" else "Open Emergency Staff Override",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Dynamic Feature Adder (AI Prompt Utility)
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = BrilliantGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "डायनेमिक AI फीचर जनरेटर" else "DYNAMIC FEATURE ADDER (AI UTILITY)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            letterSpacing = 0.6.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isHindi) "सिस्टम में नया मॉड्यूल या फीचर जोड़ने हेतु निर्देश लिखें:" else "Type prompt to scaffold dynamic module without redeployment:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dynamicPromptInput,
                        onValueChange = { dynamicPromptInput = it },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.generateDynamicFeature(dynamicPromptInput, context) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isHindi) "AI द्वारा मॉड्यूल तैयार करें" else "Compile & Inject Dynamic Module",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
