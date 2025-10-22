package com.hiddendanang.app.ui.model

import androidx.lifecycle.ViewModel

data class Place(
    val name: String,
    val address: String,
    val images: List<String> = listOf(

    ),
    val rating: Float =4.7f,
    val reviewCount: Int = 100,
    val category: String ,
    val hours: String,
    val priceRange: String,
    val distance: String,
    val description: String,
    val reviews: List<Review> = emptyList(),
    val image: String ="",
)

data class Review(
    val user: String,
    val timestamp: String,
    val rating: Float,
    val comment: String,

)
class PlaceViewModel : ViewModel() {
    val topPlace = listOf(
        Place(
            name = "Cầu Rồng Đà Nẵng",
            address = "Sông Hàn, Quận Hải Châu, Đà Nẵng",
            images = listOf(
                "https://danangfantasticity.com/wp-content/uploads/2018/10/cau-rong-top-20-cay-cau-ky-quai-nhat-the-gioi-theo-boredom-therapy-02.jpg",
                "url_hinh_anh_cau_rong_2"
            ),
            rating = 4.9f,
            reviewCount = 1500,
            category = "Địa điểm nổi tiếng",
            hours = "Luôn mở cửa (Phun lửa/nước T7 & CN lúc 21:00)",
            priceRange = "Miễn phí",
            distance = "5.0 km",
            description = "Biểu tượng kiến trúc độc đáo bắc qua sông Hàn, nổi tiếng với màn trình diễn phun lửa và nước vào cuối tuần.",
            reviews = listOf(
                Review("Nguyễn A", "20/10/2025", 5.0f, "Cảnh đẹp tuyệt vời!"),
                Review("Trần B", "18/10/2025", 4.5f, "Phun lửa rất ấn tượng.")
            ),
            image = "https://danangfantasticity.com/wp-content/uploads/2018/10/cau-rong-top-20-cay-cau-ky-quai-nhat-the-gioi-theo-boredom-therapy-02.jpg"
        ),

        Place(
            name = "Bà Nà Hills",
            address = "Hoà Ninh, Hoà Vang, Đà Nẵng",
            images = listOf(
                "url_hinh_anh_bana_1",
                "url_hinh_anh_bana_2"
            ),
            rating = 4.7f,
            reviewCount = 2000,
            category = "Khu vui chơi giải trí",
            hours = "07:30 - 17:30",
            priceRange = "$$$",
            distance = "25.0 km",
            description = "Khu nghỉ dưỡng và giải trí trên đỉnh núi Chúa, nổi bật với Cầu Vàng và cáp treo dài kỷ lục.",
            reviews = emptyList(),
            image = "url_main_bana_hills"
        ),

        Place(
            name = "Bãi biển Mỹ Khê",
            address = "Đường Võ Nguyên Giáp, Đà Nẵng",
            images = listOf(
                "url_hinh_anh_my_khe_1",
                "url_hinh_anh_my_khe_2"
            ),
            rating = 4.8f,
            reviewCount = 1800,
            category = "Bãi biển",
            hours = "Luôn mở cửa",
            priceRange = "Miễn phí",
            distance = "2.5 km",
            description = "Một trong những bãi biển đẹp nhất hành tinh, với bờ cát trắng mịn, sóng êm và nước biển trong xanh.",
            reviews = listOf(
                Review("Lê C", "15/10/2025", 5.0f, "Bãi biển sạch và đẹp tuyệt vời!"),
            ),
            image = "url_main_my_khe"
        ),


        Place(
            name = "Ngũ Hành Sơn",
            address = "Phường Hoà Hải, Quận Ngũ Hành Sơn, Đà Nẵng",
            images = listOf(
                "url_hinh_anh_ngu_hanh_son_1",
                "url_hinh_anh_ngu_hanh_son_2"
            ),
            rating = 4.6f,
            reviewCount = 1200,
            category = "Di tích lịch sử & Danh lam thắng cảnh",
            hours = "07:00 - 17:30",
            priceRange = "$$", // Có phí vào cổng
            distance = "9.0 km",
            description = "Quần thể năm ngọn núi đá vôi với nhiều hang động, chùa chiền tâm linh và làng nghề đá mỹ nghệ truyền thống.",
            reviews = emptyList(),
            image = "url_main_ngu_hanh_son"
        ),

        Place(
            name = "Chùa Linh Ứng Bán đảo Sơn Trà",
            address = "Bán đảo Sơn Trà, Quận Sơn Trà, Đà Nẵng",
            images = listOf(
                "url_hinh_anh_linh_ung_1",
                "url_hinh_anh_linh_ung_2"
            ),
            rating = 4.7f,
            reviewCount = 1400,
            category = "Di tích tâm linh",
            hours = "06:00 - 18:00",
            priceRange = "Miễn phí",
            distance = "12.0 km",
            description = "Ngôi chùa nổi tiếng với tượng Phật Quan Thế Âm cao nhất Việt Nam, nhìn ra biển Đông và thành phố Đà Nẵng.",
            reviews = listOf(
                Review("Phạm D", "10/10/2025", 5.0f, "Khung cảnh hùng vĩ, tâm linh và thanh tịnh."),
            ),
            image = "url_main_linh_ung"
        ),

        // --- 6. Công viên Biển Đông ---
        Place(
            name = "Công viên Biển Đông",
            address = "Đường Võ Nguyên Giáp, Quận Sơn Trà, Đà Nẵng",
            images = listOf(
                "url_hinh_anh_cvbd_1",
                "url_hinh_anh_cvbd_2"
            ),
            rating = 4.5f,
            reviewCount = 800,
            category = "Công viên / Khu vực công cộng",
            hours = "Luôn mở cửa",
            priceRange = "Miễn phí",
            distance = "3.0 km",
            description = "Nơi tập trung các hoạt động cộng đồng, nổi tiếng với hàng ngàn con chim bồ câu thân thiện và view biển tuyệt đẹp.",
            reviews = emptyList(),
            image = "url_main_cv_bien_dong"
        )
    )
}