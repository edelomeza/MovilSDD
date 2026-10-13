package com.example.movilsdd.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

private const val AUTH_PREFS_NAME = "movilsdd_auth_prefs"
private const val KEY_TOKEN = "auth_token"
private const val KEY_USERNAME = "auth_username"
private const val KEY_EXPIRES_AT = "auth_expires_at_millis"

interface TokenLocalDataSource {
    fun saveCredentials(token: String, username: String, expiresAtMillis: Long)

    fun readToken(): String?

    fun readUsername(): String?

    fun readExpiresAtMillis(): Long

    fun clear()
}

class TokenLocalDataSourceImpl(context: Context) : TokenLocalDataSource {
    private val appContext: Context = context.applicationContext
    private val lock = Any()
    private var cached: SharedPreferences? = null

    private fun prefs(): SharedPreferences {
        synchronized(lock) {
            val existing = cached
            if (existing != null) return existing
            val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val created = EncryptedSharedPreferences.create(
                AUTH_PREFS_NAME,
                masterKeyAlias,
                appContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            cached = created
            return created
        }
    }

    override fun saveCredentials(token: String, username: String, expiresAtMillis: Long) {
        prefs().edit().putString(KEY_TOKEN, token).putString(KEY_USERNAME, username).putLong(KEY_EXPIRES_AT, expiresAtMillis).apply()
    }

    override fun readToken(): String? = prefs().getString(KEY_TOKEN, null)

    override fun readUsername(): String? = prefs().getString(KEY_USERNAME, null)

    override fun readExpiresAtMillis(): Long = prefs().getLong(KEY_EXPIRES_AT, 0L)

    override fun clear() {
        prefs().edit().remove(KEY_TOKEN).remove(KEY_USERNAME).remove(KEY_EXPIRES_AT).apply()
    }
}
