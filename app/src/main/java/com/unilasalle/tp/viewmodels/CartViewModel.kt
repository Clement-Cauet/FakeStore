package com.unilasalle.tp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unilasalle.tp.services.database.controllers.CartController
import com.unilasalle.tp.services.database.controllers.CartItemController
import com.unilasalle.tp.services.database.entities.Cart
import com.unilasalle.tp.services.database.entities.CartItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class CartViewModel(private val cartController: CartController, private val cartItemController: CartItemController) : ViewModel() {

    private val _carts = MutableStateFlow<List<Cart>>(emptyList())
    val carts: StateFlow<List<Cart>> = _carts

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private var currentCartId: String? = null

    init {
        viewModelScope.launch {
            _cartItems.value = cartItemController.getAll()
        }
    }

    val filteredCartItems: StateFlow<List<CartItem>> = _cartItems.map { items ->
        items.filter { it.cartId == currentCartId }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalItemCount: StateFlow<Int> = filteredCartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    suspend fun getCartItemsByCartId(cartId: String): List<CartItem> {
        return cartItemController.getCartItemsByCartId(cartId)
    }

    fun loadCartItems(cartId: String) {
        viewModelScope.launch {
            _cartItems.value = cartItemController.getCartItemsByCartId(cartId)
        }
    }

    fun loadCartHistory(userId: String) {
        viewModelScope.launch {
            _carts.value = cartController.getCartUserId(userId)
        }
    }

    fun createTemporaryCart() {
        currentCartId = UUID.randomUUID().toString()
    }

    fun getCurrentCartId(): String? {
        return currentCartId
    }

    fun addToTemporaryCart(productId: Int, quantity: Int) {
        currentCartId?.let { cartId ->
            viewModelScope.launch {
                val cartItem = CartItem(cartId = cartId, productId = productId, quantity = quantity)
                cartItemController.insert(cartItem)
                _cartItems.value = cartItemController.getCartItemsByCartId(cartId)
            }
        }
    }

    fun confirmCart(userId: String) {
        currentCartId?.let { cartId ->
            viewModelScope.launch {
                val cart = Cart(id = cartId, userId = userId)
                cartController.insert(cart)
                currentCartId = null
                _cartItems.value = emptyList()
            }
        }
    }

//    fun addToCart(productId: Int, quantity: Int) {
//        viewModelScope.launch {
//            val cartItem = cartItemController.getCartItemByProductId(productId)
//            if (cartItem != null) {
//                cartItemController.insert(cartItem.copy(quantity = cartItem.quantity + quantity))
//            } else {
//                cartItemController.insert(CartItem(productId = productId, quantity = quantity))
//            }
//            _cartItems.value = cartItemController.getAll()
//        }
//    }

    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartItemController.delete(cartItem)
            _cartItems.value = cartItemController.getCartItemsByCartId(cartItem.cartId)
        }
    }
}

class CartViewModelFactory(private val cartController: CartController, private val cartItemController: CartItemController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(cartController, cartItemController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}