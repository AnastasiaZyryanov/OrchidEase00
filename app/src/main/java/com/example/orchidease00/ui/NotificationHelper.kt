package com.example.orchidease00.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.widget.Toast
import java.time.LocalDate
import java.time.ZoneId


fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Event Reminders"
        val descriptionText = "Notifications about orchid care"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("orchid_events", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun scheduleNotification(context: Context, orchidName: String, triggerAt: Long) {
    //triggerAt - tempo in millisec
    val intent = Intent(context, ReminderReceiver::class.java).apply {
        putExtra("orchid_name", orchidName)
    }
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        orchidName.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    try {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    } catch (e: SecurityException) {
        e.printStackTrace()
    }

}

fun LocalDate.toMillis(): Long {
    return this.atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}



