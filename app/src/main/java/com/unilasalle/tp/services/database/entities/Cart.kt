package com.unilasalle.tp.services.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "carts")
data class Cart(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val datetime: Long = System.currentTimeMillis()
)