package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
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
    val isAIGeneratorDialogVisible: Boolean = false,
    val snackbarMessage: String? = null
)

class SchemlyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SchemeRepository
    val schemes: StateFlow<List<SchemeOfWork>>
    val lessonPlans: StateFlow<List<LessonPlan>>

    private val _uiState = MutableStateFlow(SchemlyUiState())
    val uiState: StateFlow<SchemlyUiState> = _uiState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SchemeRepository(database.schemeDao(), database.lessonPlanDao())

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

    fun generateLessonPlanFromRow(scheme: SchemeOfWork, row: SchemeLessonRow) {
        viewModelScope.launch {
            val plan = repository.createLessonPlanFromSchemeRow(scheme, row)
            repository.saveLessonPlan(plan)
            _uiState.value = _uiState.value.copy(
                activeLessonPlan = plan,
                docView = CurrentDocView.LessonDoc(plan),
                snackbarMessage = "Generated Lesson Plan for Week ${row.week} Lesson ${row.lesson}!"
            )
        }
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
