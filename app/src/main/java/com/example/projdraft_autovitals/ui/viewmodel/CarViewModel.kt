package com.example.projdraft_autovitals.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projdraft_autovitals.data.model.*
import com.example.projdraft_autovitals.util.SessionManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CarViewModel(
    private val repository: AutoVitalsRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Emits list of cars owned by the logged-in user
    private val _cars = MutableStateFlow<List<Car>>(emptyList())
    val cars: StateFlow<List<Car>> = _cars.asStateFlow()

    // Retrieves the user ID from session storage
    private val userId = sessionManager.getUserId()

    init {
        if (userId != -1) {
            loadCars() // Automatically load cars on init if logged in
        } else {
            Log.e("CarViewModel", "❌ No logged-in user ID found.")
        }
    }

    // Fetch all cars for the current user and emit to flow
    fun loadCars() {
        viewModelScope.launch {
            try {
                repository.getCarsByUser(userId).collect {
                    _cars.value = it
                }
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error loading cars: ${e.localizedMessage}")
            }
        }
    }

    // Add a new car linked to the current user
    fun addCar(car: Car) {
        viewModelScope.launch {
            try {
                repository.insertCar(car.copy(userId = userId))
                Log.d("CarViewModel", "✅ Car added: ${car.carName}")
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error adding car: ${e.localizedMessage}")
            }
        }
    }

    // Update car details in the database
    fun updateCar(car: Car) {
        viewModelScope.launch {
            try {
                repository.updateCar(car)
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error updating car: ${e.localizedMessage}")
            }
        }
    }

    // Delete a car by its unique ID
    fun deleteCar(carId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteCarById(carId)
            } catch (e: Exception) {
                Log.e("CarViewModel", "Error deleting car: ${e.localizedMessage}")
            }
        }
    }

    // Retrieve a car from the current list by ID
    fun getCarById(carId: Int): Car? {
        return _cars.value.find { it.id == carId }
    }
}

// Factory class for injecting repository and session manager into ViewModel
class CarViewModelFactory(
    private val repository: AutoVitalsRepository,
    private val sessionManager: SessionManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarViewModel(repository, sessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
