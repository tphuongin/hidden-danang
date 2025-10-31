package com.hiddendanang.app.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URL
import java.util.UUID

data class Place(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val address: String,
    val images: List<String> = listOf(),
    val rating: Float = 4.7f,
    val reviewCount: Int = 100,
    val category: String,
    val hours: String,
    val priceRange: String,
    val distance: String,
    val description: String,
    val reviews: List<Review> = emptyList(),
    val image: String = "",
    val isFavorite: Boolean = false,
    // Bổ sung các thuộc tính cần thiết cho map
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Review(
    val userID: String,
    val timestamp: String,
    val rating: Float,
    val comment: String,
)

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarURL: URL? = null,
    val phone: String? = null,
    val bio: String? = null,
    val joinDate: String,
    val level: String = "Explorer",
    val favoriteCount: Int = 0,
    val reviewCount: Int = 0,
    val visitedPlaces: Int = 0
)

class DataViewModel : ViewModel() {

    // Mock User Data (giữ nguyên)
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _mockUsers = MutableStateFlow(listOf(
        User(
            id = "user_001",
            name = "Nguyễn Văn A",
            email = "nguyenvana@email.com",
            avatarURL = URL("https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=500&q=80"),
            phone = "+84 123 456 789",
            bio = "Du lịch là đam mê của tôi. Đã khám phá hơn 50 địa điểm tại Việt Nam và mong muốn chia sẻ những trải nghiệm tuyệt vời với mọi người.",
            joinDate = "Tháng 1, 2024",
            level = "Senior Explorer",
            favoriteCount = 12,
            reviewCount = 25,
            visitedPlaces = 48
        ),
        // ... giữ nguyên các user khác
    ))

    // Mock Place Data với tọa độ (bổ sung latitude, longitude)
    private val _places = MutableStateFlow(listOf(
        Place(
            id = "caurong",
            name = "Cầu Rồng Đà Nẵng",
            address = "Sông Hàn, Quận Hải Châu, Đà Nẵng",
            images = listOf(
                "https://danangfantasticity.com/wp-content/uploads/2018/10/cau-rong-top-20-cay-cau-ky-quai-nhat-the-gioi-theo-boredom-therapy-02.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTtFwIU4r6NkSAjpWJ3B3b__nFqh0PWIu8LAAWaqTEtdX-ihCIX3hSDxHjXLElBfN4fSS8&usqp=CAU",
                "https://ik.imagekit.io/tvlk/blog/2023/02/cau-rong-da-nang-2.jpg"
            ),
            rating = 4.9f,
            reviewCount = 1500,
            category = "Địa điểm nổi tiếng",
            hours = "Luôn mở cửa (Phun lửa/nước T7 & CN lúc 21:00)",
            priceRange = "Miễn phí",
            distance = "5.0 km",
            description = "Biểu tượng kiến trúc độc đáo bắc qua sông Hàn, nổi tiếng với màn trình diễn phun lửa và nước vào cuối tuần. Cầu Rồng được thiết kế và xây dựng theo phong cách hiện đại, có chiều dài 666m và rộng 37,5m.",
            reviews = listOf(
                Review("user_001", "2 ngày trước", 5.0f, "Cảnh đẹp tuyệt vời! Màn phun lửa rất ấn tượng, đáng để trải nghiệm."),
                Review("user_003", "1 tuần trước", 4.5f, "Địa điểm check-in tuyệt đẹp, view sông Hàn rất lãng mạn vào buổi tối."),
                Review("user_002", "2 tuần trước", 5.0f, "Biểu tượng của Đà Nẵng, không thể bỏ qua khi đến đây.")
            ),
            image = "https://danangfantasticity.com/wp-content/uploads/2018/10/cau-rong-top-20-cay-cau-ky-quai-nhat-the-gioi-theo-boredom-therapy-02.jpg",
            // Bổ sung tọa độ
            latitude = 16.0617,
            longitude = 108.2239
        ),

        Place(
            id = "banahills",
            name = "Bà Nà Hills",
            address = "Hoà Ninh, Hoà Vang, Đà Nẵng",
            images = listOf(
                "https://vcdn1-dulich.vnecdn.net/2025/04/10/BNtop-1744279080-7298-1744280762.jpg",
                "https://statics.vinpearl.com/Ba-Na-Hills-1_1688712496.jpg",
                "https://cdn3.ivivu.com/2023/08/Ba-Na-Hills-ivivu-23.jpg"
            ),
            rating = 4.7f,
            reviewCount = 2000,
            category = "Khu vui chơi giải trí",
            hours = "07:30 - 17:30",
            priceRange = "850.000 VND",
            distance = "25.0 km",
            description = "Khu nghỉ dưỡng và giải trí trên đỉnh núi Chúa, nổi bật với Cầu Vàng và cáp treo dài kỷ lục. Bà Nà Hills nằm ở độ cao 1.487m so với mực nước biển, có khí hậu mát mẻ quanh năm.",
            reviews = listOf(
                Review("user_004", "3 ngày trước", 4.8f, "Cầu Vàng đẹp tuyệt vời, view mây rất ảo diệu."),
                Review("user_005", "5 ngày trước", 4.5f, "Cáp treo dài nhất thế giới, trải nghiệm đáng giá."),
                Review("user_001", "1 tuần trước", 4.7f, "Khí hậu mát mẻ, thích hợp tránh nóng mùa hè.")
            ),
            image = "https://statics.vinpearl.com/Ba-Na-Hills-1_1688712496.jpg",
            // Bổ sung tọa độ
            latitude = 15.9989,
            longitude = 107.9875
        ),

        Place(
            id = "mykhe",
            name = "Bãi biển Mỹ Khê",
            address = "Đường Võ Nguyên Giáp, Đà Nẵng",
            images = listOf(
                "https://static.vinwonders.com/2022/04/bai-bien-my-khe-da-nang-2.jpg",
                "https://havi-web.s3.ap-southeast-1.amazonaws.com/bien_my_khe_da_nang_2_11zon_1_a3a8e98ee1.webp",
                "https://cdn3.ivivu.com/2023/07/Bai-bien-My-Khe-ivivu-7.jpg"
            ),
            rating = 4.8f,
            reviewCount = 1800,
            category = "Bãi biển",
            hours = "Luôn mở cửa",
            priceRange = "Miễn phí",
            distance = "2.5 km",
            description = "Một trong những bãi biển đẹp nhất hành tinh, với bờ cát trắng mịn, sóng êm và nước biển trong xanh. Bãi biển Mỹ Khê dài khoảng 900m.",
            reviews = listOf(
                Review("user_002", "Hôm qua", 5.0f, "Bãi biển sạch sẽ, nước trong xanh, hoàng hôn tuyệt đẹp!"),
                Review("user_003", "4 ngày trước", 4.9f, "Hoàn hảo cho buổi sáng chạy bộ và chiều tắm biển."),
                Review("user_004", "1 tuần trước", 4.7f, "Cát mịn, nước sạch, rất thích hợp cho gia đình.")
            ),
            image = "https://static.vinwonders.com/2022/04/bai-bien-my-khe-da-nang-2.jpg",
            // Bổ sung tọa độ
            latitude = 16.0611,
            longitude = 108.2425
        ),

        Place(
            id = "nguhanhson",
            name = "Ngũ Hành Sơn",
            address = "Phường Hoà Hải, Quận Ngũ Hành Sơn, Đà Nẵng",
            images = listOf(
                "https://owa.bestprice.vn/images/destinations/uploads/nui-ngu-hanh-son-5f59ac20e5100.jpg",
                "https://cdn.vntrip.vn/cam-nang/wp-content/uploads/2017/08/ngu-hanh-son-1.png",
                "https://ik.imagekit.io/tvlk/blog/2023/02/ngu-hanh-son-da-nang-1.jpg"
            ),
            rating = 4.6f,
            reviewCount = 1200,
            category = "Di tích lịch sử & Danh lam thắng cảnh",
            hours = "07:00 - 17:30",
            priceRange = "40.000 VND",
            distance = "9.0 km",
            description = "Quần thể năm ngọn núi đá vôi với nhiều hang động, chùa chiền tâm linh và làng nghề đá mỹ nghệ truyền thống.",
            reviews = listOf(
                Review("user_005", "2 ngày trước", 4.5f, "View từ trên cao tuyệt đẹp, nhiều hang động thú vị."),
                Review("user_001", "6 ngày trước", 4.8f, "Di sản văn hóa độc đáo, kiến trúc chùa chiền ấn tượng."),
                Review("user_002", "1 tuần trước", 4.4f, "Làng đá mỹ nghệ rất thú vị, mua được nhiều quà lưu niệm đẹp.")
            ),
            image = "https://owa.bestprice.vn/images/destinations/uploads/nui-ngu-hanh-son-5f59ac20e5100.jpg",
            // Bổ sung tọa độ
            latitude = 16.0075,
            longitude = 108.2653
        ),

        Place(
            id = "chualinhung",
            name = "Chùa Linh Ứng Bán đảo Sơn Trà",
            address = "Bán đảo Sơn Trà, Quận Sơn Trà, Đà Nẵng",
            images = listOf(
                "https://storage-phatsuonline-v2.sgp1.digitaloceanspaces.com/files/2025/07/a6c8e91a-88d6-40ee-9748-7bc09ea43c94-crop.JPEG",
                "https://static-image.adavigo.com/uploads/images/2023/11/16/1e435d8b-7b8c-4e04-9522-10fdb838914a.jpg",
                "https://cdn3.ivivu.com/2023/07/Chua-Linh-Ung-Son-Tra-ivivu-8.jpg"
            ),
            rating = 4.7f,
            reviewCount = 1400,
            category = "Di tích tâm linh",
            hours = "06:00 - 18:00",
            priceRange = "Miễn phí",
            distance = "12.0 km",
            description = "Ngôi chùa nổi tiếng với tượng Phật Quan Thế Âm cao nhất Việt Nam, nhìn ra biển Đông và thành phố Đà Nẵng.",
            reviews = listOf(
                Review("user_003", "Hôm qua", 5.0f, "Không gian thanh tịnh, view biển tuyệt đẹp từ trên cao."),
                Review("user_004", "3 ngày trước", 4.8f, "Tượng Phật khổng lồ rất ấn tượng, kiến trúc đẹp."),
                Review("user_005", "1 tuần trước", 4.6f, "Nơi lý tưởng để tìm sự bình yên, cảnh quan hùng vĩ.")
            ),
            image = "https://static-image.adavigo.com/uploads/images/2023/11/16/1e435d8b-7b8c-4e04-9522-10fdb838914a.jpg",
            // Bổ sung tọa độ
            latitude = 16.1122,
            longitude = 108.2492
        ),

        Place(
            id = "congvien",
            name = "Công viên Biển Đông",
            address = "Đường Võ Nguyên Giáp, Quận Sơn Trà, Đà Nẵng",
            images = listOf(
                "https://static.vinwonders.com/production/cong-vien-bien-dong-top-banner-1.jpg",
                "https://static.vinwonders.com/2022/06/ZwIlGdJ2-cong-vien-bien-dong-1.jpg",
                "https://cdn3.ivivu.com/2023/07/Cong-vien-bien-Dong-ivivu-10.jpg"
            ),
            rating = 4.5f,
            reviewCount = 800,
            category = "Công viên / Khu vực công cộng",
            hours = "Luôn mở cửa",
            priceRange = "Miễn phí",
            distance = "3.0 km",
            description = "Nơi tập trung các hoạt động cộng đồng, nổi tiếng với hàng ngàn con chim bồ câu thân thiện và view biển tuyệt đẹp.",
            reviews = listOf(
                Review("user_001", "2 ngày trước", 4.3f, "Công viên sạch sẽ, nhiều chim bồ câu dễ thương."),
                Review("user_002", "5 ngày trước", 4.7f, "View hoàng hôn tuyệt đẹp, không gian thoáng đãng."),
                Review("user_003", "1 tuần trước", 4.5f, "Lý tưởng cho buổi sáng tập thể dục và chiều đi dạo.")
            ),
            image = "https://static.vinwonders.com/production/cong-vien-bien-dong-top-banner-1.jpg",
            // Bổ sung tọa độ
            latitude = 16.0744,
            longitude = 108.2292
        ),

        Place(
            id = "hoian",
            name = "Phố cổ Hội An",
            address = "Thành phố Hội An, Quảng Nam",
            images = listOf(
                "https://cdn.tgdd.vn/Files/2022/01/18/1412747/hoi-an-13-dia-diem-du-lich-dep-lung-linh-cuc-thich-hop-cho-cac-cap-doi-202201181554364367.jpg",
                "https://statics.vinpearl.com/hoi-an-quang-nam-1_1681368751.jpg",
                "https://ik.imagekit.io/tvlk/blog/2023/02/hoi-an-ancient-town-1.jpg"
            ),
            rating = 4.9f,
            reviewCount = 3500,
            category = "Di sản văn hóa",
            hours = "08:00 - 21:00",
            priceRange = "120.000 VND",
            distance = "30.0 km",
            description = "Di sản văn hóa thế giới UNESCO với kiến trúc cổ kính, đèn lồng rực rỡ và ẩm thực đặc sắc.",
            reviews = listOf(
                Review("user_004", "Hôm qua", 5.0f, "Phố cổ đẹp như tranh vẽ, đèn lồng lung linh về đêm."),
                Review("user_005", "3 ngày trước", 4.9f, "Ẩm thực đường phố tuyệt vời, không khí cổ kính."),
                Review("user_001", "1 tuần trước", 5.0f, "Di sản văn hóa độc đáo, xứng đáng là kỳ quan.")
            ),
            image = "https://statics.vinpearl.com/hoi-an-quang-nam-1_1681368751.jpg",
            isFavorite = true,
            // Bổ sung tọa độ
            latitude = 15.8801,
            longitude = 108.3380
        )
    ))

    // State flows (giữ nguyên)
    private val _favoritePlaceIds = MutableStateFlow<Set<String>>(setOf("caurong", "mykhe", "hoian"))
    private val _selectedPlace = MutableStateFlow<Place?>(null)

    // Public state flows (giữ nguyên)
    val topPlace: List<Place> get() = _places.value.take(6)
    val allPlaces: List<Place> get() = _places.value
    val selectedPlace: StateFlow<Place?> = _selectedPlace.asStateFlow()
    val mockUsers: List<User> get() = _mockUsers.value

    // Favorite places stream với real-time updates (giữ nguyên)
    val favoritePlaces: StateFlow<List<Place>> = combine(
        _places,
        _favoritePlaceIds
    ) { places, favoriteIds ->
        places.filter { it.id in favoriteIds }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // All places với favorite status (giữ nguyên)
    val allPlacesWithFavorite: StateFlow<List<Place>> = combine(
        _places,
        _favoritePlaceIds
    ) { places, favoriteIds ->
        places.map { place ->
            place.copy(isFavorite = place.id in favoriteIds)
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // User Management Functions (giữ nguyên)
    fun setCurrentUser(user: User?) {
        _currentUser.value = user
    }

    fun loginUser(email: String, password: String): User? {
        return _mockUsers.value.find { it.email == email }?.also { user ->
            _currentUser.value = user
            Log.d("PlaceViewModel", "User logged in: ${user.name}")
        }
    }

    fun logoutUser() {
        _currentUser.value = null
        Log.d("PlaceViewModel", "User logged out")
    }

    fun getCurrentUserStats(): Triple<Int, Int, Int> {
        val user = _currentUser.value
        return Triple(
            user?.favoriteCount ?: 0,
            user?.reviewCount ?: 0,
            user?.visitedPlaces ?: 0
        )
    }

    // Place Management Functions (giữ nguyên)
    fun getPlaceById(id: String) {
        Log.d("PlaceViewModel", "Getting place by id: $id")
        val place = _places.value.find { it.id == id }
        if (place != null) {
            val placeWithFavorite = place.copy(isFavorite = place.id in _favoritePlaceIds.value)
            _selectedPlace.value = placeWithFavorite
            Log.d("PlaceViewModel", "Found place: ${placeWithFavorite.name}, favorite: ${placeWithFavorite.isFavorite}")
        } else {
            Log.e("PlaceViewModel", "Place not found with id: $id")
            _selectedPlace.value = null
        }
    }

    fun isFavorite(placeId: String): StateFlow<Boolean> {
        return _favoritePlaceIds.map { favorites ->
            placeId in favorites
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    }

    fun toggleFavorite(placeId: String) {
        viewModelScope.launch {
            val currentFavorites = _favoritePlaceIds.value.toMutableSet()
            if (placeId in currentFavorites) {
                currentFavorites.remove(placeId)
                Log.d("PlaceViewModel", "Removed from favorites: $placeId")
            } else {
                currentFavorites.add(placeId)
                Log.d("PlaceViewModel", "Added to favorites: $placeId")
            }
            _favoritePlaceIds.value = currentFavorites

            // Cập nhật selected place nếu đang xem place này
            _selectedPlace.value?.let { currentPlace ->
                if (currentPlace.id == placeId) {
                    _selectedPlace.value = currentPlace.copy(isFavorite = placeId in currentFavorites)
                }
            }
        }
    }

    fun loadFavoritePlaces() {
        Log.d("PlaceViewModel", "Loading favorite places, current count: ${_favoritePlaceIds.value.size}")
    }

    fun removeFromFavorites(placeId: String) {
        viewModelScope.launch {
            val currentFavorites = _favoritePlaceIds.value.toMutableSet()
            currentFavorites.remove(placeId)
            _favoritePlaceIds.value = currentFavorites
            Log.d("PlaceViewModel", "Removed from favorites: $placeId")
        }
    }

    // Helper function để lấy place by id với favorite status
    fun getPlaceWithFavoriteStatus(placeId: String): Place? {
        return _places.value.find { it.id == placeId }?.copy(
            isFavorite = placeId in _favoritePlaceIds.value
        )
    }

    // Tìm kiếm địa điểm theo tên hoặc category
    fun searchPlaces(query: String): List<Place> {
        return if (query.isBlank()) {
            _places.value
        } else {
            _places.value.filter { place ->
                place.name.contains(query, ignoreCase = true) ||
                        place.category.contains(query, ignoreCase = true) ||
                        place.address.contains(query, ignoreCase = true)
            }
        }
    }

    // Lấy địa điểm theo category
    fun getPlacesByCategory(category: String): List<Place> {
        return _places.value.filter { it.category == category }
    }

    // Lấy các category duy nhất
    fun getUniqueCategories(): List<String> {
        return _places.value.map { it.category }.distinct()
    }

    // Lấy user by id
    fun getUserById(userId: String): User? {
        return _mockUsers.value.find { it.id == userId }
    }

    // Lấy tất cả users (cho mục đích debug hoặc admin)
    fun getAllUsers(): List<User> {
        return _mockUsers.value
    }

    // Bổ sung: Lấy tọa độ của place theo ID
    fun getPlaceCoordinates(placeId: String): Pair<Double, Double>? {
        val place = _places.value.find { it.id == placeId }
        return place?.let { it.latitude to it.longitude }
    }

    // Bổ sung: Lấy địa chỉ đầy đủ cho map
    fun getPlaceAddress(placeId: String): String {
        return _places.value.find { it.id == placeId }?.address ?: ""
    }
}