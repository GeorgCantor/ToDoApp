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
        fun isFresh(): Boolean = System.currentTimeMillis() - timeStamp < CACHE_TTL
    }

    fun getNews(): CachedNews? = memoryCache.get(HEADLINES)

    fun putNews(news: List<NewsArticle>) {
        memoryCache.put(HEADLINES, CachedNews(news))
    }

    fun clear() {
        memoryCache.clear()
    }
}
