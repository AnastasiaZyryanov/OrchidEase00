package com.example.orchidease00.ui.catalog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.remote.SupabaseCatalogRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrchidDetailViewModel(
    private val repository: SupabaseCatalogRepository = SupabaseCatalogRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrchidDetailUiState>(OrchidDetailUiState.Loading)
    val uiState: StateFlow<OrchidDetailUiState> = _uiState.asStateFlow()

    fun loadOrchidDetail(name: String) {

        viewModelScope.launch {
            _uiState.value = OrchidDetailUiState.Loading
            try {
                val cleanName = name.trim()
                val orchid = repository.getOrchidByName(cleanName)
                val images = repository.getImagesByName(cleanName)
                _uiState.value =
                    OrchidDetailUiState.Success(orchid, images.map { it.imageUrl.trim() })
            } catch (e: Exception) {
                _uiState.value = OrchidDetailUiState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }
}
