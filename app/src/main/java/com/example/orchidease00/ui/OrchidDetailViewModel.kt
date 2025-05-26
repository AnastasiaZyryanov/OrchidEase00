package com.example.orchidease00.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.network.SupabaseCatalogRepository
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
                Log.d("OrchidDetail", "Carica dettagli per: [$name], dopo trim(): [$cleanName]")

                val orchid = repository.getOrchidByName(cleanName)
                Log.d("OrchidDetail", "Trovata orchidea: ${orchid.name}")

                val images = repository.getImagesByName(cleanName)
                Log.d("OrchidDetail", "Trovate imagini: ${images.size}")
                _uiState.value = OrchidDetailUiState.Success(orchid, images.map { it.imageUrl.trim() })
            } catch (e: Exception) {
                Log.e("OrchidDetail", "Errore durante caricamento dettagli: ${e.message}", e)
                _uiState.value = OrchidDetailUiState.Error(e.message ?: "Errore sconosciuto")
            }
        }
    }
}
