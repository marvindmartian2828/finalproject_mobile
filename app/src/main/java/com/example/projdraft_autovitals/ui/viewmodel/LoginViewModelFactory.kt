package com.example.projdraft_autovitals.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository

// Factory for creating LoginViewModel with repository and context dependencies
class LoginViewModelFactory(
    private val repository: AutoVitalsRepository,
    private val context: Context
) : ViewModelProvider.Factory {

    // Creates and returns an instance of LoginViewModel
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
