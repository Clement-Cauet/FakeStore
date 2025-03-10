package com.unilasalle.fakestore.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.unilasalle.fakestore.MainActivity
import com.unilasalle.fakestore.services.database.controllers.UsersController
import com.unilasalle.fakestore.ui.components.RegisterForm
import com.unilasalle.fakestore.viewmodels.RegisterViewModel
import com.unilasalle.fakestore.viewmodels.RegisterViewModelFactory

/**
 * Register screen.
 *
 * @param navController The nav controller.
 * @param usersController The users controller.
 */
@Composable
fun RegisterScreen(navController: NavController, usersController: UsersController) {
    val context = LocalContext.current
    val registerViewModel = ViewModelProvider(context as ComponentActivity, RegisterViewModelFactory(usersController))[RegisterViewModel::class.java]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RegisterForm { email, password ->
                registerViewModel.register(email, password) { user ->
                    if (user != null) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("userId", user.id)
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            Button(
                onClick = { navController.navigate("login") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Login")
            }
        }
    }
}