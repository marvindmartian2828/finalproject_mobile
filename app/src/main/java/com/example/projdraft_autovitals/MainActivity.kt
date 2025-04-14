package com.example.projdraft_autovitals

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.runtime.remember
import com.google.android.libraries.places.api.Places
import androidx.navigation.compose.rememberNavController
import com.example.projdraft_autovitals.data.model.AutoVitalsDatabase
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.ui.navigation.NavGraph
import com.example.projdraft_autovitals.util.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request exact alarm permission on Android 12+ (for scheduling reminders)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }

        // Initialize Google Places API (for service center search)
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyA6NjXQDn6GS4ifB-1y8-WuWEvsD1DRymQ")
        }

        setContent {
            // Compose navigation and dependency setup
            val context = this
            val navController = rememberNavController()
            val database = AutoVitalsDatabase.getDatabase(context)
            val repository = remember { AutoVitalsRepository(database.autoVitalsDao()) }
            val sessionManager = remember { SessionManager(context) }

            NavGraph(
                navController = navController,
                repository = repository,
                sessionManager = sessionManager
            )
        }
    }
}