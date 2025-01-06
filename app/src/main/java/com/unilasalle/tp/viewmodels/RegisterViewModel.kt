package com.unilasalle.tp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unilasalle.tp.services.database.controllers.UsersController
import com.unilasalle.tp.services.database.entities.User
import kotlinx.coroutines.launch

class RegisterViewModel(private val usersController: UsersController) : ViewModel() {

    fun register(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = User(email = email, password = password)
            try {
                usersController.insert(user)
                onResult(user)
            } catch (e: Exception) {
                Log.e("RegisterViewModel", "Failed to register user", e)
                onResult(null)
            }
        }
    }
}

class RegisterViewModelFactory(private val usersController: UsersController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(usersController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}