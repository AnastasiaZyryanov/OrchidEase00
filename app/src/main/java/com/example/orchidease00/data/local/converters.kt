package com.example.orchidease00.data.local


import androidx.room.TypeConverter
import java.time.LocalDate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? =
        dateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromImagePathList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toImagePathList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}




