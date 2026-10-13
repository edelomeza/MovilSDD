package com.example.movilsdd.data.repository

import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.data.remote.AuthRemoteDataSource
import com.example.movilsdd.data.remote.toDomain
import com.example.movilsdd.data.remote.toDto
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.model.LoginResponse
import com.example.movilsdd.domain.repository.AuthRepository
import com.example.movilsdd.domain.repository.SessionManager
import com.example.movilsdd.util.Logger

private const val REPOSITORY_TAG = "AuthRepository"

class AuthRepositoryImpl(private val remote: AuthRemoteDataSource, private val sessionManager: SessionManager) : AuthRepository {
    override suspend fun login(request: LoginRequest): SealedResult<LoginResponse> {
        val remoteResult = remote.login(request.toDto())
        if (remoteResult is SealedResult.Failure) return remoteResult
        val payload = (remoteResult as SealedResult.Success).data
        return payload.toDomain(request.username)
    }

    override suspend fun logout(): SealedResult<Unit> {
        val token = sessionManager.getCurrentUser()?.token.orEmpty()
        val result = remote.logout(authorization = "Bearer $token")
        if (result is SealedResult.Failure) {
            Logger.w(REPOSITORY_TAG, "logout request did not succeed")
        }
        return result
    }
}
