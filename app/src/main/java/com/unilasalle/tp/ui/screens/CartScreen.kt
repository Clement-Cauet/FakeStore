package com.unilasalle.tp.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.unilasalle.tp.services.database.controllers.CartController
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.database.entities.CartItem
import com.unilasalle.tp.services.database.entities.User
import com.unilasalle.tp.services.network.ApiService
import com.unilasalle.tp.services.network.datas.Product
import com.unilasalle.tp.ui.components.CartConfirmationModal
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun CartScreen(cartController: CartController, cartItemController: CartItemController, user: User?) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(cartController, cartItemController))[CartViewModel::class.java]

    val cartItems by cartViewModel.filteredCartItems.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()

    var totalAmount by remember { mutableDoubleStateOf(0.0) }
    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(cartViewModel.getCurrentCartId()) {
        cartViewModel.getCurrentCartId()?.let { cartId ->
            cartViewModel.loadCartItems(cartId)
        }
    }

    LaunchedEffect(cartItems) {
        val total = cartItems.sumOf { cartItem ->
            val product = apiService.getProduct(cartItem.productId)
            product.price.times(cartItem.quantity).toDouble()
        }
        totalAmount = total
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Cart")
        LazyColumn {
            items(cartItems) { cartItem ->
                CartItemRow(cartItem = cartItem, cartViewModel = cartViewModel)
            }
        }
        Text(text = "Total: \$${String.format("%.2f", totalAmount)}")
        Button(
            onClick = { showModal = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Proceed to Checkout")
        }
    }

    if (showModal) {
        CartConfirmationModal(totalAmount = totalAmount, user = user, onDismiss = { showModal = false }) {
            coroutineScope.launch {
                user?.let {
                    cartViewModel.confirmCart(it.id)
                }
                showModal = false
            }
        }
    }
}

@Composable
fun CartItemRow(cartItem: CartItem, cartViewModel: CartViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()
    var product by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            product = apiService.getProduct(cartItem.productId)
        }
    }
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        product?.let {
            Button(
                onClick = {
                    coroutineScope.launch {
                        cartViewModel.removeFromCart(cartItem)
                    }
                }
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Remove from cart")
            }
            Column {
                Text(text = it.title)
                Text(text = "\$${it.price}")
                Text(text = "Quantity: ${cartItem.quantity}")
            }
        } ?: Text(text = "Loading...")
    }
}