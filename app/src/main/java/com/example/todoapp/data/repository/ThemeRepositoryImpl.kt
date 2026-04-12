package com.example.todoapp.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoapp.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("theme_prefs")

class ThemeRepositoryImpl(
    context: Context,
) : ThemeRepository {
    private val dataStore = context.dataStore
    private val key = intPreferencesKey("selected_color")

    override fun getThemeColor() = dataStore.data.map { it[key] ?: 0xFF6200EE.toInt() }

    override suspend fun saveThemeColor(colorInt: Int) {
        dataStore.edit { it[key] = colorInt }
    }
}
