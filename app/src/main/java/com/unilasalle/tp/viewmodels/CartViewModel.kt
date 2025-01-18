package com.unilasalle.tp.viewmodels

import android.content.Context
import android.content.SharedPreferences
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

/**
 * Cart view model.
 *
 * @param context The context.
 * @param cartController The cart controller.
 * @param cartItemController The cart item controller.
 */
class CartViewModel(context: Context, private val cartController: CartController, private val cartItemController: CartItemController) : ViewModel() {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)

    private val _carts = MutableStateFlow<List<Cart>>(emptyList())
    val carts: StateFlow<List<Cart>> = _carts

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    private var currentCartId: String? = sharedPreferences.getString("currentCartId", null)
        set(value) {
            field = value
            sharedPreferences.edit().putString("currentCartId", value).apply()
        }

    init {
        viewModelScope.launch {
            _cartItems.value = cartItemController.getAll()
            if (_cartItems.value.isNotEmpty() && currentCartId == null) {
                currentCartId = UUID.randomUUID().toString()
            }
        }
    }

    val filteredCartItems: StateFlow<List<CartItem>> = _cartItems.map { items ->
        items.filter { it.cartId == currentCartId }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val totalItemCount: StateFlow<Int> = filteredCartItems.map { items ->
        items.sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    /**
     * Get cart items by cart id.
     *
     * @param cartId The cart id.
     * @return The cart items.
     */
    suspend fun getCartItemsByCartId(cartId: String): List<CartItem> {
        return cartItemController.getCartItemsByCartId(cartId)
    }

    /**
     * Get cart by cart id.
     *
     * @param cartId The user id.
     * @return The cart.
     */
    fun loadCartItems(cartId: String) {
        viewModelScope.launch {
            _cartItems.value = cartItemController.getCartItemsByCartId(cartId)
        }
    }

    /**
     * Load cart history.
     *
     * @param userId The user id.
     */
    fun loadCartHistory(userId: String) {
        viewModelScope.launch {
            _carts.value = cartController.getCartUserId(userId)
        }
    }

    /**
     * Create temporary cart.
     */
    fun createTemporaryCart() {
        if (currentCartId == null) {
            currentCartId = UUID.randomUUID().toString()
        }
    }

    /**
     * Get current cart id.
     *
     * @return The current cart id.
     */
    fun getCurrentCartId(): String? {
        return currentCartId
    }

    /**
     * Add to temporary cart.
     *
     * @param productId The product id.
     * @param quantity The quantity.
     */
    fun addToTemporaryCart(productId: Int, quantity: Int) {
        currentCartId?.let { cartId ->
            viewModelScope.launch {
                val cartItem = CartItem(cartId = cartId, productId = productId, quantity = quantity)
                cartItemController.insert(cartItem)
                _cartItems.value = cartItemController.getCartItemsByCartId(cartId)
            }
        }
    }

    /**
     * Confirm cart.
     *
     * @param userId The user id.
     */
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

    /**
     * Remove from cart.
     *
     * @param cartItem The cart item.
     */
    fun removeFromCart(cartItem: CartItem) {
        viewModelScope.launch {
            cartItemController.delete(cartItem)
            _cartItems.value = cartItemController.getCartItemsByCartId(cartItem.cartId)
        }
    }
}

/**
 * Cart view model factory.
 *
 * @param context The context.
 * @param cartController The cart controller.
 * @param cartItemController The cart item controller.
 */
class CartViewModelFactory(private val context: Context, private val cartController: CartController, private val cartItemController: CartItemController) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CartViewModel(context, cartController, cartItemController) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}