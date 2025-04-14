package com.example.projdraft_autovitals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.ui.viewmodel.AutoVitalsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*
import org.junit.runner.RunWith

@RunWith(JUnit4::class)
class AutoVitalsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AutoVitalsRepository
    private lateinit var viewModel: AutoVitalsViewModel

    @Before
    fun setup() {
        repository = mock(AutoVitalsRepository::class.java)
        viewModel = AutoVitalsViewModel(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_loadMaintenanceRecords_calls_repository() = runTest {
        val testCarId = 1
        viewModel.loadMaintenanceRecords(testCarId)
        verify(repository).getMaintenanceRecords(testCarId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun test_updateUserPassword_calls_repository() = runTest {
        viewModel.updateUserPassword(1, "newPass123")
        verify(repository).updateUserPassword(1, "newPass123")
    }
}
