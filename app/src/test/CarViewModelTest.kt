package com.example.projdraft_autovitals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.data.model.Car
import com.example.projdraft_autovitals.ui.viewmodel.CarViewModel
import com.example.projdraft_autovitals.util.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class CarViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AutoVitalsRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: CarViewModel

    @Before
    fun setup() {
        repository = mock(AutoVitalsRepository::class.java)
        sessionManager = mock(SessionManager::class.java)
        `when`(sessionManager.getUserId()).thenReturn(1)
        viewModel = CarViewModel(repository, sessionManager)
    }

    @Test
    fun `test addCar calls repository`() = runTest {
        val car = Car(
            id = 0, userId = 1, carName = "Test Car", make = "Toyota",
            model = "Camry", year = "2022", mileage = "5000", vin = "VIN123456789",
            imageUri = null
        )
        viewModel.addCar(car)
        verify(repository).insertCar(car)
    }

    @Test
    fun `test deleteCar calls repository`() = runTest {
        val car = Car(1, 1, "Test Car", "Honda", "Civic", "2020", "15000", "VIN987654321", null)
        viewModel.deleteCar(car)
        verify(repository).deleteCar(car)
    }
}
