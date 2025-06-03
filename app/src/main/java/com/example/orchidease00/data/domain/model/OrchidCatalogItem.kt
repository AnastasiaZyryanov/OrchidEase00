package com.example.orchidease00.data.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OrchidCatalogItem(
    val id: Int,
    val name: String,
    val description: String,
    val care: String,
    @SerialName("imageUrl")
    val imageUrl: String? = null
)