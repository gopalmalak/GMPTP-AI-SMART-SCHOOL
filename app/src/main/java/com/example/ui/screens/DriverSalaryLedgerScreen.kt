package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.DriverSalaryRecord
import com.example.data.UserRole
import com.example.ui.components.StaffSalaryLedgerWidget
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverSalaryLedgerScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi

    // Security Gate: Staff (Drivers) can ONLY view their own records without edit/delete controls
    if (state.activeRole == UserRole.DRIVER) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundCanvas),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            item {
                StaffSalaryLedgerWidget(
                    state = state,
                    isDriverRole = true
                )
            }
        }
        return
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Salary Ledger, 1: Register New Driver
    var selectedMonthYear by remember { mutableStateOf("August 2026") }
    val monthOptions = listOf("July 2026", "August 2026", "September 2026", "October 2026")

    // Registration Form States
    var regFullName by remember { mutableStateOf("") }
    var regMobile by remember { mutableStateOf("") }
    var regRouteName by remember { mutableStateOf("Route 01 - Main Campus") }
    var regVehicleNo by remember { mutableStateOf("DL-01-AB-1234") }
    var regBaseSalary by remember { mutableStateOf("22000") }
    var regPreviousDues by remember { mutableStateOf("0") }
    var regAdvancePaid by remember { mutableStateOf("0") }

    // Edit/Log Salary Modal
    var activeEditRecord by remember { mutableStateOf<DriverSalaryRecord?>(null) }
    var editMonthYear by remember { mutableStateOf("August 2026") }
    var editBaseSalary by remember { mutableStateOf("22000") }
    var editAdvancePaid by remember { mutableStateOf("0") }
    var editPreviousDues by remember { mutableStateOf("0") }
    var editTotalPaid by remember { mutableStateOf("0") }
    var editPaymentMode by remember { mutableStateOf("Cash with Voucher") }
    var editRemarks by remember { mutableStateOf("") }

    // Live remaining balance calculation inside dialog
    val baseVal = editBaseSalary.toLongOrNull() ?: 0L
    val prevVal = editPreviousDues.toLongOrNull() ?: 0L
    val advVal = editAdvancePaid.toLongOrNull() ?: 0L
    val paidVal = editTotalPaid.toLongOrNull() ?: 0L
    // Formula: Remaining Balance = (Base Salary + Previous Year Dues) - (Advance + Total Paid)
    val calculatedRemaining = (baseVal + prevVal) - (advVal + paidVal)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        // Tab Navigation
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = AmberGlow
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        if (isHindi) "चालक मासिक वेतन बहीखाता" else "Driver Salary Ledger",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Text(
                        if (isHindi) "नया चालक पंजीकरण" else "Driver Registration",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            if (selectedTab == 0) {
                // Header & Month Selector
                item {
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
                                Column {
                                    Text(
                                        text = if (isHindi) "वाहन चालक मासिक वेतन प्रणाली" else "DRIVER MONTHLY SALARY LEDGER",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkSecondary,
                                        letterSpacing = 0.6.sp
                                    )
                                    Text(
                                        text = if (isHindi) "सत्र: 2026-27 • कुल चालक: ${state.driverSalaries.size}" else "Session: 2026-27 • Fleet Drivers: ${state.driverSalaries.size}",
                                        fontSize = 12.sp,
                                        color = TextMuted
                                    )
                                }

                                Surface(
                                    onClick = { selectedTab = 1 },
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DirectionsBus,
                                            contentDescription = "Add",
                                            tint = AmberGlow,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isHindi) "+ नया चालक" else "+ Add Driver",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberGlow
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Month Selector Chips
                            Text(
                                text = if (isHindi) "महीना एवं वर्ष चुनें (Month Selector):" else "Select Month & Year:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextDarkPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                monthOptions.forEach { m ->
                                    val isSelected = m == selectedMonthYear
                                    Surface(
                                        onClick = { selectedMonthYear = m },
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) RoyalPurple800 else Color(0xFFF1F5F9),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) RoyalPurple800 else BorderLight
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = m.split(" ")[0],
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextDarkPrimary,
                                            modifier = Modifier
                                                .padding(vertical = 8.dp)
                                                .fillMaxWidth(),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Aggregated Financial Metrics for selected month
                            val monthRecords = state.driverSalaries.filter { it.monthYear == selectedMonthYear }
                            val totalBase = monthRecords.sumOf { it.baseMonthlySalary }
                            val totalAdv = monthRecords.sumOf { it.advanceSalaryPaid }
                            val totalDues = monthRecords.sumOf { it.previousYearOutstanding }
                            val totalPaidSum = monthRecords.sumOf { it.totalPaid }
                            val totalRemaining = (totalBase + totalDues) - (totalAdv + totalPaidSum)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                MiniMetricBox(
                                    title = if (isHindi) "कुल मूल वेतन" else "Base Salary",
                                    amount = "₹$totalBase",
                                    color = RoyalPurple800,
                                    modifier = Modifier.weight(1f)
                                )
                                MiniMetricBox(
                                    title = if (isHindi) "अग्रिम भुगतान" else "Advance Paid",
                                    amount = "₹$totalAdv",
                                    color = AmberGlow,
                                    modifier = Modifier.weight(1f)
                                )
                                MiniMetricBox(
                                    title = if (isHindi) "बकाया भुगतान" else "Net Remaining",
                                    amount = "₹$totalRemaining",
                                    color = if (totalRemaining > 0) CrimsonRed else EmeraldGreen,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (isHindi) "चालक वेतन रोस्टर ($selectedMonthYear)" else "DRIVER FLEET SALARY ROSTER ($selectedMonthYear)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSecondary,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                val currentMonthRecords = state.driverSalaries.filter { it.monthYear == selectedMonthYear }
                if (currentMonthRecords.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = TextMuted, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = if (isHindi) "इस महीने ($selectedMonthYear) के लिए कोई चालक रिकॉर्ड उपलब्ध नहीं है।" else "No driver salary records logged for $selectedMonthYear.",
                                    fontSize = 13.sp,
                                    color = TextDarkSecondary
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        state.driverSalaries.map { it.driverName }.distinct().forEach { name ->
                                            val d = state.driverSalaries.first { it.driverName == name }
                                            viewModel.logDriverSalaryRecord(
                                                recordId = null,
                                                driverName = d.driverName,
                                                driverMobile = d.driverMobile,
                                                monthYear = selectedMonthYear,
                                                baseMonthlySalary = d.baseMonthlySalary,
                                                advanceSalaryPaid = 0L,
                                                previousYearOutstanding = 0L,
                                                totalPaid = 0L,
                                                paymentMode = "Pending",
                                                remarks = "Auto-initialized for $selectedMonthYear"
                                            )
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800)
                                ) {
                                    Text(if (isHindi) "इस माह हेतु चालक वेतन शीट तैयार करें" else "Initialize Driver Roster For Month")
                                }
                            }
                        }
                    }
                } else {
                    items(currentMonthRecords) { record ->
                        DriverSalaryCard(
                            record = record,
                            isHindi = isHindi,
                            onEditClick = {
                                activeEditRecord = record
                                editMonthYear = record.monthYear
                                editBaseSalary = record.baseMonthlySalary.toString()
                                editAdvancePaid = record.advanceSalaryPaid.toString()
                                editPreviousDues = record.previousYearOutstanding.toString()
                                editTotalPaid = record.totalPaid.toString()
                                editPaymentMode = record.paymentMode
                                editRemarks = record.remarks
                            }
                        )
                    }
                }
            } else {
                // Driver Registration Form
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFEF3C7)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = AmberGlow, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isHindi) "नया चालक पंजीकरण (Driver Registration)" else "DRIVER REGISTRATION HUB",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = if (isHindi) "प्रधानाचार्य द्वारा सुगम पंजीकरण व स्वतः OTP लिंक" else "Accessible by Principal • Triggers auto-registration/OTP link",
                                        fontSize = 11.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = regFullName,
                                onValueChange = { regFullName = it },
                                label = { Text(if (isHindi) "पूरा नाम (Full Name) *" else "Full Name (पूरा नाम) *") },
                                placeholder = { Text("e.g. Balwinder Singh") },
                                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = RoyalPurple700) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regMobile,
                                onValueChange = { if (it.length <= 10 && it.all { c -> c.isDigit() }) regMobile = it },
                                label = { Text(if (isHindi) "मोबाइल नंबर (Mobile Number - 10 अंक) *" else "Mobile Number (मोबाइल नंबर - 10 Digits) *") },
                                placeholder = { Text("97XXXXXXXX") },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = RoyalPurple700) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regRouteName,
                                onValueChange = { regRouteName = it },
                                label = { Text(if (isHindi) "बस मार्ग नाम (Route Name)" else "Assigned Route Name") },
                                placeholder = { Text("e.g. Route 01 - Hauz Khas") },
                                leadingIcon = { Icon(Icons.Default.AltRoute, contentDescription = null, tint = RoyalPurple700) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regVehicleNo,
                                onValueChange = { regVehicleNo = it },
                                label = { Text(if (isHindi) "वाहन क्रमांक (Vehicle Plate No)" else "Vehicle Number Plate") },
                                placeholder = { Text("DL-01-AB-1234") },
                                leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = RoyalPurple700) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = regBaseSalary,
                                onValueChange = { if (it.all { c -> c.isDigit() }) regBaseSalary = it },
                                label = { Text(if (isHindi) "निर्धारित मासिक वेतन (Base Monthly Salary ₹)" else "Base Monthly Salary (मासिक वेतन ₹)") },
                                leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = EmeraldGreen) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = regPreviousDues,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) regPreviousDues = it },
                                    label = { Text(if (isHindi) "गत वर्ष का बकाया (₹)" else "Previous Year Dues (₹)") },
                                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = AmberGold) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = regAdvancePaid,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) regAdvancePaid = it },
                                    label = { Text(if (isHindi) "स्वीकृत अग्रिम (₹)" else "Advance Approved (₹)") },
                                    leadingIcon = { Icon(Icons.Default.CurrencyRupee, contentDescription = null, tint = RoyalPurple700) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (regFullName.isBlank() || regMobile.length < 10) {
                                        android.widget.Toast.makeText(
                                            context,
                                            if (isHindi) "कृपया चालक का पूरा नाम एवं 10-अंकीय मोबाइल नंबर दर्ज करें" else "Please enter full name and 10-digit mobile number",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                        return@Button
                                    }
                                    val salary = regBaseSalary.toLongOrNull() ?: 20000L
                                    val prevDues = regPreviousDues.toLongOrNull() ?: 0L
                                    val advSalary = regAdvancePaid.toLongOrNull() ?: 0L
                                    viewModel.registerDriver(
                                        fullName = regFullName.trim(),
                                        mobile = regMobile.trim(),
                                        baseSalary = salary,
                                        routeName = regRouteName.trim(),
                                        vehicleNo = regVehicleNo.trim(),
                                        previousYearOutstanding = prevDues,
                                        advanceSalaryPaid = advSalary,
                                        context = context
                                    )
                                    // Reset form and return to ledger
                                    regFullName = ""
                                    regMobile = ""
                                    regPreviousDues = "0"
                                    regAdvancePaid = "0"
                                    selectedTab = 0
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "चालक पंजीकृत करें एवं लॉगिन OTP लिंक भेजें" else "Register Driver & Dispatch OTP Link",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Update Driver Monthly Salary Ledger
    if (activeEditRecord != null) {
        val driver = activeEditRecord!!
        Dialog(onDismissRequest = { activeEditRecord = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                LazyColumn(modifier = Modifier.padding(18.dp)) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = driver.driverName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkPrimary
                                )
                                Text(
                                    text = "${driver.driverMobile} • ${driver.vehicleNumber}",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }
                            IconButton(onClick = { activeEditRecord = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextDarkSecondary)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

                        Text(
                            text = if (isHindi) "चालक मासिक वेतन प्रविष्टि" else "DRIVER MONTHLY SALARY LOGGING",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RoyalPurple800,
                            letterSpacing = 0.6.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Month Selection inside dialog
                        OutlinedTextField(
                            value = editMonthYear,
                            onValueChange = { editMonthYear = it },
                            label = { Text(if (isHindi) "माह एवं वर्ष (Month & Year)" else "Month & Year") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Base Monthly Salary
                        OutlinedTextField(
                            value = editBaseSalary,
                            onValueChange = { if (it.all { c -> c.isDigit() }) editBaseSalary = it },
                            label = { Text(if (isHindi) "मासिक वेतन (Base Monthly Salary ₹)" else "Base Monthly Salary (मासिक वेतन ₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Advance Salary Paid
                        OutlinedTextField(
                            value = editAdvancePaid,
                            onValueChange = { if (it.all { c -> c.isDigit() }) editAdvancePaid = it },
                            label = { Text(if (isHindi) "अग्रिम वेतन (Advance Salary Paid ₹)" else "Advance Salary Paid (अग्रिम वेतन ₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Previous Year Outstanding Salary
                        OutlinedTextField(
                            value = editPreviousDues,
                            onValueChange = { if (it.all { c -> c.isDigit() }) editPreviousDues = it },
                            label = { Text(if (isHindi) "पिछले वर्ष का बकाया वेतन (Previous Outstanding ₹)" else "Previous Year Outstanding (पिछले वर्ष का बकाया ₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Total Paid So Far
                        OutlinedTextField(
                            value = editTotalPaid,
                            onValueChange = { if (it.all { c -> c.isDigit() }) editTotalPaid = it },
                            label = { Text(if (isHindi) "कुल भुगतान किया गया (Total Paid ₹)" else "Total Paid (कुल भुगतान ₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Payment Mode & Remarks
                        OutlinedTextField(
                            value = editPaymentMode,
                            onValueChange = { editPaymentMode = it },
                            label = { Text(if (isHindi) "भुगतान माध्यम (Payment Mode)" else "Payment Mode (UPI/Bank/Cash)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = editRemarks,
                            onValueChange = { editRemarks = it },
                            label = { Text(if (isHindi) "टिप्पणी (Remarks / Voucher No)" else "Remarks / Voucher No") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Live Dynamic Auto-Calculated Remaining Balance Box
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = if (calculatedRemaining > 0) Color(0xFFFEF2F2) else Color(0xFFF0FDF4)),
                            modifier = Modifier.fillMaxWidth(),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (calculatedRemaining > 0) CrimsonRed.copy(alpha = 0.4f) else EmeraldGreen.copy(alpha = 0.4f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = if (isHindi) "स्वतः परिकलित शेष देय वेतन" else "AUTO-CALCULATED REMAINING BALANCE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (calculatedRemaining > 0) CrimsonRed else EmeraldGreen,
                                            letterSpacing = 0.5.sp
                                        )
                                        Text(
                                            text = if (isHindi) "सूत्र: (मूल + बकाया) - (अग्रिम + कुल भुगतान)" else "Formula: (Base + Prev Dues) - (Advance + Total Paid)",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    Text(
                                        text = "₹$calculatedRemaining",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (calculatedRemaining > 0) CrimsonRed else EmeraldGreen
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                viewModel.logDriverSalaryRecord(
                                    recordId = driver.id,
                                    driverName = driver.driverName,
                                    driverMobile = driver.driverMobile,
                                    monthYear = editMonthYear.trim(),
                                    baseMonthlySalary = editBaseSalary.toLongOrNull() ?: 0L,
                                    advanceSalaryPaid = editAdvancePaid.toLongOrNull() ?: 0L,
                                    previousYearOutstanding = editPreviousDues.toLongOrNull() ?: 0L,
                                    totalPaid = editTotalPaid.toLongOrNull() ?: 0L,
                                    paymentMode = editPaymentMode.trim(),
                                    remarks = editRemarks.trim(),
                                    context = context
                                )
                                activeEditRecord = null
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isHindi) "वेतन बहीखाता में सहेजें" else "Save to Salary Ledger",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverSalaryCard(
    record: DriverSalaryRecord,
    isHindi: Boolean,
    onEditClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint = AmberGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = record.driverName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                        Text(
                            text = "Mob: ${record.driverMobile} • ${record.vehicleNumber}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Surface(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    color = RoyalPurple800
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isHindi) "वेतन प्रविष्टि" else "Log / Pay",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4-cell Ledger Metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LedgerCell(
                    title = if (isHindi) "मासिक वेतन" else "Base Salary",
                    amount = "₹${record.baseMonthlySalary}",
                    modifier = Modifier.weight(1f)
                )
                LedgerCell(
                    title = if (isHindi) "अग्रिम वेतन" else "Advance",
                    amount = "₹${record.advanceSalaryPaid}",
                    color = AmberGlow,
                    modifier = Modifier.weight(1f)
                )
                LedgerCell(
                    title = if (isHindi) "पिछला बकाया" else "Prev Dues",
                    amount = "₹${record.previousYearOutstanding}",
                    modifier = Modifier.weight(1f)
                )
                LedgerCell(
                    title = if (isHindi) "कुल भुगतान" else "Total Paid",
                    amount = "₹${record.totalPaid}",
                    color = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Remaining Balance Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (record.remainingBalance > 0) Color(0xFFFEF2F2) else Color(0xFFF0FDF4))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (record.remainingBalance > 0) Icons.Default.Pending else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (record.remainingBalance > 0) CrimsonRed else EmeraldGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isHindi) "बकाया वेतन (Remaining Balance):" else "Remaining Balance (बकाया):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDarkSecondary
                    )
                }

                Text(
                    text = "₹${record.remainingBalance}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (record.remainingBalance > 0) CrimsonRed else EmeraldGreen
                )
            }
        }
    }
}
