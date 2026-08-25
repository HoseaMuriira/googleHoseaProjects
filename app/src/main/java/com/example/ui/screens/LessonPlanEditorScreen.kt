package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonPlan
import com.example.ui.components.SchemlyTopBar
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.AttitudeColor
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.KnowledgeColor
import com.example.ui.theme.SkillColor
import com.example.ui.theme.TealSecondary
import com.example.util.ShareUtils
import com.example.util.WordDocExporter
import com.example.viewmodel.SchemlyUiState
import com.example.viewmodel.SchemlyViewModel

@Composable
fun LessonPlanEditorScreen(
    plan: LessonPlan,
    viewModel: SchemlyViewModel,
    uiState: SchemlyUiState,
    onNavigateHome: () -> Unit,
    onOpenWordViewer: (LessonPlan) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    var schoolName by remember { mutableStateOf(plan.schoolName) }
    var date by remember { mutableStateOf(plan.date) }
    var time by remember { mutableStateOf(plan.time) }
    var roll by remember { mutableStateOf(plan.roll) }
    var knowledgeOutcome by remember { mutableStateOf(plan.knowledgeOutcome) }
    var skillOutcome by remember { mutableStateOf(plan.skillOutcome) }
    var attitudeOutcome by remember { mutableStateOf(plan.attitudeOutcome) }
    var keyInquiryQuestion by remember { mutableStateOf(plan.keyInquiryQuestion) }
    var coreCompetencies by remember { mutableStateOf(plan.coreCompetencies) }
    var values by remember { mutableStateOf(plan.values) }
    var pertAndContIssues by remember { mutableStateOf(plan.pertAndContIssues) }
    var learningResources by remember { mutableStateOf(plan.learningResources) }
    var step1Intro by remember { mutableStateOf(plan.step1Intro) }
    var step2Development by remember { mutableStateOf(plan.step2Development) }
    var step3Application by remember { mutableStateOf(plan.step3Application) }
    var step4Conclusion by remember { mutableStateOf(plan.step4Conclusion) }
    var extendedActivity by remember { mutableStateOf(plan.extendedActivity) }
    var reflection by remember { mutableStateOf(plan.reflection) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchemlyTopBar(
                title = "Lesson Plan (W${plan.week} L${plan.lessonNumber})",
                subtitle = "${plan.grade} ${plan.learningArea}",
                currentScreen = "lesson_editor",
                onNavigateHome = onNavigateHome,
                onOpenWordViewer = { onOpenWordViewer(plan) },
                onShareWordDoc = {
                    val html = WordDocExporter.generateLessonPlanWordHtml(plan)
                    ShareUtils.shareAsWordDoc(context, "${plan.learningArea}_LessonPlan_W${plan.week}_L${plan.lessonNumber}", html)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Action Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = IndigoPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "KICD LESSON PLAN TEMPLATE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    val updated = plan.copy(
                                        schoolName = schoolName,
                                        date = date,
                                        time = time,
                                        roll = roll,
                                        knowledgeOutcome = knowledgeOutcome,
                                        skillOutcome = skillOutcome,
                                        attitudeOutcome = attitudeOutcome,
                                        keyInquiryQuestion = keyInquiryQuestion,
                                        coreCompetencies = coreCompetencies,
                                        values = values,
                                        pertAndContIssues = pertAndContIssues,
                                        learningResources = learningResources,
                                        step1Intro = step1Intro,
                                        step2Development = step2Development,
                                        step3Application = step3Application,
                                        step4Conclusion = step4Conclusion,
                                        extendedActivity = extendedActivity,
                                        reflection = reflection
                                    )
                                    viewModel.saveLessonPlan(updated)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", fontSize = 11.sp, color = Color.White)
                            }

                            Button(
                                onClick = { onOpenWordViewer(plan) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Word Doc", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // Meta Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "LESSON DETAILS & SCHEDULE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        OutlinedTextField(
                            value = schoolName,
                            onValueChange = { schoolName = it },
                            label = { Text("School Name") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                label = { Text("Date (e.g. DD/MM/YYYY)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = time,
                                onValueChange = { time = it },
                                label = { Text("Time (e.g. 8:00 - 8:40 AM)") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = "${plan.grade} • Week ${plan.week} Lesson ${plan.lessonNumber}",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Grade / Week / Lesson") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = roll,
                                onValueChange = { roll = it },
                                label = { Text("Roll / Attendance") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = plan.strand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Strand") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = plan.subStrand,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Sub-strand") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Learning Outcomes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "1. SPECIFIC LEARNING OUTCOMES",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        OutlinedTextField(
                            value = knowledgeOutcome,
                            onValueChange = { knowledgeOutcome = it },
                            label = { Text("• Knowledge Outcome") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = skillOutcome,
                            onValueChange = { skillOutcome = it },
                            label = { Text("• Skill Outcome") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = attitudeOutcome,
                            onValueChange = { attitudeOutcome = it },
                            label = { Text("• Attitude Outcome") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Key Inquiry Question
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2FF))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = Color(0xFF4338CA))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "2. KEY INQUIRY QUESTION (Starts with 'How...?')",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )
                        }

                        OutlinedTextField(
                            value = keyInquiryQuestion,
                            onValueChange = { keyInquiryQuestion = it },
                            label = { Text("Inquiry Question") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Competencies, Values & PCIs
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "3. CORE COMPETENCIES, VALUES & PCIs",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        OutlinedTextField(
                            value = coreCompetencies,
                            onValueChange = { coreCompetencies = it },
                            label = { Text("Core Competencies") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = values,
                            onValueChange = { values = it },
                            label = { Text("Core Values (Respect, Integrity, Love...)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = pertAndContIssues,
                            onValueChange = { pertAndContIssues = it },
                            label = { Text("Pertinent and Contemporary Issues (PCIs)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = learningResources,
                            onValueChange = { learningResources = it },
                            label = { Text("4. Learning Resources") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Lesson Development Matrix (4 Steps)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "5. LESSON DEVELOPMENT MATRIX (40 MINUTES)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        OutlinedTextField(
                            value = step1Intro,
                            onValueChange = { step1Intro = it },
                            label = { Text("Step 1: Introduction (5 Mins)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        OutlinedTextField(
                            value = step2Development,
                            onValueChange = { step2Development = it },
                            label = { Text("Step 2: Lesson Development & Group Tasks (20 Mins)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )

                        OutlinedTextField(
                            value = step3Application,
                            onValueChange = { step3Application = it },
                            label = { Text("Step 3: Application & Formative Assessment (10 Mins)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        OutlinedTextField(
                            value = step4Conclusion,
                            onValueChange = { step4Conclusion = it },
                            label = { Text("Step 4: Conclusion & Summary (5 Mins)") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }

            // Extended Activity & Reflection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "6. EXTENDED ACTIVITY & 7. TEACHER'S REFLECTION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )

                        OutlinedTextField(
                            value = extendedActivity,
                            onValueChange = { extendedActivity = it },
                            label = { Text("6. Extended Learning / Community Activity") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        OutlinedTextField(
                            value = reflection,
                            onValueChange = { reflection = it },
                            label = { Text("7. Teacher's Self-Reflection & Remarks") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }
                }
            }

            // Bottom Save & Export Button
            item {
                Button(
                    onClick = {
                        val updated = plan.copy(
                            schoolName = schoolName,
                            date = date,
                            time = time,
                            roll = roll,
                            knowledgeOutcome = knowledgeOutcome,
                            skillOutcome = skillOutcome,
                            attitudeOutcome = attitudeOutcome,
                            keyInquiryQuestion = keyInquiryQuestion,
                            coreCompetencies = coreCompetencies,
                            values = values,
                            pertAndContIssues = pertAndContIssues,
                            learningResources = learningResources,
                            step1Intro = step1Intro,
                            step2Development = step2Development,
                            step3Application = step3Application,
                            step4Conclusion = step4Conclusion,
                            extendedActivity = extendedActivity,
                            reflection = reflection
                        )
                        viewModel.saveLessonPlan(updated)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Lesson Plan Changes", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
