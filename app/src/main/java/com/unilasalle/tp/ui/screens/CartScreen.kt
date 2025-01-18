package com.unilasalle.tp.ui.screens

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
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

/**
 * Cart screen.
 *
 * @param cartController The cart controller.
 * @param cartItemController The cart item controller.
 * @param user The user.
 */
@Composable
fun CartScreen(cartController: CartController, cartItemController: CartItemController, user: User?) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(context, cartController, cartItemController))[CartViewModel::class.java]

    val cartItems by cartViewModel.filteredCartItems.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { ApiService.createService() }

    var totalAmount by remember { mutableDoubleStateOf(0.0) }
    var showModal by remember { mutableStateOf(false) }

    LaunchedEffect(cartItems) {
        cartViewModel.getCurrentCartId()?.let { cartId ->
            cartViewModel.loadCartItems(cartId)
        }

        val total = cartItems.sumOf { cartItem ->
            val product = apiService.getProduct(cartItem.productId)
            product.price.times(cartItem.quantity).toDouble()
        }
        totalAmount = total
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {
        Text(
            text = "Cart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            items(cartItems) { cartItem ->
                CartItemRow(cartItem = cartItem, cartViewModel = cartViewModel)
            }
        }
        Text(
            text = "Total: \$${String.format("%.2f", totalAmount)}",
            fontWeight = FontWeight.Bold
        )
        Button(
            onClick = { showModal = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check the cart")
        }
    }

    if (showModal) {
        CartConfirmationModal(totalAmount = totalAmount, user = user, onDismiss = { showModal = false }) {
            coroutineScope.launch {
                user?.let {
                    cartViewModel.confirmCart(it.id)
                    Toast.makeText(context, "Payment successful", Toast.LENGTH_SHORT).show()
                }
                showModal = false
            }
        }
    }
}

/**
 * Cart item row.
 *
 * @param cartItem The cart item.
 * @param cartViewModel The cart view model.
 */
@Composable
fun CartItemRow(cartItem: CartItem, cartViewModel: CartViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val apiService = remember { ApiService.createService() }
    var product by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(cartItem.productId) {
        coroutineScope.launch {
            product = apiService.getProduct(cartItem.productId)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            product?.let {
                Image(
                    painter = rememberImagePainter(data = it.image),
                    contentDescription = it.title,
                    modifier = Modifier
                        .height(100.dp)
                        .weight(1f),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = it.title,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Quantity: ${cartItem.quantity}")
                        Text(text = "\$${it.price}")
                    }
                }
                Button(
                    onClick = {
                        coroutineScope.launch {
                            cartViewModel.removeFromCart(cartItem)
                        }
                    }
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = "Remove from cart")
                }
            } ?: Text(text = "Loading...")
        }
    }
}