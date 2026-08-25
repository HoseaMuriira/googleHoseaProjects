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
import androidx.compose.material.icons.filled.Download
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonPlan
import com.example.ui.components.DownloadDocumentDialog
import com.example.ui.components.ExportableDoc
import com.example.ui.components.MpesaPaymentDialog
import com.example.ui.components.SchemlyTopBar
import com.example.ui.components.UserAuthDialog
import com.example.ui.components.UserProfileDialog
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
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    var schoolName by remember { mutableStateOf(plan.schoolName) }
    var teacherName by remember { mutableStateOf(plan.teacherName) }
    var teacherTscNo by remember { mutableStateOf(plan.teacherTscNo) }
    var teacherContact by remember { mutableStateOf(plan.teacherContact) }
    var className by remember { mutableStateOf(plan.className) }
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
    var showDownloadDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchemlyTopBar(
                title = "Lesson Plan (W${plan.week} L${plan.lessonNumber})",
                subtitle = "${plan.grade} ${plan.learningArea}",
                currentScreen = "lesson_editor",
                currentUser = currentUser,
                onNavigateHome = onNavigateHome,
                onOpenAuth = { viewModel.showLoginDialog(true) },
                onOpenProfile = { viewModel.showProfileDialog(true) },
                onSync = { viewModel.syncUserCredentials() },
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
                                        teacherName = teacherName,
                                        teacherTscNo = teacherTscNo,
                                        teacherContact = teacherContact,
                                        className = className,
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
                                onClick = { showDownloadDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.height(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download", fontSize = 11.sp, color = Color.White)
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

            // Teacher's Information Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "TEACHER'S INFORMATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary
                            )
                            Surface(
                                color = IndigoPrimary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "Created: ${plan.formattedCreatedDateTime()}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = IndigoPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

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
                                label = { Text("TSC No. / ID") },
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
            }

            // Meta Info & Class Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "CLASS & LESSON SCHEDULE",
                            fontSize = 12.sp,
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

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = date,
                                onValueChange = { date = it },
                                label = { Text("Lesson Date (e.g. DD/MM/YYYY)") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = time,
                                onValueChange = { time = it },
                                label = { Text("Lesson Time / Duration") },
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

    if (showDownloadDialog) {
        DownloadDocumentDialog(
            doc = ExportableDoc.Lesson(plan),
            user = currentUser,
            onDismiss = { showDownloadDialog = false },
            onGatedDownload = { perform -> viewModel.performGatedDownload(perform) },
            onTopUpMpesa = { viewModel.showPaymentDialog(true) }
        )
    }

    if (uiState.isLoginDialogVisible) {
        UserAuthDialog(
            onDismiss = { viewModel.showLoginDialog(false) },
            errorMessage = uiState.authErrorMessage,
            onLoginOrRegister = { u, p, name, school, tsc, phone ->
                viewModel.loginOrRegister(u, p, name, school, tsc, phone)
            }
        )
    }

    if (uiState.isPaymentDialogVisible) {
        MpesaPaymentDialog(
            onDismiss = { viewModel.showPaymentDialog(false) },
            currentBalance = currentUser?.totalAvailableDownloads ?: 0,
            onVerifyPayment = { code, amount, count ->
                viewModel.topUpMpesaCredits(code, amount, count)
            }
        )
    }

    if (uiState.isProfileDialogVisible && currentUser != null) {
        UserProfileDialog(
            user = currentUser!!,
            onDismiss = { viewModel.showProfileDialog(false) },
            onSync = { viewModel.syncUserCredentials() },
            onTopUpMpesa = {
                viewModel.showProfileDialog(false)
                viewModel.showPaymentDialog(true)
            },
            onUpdateProfile = { updated -> viewModel.updateUserProfile(updated) },
            onLogout = { viewModel.logout() }
        )
    }
}
