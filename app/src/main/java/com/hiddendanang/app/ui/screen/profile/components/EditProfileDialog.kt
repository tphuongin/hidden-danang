package com.hiddendanang.app.ui.screen.profile.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import com.composables.icons.lucide.Lucide

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Camera
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.hiddendanang.app.data.model.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Composable
fun EditProfileDialog(
    user: User,
    onDismiss: () -> Unit,
    // Callback trả về: Name, Bio, PhotoUrl (mới hoặc cũ)
    onConfirm: (String, String, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // State cho các trường text
    var displayName by remember { mutableStateOf(user.display_name ?: "") }
    var bio by remember { mutableStateOf(user.bio ?: "") }

    // State cho ảnh
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    // Launcher để mở thư viện ảnh (Photo Picker)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    AlertDialog(
        onDismissRequest = { if (!isUploading) onDismiss() },
        title = { Text(text = "Chỉnh sửa hồ sơ") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally, // Căn giữa ảnh
                modifier = Modifier.fillMaxWidth()
            ) {
                // --- PHẦN CHỌN ẢNH ---
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray.copy(alpha = 0.1f))
                        .clickable {
                            // Mở thư viện ảnh chỉ chọn ảnh
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    // Ưu tiên hiển thị ảnh vừa chọn từ máy, nếu không có thì lấy ảnh cũ
                    val imageModel = selectedImageUri ?: user.photo_url

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Icon Camera mờ mờ đè lên để user biết là bấm được
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = "Change Avatar",
                            tint = Color.White
                        )
                    }

                    // Hiển thị loading xoay tròn ngay trên avatar khi đang upload
                    if (isUploading) {
                        CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Color.White)
                    }
                }

                Text(text = "Chạm vào ảnh để thay đổi", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                // --- PHẦN NHẬP TEXT ---
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Tên hiển thị") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Giới thiệu bản thân") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !isUploading,
                onClick = {
                    scope.launch {
                        isUploading = true
                        try {
                            var finalPhotoUrl = user.photo_url ?: ""

                            // Nếu người dùng có chọn ảnh mới -> Upload lên Firebase Storage
                            if (selectedImageUri != null) {
                                val storageRef = Firebase.storage.reference
                                // Tạo tên file unique hoặc theo user id
                                val imageRef = storageRef.child("avatars/${user.uid}_${System.currentTimeMillis()}.jpg")

                                // 1. Upload file
                                imageRef.putFile(selectedImageUri!!).await()

                                // 2. Lấy Download URL
                                finalPhotoUrl = imageRef.downloadUrl.await().toString()
                            }

                            // Trả dữ liệu về callback
                            onConfirm(displayName, bio, finalPhotoUrl)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(context, "Lỗi upload ảnh: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isUploading = false
                        }
                    }
                }
            ) {
                if (isUploading) {
                    Text("Đang lưu...")
                } else {
                    Text("Lưu")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUploading
            ) {
                Text("Hủy")
            }
        }
    )
}