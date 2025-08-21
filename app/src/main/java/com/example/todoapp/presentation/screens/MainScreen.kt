package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NewsViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val items =
        listOf(
            NavRoutes.NewsList,
            NavRoutes.BleScanScreen,
            NavRoutes.Chat,
            NavRoutes.Map,
        )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = BottomAppBarDefaults.containerColor,
                tonalElevation = BottomAppBarDefaults.ContainerElevation,
            ) {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        icon = {
                            when (item) {
                                NavRoutes.NewsList -> Icon(Icons.Filled.Home, contentDescription = "News")
                                NavRoutes.BleScanScreen -> Icon(Icons.Filled.Share, contentDescription = "Bluetooth")
                                NavRoutes.Chat -> Icon(Icons.Filled.MailOutline, contentDescription = "Chat")
                                NavRoutes.Map -> Icon(Icons.Filled.LocationOn, contentDescription = "Map")
                                else -> Icon(Icons.Filled.Home, contentDescription = item.route)
                            }
                        },
                        label = {
                            Text(item.route.takeWhile { it != '_' }.replaceFirstChar { it.uppercase() })
                        },
                        selected = currentRoute == item.route,
                        onClick = {
                            selectedItem = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        when (currentRoute) {
            NavRoutes.NewsList.route ->
                NewsListScreen(
                    navController = navController,
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding),
                )
            NavRoutes.BleScanScreen.route -> BleScanScreen()
            NavRoutes.Chat.route -> ChatScreen(chatViewModel)
            NavRoutes.Map.route -> MapScreen()
        }
    }
}
