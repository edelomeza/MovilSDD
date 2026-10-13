package com.example.movilsdd.domain.repository

import com.example.movilsdd.domain.model.LoginResponse
import com.example.movilsdd.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface SessionManager {
    val sessionFlow: Flow<UserSession?>

    suspend fun saveSession(response: LoginResponse)

    suspend fun clearSession()

    suspend fun isSessionValid(): Boolean

    suspend fun getCurrentUser(): UserSession?
}
