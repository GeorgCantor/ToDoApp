package com.example.todoapp.data.remote

import com.example.todoapp.RocketQuery
import com.example.todoapp.domain.model.SpaceXLaunch

interface GraphQLClient {
    suspend fun getLaunches(limit: Int): Result<List<SpaceXLaunch>>
    suspend fun getRocket(rocketId: String): Result<RocketQuery.Rocket?>
}