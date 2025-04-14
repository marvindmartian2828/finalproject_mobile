package com.example.projdraft_autovitals.data.model

import androidx.room.*

@Entity(
    tableName = "cars",
    foreignKeys = [
        ForeignKey(
            // Each car is linked to a user; deleting the user also deletes their cars
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)

data class Car(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Unique ID for each car
    @ColumnInfo(name = "user_id") val userId: Int,   // Foreign key to the User table
    @ColumnInfo(name = "car_name") val carName: String,  // Display name
    val make: String,   // Car manufacturer
    val model: String,  // Car model name
    val year: Int,      // Manufacturing year
    val mileage: Int,   // Current mileage
    val vin: String,    // Vehicle Identification Number

    @ColumnInfo(name = "image_uri") val imageUri: String? = null  // Path to the saved car image (stored as URI string)
)

