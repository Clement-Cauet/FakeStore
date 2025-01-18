package com.unilasalle.tp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.unilasalle.tp.services.network.datas.Product
import com.unilasalle.tp.viewmodels.CartViewModel
import kotlinx.coroutines.launch

/**
 * Product detail modal.
 *
 * @param product The product.
 * @param onDismiss The on dismiss.
 * @param cartViewModel The cart view model.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailModal(product: Product, onDismiss: () -> Unit, cartViewModel: CartViewModel) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var quantity by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
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
                .imePadding()
                .padding(bottom = if (sheetState.isVisible) 16.dp else 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(text = product.title)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberImagePainter(data = product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Price")
                Text(text = "\$${product.price}")
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text("Description")
                Column(modifier = Modifier
                    .height(100.dp)
                    .verticalScroll(rememberScrollState())) {
                    Text(text = product.description)
                }
            }
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .imePadding()
            ) {
                OutlinedTextField(
                    value = quantity.toString(),
                    onValueChange = { newValue ->
                        quantity = newValue.toIntOrNull() ?: 1
                    },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Button(
                    onClick = {
                        if (cartViewModel.getCurrentCartId() == null) {
                            cartViewModel.createTemporaryCart()
                        }
                        cartViewModel.addToTemporaryCart(product.id, quantity)
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add to cart")
                }
            }
        }
    }
}