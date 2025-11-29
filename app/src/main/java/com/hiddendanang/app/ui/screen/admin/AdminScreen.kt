package com.hiddendanang.app.ui.screen.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.composables.icons.lucide.*
import com.hiddendanang.app.data.model.Place
import com.hiddendanang.app.data.model.User
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.screen.auth.ErrorDialog
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavHostController,
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Users", "Places")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Updated to PrimaryTabRow to fix deprecation warning
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                when (selectedTab) {
                    0 -> UserList(
                        users = uiState.users,
                        onUpdateRole = viewModel::updateUserRole,
                        onDelete = viewModel::deleteUser
                    )
                    1 -> PlaceList(
                        places = uiState.places,
                        currentFilter = uiState.placeStatusFilter,
                        onUpdateStatus = viewModel::updatePlaceStatus,
                        onDelete = viewModel::deletePlace,
                        onFilter = viewModel::loadPlaces,
                        onItemClick = { placeId ->
                            // Điều hướng đến màn hình chi tiết
                            navController.navigate(Screen.DetailPlace.createRoute(placeId))
                        }
                    )
                }
            }
        }
    }

    uiState.error?.let {
        ErrorDialog(message = it, onDismiss = { viewModel.errorShown() })
    }
}

@Composable
fun UserList(
    users: List<User>,
    onUpdateRole: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(Dimens.PaddingMedium),
        verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
    ) {
        items(users) { user ->
            UserItem(user, onUpdateRole, onDelete)
        }
    }
}

@Composable
fun UserItem(
    user: User,
    onUpdateRole: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete User?") },
            text = { Text("Are you sure you want to delete ${user.display_name}?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(user.uid)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.PaddingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Lucide.User, null, modifier = Modifier.size(Dimens.IconMedium))
                Spacer(Modifier.width(Dimens.PaddingSmall))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.display_name, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall)
                    Text("Role: ${user.role}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Lucide.Trash2, null, tint = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(Dimens.PaddingSmall))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)) {
                if (user.role != "admin") {
                    Button(
                        onClick = { onUpdateRole(user.uid, "admin") },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) { Text("Make Admin", style = MaterialTheme.typography.labelSmall) }
                } else {
                    OutlinedButton(
                        onClick = { onUpdateRole(user.uid, "user") },
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) { Text("Demote", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}

@Composable
fun PlaceList(
    places: List<Place>,
    currentFilter: String?, // null = All, "pending", "active"
    onUpdateStatus: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onFilter: (String?) -> Unit,
    onItemClick: (String) -> Unit
) {
    Column {
        // --- Filter Row ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
            horizontalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            FilterChip(
                selected = currentFilter == null,
                onClick = { onFilter(null) },
                label = { Text("All") },
                leadingIcon = if (currentFilter == null) {
                    { Icon(Lucide.Check, null, Modifier.size(16.dp)) }
                } else null
            )
            FilterChip(
                selected = currentFilter == "pending",
                onClick = { onFilter("pending") },
                label = { Text("Pending") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFFC107).copy(alpha = 0.2f),
                    selectedLabelColor = Color(0xFFFFA000)
                ),
                leadingIcon = if (currentFilter == "pending") {
                    { Icon(Lucide.Check, null, Modifier.size(16.dp)) }
                } else null
            )
            FilterChip(
                selected = currentFilter == "active",
                onClick = { onFilter("active") },
                label = { Text("Active") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50).copy(alpha = 0.2f),
                    selectedLabelColor = Color(0xFF388E3C)
                ),
                leadingIcon = if (currentFilter == "active") {
                    { Icon(Lucide.Check, null, Modifier.size(16.dp)) }
                } else null
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall),
            verticalArrangement = Arrangement.spacedBy(Dimens.PaddingSmall)
        ) {
            items(places) { place ->
                PlaceAdminItem(
                    place = place,
                    onUpdateStatus = onUpdateStatus,
                    onDelete = onDelete,
                    onClick = { onItemClick(place.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceAdminItem(
    place: Place,
    onUpdateStatus: (String, String) -> Unit,
    onDelete: (String) -> Unit,
    onClick: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Place?") },
            text = { Text("Permanent delete '${place.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete(place.id)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        onClick = onClick, // Cho phép click vào toàn bộ Card để xem chi tiết
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.PaddingMedium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(place.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(place.address.district, style = MaterialTheme.typography.bodySmall)
                    
                    val (statusColor, statusText) = when(place.status) {
                        "active" -> Color(0xFF4CAF50) to "Active"
                        "pending" -> Color(0xFFFFC107) to "Pending Review"
                        "rejected" -> Color.Red to "Rejected"
                        else -> Color.Gray to place.status
                    }
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(Dimens.PaddingSmall))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (place.status == "pending") {
                    // Nút Từ chối (Xóa hoặc set rejected)
                    OutlinedButton(
                        onClick = { onUpdateStatus(place.id, "rejected") },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Lucide.X, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Reject")
                    }
                    
                    Spacer(Modifier.width(8.dp))
                    
                    // Nút Duyệt
                    Button(
                        onClick = { onUpdateStatus(place.id, "active") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        modifier = Modifier.height(36.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Icon(Lucide.Check, null, Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Approve")
                    }
                } else {
                    // Nếu đã active hoặc rejected thì cho phép xóa
                     IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Lucide.Trash2, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}