package com.example.projdraft_autovitals.data.model

import androidx.room.*

@Entity(
    tableName = "service_reminders",
    foreignKeys = [
        ForeignKey(
            // Each reminder is associated with a specific car; deleting the car deletes its reminders
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["car_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["car_id"])]
)

data class ServiceReminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Unique ID for the reminder
    @ColumnInfo(name = "car_id") val carId: Int,  // Foreign key referencing the associated car
    val service: String,  // Name of the service
    val reminderDate: String,  // Date the reminder is scheduled for
    val reminderTime: String,  // Time the reminder is scheduled for
    val notes: String? = null,  // Optional notes related to the reminder
    val notificationSent: Boolean = false  // Tracks whether the notification has been triggered
)
