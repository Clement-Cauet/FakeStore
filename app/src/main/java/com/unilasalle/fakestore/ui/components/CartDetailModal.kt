package com.unilasalle.fakestore.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.unilasalle.fakestore.services.database.entities.Cart
import com.unilasalle.fakestore.services.database.entities.CartItem
import com.unilasalle.fakestore.services.network.ApiService
import com.unilasalle.fakestore.services.network.datas.Product
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Cart detail modal.
 *
 * @param cart The cart.
 * @param cartItems The cart items.
 * @param onDismiss The on dismiss.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDetailModal(cart: Cart, cartItems: List<CartItem>, onDismiss: () -> Unit) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()
    var products by remember { mutableStateOf<Map<Int, Product>>(emptyMap()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedProducts = cartItems.map { cartItem ->
                cartItem.productId to apiService.getProduct(cartItem.productId)
            }.toMap()
            products = fetchedProducts
            sheetState.show()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Command number\n ${cart.id}",
                fontWeight = FontWeight.Bold,
            )
            Text(text = LocalDateTime.ofInstant(Instant.ofEpochMilli(cart.datetime), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            cartItems.forEach { cartItem ->
                val product = products[cartItem.productId]
                product?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
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
                                Text(text = it.title, fontWeight = FontWeight.Bold)
                                Text(text = "Quantity: ${cartItem.quantity}")
                            }
                        }
                    }
                } ?: Text(text = "Loading...")
            }
        }
    }
}