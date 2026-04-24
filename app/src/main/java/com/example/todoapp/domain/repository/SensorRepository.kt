package com.example.todoapp.domain.repository

import com.example.todoapp.domain.model.GravityData
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun getGravityFlow(): Flow<GravityData>
}
