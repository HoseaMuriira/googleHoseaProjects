package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LessonPlan
import com.example.data.repository.KicdCurriculumData
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.AttitudeColor
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.InquiryBadgeBg
import com.example.ui.theme.InquiryBadgeText
import com.example.ui.theme.KnowledgeColor
import com.example.ui.theme.SkillColor
import com.example.ui.theme.TealSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateLessonPlanDialog(
    initialGrade: String = "Grade 7",
    initialSubject: String = "Mathematics",
    initialWeek: Int = 1,
    initialLesson: Int = 1,
    onDismiss: () -> Unit,
    onGenerate: (LessonPlan) -> Unit,
    onBatchGenerateWeek: ((grade: String, subject: String, week: Int, school: String, teacher: String) -> Unit)? = null
) {
    var selectedGrade by remember { mutableStateOf(initialGrade) }
    var selectedSubject by remember { mutableStateOf(initialSubject) }
    var selectedWeek by remember { mutableIntStateOf(initialWeek) }
    var selectedLesson by remember { mutableIntStateOf(initialLesson) }

    var schoolName by remember { mutableStateOf("JUNIOR SECONDARY SCHOOL") }
    var teacherName by remember { mutableStateOf("") }
    var teacherTscNo by remember { mutableStateOf("") }
    var teacherContact by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("$initialGrade East") }
    val currentDateStr = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) }
    val creationTimeStr = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()) }
    var date by remember { mutableStateOf(currentDateStr) }
    var time by remember { mutableStateOf("8:00 AM - 8:40 AM") }
    var roll by remember { mutableStateOf("45 Learners") }

    var strand by remember { mutableStateOf("") }
    var subStrand by remember { mutableStateOf("") }
    var knowledgeOutcome by remember { mutableStateOf("") }
    var skillOutcome by remember { mutableStateOf("") }
    var attitudeOutcome by remember { mutableStateOf("") }
    var keyInquiryQuestion by remember { mutableStateOf("") }
    var coreCompetencies by remember { mutableStateOf("Critical thinking and problem solving, Communication and collaboration, Digital literacy") }
    var values by remember { mutableStateOf("Respect, Responsibility, Unity, Integrity, Love") }
    var pertAndContIssues by remember { mutableStateOf("Environmental awareness, Safety, Financial literacy") }
    var learningResources by remember { mutableStateOf("Curriculum design, Learner's book, Realia/Charts, Digital devices") }

    var step1Intro by remember { mutableStateOf("") }
    var step2Development by remember { mutableStateOf("") }
    var step3Application by remember { mutableStateOf("") }
    var step4Conclusion by remember { mutableStateOf("") }
    var extendedActivity by remember { mutableStateOf("") }

    var isGradeDropdownExpanded by remember { mutableStateOf(false) }
    var isSubjectDropdownExpanded by remember { mutableStateOf(false) }
    var isWeekDropdownExpanded by remember { mutableStateOf(false) }
    var isLessonDropdownExpanded by remember { mutableStateOf(false) }

    // Auto-update content from KICD curriculum repository when grade, subject, week, or lesson changes
    LaunchedEffect(selectedGrade, selectedSubject, selectedWeek, selectedLesson) {
        val scheme = KicdCurriculumData.getSchemeFor(selectedGrade, selectedSubject, "Term 1")
        val matchingRow = scheme.rows.find { it.week == selectedWeek && it.lesson == selectedLesson }
            ?: scheme.rows.firstOrNull { it.week == selectedWeek }
            ?: scheme.rows.firstOrNull()

        if (matchingRow != null) {
            strand = matchingRow.strand
            subStrand = matchingRow.subStrand
            knowledgeOutcome = matchingRow.knowledgeOutcome
            skillOutcome = matchingRow.skillOutcome
            attitudeOutcome = matchingRow.attitudeOutcome
            keyInquiryQuestion = matchingRow.keyInquiryQuestion
            learningResources = matchingRow.learningResources.ifBlank { "Curriculum design, Learner's book, Realia, Digital devices" }
            step1Intro = "Teacher reviews previous lesson with diagnostic questions. Learners link prior knowledge to $subStrand."
            step2Development = "Teacher demonstrates core methods with learning aids. Learners work in small groups with learning resources to explore $subStrand (${matchingRow.learningExperiences})."
            step3Application = "Learners solve individual practical problems on $subStrand. Teacher provides targeted scaffolding and constructive feedback."
            step4Conclusion = "Learners summarize key concepts. Teacher clarifies misconceptions and poses closing wrap-up questions."
            extendedActivity = "Learners investigate practical applications of $subStrand at home/community."
        }
    }

    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("generate_lesson_plan_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IndigoPrimary)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Generate CBC Lesson Plan",
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
                        text = "Standard KICD CBC Junior Secondary Lesson Plan Generator",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )

                    // Grade & Subject Pickers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Grade Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isGradeDropdownExpanded,
                            onExpandedChange = { isGradeDropdownExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = selectedGrade,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Grade") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGradeDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isGradeDropdownExpanded,
                                onDismissRequest = { isGradeDropdownExpanded = false }
                            ) {
                                KicdCurriculumData.GRADES.forEach { grade ->
                                    DropdownMenuItem(
                                        text = { Text(grade) },
                                        onClick = {
                                            selectedGrade = grade
                                            isGradeDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Subject Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isSubjectDropdownExpanded,
                            onExpandedChange = { isSubjectDropdownExpanded = it },
                            modifier = Modifier.weight(1.3f)
                        ) {
                            OutlinedTextField(
                                value = selectedSubject,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Learning Area") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSubjectDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isSubjectDropdownExpanded,
                                onDismissRequest = { isSubjectDropdownExpanded = false }
                            ) {
                                KicdCurriculumData.SUBJECTS.forEach { subject ->
                                    DropdownMenuItem(
                                        text = { Text(subject) },
                                        onClick = {
                                            selectedSubject = subject
                                            isSubjectDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Week & Lesson Pickers
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Week Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isWeekDropdownExpanded,
                            onExpandedChange = { isWeekDropdownExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = "Week $selectedWeek",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Week") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWeekDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isWeekDropdownExpanded,
                                onDismissRequest = { isWeekDropdownExpanded = false }
                            ) {
                                (1..9).forEach { w ->
                                    DropdownMenuItem(
                                        text = { Text("Week $w") },
                                        onClick = {
                                            selectedWeek = w
                                            isWeekDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Lesson Dropdown
                        ExposedDropdownMenuBox(
                            expanded = isLessonDropdownExpanded,
                            onExpandedChange = { isLessonDropdownExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = "Lesson $selectedLesson",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Lesson") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLessonDropdownExpanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = isLessonDropdownExpanded,
                                onDismissRequest = { isLessonDropdownExpanded = false }
                            ) {
                                (1..5).forEach { l ->
                                    DropdownMenuItem(
                                        text = { Text("Lesson $l") },
                                        onClick = {
                                            selectedLesson = l
                                            isLessonDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Strand & Sub-strand
                    OutlinedTextField(
                        value = strand,
                        onValueChange = { strand = it },
                        label = { Text("Strand") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = subStrand,
                        onValueChange = { subStrand = it },
                        label = { Text("Sub-strand") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Specific Learning Outcomes (Knowledge, Skill, Attitude)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "1. SPECIFIC LEARNING OUTCOMES (CBC K-S-A FORMAT)",
                                fontSize = 11.5.sp,
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

                    // Key Inquiry Question
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = InquiryBadgeBg)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = InquiryBadgeText, modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "2. KEY INQUIRY QUESTION (Starts with 'How...?')",
                                    fontSize = 11.5.sp,
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

                    // Quick AI Regenerator Helper
                    OutlinedButton(
                        onClick = {
                            val cleanTopic = subStrand.ifBlank { "$selectedSubject Topic" }
                            knowledgeOutcome = "Identify core concepts, state definitions, and explain rules regarding $cleanTopic."
                            skillOutcome = "Demonstrate problem-solving procedures and practical applications of $cleanTopic."
                            attitudeOutcome = "Appreciate the relevance and responsible application of $cleanTopic in everyday life."
                            keyInquiryQuestion = "How does $cleanTopic help us solve real challenges in our community?"
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Re-format Outcomes & Inquiry Question with AI", fontSize = 11.5.sp, color = IndigoPrimary)
                    }

                    // School & Class Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "INSTITUTION & CLASS DETAILS",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = schoolName,
                                    onValueChange = { schoolName = it },
                                    label = { Text("School Name") },
                                    modifier = Modifier.weight(1.2f)
                                )
                                OutlinedTextField(
                                    value = className,
                                    onValueChange = { className = it },
                                    label = { Text("Class / Stream") },
                                    placeholder = { Text("e.g. Grade 7 East") },
                                    modifier = Modifier.weight(0.8f)
                                )
                            }
                        }
                    }

                    // Teacher's Information Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "TEACHER'S INFORMATION",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )

                            OutlinedTextField(
                                value = teacherName,
                                onValueChange = { teacherName = it },
                                label = { Text("Teacher's Full Name") },
                                placeholder = { Text("e.g. Tr. Jane Wanjiku") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = teacherTscNo,
                                    onValueChange = { teacherTscNo = it },
                                    label = { Text("TSC No. / Teacher ID") },
                                    placeholder = { Text("e.g. TSC/847291") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = teacherContact,
                                    onValueChange = { teacherContact = it },
                                    label = { Text("Contact / Phone / Email") },
                                    placeholder = { Text("e.g. +254 700 000000") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    // Schedule & Creation Date/Time Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "LESSON SCHEDULE & TIMING",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = date,
                                    onValueChange = { date = it },
                                    label = { Text("Lesson Date") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = time,
                                    onValueChange = { time = it },
                                    label = { Text("Lesson Time / Duration") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = roll,
                                onValueChange = { roll = it },
                                label = { Text("Learner Roll / Attendance") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Surface(
                                color = IndigoPrimary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🕒 Plan Created Timestamp: $creationTimeStr",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = IndigoPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Bottom Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBatchGenerateWeek != null) {
                        OutlinedButton(
                            onClick = {
                                onBatchGenerateWeek(selectedGrade, selectedSubject, selectedWeek, schoolName, teacherName)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Generate Full Week (All 5)", fontSize = 11.sp)
                        }
                    } else {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                    }

                    Button(
                        onClick = {
                            val newPlan = LessonPlan(
                                id = UUID.randomUUID().toString(),
                                schoolName = schoolName,
                                teacherName = teacherName,
                                teacherTscNo = teacherTscNo,
                                teacherContact = teacherContact,
                                className = className.ifBlank { "$selectedGrade East" },
                                date = date,
                                time = time,
                                roll = roll,
                                grade = selectedGrade,
                                learningArea = selectedSubject,
                                week = selectedWeek,
                                lessonNumber = selectedLesson,
                                strand = strand,
                                subStrand = subStrand,
                                knowledgeOutcome = knowledgeOutcome,
                                skillOutcome = skillOutcome,
                                attitudeOutcome = attitudeOutcome,
                                keyInquiryQuestion = if (keyInquiryQuestion.startsWith("How", ignoreCase = true)) keyInquiryQuestion else "How does $subStrand apply in everyday life?",
                                coreCompetencies = coreCompetencies,
                                values = values,
                                pertAndContIssues = pertAndContIssues,
                                learningResources = learningResources,
                                step1Intro = step1Intro,
                                step2Development = step2Development,
                                step3Application = step3Application,
                                step4Conclusion = step4Conclusion,
                                extendedActivity = extendedActivity,
                                reflection = "Lesson outcomes achieved. Learners actively participated in practical exercises.",
                                createdAt = System.currentTimeMillis(),
                                lastModified = System.currentTimeMillis()
                            )
                            onGenerate(newPlan)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.height(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Lesson Plan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
