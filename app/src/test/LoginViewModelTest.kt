package com.example.projdraft_autovitals

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.ui.viewmodel.LoginViewModel
import com.example.projdraft_autovitals.util.SessionManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var repository: AutoVitalsRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        repository = mock(AutoVitalsRepository::class.java)
        sessionManager = mock(SessionManager::class.java)
        viewModel = LoginViewModel(repository, sessionManager)
    }

    @Test
    fun `test login calls repository with correct email and password`() = runTest {
        val email = "test@example.com"
        val password = "password123"
        viewModel.updateEmail(email)
        viewModel.updatePassword(password)
        viewModel.login()

        verify(repository).loginUser(email, password)
    }

    @Test
    fun `test resetCredentials clears email and password`() {
        viewModel.updateEmail("abc")
        viewModel.updatePassword("1234")
        viewModel.resetCredentials()

        Assert.assertEquals("", viewModel.email.value)
        Assert.assertEquals("", viewModel.password.value)
    }
}
