package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchemeLessonRow
import com.example.ui.theme.AttitudeColor
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.InquiryBadgeBg
import com.example.ui.theme.InquiryBadgeText
import com.example.ui.theme.KnowledgeColor
import com.example.ui.theme.SkillColor
import com.example.ui.theme.TealSecondary

@Composable
fun SchemlyTableGrid(
    rows: List<SchemeLessonRow>,
    onEditRow: (Int, SchemeLessonRow) -> Unit,
    onGenerateLessonPlan: (SchemeLessonRow) -> Unit,
    onDeleteRow: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Dimensions for the 10 columns in the spreadsheet grid
    val colWidthWeek = 65.dp
    val colWidthLesson = 65.dp
    val colWidthStrand = 130.dp
    val colWidthSubStrand = 150.dp
    val colWidthOutcomes = 280.dp // Knowledge, Skill, Attitude
    val colWidthInquiry = 180.dp // Starts with How?
    val colWidthExperiences = 220.dp
    val colWidthResources = 160.dp
    val colWidthAssessment = 130.dp
    val colWidthReflection = 130.dp
    val colWidthActions = 110.dp

    val totalTableWidth = colWidthWeek + colWidthLesson + colWidthStrand +
            colWidthSubStrand + colWidthOutcomes + colWidthInquiry +
            colWidthExperiences + colWidthResources + colWidthAssessment +
            colWidthReflection + colWidthActions

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scheme_10_column_table"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Table Sub-header with Scroll Hint & Column Count
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0284C7))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "FOURTH ROW: 10-COLUMN KICD CBC SCHEME GRID (${rows.size} Lessons)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "← Swipe horizontally to view all 10 columns →",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            // HORIZONTAL SCROLLING 10-COLUMN TABLE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
            ) {
                Column(modifier = Modifier.width(totalTableWidth)) {
                    // FOURTH ROW: 10 COLUMN HEADERS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0369A1))
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableColHeader(text = "1. WEEK", width = colWidthWeek)
                        TableColHeader(text = "2. LESSON", width = colWidthLesson)
                        TableColHeader(text = "3. STRAND", width = colWidthStrand)
                        TableColHeader(text = "4. SUB-STRAND", width = colWidthSubStrand)
                        TableColHeader(
                            text = "5. SPECIFIC LEARNING OUTCOMES\n(Knowledge, Skill, Attitude)",
                            width = colWidthOutcomes
                        )
                        TableColHeader(
                            text = "6. KEY ENQUIRY QUESTIONS\n(Starts with How?)",
                            width = colWidthInquiry
                        )
                        TableColHeader(text = "7. LEARNING EXPERIENCES", width = colWidthExperiences)
                        TableColHeader(text = "8. LEARNING RESOURCES", width = colWidthResources)
                        TableColHeader(text = "9. ASSESSMENT", width = colWidthAssessment)
                        TableColHeader(text = "10. REFLECTION", width = colWidthReflection)
                        TableColHeader(text = "ACTIONS", width = colWidthActions)
                    }

                    HorizontalDivider(color = Color(0xFF0284C7), thickness = 2.dp)

                    // DATA ROWS
                    if (rows.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No lessons found in this scheme. Tap '+ Add Lesson' to create one.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        rows.forEachIndexed { index, row ->
                            val isEven = index % 2 == 0
                            val rowBg = if (isEven) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(rowBg)
                                    .clickable { onEditRow(index, row) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // 1. Week
                                TableDataCell(width = colWidthWeek, alignment = Alignment.Center) {
                                    Surface(
                                        color = IndigoPrimary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "W${row.week}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = IndigoPrimary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // 2. Lesson
                                TableDataCell(width = colWidthLesson, alignment = Alignment.Center) {
                                    Surface(
                                        color = TealSecondary.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "L${row.lesson}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = TealSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // 3. Strand
                                TableDataCell(width = colWidthStrand) {
                                    Text(
                                        text = row.strand,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // 4. Sub-strand
                                TableDataCell(width = colWidthSubStrand) {
                                    Text(
                                        text = row.subStrand,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                // 5. Specific Learning Outcomes (Knowledge, Skill, Attitude)
                                TableDataCell(width = colWidthOutcomes) {
                                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Text(
                                            text = "By the end of the lesson, learner should:",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        OutcomeBadgeItem(
                                            label = "Knowledge",
                                            text = row.knowledgeOutcome,
                                            color = KnowledgeColor
                                        )
                                        OutcomeBadgeItem(
                                            label = "Skill",
                                            text = row.skillOutcome,
                                            color = SkillColor
                                        )
                                        OutcomeBadgeItem(
                                            label = "Attitude",
                                            text = row.attitudeOutcome,
                                            color = AttitudeColor
                                        )
                                    }
                                }

                                // 6. Key Inquiry Questions (Starts with How?)
                                TableDataCell(width = colWidthInquiry) {
                                    Surface(
                                        color = InquiryBadgeBg,
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC7D2FE))
                                    ) {
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.HelpOutline,
                                                    contentDescription = "Inquiry",
                                                    tint = InquiryBadgeText,
                                                    modifier = Modifier.padding(end = 4.dp).height(12.dp)
                                                )
                                                Text(
                                                    text = "Inquiry Question:",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = InquiryBadgeText
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = row.keyInquiryQuestion.ifBlank { "How does this concept apply in real life?" },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                fontStyle = FontStyle.Italic,
                                                color = Color(0xFF1E1B4B)
                                            )
                                        }
                                    }
                                }

                                // 7. Learning Experiences
                                TableDataCell(width = colWidthExperiences) {
                                    Text(
                                        text = row.learningExperiences,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        lineHeight = 15.sp
                                    )
                                }

                                // 8. Learning Resources
                                TableDataCell(width = colWidthResources) {
                                    Text(
                                        text = row.learningResources,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // 9. Assessment
                                TableDataCell(width = colWidthAssessment) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = row.assessment,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // 10. Reflection
                                TableDataCell(width = colWidthReflection) {
                                    Text(
                                        text = row.reflection.ifBlank { "Remarks space..." },
                                        fontSize = 10.5.sp,
                                        color = if (row.reflection.isBlank()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurface,
                                        fontStyle = if (row.reflection.isBlank()) FontStyle.Italic else FontStyle.Normal
                                    )
                                }

                                // Actions (Edit / Make Lesson Plan / Delete)
                                TableDataCell(width = colWidthActions, alignment = Alignment.Center) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Button(
                                            onClick = { onGenerateLessonPlan(row) },
                                            modifier = Modifier.height(28.dp),
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = "Lesson Plan",
                                                tint = Color.White,
                                                modifier = Modifier.height(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(text = "Plan", fontSize = 10.sp, color = Color.White)
                                        }

                                        Row {
                                            IconButton(
                                                onClick = { onEditRow(index, row) },
                                                modifier = Modifier.height(28.dp).width(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Edit Row",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.height(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { onDeleteRow(index) },
                                                modifier = Modifier.height(28.dp).width(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete Row",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.height(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TableColHeader(text: String, width: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.5.sp,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun TableDataCell(
    width: androidx.compose.ui.unit.Dp,
    alignment: Alignment = Alignment.TopStart,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = alignment
    ) {
        content()
    }
}

@Composable
private fun OutcomeBadgeItem(label: String, text: String, color: Color) {
    if (text.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "• $label: ",
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = text,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 14.sp
            )
        }
    }
}
