package com.hiddendanang.app.ui.screen.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Star
import com.hiddendanang.app.R // Đảm bảo bạn có file strings.xml
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.ui.theme.HiddenDaNangTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewForm(
    initialRating: Int = 0, // Giá trị rating ban đầu (khi chỉnh sửa)
    initialComment: String = "", // Giá trị comment ban đầu (khi chỉnh sửa)
    onDismiss: () -> Unit = {}, // Khi người dùng đóng form
    onSubmit: (rating: Int, comment: String) -> Unit // Khi người dùng gửi đánh giá
) {
    var rating by remember { mutableIntStateOf(initialRating) }
    var comment by remember { mutableStateOf(initialComment) }
    var showDialog by remember { mutableStateOf(true) } // Để kiểm soát hiển thị Dialog

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                onDismiss()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.PaddingSmall),
                shape = RoundedCornerShape(Dimens.PaddingXLarge),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(Dimens.SpaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium)
                ) {
                    // Tiêu đề
                    Text(
                        text = if (initialRating == 0) stringResource(R.string.review_form_title_new) else stringResource(R.string.review_form_title_edit),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                    // Rating Stars
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            val starRating = index + 1
                            Icon(
                                imageVector = Lucide.Star,
                                contentDescription = stringResource(R.string.review_form_rating_star_description, starRating),
                                tint = if (starRating <= rating) Color.Yellow else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .size(Dimens.IconLarge)
                                    .clickable { rating = starRating }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                    // Comment Input
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text(stringResource(R.string.review_form_comment_label)) },
                        placeholder = { Text(stringResource(R.string.review_form_comment_placeholder)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp), // Chiều cao tối thiểu
                        maxLines = 5,
                        shape = RoundedCornerShape(Dimens.PaddingMedium)
                    )

                    Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

                    // Buttons (Submit / Cancel)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        OutlinedButton(
                            onClick = {
                                showDialog = false
                                onDismiss()
                            },
                            modifier = Modifier.weight(1f).padding(end = Dimens.PaddingSmall)
                        ) {
                            Text(stringResource(R.string.review_form_button_cancel))
                        }
                        Button(
                            onClick = {
                                if (rating > 0 && comment.isNotBlank()) {
                                    showDialog = false
                                    onSubmit(rating, comment)
                                }
                            },
                            enabled = rating > 0 && comment.isNotBlank(), // Enable khi có rating và comment
                            modifier = Modifier.weight(1f).padding(start = Dimens.PaddingSmall)
                        ) {
                            Text(stringResource(R.string.review_form_button_submit))
                        }
                    }
                }
            }
        }
    }
}

// --- Preview ---
@Preview(showBackground = true)
@Composable
fun ReviewFormNewPreview() {
    HiddenDaNangTheme {
        ReviewForm(
            initialRating = 0,
            initialComment = "",
            onDismiss = {},
            onSubmit = { rating, comment ->
                println("New Review: Rating $rating, Comment: $comment")
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewFormEditPreview() {
    HiddenDaNangTheme {
        ReviewForm(
            initialRating = 4,
            initialComment = "Tôi rất thích địa điểm này, không gian đẹp và đồ uống ngon!",
            onDismiss = {},
            onSubmit = { rating, comment ->
                println("Edit Review: Rating $rating, Comment: $comment")
            }
        )
    }
}