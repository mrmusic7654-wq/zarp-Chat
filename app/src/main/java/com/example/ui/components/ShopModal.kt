package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.ChatViewModel
import com.example.ui.theme.GptAccent
import com.example.ui.theme.GptBackground
import com.example.ui.theme.GptSidebar
import com.example.ui.theme.GptTextPrimary

@Composable
fun ShopModal(viewModel: ChatViewModel, onDismiss: () -> Unit) {
    val points by viewModel.points.collectAsState()
    val glassesUnlocked by viewModel.unlockedGlasses.collectAsState()
    val scarfUnlocked by viewModel.unlockedScarf.collectAsState()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GptSidebar),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sion Accessory Shop",
                    color = GptTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Points: \$points",
                    color = GptAccent,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Shop Items
                ShopItem(
                    name = "Cool Glasses",
                    cost = 50,
                    unlocked = glassesUnlocked,
                    points = points,
                    onUnlock = { viewModel.unlockGlasses(50) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ShopItem(
                    name = "Red Scarf",
                    cost = 100,
                    unlocked = scarfUnlocked,
                    points = points,
                    onUnlock = { viewModel.unlockScarf(100) }
                )

                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = GptBackground)
                ) {
                    Text("Close", color = GptTextPrimary)
                }
            }
        }
    }
}

@Composable
fun ShopItem(name: String, cost: Int, unlocked: Boolean, points: Int, onUnlock: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(GptBackground)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, color = GptTextPrimary, fontWeight = FontWeight.Bold)
            Text(if (unlocked) "Unlocked" else "Cost: \$cost pts", color = Color.Gray, fontSize = 12.sp)
        }
        if (!unlocked) {
            Button(
                onClick = onUnlock,
                enabled = points >= cost,
                colors = ButtonDefaults.buttonColors(
                    containerColor = GptAccent,
                    disabledContainerColor = Color.Gray
                )
            ) {
                Text("Unlock")
            }
        } else {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Unlocked",
                tint = GptAccent
            )
        }
    }
}
