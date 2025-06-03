package com.example.orchidease00.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.domain.model.OrchidCatalogItem
import com.example.orchidease00.data.remote.SupabaseCatalogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.orchidease00.data.domain.model.AppUiState

sealed interface OrchidCatalogUiState {
    object Loading : OrchidCatalogUiState
    data class Success(val orchids: List<OrchidCatalogItem>) : OrchidCatalogUiState
    data class Error(val message: String) : OrchidCatalogUiState
}

class OrchidCatalogViewModel(
    private val repository: SupabaseCatalogRepository = SupabaseCatalogRepository,
    ) : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        fetchCatalog()
    }

    fun fetchCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(catalogUiState = OrchidCatalogUiState.Loading) }
            try {
                val orchids = repository.fetchCatalog()
                _uiState.update { it.copy(catalogUiState = OrchidCatalogUiState.Success(orchids)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(catalogUiState = OrchidCatalogUiState.Error(
                    e.message ?: "Unknown error"
                )
                ) }
            }
        }
    }


}

