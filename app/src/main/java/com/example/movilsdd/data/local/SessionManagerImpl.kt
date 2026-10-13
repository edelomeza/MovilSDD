package com.example.movilsdd.data.local

import com.example.movilsdd.domain.model.LoginResponse
import com.example.movilsdd.domain.model.UserSession
import com.example.movilsdd.domain.repository.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManagerImpl(private val tokenStore: TokenLocalDataSource) : SessionManager {
    private val states = MutableStateFlow(loadStoredSession())

    override val sessionFlow: Flow<UserSession?> = states.asStateFlow()

    override suspend fun saveSession(response: LoginResponse) {
        tokenStore.saveCredentials(response.token, response.username, response.expiresAtMillis)
        states.value = UserSession(username = response.username, token = response.token, expiresAtMillis = response.expiresAtMillis)
    }

    override suspend fun clearSession() {
        tokenStore.clear()
        states.value = null
    }

    override suspend fun isSessionValid(): Boolean {
        val current = states.value ?: return false
        return System.currentTimeMillis() < current.expiresAtMillis
    }

    override suspend fun getCurrentUser(): UserSession? = states.value

    private fun loadStoredSession(): UserSession? {
        val token = tokenStore.readToken()
        val username = tokenStore.readUsername()
        if (token.isNullOrEmpty() || username.isNullOrEmpty()) return null
        return UserSession(username = username, token = token, expiresAtMillis = tokenStore.readExpiresAtMillis())
    }
}
