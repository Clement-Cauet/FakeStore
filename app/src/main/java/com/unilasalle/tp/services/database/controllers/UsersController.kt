package com.unilasalle.tp.services.database.controllers

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.unilasalle.tp.services.database.entities.User

/**
 * Users controller
 *
 * This controller is responsible for managing users
 */
@Dao
interface UsersController {
    @Query("SELECT * FROM Users")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    suspend fun getUser(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(users: User)

    @Delete
    suspend fun delete(users: User)
}