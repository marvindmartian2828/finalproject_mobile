package com.example.projdraft_autovitals.data.model

import androidx.room.*

@Entity(
    tableName = "maintenance_records",
    foreignKeys = [

        // Each maintenance record is tied to a specific car; deleting the car deletes its records
        ForeignKey(
            entity = Car::class,
            parentColumns = ["id"],
            childColumns = ["car_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["car_id"])]
)
data class MaintenanceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Unique ID for the record

    @ColumnInfo(name = "car_id")
    val carId: Int,  // Foreign key to the Car table

    val service: String,  // Type of maintenance service
    val serviceDate: String,  // Date the service was performed
    val notes: String? = null,  // Optional notes about the service

    val reminderSet: Boolean = false  // Indicates if a reminder was set for this record
)
