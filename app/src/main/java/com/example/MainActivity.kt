package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.UserRole
import com.example.ui.components.BiometricMpinDialog
import com.example.ui.components.GmptpHeader
import com.example.ui.components.SideDrawerContent
import com.example.ui.screens.*
import com.example.ui.theme.BackgroundCanvas
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AuthenticationStateHolder
import com.example.util.LocalAuthState
import com.example.util.RequireRole
import com.example.util.RoleIsolatedContent
import com.example.util.TenantRoleBadge
import com.example.viewmodel.GmptpViewModel
import kotlinx.coroutines.launch

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Firebase Firestore connection for multi-tenant school sync
        com.example.data.FirebaseMultiTenantService.firestore
        setContent {
            MyApplicationTheme {
                val viewModel: GmptpViewModel = viewModel()
                GmptpApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GmptpApp(viewModel: GmptpViewModel) {
    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // If user is not yet authenticated (Mobile Verification, MPIN Setup, or MPIN Login)
    if (state.authStep != com.example.viewmodel.AuthFlowStep.AUTHENTICATED) {
        AuthGatewayScreen(state = state, viewModel = viewModel)
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp)
            ) {
                SideDrawerContent(
                    userProfile = state.currentUser,
                    currentSchool = state.currentSchool,
                    allSchools = state.allSchools,
                    activeRole = state.activeRole,
                    isHindi = state.isHindi,
                    onSelectScreen = { screen ->
                        viewModel.navigateTo(screen)
                    },
                    onSwitchRole = { newRole ->
                        viewModel.switchRole(newRole)
                    },
                    onSwitchSchool = { schoolId ->
                        viewModel.switchSchool(schoolId)
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        viewModel.lockSession()
                    },
                    onCloseDrawer = {
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundCanvas,
            topBar = {
                val screenTitle = when (state.currentScreen) {
                    "FEES" -> if (state.isHindi) "फीस बहीखाता" else "Fees Ledger"
                    "TEACHER_SALARY" -> if (state.isHindi) "शिक्षक वेतन बहीखाता" else "Teacher Salary Ledger"
                    "DRIVER_SALARY" -> if (state.isHindi) "चालक वेतन बहीखाता" else "Driver Salary Ledger"
                    "REGISTER_STUDENT" -> if (state.isHindi) "छात्र पंजीकरण एवं फीस खाता" else "Student Registration"
                    "GPS" -> if (state.isHindi) "वाहन जीपीएस" else "Live GPS Bus"
                    "ATTENDANCE" -> if (state.isHindi) "उपस्थिति कैलेंडर" else "Attendance Log"
                    "SUVICHAR" -> if (state.isHindi) "AI सुविचार" else "AI Daily Suvichar"
                    "EXAM_SCANNER" -> if (state.isHindi) "AI परीक्षा स्कैन" else "AI Exam Grading"
                    "GALLERY" -> if (state.isHindi) "मीडिया गैलरी" else "School Gallery"
                    "SUPER_ADMIN" -> "GMPTP AI Super Admin"
                    "STAFF_REPLACE" -> if (state.isHindi) "स्टाफ प्रतिस्थापन" else "Staff Replacement"
                    "HELP_BOT" -> if (state.isHindi) "AI सहायता बॉट" else "AI Help Copilot"
                    "REGISTER_PRINCIPAL" -> if (state.isHindi) "प्रधानाचार्य पंजीकरण" else "Principal Register"
                    "SECURITY" -> if (state.isHindi) "सुरक्षा एवं भाषा सेटिंग्स" else "Security & Language"
                    else -> if (state.isHindi) state.activeRole.labelHi else state.activeRole.labelEn
                }

                val subtitle = if (state.isHindi) state.currentSchool.nameHi else state.currentSchool.nameEn
                val isNotHome = state.currentScreen != "DASHBOARD"

                GmptpHeader(
                    title = screenTitle,
                    subtitle = subtitle,
                    isHindi = state.isHindi,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onLanguageToggle = {
                        viewModel.toggleLanguage()
                    },
                    onAlertClick = {
                        // Navigate to fee approvals or alerts
                        viewModel.navigateTo("FEES")
                    },
                    alertCount = state.feeRecords.count { it.status == com.example.data.FeeApprovalStatus.PENDING_VERIFICATION },
                    showBackButton = isNotHome,
                    onBackClick = {
                        viewModel.navigateTo("DASHBOARD")
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val authorizedScreen = AuthenticationStateHolder.resolveAuthorizedScreen(
                    role = state.activeRole,
                    targetRoute = state.currentScreen
                )

                when (authorizedScreen) {
                    "DASHBOARD" -> {
                        RoleIsolatedContent(
                            activeRole = state.activeRole,
                            principalContent = { PrincipalDashboardScreen(state = state, viewModel = viewModel) },
                            teacherContent = { TeacherDashboardScreen(state = state, viewModel = viewModel) },
                            parentStudentContent = { ParentStudentDashboardScreen(state = state, viewModel = viewModel) },
                            driverContent = { DriverDashboardScreen(state = state, viewModel = viewModel) },
                            superAdminContent = { SuperAdminScreen(state = state, viewModel = viewModel) }
                        )
                    }
                    "FEES" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            FeesLedgerScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "TEACHER_SALARY" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN, UserRole.TEACHER),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            TeacherSalaryLedgerScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "DRIVER_SALARY" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN, UserRole.DRIVER),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            DriverSalaryLedgerScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "REGISTER_STUDENT" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            StudentRegistrationScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "GPS" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PARENT_STUDENT, UserRole.DRIVER, UserRole.PRINCIPAL, UserRole.SUPER_ADMIN),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            LiveBusTrackingScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "ATTENDANCE" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN, UserRole.TEACHER),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            AttendanceCalendarScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "SUVICHAR" -> AiSuvicharScreen(state = state, viewModel = viewModel)
                    "EXAM_SCANNER" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN, UserRole.TEACHER),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            AiExamScannerScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "GALLERY" -> SchoolGalleryScreen(state = state, viewModel = viewModel)
                    "SUPER_ADMIN" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.SUPER_ADMIN),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            SuperAdminScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "STAFF_REPLACE" -> {
                        RequireRole(
                            permittedRoles = setOf(UserRole.PRINCIPAL, UserRole.SUPER_ADMIN),
                            activeRole = state.activeRole,
                            isHindi = state.isHindi
                        ) {
                            StaffReplacementScreen(state = state, viewModel = viewModel)
                        }
                    }
                    "HELP_BOT" -> AiHelpBotScreen(state = state, viewModel = viewModel)
                    "REGISTER_PRINCIPAL" -> PrincipalRegistrationScreen(state = state, viewModel = viewModel)
                    "SECURITY" -> SecuritySettingsScreen(state = state, viewModel = viewModel)
                    else -> PrincipalDashboardScreen(state = state, viewModel = viewModel)
                }

                // Global Ad System Overlay Handlers
                if (state.showInterstitialAd) {
                    com.example.ui.components.AdMobInterstitialDialog(state = state, viewModel = viewModel)
                }
                if (state.showRewardedAd) {
                    com.example.ui.components.AdMobRewardedDialog(state = state, viewModel = viewModel)
                }
            }
        }
    }
}
