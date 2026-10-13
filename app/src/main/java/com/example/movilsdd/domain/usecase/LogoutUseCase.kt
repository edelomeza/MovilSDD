package com.example.movilsdd.domain.usecase

import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.repository.AuthRepository
import com.example.movilsdd.domain.repository.SessionManager

class LogoutUseCase(private val authRepository: AuthRepository, private val sessionManager: SessionManager) {
    suspend operator fun invoke(): SealedResult<Unit> {
        val result = authRepository.logout()
        sessionManager.clearSession()
        return result
    }
}
