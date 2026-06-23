package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.ArrowDropDown
import com.example.ui.components.ShopModal
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ThumbDownAlt
import androidx.compose.material.icons.filled.ThumbUpAlt
import androidx.compose.material.icons.outlined.ThumbDownAlt
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RobotPet
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onMenuClick: () -> Unit,
    onDoubleTapPet: () -> Unit
) {
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val petState by viewModel.petState.collectAsState()
    val battery by viewModel.batteryLevel.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var showShop by remember { mutableStateOf(false) }

    if (showShop) {
        ShopModal(viewModel = viewModel, onDismiss = { showShop = false })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                ModelSelector()
            },
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = GptTextPrimary)
                }
            },
            actions = {
                IconButton(onClick = { showShop = true }) {
                    Icon(Icons.Default.Storefront, contentDescription = "Shop", tint = GptTextPrimary)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More", tint = GptTextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                navigationIconContentColor = GptTextPrimary,
                actionIconContentColor = GptTextPrimary
            )
        )
        Divider(color = GptDivider.copy(alpha = 0.5f), thickness = 1.dp)

        // Robot Pet area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            RobotPet(
                state = petState,
                battery = battery,
                accessory = viewModel.accessory.collectAsState().value,
                modifier = Modifier.size(80.dp),
                onDoubleTap = onDoubleTapPet,
                onStateChangeRequest = { viewModel.setPetState(it) }
            )
        }
        Divider(color = GptDivider.copy(alpha = 0.5f), thickness = 1.dp)

        // Chat list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(messages) { message ->
                ChatMessageItem(message = message)
            }
            if (isTyping) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Input Bar
        BottomInputBar(onSend = { viewModel.sendMessage(it) })
    }
}

@Composable
fun ModelSelector() {
    var expanded by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf("Gemini 1.5 Pro") }
    val models = listOf("Gemini 1.5 Pro", "Gemini 1.5 Flash", "Gemini 1.0 Pro", "Gemini Ultra")

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(selectedModel, color = GptTextPrimary, fontSize = 18.sp)
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Model", tint = GptTextSecondary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(GptSidebar)
        ) {
            models.forEach { model ->
                DropdownMenuItem(
                    text = { Text(model, color = GptTextPrimary) },
                    onClick = {
                        selectedModel = model
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isUser = message.role == MessageRole.User
    val contentColor = GptTextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (isUser) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 0.dp, bottomStart = 16.dp, bottomEnd = 16.dp))
                        .background(GptUserBubble)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(text = message.content, color = contentColor, fontSize = 15.sp, lineHeight = 24.sp)
                }
            } else {
                // Assistant Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(GptSystemText),
                    contentAlignment = Alignment.Center
                ) {
                    Text("S", color = Color.White, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = message.content, color = contentColor, fontSize = 15.sp, lineHeight = 24.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = GptTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate", tint = GptTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Outlined.ThumbUpAlt, contentDescription = "Good response", tint = GptTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Outlined.ThumbDownAlt, contentDescription = "Bad response", tint = GptTextSecondary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(GptSystemText),
                contentAlignment = Alignment.Center
            ) {
                Text("S", color = Color.White, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GptTextSecondary))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GptTextSecondary))
                Spacer(modifier = Modifier.width(4.dp))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(GptTextSecondary))
            }
        }
    }
}

@Composable
fun BottomInputBar(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    
    // Modes
    var webSearchEnabled by remember { mutableStateOf(false) }
    var agentEnabled by remember { mutableStateOf(false) }
    var translateEnabled by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Mode Toggles
        Row(
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ModeChip(
                icon = Icons.Default.Public,
                label = "Web",
                selected = webSearchEnabled,
                onClick = { webSearchEnabled = !webSearchEnabled }
            )
            ModeChip(
                icon = Icons.Default.AutoAwesome,
                label = "Agent",
                selected = agentEnabled,
                onClick = { agentEnabled = !agentEnabled }
            )
            ModeChip(
                icon = Icons.Default.Language,
                label = "Translate",
                selected = translateEnabled,
                onClick = { translateEnabled = !translateEnabled }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(50))
                .background(GptInputBackground)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AttachFile,
                contentDescription = "Upload File",
                tint = GptTextSecondary,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF565869))
                    .padding(4.dp)
                    .clickable { /* Handle upload */ }
            )
            Spacer(modifier = Modifier.width(12.dp))
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp),
                textStyle = TextStyle(color = GptTextPrimary, fontSize = 16.sp),
                cursorBrush = SolidColor(GptAccent),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text("Message", color = GptTextSecondary)
                    }
                    innerTextField()
                }
            )
            Spacer(modifier = Modifier.width(12.dp))
            
            val icon = if (text.isNotBlank()) Icons.Default.ArrowUpward else Icons.Default.Mic
            val iconBg = if (text.isNotBlank()) GptAccent else Color.Transparent
            val iconTint = if (text.isNotBlank()) Color.White else GptTextSecondary
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg)
                    .clickable {
                        if (text.isNotBlank()) {
                            onSend(text)
                            text = ""
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = "Send/Mic", tint = iconTint)
            }
        }
    }
}

@Composable
fun ModeChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (selected) GptAccent.copy(alpha = 0.2f) else Color.Transparent
    val contentColor = if (selected) GptAccent else GptTextSecondary
    val borderColor = if (selected) GptAccent.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = contentColor, modifier = Modifier.size(16.dp))
        Text(label, color = contentColor, fontSize = 12.sp)
    }
}
