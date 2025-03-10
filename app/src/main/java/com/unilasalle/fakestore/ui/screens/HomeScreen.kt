package com.unilasalle.fakestore.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import coil.compose.rememberImagePainter
import com.unilasalle.fakestore.services.database.controllers.CartController
import com.unilasalle.fakestore.services.database.controllers.CartItemController
import com.unilasalle.fakestore.services.network.ApiService
import com.unilasalle.fakestore.services.network.datas.Product
import com.unilasalle.fakestore.ui.components.CategoryFilter
import com.unilasalle.fakestore.ui.components.ProductDetailModal
import com.unilasalle.fakestore.viewmodels.CartViewModel
import com.unilasalle.fakestore.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

/**
 * Home screen.
 *
 * @param cartController The cart controller.
 * @param cartItemController The cart item controller.
 */
@Composable
fun HomeScreen(cartController: CartController, cartItemController: CartItemController) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(context, cartController,cartItemController))[CartViewModel::class.java]
    val coroutineScope = rememberCoroutineScope()
    val apiService = ApiService.createService()

    var products by remember { mutableStateOf(listOf<Product>()) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            products = apiService.getProducts()
        }
    }

    Column {
        CategoryFilter { category ->
            coroutineScope.launch {
                products = if (category == "All") {
                    apiService.getProducts()
                } else {
                    apiService.getProductsByCategory(category)
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(product = product, onClick = { selectedProduct = product })
            }
        }

        selectedProduct?.let { product ->
            ProductDetailModal(product = product, onDismiss = { selectedProduct = null }, cartViewModel = cartViewModel)
        }
    }
}

/**
 * Product card.
 *
 * @param product The product.
 * @param onClick The on click.
 */
@Composable
fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberImagePainter(data = product.image),
                contentDescription = product.title,
                modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = product.title, maxLines = 2)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "\$${product.price}")
        }
    }
}