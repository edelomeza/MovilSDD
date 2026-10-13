package com.example.movilsdd.domain.usecase

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.repository.AuthRepository
import com.example.movilsdd.domain.repository.SessionManager
import com.example.movilsdd.shared.TestDataFactory
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LoginUseCaseTest {
    private lateinit var authRepository: AuthRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var useCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        authRepository = mockk()
        sessionManager = mockk()
        useCase = LoginUseCase(authRepository = authRepository, sessionManager = sessionManager)
    }

    @Test
    fun validCredentialsSaveSessionOnSuccess() = runTest {
        val response = TestDataFactory.loginResponse()
        coEvery { authRepository.login(any()) } returns SealedResult.Success(response)
        coEvery { sessionManager.saveSession(response) } just Runs
        val result = useCase(TestDataFactory.loginRequest())
        assertEquals(SealedResult.Success(response), result)
        coVerify(exactly = 1) { sessionManager.saveSession(response) }
    }

    @Test
    fun invalidUsernameSkipsRepository() = runTest {
        val result = useCase(TestDataFactory.loginRequest(username = "ab"))
        assertTrue(result is SealedResult.Failure)
        coVerify(exactly = 0) { authRepository.login(any()) }
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun invalidPasswordSkipsRepository() = runTest {
        val result = useCase(TestDataFactory.loginRequest(password = "short"))
        assertTrue(result is SealedResult.Failure)
        coVerify(exactly = 0) { authRepository.login(any()) }
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun forbiddenErrorSkipsSaveSession() = runTest {
        coEvery { authRepository.login(any()) } returns SealedResult.Failure(DomainError.Forbidden)
        val result = useCase(TestDataFactory.loginRequest())
        assertEquals(SealedResult.Failure(DomainError.Forbidden), result)
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun networkErrorSkipsSaveSession() = runTest {
        val network = SealedResult.Failure(DomainError.Unexpected(IOException("offline")))
        coEvery { authRepository.login(any()) } returns network
        val result = useCase(TestDataFactory.loginRequest())
        assertEquals(network, result)
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun serverErrorSkipsSaveSession() = runTest {
        coEvery { authRepository.login(any()) } returns SealedResult.Failure(DomainError.Unexpected())
        val result = useCase(TestDataFactory.loginRequest())
        assertTrue(result is SealedResult.Failure)
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }
}
