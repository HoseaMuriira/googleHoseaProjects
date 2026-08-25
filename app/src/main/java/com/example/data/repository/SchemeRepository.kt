package com.example.data.repository

import com.example.data.local.LessonPlanDao
import com.example.data.local.SchemeDao
import com.example.data.local.UserDao
import com.example.data.model.LessonPlan
import com.example.data.model.PaymentTransaction
import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
import com.example.data.model.UserAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class SchemeRepository(
    private val schemeDao: SchemeDao,
    private val lessonPlanDao: LessonPlanDao,
    private val userDao: UserDao
) {
    val allSchemes: Flow<List<SchemeOfWork>> = schemeDao.getAllSchemes()
    val allLessonPlans: Flow<List<LessonPlan>> = lessonPlanDao.getAllLessonPlans()
    val currentUser: Flow<UserAccount?> = userDao.getActiveUser()

    suspend fun getCurrentUser(): UserAccount? {
        return userDao.getActiveUserSync()
    }

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
     * User Login or Registration with Username and Password.
     * New users automatically receive 3 free downloads.
     * Existing users have their credentials and download balance retained.
     */
    suspend fun loginOrRegister(
        username: String,
        password: String,
        fullName: String = "",
        schoolName: String = "JUNIOR SECONDARY SCHOOL",
        tscNumber: String = "",
        phone: String = ""
    ): Pair<Boolean, String> {
        val trimmedUsername = username.trim()
        val trimmedPassword = password.trim()

        if (trimmedUsername.isBlank() || trimmedPassword.isBlank()) {
            return Pair(false, "Username and password cannot be empty")
        }

        userDao.logoutAll()
        val existing = userDao.getUserByUsername(trimmedUsername)

        if (existing != null) {
            if (existing.passwordHash == trimmedPassword) {
                val updated = existing.copy(
                    isLoggedIn = true,
                    lastSyncedAt = System.currentTimeMillis(),
                    fullName = if (fullName.isNotBlank()) fullName else existing.fullName,
                    schoolName = if (schoolName.isNotBlank()) schoolName else existing.schoolName,
                    tscNumber = if (tscNumber.isNotBlank()) tscNumber else existing.tscNumber,
                    phone = if (phone.isNotBlank()) phone else existing.phone
                )
                userDao.updateUser(updated)
                return Pair(true, "Welcome back, $trimmedUsername! Logged in successfully.")
            } else {
                return Pair(false, "Incorrect password for user $trimmedUsername.")
            }
        } else {
            // New user gets 3 free downloads
            val newUser = UserAccount(
                username = trimmedUsername,
                passwordHash = trimmedPassword,
                fullName = fullName.ifBlank { trimmedUsername },
                schoolName = schoolName.ifBlank { "JUNIOR SECONDARY SCHOOL" },
                tscNumber = tscNumber,
                phone = phone,
                freeDownloadsRemaining = 3,
                paidDownloadsRemaining = 0,
                totalDownloadsUsed = 0,
                isLoggedIn = true,
                createdAt = System.currentTimeMillis(),
                lastSyncedAt = System.currentTimeMillis()
            )
            userDao.insertUser(newUser)
            return Pair(true, "Account created! You have 3 free downloads available.")
        }
    }

    suspend fun logout() {
        userDao.logoutAll()
    }

    suspend fun syncUser(): UserAccount? {
        val current = userDao.getActiveUserSync() ?: return null
        val updated = current.copy(lastSyncedAt = System.currentTimeMillis())
        userDao.updateUser(updated)
        return updated
    }

    suspend fun updateUserProfile(user: UserAccount) {
        userDao.updateUser(user.copy(lastSyncedAt = System.currentTimeMillis()))
    }

    /**
     * Consumes 1 download quota for document export.
     * Returns true if quota was successfully deducted, or false if insufficient balance.
     */
    suspend fun tryConsumeDownloadQuota(): Pair<Boolean, String> {
        val user = userDao.getActiveUserSync()
        if (user == null) {
            return Pair(false, "Please log in to download documents.")
        }

        if (user.freeDownloadsRemaining > 0) {
            val updated = user.copy(
                freeDownloadsRemaining = user.freeDownloadsRemaining - 1,
                totalDownloadsUsed = user.totalDownloadsUsed + 1,
                lastSyncedAt = System.currentTimeMillis()
            )
            userDao.updateUser(updated)
            val left = updated.freeDownloadsRemaining
            return Pair(true, "Download allowed (${left} free download${if (left == 1) "" else "s"} remaining)")
        } else if (user.paidDownloadsRemaining > 0) {
            val updated = user.copy(
                paidDownloadsRemaining = user.paidDownloadsRemaining - 1,
                totalDownloadsUsed = user.totalDownloadsUsed + 1,
                lastSyncedAt = System.currentTimeMillis()
            )
            userDao.updateUser(updated)
            val left = updated.paidDownloadsRemaining
            return Pair(true, "Download allowed (${left} paid credit${if (left == 1) "" else "s"} remaining)")
        } else {
            return Pair(false, "Download limit reached. Please top up KES 10 per download via M-PESA 0748053644.")
        }
    }

    /**
     * Records M-PESA confirmation and adds download credits (1 download = KES 10)
     */
    suspend fun addMpesaDownloadCredits(
        transactionCode: String,
        amountKes: Int,
        downloadsCount: Int
    ): Triple<Boolean, String, PaymentTransaction?> {
        val user = userDao.getActiveUserSync() ?: return Triple(false, "Please log in first.", null)
        val cleanCode = transactionCode.trim().uppercase()
        if (cleanCode.length < 5) {
            return Triple(false, "Please enter a valid M-PESA confirmation code.", null)
        }

        val transaction = PaymentTransaction(
            transactionId = cleanCode,
            username = user.username,
            amountKes = amountKes,
            downloadsAdded = downloadsCount,
            recipientPhone = "0748053644",
            timestamp = System.currentTimeMillis(),
            status = "CONFIRMED"
        )
        userDao.insertTransaction(transaction)

        val updatedUser = user.copy(
            paidDownloadsRemaining = user.paidDownloadsRemaining + downloadsCount,
            lastSyncedAt = System.currentTimeMillis()
        )
        userDao.updateUser(updatedUser)

        return Triple(true, "Payment verified! Added $downloadsCount download${if (downloadsCount == 1) "" else "s"} to your account.", transaction)
    }

    fun getTransactions(username: String): Flow<List<PaymentTransaction>> {
        return userDao.getTransactionsForUser(username)
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

        // Check if there is an active user; if none exists, check if any accounts exist or auto-create default user
        val activeUser = userDao.getActiveUserSync()
        if (activeUser == null) {
            val allUsers = userDao.getAllUsers()
            if (allUsers.isNotEmpty()) {
                // Auto-login last saved user
                val lastUser = allUsers.first()
                userDao.loginUser(lastUser.username)
            }
        }
    }

    /**
     * Creates a Lesson Plan draft populated from a specific SchemeLessonRow
     */
    fun createLessonPlanFromSchemeRow(scheme: SchemeOfWork, row: SchemeLessonRow): LessonPlan {
        val todayStr = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        return LessonPlan(
            id = UUID.randomUUID().toString(),
            schemeId = scheme.id,
            schoolName = scheme.schoolName,
            teacherName = scheme.teacherName,
            teacherTscNo = "",
            teacherContact = "",
            className = "${scheme.grade} East",
            date = todayStr,
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
            createdAt = System.currentTimeMillis(),
            lastModified = System.currentTimeMillis()
        )
    }
}
