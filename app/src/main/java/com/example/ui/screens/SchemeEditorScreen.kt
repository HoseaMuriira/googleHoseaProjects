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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
import com.example.ui.components.AIEnhancerDialog
import com.example.ui.components.DownloadDocumentDialog
import com.example.ui.components.EditHeaderDialog
import com.example.ui.components.EditRowDialog
import com.example.ui.components.ExportableDoc
import com.example.ui.components.MpesaPaymentDialog
import com.example.ui.components.PaymentStatusDialog
import com.example.ui.components.SchemlyHeaderCard
import com.example.ui.components.SchemlyTableGrid
import com.example.ui.components.SchemlyTopBar
import com.example.ui.components.UserAuthDialog
import com.example.ui.components.UserProfileDialog
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TealSecondary
import com.example.util.ShareUtils
import com.example.util.WordDocExporter
import com.example.viewmodel.SchemlyUiState
import com.example.viewmodel.SchemlyViewModel

@Composable
fun SchemeEditorScreen(
    scheme: SchemeOfWork,
    viewModel: SchemlyViewModel,
    uiState: SchemlyUiState,
    onNavigateHome: () -> Unit,
    onOpenWordViewer: (SchemeOfWork) -> Unit,
    onOpenLessonPlan: (LessonPlan) -> Unit,
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

    var selectedWeekFilter by remember { mutableIntStateOf(0) } // 0 = All Weeks, 1..9 = Specific week
    var showDownloadDialog by remember { mutableStateOf(false) }

    val displayedRows = if (selectedWeekFilter == 0) {
        scheme.rows
    } else {
        scheme.rows.filter { it.week == selectedWeekFilter }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchemlyTopBar(
                title = "${scheme.learningArea} - ${scheme.grade}",
                subtitle = "${scheme.schoolName} • ${scheme.term} ${scheme.year}",
                currentScreen = "scheme_editor",
                currentUser = currentUser,
                onNavigateHome = onNavigateHome,
                onOpenAuth = { viewModel.showLoginDialog(true) },
                onOpenProfile = { viewModel.showProfileDialog(true) },
                onSync = { viewModel.syncUserCredentials() },
                onOpenWordViewer = { onOpenWordViewer(scheme) },
                onShareWordDoc = {
                    val html = WordDocExporter.generateSchemeWordHtml(scheme)
                    ShareUtils.shareAsWordDoc(context, "${scheme.learningArea}_${scheme.grade}_Scheme", html)
                },
                onOpenAIHelper = { viewModel.showAIDialog(true) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val targetWeek = if (selectedWeekFilter == 0) 1 else selectedWeekFilter
                    viewModel.addNewLessonRow(targetWeek)
                },
                containerColor = IndigoPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("add_lesson_fab")
            ) {
                Row(modifier = Modifier.padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Lesson")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Lesson", fontWeight = FontWeight.Bold)
                }
            }
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
            // Document Action Bar (Word Doc Export / AI / Save status)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                color = Color(0xFF0284C7).copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "10-COLUMN CBC SCHEME",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0284C7),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = {
                                    val targetWeek = if (selectedWeekFilter == 0) 1 else selectedWeekFilter
                                    viewModel.batchGenerateForSchemeWeek(scheme, targetWeek) { plans ->
                                        plans.firstOrNull()?.let { onOpenLessonPlan(it) }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (selectedWeekFilter == 0) "Generate W1 Plans" else "Generate W$selectedWeekFilter Plans",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }

                            Button(
                                onClick = { showDownloadDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Download", fontSize = 11.sp, color = Color.White)
                            }

                            Button(
                                onClick = { onOpenWordViewer(scheme) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Word Doc", fontSize = 11.sp, color = Color.White)
                            }

                            Button(
                                onClick = {
                                    val html = WordDocExporter.generateSchemeWordHtml(scheme)
                                    ShareUtils.shareAsWordDoc(context, "${scheme.learningArea}_${scheme.grade}_Scheme", html)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }

            // EXACT ROW 1, 2, 3: Schemly Header Card (School, Grade, Area, Year + Filled Row + Merged Activities Row)
            item {
                SchemlyHeaderCard(
                    scheme = scheme,
                    onEditHeaderClick = { viewModel.showHeaderEditDialog(true) }
                )
            }

            // Week Filter Chips (All Weeks, Week 1 to Week 9)
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "WEEK SELECTOR (9 WEEKS CURRICULUM)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Showing ${displayedRows.size} of ${scheme.rows.size} Lessons",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item {
                            ElevatedFilterChip(
                                selected = selectedWeekFilter == 0,
                                onClick = { selectedWeekFilter = 0 },
                                label = { Text("All Weeks (1-9)") },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        items((1..9).toList()) { weekNum ->
                            val count = scheme.rows.count { it.week == weekNum }
                            ElevatedFilterChip(
                                selected = selectedWeekFilter == weekNum,
                                onClick = { selectedWeekFilter = weekNum },
                                label = { Text("Week $weekNum ($count)") },
                                colors = FilterChipDefaults.elevatedFilterChipColors(
                                    selectedContainerColor = IndigoPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // EXACT ROW 4: 10-Column Interactive Table Grid
            item {
                SchemlyTableGrid(
                    rows = displayedRows,
                    onEditRow = { index, row ->
                        // Find global index in scheme.rows
                        val globalIndex = scheme.rows.indexOf(row).let { if (it >= 0) it else index }
                        viewModel.openEditRowDialog(globalIndex, row)
                    },
                    onGenerateLessonPlan = { row ->
                        viewModel.generateLessonPlanFromRow(scheme, row) { createdPlan ->
                            onOpenLessonPlan(createdPlan)
                        }
                    },
                    onDeleteRow = { index ->
                        val row = displayedRows.getOrNull(index)
                        if (row != null) {
                            val globalIndex = scheme.rows.indexOf(row)
                            if (globalIndex >= 0) viewModel.deleteLessonRow(globalIndex)
                        }
                    }
                )
            }
        }
    }

    // Dialogs
    if (uiState.isHeaderEditDialogVisible) {
        EditHeaderDialog(
            scheme = scheme,
            onDismiss = { viewModel.showHeaderEditDialog(false) },
            onSave = { school, grade, area, year, term, teacher, activities ->
                viewModel.updateSchemeHeader(school, grade, area, year, term, teacher, activities)
            }
        )
    }

    if (uiState.isEditingRowDialogVisible && uiState.editingRow != null) {
        EditRowDialog(
            initialRow = uiState.editingRow,
            onDismiss = { viewModel.closeEditRowDialog() },
            onSave = { updatedRow ->
                viewModel.saveEditingRow(updatedRow)
            }
        )
    }

    if (showDownloadDialog) {
        DownloadDocumentDialog(
            doc = ExportableDoc.Scheme(scheme),
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
            userPhone = currentUser?.phone ?: "",
            onVerifyPayment = { code, amount, count ->
                viewModel.topUpMpesaCredits(code, amount, count)
            }
        )
    }

    if (uiState.isPaymentSuccessDialogVisible && uiState.paymentStatusTransaction != null) {
        PaymentStatusDialog(
            transaction = uiState.paymentStatusTransaction!!,
            currentUser = currentUser,
            onDismiss = { viewModel.dismissPaymentStatusDialog() }
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

    if (uiState.isAIGeneratorDialogVisible) {
        AIEnhancerDialog(
            subject = scheme.learningArea,
            grade = scheme.grade,
            onDismiss = { viewModel.showAIDialog(false) },
            onApplyOutcomes = { knowledge, skill, attitude, inquiry ->
                // Apply to active editing row if present or snackbar notification
                viewModel.showAIDialog(false)
            }
        )
    }
}
