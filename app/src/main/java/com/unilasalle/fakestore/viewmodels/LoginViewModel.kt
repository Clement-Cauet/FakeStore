package com.unilasalle.fakestore.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unilasalle.fakestore.services.database.entities.User
import com.unilasalle.fakestore.services.database.controllers.UsersController
import kotlinx.coroutines.launch

/**
 * Login view model.
 *
 * @param usersController The users controller.
 */
class LoginViewModel(private val usersController: UsersController) : ViewModel() {

    /**
     * Login.
     *
     * @param email The email.
     * @param password The password.
     * @param onResult The on result.
     */
    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = usersController.getUser(email, password)
            onResult(user)
        }
    }
}

/**
 * Login view model factory.
 *
 * @param usersController The users controller.
 */
class LoginViewModelFactory(private val usersController: UsersController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(usersController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}