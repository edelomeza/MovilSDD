package com.example.movilsdd.data.remote

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.shared.TestDataFactory
import com.example.movilsdd.util.Logger
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import java.io.IOException
import java.net.SocketTimeoutException
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class AuthRemoteDataSourceTest {
    private lateinit var api: AuthApiService
    private lateinit var source: AuthRemoteDataSource

    @BeforeEach
    fun setUp() {
        api = mockk()
        source = AuthRemoteDataSourceImpl(api = api)
    }

    @Test
    fun loginSuccessReturnsPayload() = runTest {
        val payload = TestDataFactory.loginResponseDto()
        coEvery { api.login(any()) } returns Response.success(payload)
        assertEquals(SealedResult.Success(payload), source.login(TestDataFactory.loginRequestDto()))
    }

    @Test
    fun loginNullBodyReturnsUnexpected() = runTest {
        coEvery { api.login(any()) } returns Response.success(null)
        val result = source.login(TestDataFactory.loginRequestDto())
        assertTrue(result is SealedResult.Failure)
        assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
    }

    @Test
    fun loginUnauthorizedMapsToForbidden() = runTest {
        coEvery { api.login(any()) } returns Response.error(401, errorBody())
        assertEquals(SealedResult.Failure(DomainError.Forbidden), source.login(TestDataFactory.loginRequestDto()))
    }

    @Test
    fun loginForbiddenMapsToForbidden() = runTest {
        coEvery { api.login(any()) } returns Response.error(403, errorBody())
        assertEquals(SealedResult.Failure(DomainError.Forbidden), source.login(TestDataFactory.loginRequestDto()))
    }

    @Test
    fun loginNotFoundMapsToNotFound() = runTest {
        coEvery { api.login(any()) } returns Response.error(404, errorBody())
        assertEquals(SealedResult.Failure(DomainError.NotFound), source.login(TestDataFactory.loginRequestDto()))
    }

    @Test
    fun loginConflictMapsToConflict() = runTest {
        coEvery { api.login(any()) } returns Response.error(409, errorBody())
        assertEquals(SealedResult.Failure(DomainError.Conflict(409)), source.login(TestDataFactory.loginRequestDto()))
    }

    @Test
    fun loginTooManyRequestsMapsToConflict() = runTest {
        coEvery { api.login(any()) } returns Response.error(429, errorBody())
        assertEquals(SealedResult.Failure(DomainError.Conflict(429)), source.login(TestDataFactory.loginRequestDto()))
    }

    @Test
    fun loginServerErrorMapsToUnexpected() = runTest {
        coEvery { api.login(any()) } returns Response.error(500, errorBody())
        val result = source.login(TestDataFactory.loginRequestDto())
        assertTrue(result is SealedResult.Failure)
        assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
    }

    @Test
    fun loginTimeoutMapsToUnexpected() = runTest {
        withSilencedLogger {
            coEvery { api.login(any()) } throws SocketTimeoutException("timed out")
            val result = source.login(TestDataFactory.loginRequestDto())
            assertTrue(result is SealedResult.Failure)
            assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
        }
    }

    @Test
    fun loginNetworkFailureMapsToUnexpected() = runTest {
        withSilencedLogger {
            coEvery { api.login(any()) } throws IOException("offline")
            val result = source.login(TestDataFactory.loginRequestDto())
            assertTrue(result is SealedResult.Failure)
            assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
        }
    }

    @Test
    fun logoutSuccessReturnsUnit() = runTest {
        coEvery { api.logout(any()) } returns Response.success(Unit)
        assertEquals(SealedResult.Success(Unit), source.logout("Bearer sample"))
    }

    @Test
    fun logoutUnauthorizedMapsToForbidden() = runTest {
        coEvery { api.logout(any()) } returns Response.error(401, errorBody())
        assertEquals(SealedResult.Failure(DomainError.Forbidden), source.logout("Bearer sample"))
    }

    @Test
    fun logoutTimeoutMapsToUnexpected() = runTest {
        withSilencedLogger {
            coEvery { api.logout(any()) } throws SocketTimeoutException("timed out")
            val result = source.logout("Bearer sample")
            assertTrue(result is SealedResult.Failure)
            assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
        }
    }

    private fun errorBody(): ResponseBody = "error".toResponseBody("application/json".toMediaType())

    private inline fun withSilencedLogger(block: () -> Unit) {
        mockkObject(Logger)
        every { Logger.w(any(), any(), any()) } just Runs
        try {
            block()
        } finally {
            unmockkObject(Logger)
        }
    }
}
