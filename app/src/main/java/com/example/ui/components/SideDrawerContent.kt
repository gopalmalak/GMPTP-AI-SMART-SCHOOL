package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.School
import com.example.data.UserProfile
import com.example.data.UserRole
import com.example.ui.theme.*
import com.example.util.AuthenticationStateHolder

@Composable
fun SideDrawerContent(
    userProfile: UserProfile,
    currentSchool: School,
    allSchools: List<School>,
    activeRole: UserRole,
    isHindi: Boolean,
    onSelectScreen: (String) -> Unit,
    onSwitchRole: (UserRole) -> Unit,
    onSwitchSchool: (String) -> Unit,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    var isSwitchRoleExpanded by remember { mutableStateOf(false) }
    var isSwitchSchoolExpanded by remember { mutableStateOf(false) }

    var showSchoolProfileDialog by remember { mutableStateOf(false) }
    var showClassAllocationDialog by remember { mutableStateOf(false) }
    var showReportCardDialog by remember { mutableStateOf(false) }
    var showNoticesDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RoyalPurple900)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        // Top Brand Header with strict Icon Spec:
        // "A distinct premium purple square container containing a structured school building facade,
        // a prominent visible clock, and a glowing gold microchip star placed precisely on top, branded explicitly as GMPTP AI"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(RoyalPurple900, RoyalPurple800)
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(RoyalPurple700, RoyalPurple900)
                                )
                            )
                            .border(2.dp, BrilliantGold, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Microchip Star on top
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Gold Star",
                                tint = BrilliantGold,
                                modifier = Modifier.size(16.dp)
                            )
                            // School building facade with clock
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = "School Facade",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "GMPTP AI",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = BrilliantGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = if (isHindi) "बहु-विद्यालय प्रबंधन प्रणाली" else "Multi-Tenant School Core",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Profile Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrilliantGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile.name.take(1).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = RoyalPurple900,
                                fontSize = 18.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = userProfile.name,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isHindi) activeRole.labelHi else activeRole.labelEn,
                                color = BrilliantGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = userProfile.mobile,
                                color = Color.White.copy(alpha = 0.65f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

        // UNIFIED SWITCH DROPDOWNS SECTION:
        // 1. Role Switcher Dropdown
        // 2. Multi-School Switcher Dropdown (Switch to School A / School B under 1 second!)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (isHindi) "त्वरित भूमिका एवं विद्यालय चयन" else "UNIFIED SWITCHER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BrilliantGold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Switch School Dropdown Button
            Surface(
                onClick = { isSwitchSchoolExpanded = !isSwitchSchoolExpanded },
                shape = RoundedCornerShape(10.dp),
                color = RoyalPurple800,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, GoldMetallic)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Apartment,
                            contentDescription = "School",
                            tint = BrilliantGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isHindi) "सक्रिय विद्यालय:" else "Active School:",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (isHindi) currentSchool.nameHi else currentSchool.nameEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isSwitchSchoolExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = BrilliantGold
                    )
                }
            }

            AnimatedVisibility(visible = isSwitchSchoolExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    val accessibleSchools = allSchools.filter { sch ->
                        AuthenticationStateHolder.isTenantMatch(
                            targetTenantSchoolId = sch.id,
                            userTenantId = userProfile.primarySchoolId,
                            userRole = activeRole,
                            linkedTenantIds = userProfile.linkedSchoolIds
                        )
                    }
                    accessibleSchools.forEach { sch ->
                        val isSelected = sch.id == currentSchool.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSwitchSchool(sch.id)
                                    isSwitchSchoolExpanded = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isHindi) sch.nameHi else sch.nameEn,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) BrilliantGold else Color.White
                                )
                                Text(
                                    text = "${sch.code} • ${sch.district}",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = BrilliantGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Switch Role Dropdown Button
            Surface(
                onClick = { isSwitchRoleExpanded = !isSwitchRoleExpanded },
                shape = RoundedCornerShape(10.dp),
                color = RoyalPurple800,
                border = androidx.compose.foundation.BorderStroke(1.2.dp, BrilliantGold.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            tint = BrilliantGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isHindi) "सक्रिय भूमिका:" else "Active Role:",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (isHindi) activeRole.labelHi else activeRole.labelEn,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrilliantGold
                            )
                        }
                    }
                    Icon(
                        imageVector = if (isSwitchRoleExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = BrilliantGold
                    )
                }
            }

            AnimatedVisibility(visible = isSwitchRoleExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = role == activeRole
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSwitchRole(role)
                                    isSwitchRoleExpanded = false
                                }
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) role.labelHi else role.labelEn,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BrilliantGold else Color.White
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = BrilliantGold,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

        // ROLE-ISOLATED NAVIGATION MENU BAR (Strict Separation Per Role Tier)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            when (activeRole) {
                UserRole.SUPER_ADMIN -> {
                    // Super Admin Console - 100% Exclusive
                    DrawerNavItem(
                        icon = Icons.Default.HealthAndSafety,
                        title = if (isHindi) "ऐप स्वास्थ्य एवं सुरक्षा मॉनिटर" else "App Health & Security Monitor",
                        onClick = { onSelectScreen("SUPER_ADMIN"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.VerifiedUser,
                        title = if (isHindi) "प्रधानाचार्य अनुमोदन हब" else "Principal Approvals Hub",
                        onClick = { onSelectScreen("SUPER_ADMIN"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Campaign,
                        title = if (isHindi) "AdMob विज्ञापन प्रबंधक" else "AdMob Ads Manager",
                        onClick = { onSelectScreen("SUPER_ADMIN"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.WorkspacePremium,
                        title = if (isHindi) "सदस्यता योजना कॉन्फ़िगरेटर" else "Subscription Plan Configurator",
                        onClick = { onSelectScreen("SUPER_ADMIN"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Palette,
                        title = if (isHindi) "ग्लोबल थीम प्रबंधक" else "Global Theme Manager",
                        onClick = { onSelectScreen("SUPER_ADMIN"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.CloudSync,
                        title = if (isHindi) "डेटाबेस बैकअप एवं रोलबैक" else "Database Backup & Rollback",
                        onClick = { onSelectScreen("SUPER_ADMIN"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Lock,
                        title = if (isHindi) "लॉगआउट" else "Logout",
                        onClick = { onCloseDrawer(); onLogout() }
                    )
                }
                UserRole.PRINCIPAL -> {
                    // Principal Executive View Only
                    DrawerNavItem(
                        icon = Icons.Default.AccountBalance,
                        title = if (isHindi) "विद्यालय प्रोफ़ाइल प्रबंधन" else "Manage School Profile",
                        onClick = { showSchoolProfileDialog = true }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.GroupAdd,
                        title = if (isHindi) "स्टाफ प्रबंधन (शिक्षक व चालक जोड़ें)" else "Manage Staff (Add Teachers & Drivers)",
                        onClick = { onSelectScreen("REGISTER_STAFF"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.FactCheck,
                        title = if (isHindi) "शिक्षक उपस्थिति रजिस्टर" else "Teacher Attendance Register",
                        onClick = { onSelectScreen("ATTENDANCE"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.ReceiptLong,
                        title = if (isHindi) "मास्टर वित्तीय विवरण" else "Master Financial Statements",
                        onClick = { onSelectScreen("FEES"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Campaign,
                        title = if (isHindi) "AI आपातकालीन घोषणा हब" else "AI Emergency Announcement Hub",
                        onClick = { onSelectScreen("EMERGENCY_BROADCAST"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.AutoAwesome,
                        title = if (isHindi) "दैनिक सुविचार हब" else "Daily Suvichar Hub",
                        onClick = { onSelectScreen("SUVICHAR"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.PhotoLibrary,
                        title = if (isHindi) "विद्यालय मीडिया गैलरी" else "School Media Gallery",
                        onClick = { onSelectScreen("GALLERY"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.SmartToy,
                        title = if (isHindi) "द्विभाषी सहायता चैटबॉट" else "Bilingual Support Chatbot",
                        onClick = { onSelectScreen("HELP_BOT"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Lock,
                        title = if (isHindi) "लॉगआउट" else "Logout",
                        onClick = { onCloseDrawer(); onLogout() }
                    )
                }
                UserRole.TEACHER -> {
                    // Teacher Classroom View Only (Never shows salary adjustments or principal controls)
                    DrawerNavItem(
                        icon = Icons.Default.FactCheck,
                        title = if (isHindi) "छात्र उपस्थिति दर्ज करें" else "Mark Student Attendance",
                        onClick = { onSelectScreen("ATTENDANCE"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.PersonAdd,
                        title = if (isHindi) "नए छात्र जोड़ें / पंजीकृत करें" else "Add/Register New Students",
                        onClick = { onSelectScreen("REGISTER_STUDENT"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Forum,
                        title = if (isHindi) "कक्षा व्हाट्सएप-शैली चैट ग्रुप्स" else "Classroom WhatsApp-Style Chat Groups",
                        onClick = { onSelectScreen("CLASS_CHAT"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.DocumentScanner,
                        title = if (isHindi) "AI परीक्षा ओसीआर स्कैनर" else "AI Exam OCR Scanner",
                        onClick = { onSelectScreen("EXAM_SCANNER"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Assignment,
                        title = if (isHindi) "गृहकार्य / सूचना अपलोडर" else "Homework/Notice Uploader",
                        onClick = { onSelectScreen("HOMEWORK_MANAGER"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.CurrencyRupee,
                        title = if (isHindi) "शुल्क टोकन संग्रह" else "Collect Fee Tokens",
                        onClick = { onSelectScreen("TEACHER_DASHBOARD"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Receipt,
                        title = if (isHindi) "मेरी केवल-पठनीय वेतन पर्ची" else "My Read-Only Salary Slip",
                        onClick = { onSelectScreen("TEACHER_SALARY"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Lock,
                        title = if (isHindi) "लॉगआउट" else "Logout",
                        onClick = { onCloseDrawer(); onLogout() }
                    )
                }
                UserRole.PARENT_STUDENT -> {
                    // Parent / Student Family Portal View Only
                    DrawerNavItem(
                        icon = Icons.Default.Speed,
                        title = if (isHindi) "लाइव छात्र उपस्थिति मीटर" else "Live Student Attendance Meter",
                        onClick = { onSelectScreen("PARENT_DASHBOARD"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Forum,
                        title = if (isHindi) "कक्षा चैट एवं नोट्स समूह पहुंच" else "Class Chat & Notes Group Access",
                        onClick = { onSelectScreen("CLASS_CHAT"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Campaign,
                        title = if (isHindi) "अत्यावश्यक विद्यालय घोषणाएँ" else "Urgent School Announcements",
                        onClick = { showNoticesDialog = true }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = if (isHindi) "फीस बहीखाता एवं UPI भुगतान" else "Fee Ledger & UPI Payment",
                        onClick = { onSelectScreen("PARENT_DASHBOARD"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.DirectionsBus,
                        title = if (isHindi) "लाइव स्कूल बस लोकेशन मैप" else "Live School Bus Location Map",
                        onClick = { onSelectScreen("GPS"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Assessment,
                        title = if (isHindi) "प्रगति रिपोर्ट कार्ड" else "Report Card",
                        onClick = { showReportCardDialog = true }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Notifications,
                        title = if (isHindi) "विद्यालय सूचनाएं" else "School Notices",
                        onClick = { showNoticesDialog = true }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Lock,
                        title = if (isHindi) "लॉगआउट" else "Logout",
                        onClick = { onCloseDrawer(); onLogout() }
                    )
                }
                UserRole.DRIVER -> {
                    // Logistics Portal View Only
                    DrawerNavItem(
                        icon = Icons.Default.PlayCircle,
                        title = if (isHindi) "रूट प्रसारण प्रारंभ / बंद करें" else "Start / Stop Route Broadcast",
                        onClick = { onSelectScreen("DRIVER_DASHBOARD"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Checklist,
                        title = if (isHindi) "आवंटित यात्री चेकलिस्ट" else "Assigned Passenger Checklist",
                        onClick = { onSelectScreen("DRIVER_DASHBOARD"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.DirectionsBus,
                        title = if (isHindi) "मेरे मार्ग का विवरण" else "My Route Details",
                        onClick = { onSelectScreen("GPS"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Receipt,
                        title = if (isHindi) "मेरी केवल-पठनीय वेतन पर्ची (लॉक दृश्य)" else "My Read-Only Salary Slip (Lock View)",
                        onClick = { onSelectScreen("DRIVER_SALARY"); onCloseDrawer() }
                    )
                    DrawerNavItem(
                        icon = Icons.Default.Lock,
                        title = if (isHindi) "लॉगआउट" else "Logout",
                        onClick = { onCloseDrawer(); onLogout() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)

        // Logout / Lock Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = CrimsonRed,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isHindi) "सुरक्षित लॉगआउट (MPIN द्वारा लॉक)" else "Lock Session / MPIN Logout",
                color = CrimsonRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }

    // Role-Specific Popups / Dialogs
    if (showSchoolProfileDialog) {
        AlertDialog(
            onDismissRequest = { showSchoolProfileDialog = false },
            title = {
                Text(
                    text = if (isHindi) "विद्यालय प्रोफ़ाइल" else "School Profile Details",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = if (isHindi) currentSchool.nameHi else currentSchool.nameEn, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "UDISE / Affiliation Code: ${currentSchool.code}", fontSize = 13.sp, color = TextDarkSecondary)
                    Text(text = "Principal: ${currentSchool.principalName}", fontSize = 13.sp, color = TextDarkSecondary)
                    Text(text = "District & State: ${currentSchool.district}, ${currentSchool.state}", fontSize = 13.sp, color = TextDarkSecondary)
                    Text(text = "Total Capacity: ${currentSchool.totalStudents} Students • ${currentSchool.totalTeachers} Teachers", fontSize = 13.sp, color = TextDarkSecondary)
                    Text(text = "RTE Students Enrolled: ${currentSchool.rteStudentsCount}", fontSize = 13.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
            },
            confirmButton = {
                TextButton(onClick = { showSchoolProfileDialog = false }) {
                    Text("OK", color = RoyalPurple700, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showClassAllocationDialog) {
        AlertDialog(
            onDismissRequest = { showClassAllocationDialog = false },
            title = {
                Text(
                    text = if (isHindi) "कक्षा एवं विषय आवंटन" else "My Class Allocation",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = RoyalPurple50,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Class Teacher: Class 9 - Section A", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalPurple900)
                            Text("Subject: Science & Mathematics", fontSize = 12.sp, color = TextDarkSecondary)
                            Text("Room No: 204 • Academic Shift: Morning", fontSize = 12.sp, color = TextDarkSecondary)
                        }
                    }
                    Surface(
                        color = RoyalPurple50,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Secondary: Class 10 - Section B", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = RoyalPurple900)
                            Text("Subject: Physics Lab Practical", fontSize = 12.sp, color = TextDarkSecondary)
                            Text("Room No: Lab 1 • Days: Mon, Wed, Fri", fontSize = 12.sp, color = TextDarkSecondary)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showClassAllocationDialog = false }) {
                    Text("OK", color = RoyalPurple700, fontWeight = FontWeight.Bold)
                }
            }
        )
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
                    Text(text = "Overall Grade: A+ (94.2%)", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = EmeraldGreen)
                    Text(text = "Mathematics: 96/100 • Science: 92/100", fontSize = 13.sp)
                    Text(text = "Social Science: 90/100 • English: 95/100", fontSize = 13.sp)
                    Text(text = "Attendance Record: 95% (Eligible for Distinction)", fontSize = 12.sp, color = TextDarkSecondary)
                    Text(text = "Class Teacher Remarks: Outstanding analytical problem solving and discipline.", fontSize = 12.sp, color = RoyalPurple800, fontWeight = FontWeight.Medium)
                }
            },
            confirmButton = {
                TextButton(onClick = { showReportCardDialog = false }) {
                    Text("CLOSE", color = RoyalPurple700, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showNoticesDialog) {
        AlertDialog(
            onDismissRequest = { showNoticesDialog = false },
            title = {
                Text(
                    text = if (isHindi) "विद्यालय आधिकारिक सूचनाएं" else "Official School Notices",
                    fontWeight = FontWeight.Bold,
                    color = RoyalPurple900
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        color = BrilliantGold.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrilliantGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🚨 EMERGENCY NOTICE: Heavy Rain Alert", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CrimsonRed)
                            Text("District Magistrate weather forecast: School campus will remain closed on Monday, 7th September.", fontSize = 12.sp)
                            Text("Dispatched via WhatsApp API • Today", fontSize = 10.sp, color = TextDarkSecondary)
                        }
                    }
                    Surface(
                        color = RoyalPurple50,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Parent-Teacher Meeting (PTM)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = RoyalPurple900)
                            Text("Quarterly PTM scheduled for Saturday, 9:00 AM to 1:00 PM.", fontSize = 12.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNoticesDialog = false }) {
                    Text("OK", color = RoyalPurple700, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
private fun DrawerNavItem(
    icon: ImageVector,
    title: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        badge?.let {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = BrilliantGold
            ) {
                Text(
                    text = it,
                    color = RoyalPurple900,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
