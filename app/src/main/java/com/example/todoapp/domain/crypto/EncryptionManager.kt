package com.example.todoapp.domain.crypto

import android.util.Base64
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyAgreement
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionManager {
    private const val GCM_NONCE_LENGTH = 12
    private const val GCM_TAG_LENGTH = 128
    private const val AES_GCM = "AES/GCM/NoPadding"
    private const val EC = "EC"

    // Генерирует пару ключей для ECDH
    fun generateKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance(EC)
        generator.initialize(ECGenParameterSpec("secp256r1"))
        return generator.generateKeyPair()
    }

    // Вычисляет общий секрет из своего приватного ключа и публичного ключа собеседника
    fun computeSharedSecret(
        privateKey: PrivateKey,
        publicKey: PublicKey,
    ): ByteArray {
        val agreement = KeyAgreement.getInstance("ECDH")
        agreement.init(privateKey)
        agreement.doPhase(publicKey, true)
        return agreement.generateSecret()
    }

    // Создаёт AES-ключ из общего секрета
    fun deriveAesKey(secret: ByteArray) = SecretKeySpec(secret.copyOfRange(0, 32), "AES")

    // Шифрует сообщение с использованием AES/GCM/NoPadding
    fun encrypt(
        message: String,
        secretKey: SecretKey,
    ): String {
        val cipher = Cipher.getInstance(AES_GCM)
        val nonce = ByteArray(GCM_NONCE_LENGTH)
        SecureRandom().nextBytes(nonce)

        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, gcmSpec)

        val ciphertext = cipher.doFinal(message.toByteArray(Charsets.UTF_8))
        val result = nonce + ciphertext
        return Base64.encodeToString(result, Base64.NO_WRAP)
    }

    // Расшифровывает сообщение
    fun decrypt(
        encryptedData: String,
        secretKey: SecretKey,
    ): String {
        val data = Base64.decode(encryptedData, Base64.NO_WRAP)
        val nonce = data.copyOfRange(0, GCM_NONCE_LENGTH)
        val ciphertext = data.copyOfRange(GCM_NONCE_LENGTH, data.size)

        val cipher = Cipher.getInstance(AES_GCM)
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, nonce)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmSpec)

        return String(cipher.doFinal(ciphertext), Charsets.UTF_8)
    }

    fun publicKeyToString(key: PublicKey) = Base64.encodeToString(key.encoded, Base64.NO_WRAP)

    fun stringToPublicKey(keyString: String): PublicKey {
        val keyBytes = Base64.decode(keyString, Base64.NO_WRAP)
        return KeyFactory.getInstance(EC).generatePublic(X509EncodedKeySpec(keyBytes))
    }
}
