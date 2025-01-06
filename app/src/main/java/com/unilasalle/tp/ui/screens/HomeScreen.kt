package com.unilasalle.tp.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.network.ApiService
import com.unilasalle.tp.services.network.datas.Product
import com.unilasalle.tp.ui.components.CategoryFilter
import com.unilasalle.tp.ui.components.ProductCard
import com.unilasalle.tp.ui.components.ProductDetailModal
import com.unilasalle.tp.viewmodels.CartViewModel
import com.unilasalle.tp.viewmodels.CartViewModelFactory
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(cartItemController: CartItemController) {
    val context = LocalContext.current
    val cartViewModel = ViewModelProvider(context as ComponentActivity, CartViewModelFactory(cartItemController))[CartViewModel::class.java]
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