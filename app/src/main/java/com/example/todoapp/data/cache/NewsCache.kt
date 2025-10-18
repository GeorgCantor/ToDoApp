package com.example.todoapp.data.cache

import android.content.Context
import com.example.todoapp.domain.model.NewsArticle
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

private const val MAX_MEMORY_CACHE_SIZE = 50
private const val CACHE_TTL = 30 * 60 * 1000
private const val CACHE_FILE_NAME = "news_cache.ser"

data class CachedNews(
    val news: List<NewsArticle>,
    val category: String,
    val timeStamp: Long = System.currentTimeMillis(),
) : Serializable {
    fun isFresh(): Boolean = System.currentTimeMillis() - timeStamp < CACHE_TTL
}

object NewsCache {
    private val memoryCache = NewsLruCache<String, CachedNews>(MAX_MEMORY_CACHE_SIZE)
    private var cacheDir: File? = null

    fun init(context: Context) {
        cacheDir = context.cacheDir
        loadFromDisk()
    }

    fun getNews(category: String) = memoryCache.get(category)

    fun putNews(
        news: List<NewsArticle>,
        category: String,
    ) {
        val cachedNews = CachedNews(news, category)
        memoryCache.put(category, cachedNews)
        saveToDisk()
    }

    private fun saveToDisk() {
        try {
            cacheDir?.let { dir ->
                val cacheFile = File(dir, CACHE_FILE_NAME)
                ObjectOutputStream(FileOutputStream(cacheFile)).use { outputStream ->
                    outputStream.writeObject(memoryCache)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun loadFromDisk() {
        try {
            cacheDir?.let { dir ->
                val cacheFile = File(dir, CACHE_FILE_NAME)
                if (cacheFile.exists() && cacheFile.length() > 0) {
                    ObjectInputStream(FileInputStream(cacheFile)).use { inputStream ->
                        val loadedCache = inputStream.readObject() as? NewsLruCache<String, CachedNews>
                        loadedCache?.getAll()?.forEach { (category, cachedNews) ->
                            memoryCache.put(category, cachedNews)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            cacheDir?.let { dir ->
                val cacheFile = File(dir, CACHE_FILE_NAME)
                if (cacheFile.exists()) cacheFile.delete()
            }
        }
    }
}
