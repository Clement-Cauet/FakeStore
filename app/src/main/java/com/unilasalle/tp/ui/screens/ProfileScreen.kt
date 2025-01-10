package com.unilasalle.tp.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.unilasalle.tp.LoginActivity
import com.unilasalle.tp.services.database.entities.User

@Composable
fun ProfileScreen(context: Context, users: List<User>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        users.forEach { user ->
            Text(text = "Email: ${user.email}")
        }

        Button(
            onClick = {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout")
        }
    }
}