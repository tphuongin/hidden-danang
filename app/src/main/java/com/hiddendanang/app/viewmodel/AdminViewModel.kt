package com.hiddendanang.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val places: List<Place> = emptyList(),
    val error: String? = null,
    // Filters
    val placeStatusFilter: String? = null
)

class AdminViewModel : ViewModel() {
    private val adminRepository = AdminRepository()

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadUsers()
        loadPlaces()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            adminRepository.getAllUsers()
                .onSuccess { users ->
                    _uiState.update { it.copy(users = users, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun loadPlaces(status: String? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, placeStatusFilter = status) }
            adminRepository.getAllPlaces(status)
                .onSuccess { places ->
                    _uiState.update { it.copy(places = places, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            adminRepository.updateUserRole(userId, newRole)
                .onSuccess {
                    loadUsers() // Reload list
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            adminRepository.deleteUser(userId)
                .onSuccess {
                    loadUsers()
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun updatePlaceStatus(placeId: String, newStatus: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            adminRepository.updatePlaceStatus(placeId, newStatus)
                .onSuccess {
                    loadPlaces(_uiState.value.placeStatusFilter)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun deletePlace(placeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            adminRepository.deletePlace(placeId)
                .onSuccess {
                    loadPlaces(_uiState.value.placeStatusFilter)
                    _uiState.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}