package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.LessonPlan
import com.example.data.model.PaymentTransaction
import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
import com.example.data.model.UserAccount
import com.example.data.repository.KicdCurriculumData
import com.example.data.repository.SchemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

sealed interface CurrentDocView {
    data class SchemeDoc(val scheme: SchemeOfWork) : CurrentDocView
    data class LessonDoc(val plan: LessonPlan) : CurrentDocView
}

data class SchemlyUiState(
    val selectedGrade: String = "All", // "All", "Grade 7", "Grade 8", "Grade 9"
    val selectedSubject: String = "All",
    val searchQuery: String = "",
    val activeScheme: SchemeOfWork? = null,
    val activeLessonPlan: LessonPlan? = null,
    val docView: CurrentDocView? = null,
    val isEditingRowDialogVisible: Boolean = false,
    val editingRowIndex: Int = -1,
    val editingRow: SchemeLessonRow? = null,
    val isHeaderEditDialogVisible: Boolean = false,
    val isCreateSchemeDialogVisible: Boolean = false,
    val isCreateLessonPlanDialogVisible: Boolean = false,
    val isAIGeneratorDialogVisible: Boolean = false,
    val isLoginDialogVisible: Boolean = false,
    val isPaymentDialogVisible: Boolean = false,
    val isProfileDialogVisible: Boolean = false,
    val authErrorMessage: String? = null,
    val snackbarMessage: String? = null
)

class SchemlyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchemeRepository
    val schemes: StateFlow<List<SchemeOfWork>>
    val lessonPlans: StateFlow<List<LessonPlan>>
    val currentUser: StateFlow<UserAccount?>

    private val _uiState = MutableStateFlow(SchemlyUiState())
    val uiState: StateFlow<SchemlyUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SchemeRepository(database.schemeDao(), database.lessonPlanDao(), database.userDao())

        schemes = repository.allSchemes.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        lessonPlans = repository.allLessonPlans.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        currentUser = repository.currentUser.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

        viewModelScope.launch {
            repository.seedInitialKicdSchemesIfEmpty()
        }
    }

    fun setGradeFilter(grade: String) {
        _uiState.value = _uiState.value.copy(selectedGrade = grade)
    }

    fun setSubjectFilter(subject: String) {
        _uiState.value = _uiState.value.copy(selectedSubject = subject)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectScheme(scheme: SchemeOfWork) {
        _uiState.value = _uiState.value.copy(
            activeScheme = scheme,
            docView = CurrentDocView.SchemeDoc(scheme)
        )
    }

    fun selectLessonPlan(plan: LessonPlan) {
        _uiState.value = _uiState.value.copy(
            activeLessonPlan = plan,
            docView = CurrentDocView.LessonDoc(plan)
        )
    }

    fun createNewScheme(grade: String, subject: String, term: String, schoolName: String, teacherName: String) {
        viewModelScope.launch {
            val template = KicdCurriculumData.getSchemeFor(grade, subject, term).copy(
                id = UUID.randomUUID().toString(),
                schoolName = schoolName.ifBlank { "JUNIOR SECONDARY SCHOOL" },
                teacherName = teacherName,
                lastModified = System.currentTimeMillis()
            )
            repository.saveScheme(template)
            _uiState.value = _uiState.value.copy(
                activeScheme = template,
                docView = CurrentDocView.SchemeDoc(template),
                isCreateSchemeDialogVisible = false,
                snackbarMessage = "Created $grade $subject Scheme of Work!"
            )
        }
    }

    fun updateSchemeHeader(
        schoolName: String,
        grade: String,
        learningArea: String,
        year: String,
        term: String,
        teacherName: String,
        activitiesOverview: String
    ) {
        val current = _uiState.value.activeScheme ?: return
        val updated = current.copy(
            schoolName = schoolName,
            grade = grade,
            learningArea = learningArea,
            year = year,
            term = term,
            teacherName = teacherName,
            activitiesOverview = activitiesOverview,
            lastModified = System.currentTimeMillis()
        )
        _uiState.value = _uiState.value.copy(
            activeScheme = updated,
            docView = CurrentDocView.SchemeDoc(updated),
            isHeaderEditDialogVisible = false
        )
        viewModelScope.launch {
            repository.saveScheme(updated)
        }
    }

    fun openEditRowDialog(index: Int, row: SchemeLessonRow) {
        _uiState.value = _uiState.value.copy(
            isEditingRowDialogVisible = true,
            editingRowIndex = index,
            editingRow = row
        )
    }

    fun closeEditRowDialog() {
        _uiState.value = _uiState.value.copy(
            isEditingRowDialogVisible = false,
            editingRowIndex = -1,
            editingRow = null
        )
    }

    fun saveEditingRow(updatedRow: SchemeLessonRow) {
        val currentScheme = _uiState.value.activeScheme ?: return
        val index = _uiState.value.editingRowIndex
        if (index < 0 || index >= currentScheme.rows.size) return

        val newRows = currentScheme.rows.toMutableList()
        newRows[index] = updatedRow

        val updatedScheme = currentScheme.copy(
            rows = newRows,
            lastModified = System.currentTimeMillis()
        )

        _uiState.value = _uiState.value.copy(
            activeScheme = updatedScheme,
            docView = CurrentDocView.SchemeDoc(updatedScheme),
            isEditingRowDialogVisible = false,
            editingRowIndex = -1,
            editingRow = null,
            snackbarMessage = "Updated Week ${updatedRow.week} Lesson ${updatedRow.lesson}"
        )

        viewModelScope.launch {
            repository.saveScheme(updatedScheme)
        }
    }

    fun addNewLessonRow(afterWeek: Int) {
        val currentScheme = _uiState.value.activeScheme ?: return
        val currentRows = currentScheme.rows.toMutableList()
        val weekLessons = currentRows.filter { it.week == afterWeek }
        val nextLessonNum = (weekLessons.maxOfOrNull { it.lesson } ?: 0) + 1

        val newRow = SchemeLessonRow(
            id = UUID.randomUUID().toString(),
            week = afterWeek,
            lesson = nextLessonNum,
            strand = currentRows.lastOrNull { it.week == afterWeek }?.strand ?: "Curriculum Strand",
            subStrand = "New Sub-strand Concept & Practical Skills",
            knowledgeOutcome = "Identify core principles and state key facts related to the sub-strand.",
            skillOutcome = "Demonstrate accurate problem solving and practical operations in the learning area.",
            attitudeOutcome = "Appreciate the real-world value and utility of the learned competency.",
            keyInquiryQuestion = "How can we apply these concepts to solve problems in our everyday life?",
            learningExperiences = "Learners engage in collaborative discussions, hands-on tasks, and peer presentations.",
            learningResources = "KICD Junior School Curriculum Design, Learner's Book, Realia, Digital devices",
            assessment = "Oral questions, Observation checklist, Written exercise",
            reflection = ""
        )

        val insertIndex = currentRows.indexOfLast { it.week <= afterWeek }.let { if (it >= 0) it + 1 else currentRows.size }
        currentRows.add(insertIndex, newRow)

        val updatedScheme = currentScheme.copy(rows = currentRows, lastModified = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(
            activeScheme = updatedScheme,
            docView = CurrentDocView.SchemeDoc(updatedScheme),
            snackbarMessage = "Added Lesson $nextLessonNum to Week $afterWeek"
        )

        viewModelScope.launch {
            repository.saveScheme(updatedScheme)
        }
    }

    fun deleteLessonRow(index: Int) {
        val currentScheme = _uiState.value.activeScheme ?: return
        if (index < 0 || index >= currentScheme.rows.size) return

        val currentRows = currentScheme.rows.toMutableList()
        val removed = currentRows.removeAt(index)

        val updatedScheme = currentScheme.copy(rows = currentRows, lastModified = System.currentTimeMillis())
        _uiState.value = _uiState.value.copy(
            activeScheme = updatedScheme,
            docView = CurrentDocView.SchemeDoc(updatedScheme),
            snackbarMessage = "Deleted Week ${removed.week} Lesson ${removed.lesson}"
        )

        viewModelScope.launch {
            repository.saveScheme(updatedScheme)
        }
    }

    fun generateLessonPlanFromRow(
        scheme: SchemeOfWork,
        row: SchemeLessonRow,
        onCreated: ((LessonPlan) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val plan = repository.createLessonPlanFromSchemeRow(scheme, row)
            repository.saveLessonPlan(plan)
            _uiState.value = _uiState.value.copy(
                activeLessonPlan = plan,
                docView = CurrentDocView.LessonDoc(plan),
                snackbarMessage = "Generated Lesson Plan for Week ${row.week} Lesson ${row.lesson}!"
            )
            onCreated?.invoke(plan)
        }
    }

    fun createLessonPlanDirectly(plan: LessonPlan, onCreated: ((LessonPlan) -> Unit)? = null) {
        viewModelScope.launch {
            repository.saveLessonPlan(plan)
            _uiState.value = _uiState.value.copy(
                activeLessonPlan = plan,
                docView = CurrentDocView.LessonDoc(plan),
                isCreateLessonPlanDialogVisible = false,
                snackbarMessage = "Generated ${plan.grade} ${plan.learningArea} Lesson Plan!"
            )
            onCreated?.invoke(plan)
        }
    }

    fun batchGenerateWeekLessonPlans(
        grade: String,
        subject: String,
        week: Int,
        schoolName: String,
        teacherName: String,
        onCreated: ((List<LessonPlan>) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val scheme = KicdCurriculumData.getSchemeFor(grade, subject, "Term 1").copy(
                schoolName = schoolName.ifBlank { "JUNIOR SECONDARY SCHOOL" },
                teacherName = teacherName
            )
            val weekRows = scheme.rows.filter { it.week == week }.ifEmpty { scheme.rows.take(5) }
            val createdPlans = mutableListOf<LessonPlan>()

            weekRows.forEach { row ->
                val plan = repository.createLessonPlanFromSchemeRow(scheme, row)
                repository.saveLessonPlan(plan)
                createdPlans.add(plan)
            }

            _uiState.value = _uiState.value.copy(
                activeLessonPlan = createdPlans.firstOrNull(),
                docView = createdPlans.firstOrNull()?.let { CurrentDocView.LessonDoc(it) },
                isCreateLessonPlanDialogVisible = false,
                snackbarMessage = "Batch generated ${createdPlans.size} Lesson Plans for Week $week!"
            )
            onCreated?.invoke(createdPlans)
        }
    }

    fun batchGenerateForSchemeWeek(
        scheme: SchemeOfWork,
        week: Int,
        onCreated: ((List<LessonPlan>) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val weekRows = scheme.rows.filter { it.week == week }.ifEmpty { scheme.rows.take(5) }
            val createdPlans = mutableListOf<LessonPlan>()

            weekRows.forEach { row ->
                val plan = repository.createLessonPlanFromSchemeRow(scheme, row)
                repository.saveLessonPlan(plan)
                createdPlans.add(plan)
            }

            _uiState.value = _uiState.value.copy(
                activeLessonPlan = createdPlans.firstOrNull(),
                docView = createdPlans.firstOrNull()?.let { CurrentDocView.LessonDoc(it) },
                snackbarMessage = "Generated all ${createdPlans.size} Lesson Plans for Week $week!"
            )
            onCreated?.invoke(createdPlans)
        }
    }

    fun showCreateLessonPlanDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isCreateLessonPlanDialogVisible = show)
    }

    fun saveLessonPlan(updatedPlan: LessonPlan) {
        _uiState.value = _uiState.value.copy(
            activeLessonPlan = updatedPlan,
            docView = CurrentDocView.LessonDoc(updatedPlan),
            snackbarMessage = "Saved Lesson Plan!"
        )
        viewModelScope.launch {
            repository.saveLessonPlan(updatedPlan)
        }
    }

    fun deleteLessonPlan(id: String) {
        viewModelScope.launch {
            repository.deleteLessonPlan(id)
            _uiState.value = _uiState.value.copy(
                activeLessonPlan = null,
                docView = _uiState.value.activeScheme?.let { CurrentDocView.SchemeDoc(it) },
                snackbarMessage = "Lesson plan deleted."
            )
        }
    }

    fun deleteScheme(id: String) {
        viewModelScope.launch {
            repository.deleteScheme(id)
            _uiState.value = _uiState.value.copy(
                activeScheme = null,
                docView = null,
                snackbarMessage = "Scheme of Work deleted."
            )
        }
    }

    fun duplicateScheme(scheme: SchemeOfWork) {
        viewModelScope.launch {
            val cloned = scheme.copy(
                id = UUID.randomUUID().toString(),
                schoolName = scheme.schoolName,
                learningArea = "${scheme.learningArea} (Copy)",
                lastModified = System.currentTimeMillis()
            )
            repository.saveScheme(cloned)
            _uiState.value = _uiState.value.copy(
                activeScheme = cloned,
                docView = CurrentDocView.SchemeDoc(cloned),
                snackbarMessage = "Duplicated ${scheme.learningArea} Scheme!"
            )
        }
    }

    fun showHeaderEditDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isHeaderEditDialogVisible = show)
    }

    fun showCreateSchemeDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isCreateSchemeDialogVisible = show)
    }

    fun showAIDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isAIGeneratorDialogVisible = show)
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }

    fun showLoginDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLoginDialogVisible = show,
            authErrorMessage = null
        )
    }

    fun showPaymentDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isPaymentDialogVisible = show)
    }

    fun showProfileDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(isProfileDialogVisible = show)
    }

    fun loginOrRegister(
        username: String,
        password: String,
        fullName: String = "",
        schoolName: String = "JUNIOR SECONDARY SCHOOL",
        tscNumber: String = "",
        phone: String = "",
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val (success, message) = repository.loginOrRegister(
                username = username,
                password = password,
                fullName = fullName,
                schoolName = schoolName,
                tscNumber = tscNumber,
                phone = phone
            )
            if (success) {
                _uiState.value = _uiState.value.copy(
                    isLoginDialogVisible = false,
                    authErrorMessage = null,
                    snackbarMessage = message
                )
                onSuccess?.invoke()
            } else {
                _uiState.value = _uiState.value.copy(
                    authErrorMessage = message,
                    snackbarMessage = message
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.value = _uiState.value.copy(
                isProfileDialogVisible = false,
                snackbarMessage = "Logged out successfully."
            )
        }
    }

    fun syncUserCredentials() {
        viewModelScope.launch {
            val updated = repository.syncUser()
            if (updated != null) {
                _uiState.value = _uiState.value.copy(
                    snackbarMessage = "User credentials & download quota synchronized! (${updated.totalAvailableDownloads} downloads available)"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoginDialogVisible = true,
                    snackbarMessage = "Please log in to synchronize."
                )
            }
        }
    }

    fun updateUserProfile(user: UserAccount) {
        viewModelScope.launch {
            repository.updateUserProfile(user)
            _uiState.value = _uiState.value.copy(
                isProfileDialogVisible = false,
                snackbarMessage = "Teacher profile updated."
            )
        }
    }

    /**
     * Attempts to consume a download quota credit.
     * If user is not logged in, opens login dialog.
     * If user has 0 downloads left, opens M-PESA payment dialog.
     * If user has downloads available, decrements quota and executes onPermitted.
     */
    fun performGatedDownload(
        onPermitted: () -> Unit
    ) {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user == null) {
                _uiState.value = _uiState.value.copy(
                    isLoginDialogVisible = true,
                    snackbarMessage = "Please log in to download documents (3 Free downloads for new users!)"
                )
                return@launch
            }

            val (allowed, msg) = repository.tryConsumeDownloadQuota()
            if (allowed) {
                _uiState.value = _uiState.value.copy(snackbarMessage = msg)
                onPermitted()
            } else {
                _uiState.value = _uiState.value.copy(
                    isPaymentDialogVisible = true,
                    snackbarMessage = msg
                )
            }
        }
    }

    fun topUpMpesaCredits(
        transactionCode: String,
        amountKes: Int,
        downloadsCount: Int,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            val (success, message) = repository.addMpesaDownloadCredits(
                transactionCode = transactionCode,
                amountKes = amountKes,
                downloadsCount = downloadsCount
            )
            _uiState.value = _uiState.value.copy(
                isPaymentDialogVisible = !success,
                snackbarMessage = message
            )
            if (success) {
                onSuccess?.invoke()
            }
        }
    }

    fun viewSchemeDoc(scheme: SchemeOfWork) {
        _uiState.value = _uiState.value.copy(
            activeScheme = scheme,
            docView = CurrentDocView.SchemeDoc(scheme)
        )
    }

    fun viewLessonPlanDoc(plan: LessonPlan) {
        _uiState.value = _uiState.value.copy(
            activeLessonPlan = plan,
            docView = CurrentDocView.LessonDoc(plan)
        )
    }
}
