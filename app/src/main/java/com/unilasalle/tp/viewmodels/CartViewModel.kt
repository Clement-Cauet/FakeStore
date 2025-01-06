package com.unilasalle.tp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.database.entities.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(private val cartItemController: CartItemController) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    init {
        viewModelScope.launch {
            _cartItems.value = cartItemController.getAll()
        }
    }

    fun addToCart(productId: Int, quantity: Int) {
        viewModelScope.launch {
            val cartItem = cartItemController.getCartItemByProductId(productId)
            if (cartItem != null) {
                cartItemController.insert(cartItem.copy(quantity = cartItem.quantity + quantity))
            } else {
                cartItemController.insert(CartItem(productId = productId, quantity = quantity))
            }
            _cartItems.value = cartItemController.getAll()
        }
    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartItemController.delete(cartItem)
            _cartItems.value = cartItemController.getAll()
        }
    }
}

class CartViewModelFactory(private val cartItemController: CartItemController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartItemController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}