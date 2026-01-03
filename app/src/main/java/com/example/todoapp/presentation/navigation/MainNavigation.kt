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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.todoapp.domain.model.AuthUiState
import com.example.todoapp.domain.model.WidgetIntentData
import com.example.todoapp.presentation.screens.AuthScreen
import com.example.todoapp.presentation.screens.BiometricAuthScreen
import com.example.todoapp.presentation.screens.CalculatorScreen
import com.example.todoapp.presentation.screens.ForgotPasswordScreen
import com.example.todoapp.presentation.screens.LoginScreen
import com.example.todoapp.presentation.screens.MainScreen
import com.example.todoapp.presentation.screens.MapScreen
import com.example.todoapp.presentation.screens.NewsCategoriesScreen
import com.example.todoapp.presentation.screens.NewsDetailScreen
import com.example.todoapp.presentation.screens.ProfileScreen
import com.example.todoapp.presentation.screens.SearchNewsScreen
import com.example.todoapp.presentation.screens.SignUpScreen
import com.example.todoapp.presentation.screens.SpaceXScreen
import com.example.todoapp.presentation.screens.SpaceXStatsScreen
import com.example.todoapp.presentation.screens.SplashScreen
import com.example.todoapp.presentation.screens.TicTacToeScreen
import com.example.todoapp.presentation.viewmodel.AuthViewModel
import com.example.todoapp.presentation.viewmodel.CalculatorViewModel
import com.example.todoapp.presentation.viewmodel.ChatViewModel
import com.example.todoapp.presentation.viewmodel.NewsViewModel
import com.example.todoapp.presentation.viewmodel.ProfileViewModel
import com.example.todoapp.presentation.viewmodel.SpaceXStatsViewModel
import com.example.todoapp.presentation.viewmodel.SpaceXViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavigation(
    widgetIntentData: WidgetIntentData? = null,
    onIntentProcessed: () -> Unit = {},
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = koinViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val isBiometricAvailable = remember { authViewModel.isBiometricAvailable() }
    val newsViewModel: NewsViewModel = koinViewModel()
    val chatViewModel: ChatViewModel = koinViewModel()
    val calculatorViewModel: CalculatorViewModel = koinViewModel()
    val profileViewModel: ProfileViewModel = koinViewModel()
    val spaceXViewModel: SpaceXViewModel = koinViewModel()
    val spaceXStatsViewModel: SpaceXStatsViewModel = koinViewModel()
    val newsPagingItems = newsViewModel.news.collectAsLazyPagingItems()
    val isLoading =
        remember(newsPagingItems.loadState) {
            newsPagingItems.loadState.refresh is LoadState.Loading
        }

    val uiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(widgetIntentData) {
        widgetIntentData?.let { intentData ->
            delay(200)
            navController.navigate(intentData.targetScreen)
            onIntentProcessed()
        }
    }

    LaunchedEffect(uiState, currentRoute) {
        if (uiState is AuthUiState.Authenticated) {
            val authScreens =
                setOf(
                    NavRoutes.Auth.route,
                    NavRoutes.Login.route,
                    NavRoutes.SignUp.route,
                    NavRoutes.ForgotPassword.route,
                )
            if (authScreens.contains(currentRoute)) {
                navController.navigate(NavRoutes.Splash.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

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
        startDestination =
            if (isBiometricAvailable) {
                NavRoutes.BiometricAuth.route
            } else if (isAuthenticated) {
                NavRoutes.Splash.route
            } else {
                NavRoutes.Auth.route
            },
    ) {
        composable(NavRoutes.BiometricAuth.route) {
            BiometricAuthScreen(
                navController = navController,
                authViewModel = authViewModel,
            )
        }

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
                profileViewModel = profileViewModel,
            )
        }

        composable(NavRoutes.Chat.route) {
            MainScreen(
                navController = navController,
                viewModel = newsViewModel,
                chatViewModel = chatViewModel,
                authViewModel = authViewModel,
                calculatorViewModel = calculatorViewModel,
                profileViewModel = profileViewModel,
            )
        }

        composable(NavRoutes.Calculator.route) {
            CalculatorScreen(navController = navController, viewModel = calculatorViewModel)
        }

        composable(NavRoutes.Profile.route) {
            ProfileScreen(navController = navController, viewModel = profileViewModel)
        }

        composable(NavRoutes.NewsCategories.route) {
            NewsCategoriesScreen(navController = navController, viewModel = newsViewModel)
        }

        composable(NavRoutes.Map.route) { MapScreen() }

        composable(NavRoutes.TicTacToe.route) { TicTacToeScreen() }

        composable(NavRoutes.SpaceX.route) { SpaceXScreen(navController = navController, viewModel = spaceXViewModel) }

        composable(NavRoutes.SpaceXStats.route) { SpaceXStatsScreen(spaceXStatsViewModel) }

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
