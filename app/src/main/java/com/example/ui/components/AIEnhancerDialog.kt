package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SchemeLessonRow
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary

@Composable
fun AIEnhancerDialog(
    subject: String,
    grade: String,
    onDismiss: () -> Unit,
    onApplyOutcomes: (knowledge: String, skill: String, attitude: String, inquiry: String) -> Unit
) {
    var topicQuery by remember { mutableStateOf("") }
    var generatedKnowledge by remember { mutableStateOf("") }
    var generatedSkill by remember { mutableStateOf("") }
    var generatedAttitude by remember { mutableStateOf("") }
    var generatedInquiry by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.95f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF312E81))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = AmberTertiary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Schemly AI CBC Curriculum Assistant",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Instant KICD CBC Outcome & Inquiry Question Generator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = IndigoPrimary
                    )
                    Text(
                        text = "Generates specific learning outcomes strictly categorized into Knowledge, Skill, Attitude and formulation of inquiry questions starting with 'How...?'.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = topicQuery,
                        onValueChange = { topicQuery = it },
                        label = { Text("Enter Strand / Sub-strand topic in $grade $subject") },
                        placeholder = { Text("e.g. Linear Equations, Solar System, Soil Conservation") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val cleanTopic = topicQuery.ifBlank { "Core Concepts in $subject" }
                            generatedKnowledge = "Identify key principles, state definitions, and explain standard rules governing $cleanTopic."
                            generatedSkill = "Demonstrate accurate problem solving, execute practical procedures, and construct representations of $cleanTopic."
                            generatedAttitude = "Appreciate the practical importance, ethics, and sustainability of $cleanTopic in society."
                            generatedInquiry = "How do principles of $cleanTopic help us solve real-world community challenges?"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate CBC Outcomes & Inquiry Question")
                    }

                    if (generatedKnowledge.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Generated KICD CBC Outcomes:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = IndigoPrimary
                                )
                                Text(text = "• Knowledge: $generatedKnowledge", fontSize = 11.5.sp)
                                Text(text = "• Skill: $generatedSkill", fontSize = 11.5.sp)
                                Text(text = "• Attitude: $generatedAttitude", fontSize = 11.5.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "• Key Inquiry: $generatedInquiry",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF4338CA)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    if (generatedKnowledge.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onApplyOutcomes(generatedKnowledge, generatedSkill, generatedAttitude, generatedInquiry)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                        ) {
                            Text("Apply to Lesson Row")
                        }
                    }
                }
            }
        }
    }
}
