package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

@Composable
fun StaffReplacementScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val context = LocalContext.current
    val isHindi = state.isHindi
    var selectedTab by remember { mutableStateOf(0) } // 0: Transfer Principal Ownership, 1: Swap Teacher Mobile

    var newPrincipalName by remember { mutableStateOf("") }
    var newPrincipalPhone by remember { mutableStateOf("+91 ") }
    var newPrincipalEmail by remember { mutableStateOf("") }

    var oldTeacherPhone by remember { mutableStateOf("+91 98765 00001") }
    var newTeacherName by remember { mutableStateOf("Mrs. Anjali Gupta") }
    var newTeacherPhone by remember { mutableStateOf("+91 98765 11122") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardWhite,
            contentColor = RoyalPurple700
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(if (isHindi) "प्रधानाचार्य स्वामित्व हस्तांतरण" else "Principal Ownership Transfer", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(if (isHindi) "शिक्षक प्रतिस्थापन" else "Teacher Swap", fontWeight = FontWeight.Bold) }
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            if (selectedTab == 0) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = if (isHindi) "प्रधानाचार्य स्वामित्व व अधिकार स्थानांतरण" else "TRANSFER ADMINISTRATIVE SCHOOL OWNERSHIP",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = CrimsonRed,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = if (isHindi)
                                    "नए प्रधानाचार्य का विवरण दर्ज करें। सभी प्रशासनिक नियंत्रण, वित्तीय खाता-बही एवं विद्यालय स्वामित्व तुरंत नए नंबर पर स्थानांतरित हो जाएगा और पुराने नंबर का एक्सेस तुरंत निरस्त (Revoke) कर दिया जाएगा।"
                                else
                                    "Transfers all administrative controls, financial ledgers, and school ownership to the new principal while completely revoking administrative access for the old mobile number.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = newPrincipalName,
                                onValueChange = { newPrincipalName = it },
                                label = { Text(if (isHindi) "नए प्रधानाचार्य का नाम" else "New Principal Full Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newPrincipalPhone,
                                onValueChange = { newPrincipalPhone = it },
                                label = { Text(if (isHindi) "नया पंजीकृत मोबाइल नंबर" else "New Mobile Number (For Login & MPIN)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newPrincipalEmail,
                                onValueChange = { newPrincipalEmail = it },
                                label = { Text("Official Email Address") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (newPrincipalName.isNotEmpty() && newPrincipalPhone.length > 5) {
                                        viewModel.replacePrincipalOwnership(
                                            newPrincipalName = newPrincipalName,
                                            newMobile = newPrincipalPhone,
                                            context = context
                                        )
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwapHoriz,
                                    contentDescription = "Transfer",
                                    tint = androidx.compose.ui.graphics.Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "स्वामित्व स्थानांतरित करें एवं पुराना नंबर हटाएं" else "Execute Transfer & Revoke Old Access",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardWhite),
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = if (isHindi) "शिक्षक प्रतिस्थापन (कक्षा डेटा सुरक्षित रखते हुए)" else "SWAP DEPARTING TEACHER MOBILE NUMBER",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = RoyalPurple700,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = if (isHindi)
                                    "बिना किसी कक्षा असाइनमेंट, ऐतिहासिक ग्रेडिंग या छात्र रोस्टर को खोए, जाने वाले शिक्षक का मोबाइल नए शिक्षक के मोबाइल से बदलें।"
                                else
                                    "Easily swap out a departing teacher's phone number with a new teacher's phone number without losing class assignment tags, historical grading records, or student rosters.",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = oldTeacherPhone,
                                onValueChange = { oldTeacherPhone = it },
                                label = { Text(if (isHindi) "जाने वाले शिक्षक का पुराना फोन नंबर" else "Departing Teacher Old Phone") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newTeacherName,
                                onValueChange = { newTeacherName = it },
                                label = { Text(if (isHindi) "नए शिक्षक का नाम" else "New Teacher Name") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newTeacherPhone,
                                onValueChange = { newTeacherPhone = it },
                                label = { Text(if (isHindi) "नए शिक्षक का फोन नंबर" else "New Teacher Phone Number") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    viewModel.swapTeacherStaff(
                                        oldTeacherPhone = oldTeacherPhone,
                                        newTeacherName = newTeacherName,
                                        newTeacherPhone = newTeacherPhone,
                                        context = context
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple700),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HowToReg,
                                    contentDescription = "Swap",
                                    tint = androidx.compose.ui.graphics.Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isHindi) "शिक्षक डेटा रोस्टर सहित स्थानांतरित करें" else "Swap Teacher & Retain Roster",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = androidx.compose.ui.graphics.Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
