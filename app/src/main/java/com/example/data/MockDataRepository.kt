package com.example.data

object MockDataRepository {

    val schools = listOf(
        School(
            id = "SCH-001",
            code = "DPA-101",
            nameEn = "Delhi Public Academy",
            nameHi = "दिल्ली पब्लिक एकेडमी",
            state = "Delhi NCR",
            district = "South Delhi",
            block = "Hauz Khas",
            isUrban = true,
            areaName = "South Zone Nagar Nigam",
            wardOrVillage = "Ward No. 14",
            principalName = "Dr. Alok Verma",
            principalMobile = "9876543210",
            trialDaysRemaining = 11,
            hasPaidSubscription = false,
            subscriptionPlan = "14-Day Free Trial",
            totalStudents = 540,
            totalTeachers = 32,
            schoolFeesCollected = 890000L,
            transportFeesCollected = 320000L,
            rteStudentsCount = 45
        ),
        School(
            id = "SCH-002",
            code = "XAV-202",
            nameEn = "St. Xavier Model School",
            nameHi = "सेंट जेवियर्स मॉडल स्कूल",
            state = "Uttar Pradesh",
            district = "Lucknow",
            block = "Sarojini Nagar",
            isUrban = false,
            areaName = "Banthra Gram Panchayat",
            wardOrVillage = "Village Miranpur",
            principalName = "Sister Maria Joseph",
            principalMobile = "9876500001",
            trialDaysRemaining = 0,
            hasPaidSubscription = true,
            subscriptionPlan = "Yearly Enterprise",
            totalStudents = 680,
            totalTeachers = 42,
            schoolFeesCollected = 1420000L,
            transportFeesCollected = 460000L,
            rteStudentsCount = 60
        ),
        School(
            id = "SCH-003",
            code = "SRI-303",
            nameEn = "Sunrise International Vidyalaya",
            nameHi = "सनराइज इंटरनेशनल विद्यालय",
            state = "Rajasthan",
            district = "Jaipur",
            block = "Sanganer",
            isUrban = true,
            areaName = "Jaipur Greater Nagar Nigam",
            wardOrVillage = "Ward No. 36",
            principalName = "R. K. Shekhawat",
            principalMobile = "9876500002",
            trialDaysRemaining = 14,
            hasPaidSubscription = false,
            subscriptionPlan = "14-Day Free Trial",
            totalStudents = 320,
            totalTeachers = 18,
            schoolFeesCollected = 510000L,
            transportFeesCollected = 180000L,
            rteStudentsCount = 28
        )
    )

    // Preloaded users
    val users = listOf(
        UserProfile(
            id = "USR-PRIN-01",
            name = "Dr. Alok Verma",
            mobile = "9876543210",
            email = "principal.dpa@gmptp.ai",
            role = UserRole.PRINCIPAL,
            primarySchoolId = "SCH-001",
            linkedSchoolIds = listOf("SCH-001"),
            mpin = "1234"
        ),
        UserProfile(
            id = "USR-TCH-01",
            name = "Prof. Rajesh Sharma",
            mobile = "9811223344",
            email = "rajesh.sharma@gmptp.ai",
            role = UserRole.TEACHER,
            primarySchoolId = "SCH-001",
            linkedSchoolIds = listOf("SCH-001", "SCH-002", "SCH-003"), // Multi-school teacher!
            mpin = "1234",
            assignedClasses = listOf("Class 8-A", "Class 9-B", "Class 10-A")
        ),
        UserProfile(
            id = "USR-PAR-01",
            name = "Suresh Patel (Parent of Aarav)",
            mobile = "9988776655",
            email = "suresh.patel@gmail.com",
            role = UserRole.PARENT_STUDENT,
            primarySchoolId = "SCH-001",
            linkedSchoolIds = listOf("SCH-001"),
            mpin = "1234"
        ),
        UserProfile(
            id = "USR-DRV-01",
            name = "Balwinder Singh",
            mobile = "9711002233",
            email = "driver.route1@gmptp.ai",
            role = UserRole.DRIVER,
            primarySchoolId = "SCH-001",
            linkedSchoolIds = listOf("SCH-001"),
            mpin = "1234"
        ),
        UserProfile(
            id = "USR-ADMIN-01",
            name = "GMPTP AI Super Admin",
            mobile = "9000000000",
            email = "admin@gmptp.ai",
            role = UserRole.SUPER_ADMIN,
            primarySchoolId = "SCH-001",
            linkedSchoolIds = listOf("SCH-001", "SCH-002", "SCH-003"),
            mpin = "1234"
        )
    )

    val sampleStudents = listOf(
        Student(
            id = "STU-101",
            schoolId = "SCH-001",
            rollNo = "101",
            name = "Aarav Patel",
            standard = "Class 9",
            section = "B",
            fatherPhone = "+91 99887 76655",
            motherPhone = "+91 99887 76656",
            studentPhone = "+91 99887 76657",
            isRte = false,
            previousYearDues = 4500L,
            currentSessionFees = 28000L,
            previousYearTransportDues = 1200L,
            currentSessionTransportFees = 9500L,
            busRouteId = "BUS-RT-01",
            attendancePercentage = 94
        ),
        Student(
            id = "STU-102",
            schoolId = "SCH-001",
            rollNo = "102",
            name = "Kunal Bharti (RTE)",
            standard = "Class 9",
            section = "B",
            fatherPhone = "+91 98711 22334",
            motherPhone = "",
            studentPhone = "",
            isRte = true, // Right To Education: ZERO Fee
            previousYearDues = 0L,
            currentSessionFees = 0L,
            previousYearTransportDues = 0L,
            currentSessionTransportFees = 0L,
            busRouteId = "BUS-RT-01",
            attendancePercentage = 96
        ),
        Student(
            id = "STU-103",
            schoolId = "SCH-001",
            rollNo = "103",
            name = "Ananya Mishra",
            standard = "Class 9",
            section = "B",
            fatherPhone = "+91 94150 99881",
            motherPhone = "+91 94150 99882",
            studentPhone = "",
            isRte = false,
            previousYearDues = 0L,
            currentSessionFees = 28000L,
            previousYearTransportDues = 0L,
            currentSessionTransportFees = 9500L,
            busRouteId = "BUS-RT-01",
            attendancePercentage = 91
        ),
        Student(
            id = "STU-104",
            schoolId = "SCH-001",
            rollNo = "104",
            name = "Pooja Devi (RTE)",
            standard = "Class 9",
            section = "B",
            fatherPhone = "+91 91234 56789",
            motherPhone = "",
            studentPhone = "",
            isRte = true, // RTE Free Education
            previousYearDues = 0L,
            currentSessionFees = 0L,
            previousYearTransportDues = 0L,
            currentSessionTransportFees = 0L,
            busRouteId = "BUS-RT-01",
            attendancePercentage = 98
        ),
        Student(
            id = "STU-105",
            schoolId = "SCH-001",
            rollNo = "105",
            name = "Rohan Sengupta",
            standard = "Class 9",
            section = "B",
            fatherPhone = "+91 98300 11223",
            motherPhone = "+91 98300 11224",
            studentPhone = "+91 98300 11225",
            isRte = false,
            previousYearDues = 2000L,
            currentSessionFees = 28000L,
            previousYearTransportDues = 800L,
            currentSessionTransportFees = 9500L,
            busRouteId = "BUS-RT-01",
            attendancePercentage = 88
        )
    )

    val sampleFees = listOf(
        FeePaymentRecord(
            id = "FEE-TX-901",
            schoolId = "SCH-001",
            studentId = "STU-101",
            studentName = "Aarav Patel",
            standard = "Class 9-B",
            amount = 4500L,
            feeCategory = "Previous Year Dues",
            collectedByTeacher = "Prof. Rajesh Sharma",
            status = FeeApprovalStatus.PENDING_VERIFICATION,
            date = "Today, 10:45 AM",
            receiptNumber = "GMPTP-REC-2026-901"
        ),
        FeePaymentRecord(
            id = "FEE-TX-902",
            schoolId = "SCH-001",
            studentId = "STU-103",
            studentName = "Ananya Mishra",
            standard = "Class 9-B",
            amount = 14000L,
            feeCategory = "Current Session School Fee (Term 1)",
            collectedByTeacher = "Prof. Rajesh Sharma",
            status = FeeApprovalStatus.PENDING_VERIFICATION,
            date = "Today, 09:30 AM",
            receiptNumber = "GMPTP-REC-2026-902"
        ),
        FeePaymentRecord(
            id = "FEE-TX-880",
            schoolId = "SCH-001",
            studentId = "STU-105",
            studentName = "Rohan Sengupta",
            standard = "Class 9-B",
            amount = 4750L,
            feeCategory = "Current Session Transport Fee",
            collectedByTeacher = "Prof. Rajesh Sharma",
            status = FeeApprovalStatus.APPROVED,
            date = "Yesterday",
            receiptNumber = "GMPTP-REC-2026-880"
        )
    )

    val busVehicles = listOf(
        BusVehicle(
            routeId = "BUS-RT-01",
            schoolId = "SCH-001",
            vehicleNumber = "DL-01-TA-4492",
            driverName = "Balwinder Singh",
            driverPhone = "+91 97110 02233",
            routeName = "Route 01: South Extension - Hauz Khas - Green Park",
            isLive = true,
            currentLatitude = 28.5672,
            currentLongitude = 77.2100,
            speedKmh = 36,
            currentStop = "AIIMS Ring Road Flyover",
            nextStop = "Hauz Khas Market Gate 2",
            etaMinutes = 6,
            totalCapacity = 42,
            onboardCount = 31
        ),
        BusVehicle(
            routeId = "BUS-RT-02",
            schoolId = "SCH-001",
            vehicleNumber = "DL-01-TB-7819",
            driverName = "Manoj Yadav",
            driverPhone = "+91 97110 02255",
            routeName = "Route 02: Saket - Malviya Nagar - IIT Delhi",
            isLive = false,
            currentLatitude = 28.5244,
            currentLongitude = 77.2066,
            speedKmh = 0,
            currentStop = "School Campus Parking Yard",
            nextStop = "Starting at 02:15 PM",
            etaMinutes = 0,
            totalCapacity = 45,
            onboardCount = 0
        ),
        BusVehicle(
            routeId = "BUS-RT-03",
            schoolId = "SCH-001",
            vehicleNumber = "DL-01-TC-9912",
            driverName = "Rakesh Sharma",
            driverPhone = "+91 97110 02277",
            routeName = "Route 03: Vasant Kunj - Munirka - RK Puram",
            isLive = true,
            currentLatitude = 28.5482,
            currentLongitude = 77.1685,
            speedKmh = 29,
            currentStop = "Munirka Metro Gate 3",
            nextStop = "RK Puram Sector 4",
            etaMinutes = 4,
            totalCapacity = 40,
            onboardCount = 28
        )
    )

    val dailySuvicharList = listOf(
        SuvicharQuote(
            id = "SUV-01",
            quoteEn = "Education is the most powerful weapon which you can use to change the world.",
            quoteHi = "शिक्षा वह सबसे शक्तिशाली हथियार है जिसका उपयोग आप दुनिया को बदलने के लिए कर सकते हैं।",
            authorEn = "Nelson Mandela",
            authorHi = "नेल्सन मंडेला",
            dateTag = "Today's Inspiration",
            backgroundGradientIndex = 0
        ),
        SuvicharQuote(
            id = "SUV-02",
            quoteEn = "Vidya Dadati Vinayam, Vinayad Yati Patratam. Knowledge bestows humility; from humility comes worthiness.",
            quoteHi = "विद्या ददाति विनयं, विनयाद्याति पात्रताम्। विद्या मनुष्य को विनम्रता प्रदान करती है और विनम्रता से योग्यता आती है।",
            authorEn = "Hitopadesha",
            authorHi = "हितोपदेश",
            dateTag = "Sanskrit Subhashita",
            backgroundGradientIndex = 1
        ),
        SuvicharQuote(
            id = "SUV-03",
            quoteEn = "Dream, dream, dream. Dreams transform into thoughts and thoughts result in action.",
            quoteHi = "सपने वो नहीं जो हम सोते हुए देखते हैं, सपने वो हैं जो हमें सोने नहीं देते।",
            authorEn = "Dr. A. P. J. Abdul Kalam",
            authorHi = "डॉ. ए. पी. जे. अब्दुल कलाम",
            dateTag = "Daily Motivation",
            backgroundGradientIndex = 2
        )
    )

    val galleryItems = listOf(
        GalleryItem(
            id = "GAL-01",
            schoolId = "SCH-001",
            title = "Annual Science & Robotics Exhibition 2026",
            year = "2026",
            month = "August",
            date = "15 Aug 2026",
            category = "Science Fair"
        ),
        GalleryItem(
            id = "GAL-02",
            schoolId = "SCH-001",
            title = "79th Independence Day Grand Parade & Cultural Fest",
            year = "2026",
            month = "August",
            date = "15 Aug 2026",
            category = "Independence Day"
        ),
        GalleryItem(
            id = "GAL-03",
            schoolId = "SCH-001",
            title = "Inter-School Football Championship Finals",
            year = "2026",
            month = "July",
            date = "28 Jul 2026",
            category = "Sports Meet"
        ),
        GalleryItem(
            id = "GAL-04",
            schoolId = "SCH-001",
            title = "Teachers' Day Felicitation Ceremony",
            year = "2026",
            month = "September",
            date = "05 Sep 2026",
            category = "Festival & Event Logs"
        )
    )

    // Cascading Indian Government Hierarchy
    val statesList = listOf("Delhi NCR", "Uttar Pradesh", "Madhya Pradesh", "Rajasthan", "Bihar", "Maharashtra")
    
    val districtsMap = mapOf(
        "Delhi NCR" to listOf("South Delhi", "New Delhi", "North West Delhi", "East Delhi", "South West Delhi"),
        "Uttar Pradesh" to listOf("Lucknow", "Varanasi", "Prayagraj", "Gautam Buddha Nagar (Noida)", "Kanpur Nagar", "Agra"),
        "Madhya Pradesh" to listOf("Bhopal", "Indore", "Gwalior", "Jabalpur", "Ujjain"),
        "Rajasthan" to listOf("Jaipur", "Jodhpur", "Kota", "Udaipur", "Ajmer"),
        "Bihar" to listOf("Patna", "Gaya", "Bhagalpur", "Muzaffarpur"),
        "Maharashtra" to listOf("Mumbai City", "Pune", "Nagpur", "Thane", "Nashik")
    )

    val blocksMap = mapOf(
        "South Delhi" to listOf("Hauz Khas", "Saket", "Mehrauli", "Kalkaji"),
        "Lucknow" to listOf("Sarojini Nagar", "Bakshi Ka Talab", "Chinhat", "Mohanlalganj", "Malihabad"),
        "Gautam Buddha Nagar (Noida)" to listOf("Bisrakh", "Dankaur", "Jewar"),
        "Jaipur" to listOf("Sanganer", "Amber", "Jhotwara", "Chaksu"),
        "Bhopal" to listOf("Phanda", "Berasia"),
        "Patna" to listOf("Patna Sadar", "Danapur", "Phulwari Sharif", "Fatuha")
    )

    val ruralPanchayatsMap = mapOf(
        "Sarojini Nagar" to listOf("Banthra Panchayat", "Kallikheda Panchayat", "Natkur Panchayat", "Miranpur Panchayat"),
        "Hauz Khas" to listOf("Shahpur Jat", "Hauz Khas Rural"),
        "Sanganer" to listOf("Watika Panchayat", "Muhana Panchayat", "Narsinghpura"),
        "Bisrakh" to listOf("Patwari Panchayat", "Chhapraula Panchayat")
    )

    val urbanNigamMap = mapOf(
        "South Delhi" to "South Delhi Municipal Corporation (SDMC)",
        "Lucknow" to "Lucknow Municipal Corporation (LMC)",
        "Jaipur" to "Jaipur Greater Municipal Corporation",
        "Gautam Buddha Nagar (Noida)" to "NOIDA Industrial Development Authority"
    )

    val sampleTeacherFeeds = listOf(
        TeacherFeedItem(
            id = "FEED-01",
            teacherName = "Prof. Rajesh Sharma",
            actionType = "ATTENDANCE",
            messageEn = "Marked attendance for Class 9-B (38 Present, 2 Absent)",
            messageHi = "कक्षा 9-बी की उपस्थिति दर्ज की (38 उपस्थित, 2 अनुपस्थित)",
            timestamp = "08:35 AM",
            timeAgo = "12m ago"
        ),
        TeacherFeedItem(
            id = "FEED-02",
            teacherName = "Sunita Verma",
            actionType = "FEE",
            messageEn = "Logged fee collection ₹2,500 from Roll #12 (Pending Approval)",
            messageHi = "रोल #12 से ₹2,500 शुल्क संग्रह दर्ज किया (अनुमोदन लंबित)",
            timestamp = "09:15 AM",
            timeAgo = "28m ago"
        ),
        TeacherFeedItem(
            id = "FEED-03",
            teacherName = "Dr. Vikas Mehra",
            actionType = "MARKS",
            messageEn = "AI Scanner evaluated 42 physics answer sheets (Avg: 86%)",
            messageHi = "AI स्कैनर द्वारा 42 भौतिक विज्ञान कॉपियों का मूल्यांकन (औसत: 86%)",
            timestamp = "10:10 AM",
            timeAgo = "1h ago"
        ),
        TeacherFeedItem(
            id = "FEED-04",
            teacherName = "Priyanka Saxena",
            actionType = "ATTENDANCE",
            messageEn = "Marked Class 10-A attendance (100% Full Attendance)",
            messageHi = "कक्षा 10-ए की उपस्थिति दर्ज की (100% पूर्ण उपस्थिति)",
            timestamp = "10:45 AM",
            timeAgo = "2h ago"
        )
    )

    val sampleHomework = listOf(
        HomeworkItem(
            id = "HW-01",
            subject = "Mathematics",
            title = "Quadratic Equations Exercise 4.2",
            description = "Solve questions 1 to 8 showing all formula steps in notebook.",
            dueDate = "Tomorrow, 08:30 AM",
            assignedBy = "Prof. Rajesh Sharma",
            isCompleted = false
        ),
        HomeworkItem(
            id = "HW-02",
            subject = "Science",
            title = "Chemical Reactions & Equations",
            description = "Draw balanced chemical equations diagram and answer NCERT Q3-Q7.",
            dueDate = "Friday, 09:00 AM",
            assignedBy = "Dr. Vikas Mehra",
            isCompleted = true
        ),
        HomeworkItem(
            id = "HW-03",
            subject = "English",
            title = "Essay on Sustainable Development",
            description = "Write 250 words on Renewable Energy innovations in India.",
            dueDate = "Monday, 10:00 AM",
            assignedBy = "Mrs. Neha Kapoor",
            isCompleted = false
        )
    )

    val sampleReportCard = ReportCardSummary(
        examName = "Mid-Term Examination 2026-27",
        totalMarks = 500,
        marksObtained = 462,
        percentage = 92.4,
        grade = "A+",
        remarks = "Outstanding academic performance with exceptional analytical problem-solving skills."
    )

    val sampleNotices = listOf(
        DigitalNotice(
            id = "NOT-01",
            titleEn = "Annual Sports Meet 2026 registrations open this Friday",
            titleHi = "वार्षिक खेल महोत्सव 2026 हेतु पंजीकरण शुक्रवार से प्रारंभ",
            date = "Today",
            priority = "HIGH"
        ),
        DigitalNotice(
            id = "NOT-02",
            titleEn = "Parent-Teacher Meeting scheduled for coming Saturday 10:00 AM",
            titleHi = "अभिभावक-शिक्षक बैठक आगामी शनिवार प्रातः 10:00 बजे आयोजित होगी",
            date = "Yesterday",
            priority = "NORMAL"
        )
    )

    val sampleTeacherSalaries = listOf(
        TeacherSalaryRecord(
            id = "TCH-SAL-00",
            schoolId = "SCH-001",
            teacherName = "Prof. Rajesh Sharma",
            teacherMobile = "9811223344",
            subjectOrDesignation = "Senior PGT Mathematics",
            monthYear = "July 2026",
            baseMonthlySalary = 35000L,
            advanceSalaryPaid = 2000L,
            previousYearOutstanding = 5000L,
            totalPaid = 35000L,
            paymentDate = "27 Jul 2026",
            paymentMode = "NEFT / NetBanking",
            remarks = "July salary disbursed with partial prev dues"
        ),
        TeacherSalaryRecord(
            id = "TCH-SAL-01",
            schoolId = "SCH-001",
            teacherName = "Prof. Rajesh Sharma",
            teacherMobile = "9811223344",
            subjectOrDesignation = "Senior PGT Mathematics",
            monthYear = "August 2026",
            baseMonthlySalary = 35000L,
            advanceSalaryPaid = 5000L,
            previousYearOutstanding = 3000L,
            totalPaid = 30000L,
            paymentDate = "28 Aug 2026",
            paymentMode = "NEFT / NetBanking",
            remarks = "Regular monthly disbursement"
        ),
        TeacherSalaryRecord(
            id = "TCH-SAL-01B",
            schoolId = "SCH-001",
            teacherName = "Prof. Rajesh Sharma",
            teacherMobile = "9811223344",
            subjectOrDesignation = "Senior PGT Mathematics",
            monthYear = "September 2026",
            baseMonthlySalary = 35000L,
            advanceSalaryPaid = 0L,
            previousYearOutstanding = 3000L,
            totalPaid = 35000L,
            paymentDate = "Pending Approval",
            paymentMode = "Bank Transfer",
            remarks = "Approved by Principal"
        ),
        TeacherSalaryRecord(
            id = "TCH-SAL-02",
            schoolId = "SCH-001",
            teacherName = "Mrs. Sunita Verma",
            teacherMobile = "9822334455",
            subjectOrDesignation = "TGT Science & Biology",
            monthYear = "August 2026",
            baseMonthlySalary = 28000L,
            advanceSalaryPaid = 4000L,
            previousYearOutstanding = 0L,
            totalPaid = 20000L,
            paymentDate = "29 Aug 2026",
            paymentMode = "UPI Transfer",
            remarks = "Medical advance deducted"
        ),
        TeacherSalaryRecord(
            id = "TCH-SAL-03",
            schoolId = "SCH-001",
            teacherName = "Dr. Vikas Mehra",
            teacherMobile = "9833445566",
            subjectOrDesignation = "PGT Physics",
            monthYear = "August 2026",
            baseMonthlySalary = 38000L,
            advanceSalaryPaid = 0L,
            previousYearOutstanding = 4500L,
            totalPaid = 38000L,
            paymentDate = "30 Aug 2026",
            paymentMode = "Bank Transfer",
            remarks = "Previous dues pending clearance"
        )
    )

    val sampleDriverSalaries = listOf(
        DriverSalaryRecord(
            id = "DRV-SAL-00",
            schoolId = "SCH-001",
            driverName = "Balwinder Singh",
            driverMobile = "9711002233",
            busRouteName = "Route 01 - Hauz Khas / South Ext.",
            vehicleNumber = "DL-01-AB-4567",
            monthYear = "July 2026",
            baseMonthlySalary = 22000L,
            advanceSalaryPaid = 1000L,
            previousYearOutstanding = 2500L,
            totalPaid = 22000L,
            paymentDate = "28 Jul 2026",
            paymentMode = "Cash with Voucher",
            remarks = "July regular disbursement"
        ),
        DriverSalaryRecord(
            id = "DRV-SAL-01",
            schoolId = "SCH-001",
            driverName = "Balwinder Singh",
            driverMobile = "9711002233",
            busRouteName = "Route 01 - Hauz Khas / South Ext.",
            vehicleNumber = "DL-01-AB-4567",
            monthYear = "August 2026",
            baseMonthlySalary = 22000L,
            advanceSalaryPaid = 3000L,
            previousYearOutstanding = 1500L,
            totalPaid = 18000L,
            paymentDate = "29 Aug 2026",
            paymentMode = "Cash with Voucher",
            remarks = "Fuel incentive bonus included"
        ),
        DriverSalaryRecord(
            id = "DRV-SAL-01B",
            schoolId = "SCH-001",
            driverName = "Balwinder Singh",
            driverMobile = "9711002233",
            busRouteName = "Route 01 - Hauz Khas / South Ext.",
            vehicleNumber = "DL-01-AB-4567",
            monthYear = "September 2026",
            baseMonthlySalary = 22000L,
            advanceSalaryPaid = 0L,
            previousYearOutstanding = 1500L,
            totalPaid = 22000L,
            paymentDate = "Pending Verification",
            paymentMode = "Bank Transfer",
            remarks = "September cycle scheduled"
        ),
        DriverSalaryRecord(
            id = "DRV-SAL-02",
            schoolId = "SCH-001",
            driverName = "Manoj Kumar Yadav",
            driverMobile = "9711556677",
            busRouteName = "Route 02 - Saket / Malviya Nagar",
            vehicleNumber = "DL-01-CD-8910",
            monthYear = "August 2026",
            baseMonthlySalary = 20000L,
            advanceSalaryPaid = 2000L,
            previousYearOutstanding = 0L,
            totalPaid = 18000L,
            paymentDate = "30 Aug 2026",
            paymentMode = "UPI Transfer",
            remarks = "Full balance cleared"
        )
    )

    val sampleAnnouncements = listOf(
        SchoolAnnouncement(
            id = "ANN-001",
            schoolId = "SCH-001",
            title = "Severe Weather Alert & School Closure / भारी बारिश अवकाश सूचना",
            message = "Due to heavy rainfall alert issued by the meteorological department, the school will remain closed for all classes tomorrow (Monday). Online remedial classes will be conducted as per timetable. Stay safe!",
            language = "BILINGUAL",
            attachmentUrl = "https://images.unsplash.com/photo-1516979187457-637abb4f9353",
            attachmentName = "Official_Circular_Rainfall_Holiday.pdf",
            isEmergency = true,
            formattedTime = "Today, 08:30 AM",
            publishedBy = "Dr. Alok Verma (Principal)",
            whatsAppBroadcastStatus = "DELIVERED",
            totalRecipientsReached = 342
        ),
        SchoolAnnouncement(
            id = "ANN-002",
            schoolId = "SCH-001",
            title = "Annual Science Exhibition & Robot Olympiad 2026",
            message = "All students from Class 6 to 12 are invited to submit their innovative project abstracts before 15th September. Project guidelines and rubric have been published on the school portal.",
            language = "BILINGUAL",
            attachmentUrl = null,
            attachmentName = null,
            isEmergency = false,
            formattedTime = "Yesterday, 02:15 PM",
            publishedBy = "Dr. Alok Verma (Principal)",
            whatsAppBroadcastStatus = "DELIVERED",
            totalRecipientsReached = 280
        )
    )

    val sampleClassGroupMessages = listOf(
        ClassGroupMessage(
            id = "CGM-01",
            schoolId = "SCH-001",
            className = "Class 9 - Section B",
            senderName = "Prof. Rajesh Sharma",
            senderRole = UserRole.TEACHER,
            text = "Dear Students and Parents, please review Chapter 4 Polynomials exercises before tomorrow's assessment.",
            attachmentType = "PDF",
            attachmentName = "Polynomials_Chapter4_Notes.pdf",
            timestamp = "09:15 AM",
            isFromTeacher = true
        ),
        ClassGroupMessage(
            id = "CGM-02",
            schoolId = "SCH-001",
            className = "Class 9 - Section B",
            senderName = "Suresh Patel (Aarav's Father)",
            senderRole = UserRole.PARENT_STUDENT,
            text = "Thank you Sir, Aarav has completed the problem set.",
            attachmentType = null,
            attachmentName = null,
            timestamp = "09:40 AM",
            isFromTeacher = false
        )
    )
}


