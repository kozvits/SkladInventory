package com.example.inventoryapp.data.db.dao

import androidx.room.*
import com.example.inventoryapp.data.db.entity.InventoryGroup
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryGroupDao {

    @Query("SELECT * FROM inventory_groups ORDER BY createdAt DESC")
    fun getAllGroups(): Flow<List<InventoryGroup>>

    @Query("SELECT * FROM inventory_groups WHERE id = :id")
    suspend fun getGroupById(id: Long): InventoryGroup?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(group: InventoryGroup): Long

    @Update
    suspend fun update(group: InventoryGroup)

    @Delete
    suspend fun delete(group: InventoryGroup)

    @Query("DELETE FROM inventory_groups WHERE id = :id")
    suspend fun deleteById(id: Long)
}
