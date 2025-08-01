package com.example.todoapp.data.remote.api

import com.example.todoapp.BuildConfig
import com.example.todoapp.data.remote.dto.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("v2/top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY,
    ): NewsResponse
}
