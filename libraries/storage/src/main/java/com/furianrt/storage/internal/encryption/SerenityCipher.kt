package com.furianrt.storage.internal.encryption

import android.annotation.SuppressLint
import android.util.Base64
import com.furianrt.core.DispatchersProvider
import com.furianrt.storage.BuildConfig
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

private const val ALGORITHM_NAME = "AES"

internal class SerenityCipher @Inject constructor(
    private val dispatchers: DispatchersProvider,
) {

    @SuppressLint("GetInstance")
    suspend fun encryptString(string: String): String = withContext(dispatchers.default) {
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, ALGORITHM_NAME)
        val cipher = Cipher.getInstance(ALGORITHM_NAME)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey)
        val encrypted = cipher.doFinal(string.toByteArray())
        return@withContext Base64.encodeToString(encrypted, Base64.DEFAULT)
    }

    @SuppressLint("GetInstance")
    suspend fun decryptString(string: String): String = withContext(dispatchers.default) {
        val keyBytes = BuildConfig.PREFS_PASSWORD.toByteArray()
        val aesKey = SecretKeySpec(keyBytes, ALGORITHM_NAME)
        val cipher = Cipher.getInstance(ALGORITHM_NAME)
        cipher.init(Cipher.DECRYPT_MODE, aesKey)
        val decryptedByteValue = cipher.doFinal(Base64.decode(string.toByteArray(), Base64.DEFAULT))
        return@withContext String(decryptedByteValue)
    }
}