package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "schemes_of_work")
data class SchemeOfWork(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val schoolName: String = "JUNIOR SECONDARY SCHOOL",
    val grade: String = "Grade 7", // Grade 7, Grade 8, Grade 9
    val learningArea: String = "Mathematics", // Subject
    val year: String = "2026",
    val term: String = "Term 1",
    val teacherName: String = "",
    val activitiesOverview: String = "Learners engage in collaborative discussions, hands-on practical activities, digital explorations, group projects, guided problem solving, real-world investigations, and formative peer assessments.",
    val rows: List<SchemeLessonRow> = emptyList(),
    val lastModified: Long = System.currentTimeMillis()
)

data class SchemeLessonRow(
    val id: String = UUID.randomUUID().toString(),
    val week: Int = 1,
    val lesson: Int = 1,
    val strand: String = "",
    val subStrand: String = "",
    val knowledgeOutcome: String = "",
    val skillOutcome: String = "",
    val attitudeOutcome: String = "",
    val keyInquiryQuestion: String = "", // Starts with "How...?"
    val learningExperiences: String = "",
    val learningResources: String = "",
    val assessment: String = "Oral questions, Observation, Written exercise",
    val reflection: String = ""
) {
    /**
     * Formatted Specific Learning Outcomes combining Knowledge, Skill, Attitude
     */
    fun formattedOutcomes(): String {
        val parts = mutableListOf<String>()
        if (knowledgeOutcome.isNotBlank()) parts.add("• Knowledge: $knowledgeOutcome")
        if (skillOutcome.isNotBlank()) parts.add("• Skill: $skillOutcome")
        if (attitudeOutcome.isNotBlank()) parts.add("• Attitude: $attitudeOutcome")
        return if (parts.isEmpty()) "By the end of the lesson, the learner should be able to achieve the sub-strand competencies."
        else "By the end of the lesson, the learner should be able to:\n" + parts.joinToString("\n")
    }
}

@Entity(tableName = "lesson_plans")
data class LessonPlan(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val schemeId: String? = null,
    val schoolName: String = "JUNIOR SECONDARY SCHOOL",
    val teacherName: String = "",
    val teacherTscNo: String = "",
    val teacherContact: String = "",
    val className: String = "Grade 7 East", // Class / Stream e.g. Grade 7 East, 8B
    val date: String = "",
    val time: String = "8:00 AM - 8:40 AM",
    val roll: String = "45 Learners",
    val grade: String = "Grade 7",
    val learningArea: String = "Mathematics",
    val week: Int = 1,
    val lessonNumber: Int = 1,
    val strand: String = "",
    val subStrand: String = "",
    val knowledgeOutcome: String = "",
    val skillOutcome: String = "",
    val attitudeOutcome: String = "",
    val keyInquiryQuestion: String = "", // Starts with "How...?"
    val coreCompetencies: String = "Critical thinking and problem solving, Communication and collaboration, Digital literacy",
    val values: String = "Respect, Responsibility, Unity, Integrity",
    val pertAndContIssues: String = "Environmental awareness, Financial literacy, Safety",
    val learningResources: String = "Curriculum design, Learner's book, Realia/Charts, Digital devices",
    val step1Intro: String = "Teacher reviews previous lesson through quick oral questions. Learners connect prior knowledge to new sub-strand.",
    val step2Development: String = "Teacher introduces the core concept with interactive demonstration. Learners work in small groups with learning resources to explore and solve tasks.",
    val step3Application: String = "Learners complete individual and paired practical exercises. Teacher moves around providing targeted feedback and scaffolding.",
    val step4Conclusion: String = "Learners summarize key takeaways. Teacher reinforces core knowledge, skills, and values. Oral wrap-up questions.",
    val extendedActivity: String = "Learners explore real-life application at home / community and note observations.",
    val reflection: String = "Most learners grasped the concepts effectively. A few required additional guided practice in group activities.",
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
) {
    fun formattedCreatedDateTime(): String {
        return try {
            val sdf = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault())
            sdf.format(java.util.Date(createdAt))
        } catch (e: Exception) {
            "25 Aug 2026, 10:45 AM"
        }
    }
}
