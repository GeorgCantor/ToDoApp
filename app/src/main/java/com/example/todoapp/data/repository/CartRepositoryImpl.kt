package com.example.todoapp.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.todoapp.domain.model.BadgeType
import com.example.todoapp.domain.model.CartItem
import com.example.todoapp.domain.repository.CartRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepositoryImpl(
    context: Context,
    private val gson: Gson,
) : CartRepository {
    private val prefs: SharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val cartKey = "cart_items"
    private var cachedItems: MutableMap<Int, CartItem>? = null

    override suspend fun getCartItems(): List<CartItem> =
        withContext(Dispatchers.IO) {
            getCachedItems().values.toList()
        }

    override suspend fun updateQuantity(
        itemId: Int,
        quantity: Int,
    ) = withContext(Dispatchers.IO) {
        val items = getCachedItems()
        items[itemId]?.copy(quantity = quantity)?.let { items[itemId] = it }
        saveToPrefs(items)
    }

    override suspend fun removeItem(itemId: Int) =
        withContext(Dispatchers.IO) {
            val items = getCachedItems()
            items.remove(itemId)
            saveToPrefs(items)
        }

    override suspend fun addItem(item: CartItem) =
        withContext(Dispatchers.IO) {
            val items = getCachedItems()
            items[item.id] = item
            saveToPrefs(items)
        }

    override suspend fun clearCart() =
        withContext(Dispatchers.IO) {
            cachedItems = mutableMapOf()
            prefs.edit().remove(cartKey).apply()
        }

    private fun getCachedItems(): MutableMap<Int, CartItem> {
        if (cachedItems == null) {
            val json = prefs.getString(cartKey, null)
            cachedItems =
                if (json == null) {
                    createInitialCartItems().toMutableMap()
                } else {
                    val type = object : TypeToken<MutableMap<Int, CartItem>>() {}.type
                    gson.fromJson(json, type)
                }
        }
        return cachedItems!!
    }

    private fun saveToPrefs(items: MutableMap<Int, CartItem>) {
        cachedItems = items
        val json = gson.toJson(items)
        prefs.edit().putString(cartKey, json).apply()
    }

    private fun createInitialCartItems(): Map<Int, CartItem> =
        mapOf(
            1001 to
                CartItem(
                    id = 1001,
                    name = "Стоматологическое зеркало",
                    price = 1250.0,
                    image = "https://example.com/images/mirror.jpg",
                    quantity = 1,
                    badges = emptyList(),
                ),
            1003 to
                CartItem(
                    id = 1003,
                    name = "Бор алмазный круглый",
                    price = 320.0,
                    image = "https://example.com/images/round_bur.jpg",
                    quantity = 1,
                    badges = listOf(BadgeType.PROMOTION),
                ),
            2001 to
                CartItem(
                    id = 2001,
                    name = "Фотополимерный композит",
                    price = 4500.0,
                    image = "https://example.com/images/composite.jpg",
                    quantity = 1,
                    badges = emptyList(),
                ),
            4001 to
                CartItem(
                    id = 4001,
                    name = "Наконечник стоматологический",
                    price = 12500.0,
                    image = "https://example.com/images/handpiece.jpg",
                    quantity = 82,
                    badges = listOf(BadgeType.NEW, BadgeType.BESTSELLER),
                ),
            3001 to
                CartItem(
                    id = 3001,
                    name = "Стоматологическая установка Premium",
                    price = 450000.0,
                    image = "https://example.com/images/dental_unit.jpg",
                    quantity = 1,
                    badges = emptyList(),
                ),
        )
}
