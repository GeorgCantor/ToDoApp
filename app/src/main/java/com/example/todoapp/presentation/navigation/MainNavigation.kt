package com.example.todoapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todoapp.presentation.screens.NewsDetailScreen
import com.example.todoapp.presentation.screens.NewsListScreen
import com.example.todoapp.presentation.viewmodel.NewsViewModel

@Composable
fun MainNavigation(viewModel: NewsViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.NewsList.route
    ) {
        composable(NavRoutes.NewsList.route) {
            NewsListScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = "${NavRoutes.NewsDetail.route}/{${NavRoutes.NewsDetail.ARG_NEWS_ID}}",
            arguments = listOf(
                navArgument(NavRoutes.NewsDetail.ARG_NEWS_ID) {
                    type = NavType.IntType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            val newsId = backStackEntry.arguments?.getInt(NavRoutes.NewsDetail.ARG_NEWS_ID)
            NewsDetailScreen(
                newsId = newsId ?: 0,
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}