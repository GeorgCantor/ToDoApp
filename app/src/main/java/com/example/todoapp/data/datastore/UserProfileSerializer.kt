package com.example.todoapp.data.datastore

import androidx.datastore.core.Serializer
import com.example.todoapp.domain.model.UserProfile
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object UserProfileSerializer : Serializer<UserProfile> {
    override val defaultValue: UserProfile = UserProfile()

    override suspend fun readFrom(input: InputStream): UserProfile =
        try {
            Json.decodeFromString(
                UserProfile.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            e.printStackTrace()
            defaultValue
        }

    override suspend fun writeTo(
        t: UserProfile,
        output: OutputStream,
    ) {
        output.write(
            Json.encodeToString(UserProfile.serializer(), t).encodeToByteArray(),
        )
    }
}
