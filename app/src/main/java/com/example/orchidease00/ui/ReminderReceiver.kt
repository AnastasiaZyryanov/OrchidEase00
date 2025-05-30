package com.example.orchidease00.ui

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.orchidease00.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val orchidName = intent.getStringExtra("orchid_name") ?: "Orchid"

        val builder = NotificationCompat.Builder(context, "orchid_events")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            //.setSmallIcon(R.drawable.ic_orchid)
            .setContentTitle("Cura delle orchidee")
            .setContentText("Ora di curare \"$orchidName\"")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

         try {
                val notificationManager = ContextCompat.getSystemService(
                    context,
                    NotificationManager::class.java
                ) as NotificationManager
                notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }

}
