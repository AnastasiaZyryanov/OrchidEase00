package com.example.orchidease00.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.OrchidCatalogItem
import com.example.orchidease00.data.network.SupabaseCatalogRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.orchidease00.data.AppUiState

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
             /*   val orchids = listOf(
                    OrchidCatalogItem(id = 1, name = "Phalaenopsis", care = "annoffiare 1 volta alla settimana", description = "Moth orchid"),
                    OrchidCatalogItem(id = 2, name = "Cattleya", care = "annoffiare 2 volta alla settimana", description = "Queen of orchids")
                )
*/
                Log.d("ViewModel", "Fetched ${orchids.size} orchids")
                _uiState.update { it.copy(catalogUiState = OrchidCatalogUiState.Success(orchids)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(catalogUiState = OrchidCatalogUiState.Error(e.message ?: "Unknown error")) }
            }
        }
    }


}

