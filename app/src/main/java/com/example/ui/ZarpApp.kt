package com.example.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.data.AppDatabase
import com.example.data.MessageRepository
import com.example.data.UserPreferences
import com.example.ui.components.SideDrawer
import com.example.ui.theme.GptSidebar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZarpApp() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = MessageRepository(database.messageDao())
    val userPrefs = UserPreferences(context)
    val factory = ChatViewModelFactory(repository, userPrefs)

    val navController = rememberNavController()
    val viewModel: ChatViewModel = viewModel(factory = factory)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.app_background_1782230407537),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Dark overlay for readability
        Box(modifier = Modifier.fillMaxSize().background(Color(0xD9202123)))

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = GptSidebar,
                modifier = Modifier.padding(end = 64.dp)
            ) {
                SideDrawer(
                    onNewChat = {
                        viewModel.clearHistory()
                        scope.launch { drawerState.close() }
                    },
                    onHistoryItemClick = {
                        scope.launch { drawerState.close() }
                    },
                    onSettingsClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate("settings")
                    },
                    onClearConversations = {
                        viewModel.clearHistory()
                        scope.launch { drawerState.close() }
                    },
                    onUpgradeClick = {}
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "chat") {
            composable("chat") {
                ChatScreen(
                    viewModel = viewModel,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onDoubleTapPet = {
                        navController.navigate("pet_chat")
                    }
                )
            }
            composable("pet_chat") {
                PetChatScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
}
