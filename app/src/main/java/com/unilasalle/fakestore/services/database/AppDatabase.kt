package com.unilasalle.fakestore.services.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.unilasalle.fakestore.services.database.controllers.CartController
import com.unilasalle.fakestore.services.database.controllers.CartItemController
import com.unilasalle.fakestore.services.database.controllers.UsersController
import com.unilasalle.fakestore.services.database.entities.Cart
import com.unilasalle.fakestore.services.database.entities.CartItem
import com.unilasalle.fakestore.services.database.entities.User

/**
 * App database
 *
 * This class is responsible for managing the database
 */
@Database(entities = [User::class, Cart::class, CartItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usersController(): UsersController
    abstract fun cartController(): CartController
    abstract fun cartItemController(): CartItemController


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}