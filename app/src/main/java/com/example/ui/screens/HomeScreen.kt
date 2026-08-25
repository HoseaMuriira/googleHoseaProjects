package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork
import com.example.data.repository.KicdCurriculumData
import com.example.ui.components.CreateSchemeDialog
import com.example.ui.components.DownloadDocumentDialog
import com.example.ui.components.ExportableDoc
import com.example.ui.components.GenerateLessonPlanDialog
import com.example.ui.components.MpesaPaymentDialog
import com.example.ui.components.SchemlyTopBar
import com.example.ui.components.UserAuthDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.AttitudeColor
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.InquiryBadgeBg
import com.example.ui.theme.InquiryBadgeText
import com.example.ui.theme.KnowledgeColor
import com.example.ui.theme.SkillColor
import com.example.ui.theme.TealSecondary
import com.example.util.ShareUtils
import com.example.util.WordDocExporter
import com.example.viewmodel.SchemlyUiState
import com.example.viewmodel.SchemlyViewModel

@Composable
fun HomeScreen(
    viewModel: SchemlyViewModel,
    uiState: SchemlyUiState,
    schemes: List<SchemeOfWork>,
    lessonPlans: List<LessonPlan>,
    onOpenScheme: (SchemeOfWork) -> Unit,
    onOpenLessonPlan: (LessonPlan) -> Unit,
    onNavigateSyllabus: () -> Unit,
    onOpenWordViewer: (SchemeOfWork) -> Unit,
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

    val gradeTabs = listOf("All", "Grade 7", "Grade 8", "Grade 9")
    var selectedGradeIndex by remember { mutableIntStateOf(0) }
    var activeExportDoc by remember { mutableStateOf<ExportableDoc?>(null) }

    val filteredSchemes = schemes.filter { scheme ->
        val matchesGrade = if (selectedGradeIndex == 0) true else scheme.grade == gradeTabs[selectedGradeIndex]
        val matchesSearch = uiState.searchQuery.isBlank() ||
                scheme.learningArea.contains(uiState.searchQuery, ignoreCase = true) ||
                scheme.schoolName.contains(uiState.searchQuery, ignoreCase = true) ||
                scheme.grade.contains(uiState.searchQuery, ignoreCase = true)
        matchesGrade && matchesSearch
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchemlyTopBar(
                title = "Hostech Planner",
                subtitle = "Junior School Grade 7-9 Schemes & Lesson Plans",
                currentScreen = "home",
                currentUser = currentUser,
                onNavigateHome = {},
                onOpenAuth = { viewModel.showLoginDialog(true) },
                onOpenProfile = { viewModel.showProfileDialog(true) },
                onSync = { viewModel.syncUserCredentials() },
                onOpenAIHelper = { viewModel.showAIDialog(true) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showCreateSchemeDialog(true) },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("create_scheme_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create Scheme")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "New Scheme", fontWeight = FontWeight.Bold)
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Banner: Schemly CBC KICD Design
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = IndigoPrimary)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "KENYA CBC JUNIOR SECONDARY",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                color = AmberTertiary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "9 WEEKS • 5 LESSONS/WK",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Schemes of Work & Lesson Plan Generator",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Complete 10-column scheme layout, merged activities row, Knowledge/Skill/Attitude outcomes, and instant Word document (.doc) export.",
                            color = Color.White.copy(alpha = 0.88f),
                            fontSize = 12.5.sp,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.showCreateSchemeDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = AmberTertiary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Scheme", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { viewModel.showCreateLessonPlanDialog(true) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Generate Lesson Plan", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onNavigateSyllabus,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.MenuBook, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Syllabus", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Search Bar
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text("Search by subject, grade, or school...") },
                        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Grade Switcher Tabs (All, Grade 7, Grade 8, Grade 9)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "FILTER BY GRADE LEVEL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                    ScrollableTabRow(
                        selectedTabIndex = selectedGradeIndex,
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        divider = {}
                    ) {
                        gradeTabs.forEachIndexed { index, grade ->
                            Tab(
                                selected = selectedGradeIndex == index,
                                onClick = { selectedGradeIndex = index },
                                text = {
                                    Text(
                                        text = grade,
                                        fontWeight = if (selectedGradeIndex == index) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Subject Quick Launch Carousel
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "JUNIOR SCHOOL SUBJECTS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "KICD Curriculum",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(KicdCurriculumData.SUBJECTS) { subject ->
                            SubjectQuickChip(
                                subject = subject,
                                onClick = {
                                    val targetGrade = if (selectedGradeIndex == 0) "Grade 7" else gradeTabs[selectedGradeIndex]
                                    val existing = schemes.find { it.grade == targetGrade && it.learningArea.equals(subject, ignoreCase = true) }
                                    if (existing != null) {
                                        onOpenScheme(existing)
                                    } else {
                                        viewModel.createNewScheme(targetGrade, subject, "Term 1", "JUNIOR SECONDARY SCHOOL", "")
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Schemes of Work Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SCHEMES OF WORK",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            color = IndigoPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${filteredSchemes.size} available",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = IndigoPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Schemes List
            if (filteredSchemes.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "No schemes matching filter", fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Tap 'New Scheme' above to create a new scheme of work.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(filteredSchemes) { scheme ->
                    SchemeCardItem(
                        scheme = scheme,
                        onClick = { onOpenScheme(scheme) },
                        onWordDoc = { onOpenWordViewer(scheme) },
                        onDownload = { activeExportDoc = ExportableDoc.Scheme(scheme) },
                        onShare = {
                            val html = WordDocExporter.generateSchemeWordHtml(scheme)
                            ShareUtils.shareAsWordDoc(context, "${scheme.learningArea}_${scheme.grade}_Scheme", html)
                        },
                        onDuplicate = { viewModel.duplicateScheme(scheme) },
                        onDelete = { viewModel.deleteScheme(scheme.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // CBC Lesson Plans Section
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "GENERATED CBC LESSON PLANS (${lessonPlans.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = { viewModel.showCreateLessonPlanDialog(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ New Plan", fontSize = 11.sp, color = Color.White)
                    }
                }
            }

            if (lessonPlans.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No individual lesson plans generated yet.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedButton(
                                onClick = { viewModel.showCreateLessonPlanDialog(true) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Generate CBC Lesson Plan", fontSize = 11.5.sp)
                            }
                        }
                    }
                }
            } else {
                items(lessonPlans) { plan ->
                    LessonPlanCardItem(
                        plan = plan,
                        onClick = { onOpenLessonPlan(plan) },
                        onDownload = { activeExportDoc = ExportableDoc.Lesson(plan) },
                        onShare = {
                            val html = WordDocExporter.generateLessonPlanWordHtml(plan)
                            ShareUtils.shareAsWordDoc(context, "${plan.learningArea}_LessonPlan_W${plan.week}_L${plan.lessonNumber}", html)
                        },
                        onDelete = { viewModel.deleteLessonPlan(plan.id) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }

    activeExportDoc?.let { exportDoc ->
        DownloadDocumentDialog(
            doc = exportDoc,
            user = currentUser,
            onDismiss = { activeExportDoc = null },
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

    if (uiState.isCreateSchemeDialogVisible) {
        CreateSchemeDialog(
            onDismiss = { viewModel.showCreateSchemeDialog(false) },
            onCreate = { grade, subject, term, school, teacher ->
                viewModel.createNewScheme(grade, subject, term, school, teacher)
            }
        )
    }

    if (uiState.isCreateLessonPlanDialogVisible) {
        GenerateLessonPlanDialog(
            initialGrade = if (selectedGradeIndex == 0) "Grade 7" else gradeTabs[selectedGradeIndex],
            onDismiss = { viewModel.showCreateLessonPlanDialog(false) },
            onGenerate = { newPlan ->
                viewModel.createLessonPlanDirectly(newPlan) { plan ->
                    onOpenLessonPlan(plan)
                }
            },
            onBatchGenerateWeek = { grade, subject, week, school, teacher ->
                viewModel.batchGenerateWeekLessonPlans(grade, subject, week, school, teacher) { plans ->
                    plans.firstOrNull()?.let { onOpenLessonPlan(it) }
                }
            }
        )
    }
}

@Composable
private fun SubjectQuickChip(subject: String, onClick: () -> Unit) {
    val icon = when (subject) {
        "Mathematics" -> Icons.Default.Calculate
        "Integrated Science" -> Icons.Default.Science
        "English", "Kiswahili" -> Icons.Default.Language
        "Social Studies" -> Icons.Default.Public
        "Agriculture and Nutrition" -> Icons.Default.Yard
        "Creative Arts and Sports" -> Icons.Default.Palette
        "Christian Religious Education (CRE)" -> Icons.Default.Book
        else -> Icons.Default.MenuBook
    }

    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.height(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = subject, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@Composable
private fun SchemeCardItem(
    scheme: SchemeOfWork,
    onClick: () -> Unit,
    onWordDoc: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("scheme_card_${scheme.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = scheme.grade,
                            color = IndigoPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = TealSecondary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = scheme.term,
                            color = TealSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Text(
                    text = "${scheme.rows.size} Lessons (9 Wks)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scheme.learningArea,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = scheme.schoolName,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Activities Preview (Third row summary)
            Surface(
                color = Color(0xFFEEF2FF),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Activities: ${scheme.activitiesOverview}",
                    fontSize = 11.sp,
                    color = Color(0xFF312E81),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(6.dp))

            // Card Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(text = "Open Scheme", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDownload, modifier = Modifier.height(32.dp).width(32.dp)) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Download PDF / Word", tint = IndigoPrimary)
                    }
                    IconButton(onClick = onWordDoc, modifier = Modifier.height(32.dp).width(32.dp)) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = "Word Doc View", tint = Color(0xFF0284C7))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.height(32.dp).width(32.dp)) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.height(32.dp).width(32.dp)) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Clone", tint = MaterialTheme.colorScheme.secondary)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.height(32.dp).width(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonPlanCardItem(
    plan: LessonPlan,
    onClick: () -> Unit,
    onDownload: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("lesson_plan_card_${plan.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = AmberTertiary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "WEEK ${plan.week} • LESSON ${plan.lessonNumber}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AmberTertiary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = IndigoPrimary.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (plan.className.isNotBlank()) plan.className else plan.grade,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDownload, modifier = Modifier.height(28.dp).width(28.dp)) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = "Download PDF / Word", tint = IndigoPrimary, modifier = Modifier.height(16.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.height(28.dp).width(28.dp)) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = TealSecondary, modifier = Modifier.height(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.height(28.dp).width(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.height(16.dp))
                    }
                }
            }

            Text(
                text = "${plan.learningArea}: ${plan.subStrand.ifBlank { plan.strand }}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Teacher Info & Scheduling Meta Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(IndigoPrimary.copy(alpha = 0.05f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (plan.teacherName.isNotBlank()) "👤 Tr. ${plan.teacherName}" else "👤 Tr. Unassigned",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = IndigoPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "📅 ${if (plan.date.isNotBlank()) plan.date else "Lesson Date"} • ⏰ ${plan.time}",
                    fontSize = 10.5.sp,
                    color = Color(0xFF475569),
                    maxLines = 1
                )
            }

            // Creation timestamp tag
            Text(
                text = "🕒 Plan Created: ${plan.formattedCreatedDateTime()}",
                fontSize = 10.sp,
                color = Color(0xFF94A3B8),
                fontStyle = FontStyle.Italic
            )


            // 3-Part Objectives Preview
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "• Knowledge: ${plan.knowledgeOutcome}",
                    fontSize = 11.sp,
                    color = KnowledgeColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "• Skill: ${plan.skillOutcome}",
                    fontSize = 11.sp,
                    color = SkillColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "• Attitude: ${plan.attitudeOutcome}",
                    fontSize = 11.sp,
                    color = AttitudeColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Inquiry Question Badge
            Surface(
                color = InquiryBadgeBg,
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.HelpOutline, contentDescription = null, tint = InquiryBadgeText, modifier = Modifier.height(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = plan.keyInquiryQuestion.ifBlank { "How does this concept apply in real life?" },
                        fontSize = 11.sp,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF1E1B4B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
