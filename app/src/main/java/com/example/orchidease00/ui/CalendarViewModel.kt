package com.example.orchidease00.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.OrchidEvent
import com.example.orchidease00.data.local.MyOrchidDao

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


import kotlinx.coroutines.launch
import kotlin.collections.flatMap


class CalendarViewModel(
    private val myOrchidDao: MyOrchidDao
) : ViewModel() {

    private val _events = MutableStateFlow<List<OrchidEvent>>(emptyList())
    val events: StateFlow<List<OrchidEvent>> = _events

    init {
        viewModelScope.launch {
            myOrchidDao.getAll().collect { orchidList ->
                val allEvents = orchidList.flatMap { orchid ->
                    listOfNotNull(
                        orchid.lastWatered?.let {
                            OrchidEvent(orchid.name, it, "La scorsa irrigazione di")
                        },
                        orchid.nextWatering?.let {
                            OrchidEvent(orchid.name, it, "La prossima irrigazione di")
                        },
                        orchid.lastFertilizing?.let {
                            OrchidEvent(orchid.name, it, "La scorsa fertilizzazione di")
                        },
                        orchid.nextFertilizing?.let {
                            OrchidEvent(orchid.name, it, "La prossima fertilizzazione di")
                        },
                        orchid.purchaseDate?.let {
                            OrchidEvent(orchid.name, it, "Comprato")
                        },
                        orchid.repotDate?.let {
                            OrchidEvent(orchid.name, it, "Ultimo svasamento di")
                        },
                        orchid.bloomDate?.let {
                            OrchidEvent(orchid.name, it, "Fioritura di")
                        }
                    )
                }
                _events.value = allEvents
            }
        }
    }
}


