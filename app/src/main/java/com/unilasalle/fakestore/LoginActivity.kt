package com.unilasalle.fakestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unilasalle.fakestore.services.database.AppDatabase
import com.unilasalle.fakestore.services.database.DatabaseProvider
import com.unilasalle.fakestore.ui.screens.LoginScreen
import com.unilasalle.fakestore.ui.screens.RegisterScreen

class LoginActivity : ComponentActivity(), DatabaseProvider {

    override val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController, database.usersController()) }
                composable("register") { RegisterScreen(navController, database.usersController()) }
            }
        }
    }
}