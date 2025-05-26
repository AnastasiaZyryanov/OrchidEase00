package com.example.orchidease00.data.local


import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? = date?.toString()

    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? =
        dateString?.let { LocalDate.parse(it) }

    @TypeConverter
    fun fromList(list: List<String>): String = list.joinToString(separator = ",")

        /* @TypeConverter
    fun toList(data: String): List<String> =
        if (data.isEmpty()) emptyList() else data.split(",")

         */

    @TypeConverter
    fun toList(data: String?): List<String>? =
        data?.takeIf { it.isNotEmpty() }?.split(",") ?: emptyList()

}




