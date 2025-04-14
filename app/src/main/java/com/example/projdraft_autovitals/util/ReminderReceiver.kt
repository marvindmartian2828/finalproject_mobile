package com.example.projdraft_autovitals.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.projdraft_autovitals.R
import com.example.projdraft_autovitals.ui.popup.ReminderPopupActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = intent.getStringExtra("service") ?: "Service Reminder"
        val dateTime = intent.getStringExtra("dateTime") ?: ""

        Log.d("ReminderReceiver", "Triggered: $service at $dateTime")
        Toast.makeText(context, "Reminder Triggered: $service", Toast.LENGTH_LONG).show()

        // Launch popup activity to visually notify user
        val popupIntent = Intent(context, ReminderPopupActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra("service", service)
            putExtra("dateTime", dateTime)
        }
        context.startActivity(popupIntent)

        // Setup notification channel for service reminders
        val channelId = "reminder_channel"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            channelId,
            "Service Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for scheduled car maintenance"
        }
        notificationManager.createNotificationChannel(channel)

        // Build and show the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Reminder: $service")
            .setContentText("Scheduled for $dateTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Only show the notification if permission is granted
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                builder.build()
            )
        } else {
            Log.w("ReminderReceiver", "POST_NOTIFICATIONS permission not granted")
        }
    }
}
