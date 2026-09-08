package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GmptpUiState
import com.example.viewmodel.GmptpViewModel

data class HelpChatMessage(
    val sender: String,
    val text: String,
    val isBot: Boolean
)

@Composable
fun AiHelpBotScreen(
    state: GmptpUiState,
    viewModel: GmptpViewModel
) {
    val isHindi = state.isHindi
    var queryText by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                HelpChatMessage(
                    sender = "GMPTP AI Assistant",
                    text = if (isHindi)
                        "नमस्ते! मैं GMPTP AI स्मार्ट स्कूल सहायक हूँ। आप मुझसे फीस बहीखाता, RTE शून्य शुल्क, लाइव GPS, 14-दिवसीय ट्रायल या मल्टी-स्कूल मैपिंग के बारे में कुछ भी पूछ सकते हैं।"
                    else
                        "Hello! I am your GMPTP AI Assistant. Ask me anything about Fee Ledgers, RTE Zero Fee policy, Live GPS tracking, 14-Day Free Trial, or Multi-School Teacher Mapping.",
                    isBot = true
                )
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundCanvas)
    ) {
        // Quick Question Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickQuestionChip(if (isHindi) "RTE नियम?" else "RTE Zero Fee Rules") {
                addQuestionAndAnswer(
                    it,
                    if (isHindi)
                        "शिक्षा का अधिकार (RTE) के अंतर्गत छात्र का RTE चेकबॉक्स ऑन करते ही सभी पूर्व व वर्तमान स्कूल एवं वाहन शुल्क स्वतः ₹0 हो जाते हैं।"
                    else
                        "Enabling the 'RTE Student' checkbox automatically zeroes out all historical and current session school & transport dues (₹0) pursuant to the RTE Act.",
                    chatMessages
                ) { chatMessages = it }
            }
            QuickQuestionChip(if (isHindi) "फीस अनुमोदन?" else "Fee Approvals") {
                addQuestionAndAnswer(
                    it,
                    if (isHindi)
                        "शिक्षक द्वारा छात्र से ली गई कोई भी फीस सीधे प्रधानाचार्य के डैशबोर्ड पर 'सत्यापन लंबित' कतार में जाती है। प्रधानाचार्य द्वारा स्वीकृत होते ही यह बहीखाते में जुड़ती है।"
                    else
                        "Any fee collected by a teacher is held in 'Pending Verification' until the Principal confirms it from the dashboard, safeguarding all collections.",
                    chatMessages
                ) { chatMessages = it }
            }
        }

        Divider(color = BorderLight, thickness = 1.dp)

        // Chat List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (msg.isBot) Arrangement.Start else Arrangement.End
                ) {
                    if (msg.isBot) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(RoyalPurple700),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "Bot",
                                tint = BrilliantGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (msg.isBot) CardWhite else RoyalPurple700)
                            .border(1.dp, if (msg.isBot) BorderLight else RoyalPurple800, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                            .widthIn(max = 280.dp)
                    ) {
                        Text(
                            text = msg.text,
                            color = if (msg.isBot) TextDarkPrimary else Color.White,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }

        // Input Field Bar
        Surface(
            color = CardWhite,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    placeholder = { Text(if (isHindi) "अपना प्रश्न यहाँ लिखें..." else "Type question...") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                IconButton(
                    onClick = {
                        if (queryText.isNotBlank()) {
                            val userQ = queryText.trim()
                            queryText = ""
                            val botReply = if (userQ.contains("gps", ignoreCase = true) || userQ.contains("बस", ignoreCase = true)) {
                                if (isHindi) "चालक 'Start Route' बटन दबाते ही जीपीएस लोकेशन ब्रॉडकास्ट करता है। माता-पिता लाइव मैप पर ETA व स्पीड देख सकते हैं।"
                                else "When the driver taps 'Start Route', GPS telemetry broadcasts in real-time. Parents view live ETA and speed on their map."
                            } else {
                                if (isHindi) "GMPTP AI बहु-विद्यालय प्रबंधन प्रणाली में आपका स्वागत है। आप साइड मेनू से तुरंत स्कूल व रोल बदल सकते हैं।"
                                else "GMPTP AI supports seamless multi-school swapping and instant role switching from the sidebar menu dropdown."
                            }
                            addQuestionAndAnswer(userQ, botReply, chatMessages) { chatMessages = it }
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(RoyalPurple700)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = BrilliantGold
                    )
                }
            }
        }
    }
}

private fun addQuestionAndAnswer(
    q: String,
    ans: String,
    currentList: List<HelpChatMessage>,
    update: (List<HelpChatMessage>) -> Unit
) {
    val newList = currentList.toMutableList()
    newList.add(HelpChatMessage(sender = "User", text = q, isBot = false))
    newList.add(HelpChatMessage(sender = "GMPTP AI", text = ans, isBot = true))
    update(newList)
}

@Composable
private fun QuickQuestionChip(label: String, onClick: (String) -> Unit) {
    Surface(
        onClick = { onClick(label) },
        shape = RoundedCornerShape(16.dp),
        color = RoyalPurpleLight,
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple600.copy(alpha = 0.3f))
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = RoyalPurple700,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
