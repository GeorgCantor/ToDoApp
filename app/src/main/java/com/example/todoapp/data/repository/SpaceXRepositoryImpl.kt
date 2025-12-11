package com.example.todoapp.data.repository

import com.example.todoapp.data.remote.GraphQLClient
import com.example.todoapp.domain.repository.SpaceXRepository

class SpaceXRepositoryImpl(
    private val graphQLClient: GraphQLClient,
) : SpaceXRepository {
    override suspend fun getLaunches(limit: Int) = graphQLClient.getLaunches(limit)
}
