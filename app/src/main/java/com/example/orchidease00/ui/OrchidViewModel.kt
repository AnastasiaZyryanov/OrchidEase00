package com.example.orchidease00.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.orchidease00.data.AppUiState
import com.example.orchidease00.data.OrchidCatalogItem
import kotlinx.coroutines.flow.StateFlow
import com.example.orchidease00.data.local.MyOrchid
import com.example.orchidease00.data.local.MyOrchidDatabase
import com.example.orchidease00.data.network.SupabaseHttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class OrchidViewModel(application: Application) : AndroidViewModel(application) {

    private val db = MyOrchidDatabase.getDatabase(application)
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()



    init {
        Log.d("MyOrchidViewModel", "ViewModel created")
        // Carica dati da Room
        viewModelScope.launch {
            db.myOrchidDao().getAll().collect { orchids ->
                _uiState.update { it.copy(myOrchids = orchids) }
            }
        }

        // Carica dati da Supabase
        viewModelScope.launch {
            val catalog = SupabaseHttpClient.getOrchidCatalog()
            _uiState.update { it.copy(orchids = catalog) }
        }
    }

    fun insertMyOrchid(orchid: MyOrchid) {
        viewModelScope.launch(Dispatchers.IO) {
            db.myOrchidDao().insert(orchid)
        }
    }

    fun getMyOrchidById(id: Int): Flow<MyOrchid?> {
        return db.myOrchidDao().getById(id)
    }

    fun updateMyOrchid(orchid: MyOrchid) {
        viewModelScope.launch(Dispatchers.IO) {
            db.myOrchidDao().update(orchid)
        }
    }

    fun deleteMyOrchid(orchid: MyOrchid) {
        viewModelScope.launch(Dispatchers.IO) {
            db.myOrchidDao().delete(orchid)
        }
    }

    fun getCatalogItemById(id: Int): OrchidCatalogItem? {
        return _uiState.value.orchids.find { it.id == id }
    }

}


