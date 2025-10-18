package com.example.todoapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.todoapp.data.cache.NewsCache
import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.domain.model.NewsArticle

class NewsPagingSource(
    private val newsApiService: NewsApiService,
    private val category: String,
) : PagingSource<Int, NewsArticle>() {
    override fun getRefreshKey(state: PagingState<Int, NewsArticle>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsArticle> =
        try {
            val page = params.key ?: 1
            val pageSize = params.loadSize.coerceAtMost(20)

            val response =
                newsApiService.getTopHeadlines(
                    category = category,
                    page = page,
                    pageSize = pageSize,
                )

            if (page == 1) {
                NewsCache.putNews(response.articles.map { it.toNewsArticle() }, category)
            }

            if (response.status == "ok") {
                val articles = response.articles.map { it.toNewsArticle() }
                LoadResult.Page(
                    data = articles,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (articles.isEmpty()) null else page + 1,
                )
            } else {
                LoadResult.Error(Throwable("API returned error status"))
            }
        } catch (e: Exception) {
            if (params.key == null || params.key == 1) {
                NewsCache.getNews(category)?.let { cachedNews ->
                    LoadResult.Page(
                        data = cachedNews.news,
                        prevKey = null,
                        nextKey = null,
                    )
                } ?: LoadResult.Error(e)
            } else {
                LoadResult.Error(e)
            }
        }
}
