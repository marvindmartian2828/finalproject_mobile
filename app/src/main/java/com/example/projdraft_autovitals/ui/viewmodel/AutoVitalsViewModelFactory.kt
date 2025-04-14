package com.example.projdraft_autovitals.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projdraft_autovitals.data.model.AutoVitalsRepository

class AutoVitalsViewModelFactory(private val repository: AutoVitalsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AutoVitalsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AutoVitalsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
