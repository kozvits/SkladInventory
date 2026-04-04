package com.example.inventoryapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.inventoryapp.data.db.dao.InventoryGroupDao
import com.example.inventoryapp.data.db.dao.InventoryItemDao
import com.example.inventoryapp.data.db.entity.InventoryGroup
import com.example.inventoryapp.data.db.entity.InventoryItem

@Database(
    entities = [InventoryGroup::class, InventoryItem::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inventoryGroupDao(): InventoryGroupDao
    abstract fun inventoryItemDao(): InventoryItemDao

    companion object {
        const val DATABASE_NAME = "inventory_database"
    }
}
