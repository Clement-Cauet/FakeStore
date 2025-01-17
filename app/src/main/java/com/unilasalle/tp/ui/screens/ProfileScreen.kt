package com.unilasalle.tp.ui.screens

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.unilasalle.tp.LoginActivity
import com.unilasalle.tp.services.database.controllers.CartController
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.database.entities.Cart
import com.unilasalle.tp.services.database.entities.CartItem
import com.unilasalle.tp.services.database.entities.User
import com.unilasalle.tp.ui.components.CartDetailModal
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(cartController: CartController, cartItemController: CartItemController, user: User?) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(cartController, cartItemController))[CartViewModel::class.java]

    val carts by cartViewModel.carts.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        cartViewModel.loadCartHistory(user?.id ?: "")
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        user?.let {
            Text(text = "Email: ${it.email}")
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

        Text(text = "Purchase History:")
        LazyColumn {
            items(carts) { cart ->
                CartHistoryItem(cart = cart, cartViewModel = cartViewModel)
            }
        }
    }
}

@Composable
fun CartHistoryItem(cart: Cart, cartViewModel: CartViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var showModal by remember { mutableStateOf(false) }
    var cartItems by remember { mutableStateOf<List<CartItem>>(emptyList()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            cartItems = cartViewModel.getCartItemsByCartId(cart.id)
        }
    }

    Column(modifier = Modifier
        .padding(8.dp)
        .clickable { showModal = true }
    ) {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(cart.datetime), ZoneId.systemDefault())
        val formattedDate = dateTime.format(formatter)

        Text(text = "Cart ID: ${cart.id}")
        Text(text = "Date: $formattedDate")
    }

    if (showModal) {
        CartDetailModal(cart = cart, cartItems = cartItems, onDismiss = { showModal = false })
    }
}