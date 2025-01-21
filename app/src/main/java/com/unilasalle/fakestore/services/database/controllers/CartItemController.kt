package com.unilasalle.fakestore.services.database.controllers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unilasalle.fakestore.services.database.entities.CartItem

@Dao
interface CartItemController {
    @Query("SELECT * FROM cart_items")
    suspend fun getAll(): List<CartItem>

    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemByProductId(productId: Int): CartItem?

    @Query("SELECT * FROM cart_items WHERE cartId = :cartId")
    suspend fun getCartItemsByCartId(cartId: String): List<CartItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cartItem: CartItem)

    @Delete
    suspend fun delete(cartItem: CartItem)
}