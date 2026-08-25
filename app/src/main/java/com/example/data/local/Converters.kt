package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.SchemeLessonRow
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val listType = Types.newParameterizedType(List::class.java, SchemeLessonRow::class.java)
    private val adapter = moshi.adapter<List<SchemeLessonRow>>(listType)

    @TypeConverter
    fun fromSchemeLessonRowList(value: List<SchemeLessonRow>?): String {
        return if (value == null) "[]" else adapter.toJson(value)
    }

    @TypeConverter
    fun toSchemeLessonRowList(value: String?): List<SchemeLessonRow> {
        return if (value.isNullOrBlank()) emptyList() else adapter.fromJson(value) ?: emptyList()
    }
}
