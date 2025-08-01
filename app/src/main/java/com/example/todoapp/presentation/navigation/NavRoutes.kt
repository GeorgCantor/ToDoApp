package com.example.todoapp.presentation.navigation

sealed class NavRoutes(
    val route: String,
) {
    object Splash : NavRoutes("splash_screen")

    object NewsList : NavRoutes("news_list")

    object Search : NavRoutes("search_news")

    object NewsDetail : NavRoutes("news_detail") {
        const val ARG_NEWS_ID = "news_id"

        fun createRoute(newsId: Int?) = "news_detail/$newsId"
    }

    object BleScanScreen : NavRoutes("bluetooth")

    object Chat : NavRoutes("chat")

    object Map : NavRoutes("map_screen")
}
