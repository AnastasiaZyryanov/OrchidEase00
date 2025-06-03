package com.example.orchidease00.ui.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.orchidease00.R
import com.example.orchidease00.data.local.MyOrchidDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = MyOrchidDatabase.getDatabase(context)
            val dao = db.myOrchidDao()
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

            val currentTime = System.currentTimeMillis()

            val wateringEvents = dao.getWateringEventsToNotify(currentTime)
            for (orchid in wateringEvents) {
                val builder = NotificationCompat.Builder(context, "orchid_events")
                    .setSmallIcon(R.drawable.orchid)
                    .setContentTitle("Cura delle orchidee")
                    .setContentText("È ora di innaffiare \"${orchid.name}\"")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                notificationManager.notify(orchid.id, builder.build())

                dao.markWateringAsNotified(orchid.id)
            }


            val fertilizingEvents = dao.getFertilizingEventsToNotify(currentTime)
            for (orchid in fertilizingEvents) {
                val builder = NotificationCompat.Builder(context, "orchid_events")
                    .setSmallIcon(R.drawable.orchid)
                    .setContentTitle("Cura delle orchidee")
                    .setContentText("È ora di fertilizzare \"${orchid.name}\"")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)

                notificationManager.notify(orchid.id + 10_000, builder.build())

                dao.markFertilizingAsNotified(orchid.id)
            }
        }
    }
}


