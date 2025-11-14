// (Tạo file mới)
package com.hiddendanang.app.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.repository.LocationRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<Place> = emptyList(),
    val isLoading: Boolean = false
)

class SearchViewModel : ViewModel() {
    private val locationRepository: LocationRepository by lazy { LocationRepository() }

    // Master list của *tất cả* địa điểm
    private val _allPlaces = MutableStateFlow<List<Place>>(emptyList())

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // Tải tất cả địa điểm một lần
        fetchAllPlaces()

        // Bắt đầu lắng nghe thay đổi
        observeSearchQuery()
    }

    private fun fetchAllPlaces() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Giả sử bạn có hàm này trong repo
            locationRepository.getAllPlaces().fold(
                onSuccess = { places ->
                    _allPlaces.value = places
                    _uiState.update { it.copy(isLoading = false) }
                },
                onFailure = {
                    _uiState.update { it.copy(isLoading = false) }
                    // Xử lý lỗi
                }
            )
        }
    }

    // Tự động lọc khi searchQuery thay đổi
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _uiState.map { it.searchQuery } // Lắng nghe searchQuery
                .debounce(300) // Chờ 300ms sau khi người dùng ngừng gõ
                .distinctUntilChanged() // Chỉ chạy nếu text thay đổi
                .combine(_allPlaces) { query, places ->
                    if (query.length < 2) {
                        emptyList() // Không tìm nếu query quá ngắn
                    } else {
                        places.filter {
                            it.name.contains(query, ignoreCase = true)
                        }.take(10) // Chỉ lấy 10 kết quả hàng đầu
                    }
                }
                .collect { results ->
                    _uiState.update { it.copy(searchResults = results) }
                }
        }
    }

    // Hàm này được gọi từ UI
    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
    }
}