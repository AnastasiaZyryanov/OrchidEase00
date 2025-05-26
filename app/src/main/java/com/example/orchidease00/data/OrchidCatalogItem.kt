package com.example.orchidease00.data

import androidx.room.Entity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//@Entity(tableName = "orchid_catalog")
@Serializable
data class OrchidCatalogItem(
    val id: Int,
    val name: String,
    val description: String,
    val care: String,
    @SerialName("imageUrl")
    val imageUrl: String? = null
)