package com.example.projdraft_autovitals.ui.viewmodel

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projdraft_autovitals.R
import com.example.projdraft_autovitals.data.model.*
import com.example.projdraft_autovitals.util.ReminderReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.firstOrNull
import java.util.*

class AutoVitalsViewModel(private val repository: AutoVitalsRepository) : ViewModel() {

    // Holds current logged-in user (based on session userId)
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    // Loads user details by ID and emits them to _user flow
    fun loadUserById(userId: Int) = viewModelScope.launch {
        repository.getUserById(userId).collect { _user.value = it }
    }

    // Registers a new user
    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    // Checks if the username already exists in DB (returns true if available)
    suspend fun isUsernameAvailable(username: String): Boolean {
        return repository.getUserByUsername(username).firstOrNull() == null
    }

    // Updates all user fields
    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    // Updates user password only
    fun updateUserPassword(userId: Int, newPassword: String) = viewModelScope.launch {
        repository.updateUserPassword(userId, newPassword)
    }

    // Holds list of all maintenance records for a selected car
    private val _maintenanceRecords = MutableStateFlow<List<MaintenanceRecord>>(emptyList())
    val maintenanceRecords: StateFlow<List<MaintenanceRecord>> = _maintenanceRecords

    // Loads all maintenance records for a car and emits to flow
    fun loadMaintenanceRecords(carId: Int) = viewModelScope.launch {
        repository.getMaintenanceRecordsByCar(carId).collect {
            _maintenanceRecords.value = it
        }
    }

    // Adds a new maintenance record
    fun insertMaintenanceRecord(record: MaintenanceRecord) = viewModelScope.launch {
        repository.insertMaintenanceRecord(record)
    }

    // Deletes a record by its ID
    fun deleteMaintenanceRecordById(recordId: Int) = viewModelScope.launch {
        repository.deleteMaintenanceRecordById(recordId)
    }

    // Inserts or updates a reminder and schedules a notification
    fun insertServiceReminder(
        context: Context,
        carId: Int,
        service: String,
        date: String,
        time: String,
        notes: String?
    ) {
        viewModelScope.launch {
            val existing = repository.getReminderForService(carId, service)
            val reminder = ServiceReminder(
                id = existing?.id ?: 0,
                carId = carId,
                service = service,
                reminderDate = date,
                reminderTime = time,
                notes = notes,
                notificationSent = false
            )
            repository.insertServiceReminder(reminder)
            scheduleReminderNotification(context, service, date, time)
        }
    }

    // Direct access to reminder object (used to pre-fill reminder dialog)
    suspend fun getReminderDirect(carId: Int, service: String): ServiceReminder? {
        return repository.getReminderForService(carId, service)
    }

    /**
     * Schedules an alarm at exact date+time to trigger a service reminder.
     * Uses Android AlarmManager to wake and trigger ReminderReceiver.
     */
    private fun scheduleReminderNotification(context: Context, service: String, date: String, time: String) {
        try {
            val calendar = Calendar.getInstance()
            val parts = date.split("-") + time.split(":")
            val (year, month, day, hour, minute) = parts.map { it.toInt() }

            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month - 1)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            val intent = Intent(context, ReminderReceiver::class.java).apply {
                putExtra("service", service)
                putExtra("dateTime", "$date $time")
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (service + date + time).hashCode(),
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                } else {
                    Log.w("AutoVitalsVM", "Exact alarms not permitted")
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            Log.e("AutoVitalsVM", "Alarm permission not granted", e)
        }
    }


    // Show Notification Immediately
    private fun showNotification(context: Context, title: String, content: String) {
        val channelId = "reminder_channel"
        val notificationId = System.currentTimeMillis().toInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Service Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled car maintenance"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(notificationId, builder.build())
            } else {
                Log.w("AutoVitalsVM", "POST_NOTIFICATIONS permission not granted.")
            }
        } else {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        }
    }
}