package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SideDrawer(
    onNewChat: () -> Unit,
    onHistoryItemClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    onClearConversations: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(GptSidebar)
            .padding(12.dp)
    ) {
        // New Chat Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onNewChat() }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = "New Chat", tint = GptTextPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Text("New chat", color = GptTextPrimary, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(5) { index ->
                DrawerItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    text = "Previous calculation ${index + 1}",
                    onClick = { onHistoryItemClick("Item $index") }
                )
            }
        }

        Divider(color = GptDivider, thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Clear conversations
        DrawerItem(
            icon = Icons.Default.DeleteOutline,
            text = "Clear conversations",
            onClick = onClearConversations
        )

        // Upgrade to Plus
        DrawerItem(
            icon = Icons.Default.PersonOutline,
            text = "Upgrade to Plus",
            onClick = onUpgradeClick
        )

        // Settings
        DrawerItem(
            icon = Icons.Default.Settings,
            text = "Settings",
            onClick = onSettingsClick
        )
    }
}

@Composable
fun DrawerItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = text, tint = GptTextPrimary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, color = GptTextPrimary, fontSize = 14.sp)
    }
}
