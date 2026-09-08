package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SampleExamSheet(
    val studentName: String,
    val rollNo: String,
    val subject: String,
    val standard: String,
    val questionText: String,
    val answerText: String,
    val score: Int,
    val grade: String,
    val accuracy: String,
    val teacherComment: String
)

@Composable
fun AiExamScannerScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isHindi = state.isHindi

    // Mode: 0 = Camera Alignment Viewfinder, 1 = Evaluated Results
    var activeTab by remember { mutableStateOf(0) }

    // Camera Viewfinder Controls
    var isTorchOn by remember { mutableStateOf(false) }
    var isGridOn by remember { mutableStateOf(true) }
    var isAutoCaptureEnabled by remember { mutableStateOf(true) }
    var isPaperAligned by remember { mutableStateOf(true) }
    var selectedSheetType by remember { mutableStateOf("A4 Answer Sheet") } // A4 Answer Sheet, OMR 100-Q, Lab Report
    var selectedStudentIndex by remember { mutableStateOf(0) }
    var isScanningActive by remember { mutableStateOf(false) }

    // Sample Exam Answer Sheets for Testing & Demonstration
    val sampleSheets = listOf(
        SampleExamSheet(
            studentName = "Aarav Sharma",
            rollNo = "101",
            subject = "Science & Physics (Class 10-A)",
            standard = "Class 10-A",
            questionText = "Q1: State Newton's Second Law of Motion & Derive F = m × a",
            answerText = "The rate of change of momentum of a body is directly proportional to applied unbalanced force.\n" +
                    "Let momentum p = m * v, dp/dt = m*(dv/dt) = m*a.\n" +
                    "Hence F = k * m * a. In SI units k=1, therefore F = m × a. (Proved ✓)",
            score = 92,
            grade = "A+ (Distinction)",
            accuracy = "96% • Step Completeness: 100%",
            teacherComment = "Excellent conceptual clarity, clean calculus derivation and SI units noted."
        ),
        SampleExamSheet(
            studentName = "Priya Verma",
            rollNo = "104",
            subject = "Mathematics (Class 10-A)",
            standard = "Class 10-A",
            questionText = "Q2: Solve Quadratic Equation 2x² - 7x + 3 = 0 using Quadratic Formula",
            answerText = "Standard form: ax² + bx + c = 0, a=2, b=-7, c=3.\n" +
                    "Discriminant D = b² - 4ac = (-7)² - 4(2)(3) = 49 - 24 = 25.\n" +
                    "x = (-(-7) ± √25) / (2*2) = (7 ± 5) / 4.\n" +
                    "x₁ = (7+5)/4 = 3, x₂ = (7-5)/4 = 1/2. Roots = {3, 0.5}.",
            score = 88,
            grade = "A (Very Good)",
            accuracy = "92% • Step Completeness: 95%",
            teacherComment = "Clean discriminant breakdown, both roots identified accurately."
        ),
        SampleExamSheet(
            studentName = "Rohan Singh",
            rollNo = "108",
            subject = "English Literature (Class 10-A)",
            standard = "Class 10-A",
            questionText = "Q3: Discuss the central moral theme in 'The Road Not Taken' by Robert Frost",
            answerText = "The central theme is about choices in life and individualism.\n" +
                    "The two roads diverge in a yellow wood symbolizing critical crossroads.\n" +
                    "The poet chooses the road less travelled by, which has made all the difference,\n" +
                    "emphasizing that decisions shape personal destiny irrevocably.",
            score = 85,
            grade = "A- (Proficient)",
            accuracy = "88% • Vocabulary: Rich",
            teacherComment = "Poetic analysis and metaphorical interpretation are well structured."
        )
    )

    val currentSheet = sampleSheets[selectedStudentIndex]
    var teacherOverrideScore by remember(selectedStudentIndex) { mutableStateOf(currentSheet.score) }
    var teacherComment by remember(selectedStudentIndex) { mutableStateOf(currentSheet.teacherComment) }

    // Laser Animation Sweep for live scanning
    val infiniteTransition = rememberInfiniteTransition(label = "LaserSweep")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserProgress"
    )

    // Corner bracket pulse animation when aligned
    val cornerPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CornerPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        // Top Bar & Navigation Tab
        Surface(
            color = RoyalPurple900,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.navigateTo("DASHBOARD") },
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "AI परीक्षा ओसीआर स्कैनर" else "AI Exam OCR Scanner",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BrilliantGold
                                ) {
                                    Text(
                                        text = "LIVE HUD",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = RoyalPurple900,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isHindi) "स्मार्ट पेपर अलाइनमेंट व ऑटो ग्रेडिंग" else "Paper Alignment Overlay & Auto-Grading",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.75f)
                            )
                        }
                    }

                    // Mode Switcher Tabs (Viewfinder vs Results)
                    TabRow(
                        selectedTabIndex = activeTab,
                        containerColor = RoyalPurple800,
                        contentColor = Color.White,
                        modifier = Modifier
                            .width(180.dp)
                            .clip(RoundedCornerShape(20.dp))
                    ) {
                        Tab(
                            selected = activeTab == 0,
                            onClick = { activeTab = 0 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Camera",
                                        tint = if (activeTab == 0) BrilliantGold else Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "कैमरा" else "Camera",
                                        fontSize = 11.sp,
                                        fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal,
                                        color = if (activeTab == 0) BrilliantGold else Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        )
                        Tab(
                            selected = activeTab == 1,
                            onClick = { activeTab = 1 },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Assessment,
                                        contentDescription = "Score",
                                        tint = if (activeTab == 1) BrilliantGold else Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "परिणाम" else "Results",
                                        fontSize = 11.sp,
                                        fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal,
                                        color = if (activeTab == 1) BrilliantGold else Color.White.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }

        if (activeTab == 0) {
            // CAMERA VIEWFINDER & PAPER ALIGNMENT OVERLAY SCREEN
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF070B14))
            ) {
                // Top Camera Viewfinder HUD Control Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Torch Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isTorchOn) AmberGlow.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, if (isTorchOn) AmberGlow else Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.clickable {
                            isTorchOn = !isTorchOn
                            Toast.makeText(
                                context,
                                if (isTorchOn) "Flash Torch ON (450 Lux)" else "Flash Torch OFF",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Torch",
                                tint = if (isTorchOn) AmberGlow else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isTorchOn) "Torch ON" else "Torch",
                                fontSize = 11.sp,
                                color = if (isTorchOn) AmberGlow else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Grid Toggle
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isGridOn) ElectricBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(1.dp, if (isGridOn) ElectricBlue else Color.White.copy(alpha = 0.2f)),
                        modifier = Modifier.clickable { isGridOn = !isGridOn }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isGridOn) Icons.Default.GridOn else Icons.Default.GridOff,
                                contentDescription = "Grid",
                                tint = if (isGridOn) ElectricBlue else Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isGridOn) "Grid 3×3" else "Grid",
                                fontSize = 11.sp,
                                color = if (isGridOn) ElectricBlue else Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Paper Type Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .clickable {
                                selectedSheetType = when (selectedSheetType) {
                                    "A4 Answer Sheet" -> "OMR 100-Q"
                                    "OMR 100-Q" -> "Question Booklet"
                                    else -> "A4 Answer Sheet"
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Format",
                            tint = BrilliantGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = selectedSheetType,
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Alignment Simulation Toggle (Aligned vs Misaligned)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPaperAligned) EmeraldGreen.copy(alpha = 0.2f) else OrangeWarning.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (isPaperAligned) EmeraldGreen else OrangeWarning),
                        modifier = Modifier.clickable {
                            isPaperAligned = !isPaperAligned
                            Toast.makeText(
                                context,
                                if (isPaperAligned) "Paper Aligned: 4 Corners Locked!" else "Paper Misaligned: Adjust Angle!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isPaperAligned) EmeraldGreen else OrangeWarning)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPaperAligned) "4/4 Locked" else "Adjust",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPaperAligned) EmeraldGreen else OrangeWarning
                            )
                        }
                    }
                }

                // MAIN CAMERA VIEWFINDER WITH HIGH-FIDELITY ALIGNMENT OVERLAY
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Camera live viewfinder canvas backdrop (Simulated camera sensor feed)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0F172A),
                                        Color(0xFF090D16),
                                        Color(0xFF0B0F19)
                                    )
                                )
                            )
                    ) {
                        // Torch glow simulation if active
                        if (isTorchOn) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(
                                                Color(0x33FFF3B0),
                                                Color(0x00000000)
                                            )
                                        )
                                    )
                            )
                        }

                        // THE ALIGNED ANSWER SHEET (Displayed inside the camera viewfinder)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .fillMaxHeight(0.88f)
                                .align(Alignment.Center)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFCFCF9))
                                .border(
                                    1.dp,
                                    if (isPaperAligned) EmeraldGreen.copy(alpha = 0.6f) else OrangeWarning.copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            // Ruled notebook sheet lines background simulation
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Answer sheet top header
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "STUDENT: ${currentSheet.studentName} | ROLL: ${currentSheet.rollNo}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = RoyalPurple900
                                        )
                                        Text(
                                            text = "${currentSheet.subject} • Term Final",
                                            fontSize = 9.sp,
                                            color = TextDarkSecondary
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = RoyalPurple800
                                    ) {
                                        Text(
                                            text = "EXAM SHEET",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Recognized Question Block with Optical Bounding Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(
                                            1.dp,
                                            if (isPaperAligned) ElectricBlue.copy(alpha = 0.7f) else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .background(ElectricBlue.copy(alpha = 0.04f))
                                        .padding(6.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = currentSheet.questionText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            if (isPaperAligned) {
                                                Surface(
                                                    shape = RoundedCornerShape(2.dp),
                                                    color = ElectricBlue
                                                ) {
                                                    Text(
                                                        text = "OCR LOCKED",
                                                        fontSize = 7.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Handwritten Student Answer Text & Equations
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .border(
                                            1.dp,
                                            if (isPaperAligned) EmeraldGreen.copy(alpha = 0.7f) else Color.Transparent,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .background(EmeraldGreen.copy(alpha = 0.03f))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "Handwritten Response (Recognized)",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = EmeraldGreen
                                            )
                                            Text(
                                                text = "Confidence: 99.4%",
                                                fontSize = 8.sp,
                                                color = TextDarkSecondary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = currentSheet.answerText,
                                            fontSize = 11.sp,
                                            lineHeight = 16.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color(0xFF0F172A)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Bottom Sheet QR / Barcode & Registration Marks
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.QrCode,
                                            contentDescription = "QR",
                                            tint = Color.DarkGray,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("DOC-ID: EXM-2026-X10", fontSize = 8.sp, color = TextDarkSecondary)
                                    }
                                    Text("Page 1 of 1 • Official Seal ✓", fontSize = 8.sp, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // CAMERA ALIGNMENT OVERLAY LAYER (Drawn over the simulated paper feed)
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height

                            // 1. RULE-OF-THIRDS & DOCUMENT ALIGNMENT GRID (If enabled)
                            if (isGridOn) {
                                val gridColor = Color.White.copy(alpha = 0.15f)
                                val strokeWidth = 1.dp.toPx()

                                // Vertical grid lines
                                drawLine(
                                    color = gridColor,
                                    start = Offset(canvasWidth * 0.33f, 0f),
                                    end = Offset(canvasWidth * 0.33f, canvasHeight),
                                    strokeWidth = strokeWidth
                                )
                                drawLine(
                                    color = gridColor,
                                    start = Offset(canvasWidth * 0.66f, 0f),
                                    end = Offset(canvasWidth * 0.66f, canvasHeight),
                                    strokeWidth = strokeWidth
                                )

                                // Horizontal grid lines
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, canvasHeight * 0.33f),
                                    end = Offset(canvasWidth, canvasHeight * 0.33f),
                                    strokeWidth = strokeWidth
                                )
                                drawLine(
                                    color = gridColor,
                                    start = Offset(0f, canvasHeight * 0.66f),
                                    end = Offset(canvasWidth, canvasHeight * 0.66f),
                                    strokeWidth = strokeWidth
                                )
                            }

                            // 2. DOCUMENT BOUNDING BOX & CORNER ALIGNMENT BRACKETS (L-Reticles)
                            val padX = canvasWidth * 0.04f
                            val padY = canvasHeight * 0.06f
                            val left = padX
                            val top = padY
                            val right = canvasWidth - padX
                            val bottom = canvasHeight - padY

                            val bracketColor = if (isPaperAligned) EmeraldGreen else OrangeWarning
                            val bracketStroke = (3.5f * cornerPulse).dp.toPx()
                            val armLength = 28.dp.toPx()

                            // Top-Left Corner Bracket (L)
                            drawLine(
                                color = bracketColor,
                                start = Offset(left, top),
                                end = Offset(left + armLength, top),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = bracketColor,
                                start = Offset(left, top),
                                end = Offset(left, top + armLength),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )

                            // Top-Right Corner Bracket (⅂)
                            drawLine(
                                color = bracketColor,
                                start = Offset(right, top),
                                end = Offset(right - armLength, top),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = bracketColor,
                                start = Offset(right, top),
                                end = Offset(right, top + armLength),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )

                            // Bottom-Left Corner Bracket (L reversed)
                            drawLine(
                                color = bracketColor,
                                start = Offset(left, bottom),
                                end = Offset(left + armLength, bottom),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = bracketColor,
                                start = Offset(left, bottom),
                                end = Offset(left, bottom - armLength),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )

                            // Bottom-Right Corner Bracket (⅃)
                            drawLine(
                                color = bracketColor,
                                start = Offset(right, bottom),
                                end = Offset(right - armLength, bottom),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )
                            drawLine(
                                color = bracketColor,
                                start = Offset(right, bottom),
                                end = Offset(right, bottom - armLength),
                                strokeWidth = bracketStroke,
                                cap = StrokeCap.Round
                            )

                            // 3. DASHED BORDER SURROUNDING ALIGNMENT ENVELOPE
                            val dashPathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
                            drawRect(
                                color = bracketColor.copy(alpha = 0.55f),
                                topLeft = Offset(left, top),
                                size = Size(right - left, bottom - top),
                                style = Stroke(width = 1.5.dp.toPx(), pathEffect = dashPathEffect)
                            )

                            // 4. ANIMATED LASER SCANNING BEAM (Active sweep)
                            val beamY = top + (bottom - top) * laserProgress
                            val laserBrush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    if (isPaperAligned) EmeraldGreen.copy(alpha = 0.85f) else OrangeWarning.copy(alpha = 0.85f),
                                    Color.White,
                                    if (isPaperAligned) EmeraldGreen.copy(alpha = 0.85f) else OrangeWarning.copy(alpha = 0.85f),
                                    Color.Transparent
                                ),
                                startY = beamY - 12.dp.toPx(),
                                endY = beamY + 12.dp.toPx()
                            )
                            drawLine(
                                brush = laserBrush,
                                start = Offset(left, beamY),
                                end = Offset(right, beamY),
                                strokeWidth = 3.dp.toPx()
                            )
                        }

                        // 5. CENTER SPIRIT LEVEL & HORIZON GYRO OVERLAY (TILT / PARALLEL INDICATOR)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(64.dp)
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                val radius = size.width / 2f - 4.dp.toPx()

                                // Target ring
                                drawCircle(
                                    color = Color.White.copy(alpha = 0.25f),
                                    radius = radius,
                                    style = Stroke(width = 1.5.dp.toPx())
                                )

                                // Crosshairs
                                drawLine(
                                    color = Color.White.copy(alpha = 0.35f),
                                    start = Offset(center.x - 12.dp.toPx(), center.y),
                                    end = Offset(center.x + 12.dp.toPx(), center.y),
                                    strokeWidth = 1.dp.toPx()
                                )
                                drawLine(
                                    color = Color.White.copy(alpha = 0.35f),
                                    start = Offset(center.x, center.y - 12.dp.toPx()),
                                    end = Offset(center.x, center.y + 12.dp.toPx()),
                                    strokeWidth = 1.dp.toPx()
                                )

                                // Spirit Level Bubble
                                val bubbleOffset = if (isPaperAligned) {
                                    Offset(center.x, center.y)
                                } else {
                                    Offset(center.x + 10.dp.toPx(), center.y - 8.dp.toPx())
                                }

                                drawCircle(
                                    color = if (isPaperAligned) EmeraldGreen else OrangeWarning,
                                    radius = 6.dp.toPx(),
                                    center = bubbleOffset
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = bubbleOffset
                                )
                            }
                        }

                        // 6. TOP ALIGNMENT DIAGNOSTICS HUD PILL
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = Color(0xFF0F172A).copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, if (isPaperAligned) EmeraldGreen.copy(alpha = 0.5f) else OrangeWarning.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPaperAligned) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isPaperAligned) EmeraldGreen else OrangeWarning,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isPaperAligned) {
                                        if (isHindi) "कागज़ संरेखित • 4/4 कोने लॉक • 0.2° समतल" else "Paper Aligned • 4/4 Corners Locked • 0.2° Level"
                                    } else {
                                        if (isHindi) "कागज़ तिरछा है • कैमरा सीधा रखें" else "Paper Skewed • Align inside brackets (Tilt 4.5°)"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // 7. BOTTOM DISTANCE & LIGHTING METRICS
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.65f)
                            ) {
                                Text(
                                    text = "📐 Distance: 28 cm (In Focus)",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.65f)
                            ) {
                                Text(
                                    text = "💡 Lux: 450 (Clear)",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }

                // BOTTOM CAMERA CONTROL & SHUTTER PANEL
                Surface(
                    color = Color(0xFF0F172A),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Student Paper Switcher Pill Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isHindi) "परीक्षण छात्र पत्र:" else "Select Student Sheet:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                sampleSheets.forEachIndexed { index, sheet ->
                                    FilterChip(
                                        selected = selectedStudentIndex == index,
                                        onClick = { selectedStudentIndex = index },
                                        label = {
                                            Text(
                                                text = sheet.studentName.split(" ").first(),
                                                fontSize = 10.sp
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = RoyalPurple800,
                                            selectedLabelColor = BrilliantGold,
                                            containerColor = Color.White.copy(alpha = 0.08f),
                                            labelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Alignment Status Guidance Banner
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isPaperAligned) EmeraldGreen.copy(alpha = 0.15f) else OrangeWarning.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (isPaperAligned) EmeraldGreen.copy(alpha = 0.4f) else OrangeWarning.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isPaperAligned) Icons.Default.CheckCircle else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isPaperAligned) EmeraldGreen else OrangeWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isPaperAligned) {
                                        if (isHindi) "सटीक संरेखण! शटर दबाएं या ऑटो-स्कैन से ग्रेड करें।" else "Perfect Alignment! Tap Shutter to scan & grade."
                                    } else {
                                        if (isHindi) "कागज़ के चारों कोने ब्रैकेट के अंदर रखें।" else "Align all 4 corners inside the corner brackets."
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isPaperAligned) EmeraldGreen else OrangeWarning
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Camera Actions Row: Gallery, Main Shutter, Auto-Scan Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Auto-Scan Toggle Button
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isAutoCaptureEnabled = !isAutoCaptureEnabled }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isAutoCaptureEnabled) Icons.Default.AutoAwesome else Icons.Default.Tune,
                                    contentDescription = "Auto",
                                    tint = if (isAutoCaptureEnabled) BrilliantGold else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isAutoCaptureEnabled) "Auto-Scan ON" else "Auto-Scan",
                                    fontSize = 9.sp,
                                    color = if (isAutoCaptureEnabled) BrilliantGold else Color.White.copy(alpha = 0.5f),
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // PRIMARY SHUTTER BUTTON WITH GLOWING HALO
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isPaperAligned) {
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    EmeraldGreen,
                                                    RoyalPurple800
                                                )
                                            )
                                        } else {
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    OrangeWarning,
                                                    Color(0xFF334155)
                                                )
                                            )
                                        }
                                    )
                                    .border(
                                        3.dp,
                                        if (isPaperAligned) BrilliantGold else Color.White.copy(alpha = 0.4f),
                                        CircleShape
                                    )
                                    .clickable(enabled = !isScanningActive) {
                                        isScanningActive = true
                                        coroutineScope.launch {
                                            Toast.makeText(
                                                context,
                                                if (isHindi) "कागज़ स्कैन किया जा रहा है... OCR सक्रिय" else "Scanning Paper... OCR Active",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            delay(1400)
                                            isScanningActive = false
                                            activeTab = 1 // Switch to evaluation results
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isScanningActive) {
                                    CircularProgressIndicator(
                                        color = BrilliantGold,
                                        modifier = Modifier.size(36.dp),
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.DocumentScanner,
                                        contentDescription = "Scan Shutter",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            // Direct Evaluation Results shortcut
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { activeTab = 1 }
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Assessment,
                                    contentDescription = "View Marks",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isHindi) "परिणाम देखें" else "View Score",
                                    fontSize = 9.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // EVALUATION RESULTS & RUBRIC SCREEN
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundCanvas),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Aligned paper preview thumbnail with "Re-Align Camera" action
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldGreen.copy(alpha = 0.15f))
                                        .border(1.dp, EmeraldGreen, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Scanned",
                                        tint = EmeraldGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentSheet.studentName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Roll: ${currentSheet.rollNo} • ${currentSheet.subject}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "Paper Aligned & Scanned ✓ (A4 Format)",
                                        fontSize = 10.sp,
                                        color = EmeraldGreen,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Button(
                                onClick = { activeTab = 0 },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple800),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Camera", tint = BrilliantGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isHindi) "पुनः संरेखित" else "Re-Align", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }

                // Score Gauge & Grade Summary
                item {
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isHindi) "AI स्वचालित मूल्यांकन स्कोर" else "AI EVALUATION RESULTS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDarkSecondary,
                                    letterSpacing = 0.6.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Grade: ${currentSheet.grade}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                                Text(
                                    text = currentSheet.accuracy,
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            }

                            // Radial Gauge
                            Box(
                                modifier = Modifier.size(76.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = Color(0xFFE2E8F0),
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                    drawArc(
                                        color = EmeraldGreen,
                                        startAngle = -90f,
                                        sweepAngle = 280f * (teacherOverrideScore / 100f),
                                        useCenter = false,
                                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$teacherOverrideScore",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextDarkPrimary
                                    )
                                    Text(
                                        text = "/100",
                                        fontSize = 9.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }

                // Rubric Breakdown & Teacher Feedback
                item {
                    Spacer(modifier = Modifier.height(14.dp))

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = if (isHindi) "रूब्रिक मूल्यांकन एवं शिक्षक टिप्पणी" else "RUBRIC CRITERIA & MARKS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDarkSecondary,
                                letterSpacing = 0.6.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            RubricRow("Conceptual Definition (Q1)", "20 / 20", "Exact wording & physical principle")
                            RubricRow("Mathematical Derivation (Q2)", "28 / 30", "Minor unit omission in step 3")
                            RubricRow("Diagram & Graph Precision (Q3)", "20 / 25", "Axis labels clear, legend missing")
                            RubricRow("Numerical Problem Solving (Q4)", "20 / 25", "Correct formula, rounding variance")

                            Divider(modifier = Modifier.padding(vertical = 10.dp), color = BorderLight)

                            // Teacher Comment Input Field
                            OutlinedTextField(
                                value = teacherComment,
                                onValueChange = { teacherComment = it },
                                label = { Text(if (isHindi) "शिक्षक टिप्पणी / सुधार" else "Teacher Feedback / Comment") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Actions: Teacher Override +/- and Save
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (teacherOverrideScore < 100) teacherOverrideScore += 2
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "+2 Override", fontSize = 12.sp)
                                }

                                Button(
                                    onClick = {
                                        Toast.makeText(
                                            context,
                                            "Score $teacherOverrideScore/100 Saved to ${currentSheet.studentName}'s Gradebook!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple700),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = if (isHindi) "अंक सुरक्षित करें" else "Accept Score", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Scan Next Student Paper (Re-open Camera Overlay)
                            Button(
                                onClick = {
                                    selectedStudentIndex = (selectedStudentIndex + 1) % sampleSheets.size
                                    activeTab = 0
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Next", tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHindi) "अगला छात्र पत्र स्कैन व संरेखित करें" else "Scan & Align Next Student Paper",
                                    color = Color.White,
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

@Composable
private fun RubricRow(criteria: String, marks: String, feedback: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = criteria, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextDarkPrimary)
            Text(text = feedback, fontSize = 10.sp, color = TextMuted)
        }
        Text(text = marks, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
    }
}

