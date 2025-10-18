package com.example.todoapp.data.cache

import java.io.Serializable

class NewsLruCache<K : Serializable, V : Serializable>(
    private val maxSize: Int,
) : Serializable {
    private val cache = LinkedHashMap<K, V>(maxSize, 0.75F, true)

    @Synchronized
    fun get(key: K): V? = cache[key]

    @Synchronized
    fun put(
        key: K,
        value: V,
    ): V? {
        val previous = cache.put(key, value)
        if (cache.size > maxSize) {
            val eldest = cache.entries.iterator().next()
            cache.remove(eldest.key)
        }
        return previous
    }

    @Synchronized
    fun size() = cache.size

    @Synchronized
    fun getAll(): Map<K, V> = HashMap(cache)
}
