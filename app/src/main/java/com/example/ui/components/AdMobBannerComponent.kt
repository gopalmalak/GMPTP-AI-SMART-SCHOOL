package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel
import kotlinx.coroutines.delay

data class EducationalAdCreative(
    val title: String,
    val subtitle: String,
    val tag: String,
    val rating: String,
    val installs: String,
    val icon: ImageVector,
    val gradientColors: List<Color>,
    val ctaText: String
)

val sampleEducationalAds = listOf(
    EducationalAdCreative(
        title = "EduTech STEM & AI Lab Equipment",
        subtitle = "Certified robotics, physics & chemistry kits for CBSE/ICSE schools.",
        tag = "CBSE Partner",
        rating = "4.9 ★",
        installs = "500K+",
        icon = Icons.Default.Science,
        gradientColors = listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8)),
        ctaText = "Order Kits"
    ),
    EducationalAdCreative(
        title = "NCERT 3D Digital Learning Series",
        subtitle = "Interactive audio-visual smart board curriculum for Class 1 to 12.",
        tag = "Govt Approved",
        rating = "4.8 ★",
        installs = "2M+",
        icon = Icons.Default.MenuBook,
        gradientColors = listOf(Color(0xFF10B981), Color(0xFF047857)),
        ctaText = "Free Trial"
    ),
    EducationalAdCreative(
        title = "Smart RFID School Bus GPS Tracker",
        subtitle = "Automated parent SMS alerts, speed monitoring & live route telemetry.",
        tag = "AIS 140 GPS",
        rating = "4.9 ★",
        installs = "100K+",
        icon = Icons.Default.DirectionsBus,
        gradientColors = listOf(Color(0xFFF59E0B), Color(0xFFD97706)),
        ctaText = "Get Quote"
    ),
    EducationalAdCreative(
        title = "National Olympiad & Scholarship 2026",
        subtitle = "Register your school for the All-India Math & Science Championship.",
        tag = "Scholarship",
        rating = "5.0 ★",
        installs = "800K+",
        icon = Icons.Default.EmojiEvents,
        gradientColors = listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9)),
        ctaText = "Enroll Now"
    )
)

/**
 * Primary Responsive AdMob Banner Component
 * Handles rule-based visibility (14-day free trial protection, paid subscription exemption).
 */
@Composable
fun AdMobBannerComponent(
    userRole: UserRole,
    trialDaysRemaining: Int,
    hasPaidSubscription: Boolean,
    viewModel: GmptpViewModel? = null,
    onUpgradeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val state = viewModel?.uiState?.collectAsState()?.value

    // AdMob Master & Banner switches from Super Admin
    val isGlobalEnabled = state?.superAdminConfig?.isAdMobEnabledGlobally ?: true
    val isBannerEnabled = state?.superAdminConfig?.adBannerEnabled ?: true

    if (!isGlobalEnabled || !isBannerEnabled) return

    // Evaluation according to strict specification:
    // 1. Non-paying users (Teachers, Students, Parents, Drivers, Staff): ALWAYS visible.
    // 2. Principal:
    //    - If during 14-day trial: completely hidden!
    //    - If paid subscription: completely hidden!
    //    - If post-trial & unpaid: enforced aggressive ads!
    val shouldShowAd = when (userRole) {
        UserRole.PRINCIPAL -> {
            !hasPaidSubscription && trialDaysRemaining <= 0
        }
        UserRole.SUPER_ADMIN -> false
        else -> true // Teacher, Parent, Student, Driver always see educational ads
    }

    if (!shouldShowAd) return

    val creativeIndex = (state?.activeAdCreativeIndex ?: 0) % sampleEducationalAds.size
    val creative = sampleEducationalAds[creativeIndex]

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, BorderLight, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(
                            text = "Ad",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkSecondary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (state?.superAdminConfig?.adTestMode == true) "AdMob Test Ad • ${creative.tag}" else "Google AdMob • ${creative.tag}",
                        fontSize = 10.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (userRole == UserRole.PRINCIPAL && !hasPaidSubscription) {
                    Text(
                        text = "Remove Ads (Upgrade)",
                        color = RoyalPurple600,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onUpgradeClick() }
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(creative.rating, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AmberGlow)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ad Info",
                            tint = TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(creative.gradientColors)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = creative.icon,
                        contentDescription = "Ad Icon",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = creative.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = creative.subtitle,
                        fontSize = 11.sp,
                        color = TextDarkSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalPurple700,
                    modifier = Modifier.clickable {
                        viewModel?.recordAdClick("BANNER", context)
                    }
                ) {
                    Text(
                        text = creative.ctaText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

/**
 * Full-screen Interactive Interstitial Ad Dialog
 * Emulates Google AdMob Interstitial display with realistic 5-second countdown timer and skip control.
 */
@Composable
fun AdMobInterstitialDialog(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    var secondsRemaining by remember { mutableStateOf(5) }
    var canSkip by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining--
        }
        canSkip = true
    }

    val creativeIndex = state.activeAdCreativeIndex % sampleEducationalAds.size
    val creative = sampleEducationalAds[creativeIndex]

    Dialog(
        onDismissRequest = {
            if (canSkip) viewModel.dismissInterstitialAd()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.92f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar: Ad Indicator & Skip Timer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "AdMob Interstitial • ${if (state.superAdminConfig.adTestMode) "TEST MODE" else "LIVE"}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        if (canSkip) {
                            Button(
                                onClick = { viewModel.dismissInterstitialAd() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (state.isHindi) "छोड़ें ⏩" else "Skip Ad ⏩",
                                    color = TextDarkPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFF8FAFC)
                            ) {
                                Text(
                                    text = if (state.isHindi) "${secondsRemaining}s में बंद होगा" else "Reward / Close in ${secondsRemaining}s",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = TextMuted,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hero Graphic Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(creative.gradientColors)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = creative.icon,
                                    contentDescription = "Hero Ad",
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = creative.tag.uppercase(),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrilliantGold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = creative.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = creative.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDarkPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = creative.subtitle,
                        fontSize = 12.sp,
                        color = TextDarkSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rating: ${creative.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AmberGlow)
                        Text("Downloads: ${creative.installs}", fontSize = 11.sp, color = TextMuted)
                        Text("Verified Edu Publisher", fontSize = 11.sp, color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.recordAdClick("INTERSTITIAL", context)
                            viewModel.dismissInterstitialAd()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${creative.ctaText} / अभी प्राप्त करें",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Rewarded Video Ad Simulation Dialog
 * Requires watching a 15-second simulation to unlock premium features (e.g. Suvichar HD watermark-free export).
 */
@Composable
fun AdMobRewardedDialog(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    var progressSeconds by remember { mutableStateOf(0) }
    val totalDuration = 15

    LaunchedEffect(Unit) {
        while (progressSeconds < totalDuration) {
            delay(1000L)
            progressSeconds++
        }
    }

    val isCompleted = progressSeconds >= totalDuration

    Dialog(
        onDismissRequest = {
            if (isCompleted) viewModel.dismissRewardedAd()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = AmberGlow.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "AdMob Rewarded Video",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGlow,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = if (isCompleted) "Reward Ready! ✓" else "Reward in: ${totalDuration - progressSeconds}s",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCompleted) EmeraldGreen else TextDarkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Video Player Mock Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayCircle,
                                contentDescription = "Video",
                                tint = if (isCompleted) EmeraldGreen else BrilliantGold,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isCompleted) "SPONSOR VIDEO COMPLETED" else "Playing Sponsor Video...",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Unlocks: ${state.rewardedFeatureTitle}",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (progressSeconds.toFloat() / totalDuration).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = EmeraldGreen,
                        trackColor = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isCompleted) "Congratulations! Reward is unlocked." else "Keep watching to unlock ${state.rewardedFeatureTitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isCompleted) EmeraldGreen else TextDarkSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (!isCompleted) {
                            OutlinedButton(
                                onClick = { viewModel.dismissRewardedAd() },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Cancel (No Reward)", fontSize = 11.sp, color = CrimsonRed)
                            }
                        }

                        Button(
                            onClick = {
                                if (isCompleted) {
                                    viewModel.completeRewardedAd(context)
                                } else {
                                    viewModel.recordAdClick("REWARDED", context)
                                }
                            },
                            enabled = isCompleted,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen)
                        ) {
                            Text(
                                text = if (isCompleted) "Claim Reward / पुरस्कार लें" else "Watching...",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
