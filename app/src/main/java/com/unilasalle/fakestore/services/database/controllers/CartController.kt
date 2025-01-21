package com.unilasalle.fakestore.services.database.controllers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unilasalle.fakestore.services.database.entities.Cart

@Dao
interface CartController {
    @Query("SELECT * FROM carts")
    suspend fun getAll(): List<Cart>

    @Query("SELECT * FROM carts WHERE userId = :userId")
    suspend fun getCartUserId(userId: String): List<Cart>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(cart: Cart)

    @Delete
    suspend fun delete(cart: Cart)
}