package com.unilasalle.tp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unilasalle.tp.services.database.entities.User
import com.unilasalle.tp.services.database.controllers.UsersController
import kotlinx.coroutines.launch

class LoginViewModel(private val usersController: UsersController) : ViewModel() {

    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = usersController.getUser(email, password)
            onResult(user)
        }
    }
}

class LoginViewModelFactory(private val usersController: UsersController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usersController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}