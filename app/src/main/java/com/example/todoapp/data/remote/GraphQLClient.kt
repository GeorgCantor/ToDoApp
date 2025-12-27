package com.example.todoapp.data.remote

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.network.okHttpClient
import com.example.todoapp.LaunchDetailQuery
import com.example.todoapp.LaunchesQuery
import com.example.todoapp.RocketQuery
import com.example.todoapp.data.remote.model.toDomain
import com.example.todoapp.data.remote.model.toLaunchDetail
import com.example.todoapp.data.remote.model.toRocketDetail
import com.example.todoapp.domain.model.RocketDetail
import com.example.todoapp.domain.model.SpaceXLaunch
import okhttp3.OkHttpClient

interface GraphQLClient {
    suspend fun getLaunches(limit: Int): Result<List<SpaceXLaunch>>

    suspend fun getLaunchDetail(id: String): Result<SpaceXLaunch>

    suspend fun getRocketDetail(rocketId: String): Result<RocketDetail>
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

    override suspend fun getRocketDetail(rocketId: String): Result<RocketDetail> {
        return try {
            val response = client.query(RocketQuery(rocketId)).execute()
            if (response.hasErrors()) {
                Result.failure(Exception(response.errors?.first()?.message))
            } else {
                val rocket =
                    response.data?.rocket?.toRocketDetail()
                        ?: return Result.failure(Exception("Rocket not found"))
                Result.success(rocket)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
