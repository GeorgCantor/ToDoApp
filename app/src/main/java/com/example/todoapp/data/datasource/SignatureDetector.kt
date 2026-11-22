package com.example.todoapp.data.datasource

import com.example.todoapp.domain.model.SignatureType

object SignatureDetector {
    private val googleHashes = setOf("24bb24c05e47e0aefa68a58a766179d9b613a600")
    private val oemHashes =
        setOf(
            "a5f3f8e9d2c1b4a5f3f8e9d2c1b4a5f3f8e9d2c1", // Samsung
            "c8d9e5f2e8c2c8c9d5e8f2e8c2c8c9d5e8f2e8c2", // Xiaomi
        )

    fun detect(hash: String) =
        when {
            hash.lowercase() in googleHashes -> SignatureType.GOOGLE
            hash.lowercase() in oemHashes -> SignatureType.OEM
            else -> SignatureType.THIRD_PARTY
        }
}
