package com.hiddendanang.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.Review
import com.hiddendanang.app.data.repository.LocationRepository
import com.hiddendanang.app.data.repository.ReviewRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReviewWithPlace(
    val review: Review,
    val place: Place? = null
)

data class MyReviewsUiState(
    val isLoading: Boolean = true,
    val reviews: List<ReviewWithPlace> = emptyList(), // Đổi sang list wrapper
    val error: String? = null,
    val message: String? = null
)

class MyReviewsViewModel : ViewModel() {
    private val reviewRepository = ReviewRepository()
    private val locationRepository = LocationRepository() // Cần repo này để fetch Place
    
    private val _uiState = MutableStateFlow(MyReviewsUiState())
    val uiState: StateFlow<MyReviewsUiState> = _uiState.asStateFlow()

    init {
        loadMyReviews()
    }

    private fun loadMyReviews() {
        viewModelScope.launch {
            reviewRepository.getMyReviewsStream()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collectLatest { reviews ->
                    _uiState.update { it.copy(isLoading = true) }
                    
                    // Fetch Place info for each review parallely
                    val reviewsWithPlace = reviews.map { review ->
                        async {
                            if (review.place_id.isNotEmpty()) {
                                val placeResult = locationRepository.getPlaceDetail(review.place_id)
                                val place = placeResult.getOrNull()
                                ReviewWithPlace(review, place)
                            } else {
                                ReviewWithPlace(review, null)
                            }
                        }
                    }.awaitAll()

                    _uiState.update { it.copy(isLoading = false, reviews = reviewsWithPlace) }
                }
        }
    }

    fun deleteReview(review: Review) {
        if (review.place_id.isEmpty()) {
            _uiState.update { it.copy(error = "Cannot delete review: Missing Place ID") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            reviewRepository.deleteReview(review.place_id)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, message = "Review deleted successfully") }
                    // Stream sẽ tự update list
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun updateReview(review: Review, newRating: Int, newComment: String) {
        if (review.place_id.isEmpty()) {
            _uiState.update { it.copy(error = "Cannot update review: Missing Place ID") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val updatedReview = review.copy(
                rating = newRating,
                comment = newComment
            )
            
            reviewRepository.submitOrUpdateReview(review.place_id, updatedReview)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, message = "Review updated successfully") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }

    fun messageShown() {
        _uiState.update { it.copy(message = null) }
    }
    
    fun errorShown() {
        _uiState.update { it.copy(error = null) }
    }
}