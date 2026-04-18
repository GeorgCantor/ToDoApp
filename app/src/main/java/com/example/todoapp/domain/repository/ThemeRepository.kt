package com.example.todoapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getThemeColor(): Flow<Int>

    suspend fun saveThemeColor(colorInt: Int)
}
