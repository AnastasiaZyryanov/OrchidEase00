package com.example.orchidease00.data.network.model

import com.example.orchidease00.data.OrchidCatalogItem
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrchidImageDto(
    val id: Int,
    val name: String,
    @SerialName("imageUrl")
    val imageUrl: String
)


