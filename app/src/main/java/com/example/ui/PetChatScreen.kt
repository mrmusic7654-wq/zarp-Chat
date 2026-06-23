package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RobotPet
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PetChatScreen(
    viewModel: ChatViewModel,
    onBack: () -> Unit
) {
    val messages by viewModel.petMessages.collectAsState()
    val isTyping by viewModel.isTyping.collectAsState()
    val petState by viewModel.petState.collectAsState()
    val battery by viewModel.batteryLevel.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        TopAppBar(
            title = { ModelSelector() },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GptTextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Large Pet Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f)
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            RobotPet(
                state = petState,
                battery = battery,
                accessory = viewModel.accessory.collectAsState().value,
                modifier = Modifier.fillMaxSize(0.8f),
                onDoubleTap = { /* Do nothing here */ },
                onStateChangeRequest = { viewModel.setPetState(it) }
            )
        }

        // Chat List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(messages) { message ->
                PetMessageBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (isTyping) {
                item {
                    Text("Sion is typing...", color = GptTextSecondary, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }

        BottomInputBar(onSend = { viewModel.sendMessage(it, isPetChat = true) })
    }
}

@Composable
fun PetMessageBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.User
    val alignment = if (isUser) Alignment.End else Alignment.Start
    val bgColor = if (isUser) GptAccent else GptAssistantBubble
    
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(bgColor)
                .padding(12.dp)
                .widthIn(max = 280.dp)
        ) {
            Text(message.content, color = Color.White, fontSize = 16.sp)
        }
    }
}
