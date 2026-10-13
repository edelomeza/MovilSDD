package com.example.movilsdd.shared

import com.example.movilsdd.data.local.TokenLocalDataSource

class InMemoryTokenStore : TokenLocalDataSource {
    var storedToken: String? = null
        private set
    var storedUsername: String? = null
        private set
    var storedExpiresAtMillis: Long = 0L
        private set

    override fun saveCredentials(token: String, username: String, expiresAtMillis: Long) {
        storedToken = token
        storedUsername = username
        storedExpiresAtMillis = expiresAtMillis
    }

    override fun readToken(): String? = storedToken

    override fun readUsername(): String? = storedUsername

    override fun readExpiresAtMillis(): Long = storedExpiresAtMillis

    override fun clear() {
        storedToken = null
        storedUsername = null
        storedExpiresAtMillis = 0L
    }
}
