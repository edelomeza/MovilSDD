package com.example.movilsdd.domain.repository

import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.model.LoginResponse

interface AuthRepository {
    suspend fun login(request: LoginRequest): SealedResult<LoginResponse>

    suspend fun logout(): SealedResult<Unit>
}
