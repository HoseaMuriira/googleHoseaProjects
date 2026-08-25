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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchemeLessonRow
import com.example.data.repository.KicdCurriculumData
import com.example.ui.components.SchemlyTopBar
import com.example.ui.theme.AttitudeColor
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.InquiryBadgeBg
import com.example.ui.theme.InquiryBadgeText
import com.example.ui.theme.KnowledgeColor
import com.example.ui.theme.SkillColor
import com.example.ui.theme.TealSecondary
import com.example.viewmodel.SchemlyViewModel

@Composable
fun SyllabusGuideScreen(
    viewModel: SchemlyViewModel,
    onNavigateHome: () -> Unit,
    onGenerateFromSyllabus: (grade: String, subject: String) -> Unit,
    onGenerateLessonPlan: (grade: String, subject: String, row: SchemeLessonRow) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedGrade by remember { mutableStateOf("Grade 7") }
    var selectedSubject by remember { mutableStateOf("Mathematics") }

    val syllabusScheme = remember(selectedGrade, selectedSubject) {
        KicdCurriculumData.getSchemeFor(selectedGrade, selectedSubject, "Term 1")
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchemlyTopBar(
                title = "KICD Curriculum Guide",
                subtitle = "Grade 7-9 Junior Secondary Design",
                currentScreen = "syllabus",
                onNavigateHome = onNavigateHome
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndigoPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "KICD Junior School Curriculum Design",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Browse official curriculum strands, sub-strands, specific learning outcomes (Knowledge, Skill, Attitude), and key inquiry questions starting with 'How...?'.",
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // Grade Selector
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SELECT GRADE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KicdCurriculumData.GRADES.forEach { grade ->
                            ElevatedFilterChip(
                                selected = selectedGrade == grade,
                                onClick = { selectedGrade = grade },
                                label = { Text(grade) },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Subject Selector
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "SELECT LEARNING AREA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(KicdCurriculumData.SUBJECTS) { subject ->
                            ElevatedFilterChip(
                                selected = selectedSubject == subject,
                                onClick = { selectedSubject = subject },
                                label = { Text(subject) },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = TealSecondary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Create Scheme from this Syllabus Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$selectedGrade • $selectedSubject",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "9 Weeks • 45 Total Lessons • 10 Columns Grid",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { onGenerateFromSyllabus(selectedGrade, selectedSubject) },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.height(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Scheme", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // Syllabus Lessons List
            item {
                Text(
                    text = "SYLLABUS LESSON OUTLINE (WEEKS 1 TO 9)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            items(syllabusScheme.rows) { row ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = IndigoPrimary.copy(alpha = 0.12f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "Week ${row.week} • Lesson ${row.lesson}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = row.strand,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Button(
                                onClick = { onGenerateLessonPlan(selectedGrade, selectedSubject, row) },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary.copy(alpha = 0.9f)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.height(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Plan", fontSize = 11.sp, color = Color.White)
                            }
                        }

                        Text(
                            text = "Sub-strand: ${row.subStrand}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // 3 Outcomes
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(text = "• Knowledge: ${row.knowledgeOutcome}", fontSize = 11.sp, color = KnowledgeColor)
                            Text(text = "• Skill: ${row.skillOutcome}", fontSize = 11.sp, color = SkillColor)
                            Text(text = "• Attitude: ${row.attitudeOutcome}", fontSize = 11.sp, color = AttitudeColor)
                        }

                        // Inquiry
                        Surface(
                            color = InquiryBadgeBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = InquiryBadgeText, modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = row.keyInquiryQuestion,
                                    fontSize = 11.sp,
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF1E1B4B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
