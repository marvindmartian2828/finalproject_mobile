package com.example.projdraft_autovitals.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Represents a registered user in the system
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,         // Auto-generated unique ID for each user

    val name: String,        // Full name of the user
    val username: String,    // Unique username for login
    val email: String,       // Email address of the user
    val phone: String,       // 10-digit phone number
    val passwordHash: String // Plain or hashed password (depending on future upgrades)
)
