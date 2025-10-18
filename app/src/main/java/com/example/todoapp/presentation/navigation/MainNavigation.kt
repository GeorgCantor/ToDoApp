package com.example.todoapp.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.presentation.screens.AuthScreen
import com.example.todoapp.presentation.screens.CalculatorScreen
import com.example.todoapp.presentation.screens.ForgotPasswordScreen
import com.example.todoapp.presentation.screens.IPCScreen
import com.example.todoapp.presentation.screens.LoginScreen
import com.example.todoapp.presentation.screens.MainScreen
import com.example.todoapp.presentation.screens.NewsCategoriesScreen
import com.example.todoapp.presentation.screens.NewsDetailScreen
import com.example.todoapp.presentation.screens.SearchNewsScreen
import com.example.todoapp.presentation.screens.SignUpScreen
import com.example.todoapp.presentation.screens.SplashScreen
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import com.example.todoapp.presentation.viewmodel.CalculatorViewModel
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val newsViewModel: NewsViewModel = koinViewModel()
    val chatViewModel: ChatViewModel = koinViewModel()
    val calculatorViewModel: CalculatorViewModel = koinViewModel()
    val newsPagingItems = newsViewModel.news.collectAsLazyPagingItems()
    val isLoading =
        remember(newsPagingItems.loadState) {
            newsPagingItems.loadState.refresh is LoadState.Loading
        }

    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    if (uiState is AuthUiState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) NavRoutes.Splash.route else NavRoutes.Auth.route,
    ) {
        composable(NavRoutes.Auth.route) {
            AuthScreen(navController = navController)
        }

        composable(NavRoutes.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onSignUpClick = { navController.navigate(NavRoutes.SignUp.route) },
                onForgotPasswordClick = { navController.navigate(NavRoutes.ForgotPassword.route) },
            )
        }

        composable(NavRoutes.SignUp.route) {
            SignUpScreen(
                authViewModel = authViewModel,
                onLoginClick = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(
                authViewModel = authViewModel,
                onBackClick = { navController.popBackStack() },
            )
        }

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
                authViewModel = authViewModel,
                calculatorViewModel = calculatorViewModel,
            )
        }

        composable(NavRoutes.Chat.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                authViewModel = authViewModel,
                calculatorViewModel = calculatorViewModel,
            )
        }

        composable(NavRoutes.Calculator.route) {
            CalculatorScreen(navController = navController, viewModel = calculatorViewModel)
        }

        composable(NavRoutes.IPCScreen.route) {
            IPCScreen(navController = navController)
        }

        composable(NavRoutes.NewsCategories.route) {
            NewsCategoriesScreen(navController = navController, viewModel = newsViewModel)
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
