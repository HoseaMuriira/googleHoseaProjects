package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SchemeLessonRow
import com.example.ui.theme.AttitudeColor
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.KnowledgeColor
import com.example.ui.theme.SkillColor

@Composable
fun EditRowDialog(
    initialRow: SchemeLessonRow,
    onDismiss: () -> Unit,
    onSave: (SchemeLessonRow) -> Unit
) {
    var week by remember { mutableStateOf(initialRow.week.toString()) }
    var lesson by remember { mutableStateOf(initialRow.lesson.toString()) }
    var strand by remember { mutableStateOf(initialRow.strand) }
    var subStrand by remember { mutableStateOf(initialRow.subStrand) }
    var knowledgeOutcome by remember { mutableStateOf(initialRow.knowledgeOutcome) }
    var skillOutcome by remember { mutableStateOf(initialRow.skillOutcome) }
    var attitudeOutcome by remember { mutableStateOf(initialRow.attitudeOutcome) }
    var keyInquiryQuestion by remember { mutableStateOf(initialRow.keyInquiryQuestion) }
    var learningExperiences by remember { mutableStateOf(initialRow.learningExperiences) }
    var learningResources by remember { mutableStateOf(initialRow.learningResources) }
    var assessment by remember { mutableStateOf(initialRow.assessment) }
    var reflection by remember { mutableStateOf(initialRow.reflection) }

    val scrollState = rememberScrollState()

    // Ensure inquiry question starts with "How..."
    val isInquiryValid = keyInquiryQuestion.trim().startsWith("How", ignoreCase = true)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("edit_row_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Dialog Title Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IndigoPrimary)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Edit 10-Column Lesson Row (Week $week, Lesson $lesson)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                // Scrollable 10-Column Form
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Column 1 & 2: Week and Lesson
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = week,
                            onValueChange = { week = it },
                            label = { Text("1. Week (1-9)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lesson,
                            onValueChange = { lesson = it },
                            label = { Text("2. Lesson (1-5)") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Column 3 & 4: Strand & Sub-strand
                    OutlinedTextField(
                        value = strand,
                        onValueChange = { strand = it },
                        label = { Text("3. Strand") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = subStrand,
                        onValueChange = { subStrand = it },
                        label = { Text("4. Sub-strand") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Column 5: Specific Learning Outcomes (Knowledge, Skill, Attitude)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "5. Specific Learning Outcomes",
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Categorized into Knowledge, Skill, and Attitude:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            OutlinedTextField(
                                value = knowledgeOutcome,
                                onValueChange = { knowledgeOutcome = it },
                                label = { Text("• Knowledge (e.g. Identify, State, Explain...)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = skillOutcome,
                                onValueChange = { skillOutcome = it },
                                label = { Text("• Skill (e.g. Demonstrate, Solve, Construct, Perform...)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = attitudeOutcome,
                                onValueChange = { attitudeOutcome = it },
                                label = { Text("• Attitude (e.g. Appreciate, Value, Exhibit responsibility...)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Column 6: Key Enquiry Questions (Starts with How...?)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF4338CA))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "6. Key Enquiry Question",
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E1B4B),
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "Mandatory requirement: Enquiry questions must start with 'How...?'",
                                fontSize = 11.sp,
                                color = if (isInquiryValid) Color(0xFF047857) else Color(0xFFB91C1C)
                            )

                            OutlinedTextField(
                                value = keyInquiryQuestion,
                                onValueChange = { keyInquiryQuestion = it },
                                label = { Text("Question (Starts with How...?)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            if (!isInquiryValid && keyInquiryQuestion.isNotBlank()) {
                                Button(
                                    onClick = {
                                        keyInquiryQuestion = "How does $keyInquiryQuestion?"
                                            .replace("How does How", "How")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Fix to 'How...?' prefix", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Column 7: Learning Experiences
                    OutlinedTextField(
                        value = learningExperiences,
                        onValueChange = { learningExperiences = it },
                        label = { Text("7. Learning Experiences (Learner Activities)") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )

                    // Column 8: Learning Resources
                    OutlinedTextField(
                        value = learningResources,
                        onValueChange = { learningResources = it },
                        label = { Text("8. Learning Resources (Realia, Books, Charts)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Column 9: Assessment
                    OutlinedTextField(
                        value = assessment,
                        onValueChange = { assessment = it },
                        label = { Text("9. Assessment (Oral, Observation, Written quiz)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Column 10: Reflection
                    OutlinedTextField(
                        value = reflection,
                        onValueChange = { reflection = it },
                        label = { Text("10. Reflection / Remarks") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider()

                // Dialog Buttons
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
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            var formattedInquiry = keyInquiryQuestion.trim()
                            if (!formattedInquiry.startsWith("How", ignoreCase = true) && formattedInquiry.isNotBlank()) {
                                formattedInquiry = "How does $formattedInquiry"
                            }
                            onSave(
                                initialRow.copy(
                                    week = week.toIntOrNull() ?: initialRow.week,
                                    lesson = lesson.toIntOrNull() ?: initialRow.lesson,
                                    strand = strand,
                                    subStrand = subStrand,
                                    knowledgeOutcome = knowledgeOutcome,
                                    skillOutcome = skillOutcome,
                                    attitudeOutcome = attitudeOutcome,
                                    keyInquiryQuestion = formattedInquiry,
                                    learningExperiences = learningExperiences,
                                    learningResources = learningResources,
                                    assessment = assessment,
                                    reflection = reflection
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Row Changes")
                    }
                }
            }
        }
    }
}
