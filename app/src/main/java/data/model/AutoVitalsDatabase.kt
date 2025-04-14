package com.example.projdraft_autovitals.data.model

import android.content.Context
import androidx.room.*

@Database(
    entities = [
        User::class,
        Car::class,
        MaintenanceRecord::class,
        ServiceReminder::class
    ],
    version = 4,  // Ensure this matches your latest schema version
    exportSchema = false
)
abstract class AutoVitalsDatabase : RoomDatabase() {

    // Provides access to DAO methods
    abstract fun autoVitalsDao(): AutoVitalsDao

    companion object {
        @Volatile private var INSTANCE: AutoVitalsDatabase? = null

        // Singleton instance of the database
        fun getDatabase(context: Context): AutoVitalsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoVitalsDatabase::class.java,
                    "auto_vitals_db"
                )
                    .fallbackToDestructiveMigration()  // Destroys old data on schema change
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
