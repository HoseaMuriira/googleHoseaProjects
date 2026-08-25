package com.example.data.repository

import com.example.data.local.LessonPlanDao
import com.example.data.local.SchemeDao
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class SchemeRepository(
    private val schemeDao: SchemeDao,
    private val lessonPlanDao: LessonPlanDao
) {
    val allSchemes: Flow<List<SchemeOfWork>> = schemeDao.getAllSchemes()
    val allLessonPlans: Flow<List<LessonPlan>> = lessonPlanDao.getAllLessonPlans()

    suspend fun getSchemeById(id: String): SchemeOfWork? {
        return schemeDao.getSchemeById(id)
    }

    suspend fun saveScheme(scheme: SchemeOfWork) {
        schemeDao.insertScheme(scheme.copy(lastModified = System.currentTimeMillis()))
    }

    suspend fun deleteScheme(id: String) {
        schemeDao.deleteSchemeById(id)
    }

    suspend fun saveLessonPlan(lessonPlan: LessonPlan) {
        lessonPlanDao.insertLessonPlan(lessonPlan.copy(lastModified = System.currentTimeMillis()))
    }

    suspend fun deleteLessonPlan(id: String) {
        lessonPlanDao.deleteLessonPlanById(id)
    }

    suspend fun getLessonPlanById(id: String): LessonPlan? {
        return lessonPlanDao.getLessonPlanById(id)
    }

    /**
     * Initializes pre-loaded KICD CBC Schemes for Grade 7-9 subjects if database is empty
     */
    suspend fun seedInitialKicdSchemesIfEmpty() {
        val existing = schemeDao.getAllSchemes().firstOrNull()
        if (existing.isNullOrEmpty()) {
            val initialList = mutableListOf<SchemeOfWork>()
            // Seed a representative set of popular KICD subjects for Grades 7, 8, and 9
            for (grade in listOf("Grade 7", "Grade 8", "Grade 9")) {
                for (subject in listOf("Mathematics", "Integrated Science", "English", "Kiswahili", "Social Studies", "Agriculture and Nutrition", "Pre-Technical Studies", "Creative Arts and Sports", "Christian Religious Education (CRE)", "Business Studies")) {
                    initialList.add(KicdCurriculumData.getSchemeFor(grade, subject, "Term 1"))
                }
            }
            schemeDao.insertSchemes(initialList)
        }
    }

    /**
     * Creates a Lesson Plan draft populated from a specific SchemeLessonRow
     */
    fun createLessonPlanFromSchemeRow(scheme: SchemeOfWork, row: SchemeLessonRow): LessonPlan {
        return LessonPlan(
            id = UUID.randomUUID().toString(),
            schemeId = scheme.id,
            schoolName = scheme.schoolName,
            teacherName = scheme.teacherName,
            date = "",
            time = "8:00 AM - 8:40 AM",
            roll = "45 Learners",
            grade = scheme.grade,
            learningArea = scheme.learningArea,
            week = row.week,
            lessonNumber = row.lesson,
            strand = row.strand,
            subStrand = row.subStrand,
            knowledgeOutcome = row.knowledgeOutcome,
            skillOutcome = row.skillOutcome,
            attitudeOutcome = row.attitudeOutcome,
            keyInquiryQuestion = row.keyInquiryQuestion,
            coreCompetencies = "Critical thinking and problem solving, Communication and collaboration, Digital literacy, Self-efficacy",
            values = "Respect, Responsibility, Unity, Integrity, Love",
            pertAndContIssues = "Environmental awareness, Safety, Financial literacy, Health education",
            learningResources = row.learningResources.ifBlank { "KICD Junior School Curriculum Design, Learner's Book, Realia, Digital devices" },
            step1Intro = "Teacher reviews previous lesson through quick oral diagnostic questions. Learners connect prior concepts to ${row.subStrand}.",
            step2Development = "Teacher demonstrates core methods with learning aids. Learners work in small collaborative groups to explore concepts and solve practical tasks (${row.learningExperiences}).",
            step3Application = "Learners undertake individual and paired exercises to apply ${row.subStrand} skills. Teacher provides scaffolded feedback and formative checks.",
            step4Conclusion = "Learners summarize key learning takeaways. Teacher clarifies misconceptions and poses closing wrap-up questions.",
            extendedActivity = "Learners observe real-life applications at home/community and note observations in their exercise books.",
            reflection = "Lesson objectives achieved satisfactorily. Majority of learners engaged actively in hands-on group tasks.",
            lastModified = System.currentTimeMillis()
        )
    }
}
