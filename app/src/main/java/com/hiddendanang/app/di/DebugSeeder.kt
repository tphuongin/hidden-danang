package com.hiddendanang.app.di // Hoặc package com.hiddendanang.app.utils

import android.util.Log
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
            Log.d("FirebaseCheck", "  Kết nối Firebase Firestore thành công.")
            true
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "  Kết nối Firebase thất bại: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    suspend fun seedAllPlaces(): Int {
        Log.i("Seeder", "Bắt đầu Seed Data cho Collection 'places'...")

        val batch = firestore.batch()

        val placesData = getMockPlacesData()

        if (placesData.isEmpty()) {
            Log.w("Seeder", "Không có dữ liệu mẫu để Seed.")

        }

        placesData.forEachIndexed { index, data ->
            val docRef = placesCollection.document()
            batch.set(docRef, data)
        }


        return try {
            batch.commit().await()
            Log.i("Seeder", " Seed Data thành công! Đã thêm ${placesData.size} địa điểm.")
        } catch (e: Exception) {
            Log.e("Seeder", " Lỗi khi Seed Data: ${e.message}")
        }
    }

    private fun getMockPlacesData(): List<Map<String, Any>> {
        return listOf(
            mapOf(
                "name" to "Bánh Xèo Bà Dưỡng",
                "name_lower" to "bánh xèo bà dưỡng",
                "description" to "Quán bánh xèo nổi tiếng Đà Nẵng, giòn rụm và đậm đà hương vị.",
                "category_id" to "category_food",
                "subcategory" to "Street Food",
                "coordinates" to mapOf(
                    "latitude" to 16.0596,
                    "longitude" to 108.2426,
                    "geohash" to "w7gx6y"
                ),
                "address" to mapOf("formatted_address" to "280 Hoàng Diệu, Hải Châu, Đà Nẵng"),
                // LƯU Ý: Link Google Drive (folders/file) sẽ không load được
                // trong AsyncImage. Bạn cần link ảnh trực tiếp (vd: .jpg).
                "images" to listOf(mapOf("url" to "https://drive.google.com/drive/folders/1zTK3pxtv7p24e9wHStAYf9q6xQgI5Q4y")),
                "status" to "active",
                "popularity_score" to 8.5
            ),
            //2
            mapOf(
                "name" to "Cầu Rồng",
                "name_lower" to "cầu rồng",
                "description" to "Biểu tượng của Đà Nẵng, phun lửa và nước vào cuối tuần.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge",
                "coordinates" to mapOf(
                    "latitude" to 16.0601,
                    "longitude" to 108.2255,
                    "geohash" to "w7gxdp"
                ),
                "address" to mapOf("formatted_address" to "Sông Hàn, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1-_r_1aLBHfupzMTE2pXQH_t6sCM-vJKj/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 9.2
            ),
            //3
            mapOf(
                "name" to "Ngũ Hành Sơn",
                "name_lower" to "ngũ hành sơn",
                "description" to "Quần thể 5 ngọn núi đá vôi với nhiều hang động và đền chùa cổ kính.",
                "category_id" to "category_nature",
                "subcategory" to "Mountain",
                "coordinates" to mapOf(
                    "latitude" to 15.9892,
                    "longitude" to 108.2831,
                    "geohash" to "w7grv8"
                ),
                "address" to mapOf("formatted_address" to "Hòa Hải, Ngũ Hành Sơn, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1_Jjol39qSU1vsZ_8vk9cDTDMXWOk53o0/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.9
            ),
            // === 4 ===
            mapOf(
                "name" to "Công Viên Châu Á (Asia Park)",
                "name_lower" to "công viên châu á",
                "description" to "Khu vui chơi giải trí lớn với vòng quay Sun Wheel và nhiều trò chơi hấp dẫn.",
                "category_id" to "category_entertainment",
                "subcategory" to "Theme Park",
                "coordinates" to mapOf(
                    "latitude" to 16.0474,
                    "longitude" to 108.2231,
                    "geohash" to "w7gxdj"
                ),
                "address" to mapOf("formatted_address" to "01 Phan Đăng Lưu, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1V42c3BZeOXyvWBz1cBD8G-9iO2vMJ48J/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 9.0
            ),
            // === 5 ===
            mapOf(
                "name" to "Cầu Tình Yêu",
                "name_lower" to "cầu tình yêu",
                "description" to "Cây cầu lãng mạn trên sông Hàn, nơi các cặp đôi treo ổ khóa tình yêu.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge",
                "coordinates" to mapOf(
                    "latitude" to 16.0640,
                    "longitude" to 108.2290,
                    "geohash" to "w7gxdr"
                ),
                "address" to mapOf("formatted_address" to "Trần Hưng Đạo, Sơn Trà, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1auWnnr_ZolKGKEhFB7xKNVBvh959YVtl/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.8
            ),
            // === 6 ===
            mapOf(
                "name" to "Bảo Tàng Điêu Khắc Chăm",
                "name_lower" to "bảo tàng điêu khắc chăm",
                "description" to "Nơi lưu giữ bộ sưu tập hiện vật Chăm Pa lớn nhất thế giới.",
                "category_id" to "category_culture",
                "subcategory" to "Museum",
                "coordinates" to mapOf(
                    "latitude" to 16.0625,
                    "longitude" to 108.2245,
                    "geohash" to "w7gxdq"
                ),
                "address" to mapOf("formatted_address" to "02 2 Tháng 9, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/12HyeERQdMj_-0LG30_9gc0ai8llbei-1/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.6
            ),
            // === 7 ===
            mapOf(
                "name" to "Café Memory Lounge",
                "name_lower" to "café memory lounge",
                "description" to "Quán cà phê độc đáo hình chiếc lá nằm trên sông Hàn, nổi tiếng với không gian sang trọng.",
                "category_id" to "category_food",
                "subcategory" to "Café",
                "coordinates" to mapOf(
                    "latitude" to 16.0671,
                    "longitude" to 108.2293,
                    "geohash" to "w7gxdx"
                ),
                "address" to mapOf("formatted_address" to "7 Bạch Đằng, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1-08sBFE7AahT-HJ07T8_chHYq19usr8u/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.2
            ),
            // === 8 ===
            mapOf(
                "name" to "Helio Center",
                "name_lower" to "helio center",
                "description" to "Tổ hợp vui chơi giải trí, ẩm thực đêm và chợ đêm nổi tiếng dành cho giới trẻ.",
                "category_id" to "category_entertainment",
                "subcategory" to "Entertainment Center",
                "coordinates" to mapOf(
                    "latitude" to 16.0470,
                    "longitude" to 108.2212,
                    "geohash" to "w7gxdh"
                ),
                "address" to mapOf("formatted_address" to "2/9, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1IBdjme0Q8mN0A22vfElSinrDRqob2x3h/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.5
            ),
            // === 9 ===
            mapOf(
                "name" to "Café 1975",
                "name_lower" to "café 1975",
                "description" to "Quán cà phê mang phong cách retro, gợi nhớ về Đà Nẵng xưa cũ.",
                "category_id" to "category_food",
                "subcategory" to "Café",
                "coordinates" to mapOf(
                    "latitude" to 16.0670,
                    "longitude" to 108.2208,
                    "geohash" to "w7gxdk"
                ),
                "address" to mapOf("formatted_address" to "45 Lê Lợi, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1T3ZgL6LCeJ0rM7s6X5c3yyeZmhROAxvK/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.1
            ),
            // === 10 ===
            mapOf(
                "name" to "Công Viên Biển Đông",
                "name_lower" to "công viên biển đông",
                "description" to "Nơi thường xuyên tổ chức lễ hội và có hàng ngàn chim bồ câu trắng bay lượn mỗi chiều.",
                "category_id" to "category_nature",
                "subcategory" to "Park",
                "coordinates" to mapOf(
                    "latitude" to 16.0690,
                    "longitude" to 108.2475,
                    "geohash" to "w7gx6z"
                ),
                "address" to mapOf("formatted_address" to "Phạm Văn Đồng, Sơn Trà, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1KjYTOGfm14gUbLhxB-3JMqK3jxKPsF6I/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.4
            ),
            // === 11 ===
            mapOf(
                "name" to "Hải Sản Bé Mặn",
                "name_lower" to "hải sản bé mặn",
                "description" to "Quán hải sản tươi ngon nổi tiếng, được nhiều du khách yêu thích.",
                "category_id" to "category_food",
                "subcategory" to "Seafood",
                "coordinates" to mapOf(
                    "latitude" to 16.0760,
                    "longitude" to 108.2501,
                    "geohash" to "w7gx7g"
                ),
                "address" to mapOf("formatted_address" to "11 Võ Nguyên Giáp, Sơn Trà, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/115tuWcLZ7Mz6Z5tP861BOa8FPkqBe7AM/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.9
            ),
            // === 12 ===
            mapOf(
                "name" to "Nhà Thờ Con Gà (Chính Tòa)",
                "name_lower" to "nhà thờ con gà",
                "description" to "Công trình kiến trúc Pháp cổ kính với biểu tượng con gà trên đỉnh tháp.",
                "category_id" to "category_culture",
                "subcategory" to "Church",
                "coordinates" to mapOf(
                    "latitude" to 16.0669,
                    "longitude" to 108.2225,
                    "geohash" to "w7gxdm"
                ),
                "address" to mapOf("formatted_address" to "156 Trần Phú, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1EfwG1grikqC4HLvCXmdb31gvskQw49Jh/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.7
            ),
            // === 13 ===
            mapOf(
                "name" to "Cầu Thuận Phước",
                "name_lower" to "cầu thuận phước",
                "description" to "Cầu dây võng dài nhất Việt Nam, nối liền trung tâm thành phố với bán đảo Sơn Trà.",
                "category_id" to "category_sightseeing",
                "subcategory" to "Bridge",
                "coordinates" to mapOf(
                    "latitude" to 16.0967,
                    "longitude" to 108.2358,
                    "geohash" to "w7gx98"
                ),
                "address" to mapOf("formatted_address" to "Thuận Phước, Hải Châu, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1UBzz1fAyl8D9yI6hNb83CLmyk53deDlh/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.5
            ),
            // === 14 ===
            mapOf(
                "name" to "Chùa Linh Ứng Bãi Bụt",
                "name_lower" to "chùa linh ứng bãi bụt",
                "description" to "Ngôi chùa linh thiêng với tượng Phật Quan Âm cao 67m nhìn ra biển.",
                "category_id" to "category_culture",
                "subcategory" to "Pagoda",
                "coordinates" to mapOf(
                    "latitude" to 16.1230,
                    "longitude" to 108.2821,
                    "geohash" to "w7gxk5"
                ),
                "address" to mapOf("formatted_address" to "Bán đảo Sơn Trà, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/12QyVhkh2tTGCItqeIprRYkqfiNqZdCLE/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 9.4
            ),
            // === 15 ===
            mapOf(
                "name" to "Chợ Đêm Sơn Trà",
                "name_lower" to "chợ đêm sơn trà",
                "description" to "Điểm đến sôi động vào buổi tối với nhiều gian hàng đồ ăn, quà lưu niệm và nhạc sống.",
                "category_id" to "category_entertainment",
                "subcategory" to "Night Market",
                "coordinates" to mapOf(
                    "latitude" to 16.0663,
                    "longitude" to 108.2307,
                    "geohash" to "w7gxds"
                ),
                "address" to mapOf("formatted_address" to "Mai Hắc Đế, Sơn Trà, Đà Nẵng"),
                "images" to listOf(mapOf("url" to "https://drive.google.com/file/d/1OogAjk2HFbcNiqLe3gwqSsaoBi7Kl14u/view?usp=drive_link")),
                "status" to "active",
                "popularity_score" to 8.6
            )
        )
    }
}