package com.example.inventoryapp

import com.example.inventoryapp.data.db.dao.InventoryGroupDao
import com.example.inventoryapp.data.db.dao.InventoryItemDao
import com.example.inventoryapp.data.db.entity.InventoryGroup
import com.example.inventoryapp.data.db.entity.InventoryItem
import com.example.inventoryapp.data.repository.InventoryRepository
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class InventoryRepositoryTest {

    private lateinit var groupDao: InventoryGroupDao
    private lateinit var itemDao: InventoryItemDao
    private lateinit var repository: InventoryRepository

    @Before
    fun setUp() {
        groupDao = mockk()
        itemDao = mockk()
        repository = InventoryRepository(groupDao, itemDao)
    }

    @Test
    fun `createGroup inserts group with trimmed name`() = runTest {
        coEvery { groupDao.insert(any()) } returns 1L
        val id = repository.createGroup("  Тест  ")
        assertEquals(1L, id)
        coVerify {
            groupDao.insert(match { it.name == "Тест" })
        }
    }

    @Test
    fun `processBarcode increments quantity for existing item`() = runTest {
        val groupId = 1L
        val barcode = "1234567890"
        val existing = InventoryItem(id = 1, groupId = groupId, barcode = barcode, name = "Товар", quantity = 2)

        every { itemDao.getItemsByGroup(groupId) } returns flowOf(listOf(existing))
        coEvery { itemDao.getItemByBarcode(groupId, barcode) } returns existing
        coEvery { itemDao.update(any()) } just Runs

        val result = repository.processBarcode(groupId, barcode, "Товар")
        assertEquals(3, result.quantity)
        coVerify { itemDao.update(match { it.quantity == 3 }) }
    }

    @Test
    fun `processBarcode inserts new item when barcode not found`() = runTest {
        val groupId = 1L
        val barcode = "9999999999"

        coEvery { itemDao.getItemByBarcode(groupId, barcode) } returns null
        coEvery { itemDao.insert(any()) } returns 42L

        val result = repository.processBarcode(groupId, barcode, "Новый товар")
        assertEquals(42L, result.id)
        assertEquals(1, result.quantity)
        coVerify { itemDao.insert(match { it.barcode == barcode && it.name == "Новый товар" }) }
    }

    @Test
    fun `getAllGroups delegates to dao`() = runTest {
        val groups = listOf(InventoryGroup(id = 1, name = "Группа A"))
        every { groupDao.getAllGroups() } returns flowOf(groups)

        val flow = repository.getAllGroups()
        flow.collect { result ->
            assertEquals(groups, result)
        }
    }
}
