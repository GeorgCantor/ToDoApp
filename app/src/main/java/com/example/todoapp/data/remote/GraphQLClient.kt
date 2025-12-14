package com.example.todoapp.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.okHttpClient
import com.example.todoapp.LaunchDetailQuery
import com.example.todoapp.LaunchesQuery
import com.example.todoapp.RocketQuery
import com.example.todoapp.data.remote.model.toDomain
import com.example.todoapp.data.remote.model.toLaunchDetail
import com.example.todoapp.domain.model.SpaceXLaunch
import okhttp3.OkHttpClient

interface GraphQLClient {
    suspend fun getLaunches(limit: Int): Result<List<SpaceXLaunch>>

    suspend fun getLaunchDetail(id: String): Result<SpaceXLaunch>

    suspend fun getRocket(rocketId: String): Result<RocketQuery.Rocket?>
}

class ApolloGraphQLClient(
    okHttpClient: OkHttpClient,
) : GraphQLClient {
    private val client =
        ApolloClient
            .Builder()
            .serverUrl("https://spacex-production.up.railway.app/")
            .okHttpClient(okHttpClient)
            .build()

    override suspend fun getLaunches(limit: Int): Result<List<SpaceXLaunch>> =
        try {
            val response = client.query(LaunchesQuery(Optional.present(limit))).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                val launches =
                    response.data
                        ?.launches
                        ?.mapNotNull { it?.toDomain() }
                        .orEmpty()
                Result.success(launches)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun getLaunchDetail(id: String): Result<SpaceXLaunch> {
        return try {
            val response = client.query(LaunchDetailQuery(id)).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                val launch =
                    response.data?.launch?.toLaunchDetail()
                        ?: return Result.failure(Exception("Launch not found"))
                Result.success(launch)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRocket(rocketId: String): Result<RocketQuery.Rocket?> =
        try {
            val response = client.query(RocketQuery(rocketId)).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                Result.success(response.data?.rocket)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
