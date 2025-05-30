package com.example.orchidease00.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverter
import java.time.LocalDate
import com.example.orchidease00.data.OrchidCatalogItem

/*
@Entity(
    tableName = "my_orchids",
    foreignKeys = [
        ForeignKey(
            entity = OrchidCatalogItem::class,
            parentColumns = ["id"],
            childColumns = ["orchidTypeId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)*/
@Entity(tableName = "my_orchids")
data class MyOrchid(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val customName: String = "",
    //val imageUris: List<String> = emptyList(),
    val filePath: String ="",
    val orchidTypeId: Int? = null, // foreign key
    val purchaseDate: LocalDate? = null,
    val repotDate: LocalDate? = null,
    val bloomDate: LocalDate? = null,
    val lastWatered: LocalDate? = null,
    val nextWatering: Long? = null,
    val lastFertilizing: LocalDate? = null,
    val nextFertilizing: Long? = null,
       )


