package com.example.orchidease00.ui.calendar

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orchidease00.data.domain.model.OrchidEvent
import com.example.orchidease00.data.local.MyOrchidDao
import com.example.orchidease00.ui.receiver.ReminderReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
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
                            OrchidEvent(
                                orchid.id,
                                orchid.name,
                                it,
                                "La scorsa irrigazione di ${orchid.name}"
                            )
                        },
                        orchid.nextWatering?.let {
                            if (!orchid.wateringNotified) {
                                val localDate =
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                OrchidEvent(
                                    orchid.id,
                                    orchid.name,
                                    localDate,
                                    "La prossima irrigazione di ${orchid.name}"
                                )
                            } else null
                        },
                        orchid.lastFertilizing?.let {
                            OrchidEvent(
                                orchid.id,
                                orchid.name,
                                it,
                                "La scorsa fertilizzazione di ${orchid.name}"
                            )
                        },
                        orchid.nextFertilizing?.let {
                            if (!orchid.fertilizingNotified) {
                                val localDate =
                                    Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                OrchidEvent(
                                    orchid.id,
                                    orchid.name,
                                    localDate,
                                    "La prossima fertilizzazione di ${orchid.name}"
                                )
                            } else null
                        },
                        orchid.purchaseDate?.let {
                            OrchidEvent(orchid.id, orchid.name, it, "Comprato ${orchid.name}")
                        },
                        orchid.repotDate?.let {
                            OrchidEvent(
                                orchid.id,
                                orchid.name,
                                it,
                                "Ultimo svasamento di ${orchid.name}"
                            )
                        },
                        orchid.bloomDate?.let {
                            OrchidEvent(orchid.id, orchid.name, it, "Fioritura di ${orchid.name}")
                        }
                    )
                }
                _events.value = allEvents
            }
        }
    }

    fun scheduleOrchidReminder(
        context: Context,
        orchidId: Int,
        orchidName: String,
        triggerAtMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val settingsIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                if (context is Activity) {
                    context.startActivity(settingsIntent)
                } else {
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(settingsIntent)
                }
                return
            }
        }

        val reminderIntent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("orchid_id", orchidId)
            putExtra("orchid_name", orchidName)

        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            orchidId,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }
}



