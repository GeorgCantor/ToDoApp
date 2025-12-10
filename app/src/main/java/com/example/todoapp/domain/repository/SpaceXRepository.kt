package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.SpaceXLaunch

interface SpaceXRepository {
    suspend fun getLaunches(limit: Int = 10): Result<List<SpaceXLaunch>>
}
