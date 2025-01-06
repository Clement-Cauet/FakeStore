package com.unilasalle.tp.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.unilasalle.tp.MainActivity
import com.unilasalle.tp.services.database.controllers.UsersController
import com.unilasalle.tp.ui.components.LoginForm
import com.unilasalle.tp.viewmodels.LoginViewModel
import com.unilasalle.tp.viewmodels.LoginViewModelFactory

@Composable
fun LoginScreen(navController: NavController, usersController: UsersController) {
    val context = LocalContext.current
    val loginViewModel = ViewModelProvider(context as ComponentActivity, LoginViewModelFactory(usersController))[LoginViewModel::class.java]

    Column {
        LoginForm { email, password ->
            loginViewModel.login(email, password) { user ->
                if (user != null) {
                    val intent = Intent(context, MainActivity::class.java)
                    intent.putExtra("userId", user.id.toLong())
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        Button(onClick = { navController.navigate("register") }, modifier = Modifier.fillMaxWidth()) {
            Text("Register")
        }
    }
}