package com.example.projdraft_autovitals.ui.popup

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.example.projdraft_autovitals.R

class ReminderPopupActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure popup window size and background dim
        window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        setContentView(R.layout.activity_reminder_popup)

        // Extract reminder data from intent extras
        val service = intent.getStringExtra("service") ?: "Service Reminder"
        val dateTime = intent.getStringExtra("dateTime") ?: "Unknown Time"

        // Populate UI elements with reminder details
        findViewById<TextView>(R.id.popupServiceTitle).text =
            getString(R.string.reminder_title, service)

        findViewById<TextView>(R.id.popupServiceTime).text =
            getString(R.string.reminder_time, dateTime)

        // Close popup when button is clicked
        findViewById<Button>(R.id.btnClosePopup).setOnClickListener {
            finish()
        }
    }
}
