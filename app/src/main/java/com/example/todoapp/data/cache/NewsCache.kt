package com.example.todoapp.data.cache

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.example.todoapp.domain.model.NewsArticle
import kotlinx.parcelize.Parcelize
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

private const val MAX_MEMORY_CACHE_SIZE = 50
private const val CACHE_TTL = 30 * 60 * 1000
private const val CACHE_FILE_NAME = "news_cache.parcel"
const val HEADLINES = "headlines"

object NewsCache {
    private val memoryCache = NewsLruCache<String, CachedNews>(MAX_MEMORY_CACHE_SIZE)
    private var cacheDir: File? = null

    @Parcelize
    data class CachedNews(
        val news: List<NewsArticle>,
        val timeStamp: Long = System.currentTimeMillis(),
    ) : Parcelable {
        fun isFresh(): Boolean = System.currentTimeMillis() - timeStamp < CACHE_TTL
    }

    fun init(context: Context) {
        cacheDir = context.cacheDir
        loadFromDisk()
    }

    fun getNews(): CachedNews? = memoryCache.get(HEADLINES)

    fun putNews(news: List<NewsArticle>) {
        val cachedNews = CachedNews(news)
        memoryCache.put(HEADLINES, cachedNews)
        saveToDisk(cachedNews)
    }

    private fun saveToDisk(cachedNews: CachedNews) {
        try {
            cacheDir?.let { dir ->
                val cacheFile = File(dir, CACHE_FILE_NAME)
                val parcel = Parcel.obtain()
                try {
                    parcel.writeParcelable(cachedNews, 0)
                    FileOutputStream(cacheFile).use { outputStream ->
                        outputStream.write(parcel.marshall())
                    }
                } finally {
                    parcel.recycle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFromDisk() {
        try {
            cacheDir?.let { dir ->
                val cacheFile = File(dir, CACHE_FILE_NAME)
                if (cacheFile.exists() && cacheFile.length() > 0) {
                    FileInputStream(cacheFile).use { inputStream ->
                        val bytes = inputStream.readBytes()
                        val parcel = Parcel.obtain()
                        try {
                            parcel.unmarshall(bytes, 0, bytes.size)
                            parcel.setDataPosition(0)
                            val cachedNews = parcel.readParcelable<CachedNews>(CachedNews::class.java.classLoader)
                            if (cachedNews != null) {
                                memoryCache.put(HEADLINES, cachedNews)
                            }
                        } finally {
                            parcel.recycle()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
