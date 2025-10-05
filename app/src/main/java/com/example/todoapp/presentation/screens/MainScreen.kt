package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import com.example.todoapp.presentation.viewmodel.CalculatorViewModel
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NewsViewModel,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    calculatorViewModel: CalculatorViewModel,
) {
    val items =
        listOf(
            NavRoutes.NewsList,
            NavRoutes.Chat,
            NavRoutes.Calculator,
            NavRoutes.IPCScreen,
            NavRoutes.ContentProviderScreen,
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
                                NavRoutes.NewsList ->
                                    Icon(
                                        Icons.Filled.Home,
                                        contentDescription = "News",
                                    )

                                NavRoutes.Chat ->
                                    Icon(
                                        Icons.Filled.MailOutline,
                                        contentDescription = "Chat",
                                    )

                                NavRoutes.Calculator ->
                                    Icon(
                                        Icons.Filled.AddCircle,
                                        contentDescription = "Calculator",
                                    )

                                NavRoutes.IPCScreen ->
                                    Icon(
                                        Icons.Filled.List,
                                        contentDescription = "IPC",
                                    )

                                NavRoutes.ContentProviderScreen ->
                                    Icon(
                                        Icons.Filled.Build,
                                        contentDescription = "Provider",
                                    )

                                else -> Icon(Icons.Filled.Home, contentDescription = item.route)
                            }
                        },
                        label = {
                            Text(
                                item.route
                                    .takeWhile { it != '_' }
                                    .replaceFirstChar { it.uppercase() },
                            )
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
                    authViewModel = authViewModel,
                    modifier = Modifier.padding(innerPadding),
                )

            NavRoutes.Chat.route -> ChatScreen(chatViewModel)

            NavRoutes.Calculator.route ->
                CalculatorScreen(
                    navController = navController,
                    viewModel = calculatorViewModel,
                )

            NavRoutes.IPCScreen.route -> IPCScreen(navController = navController)

            NavRoutes.ContentProviderScreen.route -> ContentProviderScreen(navController = navController)
        }
    }
}
