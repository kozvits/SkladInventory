package com.example.inventoryapp.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventoryapp.data.db.entity.InventoryGroup
import com.example.inventoryapp.data.repository.InventoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsUiState(
    val groups: List<InventoryGroup> = emptyList(),
    val isLoading: Boolean = true,
    val showCreateDialog: Boolean = false,
    val newGroupName: String = "",
    val errorMessage: String? = null
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val repository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllGroups()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { groups ->
                    _uiState.update { it.copy(groups = groups, isLoading = false) }
                }
        }
    }

    fun showCreateDialog() = _uiState.update { it.copy(showCreateDialog = true, newGroupName = "") }

    fun hideCreateDialog() = _uiState.update { it.copy(showCreateDialog = false) }

    fun onGroupNameChange(name: String) = _uiState.update { it.copy(newGroupName = name) }

    fun createGroup() {
        val name = _uiState.value.newGroupName.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            try {
                repository.createGroup(name)
                _uiState.update { it.copy(showCreateDialog = false, newGroupName = "") }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun deleteGroup(group: InventoryGroup) {
        viewModelScope.launch {
            try {
                repository.deleteGroup(group)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
