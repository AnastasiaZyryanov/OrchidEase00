package com.example.orchidease00.ui.catalog

import com.example.orchidease00.data.domain.model.OrchidCatalogItem

sealed interface OrchidDetailUiState {
    object Loading : OrchidDetailUiState
    data class Success(
        val orchid: OrchidCatalogItem,
        val images: List<String>
    ) : OrchidDetailUiState

    data class Error(val message: String) : OrchidDetailUiState
}