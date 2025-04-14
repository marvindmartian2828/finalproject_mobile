package com.example.projdraft_autovitals.data.model

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AutoVitalsDao {

    // Inserts or replaces a user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Updates user information
    @Update
    suspend fun updateUser(user: User)

    // Gets user by username
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): Flow<User?>

    // Gets user by username
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun checkUsernameExists(username: String): User?

    // Gets user by ID
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUserById(userId: Int): Flow<User?>

    // Authenticates user by username and password
    @Query("SELECT * FROM users WHERE username = :username AND passwordHash = :password LIMIT 1")
    suspend fun loginUser(username: String, password: String): User?

    // Deletes a user by ID
    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUserById(userId: Int)

    // Updates user password
    @Query("UPDATE users SET passwordHash = :newPassword WHERE id = :userId")
    suspend fun updateUserPassword(userId: Int, newPassword: String)

    // Inserts or replaces a car
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCar(car: Car)

    // Updates car details
    @Update
    suspend fun updateCar(car: Car)

    // Retrieves cars for a specific user
    @Query("SELECT * FROM cars WHERE user_id = :userId")
    fun getCarsByUser(userId: Int): Flow<List<Car>>

    // Deletes car by ID
    @Query("DELETE FROM cars WHERE id = :carId")
    suspend fun deleteCarById(carId: Int)

    // Inserts or updates a maintenance record
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaintenanceRecord(record: MaintenanceRecord)

    // Gets all maintenance records for a car
    @Query("SELECT * FROM maintenance_records WHERE car_id = :carId")
    fun getMaintenanceRecordsByCar(carId: Int): Flow<List<MaintenanceRecord>>

    // Deletes maintenance record by ID
    @Query("DELETE FROM maintenance_records WHERE id = :recordId")
    suspend fun deleteMaintenanceRecordById(recordId: Int)

    // Inserts or updates a service reminder
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServiceReminder(reminder: ServiceReminder)

    // Retrieves all reminders for a specific car
    @Query("SELECT * FROM service_reminders WHERE car_id = :carId")
    fun getServiceRemindersByCar(carId: Int): Flow<List<ServiceReminder>>

    // Gets a reminder for a specific service
    @Query("SELECT * FROM service_reminders WHERE car_id = :carId AND service = :service LIMIT 1")
    suspend fun getReminderForService(carId: Int, service: String): ServiceReminder?

    // Deletes a reminder by ID
    @Query("DELETE FROM service_reminders WHERE id = :reminderId")
    suspend fun deleteServiceReminderById(reminderId: Int)
}

