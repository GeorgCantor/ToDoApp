package com.example.todoapp.data.repository

import com.example.todoapp.data.remote.GraphQLClient
import com.example.todoapp.domain.repository.SpaceXRepository

class SpaceXRepositoryImpl(
    private val client: GraphQLClient,
) : SpaceXRepository {
    override suspend fun getLaunches(limit: Int) = client.getLaunches(limit)

    override suspend fun getLaunchDetail(id: String) = client.getLaunchDetail(id)

    override suspend fun getRocketDetail(rocketId: String) = client.getRocketDetail(rocketId)
}
