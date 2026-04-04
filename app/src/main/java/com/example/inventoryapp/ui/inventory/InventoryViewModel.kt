package com.example.inventoryapp.ui.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.data.db.entity.InventoryItem
import com.example.inventoryapp.data.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryUiState(
    val items: List<InventoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val totalCount: Int = 0,
    val errorMessage: String? = null
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _groupId = MutableStateFlow<Long>(-1L)

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    fun setGroupId(groupId: Long) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId
        viewModelScope.launch {
            repository.getItemsByGroup(groupId)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { items ->
                    _uiState.update {
                        it.copy(
                            items = items,
                            isLoading = false,
                            totalCount = items.sumOf { item -> item.quantity }
                        )
                    }
                }
        }
    }

    fun deleteItem(item: InventoryItem) {
        viewModelScope.launch {
            try {
                repository.deleteItem(item)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun incrementQuantity(item: InventoryItem) {
        viewModelScope.launch {
            repository.updateItem(item.copy(quantity = item.quantity + 1))
        }
    }

    fun decrementQuantity(item: InventoryItem) {
        if (item.quantity <= 1) {
            deleteItem(item)
            return
        }
        viewModelScope.launch {
            repository.updateItem(item.copy(quantity = item.quantity - 1))
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
