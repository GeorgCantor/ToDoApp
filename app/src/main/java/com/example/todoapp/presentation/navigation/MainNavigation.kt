package com.example.todoapp.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.presentation.screens.MainScreen
import com.example.todoapp.presentation.screens.NewsDetailScreen
import com.example.todoapp.presentation.screens.SearchNewsScreen
import com.example.todoapp.presentation.screens.SplashScreen
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.DocumentsViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val newsViewModel: NewsViewModel = koinViewModel()
    val chatViewModel: ChatViewModel = koinViewModel()
    val documentsViewModel: DocumentsViewModel = koinViewModel()
    val isLoading by newsViewModel.isLoading

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Splash.route,
    ) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(
                isLoading = isLoading,
                onLoaded = {
                    navController.navigate(NavRoutes.NewsList.route) {
                        popUpTo(NavRoutes.Splash.route) { inclusive = true }
                    }
                },
            )
        }

        composable(NavRoutes.NewsList.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                documentsViewModel = documentsViewModel,
            )
        }

        composable(NavRoutes.BleScanScreen.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                documentsViewModel = documentsViewModel,
            )
        }

        composable(NavRoutes.Chat.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                documentsViewModel = documentsViewModel,
            )
        }

        composable(NavRoutes.Map.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                documentsViewModel = documentsViewModel,
            )
        }

        composable(NavRoutes.Documents.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                documentsViewModel = documentsViewModel,
            )
        }

        composable(
            route = NavRoutes.Search.route,
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300),
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300),
                ) + fadeOut(tween(300))
            },
        ) {
            SearchNewsScreen(navController = navController, viewModel = newsViewModel)
        }

        composable(
            route = "${NavRoutes.NewsDetail.route}/{${NavRoutes.NewsDetail.ARG_NEWS_ID}}",
            arguments =
                listOf(
                    navArgument(NavRoutes.NewsDetail.ARG_NEWS_ID) { type = NavType.IntType },
                ),
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(300),
                ) + fadeIn(tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(300),
                ) + fadeOut(tween(300))
            },
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getInt(NavRoutes.NewsDetail.ARG_NEWS_ID) ?: 0
            NewsDetailScreen(
                newsId = newsId,
                viewModel = newsViewModel,
                navController = navController,
            )
        }
    }
}
