package com.example.todoapp.presentation.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
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
import com.example.todoapp.presentation.viewmodel.CalculatorViewModel
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import com.example.todoapp.presentation.viewmodel.ProfileViewModel

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: NewsViewModel,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel,
    calculatorViewModel: CalculatorViewModel,
    profileViewModel: ProfileViewModel,
) {
    val items =
        listOf(
            NavRoutes.NewsList,
            NavRoutes.Chat,
            NavRoutes.Calculator,
            NavRoutes.Profile,
            NavRoutes.NewsCategories,
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
                                        contentDescription = stringResource(R.string.news),
                                    )

                                NavRoutes.Chat ->
                                    Icon(
                                        Icons.Filled.MailOutline,
                                        contentDescription = stringResource(R.string.chat),
                                    )

                                NavRoutes.Calculator ->
                                    Icon(
                                        Icons.Filled.AddCircle,
                                        contentDescription = stringResource(R.string.calculator),
                                    )

                                NavRoutes.Profile ->
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = stringResource(R.string.profile),
                                    )

                                NavRoutes.NewsCategories ->
                                    Icon(
                                        Icons.Filled.Build,
                                        contentDescription = stringResource(R.string.categories),
                                    )

                                else -> Icon(Icons.Filled.Home, contentDescription = item.route)
                            }
                        },
                        label = {
                            Text(
                                when (item) {
                                    NavRoutes.NewsList -> stringResource(R.string.news)
                                    NavRoutes.Chat -> stringResource(R.string.chat)
                                    NavRoutes.Calculator -> stringResource(R.string.calculator)
                                    NavRoutes.Profile -> stringResource(R.string.profile)
                                    NavRoutes.NewsCategories -> stringResource(R.string.categories)
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

            NavRoutes.Profile.route -> ProfileScreen(navController = navController, viewModel = profileViewModel)

            NavRoutes.NewsCategories.route -> NewsCategoriesScreen(navController = navController, viewModel = viewModel)
        }
    }
}
