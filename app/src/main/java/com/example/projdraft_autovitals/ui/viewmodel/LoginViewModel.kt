package com.example.projdraft_autovitals.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository
import com.example.projdraft_autovitals.util.SessionManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.*

// UI state holder for login screen with validation and error flags
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel(
    private val repository: AutoVitalsRepository,
    private val context: Context
) : ViewModel() {

    // Mutable UI state for Compose
    var uiState by mutableStateOf(LoginUiState())
        private set

    private val sessionManager = SessionManager(context)

    // Update username field and clear any existing error
    fun updateUsername(username: String) {
        uiState = uiState.copy(username = username, usernameError = null)
    }

    // Update password field and clear any existing error
    fun updatePassword(password: String) {
        uiState = uiState.copy(password = password, passwordError = null)
    }

    // Validate input fields before login
    fun validateAndLogin(onSuccess: () -> Unit) {
        val usernameValid = uiState.username.isNotBlank()
        val passwordValid = uiState.password.length >= 5

        uiState = uiState.copy(
            usernameError = if (!usernameValid) "Username is required" else null,
            passwordError = if (!passwordValid) "Password must be at least 5 characters" else null,
            errorMessage = null
        )

        if (usernameValid && passwordValid) {
            login(onSuccess)
        }
    }

    // Perform login and persist user session
    private fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            val user = repository.loginUser(
                username = uiState.username,
                password = uiState.password
            )

            if (user != null) {
                sessionManager.saveUserId(user.id)
                resetCredentials()
                onSuccess()
            } else {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "Invalid username or password"
                )
            }
        }
    }

    // Reset login fields and errors
    fun resetCredentials() {
        uiState = LoginUiState()
    }
}
