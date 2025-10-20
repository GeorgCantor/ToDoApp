package com.example.todoapp.presentation.navigation

sealed class NavRoutes(
    val route: String,
) {
    object Splash : NavRoutes("splash_screen")

    object NewsList : NavRoutes("news_list")

    object NewsCategories : NavRoutes("news_categories")

    object Search : NavRoutes("search_news")

    object NewsDetail : NavRoutes("news_detail") {
        const val ARG_NEWS_ID = "news_id"

        fun createRoute(newsId: Int?) = "news_detail/$newsId"
    }

    object BleScanScreen : NavRoutes("bluetooth")

    object Chat : NavRoutes("chat")

    object Map : NavRoutes("map_screen")

    object Documents : NavRoutes("documents_screen")

    object Calculator : NavRoutes("calculator_screen")

    object IPCScreen : NavRoutes("ipc_screen")

    object ContentProviderScreen : NavRoutes("content_provider_screen")

    object Auth : NavRoutes("auth_screen")

    object Login : NavRoutes("login_screen")

    object SignUp : NavRoutes("signup_screen")

    object ForgotPassword : NavRoutes("forgot_password_screen")

    object BiometricAuth : NavRoutes("biometric_auth")
}
