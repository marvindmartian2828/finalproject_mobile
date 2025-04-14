package com.example.projdraft_autovitals.data.model

class AutoVitalsRepository(private val dao: AutoVitalsDao) {

    // Insert new user into the database
    suspend fun insertUser(user: User) = dao.insertUser(user)

    // Update existing user data
    suspend fun updateUser(user: User) = dao.updateUser(user)

    // Observe user by username (used in login, validation)
    fun getUserByUsername(username: String) = dao.getUserByUsername(username)

    // Observe user by ID
    fun getUserById(userId: Int) = dao.getUserById(userId)

    //Delete user from database
    suspend fun deleteUserById(userId: Int) = dao.deleteUserById(userId)

    // Check if a username already exists (used during registration)
    suspend fun checkUsernameExists(username: String): User? {
        return dao.checkUsernameExists(username)
    }

    // Authenticate user with username and password
    suspend fun loginUser(username: String, password: String): User? {
        return dao.loginUser(username, password)
    }

    // Update password for an existing user
    suspend fun updateUserPassword(userId: Int, newPassword: String) {
        dao.updateUserPassword(userId, newPassword)
    }

    // Insert a car for a user
    suspend fun insertCar(car: Car) = dao.insertCar(car)

    // Update an existing car
    suspend fun updateCar(car: Car) = dao.updateCar(car)

    // Get all cars for a specific user
    fun getCarsByUser(userId: Int) = dao.getCarsByUser(userId)

    // Delete a car by its ID
    suspend fun deleteCarById(carId: Int) = dao.deleteCarById(carId)

    // Add a maintenance record
    suspend fun insertMaintenanceRecord(record: MaintenanceRecord) = dao.insertMaintenanceRecord(record)

    // Get maintenance records for a specific car
    fun getMaintenanceRecordsByCar(carId: Int) = dao.getMaintenanceRecordsByCar(carId)

    // Delete a specific maintenance record
    suspend fun deleteMaintenanceRecordById(recordId: Int) = dao.deleteMaintenanceRecordById(recordId)

    // Insert or update a service reminder
    suspend fun insertServiceReminder(reminder: ServiceReminder) = dao.insertServiceReminder(reminder)

    // Get all reminders for a car
    fun getServiceRemindersByCar(carId: Int) = dao.getServiceRemindersByCar(carId)

    // Delete reminder by ID
    suspend fun deleteServiceReminderById(reminderId: Int) = dao.deleteServiceReminderById(reminderId)

    // Get a specific reminder for a given service (used to check if it already exists)
    suspend fun getReminderForService(carId: Int, service: String): ServiceReminder? {
        return dao.getReminderForService(carId, service)
    }
}
