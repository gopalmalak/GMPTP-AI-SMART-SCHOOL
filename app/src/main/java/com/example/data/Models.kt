package com.example.data

enum class UserRole(val labelEn: String, val labelHi: String) {
    PRINCIPAL("School Principal", "विद्यालय प्रधानाचार्य"),
    TEACHER("Teacher", "अध्यापक / शिक्षिका"),
    PARENT_STUDENT("Parent & Student", "अभिभावक एवं छात्र"),
    DRIVER("Transport Driver", "वाहन चालक / ड्राइवर"),
    SUPER_ADMIN("Super Admin", "सुपर व्यवस्थापक")
}

data class School(
    val id: String,
    val code: String,
    val nameEn: String,
    val nameHi: String,
    val state: String,
    val district: String,
    val block: String,
    val isUrban: Boolean = false,
    val areaName: String = "",
    val wardOrVillage: String = "",
    val principalName: String,
    val principalMobile: String,
    val trialDaysRemaining: Int = 14,
    val hasPaidSubscription: Boolean = false,
    val subscriptionPlan: String = "Free Trial",
    val totalStudents: Int = 420,
    val totalTeachers: Int = 24,
    val schoolFeesCollected: Long = 684000L,
    val transportFeesCollected: Long = 215000L,
    val rteStudentsCount: Int = 38
)

data class UserProfile(
    val id: String,
    val name: String,
    val mobile: String,
    val email: String,
    val role: UserRole,
    val primarySchoolId: String,
    val linkedSchoolIds: List<String> = emptyList(), // Multi-school teacher mapping!
    val mpin: String = "1234",
    val biometricEnabled: Boolean = true,
    val assignedClasses: List<String> = listOf("Class 8-A", "Class 9-B", "Class 10-A")
)

data class Student(
    val id: String,
    val schoolId: String,
    val rollNo: String,
    val name: String,
    val standard: String,
    val section: String,
    val fatherName: String = "Suresh Kumar",
    val motherName: String = "Anita Devi",
    val dob: String = "2011-08-15",
    val gender: String = "Male",
    val medium: String = "HINDI", // "HINDI" (हिंदी माध्यम) or "ENGLISH" (इंग्लिश माध्यम)
    val fatherPhone: String = "",
    val motherPhone: String = "",
    val studentPhone: String = "",
    val isRte: Boolean = false, // RTE Free Education Toggle
    val previousYearDues: Long = 0L,
    val currentSessionFees: Long = 24000L,
    val previousYearTransportDues: Long = 0L,
    val currentSessionTransportFees: Long = 8500L,
    val busRouteId: String = "ROUTE-01",
    val attendancePercentage: Int = 92
)

data class FeePaymentRecord(
    val id: String,
    val schoolId: String,
    val studentId: String,
    val studentName: String,
    val standard: String,
    val amount: Long,
    val feeCategory: String, // "School Fee", "Transport Fee", "Previous Dues"
    val collectedByTeacher: String,
    val status: FeeApprovalStatus,
    val date: String,
    val timestamp: Long = System.currentTimeMillis(),
    val receiptNumber: String
)

enum class FeeApprovalStatus(val labelEn: String, val labelHi: String) {
    PENDING_VERIFICATION("Pending Approval", "सत्यापन हेतु लंबित"),
    APPROVED("Approved & Credited", "स्वीकृत एवं जमा")
}

enum class AttendanceStatus(val code: String, val labelEn: String, val labelHi: String) {
    PRESENT("P", "Present", "उपस्थित"),
    ABSENT("A", "Absent", "अनुपस्थित"),
    LEAVE("L", "On Leave", "अवकाश"),
    LATE("LT", "Late", "देरी से")
}

data class DailyAttendance(
    val studentId: String,
    val studentName: String,
    val rollNo: String,
    val status: AttendanceStatus,
    val date: String, // YYYY-MM-DD
    val monthYear: String, // YYYY-MM
    val markedBy: String
)

data class BusVehicle(
    val routeId: String,
    val schoolId: String,
    val vehicleNumber: String,
    val driverName: String,
    val driverPhone: String,
    val routeName: String,
    val isLive: Boolean = false,
    val currentLatitude: Double = 28.6139,
    val currentLongitude: Double = 77.2090,
    val speedKmh: Int = 32,
    val currentStop: String = "City Center Crossing",
    val nextStop: String = "Sector 4 Metro Pillar 12",
    val etaMinutes: Int = 8,
    val totalCapacity: Int = 45,
    val onboardCount: Int = 34
)

data class SuvicharQuote(
    val id: String,
    val quoteEn: String,
    val quoteHi: String,
    val authorEn: String,
    val authorHi: String,
    val dateTag: String,
    val backgroundGradientIndex: Int = 0
)

data class GalleryItem(
    val id: String,
    val schoolId: String,
    val title: String,
    val year: String,
    val month: String,
    val date: String,
    val category: String, // "Annual Day", "Sports Meet", "Science Fair", "Independence Day"
    val isVideo: Boolean = false
)

data class RubricCriteria(
    val nameEn: String,
    val nameHi: String,
    val maxScore: Int,
    var awardedScore: Int,
    var comments: String
)

data class ExamGradingResult(
    val studentName: String,
    val rollNo: String,
    val subject: String,
    val criteriaList: List<RubricCriteria>,
    val teacherOverrideScore: Int? = null,
    val isAccepted: Boolean = false,
    val teacherNote: String = ""
)

data class SuperAdminConfig(
    val trialDurationDays: Int = 14,
    val isAdMobEnabledGlobally: Boolean = true,
    val adBannerEnabled: Boolean = true,
    val adInterstitialEnabled: Boolean = true,
    val adRewardedEnabled: Boolean = true,
    val adNativeAdvancedEnabled: Boolean = true,
    val principalAdsBlockedDuringTrial: Boolean = true,
    val admobAppId: String = "ca-app-pub-3940256099942544~3347511713",
    val admobBannerUnitId: String = "ca-app-pub-3940256099942544/6300978111",
    val admobInterstitialUnitId: String = "ca-app-pub-3940256099942544/1033173712",
    val admobRewardedUnitId: String = "ca-app-pub-3940256099942544/5224354917",
    val adTestMode: Boolean = true,
    val interstitialIntervalClicks: Int = 3,
    val adImpressionsToday: Int = 14280,
    val adClicksToday: Int = 412,
    val adEstimatedRevenueTodayInr: Double = 846.50,
    val adEcpmInr: Double = 59.28,
    val activeAccentColorHex: String = "#FFD700",
    val activeThemeTemplate: String = "ROYAL_PURPLE_GOLD", // "ROYAL_PURPLE_GOLD", "DEEP_NAVY_AMBER", "IMPERIAL_EMERALD", "RUBY_SUNSET"
    val monthlyRate: Long = 999L,
    val sixMonthRate: Long = 4999L,
    val yearlyRate: Long = 8999L,
    val dynamicModules: Map<String, Boolean> = mapOf(
        "AI_EXAM_SCANNER" to true,
        "GPS_TRACKING" to true,
        "RTE_LEDGER" to true,
        "AI_SUVICHAR" to true,
        "MEDIA_GALLERY" to true,
        "MULTI_SCHOOL_SWITCHING" to true,
        "STAFF_REPLACEMENT" to true
    ),
    val activeCouponCode: String = "GMPTP100",
    val couponDiscountPercent: Int = 20
)

data class PendingPrincipalApproval(
    val id: String,
    val principalName: String,
    val mobile: String,
    val email: String,
    val schoolNameEn: String,
    val schoolNameHi: String,
    val schoolCode: String,
    val district: String,
    val state: String,
    val submissionDate: String = "Today, 09:30 AM",
    val isApproved: Boolean = false
)

data class GovtLocationFilter(
    val state: String,
    val district: String,
    val block: String,
    val isRural: Boolean,
    val panchayat: String = "",
    val village: String = "",
    val nagarPalika: String = "",
    val wardNumber: String = ""
)

data class TeacherFeedItem(
    val id: String,
    val teacherName: String,
    val actionType: String, // "ATTENDANCE", "MARKS", "FEE"
    val messageEn: String,
    val messageHi: String,
    val timestamp: String = "Just now",
    val timeAgo: String = "Just now"
)

data class HomeworkItem(
    val id: String,
    val subject: String,
    val title: String,
    val description: String,
    val dueDate: String,
    val assignedBy: String,
    val isCompleted: Boolean = false
)

data class ReportCardSummary(
    val examName: String,
    val totalMarks: Int,
    val marksObtained: Int,
    val percentage: Double,
    val grade: String,
    val remarks: String
)

data class DigitalNotice(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val date: String,
    val priority: String, // "HIGH", "NORMAL"
    val contentEn: String = "",
    val contentHi: String = ""
)

data class TeacherSalaryRecord(
    val id: String,
    val schoolId: String,
    val teacherName: String,
    val teacherMobile: String,
    val subjectOrDesignation: String = "TGT Science & Math",
    val monthYear: String, // e.g., "August 2026", "September 2026"
    val baseMonthlySalary: Long = 32000L,
    val advanceSalaryPaid: Long = 0L,
    val previousYearOutstanding: Long = 0L,
    val totalPaid: Long = 0L,
    val paymentDate: String = "Pending",
    val paymentMode: String = "Bank Transfer / UPI",
    val remarks: String = ""
) {
    val name: String get() = teacherName
    val phone: String get() = teacherMobile
    val monthlyBaseSalary: Long get() = baseMonthlySalary

    // Remaining Balance = (Base Salary + Previous Year Dues) - (Advance + Total Paid)
    val remainingBalance: Long
        get() = (baseMonthlySalary + previousYearOutstanding) - (advanceSalaryPaid + totalPaid)
}

data class DriverSalaryRecord(
    val id: String,
    val schoolId: String,
    val driverName: String,
    val driverMobile: String,
    val busRouteName: String = "Route 01 - Hauz Khas",
    val vehicleNumber: String = "DL-01-AB-4567",
    val monthYear: String, // e.g., "August 2026", "September 2026"
    val baseMonthlySalary: Long = 22000L,
    val advanceSalaryPaid: Long = 0L,
    val previousYearOutstanding: Long = 0L,
    val totalPaid: Long = 0L,
    val paymentDate: String = "Pending",
    val paymentMode: String = "Bank Transfer / Cash",
    val remarks: String = ""
) {
    val name: String get() = driverName
    val phone: String get() = driverMobile
    val routeAssigned: String get() = busRouteName

    // Remaining Balance = (Base Salary + Previous Year Dues) - (Advance + Total Paid)
    val remainingBalance: Long
        get() = (baseMonthlySalary + previousYearOutstanding) - (advanceSalaryPaid + totalPaid)
}

data class SchoolAnnouncement(
    val id: String,
    val schoolId: String,
    val title: String,
    val message: String,
    val language: String = "BILINGUAL", // "EN", "HI", "BILINGUAL"
    val attachmentUrl: String? = null,
    val attachmentName: String? = null,
    val isEmergency: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = "Today, 10:45 AM",
    val publishedBy: String = "Principal (प्रधानाचार्य)",
    val targetAudience: String = "ALL_SCHOOL", // "ALL_SCHOOL", "TEACHERS", "PARENTS", "DRIVERS"
    val recipientRoles: List<UserRole> = listOf(UserRole.TEACHER, UserRole.PARENT_STUDENT, UserRole.DRIVER),
    val whatsAppBroadcastStatus: String = "BROADCAST_SENT", // "BROADCAST_SENT", "DELIVERED"
    val totalRecipientsReached: Int = 184
) {
    val senderName: String get() = publishedBy
}

data class ClassGroupMessage(
    val id: String,
    val schoolId: String,
    val className: String, // e.g. "Class 9 - Section B"
    val senderName: String,
    val senderRole: UserRole,
    val text: String,
    val attachmentType: String? = null, // "PDF", "IMAGE", "NOTE", null
    val attachmentName: String? = null,
    val timestamp: String = "10:30 AM",
    val isFromTeacher: Boolean = true
) {
    val message: String get() = text
    val formattedTime: String get() = timestamp
}

data class LeaveRequest(
    val id: String,
    val applicantName: String,
    val applicantRole: String, // "Teacher" or "Student"
    val leaveType: String, // "Casual" or "Emergency"
    val fromDate: String,
    val toDate: String,
    val reason: String,
    val status: String = "PENDING" // "PENDING", "ACCEPTED", "REJECTED"
)

data class TimetablePeriod(
    val id: String,
    val standard: String,
    val section: String,
    val dayOfWeek: String,
    val periodNumber: Int,
    val subject: String,
    val teacherName: String,
    val startTime: String,
    val endTime: String
)



