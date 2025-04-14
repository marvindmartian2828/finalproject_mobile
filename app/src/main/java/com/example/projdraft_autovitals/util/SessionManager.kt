package com.example.projdraft_autovitals.util

import android.content.Context

// Manages user session using SharedPreferences
class SessionManager(context: Context) {

    // Reference to shared preferences file
    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    // Saves the logged-in user's ID
    fun saveUserId(userId: Int) {
        prefs.edit().putInt("USER_ID", userId).apply()
    }

    // Retrieves the stored user ID, or -1 if none found
    fun getUserId(): Int {
        return prefs.getInt("USER_ID", -1)
    }

    // Clears all session data
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
