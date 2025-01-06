package com.unilasalle.tp.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.database.entities.CartItem
import com.unilasalle.tp.services.network.ApiService
import com.unilasalle.tp.services.network.datas.Product
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun CartScreen(cartItemController: CartItemController) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(cartItemController))[CartViewModel::class.java]

    val cartItems by cartViewModel.cartItems.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()

    var totalAmount by remember { mutableDoubleStateOf(0.0) }

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

suspend fun getProductPrice(productId: Int): Double {
    // Fetch product price from the database or API
    return 10.0 // Example price
}