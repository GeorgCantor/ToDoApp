package com.example.todoapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.todoapp.data.cache.NewsCache
import com.example.todoapp.data.paging.NewsPagingSource
import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class NewsRepositoryImpl(
    private val apiService: NewsApiService,
) : NewsRepository {
    override fun getNewsStream(category: String): Flow<PagingData<NewsArticle>> {
        val cachedNews = NewsCache.getNews(category)

        return if (cachedNews?.isFresh() == true) {
            createCachedPagingFlow(cachedNews.news)
        } else {
            Pager(
                config =
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = false,
                        initialLoadSize = 20,
                    ),
                pagingSourceFactory = { NewsPagingSource(apiService, category) },
            ).flow
        }
    }

    private fun createCachedPagingFlow(cachedNews: List<NewsArticle>): Flow<PagingData<NewsArticle>> =
        Pager(
            config =
                PagingConfig(
                    pageSize = 30,
                    enablePlaceholders = false,
                ),
            pagingSourceFactory = {
                object : PagingSource<Int, NewsArticle>() {
                    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> =
                        LoadResult.Page(
                            data = cachedNews,
                            prevKey = null,
                            nextKey = null,
                        )

                    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? = null
                }
            },
        ).flow
}
