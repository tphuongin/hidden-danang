package com.hiddendanang.app.ui.screen.home

import android.R
import android.R.attr.onClick
import android.content.res.Resources
import android.text.style.BackgroundColorSpan
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.hiddendanang.app.navigation.Screen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@Preview
@Composable
fun HomepageScreen() {
    val viewModel: PlaceViewModel = viewModel()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp) // khoảng cách giữa các mục
    ) {
        item {
            Spacer(modifier = Modifier.height(20.dp))
            HomePageTittle()
        }

        item { SearchBar() }

        item { CategoryRow() }

        item { PlacesSection(viewModel.topPlaces, "Top Places") }

        item { PlacesSection(viewModel.morePlaces, "Khám phá thêm") }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

data class Place(
    val name: String,
    val category: String,
    val imageUrl: String,
    val rating: Double
)

class PlaceViewModel : ViewModel() {
    // Danh sách top địa điểm
    val topPlaces = listOf(
        Place(
            "Bà Nà Hills",
            "Du lịch",
            "https://tse4.mm.bing.net/th/id/OIP.f1SL8OWZE0wxmbbbUZexrAHaE7?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3",
            4.8
        ),
        Place(
            "Cà Phê Cóc",
            "Cà phê",
            "https://images.unsplash.com/photo-1504674900247-0877df9cc836",
            4.6
        ),
        Place(
            "Bún Chả Cá 1297",
            "Ăn uống",
            "https://images.unsplash.com/photo-1540189549336-e6e99c3679fe",
            4.9
        )
    )

    // Danh sách địa điểm khám phá thêm
    val morePlaces = listOf(
        Place(
            "Cầu Rồng",
            "Tham quan",
            "https://dulich3mien.vn/wp-content/uploads/2021/12/cau-rong-da-nang-phat-sang-ve-dem.jpg",
            4.7
        ),
        Place(
            "Biển Mỹ Khê",
            "Du lịch biển",
            "https://tse1.mm.bing.net/th/id/OIP.Xl7CbOUWCVEdBJd3uHFK1wHaEx?cb=12&rs=1&pid=ImgDetMain&o=7&rm=3",
            4.9
        ),
        Place(
            "Chợ Cồn",
            "Ẩm thực",
            "https://danangbest.com/uploads/news/cho-con-da-nang-1.webp",
            4.5
        ),
        Place(
            "Sky36 Bar",
            "Giải trí",
            "https://www.therooftopguide.com/rooftop-bars-in-da-nang/Bilder/sky36-600-3.jpg",
            4.4
        )
    )
}

@Composable
fun HomePageTittle() {
    Text(
        modifier = Modifier.padding(5.dp),
        text = "Hidden Da Nang",
        style = TextStyle(
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.Black,
                    Color(0xFF66BB6A)
                )
            ),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        placeholder = { Text("Tìm kiếm địa điểm...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Tìm kiếm"
            )
        },
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(30.dp), // Bo tròn
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = Color(0xFFF5F7F8),
            focusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFF2196F3),
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
        )
    )
}

@Composable
fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        if (isSelected) Color(0xFF5AC1A7) else Color.White,
        label = "bgColorAnim"
    )

    val borderColor by animateColorAsState(
        if (isSelected) Color(0xFF5AC1A7) else Color(0xFFDDDDDD),
        label = "borderColorAnim"
    )

    val textColor by animateColorAsState(
        if (isSelected) Color.White else Color.Black,
        label = "textColorAnim"
    )

    val elevation by animateDpAsState(
        if (isSelected) 5.dp else 1.dp,
        label = "elevationAnim"
    )

    Button(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp)
            .scale(if (isSelected) 1.05f else 1f),
        shape = RoundedCornerShape(30.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CategoryRow() {
    val categories = listOf("Tất cả", "Ăn uống", "Cà phê", "Du lịch", "Góc chill", "Hidden Gems")

    var selectedIndex by remember { mutableStateOf(0) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        itemsIndexed(categories) { index, name ->
            CategoryButton(
                text = name,
                isSelected = selectedIndex == index,
                onClick = { selectedIndex = index }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaceCard(place: Place) {
    var isFavorite by remember { mutableStateOf(false) }
    var isPressed by remember { mutableStateOf(false) }
    // Animation cho độ nổi (elevation)
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 10.dp else 4.dp,
        animationSpec = tween(durationMillis = 150)
    )

    // Animation cho scale (phóng to nhẹ)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 1.03f else 1f,
        animationSpec = tween(durationMillis = 150)
    )
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        modifier = Modifier
            .width(220.dp)
            .padding(8.dp)
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease() // chờ khi thả tay ra
                        isPressed = false
                    },
                    onTap = {
                        //Process onclick
                    }
                )
            }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(140.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                // Ảnh load bằng Glide
                GlideImage(
                    model = place.imageUrl,
                    contentDescription = place.name,
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                )
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Yêu thích",
                        tint = if (isFavorite) Color.Red else Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                place.name, fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                place.category, style = MaterialTheme.typography.bodySmall, color = Color.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    "${place.rating}",
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun PlacesSection(places: List<Place>, placesTypeName: String) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = placesTypeName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow {
            items(places) { place ->
                PlaceCard(place)
            }
        }
    }
}
