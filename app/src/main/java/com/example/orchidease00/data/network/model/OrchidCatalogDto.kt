package com.example.orchidease00.data.network.model

import com.example.orchidease00.data.OrchidCatalogItem
import kotlinx.serialization.Serializable

@Serializable
data class OrchidCatalogDto(
    val id: Int,
    val name: String,
    val description: String,
    val care: String
)

fun OrchidCatalogDto.toUiModel(): OrchidCatalogItem {
    return OrchidCatalogItem(
        id = this.id,
        name = this.name,
        care = this.care,
        description = this.description
    )
}

