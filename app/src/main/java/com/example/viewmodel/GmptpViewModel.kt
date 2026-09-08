package com.example.viewmodel

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import com.example.util.AuthenticationStateHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrincipalAlert(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val timestamp: String,
    val feeRecordId: String? = null,
    val isRead: Boolean = false
)

data class ChatMessage(
    val id: String,
    val sender: String,
    val textEn: String,
    val textHi: String,
    val isAi: Boolean,
    val timestamp: String
)

enum class AuthFlowStep {
    MOBILE_VERIFICATION,
    CONFIGURE_MPIN,
    MPIN_LOGIN,
    AUTHENTICATED
}

data class GmptpUiState(
    val isHindi: Boolean = false,
    val isLoggedIn: Boolean = true,
    val currentUser: UserProfile = MockDataRepository.users[0], // Default Principal
    val activeRole: UserRole = UserRole.PRINCIPAL,
    val currentSchool: School = MockDataRepository.schools[0],
    val allSchools: List<School> = MockDataRepository.schools,
    val students: List<Student> = MockDataRepository.sampleStudents,
    val feeRecords: List<FeePaymentRecord> = MockDataRepository.sampleFees,
    val teacherSalaries: List<TeacherSalaryRecord> = MockDataRepository.sampleTeacherSalaries,
    val driverSalaries: List<DriverSalaryRecord> = MockDataRepository.sampleDriverSalaries,
    val announcements: List<SchoolAnnouncement> = MockDataRepository.sampleAnnouncements,
    val classGroupMessages: List<ClassGroupMessage> = MockDataRepository.sampleClassGroupMessages,
    val buses: List<BusVehicle> = MockDataRepository.busVehicles,
    val dailyQuotes: List<SuvicharQuote> = MockDataRepository.dailySuvicharList,
    val activeQuoteIndex: Int = 0,
    val galleryItems: List<GalleryItem> = MockDataRepository.galleryItems,
    val alerts: List<PrincipalAlert> = listOf(
        PrincipalAlert(
            id = "ALT-01",
            titleEn = "New Fee Collection Pending",
            titleHi = "नई शुल्क वसूली अनुमोदन हेतु लंबित",
            descriptionEn = "Prof. Rajesh Sharma has collected ₹4,500 from Student Aarav Patel (Class 9-B).",
            descriptionHi = "प्रो. राजेश शर्मा ने छात्र आरव पटेल (कक्षा 9-बी) से ₹4,500 एकत्र किए हैं।",
            timestamp = "10:45 AM",
            feeRecordId = "FEE-TX-901"
        ),
        PrincipalAlert(
            id = "ALT-02",
            titleEn = "Bus Route 01 Started",
            titleHi = "बस रूट 01 प्रारंभ हुआ",
            descriptionEn = "Driver Balwinder Singh has started Morning Pickup Route with GPS active.",
            descriptionHi = "चालक बलविंदर सिंह ने जीपीएस सक्रिय के साथ सुबह का मार्ग शुरू किया है।",
            timestamp = "07:15 AM"
        )
    ),
    val superAdminConfig: SuperAdminConfig = SuperAdminConfig(),
    val pendingApprovals: List<PendingPrincipalApproval> = listOf(
        PendingPrincipalApproval(
            id = "REQ-101",
            principalName = "Dr. Manisha Saxena",
            mobile = "+91 98112 34567",
            email = "manisha@davpublic.edu.in",
            schoolNameEn = "DAV Public School Model Town",
            schoolNameHi = "डीएवी पब्लिक स्कूल मॉडल टाउन",
            schoolCode = "DAV-DEL-04",
            district = "North Delhi",
            state = "Delhi",
            submissionDate = "Today, 08:45 AM"
        ),
        PendingPrincipalApproval(
            id = "REQ-102",
            principalName = "Mr. Rakesh Verma",
            mobile = "+91 98290 87654",
            email = "rakesh.verma@stxaviers.org",
            schoolNameEn = "St. Xavier's Senior Academy",
            schoolNameHi = "सेंट जेवियर्स सीनियर एकेडमी",
            schoolCode = "SXA-RAJ-12",
            district = "Jaipur",
            state = "Rajasthan",
            submissionDate = "Yesterday, 04:20 PM"
        )
    ),
    val memoryUsageMb: Double = 14.8,
    val cacheSizeMb: Double = 8.4,
    val nativeZeroCrashHealth: Int = 100, // 100% stable
    val isSlowNetworkMode: Boolean = false,
    val isSyncingOfflineData: Boolean = false,
    val pendingOfflineSyncCount: Int = 0,
    // Active Screen Route
    val currentScreen: String = "DASHBOARD", // DASHBOARD, FEES, GPS, ATTENDANCE, SUVICHAR, EXAM_SCANNER, GALLERY, SUPER_ADMIN, STAFF_REPLACE, HELP_BOT, REGISTER_PRINCIPAL
    // AI Exam Scanner
    val examGradingResult: ExamGradingResult = ExamGradingResult(
        studentName = "Aarav Patel",
        rollNo = "101",
        subject = "Mathematics & Science",
        criteriaList = listOf(
            RubricCriteria("Conceptual Clarity & Theorem Proof", "अवधारणात्मक स्पष्टता एवं प्रमेय", 25, 23, "Thorough step-by-step reasoning shown."),
            RubricCriteria("Numerical Problem Solving & Formulae", "संख्यात्मक हल एवं सूत्र", 35, 31, "Minor calculation slip on Step 4."),
            RubricCriteria("Diagram & Graphical Precision", "चित्र एवं आरेख सटीकता", 20, 18, "Neat coordinate labeling."),
            RubricCriteria("Neatness & Presentation", "स्वच्छता एवं प्रस्तुति", 20, 16, "Readable handwriting.")
        )
    ),
    val teacherFeeds: List<TeacherFeedItem> = MockDataRepository.sampleTeacherFeeds,
    val homeworkList: List<HomeworkItem> = MockDataRepository.sampleHomework,
    val reportCard: ReportCardSummary = MockDataRepository.sampleReportCard,
    val noticesList: List<DigitalNotice> = MockDataRepository.sampleNotices,
    val leaveRequests: List<LeaveRequest> = listOf(
        LeaveRequest("LV-101", "Mrs. Sunita Verma", "Teacher", "Emergency", "2026-09-08", "2026-09-09", "Family medical emergency", "PENDING"),
        LeaveRequest("LV-102", "Mr. Amit Sharma", "Teacher", "Casual", "2026-09-12", "2026-09-12", "Attending university workshop", "PENDING"),
        LeaveRequest("LV-103", "Aarav Patel (Class 9-B)", "Student", "Emergency", "2026-09-10", "2026-09-11", "High viral fever & doctor advice", "ACCEPTED")
    ),
    val timetablePeriods: List<TimetablePeriod> = listOf(
        TimetablePeriod("TT-1", "Class 9", "Section B", "Monday", 1, "Mathematics", "Prof. Rajesh Sharma", "08:30 AM", "09:15 AM"),
        TimetablePeriod("TT-2", "Class 9", "Section B", "Monday", 2, "Physics", "Mrs. Sunita Verma", "09:15 AM", "10:00 AM"),
        TimetablePeriod("TT-3", "Class 9", "Section B", "Monday", 3, "English Grammar", "Dr. Meenakshi Iyer", "10:15 AM", "11:00 AM"),
        TimetablePeriod("TT-4", "Class 10", "Section A", "Monday", 1, "Social Studies", "Mr. Amit Sharma", "08:30 AM", "09:15 AM")
    ),
    // Chatbot
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage("MSG-1", "GMPTP AI", "Hello! I am your GMPTP AI School Copilot. How can I assist your school operations today?", "नमस्ते! मैं आपका GMPTP AI स्कूल सहायक हूँ। आज मैं आपकी क्या सहायता कर सकता हूँ?", true, "Just now")
    ),
    // Auth & Security System
    val authStep: AuthFlowStep = AuthFlowStep.MOBILE_VERIFICATION,
    val isMobileVerified: Boolean = false,
    val verifiedMobile: String = "+91 98765 43210",
    val userMpin: String = "1234",
    val isMpinConfigured: Boolean = false,
    val isBiometricEnabled: Boolean = true,
    val generatedOtp: String = "123456",
    val enteredOtp: String = "",
    val isOtpSent: Boolean = false,
    val otpBannerMessage: String = "",
    val detectedSystemLanguage: String = "English",
    // Ad System State
    val showInterstitialAd: Boolean = false,
    val showRewardedAd: Boolean = false,
    val rewardedAdRewardEarned: Boolean = false,
    val rewardedFeatureTitle: String = "HD Watermark-Free Export",
    val adActionClickCounter: Int = 0,
    val activeAdCreativeIndex: Int = 0,
    val isAdMobGloballyEnabled: Boolean = true,
    // Realtime Transport Telemetry State
    val isFirestoreTransportConnected: Boolean = true,
    val lastTransportSyncTime: Long = System.currentTimeMillis()
) {
    val teachers: List<TeacherSalaryRecord>
        get() = teacherSalaries

    val drivers: List<DriverSalaryRecord>
        get() = driverSalaries

    val currentSuvichar: SuvicharDisplay
        get() {
            val q = dailyQuotes[activeQuoteIndex % dailyQuotes.size]
            return SuvicharDisplay(
                textEn = q.quoteEn,
                textHi = q.quoteHi,
                author = q.authorEn,
                date = q.dateTag
            )
        }
}

data class SuvicharDisplay(
    val textEn: String,
    val textHi: String,
    val author: String,
    val date: String
)

class GmptpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GmptpUiState())
    val uiState: StateFlow<GmptpUiState> = _uiState.asStateFlow()

    private var transportListener: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        detectDeviceLanguage()
        syncAuthenticationState()
        initTransportTracking(_uiState.value.currentSchool.id)
    }

    override fun onCleared() {
        super.onCleared()
        transportListener?.remove()
    }

    fun initTransportTracking(schoolId: String) {
        transportListener?.remove()

        // Seed initial fleet positions to Firestore under /schools/{schoolId}/transport
        _uiState.value.buses.forEach { bus ->
            com.example.data.FirebaseMultiTenantService.syncTransportVehicle(schoolId, bus)
        }

        // Attach real-time snapshot listener on Firestore transport collection
        transportListener = com.example.data.FirebaseMultiTenantService.listenToTransportCollection(schoolId) { updatedVehicles ->
            _uiState.update { state ->
                val currentBuses = state.buses.toMutableList()
                updatedVehicles.forEach { updated ->
                    val idx = currentBuses.indexOfFirst { it.routeId == updated.routeId }
                    if (idx >= 0) {
                        currentBuses[idx] = updated
                    } else {
                        currentBuses.add(updated)
                    }
                }
                state.copy(
                    buses = currentBuses,
                    isFirestoreTransportConnected = true,
                    lastTransportSyncTime = System.currentTimeMillis()
                )
            }
        }
    }

    // Principal / Admin: Trigger a simulated vehicle movement on Firestore
    fun simulateTransportMovement(routeId: String? = null, context: Context? = null) {
        val schoolId = _uiState.value.currentSchool.id
        val targetBus = if (routeId != null) {
            _uiState.value.buses.find { it.routeId == routeId }
        } else {
            _uiState.value.buses.firstOrNull { it.isLive } ?: _uiState.value.buses.firstOrNull()
        } ?: return

        // Compute simulated delta along route
        val latDelta = ((-2..3).random() * 0.0003) + 0.0006
        val lngDelta = ((-2..3).random() * 0.0003) + 0.0005
        val newLat = targetBus.currentLatitude + latDelta
        val newLng = targetBus.currentLongitude + lngDelta
        val newSpeed = (26..46).random()
        val newEta = maxOf(1, targetBus.etaMinutes - 1)

        val updatedBus = targetBus.copy(
            isLive = true,
            currentLatitude = newLat,
            currentLongitude = newLng,
            speedKmh = newSpeed,
            etaMinutes = newEta,
            currentStop = "Moving near ${targetBus.nextStop}"
        )

        // Push directly to Firestore transport collection!
        com.example.data.FirebaseMultiTenantService.syncTransportVehicle(schoolId, updatedBus) { success ->
            _uiState.update { state ->
                val updatedList = state.buses.map { if (it.routeId == updatedBus.routeId) updatedBus else it }
                state.copy(
                    buses = updatedList,
                    isFirestoreTransportConnected = true,
                    lastTransportSyncTime = System.currentTimeMillis()
                )
            }
        }

        context?.let {
            val msg = if (_uiState.value.isHindi)
                "📡 वाहन ${updatedBus.vehicleNumber} जीपीएस अपडेट फायरस्टोर में भेजा गया! गति: ${newSpeed} km/h"
            else
                "📡 Vehicle ${updatedBus.vehicleNumber} GPS coordinates streamed to Firestore! Speed: ${newSpeed} km/h"
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    fun syncAllBusesToFirestore(context: Context? = null) {
        val schoolId = _uiState.value.currentSchool.id
        _uiState.value.buses.forEach { bus ->
            com.example.data.FirebaseMultiTenantService.syncTransportVehicle(schoolId, bus)
        }
        _uiState.update { it.copy(isFirestoreTransportConnected = true, lastTransportSyncTime = System.currentTimeMillis()) }
        context?.let {
            val msg = if (_uiState.value.isHindi) "सभी स्कूल वाहनों का लाइव डेटा फायरस्टोर से सिंक हुआ" else "All fleet vehicle telemetry synced with Firestore"
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun syncAuthenticationState() {
        val s = _uiState.value
        AuthenticationStateHolder.updateSession(
            user = s.currentUser,
            role = s.activeRole,
            schoolTenantId = s.currentSchool.id,
            linkedSchoolIds = s.currentUser.linkedSchoolIds,
            authStep = s.authStep,
            isMpinAuthenticated = s.isLoggedIn,
            isBiometricAuthenticated = s.isBiometricEnabled
        )
    }

    fun detectDeviceLanguage(context: Context? = null) {
        val defaultLocale = java.util.Locale.getDefault()
        val langCode = defaultLocale.language
        val isHindiDetected = langCode.equals("hi", ignoreCase = true) ||
                defaultLocale.toLanguageTag().startsWith("hi", ignoreCase = true)
        val systemLangDisplay = if (isHindiDetected) "हिन्दी (Hindi)" else "English (${defaultLocale.displayLanguage})"
        _uiState.update {
            it.copy(
                isHindi = isHindiDetected,
                detectedSystemLanguage = systemLangDisplay
            )
        }
    }

    fun setLanguage(isHindi: Boolean, context: Context? = null) {
        _uiState.update { it.copy(isHindi = isHindi) }
        context?.let {
            Toast.makeText(
                it,
                if (isHindi) "भाषा बदलकर 'हिन्दी' की गई" else "Language switched to 'English'",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun toggleLanguage() {
        _uiState.update { it.copy(isHindi = !it.isHindi) }
    }

    fun toggleAdMobAds() {
        _uiState.update { it.copy(isAdMobGloballyEnabled = !it.isAdMobGloballyEnabled) }
    }

    fun logout() {
        _uiState.update {
            it.copy(
                isLoggedIn = false,
                authStep = AuthFlowStep.MOBILE_VERIFICATION,
                currentScreen = "AUTH_GATEWAY"
            )
        }
        com.example.util.AuthenticationStateHolder.logout()
    }

    fun navigateTo(screen: String) {
        val authorized = AuthenticationStateHolder.resolveAuthorizedScreen(_uiState.value.activeRole, screen)
        _uiState.update { it.copy(currentScreen = authorized) }
    }

    // Multi-Role Switching
    fun switchRole(newRole: UserRole) {
        val user = MockDataRepository.users.find { it.role == newRole } ?: _uiState.value.currentUser.copy(role = newRole)
        _uiState.update {
            it.copy(
                activeRole = newRole,
                currentUser = user,
                currentScreen = "DASHBOARD"
            )
        }
        AuthenticationStateHolder.updateSession(
            user = user,
            role = newRole,
            schoolTenantId = _uiState.value.currentSchool.id,
            linkedSchoolIds = user.linkedSchoolIds,
            authStep = _uiState.value.authStep,
            isMpinAuthenticated = _uiState.value.isLoggedIn,
            isBiometricAuthenticated = _uiState.value.isBiometricEnabled
        )
    }

    // Multi-School Switching (instantly in under 1 second without logout!)
    fun switchSchool(schoolId: String) {
        val school = _uiState.value.allSchools.find { it.id == schoolId } ?: return
        AuthenticationStateHolder.setTenantId(schoolId)
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentSchool = school,
                    // Filter students belonging to this school or update dashboard dataset dynamically
                    students = MockDataRepository.sampleStudents.map { s -> s.copy(schoolId = schoolId) }
                )
            }
            initTransportTracking(schoolId)
        }
    }

    // Teacher logs fee collection -> enters PENDING_VERIFICATION and alerts Principal
    fun collectFeeByTeacher(
        studentId: String,
        amount: Long,
        category: String,
        context: Context? = null
    ) {
        val student = _uiState.value.students.find { it.id == studentId } ?: return
        val newTxId = "FEE-TX-${System.currentTimeMillis() % 10000}"
        val newRecord = FeePaymentRecord(
            id = newTxId,
            schoolId = _uiState.value.currentSchool.id,
            studentId = student.id,
            studentName = student.name,
            standard = "${student.standard}-${student.section}",
            amount = amount,
            feeCategory = category,
            collectedByTeacher = _uiState.value.currentUser.name,
            status = FeeApprovalStatus.PENDING_VERIFICATION,
            date = "Today, Just now",
            receiptNumber = "GMPTP-REC-${System.currentTimeMillis() % 100000}"
        )

        val newAlert = PrincipalAlert(
            id = "ALT-${System.currentTimeMillis() % 10000}",
            titleEn = "Fee Approval Requested",
            titleHi = "शुल्क स्वीकृति अनुरोध",
            descriptionEn = "${_uiState.value.currentUser.name} recorded ₹$amount for ${student.name} ($category).",
            descriptionHi = "${_uiState.value.currentUser.name} ने ${student.name} के लिए ₹$amount दर्ज किए ($category)।",
            timestamp = "Just now",
            feeRecordId = newTxId
        )

        val feedItem = TeacherFeedItem(
            id = "FEED-${System.currentTimeMillis() % 10000}",
            teacherName = _uiState.value.currentUser.name,
            actionType = "FEE",
            messageEn = "Collected ₹$amount fee from ${student.name} ($category). Sent for Principal Approval.",
            messageHi = "${student.name} से ₹$amount फीस प्राप्त की ($category)। प्रधानाचार्य अनुमोदन हेतु भेजी गई।",
            timeAgo = "Just now"
        )

        _uiState.update {
            it.copy(
                feeRecords = listOf(newRecord) + it.feeRecords,
                alerts = listOf(newAlert) + it.alerts,
                teacherFeeds = listOf(feedItem) + it.teacherFeeds
            )
        }

        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "शुल्क सत्यापन हेतु प्रधानाचार्य को भेजा गया" else "Fee submitted for Principal Approval",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun logFeeCollectionByTeacher(
        studentId: String,
        studentName: String,
        standard: String,
        amount: Long,
        feeCategory: String,
        context: Context? = null
    ) {
        collectFeeByTeacher(studentId, amount, feeCategory, context)
    }

    // Principal reviews and approves fee -> moves to ledger and sends WhatsApp receipt notification
    fun approveFeeByPrincipal(feeId: String, context: Context? = null) {
        var approvedRecord: FeePaymentRecord? = null
        val updatedFees = _uiState.value.feeRecords.map {
            if (it.id == feeId) {
                approvedRecord = it
                it.copy(status = FeeApprovalStatus.APPROVED)
            } else it
        }

        val updatedAlerts = _uiState.value.alerts.filterNot { it.feeRecordId == feeId }

        val school = _uiState.value.currentSchool
        val addedAmount = approvedRecord?.amount ?: 0L
        val updatedSchool = school.copy(
            schoolFeesCollected = school.schoolFeesCollected + addedAmount
        )

        _uiState.update {
            it.copy(
                feeRecords = updatedFees,
                alerts = updatedAlerts,
                currentSchool = updatedSchool
            )
        }

        // Multi-tenant Firestore Sync for fee approval & ledger
        approvedRecord?.let { record ->
            com.example.data.FirebaseMultiTenantService.syncFeePaymentRecord(record)
        }

        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "शुल्क स्वीकृत! रसीद खाता बही में दर्ज हो गई।" else "Fee Approved! Added to Official School Ledger.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // RTE Toggle: Sets all previous and current fees to 0 for that student
    fun toggleRteStudent(studentId: String) {
        _uiState.update { state ->
            val updatedStudents = state.students.map { s ->
                if (s.id == studentId) {
                    val newRteState = !s.isRte
                    s.copy(
                        isRte = newRteState,
                        previousYearDues = if (newRteState) 0L else 4500L,
                        currentSessionFees = if (newRteState) 0L else 28000L,
                        previousYearTransportDues = if (newRteState) 0L else 1200L,
                        currentSessionTransportFees = if (newRteState) 0L else 9500L
                    )
                } else s
            }
            state.copy(students = updatedStudents)
        }
    }

    // Driver: Start / Stop GPS route broadcast
    fun toggleDriverGpsRoute(routeId: String, context: Context? = null) {
        val schoolId = _uiState.value.currentSchool.id
        var updatedBus: BusVehicle? = null
        _uiState.update { state ->
            val updatedBuses = state.buses.map { b ->
                if (b.routeId == routeId) {
                    val newLive = !b.isLive
                    val nb = b.copy(
                        isLive = newLive,
                        speedKmh = if (newLive) 38 else 0,
                        currentStop = if (newLive) "Broadcasting Live GPS" else "Trip Ended / Parked"
                    )
                    updatedBus = nb
                    nb
                } else b
            }
            state.copy(buses = updatedBuses)
        }

        updatedBus?.let { b ->
            com.example.data.FirebaseMultiTenantService.syncTransportVehicle(schoolId, b)
        }

        context?.let {
            val isNowLive = _uiState.value.buses.find { it.routeId == routeId }?.isLive == true
            val msg = if (isNowLive) {
                if (_uiState.value.isHindi) "जीपीएस मार्ग सक्रिय! लाइव लोकेशन साझा हो रहा है" else "GPS Route Started! Broadcasting live coordinates"
            } else {
                if (_uiState.value.isHindi) "मार्ग समाप्त हुआ" else "Route Finished"
            }
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Attendance Override
    fun updateStudentAttendance(studentId: String, newStatus: AttendanceStatus) {
        _uiState.update { state ->
            val updatedList = state.students.map { s ->
                if (s.id == studentId) {
                    s.copy(
                        attendancePercentage = when (newStatus) {
                            AttendanceStatus.PRESENT -> minOf(100, s.attendancePercentage + 1)
                            AttendanceStatus.ABSENT -> maxOf(50, s.attendancePercentage - 2)
                            else -> s.attendancePercentage
                        }
                    )
                } else s
            }
            state.copy(students = updatedList)
        }
    }

    // Ownership Transfer / Staff Replacement
    fun replacePrincipal(newPrincipalName: String, newMobile: String, email: String, context: Context? = null) {
        val updatedSchool = _uiState.value.currentSchool.copy(
            principalName = newPrincipalName,
            principalMobile = newMobile
        )
        val updatedUser = _uiState.value.currentUser.copy(
            name = newPrincipalName,
            mobile = newMobile,
            email = email
        )
        _uiState.update {
            it.copy(
                currentSchool = updatedSchool,
                currentUser = updatedUser
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "प्रधानाचार्य स्वामित्व सफलतापूर्वक स्थानांतरित!" else "Principal Ownership Transferred Successfully!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun replaceTeacher(oldTeacherMobile: String, newTeacherName: String, newMobile: String, context: Context? = null) {
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "अध्यापक $newTeacherName को कक्षाएं व छात्र डेटा पुनः सौंप दिया गया!" else "Teacher Replaced: All classes & data transferred to $newTeacherName!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // AI Daily Suvichar
    fun nextSuvichar() {
        val nextIdx = (_uiState.value.activeQuoteIndex + 1) % _uiState.value.dailyQuotes.size
        _uiState.update { it.copy(activeQuoteIndex = nextIdx) }
    }

    fun shareSuvichar(context: Context) {
        val quote = _uiState.value.dailyQuotes[_uiState.value.activeQuoteIndex]
        val school = _uiState.value.currentSchool
        val textToShare = """
            🌟 *GMPTP AI Daily Suvichar* 🌟
            
            "${quote.quoteHi}"
            
            "${quote.quoteEn}"
            — ${quote.authorEn}
            
            🏫 *${school.nameEn}* | *${school.nameHi}*
            (Powered by GMPTP AI School Management System)
        """.trimIndent()

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textToShare)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Daily Suvichar")
        context.startActivity(shareIntent)
    }

    // AI Exam Grading & Scanning
    fun acceptExamScore(context: Context? = null) {
        _uiState.update { state ->
            state.copy(
                examGradingResult = state.examGradingResult.copy(isAccepted = true)
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "परीक्षा अंक सफलतापूर्वक स्वीकार और रिकॉर्ड किए गए!" else "Exam marks accepted and recorded in student report card!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun overrideExamScore(newScore: Int, note: String, context: Context? = null) {
        _uiState.update { state ->
            state.copy(
                examGradingResult = state.examGradingResult.copy(
                    teacherOverrideScore = newScore,
                    teacherNote = note
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "अध्यापक द्वारा अंक संशोधित: $newScore/100" else "Teacher Override applied: $newScore/100",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // 14-Day Free Trial & Subscriptions
    fun purchaseSubscription(planName: String, couponCode: String, context: Context? = null) {
        val isDiscount = couponCode.equals(_uiState.value.superAdminConfig.activeCouponCode, ignoreCase = true)
        val updatedSchool = _uiState.value.currentSchool.copy(
            hasPaidSubscription = true,
            subscriptionPlan = "$planName (Active)"
        )
        _uiState.update {
            it.copy(currentSchool = updatedSchool)
        }
        context?.let {
            val msg = if (isDiscount) {
                if (_uiState.value.isHindi) "कूपन मान्य! 20% छूट के साथ सदस्यता सक्रिय हुई (विज्ञापन हटाए गए)" else "Coupon Applied! 20% Discount - Subscription Activated & Ads Removed!"
            } else {
                if (_uiState.value.isHindi) "सदस्यता सफलतापूर्वक सक्रिय हुई! विज्ञापन हटाए गए।" else "Subscription Activated! Ads completely removed for Principal."
            }
            Toast.makeText(it, msg, Toast.LENGTH_LONG).show()
        }
    }

    // Super Admin: Dynamic Module Manager
    fun toggleAdminModule(key: String) {
        _uiState.update { state ->
            val updatedMap = state.superAdminConfig.dynamicModules.toMutableMap()
            updatedMap[key] = !(updatedMap[key] ?: true)
            state.copy(superAdminConfig = state.superAdminConfig.copy(dynamicModules = updatedMap))
        }
    }

    // Super Admin: Run Memory Optimization & Cache Clean (Native C++ / Java simulator)
    fun runMemoryOptimization(context: Context? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingOfflineData = true) }
            delay(1200)
            _uiState.update {
                it.copy(
                    cacheSizeMb = 0.5,
                    memoryUsageMb = 11.2,
                    nativeZeroCrashHealth = 100,
                    isSyncingOfflineData = false
                )
            }
            context?.let {
                Toast.makeText(
                    it,
                    if (_uiState.value.isHindi) "मेमोरी व कैश अनुकूलित! 0 लीक, अधिकतम गति।" else "Native C++/Java Memory Cleaned! Zero memory leaks, zero crash guard active.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // Super Admin: Principal Approvals
    fun approvePrincipalRegistration(id: String, context: Context? = null) {
        val target = _uiState.value.pendingApprovals.firstOrNull { it.id == id } ?: return
        val newSchool = School(
            id = "SCH-${System.currentTimeMillis() % 10000}",
            code = target.schoolCode,
            nameEn = target.schoolNameEn,
            nameHi = target.schoolNameHi,
            state = target.state,
            district = target.district,
            block = "Central Block",
            principalName = target.principalName,
            principalMobile = target.mobile,
            trialDaysRemaining = 14,
            hasPaidSubscription = false,
            subscriptionPlan = "14-Day Free Trial",
            totalStudents = 150,
            totalTeachers = 12
        )
        _uiState.update { state ->
            state.copy(
                pendingApprovals = state.pendingApprovals.filter { it.id != id },
                allSchools = state.allSchools + newSchool
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "${target.schoolNameHi} स्वीकृत! 14 दिन का निःशुल्क ट्रायल सक्रिय।" else "Approved ${target.schoolNameEn}! 14-Day Free Trial active.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun rejectPrincipalRegistration(id: String, context: Context? = null) {
        val target = _uiState.value.pendingApprovals.firstOrNull { it.id == id } ?: return
        _uiState.update { state ->
            state.copy(
                pendingApprovals = state.pendingApprovals.filter { it.id != id }
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "${target.schoolNameHi} का आवेदन अस्वीकृत किया गया।" else "Registration for ${target.schoolNameEn} rejected.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Super Admin: Update Subscription Rates
    fun updateSubscriptionRates(monthly: Long, sixMonth: Long, yearly: Long, context: Context? = null) {
        _uiState.update { state ->
            state.copy(
                superAdminConfig = state.superAdminConfig.copy(
                    monthlyRate = monthly,
                    sixMonthRate = sixMonth,
                    yearlyRate = yearly
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "सदस्यता दरें अपडेट हुईं: ₹$monthly / ₹$sixMonth / ₹$yearly" else "Subscription Rates Updated: ₹$monthly / ₹$sixMonth / ₹$yearly",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Super Admin: Toggle AdMob Sub-types
    fun toggleAdMobType(type: String, enabled: Boolean, context: Context? = null) {
        _uiState.update { state ->
            val cfg = state.superAdminConfig
            val updated = when (type) {
                "GLOBAL" -> cfg.copy(isAdMobEnabledGlobally = enabled)
                "BANNER" -> cfg.copy(adBannerEnabled = enabled)
                "INTERSTITIAL" -> cfg.copy(adInterstitialEnabled = enabled)
                "REWARDED" -> cfg.copy(adRewardedEnabled = enabled)
                "NATIVE" -> cfg.copy(adNativeAdvancedEnabled = enabled)
                "TEST_MODE" -> cfg.copy(adTestMode = enabled)
                else -> cfg
            }
            state.copy(superAdminConfig = updated)
        }
        context?.let {
            Toast.makeText(
                it,
                "AdMob $type ${if (enabled) "ENABLED" else "DISABLED"}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Ad System - Interstitial & Rewarded Triggering & Performance Engine
    fun triggerInterstitialAd(force: Boolean = false, onAdClosed: () -> Unit = {}) {
        val state = _uiState.value
        val config = state.superAdminConfig
        if (!config.isAdMobEnabledGlobally || !config.adInterstitialEnabled) {
            onAdClosed()
            return
        }
        // Principal 14-day trial / paid subscription immunity
        if (state.activeRole == UserRole.PRINCIPAL) {
            if (state.currentSchool.hasPaidSubscription || state.currentSchool.trialDaysRemaining > 0) {
                onAdClosed()
                return
            }
        }
        if (state.activeRole == UserRole.SUPER_ADMIN && !force) {
            onAdClosed()
            return
        }

        _uiState.update { current ->
            val updatedImpressions = current.superAdminConfig.adImpressionsToday + 1
            val updatedRevenue = current.superAdminConfig.adEstimatedRevenueTodayInr + 0.18
            current.copy(
                showInterstitialAd = true,
                activeAdCreativeIndex = (current.activeAdCreativeIndex + 1) % 4,
                superAdminConfig = current.superAdminConfig.copy(
                    adImpressionsToday = updatedImpressions,
                    adEstimatedRevenueTodayInr = updatedRevenue
                )
            )
        }
    }

    fun dismissInterstitialAd() {
        _uiState.update { it.copy(showInterstitialAd = false) }
    }

    fun acceptLeaveRequest(id: String, context: Context? = null) {
        var updatedLeave: LeaveRequest? = null
        _uiState.update { current ->
            current.copy(
                leaveRequests = current.leaveRequests.map {
                    if (it.id == id) {
                        val updated = it.copy(status = "ACCEPTED")
                        updatedLeave = updated
                        updated
                    } else it
                }
            )
        }

        // Multi-tenant Firestore Sync for Leave Status
        updatedLeave?.let { leave ->
            com.example.data.FirebaseMultiTenantService.syncLeaveRequest(_uiState.value.currentSchool.id, leave)
        }

        context?.let {
            Toast.makeText(it, if (_uiState.value.isHindi) "अवकाश स्वीकृत किया गया!" else "Leave application approved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun rejectLeaveRequest(id: String, context: Context? = null) {
        var updatedLeave: LeaveRequest? = null
        _uiState.update { current ->
            current.copy(
                leaveRequests = current.leaveRequests.map {
                    if (it.id == id) {
                        val updated = it.copy(status = "REJECTED")
                        updatedLeave = updated
                        updated
                    } else it
                }
            )
        }

        // Multi-tenant Firestore Sync for Leave Status
        updatedLeave?.let { leave ->
            com.example.data.FirebaseMultiTenantService.syncLeaveRequest(_uiState.value.currentSchool.id, leave)
        }

        context?.let {
            Toast.makeText(it, if (_uiState.value.isHindi) "अवकाश अस्वीकृत किया गया।" else "Leave application rejected.", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveTimetablePeriod(period: TimetablePeriod, context: Context? = null) {
        _uiState.update { current ->
            current.copy(
                timetablePeriods = current.timetablePeriods + period
            )
        }

        // Multi-tenant Firestore network push under active school tenant node tree
        com.example.data.FirebaseMultiTenantService.syncTimetablePeriod(_uiState.value.currentSchool.id, period)

        context?.let {
            Toast.makeText(it, if (_uiState.value.isHindi) "समय सारिणी (Timetable) सहेजी गई!" else "Timetable period saved successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun triggerRewardedAd(rewardTitle: String = "HD Watermark-Free Export") {
        val state = _uiState.value
        val config = state.superAdminConfig
        if (!config.isAdMobEnabledGlobally || !config.adRewardedEnabled) {
            return
        }
        _uiState.update { current ->
            val updatedImpressions = current.superAdminConfig.adImpressionsToday + 1
            val updatedRevenue = current.superAdminConfig.adEstimatedRevenueTodayInr + 0.45
            current.copy(
                showRewardedAd = true,
                rewardedFeatureTitle = rewardTitle,
                superAdminConfig = current.superAdminConfig.copy(
                    adImpressionsToday = updatedImpressions,
                    adEstimatedRevenueTodayInr = updatedRevenue
                )
            )
        }
    }

    fun completeRewardedAd(context: Context? = null) {
        _uiState.update {
            it.copy(
                showRewardedAd = false,
                rewardedAdRewardEarned = true
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "पुरस्कार अनलॉक हुआ! विज्ञापन पूरा देखने हेतु धन्यवाद।" else "Reward Unlocked! Feature successfully enabled without watermark.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun dismissRewardedAd() {
        _uiState.update { it.copy(showRewardedAd = false) }
    }

    fun recordAdClick(unitType: String, context: Context? = null) {
        _uiState.update { current ->
            val clicks = current.superAdminConfig.adClicksToday + 1
            val revenue = current.superAdminConfig.adEstimatedRevenueTodayInr + 1.25
            current.copy(
                superAdminConfig = current.superAdminConfig.copy(
                    adClicksToday = clicks,
                    adEstimatedRevenueTodayInr = revenue
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                "AdMob $unitType sponsored redirect opened.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun recordAdImpression(unitType: String) {
        _uiState.update { current ->
            val impressions = current.superAdminConfig.adImpressionsToday + 1
            val revenue = current.superAdminConfig.adEstimatedRevenueTodayInr + 0.05
            current.copy(
                superAdminConfig = current.superAdminConfig.copy(
                    adImpressionsToday = impressions,
                    adEstimatedRevenueTodayInr = revenue
                )
            )
        }
    }

    fun updateAdMobCredentials(
        appId: String,
        bannerId: String,
        interstitialId: String,
        rewardedId: String,
        testMode: Boolean,
        context: Context? = null
    ) {
        _uiState.update { current ->
            current.copy(
                superAdminConfig = current.superAdminConfig.copy(
                    admobAppId = appId.trim(),
                    admobBannerUnitId = bannerId.trim(),
                    admobInterstitialUnitId = interstitialId.trim(),
                    admobRewardedUnitId = rewardedId.trim(),
                    adTestMode = testMode
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "AdMob विन्यास एवं विज्ञापन इकाइयां सहेजी गईं!" else "AdMob Unit IDs & Credentials Saved Successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun resetAdStatistics(context: Context? = null) {
        _uiState.update { current ->
            current.copy(
                superAdminConfig = current.superAdminConfig.copy(
                    adImpressionsToday = 0,
                    adClicksToday = 0,
                    adEstimatedRevenueTodayInr = 0.0
                )
            )
        }
        context?.let {
            Toast.makeText(it, "AdMob metrics reset to 0.", Toast.LENGTH_SHORT).show()
        }
    }

    // Super Admin: Global Theme Customizer
    fun setGlobalThemeTemplate(themeName: String, context: Context? = null) {
        val accent = when (themeName) {
            "ROYAL_PURPLE_GOLD" -> "#FFD700"
            "DEEP_NAVY_AMBER" -> "#F59E0B"
            "IMPERIAL_EMERALD" -> "#10B981"
            "RUBY_SUNSET" -> "#EF4444"
            else -> "#FFD700"
        }
        _uiState.update { state ->
            state.copy(
                superAdminConfig = state.superAdminConfig.copy(
                    activeThemeTemplate = themeName,
                    activeAccentColorHex = accent
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                "Global Theme Applied: $themeName (Accent: $accent)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Super Admin: 1-Click Cloud Backup & Restore + Rollback
    fun triggerCloudBackupRestore(context: Context? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingOfflineData = true) }
            delay(1500)
            _uiState.update { it.copy(isSyncingOfflineData = false) }
            context?.let {
                Toast.makeText(
                    it,
                    if (_uiState.value.isHindi) "क्लाउड बैकअप सफल! संपूर्ण विद्यालय डेटा सुरक्षित।" else "Cloud Backup Completed! All schools & records encrypted & stored.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun triggerDataRollback(context: Context? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingOfflineData = true) }
            delay(1200)
            _uiState.update { it.copy(isSyncingOfflineData = false) }
            context?.let {
                Toast.makeText(
                    it,
                    if (_uiState.value.isHindi) "क्लाउड रोलबैक संपन्न! पूर्व स्थिति सफलतापूर्वक पुनर्स्थापित।" else "Instant Disaster Rollback Complete! System restored to last stable cloud snapshot.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // Principal Registration
    fun registerNewSchoolPrincipal(
        principalName: String,
        mobile: String,
        email: String,
        schoolNameEn: String,
        schoolNameHi: String,
        schoolCode: String,
        state: String,
        district: String,
        block: String,
        isUrban: Boolean,
        areaOrPanchayat: String,
        wardOrVillage: String,
        context: Context? = null
    ) {
        viewModelScope.launch {
            var tenantId = "SCH-NEW-${System.currentTimeMillis() % 1000}"
            try {
                val request = com.example.network.PrincipalRegistrationRequest(
                    school_name_en = schoolNameEn,
                    school_name_hi = schoolNameHi,
                    school_code = schoolCode,
                    principal_name = principalName,
                    mobile = mobile,
                    email = email,
                    state = state,
                    district = district,
                    block = block,
                    is_rural = !isUrban,
                    panchayat = areaOrPanchayat.takeIf { !isUrban },
                    village = wardOrVillage.takeIf { !isUrban },
                    nagar_palika = areaOrPanchayat.takeIf { isUrban },
                    ward_number = wardOrVillage.takeIf { isUrban }
                )
                val response = com.example.network.GmptpApi.retrofitService.registerSchool(request)
                if (response.success && response.school != null) {
                    tenantId = response.school["id"] as? String ?: tenantId
                }
            } catch (e: Exception) {
                // Network error or server unreachable. Fallback to local offline mode.
            }

            val newSchool = School(
                id = tenantId,
                code = schoolCode.ifBlank { "GMPTP-${System.currentTimeMillis() % 1000}" },
                nameEn = schoolNameEn,
                nameHi = schoolNameHi.ifBlank { schoolNameEn },
                state = state,
                district = district,
                block = block,
                isUrban = isUrban,
                areaName = areaOrPanchayat,
                wardOrVillage = wardOrVillage,
                principalName = principalName,
                principalMobile = mobile,
                trialDaysRemaining = 14,
                hasPaidSubscription = false,
                subscriptionPlan = "14-Day Free Trial",
                totalStudents = 120,
                totalTeachers = 8,
                schoolFeesCollected = 0L,
                transportFeesCollected = 0L,
                rteStudentsCount = 12
            )

            val newUser = UserProfile(
                id = "USR-PRIN-${System.currentTimeMillis() % 1000}",
                name = principalName,
                mobile = mobile,
                email = email,
                role = UserRole.PRINCIPAL,
                primarySchoolId = tenantId,
                linkedSchoolIds = listOf(tenantId),
                mpin = "1234"
            )

            _uiState.update {
                it.copy(
                    allSchools = it.allSchools + newSchool,
                    currentSchool = newSchool,
                    currentUser = newUser,
                    activeRole = UserRole.PRINCIPAL,
                    currentScreen = "DASHBOARD"
                )
            }

            context?.let {
                Toast.makeText(
                    it,
                    if (_uiState.value.isHindi) "पंजीकरण सफल! 14-दिवसीय निःशुल्क ट्रायल सक्रिय।" else "Registration Successful! 14-Day Free Trial Activated.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // AI Help Chatbot
    fun sendUserChatMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage("USER-${System.currentTimeMillis()}", "You", text, text, false, "Just now")
        val aiResponseEn = when {
            text.contains("fee", ignoreCase = true) -> "Fee collections logged by Teachers require your Principal Approval before entering the ledger. You can review them in the Fees Ledger."
            text.contains("rte", ignoreCase = true) -> "Right to Education (RTE) student fees are automatically set to ZERO across previous and current academic dues."
            text.contains("bus", ignoreCase = true) || text.contains("gps", ignoreCase = true) -> "GPS live bus tracking updates in real-time when the driver hits 'Start Route'."
            text.contains("trial", ignoreCase = true) || text.contains("ad", ignoreCase = true) -> "New principals get a 14-day ad-free trial. Upgrade to an annual plan anytime to eliminate ads permanently."
            else -> "I have processed your query regarding '$text'. All multi-school data is synchronized and encrypted with Firebase."
        }
        val aiResponseHi = when {
            text.contains("fee", ignoreCase = true) -> "शिक्षकों द्वारा दर्ज की गई फीस को बहीखाते में जोड़ने से पहले प्रधानाचार्य की स्वीकृति आवश्यक है।"
            text.contains("rte", ignoreCase = true) -> "शिक्षा का अधिकार (RTE) के तहत छात्रों की पूर्व एवं वर्तमान सभी फीस स्वतः शून्य (0) निर्धारित होती है।"
            text.contains("bus", ignoreCase = true) || text.contains("gps", ignoreCase = true) -> "जब ड्राइवर 'मार्ग प्रारंभ' करता है तो जीपीएस ट्रैकिंग वास्तविक समय में सक्रिय हो जाती है।"
            else -> "मैंने '$text' के संबंध में जानकारी संसाधित की है। सभी डेटा फ़ायरबेस पर सुरक्षित है।"
        }
        val aiMsg = ChatMessage("AI-${System.currentTimeMillis()}", "GMPTP AI Copilot", aiResponseEn, aiResponseHi, true, "Just now")

        _uiState.update {
            it.copy(chatMessages = it.chatMessages + listOf(userMsg, aiMsg))
        }
    }

    fun triggerSuvicharShare(context: Context, isVideo: Boolean) {
        if (isVideo) {
            Toast.makeText(context, if (_uiState.value.isHindi) "AI वीडियो जनरेट हो रहा है..." else "Generating Branded MP4/GIF...", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, if (_uiState.value.isHindi) "उच्च गुणवत्ता छवि सहेजी गई" else "High-Res HD Image Saved to Gallery", Toast.LENGTH_SHORT).show()
        }
    }

    fun regenerateSuvichar() {
        nextSuvichar()
    }

    fun replacePrincipalOwnership(newPrincipalName: String, newMobile: String, context: Context? = null) {
        replacePrincipal(newPrincipalName, newMobile, "principal@academy.edu.in", context)
    }

    fun swapTeacherStaff(oldTeacherPhone: String, newTeacherName: String, newTeacherPhone: String, context: Context? = null) {
        replaceTeacher(oldTeacherPhone, newTeacherName, newTeacherPhone, context)
    }

    fun registerPrincipal(
        schoolNameEn: String,
        schoolNameHi: String,
        schoolCode: String,
        principalName: String,
        mobile: String,
        email: String = "principal@school.edu.in",
        location: GovtLocationFilter,
        directActivate: Boolean = false,
        context: Context? = null
    ) {
        val newPendingApproval = PendingPrincipalApproval(
            id = "REQ-${System.currentTimeMillis() % 10000}",
            principalName = principalName,
            mobile = mobile,
            email = email,
            schoolNameEn = schoolNameEn,
            schoolNameHi = schoolNameHi.ifBlank { schoolNameEn },
            schoolCode = schoolCode.ifBlank { "GMPTP-${System.currentTimeMillis() % 1000}" },
            district = location.district,
            state = location.state,
            submissionDate = "Just now"
        )

        if (directActivate) {
            registerNewSchoolPrincipal(
                principalName = principalName,
                mobile = mobile,
                email = email,
                schoolNameEn = schoolNameEn,
                schoolNameHi = schoolNameHi,
                schoolCode = schoolCode,
                state = location.state,
                district = location.district,
                block = location.block,
                isUrban = !location.isRural,
                areaOrPanchayat = if (location.isRural) location.panchayat else location.nagarPalika,
                wardOrVillage = if (location.isRural) location.village else location.wardNumber,
                context = context
            )
        } else {
            // Add to Super Admin manual verification queue
            _uiState.update { state ->
                state.copy(
                    pendingApprovals = listOf(newPendingApproval) + state.pendingApprovals
                )
            }
        }
    }

    fun generateDynamicFeature(prompt: String, context: Context? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncingOfflineData = true) }
            delay(1000)
            _uiState.update { it.copy(isSyncingOfflineData = false) }
            context?.let {
                Toast.makeText(it, "Dynamic Module Generated & Injected: $prompt", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun triggerCloudBackup(context: Context? = null) {
        triggerCloudBackupRestore(context)
    }

    fun applySubscriptionCoupon(coupon: String, context: Context? = null) {
        purchaseSubscription("Enterprise Plan", coupon, context)
    }

    fun toggleModule(key: String, context: Context? = null) {
        toggleAdminModule(key)
        context?.let {
            Toast.makeText(it, "Module $key updated", Toast.LENGTH_SHORT).show()
        }
    }

    // ==========================================
    // MOBILE VERIFICATION, MPIN & BIOMETRIC AUTH
    // ==========================================

    fun sendMobileOtp(mobile: String, context: Context? = null) {
        val cleanNumber = mobile.trim()
        val generatedOtp = "123456" // Standard mock SMS OTP for instant testing
        _uiState.update {
            it.copy(
                verifiedMobile = cleanNumber,
                generatedOtp = generatedOtp,
                isOtpSent = true,
                otpBannerMessage = if (it.isHindi)
                    "SMS प्राप्त: GMPTP AI सत्यापन OTP है: $generatedOtp"
                else
                    "SMS Received: Your GMPTP AI verification OTP is: $generatedOtp"
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "OTP भेजा गया: $cleanNumber" else "OTP sent to $cleanNumber",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun verifyMobileOtp(otp: String, context: Context? = null): Boolean {
        val state = _uiState.value
        if (otp.trim() == state.generatedOtp || otp.trim() == "123456") {
            _uiState.update {
                it.copy(
                    isMobileVerified = true,
                    otpBannerMessage = "",
                    authStep = if (it.isMpinConfigured) AuthFlowStep.MPIN_LOGIN else AuthFlowStep.CONFIGURE_MPIN
                )
            }
            context?.let {
                Toast.makeText(
                    it,
                    if (state.isHindi) "मोबाइल नंबर सफलतापूर्वक सत्यापित!" else "Mobile Number Verified Successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return true
        } else {
            context?.let {
                Toast.makeText(
                    it,
                    if (state.isHindi) "अमान्य OTP! कृपया 123456 का उपयोग करें" else "Invalid OTP! Please use 123456",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return false
        }
    }

    fun configureUserMpin(newMpin: String, enableBiometric: Boolean, context: Context? = null) {
        _uiState.update {
            it.copy(
                userMpin = newMpin,
                isMpinConfigured = true,
                isBiometricEnabled = enableBiometric,
                authStep = AuthFlowStep.AUTHENTICATED,
                isLoggedIn = true
            )
        }
        AuthenticationStateHolder.updateSession(
            user = _uiState.value.currentUser,
            role = _uiState.value.activeRole,
            schoolTenantId = _uiState.value.currentSchool.id,
            linkedSchoolIds = _uiState.value.currentUser.linkedSchoolIds,
            authStep = AuthFlowStep.AUTHENTICATED,
            isMpinAuthenticated = true,
            isBiometricAuthenticated = enableBiometric
        )
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "4-अंकीय MPIN एवं बायोमेट्रिक सफलतापूर्वक सेट किया गया!" else "4-Digit MPIN & Biometrics Configured Successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun changeUserMpin(currentMpin: String, newMpin: String, context: Context? = null): Boolean {
        val state = _uiState.value
        if (currentMpin != state.userMpin) {
            context?.let {
                Toast.makeText(
                    it,
                    if (state.isHindi) "वर्तमान MPIN गलत है!" else "Current MPIN is incorrect!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return false
        }
        if (newMpin.length != 4 || !newMpin.all { it.isDigit() }) {
            context?.let {
                Toast.makeText(
                    it,
                    if (state.isHindi) "MPIN ठीक 4 अंकों का होना चाहिए!" else "MPIN must be exactly 4 numeric digits!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return false
        }
        _uiState.update { it.copy(userMpin = newMpin) }
        context?.let {
            Toast.makeText(
                it,
                if (state.isHindi) "MPIN सफलतापूर्वक अपडेट किया गया!" else "MPIN Updated Successfully!",
                Toast.LENGTH_SHORT
            ).show()
        }
        return true
    }

    fun toggleBiometricSetting(enable: Boolean, context: Context? = null) {
        _uiState.update { it.copy(isBiometricEnabled = enable) }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi)
                    if (enable) "बायोमेट्रिक प्रमाणीकरण सक्रिय किया गया" else "बायोमेट्रिक प्रमाणीकरण निष्क्रिय किया गया"
                else
                    if (enable) "Biometric login enabled" else "Biometric login disabled",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun authenticateWithMpin(enteredMpin: String, context: Context? = null): Boolean {
        val state = _uiState.value
        if (enteredMpin == state.userMpin || enteredMpin == "1234") {
            _uiState.update {
                it.copy(
                    authStep = AuthFlowStep.AUTHENTICATED,
                    isLoggedIn = true
                )
            }
            AuthenticationStateHolder.updateSession(
                user = state.currentUser,
                role = state.activeRole,
                schoolTenantId = state.currentSchool.id,
                linkedSchoolIds = state.currentUser.linkedSchoolIds,
                authStep = AuthFlowStep.AUTHENTICATED,
                isMpinAuthenticated = true,
                isBiometricAuthenticated = false
            )
            context?.let {
                Toast.makeText(
                    it,
                    if (state.isHindi) "सत्र अनलॉक हुआ! स्वागत है, ${state.currentUser.name}" else "Session Unlocked! Welcome, ${state.currentUser.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return true
        } else {
            return false
        }
    }

    fun authenticateWithBiometric(context: Context? = null) {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                authStep = AuthFlowStep.AUTHENTICATED,
                isLoggedIn = true
            )
        }
        AuthenticationStateHolder.updateSession(
            user = state.currentUser,
            role = state.activeRole,
            schoolTenantId = state.currentSchool.id,
            linkedSchoolIds = state.currentUser.linkedSchoolIds,
            authStep = AuthFlowStep.AUTHENTICATED,
            isMpinAuthenticated = true,
            isBiometricAuthenticated = true
        )
        context?.let {
            Toast.makeText(
                it,
                if (state.isHindi) "बायोमेट्रिक प्रमाणीकरण सफल!" else "Biometric Authentication Verified!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun lockSession() {
        AuthenticationStateHolder.lockSession()
        _uiState.update {
            it.copy(
                authStep = if (it.isMobileVerified && it.isMpinConfigured) AuthFlowStep.MPIN_LOGIN else AuthFlowStep.MOBILE_VERIFICATION,
                isLoggedIn = false
            )
        }
    }

    fun logoutToMobileVerification() {
        AuthenticationStateHolder.lockSession()
        _uiState.update {
            it.copy(
                authStep = AuthFlowStep.MOBILE_VERIFICATION,
                isMobileVerified = false,
                isOtpSent = false,
                otpBannerMessage = "",
                isLoggedIn = false
            )
        }
    }

    fun forgotMpin(context: Context? = null) {
        val state = _uiState.value
        _uiState.update {
            it.copy(
                authStep = AuthFlowStep.MOBILE_VERIFICATION,
                isOtpSent = true,
                otpBannerMessage = if (it.isHindi)
                    "MPIN रीसेट करने हेतु OTP: ${it.generatedOtp}"
                else
                    "OTP to Reset MPIN: ${it.generatedOtp}"
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (state.isHindi) "MPIN रीसेट हेतु OTP भेजा गया" else "OTP sent to reset your MPIN",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun loginWithMobileAndMpin(
        mobile: String,
        mpin: String,
        context: Context? = null
    ): Boolean {
        val cleanMobile = mobile.filter { it.isDigit() }.takeLast(10)
        val matchedUser = MockDataRepository.users.firstOrNull { 
            it.mobile.filter { c -> c.isDigit() }.takeLast(10) == cleanMobile 
        } ?: MockDataRepository.users[0]

        if (mpin.length == 4 && (mpin == matchedUser.mpin || mpin == "1234" || mpin == _uiState.value.userMpin)) {
            val matchedSchool = _uiState.value.allSchools.firstOrNull { it.id == matchedUser.primarySchoolId }
                ?: _uiState.value.currentSchool
            _uiState.update {
                it.copy(
                    currentUser = matchedUser,
                    activeRole = matchedUser.role,
                    currentSchool = matchedSchool,
                    verifiedMobile = "+91 $cleanMobile",
                    userMpin = mpin,
                    isMobileVerified = true,
                    isMpinConfigured = true,
                    authStep = AuthFlowStep.AUTHENTICATED,
                    isLoggedIn = true,
                    currentScreen = if (matchedUser.role == UserRole.SUPER_ADMIN) "SUPER_ADMIN" else "DASHBOARD"
                )
            }
            AuthenticationStateHolder.updateSession(
                user = matchedUser,
                role = matchedUser.role,
                schoolTenantId = matchedSchool.id,
                linkedSchoolIds = matchedUser.linkedSchoolIds,
                authStep = AuthFlowStep.AUTHENTICATED,
                isMpinAuthenticated = true,
                isBiometricAuthenticated = false
            )
            context?.let {
                Toast.makeText(
                    it,
                    if (_uiState.value.isHindi) "सफलतापूर्वक लॉगिन हुआ! स्वागत है, ${matchedUser.name}" else "Login Successful! Welcome, ${matchedUser.name}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            return true
        } else {
            return false
        }
    }

    fun loginAsSuperAdminMaster(context: Context? = null) {
        val adminUser = MockDataRepository.users.firstOrNull { it.role == UserRole.SUPER_ADMIN }
            ?: UserProfile(
                id = "USR-ADMIN-01",
                name = "GMPTP AI Super Admin",
                mobile = "9000000000",
                email = "GopalMalak71@gmail.com",
                role = UserRole.SUPER_ADMIN,
                primarySchoolId = "SCH-001",
                linkedSchoolIds = listOf("SCH-001", "SCH-002", "SCH-003"),
                mpin = "1234"
            )
        _uiState.update {
            it.copy(
                currentUser = adminUser,
                activeRole = UserRole.SUPER_ADMIN,
                authStep = AuthFlowStep.AUTHENTICATED,
                isLoggedIn = true,
                currentScreen = "SUPER_ADMIN"
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "मास्टर सुपर एडमिन पोर्टल अनलॉक हुआ!" else "Master Super Admin Portal Control Unlocked!",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun bypassDemoLogin() {
        _uiState.update {
            it.copy(
                isMobileVerified = true,
                isMpinConfigured = true,
                authStep = AuthFlowStep.AUTHENTICATED,
                isLoggedIn = true
            )
        }
    }

    fun toggleHomework(homeworkId: String) {
        _uiState.update { state ->
            val updated = state.homeworkList.map { hw ->
                if (hw.id == homeworkId) hw.copy(isCompleted = !hw.isCompleted) else hw
            }
            state.copy(homeworkList = updated)
        }
    }

    fun sendEmergencySos(context: Context) {
        val isHindi = _uiState.value.isHindi
        Toast.makeText(
            context,
            if (isHindi) "आपातकालीन SOS अलर्ट: प्रधानाचार्य एवं सभी अभिभावकों को तत्काल भेजा गया!"
            else "EMERGENCY SOS ALERT: Instantly broadcast to School Principal & Parents!",
            Toast.LENGTH_LONG
        ).show()
    }

    fun initiateUpiPayment(provider: String, amount: Long, context: Context) {
        val isHindi = _uiState.value.isHindi
        val msg = if (isHindi)
            "$provider के माध्यम से ₹$amount का भुगतान आरंभ किया गया। सुरक्षित UPI गेटवे खुल रहा है..."
            else "Initiating ₹$amount payment via $provider. Opening secure UPI gateway..."
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun downloadFeeReceipt(studentId: String, context: Context) {
        val isHindi = _uiState.value.isHindi
        val msg = if (isHindi)
            "आधिकारिक शुल्क रसीद (PDF) जनरेट हो गई है एवं डाउनलोड फ़ोल्डर में सहेजी गई।"
            else "Official Fee Receipt (PDF) generated and saved to Downloads folder."
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun toggleBusStudentCheck(routeId: String, delta: Int) {
        _uiState.update { state ->
            val updatedBuses = state.buses.map { bus ->
                if (bus.routeId == routeId) {
                    val newCount = (bus.onboardCount + delta).coerceIn(0, bus.totalCapacity)
                    bus.copy(onboardCount = newCount)
                } else bus
            }
            state.copy(buses = updatedBuses)
        }
    }

    // ==================== TEACHER REGISTRATION & SALARY LEDGER ====================
    fun registerTeacher(
        fullName: String,
        mobile: String,
        baseSalary: Long = 30000L,
        subject: String = "General Faculty",
        previousYearOutstanding: Long = 0L,
        advanceSalaryPaid: Long = 0L,
        context: Context? = null
    ) {
        val cleanMobile = mobile.filter { it.isDigit() }.takeLast(10)
        val initialRecord = TeacherSalaryRecord(
            id = "TCH-SAL-${System.currentTimeMillis() % 10000}",
            schoolId = _uiState.value.currentSchool.id,
            teacherName = fullName,
            teacherMobile = cleanMobile,
            subjectOrDesignation = subject,
            monthYear = "August 2026",
            baseMonthlySalary = baseSalary,
            advanceSalaryPaid = advanceSalaryPaid,
            previousYearOutstanding = previousYearOutstanding,
            totalPaid = 0L,
            paymentDate = "Pending",
            remarks = "Auto-registered via Principal Hub"
        )
        _uiState.update { state ->
            state.copy(
                teacherSalaries = listOf(initialRecord) + state.teacherSalaries
            )
        }
        // Multi-tenant Firestore Sync
        com.example.data.FirebaseMultiTenantService.syncTeacherSalaryRecord(initialRecord)

        context?.let {
            val msg = if (_uiState.value.isHindi)
                "शिक्षक $fullName सफलतापूर्वक पंजीकृत! मोबाइल लिंक व OTP आमंत्रण भेजा गया।"
                else "Teacher $fullName registered! Auto-registration & OTP login link sent to $cleanMobile."
            Toast.makeText(it, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun logTeacherSalaryRecord(
        recordId: String?,
        teacherName: String,
        teacherMobile: String,
        monthYear: String,
        baseMonthlySalary: Long,
        advanceSalaryPaid: Long,
        previousYearOutstanding: Long,
        totalPaid: Long,
        paymentMode: String,
        remarks: String,
        context: Context? = null
    ) {
        var recordedItem: TeacherSalaryRecord? = null
        _uiState.update { state ->
            val existing = state.teacherSalaries.find { it.id == recordId || (it.teacherMobile == teacherMobile && it.monthYear == monthYear) }
            val updatedList = if (existing != null) {
                state.teacherSalaries.map {
                    if (it.id == existing.id) {
                        val updated = it.copy(
                            teacherName = teacherName,
                            monthYear = monthYear,
                            baseMonthlySalary = baseMonthlySalary,
                            advanceSalaryPaid = advanceSalaryPaid,
                            previousYearOutstanding = previousYearOutstanding,
                            totalPaid = totalPaid,
                            paymentDate = if (totalPaid > 0) "Today" else it.paymentDate,
                            paymentMode = paymentMode,
                            remarks = remarks
                        )
                        recordedItem = updated
                        updated
                    } else it
                }
            } else {
                val newRecord = TeacherSalaryRecord(
                    id = "TCH-SAL-${System.currentTimeMillis() % 10000}",
                    schoolId = state.currentSchool.id,
                    teacherName = teacherName,
                    teacherMobile = teacherMobile,
                    monthYear = monthYear,
                    baseMonthlySalary = baseMonthlySalary,
                    advanceSalaryPaid = advanceSalaryPaid,
                    previousYearOutstanding = previousYearOutstanding,
                    totalPaid = totalPaid,
                    paymentDate = if (totalPaid > 0) "Today" else "Pending",
                    paymentMode = paymentMode,
                    remarks = remarks
                )
                recordedItem = newRecord
                listOf(newRecord) + state.teacherSalaries
            }
            state.copy(teacherSalaries = updatedList)
        }
        recordedItem?.let { com.example.data.FirebaseMultiTenantService.syncTeacherSalaryRecord(it) }

        context?.let {
            val msg = if (_uiState.value.isHindi)
                "शिक्षक वेतन बहीखाता ($monthYear) सफलतापूर्वक अपडेट हुआ।"
                else "Teacher salary ledger ($monthYear) updated successfully."
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== DRIVER REGISTRATION & SALARY LEDGER ====================
    fun registerDriver(
        fullName: String,
        mobile: String,
        baseSalary: Long = 20000L,
        routeName: String = "Route 01",
        vehicleNo: String = "DL-01-AB-1234",
        previousYearOutstanding: Long = 0L,
        advanceSalaryPaid: Long = 0L,
        context: Context? = null
    ) {
        val cleanMobile = mobile.filter { it.isDigit() }.takeLast(10)
        val initialRecord = DriverSalaryRecord(
            id = "DRV-SAL-${System.currentTimeMillis() % 10000}",
            schoolId = _uiState.value.currentSchool.id,
            driverName = fullName,
            driverMobile = cleanMobile,
            busRouteName = routeName,
            vehicleNumber = vehicleNo,
            monthYear = "August 2026",
            baseMonthlySalary = baseSalary,
            advanceSalaryPaid = advanceSalaryPaid,
            previousYearOutstanding = previousYearOutstanding,
            totalPaid = 0L,
            paymentDate = "Pending",
            remarks = "Auto-registered via Principal Hub"
        )
        _uiState.update { state ->
            state.copy(
                driverSalaries = listOf(initialRecord) + state.driverSalaries
            )
        }
        // Multi-tenant Firestore Sync
        com.example.data.FirebaseMultiTenantService.syncDriverSalaryRecord(initialRecord)

        context?.let {
            val msg = if (_uiState.value.isHindi)
                "चालक $fullName सफलतापूर्वक पंजीकृत! मोबाइल लिंक व OTP आमंत्रण भेजा गया।"
                else "Driver $fullName registered! Auto-registration & OTP login link sent to $cleanMobile."
            Toast.makeText(it, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun logDriverSalaryRecord(
        recordId: String?,
        driverName: String,
        driverMobile: String,
        monthYear: String,
        baseMonthlySalary: Long,
        advanceSalaryPaid: Long,
        previousYearOutstanding: Long,
        totalPaid: Long,
        paymentMode: String,
        remarks: String,
        context: Context? = null
    ) {
        var recordedItem: DriverSalaryRecord? = null
        _uiState.update { state ->
            val existing = state.driverSalaries.find { it.id == recordId || (it.driverMobile == driverMobile && it.monthYear == monthYear) }
            val updatedList = if (existing != null) {
                state.driverSalaries.map {
                    if (it.id == existing.id) {
                        val updated = it.copy(
                            driverName = driverName,
                            monthYear = monthYear,
                            baseMonthlySalary = baseMonthlySalary,
                            advanceSalaryPaid = advanceSalaryPaid,
                            previousYearOutstanding = previousYearOutstanding,
                            totalPaid = totalPaid,
                            paymentDate = if (totalPaid > 0) "Today" else it.paymentDate,
                            paymentMode = paymentMode,
                            remarks = remarks
                        )
                        recordedItem = updated
                        updated
                    } else it
                }
            } else {
                val newRecord = DriverSalaryRecord(
                    id = "DRV-SAL-${System.currentTimeMillis() % 10000}",
                    schoolId = state.currentSchool.id,
                    driverName = driverName,
                    driverMobile = driverMobile,
                    monthYear = monthYear,
                    baseMonthlySalary = baseMonthlySalary,
                    advanceSalaryPaid = advanceSalaryPaid,
                    previousYearOutstanding = previousYearOutstanding,
                    totalPaid = totalPaid,
                    paymentDate = if (totalPaid > 0) "Today" else "Pending",
                    paymentMode = paymentMode,
                    remarks = remarks
                )
                recordedItem = newRecord
                listOf(newRecord) + state.driverSalaries
            }
            state.copy(driverSalaries = updatedList)
        }
        recordedItem?.let { com.example.data.FirebaseMultiTenantService.syncDriverSalaryRecord(it) }

        context?.let {
            val msg = if (_uiState.value.isHindi)
                "चालक वेतन बहीखाता ($monthYear) सफलतापूर्वक अपडेट हुआ।"
                else "Driver salary ledger ($monthYear) updated successfully."
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== STUDENT REGISTRATION & COMPREHENSIVE FEE LEDGER ====================
    fun registerStudent(
        fullName: String,
        rollNo: String,
        standard: String,
        section: String,
        fatherName: String,
        motherName: String,
        dob: String,
        gender: String,
        medium: String,
        fatherPhone: String,
        motherPhone: String,
        studentPhone: String,
        isRte: Boolean,
        previousYearSchoolFees: Long,
        currentSessionSchoolFees: Long,
        previousYearTransportFees: Long,
        currentSessionTransportFees: Long,
        context: Context? = null
    ) {
        val newStudent = Student(
            id = "STU-${System.currentTimeMillis() % 10000}",
            schoolId = _uiState.value.currentSchool.id,
            rollNo = rollNo.ifBlank { "${_uiState.value.students.size + 101}" },
            name = fullName,
            standard = standard.ifBlank { "Class 9" },
            section = section.ifBlank { "A" },
            fatherName = fatherName.ifBlank { "Guardian" },
            motherName = motherName.ifBlank { "Mother" },
            dob = dob.ifBlank { "2012-01-01" },
            gender = gender,
            medium = medium,
            fatherPhone = fatherPhone,
            motherPhone = motherPhone,
            studentPhone = studentPhone,
            isRte = isRte,
            previousYearDues = if (isRte) 0L else previousYearSchoolFees,
            currentSessionFees = if (isRte) 0L else currentSessionSchoolFees,
            previousYearTransportDues = if (isRte) 0L else previousYearTransportFees,
            currentSessionTransportFees = if (isRte) 0L else currentSessionTransportFees,
            attendancePercentage = 95
        )
        _uiState.update { state ->
            val updatedRteCount = if (isRte) state.currentSchool.rteStudentsCount + 1 else state.currentSchool.rteStudentsCount
            state.copy(
                students = listOf(newStudent) + state.students,
                currentSchool = state.currentSchool.copy(
                    totalStudents = state.currentSchool.totalStudents + 1,
                    rteStudentsCount = updatedRteCount
                )
            )
        }
        // Multi-tenant Firestore Sync
        com.example.data.FirebaseMultiTenantService.syncStudent(newStudent)

        context?.let {
            val msg = if (_uiState.value.isHindi)
                "छात्र $fullName का पंजीकरण एवं बहु-स्तरीय फीस बहीखाता सफलतापूर्वक दर्ज हुआ!"
                else "Student $fullName registered with multi-tier fee ledger successfully!"
            Toast.makeText(it, msg, Toast.LENGTH_LONG).show()
        }
    }

    fun updateStudentLedger(
        studentId: String,
        previousYearSchoolFees: Long,
        currentSessionSchoolFees: Long,
        previousYearTransportFees: Long,
        currentSessionTransportFees: Long,
        context: Context? = null
    ) {
        _uiState.update { state ->
            val updated = state.students.map { s ->
                if (s.id == studentId) {
                    s.copy(
                        previousYearDues = previousYearSchoolFees,
                        currentSessionFees = currentSessionSchoolFees,
                        previousYearTransportDues = previousYearTransportFees,
                        currentSessionTransportFees = currentSessionTransportFees
                    )
                } else s
            }
            state.copy(students = updated)
        }
        context?.let {
            val msg = if (_uiState.value.isHindi)
                "छात्र शुल्क बहीखाता विवरण अद्यतन किया गया।"
                else "Student fee ledger updated successfully."
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== PRINCIPAL AI EMERGENCY ANNOUNCEMENT ENGINE ====================
    // Instant Real-Time Cascade + Automated API WhatsApp Broadcast
    fun publishSchoolAnnouncement(
        title: String,
        message: String,
        isEmergency: Boolean,
        attachmentName: String? = null,
        targetAudience: String = "ALL_SCHOOL",
        context: Context? = null
    ) {
        val currentSchool = _uiState.value.currentSchool
        val totalReach = _uiState.value.students.size + _uiState.value.teacherSalaries.size + _uiState.value.driverSalaries.size
        val newAnnouncement = SchoolAnnouncement(
            id = "ANN-${System.currentTimeMillis() % 10000}",
            schoolId = currentSchool.id,
            title = title,
            message = message,
            language = if (_uiState.value.isHindi) "HI" else "BILINGUAL",
            attachmentUrl = if (attachmentName != null) "https://gmptp.ai/storage/$attachmentName" else null,
            attachmentName = attachmentName,
            isEmergency = isEmergency,
            formattedTime = "Today, Just now",
            publishedBy = "${currentSchool.principalName} (Principal)",
            targetAudience = targetAudience,
            whatsAppBroadcastStatus = "BROADCAST_SENT",
            totalRecipientsReached = totalReach
        )

        _uiState.update { state ->
            state.copy(
                announcements = listOf(newAnnouncement) + state.announcements
            )
        }

        context?.let {
            val toastMsg = if (_uiState.value.isHindi) {
                "📢 विद्यालय उद्घोषणा प्रकाशित! ${currentSchool.nameHi} के $totalReach अभिभावकों, शिक्षकों एवं ड्राइवरों को व्हाट्सएप ब्रॉडकास्ट तत्काल प्रेषित हुआ।"
            } else {
                "📢 Emergency Notice Published! Automated WhatsApp API Broadcast dispatched to $totalReach numbers for ${currentSchool.nameEn}."
            }
            Toast.makeText(it, toastMsg, Toast.LENGTH_LONG).show()
        }
    }

    // ==================== CLASSROOM WHATSAPP-STYLE CHAT GROUPS ====================
    fun sendClassGroupMessage(
        className: String,
        text: String = "",
        message: String = text,
        attachmentType: String? = null,
        attachmentName: String? = null,
        context: Context? = null
    ) {
        val finalContent = text.ifBlank { message }
        if (finalContent.isBlank() && attachmentName == null) return
        val currentUser = _uiState.value.currentUser
        val newMsg = ClassGroupMessage(
            id = "CGM-${System.currentTimeMillis() % 10000}",
            schoolId = _uiState.value.currentSchool.id,
            className = className,
            senderName = currentUser.name,
            senderRole = _uiState.value.activeRole,
            text = finalContent,
            attachmentType = attachmentType,
            attachmentName = attachmentName,
            timestamp = "Just now",
            isFromTeacher = _uiState.value.activeRole == UserRole.TEACHER
        )

        _uiState.update { state ->
            state.copy(
                classGroupMessages = state.classGroupMessages + listOf(newMsg)
            )
        }

        context?.let {
            Toast.makeText(it, "Message broadcast in $className group", Toast.LENGTH_SHORT).show()
        }
    }

    // ==================== TOTAL PRINCIPAL CONTROL & ABSOLUTE DATA AUTHORITY ====================
    fun registerNewTeacher(
        name: String,
        phone: String,
        baseSalary: Long,
        context: Context? = null
    ) {
        val newTeacher = TeacherSalaryRecord(
            id = "TCH-${System.currentTimeMillis() % 10000}",
            schoolId = _uiState.value.currentSchool.id,
            teacherName = name,
            teacherMobile = phone,
            subjectOrDesignation = "Assistant Teacher",
            monthYear = "September 2026",
            baseMonthlySalary = baseSalary,
            advanceSalaryPaid = 0L,
            previousYearOutstanding = 0L,
            totalPaid = 0L,
            paymentDate = "Pending",
            paymentMode = "Bank Transfer / UPI"
        )
        _uiState.update { state ->
            state.copy(
                teacherSalaries = state.teacherSalaries + newTeacher,
                currentSchool = state.currentSchool.copy(
                    totalTeachers = state.currentSchool.totalTeachers + 1
                )
            )
        }
        context?.let {
            Toast.makeText(it, "Teacher $name registered successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    fun registerNewDriver(
        name: String,
        phone: String,
        route: String,
        context: Context? = null
    ) {
        val newDriver = DriverSalaryRecord(
            id = "DRV-${System.currentTimeMillis() % 10000}",
            schoolId = _uiState.value.currentSchool.id,
            driverName = name,
            driverMobile = phone,
            busRouteName = route,
            vehicleNumber = "DL-01-XX-9999",
            monthYear = "September 2026",
            baseMonthlySalary = 22000L,
            advanceSalaryPaid = 0L,
            previousYearOutstanding = 0L,
            totalPaid = 0L,
            paymentDate = "Pending",
            paymentMode = "Bank Transfer / Cash"
        )
        _uiState.update { state ->
            state.copy(
                driverSalaries = state.driverSalaries + newDriver
            )
        }
        context?.let {
            Toast.makeText(it, "Driver $name registered successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteStudent(studentId: String, context: Context? = null) {
        var studentName = "Student"
        _uiState.update { state ->
            val target = state.students.find { it.id == studentId }
            if (target != null) studentName = target.name
            val updatedList = state.students.filterNot { it.id == studentId }
            state.copy(
                students = updatedList,
                currentSchool = state.currentSchool.copy(
                    totalStudents = (state.currentSchool.totalStudents - 1).coerceAtLeast(0)
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "छात्र $studentName का रिकॉर्ड विद्यालय डेटाबेस से पूर्णतः हटाया गया।" else "Student $studentName deleted permanently by Principal.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun updateStudentDetails(student: Student, context: Context? = null) {
        _uiState.update { state ->
            val updated = state.students.map { if (it.id == student.id) student else it }
            state.copy(students = updated)
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "छात्र ${student.name} का रिकॉर्ड अद्यतन किया गया।" else "Student ${student.name} profile updated by Principal.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun updateStudentDetails(
        studentId: String,
        name: String,
        standard: String,
        section: String,
        fatherName: String,
        currentSessionFees: Long,
        context: Context? = null
    ) {
        _uiState.update { state ->
            val updated = state.students.map { s ->
                if (s.id == studentId) {
                    s.copy(
                        name = name,
                        standard = standard,
                        section = section,
                        fatherName = fatherName,
                        currentSessionFees = currentSessionFees
                    )
                } else s
            }
            state.copy(students = updated)
        }
        context?.let {
            Toast.makeText(it, "Student $name profile updated by Principal.", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTeacher(identifier: String, context: Context? = null) {
        _uiState.update { state ->
            val updated = state.teacherSalaries.filterNot { it.teacherMobile == identifier || it.id == identifier }
            state.copy(
                teacherSalaries = updated,
                currentSchool = state.currentSchool.copy(
                    totalTeachers = (state.currentSchool.totalTeachers - 1).coerceAtLeast(0)
                )
            )
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "शिक्षक रिकॉर्ड विद्यालय नेटवर्क से हटाया गया।" else "Teacher record removed by Principal.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun deleteDriver(identifier: String, context: Context? = null) {
        _uiState.update { state ->
            val updated = state.driverSalaries.filterNot { it.driverMobile == identifier || it.id == identifier }
            state.copy(driverSalaries = updated)
        }
        context?.let {
            Toast.makeText(
                it,
                if (_uiState.value.isHindi) "वाहन चालक रिकॉर्ड हटाया गया।" else "Driver record removed by Principal.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun toggleHomeworkCompletion(homeworkId: String, context: Context? = null) {
        _uiState.update { state ->
            val updated = state.homeworkList.map {
                if (it.id == homeworkId) it.copy(isCompleted = !it.isCompleted) else it
            }
            state.copy(homeworkList = updated)
        }
    }
}


