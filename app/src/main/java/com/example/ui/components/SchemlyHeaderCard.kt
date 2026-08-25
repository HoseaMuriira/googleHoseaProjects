package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchemeOfWork
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TealSecondary

@Composable
fun SchemlyHeaderCard(
    scheme: SchemeOfWork,
    onEditHeaderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("scheme_header_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Top Bar with Edit Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IndigoPrimary)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "School Icon",
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "KICD CBC SCHEME OF WORK",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 0.5.sp
                    )
                }

                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.clickable { onEditHeaderClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Header",
                            tint = Color.White,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Edit Header",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // EXACT ROW 1 & ROW 2: Header Grid
            // Row 1: SCHOOL | GRADE | LEARNING AREA | YEAR | TERM/TEACHER
            // Row 2: Values filled
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            ) {
                // ROW 1: Labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                ) {
                    HeaderCellLabel(text = "SCHOOL", modifier = Modifier.weight(2f))
                    HeaderDivider()
                    HeaderCellLabel(text = "GRADE", modifier = Modifier.weight(1.2f))
                    HeaderDivider()
                    HeaderCellLabel(text = "LEARNING AREA", modifier = Modifier.weight(2f))
                    HeaderDivider()
                    HeaderCellLabel(text = "YEAR", modifier = Modifier.weight(1f))
                    HeaderDivider()
                    HeaderCellLabel(text = "TERM & TEACHER", modifier = Modifier.weight(1.8f))
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                // ROW 2: Values (Second row for filling information)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(vertical = 10.dp, horizontal = 4.dp)
                ) {
                    HeaderCellValue(
                        text = scheme.schoolName.ifBlank { "Click to set school name" },
                        modifier = Modifier.weight(2f),
                        isBold = true
                    )
                    HeaderDivider()
                    HeaderCellValue(
                        text = scheme.grade,
                        modifier = Modifier.weight(1.2f),
                        badgeColor = IndigoPrimary
                    )
                    HeaderDivider()
                    HeaderCellValue(
                        text = scheme.learningArea,
                        modifier = Modifier.weight(2f),
                        badgeColor = TealSecondary
                    )
                    HeaderDivider()
                    HeaderCellValue(
                        text = scheme.year,
                        modifier = Modifier.weight(1f)
                    )
                    HeaderDivider()
                    HeaderCellValue(
                        text = "${scheme.term}${if (scheme.teacherName.isNotBlank()) " • Tr. ${scheme.teacherName}" else ""}",
                        modifier = Modifier.weight(1.8f)
                    )
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))

                // THIRD ROW: MERGED WITH ONLY ACTIVITIES
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEEF2FF))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "THIRD ROW (MERGED):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4338CA)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ACTIVITIES & TERM FOCUS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E1B4B)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scheme.activitiesOverview.ifBlank { "Learners engage in collaborative discussions, hands-on practical activities, digital explorations, group projects, guided problem solving, real-world investigations, and formative peer assessments." },
                            fontSize = 12.sp,
                            color = Color(0xFF1E293B),
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderCellLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 4.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun HeaderCellValue(
    text: String,
    modifier: Modifier = Modifier,
    isBold: Boolean = false,
    badgeColor: Color? = null
) {
    Box(
        modifier = modifier.padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (badgeColor != null) {
            Surface(
                color = badgeColor.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HeaderDivider() {
    VerticalDivider(
        modifier = Modifier.height(24.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    )
}
