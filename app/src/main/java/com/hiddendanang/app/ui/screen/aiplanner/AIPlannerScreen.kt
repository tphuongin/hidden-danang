package com.hiddendanang.app.ui.screen.aiplanner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.composables.icons.lucide.*
import com.hiddendanang.app.data.model.ChatMessage
import com.hiddendanang.app.navigation.Screen
import com.hiddendanang.app.ui.theme.Dimens
import com.hiddendanang.app.viewmodel.AIPlannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIPlannerScreen(
    navController: NavHostController,
    viewModel: AIPlannerViewModel = viewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    var inputText by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AI Travel Planner", fontWeight = FontWeight.Bold)
                        Text(
                            "Powered by Gemini",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Lucide.ArrowLeft, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearHistory() }) {
                        Icon(Lucide.Trash2, "Clear Chat", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.PaddingMedium),
                verticalArrangement = Arrangement.spacedBy(Dimens.PaddingMedium),
                contentPadding = PaddingValues(vertical = Dimens.PaddingMedium)
            ) {
                if (messages.isEmpty()) {
                    item { WelcomeCard() }
                }
                items(messages) { message ->
                    ChatBubble(message, navController)
                }
                if (isLoading) {
                    item { LoadingBubble() }
                }
            }

            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(Dimens.PaddingMedium)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Plan a trip for me...") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = Dimens.PaddingSmall),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                    focusManager.clearFocus()
                                }
                            }
                        )
                    )
                    
                    FilledIconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                                focusManager.clearFocus()
                            }
                        },
                        enabled = !isLoading && inputText.isNotBlank(),
                        modifier = Modifier.size(50.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Lucide.SendHorizontal, "Send")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.padding(Dimens.PaddingLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Lucide.Sparkles,
                null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(Modifier.width(Dimens.PaddingMedium))
            Column {
                Text(
                    "Hello! I'm your Danang AI Guide.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ask me to plan a trip, find hidden gems, or suggest food spots!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    navController: NavHostController
) {
    val isUser = message.role == "user"
    val bubbleColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    val shape = if (isUser) 
        RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp) 
    else 
        RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Surface(
            color = bubbleColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (isUser) {
                    Text(
                        text = message.content,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    FormattedChatText(
                        content = message.content,
                        textColor = textColor,
                        onPlaceClick = { placeId ->
                            navController.navigate(Screen.DetailPlace.createRoute(placeId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FormattedChatText(
    content: String,
    textColor: Color,
    onPlaceClick: (String) -> Unit
) {
    val regex = Regex("\\[(.*?)\\|(.*?)\\]")
    
    val annotatedString = buildAnnotatedString {
        var lastIndex = 0
        
        regex.findAll(content).forEach { matchResult ->
            append(content.substring(lastIndex, matchResult.range.first))
            
            val (id, name) = matchResult.destructured
            
            pushStringAnnotation(tag = "PLACE_ID", annotation = id)
            pushStyle(
                SpanStyle(
                    color = Color(0xFF007AFF),
                    fontWeight = FontWeight.Bold,
                    textDecoration = TextDecoration.Underline
                )
            )
            append(name)
            pop()
            pop()
            
            lastIndex = matchResult.range.last + 1
        }
        
        append(content.substring(lastIndex))
    }

    ClickableText(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(color = textColor),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "PLACE_ID", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onPlaceClick(annotation.item)
                }
        }
    )
}

@Composable
fun LoadingBubble() {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
            modifier = Modifier.width(80.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("...", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}