package com.example.todoapp.data.datastore

import androidx.datastore.core.Serializer
import com.example.todoapp.domain.model.NetworkMetricsList
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object NetworkMetricsSerializer : Serializer<NetworkMetricsList> {
    override val defaultValue = NetworkMetricsList()

    override suspend fun readFrom(input: InputStream): NetworkMetricsList =
        try {
            Json.decodeFromString(
                NetworkMetricsList.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }

    override suspend fun writeTo(
        t: NetworkMetricsList,
        output: OutputStream,
    ) {
        output.write(
            Json.encodeToString(NetworkMetricsList.serializer(), t).encodeToByteArray(),
        )
    }
}
