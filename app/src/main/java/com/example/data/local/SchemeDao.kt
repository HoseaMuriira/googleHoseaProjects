package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork
import kotlinx.coroutines.flow.Flow

@Dao
interface SchemeDao {
    @Query("SELECT * FROM schemes_of_work ORDER BY lastModified DESC")
    fun getAllSchemes(): Flow<List<SchemeOfWork>>

    @Query("SELECT * FROM schemes_of_work WHERE id = :id")
    suspend fun getSchemeById(id: String): SchemeOfWork?

    @Query("SELECT * FROM schemes_of_work WHERE grade = :grade AND learningArea = :learningArea LIMIT 1")
    suspend fun getSchemeByGradeAndArea(grade: String, learningArea: String): SchemeOfWork?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScheme(scheme: SchemeOfWork)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchemes(schemes: List<SchemeOfWork>)

    @Update
    suspend fun updateScheme(scheme: SchemeOfWork)

    @Delete
    suspend fun deleteScheme(scheme: SchemeOfWork)

    @Query("DELETE FROM schemes_of_work WHERE id = :id")
    suspend fun deleteSchemeById(id: String)
}

@Dao
interface LessonPlanDao {
    @Query("SELECT * FROM lesson_plans ORDER BY lastModified DESC")
    fun getAllLessonPlans(): Flow<List<LessonPlan>>

    @Query("SELECT * FROM lesson_plans WHERE id = :id")
    suspend fun getLessonPlanById(id: String): LessonPlan?

    @Query("SELECT * FROM lesson_plans WHERE schemeId = :schemeId ORDER BY week ASC, lessonNumber ASC")
    fun getLessonPlansForScheme(schemeId: String): Flow<List<LessonPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLessonPlan(lessonPlan: LessonPlan)

    @Delete
    suspend fun deleteLessonPlan(lessonPlan: LessonPlan)

    @Query("DELETE FROM lesson_plans WHERE id = :id")
    suspend fun deleteLessonPlanById(id: String)
}
