package com.example.inventoryapp.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.data.db.entity.InventoryItem
import com.example.inventoryapp.data.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ScanEvent {
    data class ItemAdded(val item: InventoryItem, val isNew: Boolean) : ScanEvent()
    data class Error(val message: String) : ScanEvent()
}

data class ScannerUiState(
    val isScanning: Boolean = true,
    val lastBarcode: String? = null,
    val showNameDialog: Boolean = false,
    val pendingBarcode: String = "",
    val productName: String = "",
    val recentItems: List<InventoryItem> = emptyList()
)

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _groupId = MutableStateFlow<Long>(-1L)
    private val _uiState = MutableStateFlow(ScannerUiState())
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ScanEvent>()
    val events: SharedFlow<ScanEvent> = _events.asSharedFlow()

    fun setGroupId(groupId: Long) {
        if (_groupId.value == groupId) return
        _groupId.value = groupId
        viewModelScope.launch {
            repository.getItemsByGroup(groupId)
                .collect { items ->
                    _uiState.update { it.copy(recentItems = items.take(5)) }
                }
        }
    }

    fun onBarcodeDetected(barcode: String) {
        val current = _uiState.value
        if (!current.isScanning || barcode == current.lastBarcode) return

        _uiState.update { it.copy(lastBarcode = barcode, isScanning = false) }

        viewModelScope.launch {
            val existing = repository.getItemsByGroup(_groupId.value)
                .first()
                .find { it.barcode == barcode }

            if (existing != null) {
                // Уже известный товар — просто добавляем количество
                val updated = repository.processBarcode(_groupId.value, barcode, existing.name)
                _events.emit(ScanEvent.ItemAdded(updated, isNew = false))
                resumeScanning()
            } else {
                // Новый товар — запрашиваем название
                _uiState.update {
                    it.copy(
                        showNameDialog = true,
                        pendingBarcode = barcode,
                        productName = ""
                    )
                }
            }
        }
    }

    fun onProductNameChange(name: String) = _uiState.update { it.copy(productName = name) }

    fun confirmNewProduct() {
        val state = _uiState.value
        val name = state.productName.trim().ifBlank { state.pendingBarcode }
        viewModelScope.launch {
            try {
                val item = repository.processBarcode(_groupId.value, state.pendingBarcode, name)
                _events.emit(ScanEvent.ItemAdded(item, isNew = true))
            } catch (e: Exception) {
                _events.emit(ScanEvent.Error(e.message ?: "Ошибка"))
            } finally {
                _uiState.update { it.copy(showNameDialog = false) }
                resumeScanning()
            }
        }
    }

    fun dismissNameDialog() {
        _uiState.update { it.copy(showNameDialog = false) }
        resumeScanning()
    }

    private fun resumeScanning() {
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            _uiState.update { it.copy(isScanning = true, lastBarcode = null) }
        }
    }
}
