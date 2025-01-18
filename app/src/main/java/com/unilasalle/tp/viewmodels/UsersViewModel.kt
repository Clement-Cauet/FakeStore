package com.unilasalle.tp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unilasalle.tp.services.database.controllers.UsersController
import com.unilasalle.tp.services.database.entities.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Users view model.
 *
 * @param usersController The users controller.
 */
class UsersViewModel(private val usersController: UsersController) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    /**
     * Fetch user by id.
     *
     * @param userId The user id.
     */
    fun fetchUserById(userId: String) {
        viewModelScope.launch {
            _user.value = usersController.getUserById(userId)
        }
    }
}

/**
 * Users view model factory.
 *
 * @param usersController The users controller.
 */
class UsersViewModelFactory(private val usersController: UsersController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsersViewModel(usersController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}