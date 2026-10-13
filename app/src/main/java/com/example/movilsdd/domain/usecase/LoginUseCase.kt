package com.example.movilsdd.domain.usecase

import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.model.LoginResponse
import com.example.movilsdd.domain.repository.AuthRepository
import com.example.movilsdd.domain.repository.SessionManager
import com.example.movilsdd.util.validatePassword
import com.example.movilsdd.util.validateUsername

class LoginUseCase(private val authRepository: AuthRepository, private val sessionManager: SessionManager) {
    suspend operator fun invoke(request: LoginRequest): SealedResult<LoginResponse> {
        val usernameCheck = validateUsername(request.username)
        if (usernameCheck is SealedResult.Failure) return usernameCheck
        val passwordCheck = validatePassword(request.password)
        if (passwordCheck is SealedResult.Failure) return passwordCheck
        val result = authRepository.login(request)
        if (result is SealedResult.Success) {
            sessionManager.saveSession(result.data)
        }
        return result
    }
}
