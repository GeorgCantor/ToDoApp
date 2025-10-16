package com.example.todoapp.data.cache

import com.example.todoapp.domain.model.NewsArticle

private const val MAX_CACHE_SIZE = 50
private const val CACHE_TTL = 30 * 60 * 1000
const val HEADLINES = "headlines"

object NewsCache {
    private val memoryCache = NewsLruCache<String, CachedNews>(MAX_CACHE_SIZE)

    data class CachedNews(
        val news: List<NewsArticle>,
        val timeStamp: Long = System.currentTimeMillis(),
    ) {
        fun isExpired() = System.currentTimeMillis() - timeStamp > CACHE_TTL
    }

    fun getNews(): List<NewsArticle>? {
        val cached = memoryCache.get(HEADLINES)
        return if (cached != null && !cached.isExpired()) {
            cached.news
        } else {
            cached?.let { memoryCache.remove(HEADLINES) }
            null
        }
    }

    fun putNews(news: List<NewsArticle>) {
        memoryCache.put(HEADLINES, CachedNews(news))
    }

    fun getArticleById(id: Int): NewsArticle? = memoryCache.get(HEADLINES)?.news?.find { it.id == id }

    fun clear() = memoryCache.clear()
}
