package com.example.orchidease00.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "my_orchids")
data class MyOrchid(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val customName: String = "",
    val imagePaths: List<String> = emptyList(),
    val orchidTypeId: Int? = null,
    val purchaseDate: LocalDate? = null,
    val repotDate: LocalDate? = null,
    val bloomDate: LocalDate? = null,
    val lastWatered: LocalDate? = null,
    val nextWatering: Long? = null,
    val lastFertilizing: LocalDate? = null,
    val nextFertilizing: Long? = null,
    val wateringNotified: Boolean = false,
    val fertilizingNotified: Boolean = false

)


