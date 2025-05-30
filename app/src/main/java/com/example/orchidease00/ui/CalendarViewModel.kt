package com.example.orchidease00.ui

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.OrchidEvent
import com.example.orchidease00.data.local.MyOrchidDao

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
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
                            val localDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                            OrchidEvent(orchid.name, localDate, "La prossima irrigazione di")
                        },
                        orchid.lastFertilizing?.let {
                            OrchidEvent(orchid.name, it, "La scorsa fertilizzazione di")
                        },
                        orchid.nextFertilizing?.let {
                            val localDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                            OrchidEvent(orchid.name, localDate, "La prossima fertilizzazione di")
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
/*
   fun scheduleOrchidReminder(context: Context, orchidName: String, dateTimeMillis: Long) {
        Log.d("Reminder", "Запланировано: $orchidName в ${Date(dateTimeMillis)}")
        Toast.makeText(context, "Reminder set for ${Date(dateTimeMillis)}", Toast.LENGTH_SHORT).show()

        scheduleNotification(context, orchidName, dateTimeMillis)
    }

 */
fun scheduleOrchidReminder(context: Context, orchidName: String, dateTimeMillis: Long) {
    val dateTime = Instant.ofEpochMilli(dateTimeMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    Log.d("Reminder", "Запланировано: $orchidName в $dateTime")
    Toast.makeText(
        context,
        "Reminder set for ${dateTime.format(DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm"))}",
        Toast.LENGTH_SHORT
    ).show()

    scheduleNotification(context, orchidName, dateTimeMillis)
}


}


