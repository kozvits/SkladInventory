package com.example.inventoryapp.data.repository

import com.example.inventoryapp.data.db.dao.InventoryGroupDao
import com.example.inventoryapp.data.db.dao.InventoryItemDao
import com.example.inventoryapp.data.db.entity.InventoryGroup
import com.example.inventoryapp.data.db.entity.InventoryItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepository @Inject constructor(
    private val groupDao: InventoryGroupDao,
    private val itemDao: InventoryItemDao
) {
    // Groups
    fun getAllGroups(): Flow<List<InventoryGroup>> = groupDao.getAllGroups()

    suspend fun getGroupById(id: Long): InventoryGroup? = groupDao.getGroupById(id)

    suspend fun createGroup(name: String): Long =
        groupDao.insert(InventoryGroup(name = name.trim()))

    suspend fun deleteGroup(group: InventoryGroup) = groupDao.delete(group)

    suspend fun deleteGroupById(id: Long) = groupDao.deleteById(id)

    // Items
    fun getItemsByGroup(groupId: Long): Flow<List<InventoryItem>> =
        itemDao.getItemsByGroup(groupId)

    fun countItemsInGroup(groupId: Long): Flow<Int> =
        itemDao.countItemsInGroup(groupId)

    /**
     * Сканирование: если товар с таким штрих-кодом уже есть в группе — увеличиваем quantity,
     * иначе создаём новый.
     */
    suspend fun processBarcode(groupId: Long, barcode: String, productName: String): InventoryItem {
        val existing = itemDao.getItemByBarcode(groupId, barcode)
        return if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + 1)
            itemDao.update(updated)
            updated
        } else {
            val newItem = InventoryItem(
                groupId = groupId,
                barcode = barcode,
                name = productName
            )
            val id = itemDao.insert(newItem)
            newItem.copy(id = id)
        }
    }

    suspend fun updateItem(item: InventoryItem) = itemDao.update(item)

    suspend fun deleteItem(item: InventoryItem) = itemDao.delete(item)

    suspend fun deleteAllItemsInGroup(groupId: Long) = itemDao.deleteAllInGroup(groupId)
}
