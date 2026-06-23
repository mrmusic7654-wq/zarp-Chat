package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: ChatViewModel, onBack: () -> Unit) {
    val currentAccessory by viewModel.accessory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        TopAppBar(
            title = { Text("Settings", color = GptTextPrimary) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GptTextPrimary)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                SettingsSection("Account")
                SettingsItem("Email", "user@example.com") {}
                SettingsItem("Subscription", "Free Plan") {}
                SettingsItem("Upgrade to Sion Pro", "Get early access to new features") {}
            }
            item {
                SettingsSection("App")
                SettingsItem("Theme", "System Default") {}
                SettingsItem("Data Controls", "") {}
                SettingsItem("Set Sion as Live Wallpaper", "Beta feature") {}
            }
            item {
                SettingsSection("Pet Accessories")
                val glassesText = if (currentAccessory == Accessory.GLASSES || currentAccessory == Accessory.BOTH) "Remove Glasses" else "Equip Glasses"
                val scarfText = if (currentAccessory == Accessory.SCARF || currentAccessory == Accessory.BOTH) "Remove Scarf" else "Equip Scarf"
                
                SettingsItem("Toggle Glasses", glassesText) {
                    when (currentAccessory) {
                        Accessory.NONE -> viewModel.setAccessory(Accessory.GLASSES)
                        Accessory.SCARF -> viewModel.setAccessory(Accessory.BOTH)
                        Accessory.GLASSES -> viewModel.setAccessory(Accessory.NONE)
                        Accessory.BOTH -> viewModel.setAccessory(Accessory.SCARF)
                    }
                }
                SettingsItem("Toggle Scarf", scarfText) {
                    when (currentAccessory) {
                        Accessory.NONE -> viewModel.setAccessory(Accessory.SCARF)
                        Accessory.GLASSES -> viewModel.setAccessory(Accessory.BOTH)
                        Accessory.SCARF -> viewModel.setAccessory(Accessory.NONE)
                        Accessory.BOTH -> viewModel.setAccessory(Accessory.GLASSES)
                    }
                }
            }
            item {
                SettingsSection("About")
                SettingsItem("Help Center", "") {}
                SettingsItem("Terms of Use", "") {}
                SettingsItem("Privacy Policy", "") {}
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Sign out",
                    color = Color.Red,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsSection(title: String) {
    Text(
        text = title,
        color = GptTextSecondary,
        fontSize = 14.sp,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(title, color = GptTextPrimary, fontSize = 16.sp)
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = GptTextSecondary, fontSize = 14.sp)
        }
    }
}
