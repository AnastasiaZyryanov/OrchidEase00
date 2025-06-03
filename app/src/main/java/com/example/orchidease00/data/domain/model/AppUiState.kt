package com.example.orchidease00.data.domain.model

import com.example.orchidease00.data.local.MyOrchid
import com.example.orchidease00.ui.catalog.OrchidCatalogUiState

data class AppUiState(
    val orchids: List<OrchidCatalogItem> = emptyList(),
    val orchidImages: List<OrchidCatalogItem> = emptyList(),
    val myOrchids: List<MyOrchid> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val catalogUiState: OrchidCatalogUiState = OrchidCatalogUiState.Loading,
)
