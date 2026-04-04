package com.example.inventoryapp.data.db.dao

import androidx.room.*
import com.example.inventoryapp.data.db.entity.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryItemDao {

    @Query("SELECT * FROM inventory_items WHERE groupId = :groupId ORDER BY scannedAt DESC")
    fun getItemsByGroup(groupId: Long): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE groupId = :groupId AND barcode = :barcode LIMIT 1")
    suspend fun getItemByBarcode(groupId: Long, barcode: String): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem): Long

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE groupId = :groupId")
    suspend fun deleteAllInGroup(groupId: Long)

    @Query("SELECT COUNT(*) FROM inventory_items WHERE groupId = :groupId")
    fun countItemsInGroup(groupId: Long): Flow<Int>
}
