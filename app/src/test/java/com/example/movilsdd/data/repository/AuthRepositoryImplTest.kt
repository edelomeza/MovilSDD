package com.example.movilsdd.data.repository

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.data.remote.AuthRemoteDataSource
import com.example.movilsdd.data.remote.dto.LoginRequestDto
import com.example.movilsdd.domain.repository.SessionManager
import com.example.movilsdd.shared.TestDataFactory
import com.example.movilsdd.util.Logger
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkObject
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthRepositoryImplTest {
    private lateinit var remote: AuthRemoteDataSource
    private lateinit var sessionManager: SessionManager
    private lateinit var repository: AuthRepositoryImpl

    @BeforeEach
    fun setUp() {
        remote = mockk()
        sessionManager = mockk()
        repository = AuthRepositoryImpl(remote = remote, sessionManager = sessionManager)
    }

    @Test
    fun loginMapsValidExpiryToDomain() = runTest {
        coEvery { remote.login(any()) } returns SealedResult.Success(TestDataFactory.loginResponseDto())
        val result = repository.login(TestDataFactory.loginRequest())
        assertTrue(result is SealedResult.Success)
        val data = (result as SealedResult.Success).data
        assertEquals(TestDataFactory.FIXTURE_TOKEN, data.token)
        assertEquals(TestDataFactory.VALID_USERNAME, data.username)
        assertEquals(TestDataFactory.FUTURE_EXPIRY_MILLIS, data.expiresAtMillis)
        val captured = slot<LoginRequestDto>()
        coVerify(exactly = 1) { remote.login(capture(captured)) }
        assertEquals(TestDataFactory.VALID_USERNAME, captured.captured.username)
        assertEquals(TestDataFactory.VALID_PASSWORD, captured.captured.password)
    }

    @Test
    fun loginInvalidExpiryReturnsUnexpectedWithoutPersisting() = runTest {
        val invalid = TestDataFactory.loginResponseDto(expiresAt = TestDataFactory.INVALID_EXPIRY_ISO)
        coEvery { remote.login(any()) } returns SealedResult.Success(invalid)
        val result = repository.login(TestDataFactory.loginRequest())
        assertTrue(result is SealedResult.Failure)
        assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun loginPropagatesRemoteFailure() = runTest {
        coEvery { remote.login(any()) } returns SealedResult.Failure(DomainError.Forbidden)
        val result = repository.login(TestDataFactory.loginRequest())
        assertEquals(SealedResult.Failure(DomainError.Forbidden), result)
        coVerify(exactly = 0) { sessionManager.saveSession(any()) }
    }

    @Test
    fun logoutSendsBearerAuthorization() = runTest {
        coEvery { sessionManager.getCurrentUser() } returns TestDataFactory.userSession()
        coEvery { remote.logout(any()) } returns SealedResult.Success(Unit)
        val result = repository.logout()
        assertEquals(SealedResult.Success(Unit), result)
        coVerify(exactly = 1) { remote.logout("Bearer ${TestDataFactory.FIXTURE_TOKEN}") }
    }

    @Test
    fun logoutWithoutSessionSendsEmptyBearer() = runTest {
        coEvery { sessionManager.getCurrentUser() } returns null
        coEvery { remote.logout(any()) } returns SealedResult.Success(Unit)
        repository.logout()
        coVerify(exactly = 1) { remote.logout("Bearer ") }
    }

    @Test
    fun logoutFailureReturnsFailure() = runTest {
        mockkObject(Logger)
        every { Logger.w(any(), any(), any()) } just Runs
        try {
            coEvery { sessionManager.getCurrentUser() } returns TestDataFactory.userSession()
            coEvery { remote.logout(any()) } returns SealedResult.Failure(DomainError.Unexpected())
            val result = repository.logout()
            assertTrue(result is SealedResult.Failure)
        } finally {
            unmockkObject(Logger)
        }
    }
}
