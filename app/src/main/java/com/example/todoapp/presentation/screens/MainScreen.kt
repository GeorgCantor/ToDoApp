package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.todoapp.R
import com.example.todoapp.presentation.navigation.NavRoutes
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import com.example.todoapp.presentation.viewmodel.PlayerViewModel
import com.example.todoapp.presentation.viewmodel.ProfileViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NewsViewModel,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    playerViewModel: PlayerViewModel,
    profileViewModel: ProfileViewModel,
) {
    val items =
        listOf(
            NavRoutes.Player,
            NavRoutes.NewsList,
            NavRoutes.Chat,
            NavRoutes.Profile,
            NavRoutes.Cart,
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
                                NavRoutes.Player ->
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = stringResource(R.string.player),
                                    )

                                NavRoutes.NewsList ->
                                    Icon(
                                        Icons.Filled.List,
                                        contentDescription = stringResource(R.string.news),
                                    )

                                NavRoutes.Chat ->
                                    Icon(
                                        Icons.Filled.MailOutline,
                                        contentDescription = stringResource(R.string.chat),
                                    )

                                NavRoutes.Profile ->
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = stringResource(R.string.profile),
                                    )

                                NavRoutes.Cart ->
                                    Icon(
                                        Icons.Filled.Build,
                                        contentDescription = stringResource(R.string.cart),
                                    )

                                else -> Icon(Icons.Filled.PlayArrow, contentDescription = item.route)
                            }
                        },
                        label = {
                            Text(
                                when (item) {
                                    NavRoutes.Player -> stringResource(R.string.player)
                                    NavRoutes.NewsList -> stringResource(R.string.news)
                                    NavRoutes.Chat -> stringResource(R.string.chat)
                                    NavRoutes.Profile -> stringResource(R.string.profile)
                                    NavRoutes.Cart -> stringResource(R.string.cart)
                                    else -> item.route
                                },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
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
            NavRoutes.Player.route -> PlayerScreen(playerViewModel)

            NavRoutes.NewsList.route ->
                NewsListScreen(
                    navController = navController,
                    viewModel = viewModel,
                    authViewModel = authViewModel,
                    modifier = Modifier.padding(innerPadding),
                )

            NavRoutes.Chat.route -> ChatScreen(chatViewModel)

            NavRoutes.Profile.route -> ProfileScreen(navController = navController, viewModel = profileViewModel)

            NavRoutes.NewsCategories.route -> NewsCategoriesScreen(navController = navController, viewModel = viewModel)
        }
    }
}
