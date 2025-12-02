package com.hiddendanang.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

class DebugSeeder {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val placesCollection = firestore.collection("places")

    suspend fun checkFirestoreConnection(): Boolean {
        return try {
            placesCollection.get(Source.SERVER).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun seedAllPlaces(): Int {
        val batch = firestore.batch()

        val placesData = getMockPlacesData()

        if (placesData.isEmpty()) {

        }

        placesData.forEachIndexed { index, data ->
            // Sử dụng document() để Firestore tự động tạo ID.
            val docRef = placesCollection.document()
            batch.set(docRef, data)
        }


        return try {
            batch.commit().await()
            placesData.size
        } catch (e: Exception) {
            0
        }
    }

    // --- CÁC HẰNG SỐ DỮ LIỆU ĐƯỢC GIỮ LẠI TRONG CLASS ---

    // Dải giá chung (20k - 50k VND)
    val defaultPriceRange = mapOf(
        "min" to 20000,
        "max" to 90000,
        "currency" to "VND"
    )

    // Giờ mở cửa chung theo yêu cầu
    val defaultOpeningHours = mapOf(
        "mon" to mapOf("open" to "07:00", "close" to "22:00", "is_closed" to false),
        "tue" to mapOf("open" to "07:00", "close" to "22:00", "is_closed" to false),
        "wed" to mapOf("open" to "07:00", "close" to "22:00", "is_closed" to false),
        "thu" to mapOf("open" to "07:00", "close" to "22:00", "is_closed" to false),
        "fri" to mapOf("open" to "07:00", "close" to "23:00", "is_closed" to false),
        "sat" to mapOf("open" to "07:00", "close" to "23:00", "is_closed" to false),
        "sun" to mapOf("open" to "08:00", "close" to "23:00", "is_closed" to false)
    )

    // Dành cho địa điểm miễn phí và mở cửa 24/7 (Ví dụ: Cầu Rồng, Bãi biển)
    val free24h = mapOf(
        "price_range_detail" to mapOf("min" to 0, "max" to 0, "currency" to "VND"),
        "opening_hours" to mapOf(
            "mon" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false),
            "tue" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false),
            "wed" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false),
            "thu" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false),
            "fri" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false),
            "sat" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false),
            "sun" to mapOf("open" to "00:00", "close" to "00:00", "is_closed" to false)
        )
    )

    // Dành cho các địa điểm tham quan/bảo tàng có giờ hành chính
    val sightseeingHours = mapOf(
        "price_range_detail" to mapOf("min" to 25000, "max" to 200000, "currency" to "VND"), // Ví dụ vé vào cửa
        "opening_hours" to mapOf(
            "mon" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false),
            "tue" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false),
            "wed" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false),
            "thu" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false),
            "fri" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false),
            "sat" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false),
            "sun" to mapOf("open" to "07:30", "close" to "17:30", "is_closed" to false)
        )
    )

    // Dành cho chợ đêm/khu vui chơi buổi tối
    val nightEntertainmentHours = mapOf(
        "price_range_detail" to mapOf("min" to 50000, "max" to 500000, "currency" to "VND"),
        "opening_hours" to mapOf(
            "mon" to mapOf("open" to "17:00", "close" to "22:00", "is_closed" to false),
            "tue" to mapOf("open" to "17:00", "close" to "22:00", "is_closed" to false),
            "wed" to mapOf("open" to "17:00", "close" to "22:00", "is_closed" to false),
            "thu" to mapOf("open" to "17:00", "close" to "22:00", "is_closed" to false),
            "fri" to mapOf("open" to "17:00", "close" to "23:00", "is_closed" to false),
            "sat" to mapOf("open" to "17:00", "close" to "23:00", "is_closed" to false),
            "sun" to mapOf("open" to "17:00", "close" to "23:00", "is_closed" to false)
        )
    )

    // Giờ Buffet (10:30 - 14:00 và 17:30 - 22:00)
    val buffetHours = mapOf(
        "mon" to mapOf("open" to "10:30", "close" to "22:00", "is_closed" to false), // Cần logic phức tạp hơn cho suất trưa/tối
        "tue" to mapOf("open" to "10:30", "close" to "22:00", "is_closed" to false),
        "wed" to mapOf("open" to "10:30", "close" to "22:00", "is_closed" to false),
        "thu" to mapOf("open" to "10:30", "close" to "22:00", "is_closed" to false),
        "fri" to mapOf("open" to "10:30", "close" to "22:30", "is_closed" to false),
        "sat" to mapOf("open" to "10:30", "close" to "22:30", "is_closed" to false),
        "sun" to mapOf("open" to "10:30", "close" to "22:30", "is_closed" to false)
    )

    // Dải giá Buffet cao cấp/đa dạng
    val highEndBuffetPrice = mapOf(
        "min" to 250000,
        "max" to 500000,
        "currency" to "VND"
    )

    // Dải giá Buffet tầm trung/lẩu nướng
    val midRangeBuffetPrice = mapOf(
        "min" to 199000,
        "max" to 350000,
        "currency" to "VND"
    )

    // --- HÀM GETMOCKPLACESDATA ĐÃ ĐƯỢC CHỈNH SỬA VỚI TẤT CẢ DỮ LIỆU VÀ CÁC TRƯỜNG MỚI ---

    private fun getMockPlacesData(): List<Map<String, Any?>> {

        // --- KHỐI ĐỊNH NGHĨA HẰNG SỐ (CẦN CÓ TRONG CLASS DEBUGSEEDEER) ---
        // (Giả sử các hằng số defaultPriceRange, sightseeingHours, free24h, nightEntertainmentHours, buffetHours, highEndBuffetPrice, midRangeBuffetPrice đã được định nghĩa trong class DebugSeeder)

        return listOf(
            // ===================================================================
            //                            HIDDEN GEMS (9 ĐIỂM)
            // ===================================================================

            // === HIDDEN GEM 1: Hẻm Ẩm Thực Trần Bình Trọng (Food) ===
            mapOf(
                "name" to "Hẻm Ẩm Thực Trần Bình Trọng",
                "name_lower" to "hẻm ẩm thực trần bình trọng",
                "description" to "Hẻm nhỏ tập trung các món ăn vặt và đặc sản địa phương ít khách du lịch biết.",
                "category_id" to "category_hidden",
                "subcategory" to "Street Food",
                "coordinates" to mapOf("latitude" to 16.0620, "longitude" to 108.2160, "geohash" to "w7gxdj"),
                "address" to mapOf("formatted_address" to "Đường Trần Bình Trọng, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/tran-binh-trong.jpg?alt=media&token=7ebcefab-7459-4bc0-9fe4-1d0af9ad43b9"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/tran-binh-trong-2.jpg?alt=media&token=1869007f-c038-42fd-ae51-b3d5e9393f8b"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/tran-binh-trong-3.jpg?alt=media&token=f2ffe9b2-e6a4-4bd5-8942-af1be18f890e")),
                "status" to "active",
                "popularity_score" to 7.8
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === HIDDEN GEM 2: Đỉnh Bàn Cờ - Sơn Trà (Sightseeing, Free, 24h) ===
            mapOf(
                "name" to "Đỉnh Bàn Cờ - Sơn Trà",
                "name_lower" to "đỉnh bàn cờ - sơn trà",
                "description" to "Điểm ngắm toàn cảnh thành phố và biển cực đẹp, ít người biết đến, đường đi hơi khó khăn.",
                "category_id" to "category_hidden",
                "subcategory" to "Sightseeing",
                "coordinates" to mapOf("latitude" to 16.1070, "longitude" to 108.2690, "geohash" to "w7gxm0"),
                "address" to mapOf("formatted_address" to "Bán đảo Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/dinh-ban-co.jpg?alt=media&token=50d10fc7-19fc-4f1b-8bbc-b6ed42cffb07"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/dinh-ban-co-2.jpg?alt=media&token=3fb5db7b-35af-448e-a830-a95f977b7f22"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/dinh-ban-co-3.jpg?alt=media&token=e7ea3364-97db-42b4-8c89-332937779dc2")),
                "status" to "active",
                "popularity_score" to 8.0
            ) + sightseeingHours,

            // === HIDDEN GEM 3: Rừng Dừa Bảy Mẫu (Nature, Vé vào cổng) ===
            mapOf(
                "name" to "Rừng Dừa Bảy Mẫu (Đoạn vắng)",
                "name_lower" to "rừng dừa bảy mẫu (đoạn vắng)",
                "description" to "Một đoạn vắng vẻ của Rừng Dừa, trải nghiệm chèo thuyền thúng yên tĩnh hơn.",
                "category_id" to "category_hidden",
                "subcategory" to "Nature",
                "coordinates" to mapOf("latitude" to 15.8900, "longitude" to 108.3300, "geohash" to "w7gq7m"),
                "address" to mapOf("formatted_address" to "Cẩm Thanh, Hội An (Gần Đà Nẵng)"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/rung-dua-bay-mau.jpg?alt=media&token=415d0c66-d354-4b08-b05d-2a07d7f932af"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/rung-dua-bay-mau-2.jpg?alt=media&token=9b36bec9-e87b-43da-b03d-59cf456a7a46"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/rung-dua-bay-mau-3.jpg?alt=media&token=185be4ae-e4c1-4d3f-a32d-0ceab4698a14")),
                "status" to "active",
                "popularity_score" to 7.5
            ) + sightseeingHours,

            // === HIDDEN GEM 4: Bảo Tàng Đồng Đình (Culture, Sightseeing Hours) ===
            mapOf(
                "name" to "Bảo Tàng Đồng Đình",
                "name_lower" to "bảo tàng đồng đình",
                "description" to "Một khu vườn-bảo tàng tư nhân độc đáo trên bán đảo Sơn Trà, ít người biết.",
                "category_id" to "category_hidden",
                "subcategory" to "Culture",
                "coordinates" to mapOf("latitude" to 16.1180, "longitude" to 108.2580, "geohash" to "w7gxm5"),
                "address" to mapOf("formatted_address" to "Hoàng Sa, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bao-tang-dong-dinh.jpg?alt=media&token=82772a44-b753-445b-b42c-8ac4ed11b903"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-cha-ca-2.jpg?alt=media&token=17c1f005-8527-44f7-9891-5ddb3e81c2b3"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bao-tang-dong-dinh-3.jpg?alt=media&token=7cf2b0c2-3edd-4a66-8920-6d63b88138dc")),
                "status" to "active",
                "popularity_score" to 7.7
            ) + sightseeingHours,

            // === HIDDEN GEM 5: Bãi Đá Ông Táo (Chill Spot, Free, 24h) ===
            mapOf(
                "name" to "Bãi Đá Đen",
                "name_lower" to "bãi đá đen",
                "description" to "Bãi biển hoang sơ, nhiều đá lớn, thích hợp để 'chill' và chụp ảnh độc đáo.",
                "category_id" to "category_hidden",
                "subcategory" to "Chill Spot",
                "coordinates" to mapOf("latitude" to 16.14935, "longitude" to 108.23410, "geohash" to "w7gxk8"),
                "address" to mapOf("formatted_address" to "Bán đảo Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bao-tang-dong-dinh-3.jpg?alt=media&token=7cf2b0c2-3edd-4a66-8920-6d63b88138dc"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bai-da-den-2.jpg?alt=media&token=94375e03-1ae4-4f4f-9f6b-67c11a6ac6bf"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bai-da-den-3.jpg?alt=media&token=0f7e529f-b284-42aa-b7d0-a4a9b2666d92")),
                "status" to "active",
                "popularity_score" to 7.6
            ) + free24h,

            // === HIDDEN GEM 6: Đèo Hải Vân (Sightseeing, Free, 24h) ===
            mapOf(
                "name" to "Đèo Hải Vân (Đoạn Cũ)",
                "name_lower" to "đèo hải vân (đoạn cũ)",
                "description" to "Đoạn đèo cũ ít xe cộ, cung đường ngoạn mục với view núi và biển tuyệt đẹp.",
                "category_id" to "category_hidden",
                "subcategory" to "Sightseeing",
                "coordinates" to mapOf("latitude" to 16.1950, "longitude" to 108.0800, "geohash" to "w7gwrz"),
                "address" to mapOf("formatted_address" to "Quốc lộ 1A, Hải Vân, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/deo-hai-van.jpg?alt=media&token=e46bbe66-8549-446c-931e-6fc55d51b031"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/deo-hai-van-2.jpg?alt=media&token=9b7a90fb-4926-447f-828d-284747982dae"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/deo-hai-van-3.jpg?alt=media&token=28a358f1-912f-4d90-a3f5-ad49c27b1638")),
                "status" to "active",
                "popularity_score" to 8.1
            ) + free24h,


            // === HIDDEN GEM 7: Thác Hoà Phú Thành (Nature, Sightseeing Hours) ===
            mapOf(
                "name" to "Thác Hoà Phú Thành",
                "name_lower" to "thác hoà phú thành",
                "description" to "Khu du lịch sinh thái với hoạt động trượt thác và tắm suối, khuất xa trung tâm.",
                "category_id" to "category_hidden",
                "subcategory" to "Nature",
                "coordinates" to mapOf("latitude" to 15.9300, "longitude" to 108.0200, "geohash" to "w7gqw4"),
                "address" to mapOf("formatted_address" to "Hòa Phú, Hòa Vang, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/thac-hoa-phu-thanh.jpg?alt=media&token=cef1536b-79e8-4018-8d5c-9c0afe301c95"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/thac-hoa-phu-thanh-2.jpg?alt=media&token=02791df9-22f6-4116-b49c-04f61fbe5b57"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/thac-hoa-phu-thanh-3.jpg?alt=media&token=6a619b85-81a3-4bf2-9532-39776af03798")),
                "status" to "active",
                "popularity_score" to 7.5
            ) + sightseeingHours,


            // === HIDDEN GEM 8: Chùa Bát Nhã (Culture, Sightseeing Hours) ===
            mapOf(
                "name" to "Chùa Bát Nhã",
                "name_lower" to "chùa bát nhã",
                "description" to "Ngôi chùa nằm trên núi, kiến trúc độc đáo, không gian thanh tịnh.",
                "category_id" to "category_hidden",
                "subcategory" to "Culture",
                "coordinates" to mapOf("latitude" to 16.0350, "longitude" to 108.2000, "geohash" to "w7gxhj"),
                "address" to mapOf("formatted_address" to "Hòa Minh, Liên Chiểu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/chua-bat-nha.jpg?alt=media&token=2b3700f2-6726-4923-be4a-a39c35738c32"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/chua-bat-nha-2.jpg?alt=media&token=7e05006f-e79f-4f99-9ec0-bd365315e339"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/chua-bat-nha-3.jpg?alt=media&token=8208f77c-4516-401b-8eb3-cf1dae3f502a")),
                "status" to "active",
                "popularity_score" to 7.3
            ) + sightseeingHours,

            // === HIDDEN GEM 9: Chợ Hoà Cường (Sightseeing/Market) ===
            mapOf(
                "name" to "Chợ Hoà Cường",
                "name_lower" to "chợ hoà cường",
                "description" to "Khu chợ địa phương sầm uất, bán nhiều loại đặc sản, trải nghiệm văn hóa địa phương.",
                "category_id" to "category_hidden",
                "subcategory" to "Sightseeing",
                "coordinates" to mapOf("latitude" to 16.0450, "longitude" to 108.2250, "geohash" to "w7gxdj"),
                "address" to mapOf("formatted_address" to "Trần Văn Dư, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cho-hoa-cuong.jpg?alt=media&token=5c01dbb0-dca5-4969-818b-083423df9014"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cho-hoa-cuong-2.jpg?alt=media&token=4bd1fd0f-bf6a-4815-9faa-8912e73a1c9a"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cho-hoa-cuong-3.jpg?alt=media&token=c4db9f98-b281-4bfb-85a1-649ca0842b7f")),
                "status" to "active",
                "popularity_score" to 7.9
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to sightseeingHours["opening_hours"]),

            // ===================================================================
            //                            POPULAR PLACES (26 ĐIỂM)
            // ===================================================================

            // === POPULAR 1: Bún Quậy Phú Quốc Út Nhi (Food) ===
            mapOf(
                "name" to "Bún Quậy Phú Quốc Út Nhi",
                "name_lower" to "bún quậy phú quốc út nhi",
                "description" to "Món bún với hương vị mới lạ, cùng các loại chả mực, chả tôm mang lại trải nghiệm mới mẻ cho thực khách Đà Nẵng",
                "category_id" to "category_food",
                "subcategory" to "Street Food",
                "coordinates" to mapOf("latitude" to 16.068, "longitude" to 108.149, "geohash" to "w7gx6y"),
                "address" to mapOf("formatted_address" to "70 Ngô Văn Sở, Hoà Khánh, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-quay-phu-quoc.jpg?alt=media&token=7640bfc9-25ac-4dbf-9d5f-f801004bbd7f"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-quay-phu-quoc-2.jpg?alt=media&token=a21bb5c9-6cfe-45dd-b1f0-14890ec97e77"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-quay-phu-quoc-3.jpg?alt=media&token=c38991e5-eaa3-4bbb-b3b0-a90b87a3f068")),
                "status" to "active",
                "popularity_score" to 8.5
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 2: Cầu Rồng (Sightseeing, Free, 24h) ===
            mapOf(
                "name" to "Cầu Rồng",
                "name_lower" to "cầu rồng",
                "description" to "Biểu tượng của Đà Nẵng, phun lửa và nước vào cuối tuần.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge",
                "coordinates" to mapOf("latitude" to 16.0601, "longitude" to 108.2255, "geohash" to "w7gxdp"),
                "address" to mapOf("formatted_address" to "Sông Hàn, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cau-rong.jpg?alt=media&token=7f337409-3d0f-426f-95f9-fe675e5f5bc5")),
                "status" to "active",
                "popularity_score" to 9.2
            ) + free24h,

            // === POPULAR 3: Ngũ Hành Sơn (Sightseeing Hours) ===
            mapOf(
                "name" to "Ngũ Hành Sơn",
                "name_lower" to "ngũ hành sơn",
                "description" to "Quần thể 5 ngọn núi đá vôi với nhiều hang động và đền chùa cổ kính.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Mountain",
                "coordinates" to mapOf("latitude" to 15.9892, "longitude" to 108.2831, "geohash" to "w7grv8"),
                "address" to mapOf("formatted_address" to "Hòa Hải, Ngũ Hành Sơn, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/ngu-hanh-son-3.jpg?alt=media&token=9c13423b-d4f1-4d52-bc8f-aa67f3f958cf")),
                "status" to "active",
                "popularity_score" to 8.9
            ) + sightseeingHours,

            // === POPULAR 4: Công Viên Châu Á (Chill, Night Entertainment Hours) ===
            mapOf(
                "name" to "Công Viên Châu Á (Asia Park)",
                "name_lower" to "công viên châu á",
                "description" to "Khu vui chơi giải trí lớn với vòng quay Sun Wheel và nhiều trò chơi hấp dẫn.",
                "category_id" to "category_chill",
                "subcategory" to "Theme Park",
                "coordinates" to mapOf("latitude" to 16.0474, "longitude" to 108.2231, "geohash" to "w7gxdj"),
                "address" to mapOf("formatted_address" to "01 Phan Đăng Lưu, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to ""),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cong-vien-chau-a(2).jpg?alt=media&token=0cec3713-beb3-4550-a716-d85c42de5acf"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cong-vien-chau-a(3).jpg?alt=media&token=df6313f8-e130-493b-93e3-a89ba441ed84")),
                "status" to "active",
                "popularity_score" to 9.0
            ) + nightEntertainmentHours,

            // === POPULAR 5: Cầu Tình Yêu (Sightseeing, Free, 24h) ===
            mapOf(
                "name" to "Cầu Tình Yêu",
                "name_lower" to "cầu tình yêu",
                "description" to "Cây cầu lãng mạn trên sông Hàn, nơi các cặp đôi treo ổ khóa tình yêu.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge",
                "coordinates" to mapOf("latitude" to 16.0640, "longitude" to 108.2290, "geohash" to "w7gxdr"),
                "address" to mapOf("formatted_address" to "Trần Hưng Đạo, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cau-tinh-yeu.jpg?alt=media&token=ee8b74e7-d3f1-42a5-be22-61a391cb9e8a")),
                "status" to "active",
                "popularity_score" to 8.8
            ) + free24h,

            // === POPULAR 6: Bảo Tàng Điêu Khắc Chăm (Sightseeing Hours) ===
            mapOf(
                "name" to "Bảo Tàng Điêu Khắc Chăm",
                "name_lower" to "bảo tàng điêu khắc chăm",
                "description" to "Nơi lưu giữ bộ sưu tập hiện vật Chăm Pa lớn nhất thế giới.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Museum",
                "coordinates" to mapOf("latitude" to 16.0625, "longitude" to 108.2245, "geohash" to "w7gxdq"),
                "address" to mapOf("formatted_address" to "02 2 Tháng 9, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bao-tang-dieu-khac-cham.jpg?alt=media&token=d6c49f03-07ee-402b-be61-94c39919fc2f")),
                "status" to "active",
                "popularity_score" to 8.6
            ) + sightseeingHours,

            // === POPULAR 7: Café Memory Lounge (Coffee) ===
            mapOf(
                "name" to "Café Memory Lounge",
                "name_lower" to "café memory lounge",
                "description" to "Quán cà phê độc đáo hình chiếc lá nằm trên sông Hàn, nổi tiếng với không gian sang trọng.",
                "category_id" to "category_coffee",
                "subcategory" to "Café",
                "coordinates" to mapOf("latitude" to 16.0671, "longitude" to 108.2293, "geohash" to "w7gxdx"),
                "address" to mapOf("formatted_address" to "7 Bạch Đằng, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/memory-lounge-da-nang.jpeg?alt=media&token=ad190629-8a56-4986-a555-2d395290ffce")),
                "status" to "active",
                "popularity_score" to 8.2
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 8: Helio Center (Chill, Night Entertainment Hours) ===
            mapOf(
                "name" to "Helio Center",
                "name_lower" to "helio center",
                "description" to "Tổ hợp vui chơi giải trí, ẩm thực đêm và chợ đêm nổi tiếng dành cho giới trẻ.",
                "category_id" to "category_chill",
                "subcategory" to "Entertainment Center",
                "coordinates" to mapOf("latitude" to 16.0470, "longitude" to 108.2212, "geohash" to "w7gxdh"),
                "address" to mapOf("formatted_address" to "2/9, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/helio-center.jpg?alt=media&token=6b9530d8-4690-448f-a278-cc486f89226b")),
                "status" to "active",
                "popularity_score" to 8.5
            ) + nightEntertainmentHours,

            // === POPULAR 9: Café 1975 (Coffee) ===
            mapOf(
                "name" to "Café 1975",
                "name_lower" to "café 1975",
                "description" to "Quán cà phê mang phong cách retro, gợi nhớ về Đà Nẵng xưa cũ.",
                "category_id" to "category_coffee",
                "subcategory" to "Café",
                "coordinates" to mapOf("latitude" to 16.0670, "longitude" to 108.2208, "geohash" to "w7gxdk"),
                "address" to mapOf("formatted_address" to "45 Lê Lợi, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cafe-1975-da-nang.jpg?alt=media&token=0765eb16-58fc-4c38-9d4c-f410276ba2c1")),
                "status" to "active",
                "popularity_score" to 8.1
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 10: Công Viên Biển Đông (Chill, Free, 24h) ===
            mapOf(
                "name" to "Công Viên Biển Đông",
                "name_lower" to "công viên biển đông",
                "description" to "Nơi thường xuyên tổ chức lễ hội và có hàng ngàn chim bồ câu trắng bay lượn mỗi chiều.",
                "category_id" to "category_chill",
                "subcategory" to "Park",
                "coordinates" to mapOf("latitude" to 16.0690, "longitude" to 108.2475, "geohash" to "w7gx6z"),
                "address" to mapOf("formatted_address" to "Phạm Văn Đồng, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cong-vien-bien-dong-1.jpg?alt=media&token=89fbd11b-6075-4a4a-8ee1-4446b98279b0")),
                "status" to "active",
                "popularity_score" to 8.4
            ) + free24h,

            // === POPULAR 11: Hải Sản Bé Mặn (Food, High Price) ===
            mapOf(
                "name" to "Hải Sản Bé Mặn",
                "name_lower" to "hải sản bé mặn",
                "description" to "Quán hải sản tươi ngon nổi tiếng, được nhiều du khách yêu thích.",
                "category_id" to "category_food",
                "subcategory" to "Seafood",
                "coordinates" to mapOf("latitude" to 16.0760, "longitude" to 108.2501, "geohash" to "w7gx7g"),
                "address" to mapOf("formatted_address" to "11 Võ Nguyên Giáp, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/hai-san-be-man.jpg?alt=media&token=02c548cd-083e-4af0-a697-30bf5d3bffa6"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/hai-san-be-man-2.jpg?alt=media&token=cdfcf004-25bf-4eee-bb53-1cf628fac4e1")),
                "status" to "active",
                "popularity_score" to 8.9
            ) + mapOf("price_range_detail" to mapOf("min" to 100000, "max" to 500000, "currency" to "VND"), "opening_hours" to defaultOpeningHours),

            // === POPULAR 12: Nhà Thờ Con Gà (Sightseeing Hours) ===
            mapOf(
                "name" to "Nhà Thờ Con Gà (Chính Tòa)",
                "name_lower" to "nhà thờ con gà",
                "description" to "Công trình kiến trúc Pháp cổ kính với biểu tượng con gà trên đỉnh tháp.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Church",
                "coordinates" to mapOf("latitude" to 16.0669, "longitude" to 108.2225, "geohash" to "w7gxdm"),
                "address" to mapOf("formatted_address" to "156 Trần Phú, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/nha-tho-con-ga.jpg?alt=media&token=31066dea-27d5-4d95-b286-f8000096fd50")),
                "status" to "active",
                "popularity_score" to 8.7
            ) + sightseeingHours,

            // === POPULAR 13: Cầu Thuận Phước (Sightseeing, Free, 24h) ===
            mapOf(
                "name" to "Cầu Thuận Phước",
                "name_lower" to "cầu thuận phước",
                "description" to "Cầu dây võng dài nhất Việt Nam, nối liền trung tâm thành phố với bán đảo Sơn Trà.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge",
                "coordinates" to mapOf("latitude" to 16.0967, "longitude" to 108.2358, "geohash" to "w7gx98"),
                "address" to mapOf("formatted_address" to "Thuận Phước, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cau-thuan-phuoc.jpg?alt=media&token=590c19f9-2b74-4f74-9ac4-176142e5b89c"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cau-thuan-phuoc-2.jpg?alt=media&token=8fbee0e9-e042-42cf-bd7b-0cbcdb15233a")),
                "status" to "active",
                "popularity_score" to 8.5
            ) + free24h,

            // === POPULAR 14: Chùa Linh Ứng Bãi Bụt (Sightseeing Hours) ===
            mapOf(
                "name" to "Chùa Linh Ứng Bãi Bụt",
                "name_lower" to "chùa linh ứng bãi bụt",
                "description" to "Ngôi chùa linh thiêng với tượng Phật Quan Âm cao 67m nhìn ra biển.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Pagoda",
                "coordinates" to mapOf("latitude" to 16.1230, "longitude" to 108.2821, "geohash" to "w7gxk5"),
                "address" to mapOf("formatted_address" to "Bán đảo Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/chua-linh-ung.jpg?alt=media&token=02d73caa-b32e-4b1e-b828-d69f0d02a01a")),
                "status" to "active",
                "popularity_score" to 9.4
            ) + mapOf("price_range_detail" to free24h["price_range_detail"], "opening_hours" to sightseeingHours["opening_hours"]),

            // === POPULAR 15: Chợ Đêm Sơn Trà (Chill, Night Entertainment Hours) ===
            mapOf(
                "name" to "Chợ Đêm Sơn Trà",
                "name_lower" to "chợ đêm sơn trà",
                "description" to "Điểm đến sôi động vào buổi tối với nhiều gian hàng đồ ăn, quà lưu niệm và nhạc sống.",
                "category_id" to "category_chill",
                "subcategory" to "Night Market",
                "coordinates" to mapOf("latitude" to 16.0663, "longitude" to 108.2307, "geohash" to "w7gxds"),
                "address" to mapOf("formatted_address" to "Mai Hắc Đế, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cho-dem-son-tra-1.jpg?alt=media&token=a4ac0e1a-f459-457c-b1b9-f4d49c61ded0")),
                "status" to "active",
                "popularity_score" to 8.6
            ) + nightEntertainmentHours,

            // === POPULAR 16: Bún Chả Cá 109 Nguyễn Chí Thanh (Food) ===
            mapOf(
                "name" to "Bún Chả Cá 109 Nguyễn Chí Thanh",
                "name_lower" to "bún chả cá 109 nguyễn chí thanh",
                "description" to "Quán bún chả cá nổi tiếng và lâu đời ở Đà Nẵng.",
                "category_id" to "category_food",
                "subcategory" to "Noodle",
                "coordinates" to mapOf("latitude" to 16.0675, "longitude" to 108.2235, "geohash" to "w7gxdm"),
                "address" to mapOf("formatted_address" to "109 Nguyễn Chí Thanh, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-cha-ca.jpg?alt=media&token=fbe8a39a-e6b2-4d18-85a2-f0de8f20b4b8"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-cha-ca-2.jpg?alt=media&token=17c1f005-8527-44f7-9891-5ddb3e81c2b3"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bun-cha-ca-3.jpg?alt=media&token=acddd852-8900-4933-aacd-303c284c578a")),
                "status" to "active",
                "popularity_score" to 8.8
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 17: The Garden Coffee (Coffee) ===
            mapOf(
                "name" to "The Garden Coffee",
                "name_lower" to "the garden coffee",
                "description" to "Không gian cà phê sân vườn thoáng mát, thiết kế theo phong cách nhiệt đới.",
                "category_id" to "category_coffee",
                "subcategory" to "Garden Cafe",
                "coordinates" to mapOf("latitude" to 16.0500, "longitude" to 108.2300, "geohash" to "w7gxdr"),
                "address" to mapOf("formatted_address" to "Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/garden-coffe-2.jpg?alt=media&token=57108559-52f6-4399-834f-8d0015e4d803")),
                "status" to "active",
                "popularity_score" to 8.3
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 18: Bãi biển Mỹ Khê (Chill, Free, 24h) ===
            mapOf(
                "name" to "Bãi biển Mỹ Khê",
                "name_lower" to "bãi biển mỹ khê",
                "description" to "Một trong sáu bãi biển đẹp nhất hành tinh, sạch sẽ và phù hợp cho hoạt động thể thao.",
                "category_id" to "category_chill",
                "subcategory" to "Beach",
                "coordinates" to mapOf("latitude" to 16.0710, "longitude" to 108.2430, "geohash" to "w7gx7d"),
                "address" to mapOf("formatted_address" to "Võ Nguyên Giáp, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bai-bien-my-khe.jpg?alt=media&token=663667b4-30a1-439b-a3de-b94b43b1e1a7"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bai-bien-my-khe-2.jpg?alt=media&token=903b9db8-4131-44e9-94b7-405c352afa53"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/bai-bien-my-khe-3.jpg?alt=media&token=bdeb84e1-d816-4002-b924-4f2e6733b22a")),
                "status" to "active",
                "popularity_score" to 9.3
            ) + free24h,

            // === POPULAR 19: Bán đảo Sơn Trà (Sightseeing, Free, 24h) ===
            mapOf(
                "name" to "Bán đảo Sơn Trà (Tuyệt Đỉnh)",
                "name_lower" to "bán đảo sơn trà (tuyệt đỉnh)",
                "description" to "Khu bảo tồn thiên nhiên, nơi lý tưởng để ngắm Voọc chà vá chân nâu và ngắm cảnh.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Nature Reserve",
                "coordinates" to mapOf("latitude" to 16.1000, "longitude" to 108.2700, "geohash" to "w7gxm2"),
                "address" to mapOf("formatted_address" to "Bán đảo Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/ban-dao-son-tra-2.jpg?alt=media&token=263ec5bf-8eb0-4116-8ba3-24ac1e3222c0"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/ban-dao-son-tra.jpg?alt=media&token=7d84e756-664c-4b0e-b97d-e742e3f32fab")),
                "status" to "active",
                "popularity_score" to 9.0
            ) + free24h,

            // === POPULAR 20: Quán Trần - Bánh Tráng Thịt Heo (Food) ===
            mapOf(
                "name" to "Quán Trần - Bánh Tráng Thịt Heo",
                "name_lower" to "quán trần - bánh tráng thịt heo",
                "description" to "Quán nổi tiếng với món bánh tráng cuốn thịt heo hai đầu da đặc trưng Đà Nẵng.",
                "category_id" to "category_food",
                "subcategory" to "Local Delicacy",
                "coordinates" to mapOf("latitude" to 16.0680, "longitude" to 108.2280, "geohash" to "w7gxdx"),
                "address" to mapOf("formatted_address" to "4 Lê Duẩn, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/quan-tran.jpg?alt=media&token=02e274c3-b123-4196-9677-4cdc46322b30"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/quan-tran-2.jpg?alt=media&token=f45a790d-6371-4075-887a-54f76b4f863f")),
                "status" to "active",
                "popularity_score" to 8.7
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 21: Wonderlust Danang (Coffee) ===
            mapOf(
                "name" to "Wonderlust Danang",
                "name_lower" to "wonderlust danang",
                "description" to "Quán cà phê có không gian mở, thiết kế tối giản, view biển đẹp.",
                "category_id" to "category_coffee",
                "subcategory" to "View Cafe",
                "coordinates" to mapOf("latitude" to 16.0675, "longitude" to 108.2490, "geohash" to "w7gx7f"),
                "address" to mapOf("formatted_address" to "Trần Bạch Đằng, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/wonderlust-coffee-2.jpg?alt=media&token=a13d064f-8a12-47a8-853e-48cce80e4e2b"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/wonderlust-coffee.jpg?alt=media&token=b050bdf6-25b4-4628-bfa5-f9099ebd1110")),
                "status" to "active",
                "popularity_score" to 8.4
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === POPULAR 24: Cầu Vàng (Bà Nà Hills) (Sightseeing, High Price) ===
            mapOf(
                "name" to "Cầu Vàng (Bà Nà Hills)",
                "name_lower" to "cầu vàng (bà nà hills)",
                "description" to "Cây cầu nổi tiếng với kiến trúc bàn tay khổng lồ, nằm trong khu du lịch Bà Nà Hills.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge/Theme Park",
                "coordinates" to mapOf("latitude" to 15.9900, "longitude" to 108.0680, "geohash" to "w7gqe6"),
                "address" to mapOf("formatted_address" to "Thôn An Sơn, Hòa Ninh, Hòa Vang, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cau-vang.jpg?alt=media&token=2c22128d-310e-43f6-916e-ecb9a032e1bd"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cau-vang-2.jpg?alt=media&token=639747e1-6b51-478c-8b55-069c0bc8a65f")),
                "status" to "active",
                "popularity_score" to 9.5
            ) + mapOf("price_range_detail" to mapOf("min" to 700000, "max" to 1000000, "currency" to "VND"), "opening_hours" to sightseeingHours["opening_hours"]),

            // === POPULAR 25: Chợ Hàn (Sightseeing Hours) ===
            mapOf(
                "name" to "Chợ Hàn",
                "name_lower" to "chợ hàn",
                "description" to "Chợ truyền thống lớn, nơi mua sắm đặc sản và quà lưu niệm.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Market",
                "coordinates" to mapOf("latitude" to 16.0660, "longitude" to 108.2260, "geohash" to "w7gxdm"),
                "address" to mapOf("formatted_address" to "119 Trần Phú, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cho-han.jpg?alt=media&token=f7aeccb5-941a-4805-9b1c-6d41b843d597"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cho-han-2.jpg?alt=media&token=d560b400-4191-4158-a25d-2d9e66c13089")),
                "status" to "active",
                "popularity_score" to 8.6
            ) + mapOf("price_range_detail" to free24h["price_range_detail"], "opening_hours" to sightseeingHours["opening_hours"]),

            // === POPULAR 26: Bánh Xèo Bà Dưỡng (Food) - Bản gốc ===
            mapOf(
                "name" to "Bánh Xèo Bà Dưỡng",
                "name_lower" to "bánh xèo bà dưỡng",
                "description" to "Quán bánh xèo nổi tiếng Đà Nẵng, giòn rụm và đậm đà hương vị.",
                "category_id" to "category_food",
                "subcategory" to "Street Food",
                "coordinates" to mapOf("latitude" to 16.0596, "longitude" to 108.2426, "geohash" to "w7gx6y"),
                "address" to mapOf("formatted_address" to "280 Hoàng Diệu, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/banh-xeo-ba-duong.jpg?alt=media&token=82f241cd-ea6f-478f-aee3-ede6719b76a2"),
                "status" to "active",
                "popularity_score" to 8.5
            )
            )
                        + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // ===================================================================
            //                            BUFFET PLACES (5 ĐIỂM)
            // ===================================================================

            // === BUFFET 1: SOHO Buffet & Alacarte ===
                mapOf(
                "name" to "SOHO Buffet & Alacarte",
                "name_lower" to "soho buffet & alacarte",
                "description" to "Nhà hàng buffet đa dạng ẩm thực Á-Âu, không gian sang trọng, vị trí trung tâm.",
                "category_id" to "category_food",
                "subcategory" to "International Buffet",
                "coordinates" to mapOf("latitude" to 16.0520, "longitude" to 108.2230, "geohash" to "w7gxdr"),
                "address" to mapOf("formatted_address" to "Lô 1+2, Khu công viên Bắc tượng đài, Đường 2/9, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/soho-buffet.jpg?alt=media&token=cea5594f-12bc-41b2-80e2-ffd06a3566c2"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/soho-buffet-2.jpg?alt=media&token=dce24ac1-3047-4c10-9d64-5728ec4977d4"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/soho-buffet-3.jpg?alt=media&token=aa09baf4-24e9-485d-97b0-25718983e5d4")),
                "status" to "active",
                "popularity_score" to 9.1
            ) + mapOf("price_range_detail" to highEndBuffetPrice, "opening_hoursl" to buffetHours),

            // === BUFFET 2: Manwah (Lẩu Đài Loan) ===
            mapOf(
                "name" to "Manwah Taiwanese Hotpot (Vincom)",
                "name_lower" to "manwah taiwanese hotpot vincom",
                "description" to "Buffet lẩu Đài Loan nổi tiếng, nước lẩu đa dạng, phục vụ đồ nhúng chất lượng.",
                "category_id" to "category_food",
                "subcategory" to "Hotpot Buffet",
                "coordinates" to mapOf("latitude" to 16.0820, "longitude" to 108.2340, "geohash" to "w7gxcb"),
                "address" to mapOf("formatted_address" to "Vincom Plaza Ngô Quyền, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/manwah-buffet.jpg?alt=media&token=97d348ff-2cd5-4795-ac4f-3a9cf745a6dc"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/manwah-buffet-2.jpg?alt=media&token=d22807a4-aa10-4e4a-b2a8-e551ea745257"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/manwah-buffet-3.jpg?alt=media&token=2952f580-829e-458a-8654-d20fae330739")),
                "status" to "active",
                "popularity_score" to 8.8
            ) + mapOf("price_range_detail" to midRangeBuffetPrice, "opening_hours" to defaultOpeningHours),

            // ===================================================================
            //                            NEW USER PLACES (7 ĐIỂM)
            // ===================================================================

            // === NEW 1: Phố đi bộ Bạch Đằng (Chill Spot, Free, 24h) ===
            mapOf(
                "name" to "Phố đi bộ Bạch Đằng",
                "name_lower" to "phố đi bộ bạch đằng",
                "description" to "Tuyến phố ven sông Hàn, là điểm tập trung ẩm thực, giải trí và ngắm cảnh buổi tối.",
                "category_id" to "category_chill",
                "subcategory" to "Walking Street",
                "coordinates" to mapOf("latitude" to 16.0691, "longitude" to 108.2259, "geohash" to "w7gxdx"),
                "address" to mapOf("formatted_address" to "Đường Bạch Đằng, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/pho-di-bo-bach-dang.jpg?alt=media&token=51ca108c-c2cb-4e21-892f-944932d2cec3"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/pho-di-bo-bach-dang-2.jpg?alt=media&token=05962753-7ddb-41ec-8ba5-1b12a0dd5b6a")),
                "status" to "active",
                "popularity_score" to 9.0
            ) + free24h,

            // === NEW 2: Sky36 Bar (Chill Spot, High End) ===
            mapOf(
                "name" to "Sky36 Bar (Novotel Hotel)",
                "name_lower" to "sky36 bar novotel hotel",
                "description" to "Quán bar trên tầng thượng (Roof top bar) của Novotel, view toàn cảnh thành phố và sông Hàn.",
                "category_id" to "category_hidden",
                "subcategory" to "Rooftop Bar",
                "coordinates" to mapOf("latitude" to 16.0710, "longitude" to 108.2250, "geohash" to "w7gxdz"),
                "address" to mapOf("formatted_address" to "36 Bạch Đằng, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/novotel.jpg?alt=media&token=51a3eef1-2c48-4d29-93bb-a53540dcc813"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/novotel-2.jpg?alt=media&token=94e08fcc-52dc-4b30-aca3-86dc0454a95a")),
                "status" to "active",
                "popularity_score" to 9.3
            ) + nightEntertainmentHours,

            // === NEW 3: Cộng Cà Phê (Coffee) ===
            mapOf(
                "name" to "Cộng Cà Phê",
                "name_lower" to "cộng cà phê",
                "description" to "Quán cà phê theo phong cách bao cấp, cổ điển và độc đáo.",
                "category_id" to "category_coffee",
                "subcategory" to "Café",
                "coordinates" to mapOf("latitude" to 16.0650, "longitude" to 108.2230, "geohash" to "w7gxdm"),
                "address" to mapOf("formatted_address" to "115 Bạch Đằng, Hải Châu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cong-ca-phe.jpg?alt=media&token=6a7eb0e7-5f28-4ba1-8d80-3e8c785354e3"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/cong-ca-phe-2.jpg?alt=media&token=7dc0c7ff-cf78-4a94-acf0-57d5df020004")),
                "status" to "active",
                "popularity_score" to 8.5
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === NEW 4: Fusion Maia Spa (Chill Spot, High Price) ===
            mapOf(
                "name" to "Fusion Maia Spa",
                "name_lower" to "fusion maia spa",
                "description" to "Khu nghỉ dưỡng và spa sang trọng, nổi tiếng với dịch vụ spa trọn gói hàng ngày.",
                "category_id" to "category_hidden",
                "subcategory" to "Spa/Wellness",
                "coordinates" to mapOf("latitude" to 16.0090, "longitude" to 108.2670, "geohash" to "w7grt0"),
                "address" to mapOf("formatted_address" to "Võ Nguyên Giáp, Ngũ Hành Sơn, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/Fusion%20Maia.jpg?alt=media&token=964df2df-b1b7-421f-96b7-3c4cd0067214"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/Fusion-Maia-Resort-Da-Nang-2.jpg?alt=media&token=d0057c1c-d758-4bda-8654-e607f68d6e50")),
                "status" to "active",
                "popularity_score" to 9.1
            ) + mapOf("price_range_detail" to mapOf("min" to 500000, "max" to 3000000, "currency" to "VND"), "opening_hours" to sightseeingHours["opening_hours"]),

            // === NEW 5: Bánh Xèo Quán Vân (Food) ===
            mapOf(
                "name" to "Bánh Xèo Quán Vân",
                "name_lower" to "bánh xèo quán vân",
                "description" to "Một trong những quán bánh xèo nổi tiếng khác tại Đà Nẵng, giòn tan đậm vị miền Trung.",
                "category_id" to "category_hidden",
                "subcategory" to "Street Food",
                "coordinates" to mapOf("latitude" to 16.0715, "longitude" to 108.2430, "geohash" to "w7gx7d"),
                "address" to mapOf("formatted_address" to "14 Tự Lực 1, Sơn Trà, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/banh-xeo-quan-van.jpg?alt=media&token=819bd1f2-36bf-46af-8e04-b18fb0440556"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/banh-xeo-quan-van-2.jpg?alt=media&token=99518896-c2ec-4688-9e17-3fddb2ac6df2")),
                "status" to "active",
                "popularity_score" to 8.4
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === NEW 6: Quán Ốc 74 (Food) ===
            mapOf(
                "name" to "Quán Ốc 74",
                "name_lower" to "quán ốc 74",
                "description" to "Quán ốc bình dân, đa dạng các món ốc và hải sản tươi ngon, giá cả phải chăng.",
                "category_id" to "category_hidden",
                "subcategory" to "Seafood",
                "coordinates" to mapOf("latitude" to 16.07027, "longitude" to 108.16582, "geohash" to "w7gxf2"),
                "address" to mapOf("formatted_address" to "Đường Hoàng Thị Loan, Liên Chiểu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/oc-74.jpg?alt=media&token=5da5371c-be6b-4bf0-8d0e-d01c8da42c0e"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/oc-74-2.jpg?alt=media&token=c598e9ee-d957-4202-a16b-d0356546ab65")),
                "status" to "active",
                "popularity_score" to 8.1
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours),

            // === NEW 7: Suối Khoáng Nóng Phước Nhơn (Nature/Wellness) ===
            mapOf(
                "name" to "Suối Khoáng Nóng Phước Nhơn",
                "name_lower" to "suối khoáng nóng phước nhơn",
                "description" to "Khu du lịch sinh thái và nghỉ dưỡng suối khoáng nóng tự nhiên, cách trung tâm không xa.",
                "category_id" to "category_nature",
                "subcategory" to "Hot Spring",
                "coordinates" to mapOf("latitude" to 15.9380, "longitude" to 108.1300, "geohash" to "w7gx1v"),
                "address" to mapOf("formatted_address" to "Xã Hòa Khương, Hòa Vang, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/suoi-khoang-nong-pn.jpg?alt=media&token=7c7f3696-17f7-43d7-87ab-5466bdcd51cc"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/suoi--khoang-nong-pn-2.jpg?alt=media&token=9d710006-ba6b-4de4-b8ae-4cf2aa0046e6")),
                "status" to "active",
                "popularity_score" to 7.9
            ) + sightseeingHours,
                (mapOf(
                "name" to "Bánh Canh Cá Nục Nhi ",
                "name_lower" to "bánh canh cá nục nhi",
                "description" to "Quán bánh canh cá nục tươi ngon, hương vị đậm đà, được xem là một bí mật ẩm thực địa phương.",
                "category_id" to "category_hidden", // Theo yêu cầu của bạn
                "subcategory" to "Street Food",
                "coordinates" to mapOf(
                    "latitude" to 16.07145,
                    "longitude" to 108.15005,
                    "geohash" to "w7gx8w" // Geohash gần đúng
                ),
                "address" to mapOf("formatted_address" to "1 Âu Cơ, Hoà Khánh Bắc, Liên Chiểu, Đà Nẵng"),
                "images" to listOf(
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/nuc-nhi.jpg?alt=media&token=9e4ba165-31ff-4000-b8b2-30badba754c4"),
                    mapOf("url" to "https://firebasestorage.googleapis.com/v0/b/hidden-da-nang.firebasestorage.app/o/nuc-nhi-2.jpg?alt=media&token=a4ba768e-c753-4104-8528-ac8dd843eb7c")),
                "status" to "active",
                "popularity_score" to 8.0 // Đánh giá cao hơn mức trung bình 7.x cho Hidden Gem
            ) + mapOf("price_range_detail" to defaultPriceRange, "opening_hours" to defaultOpeningHours)),
        )
    }
}