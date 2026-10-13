package com.example.movilsdd.domain.usecase

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.repository.AuthRepository
import com.example.movilsdd.domain.repository.SessionManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LogoutUseCaseTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var useCase: LogoutUseCase

    @BeforeEach
    fun setUp() {
        authRepository = mockk()
        sessionManager = mockk()
        useCase = LogoutUseCase(authRepository = authRepository, sessionManager = sessionManager)
    }

    @Test
    fun logoutSuccessClearsSession() = runTest {
        coEvery { authRepository.logout() } returns SealedResult.Success(Unit)
        coEvery { sessionManager.clearSession() } just Runs
        val result = useCase()
        assertEquals(SealedResult.Success(Unit), result)
        coVerify(exactly = 1) { sessionManager.clearSession() }
    }

    @Test
    fun logoutFailureStillClearsSessionLocalFirst() = runTest {
        coEvery { authRepository.logout() } returns SealedResult.Failure(DomainError.Unexpected())
        coEvery { sessionManager.clearSession() } just Runs
        val result = useCase()
        assertTrue(result is SealedResult.Failure)
        coVerify(exactly = 1) { sessionManager.clearSession() }
    }
}
