package com.example.movilsdd.data.remote

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.shared.TestDataFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AuthMappersTest {
    @Test
    fun loginRequestMapsToWireNames() {
        val dto = TestDataFactory.loginRequest().toDto()
        assertEquals(TestDataFactory.VALID_USERNAME, dto.username)
        assertEquals(TestDataFactory.VALID_PASSWORD, dto.password)
    }

    @Test
    fun validExpiryMapsToEpochMillis() {
        val result = TestDataFactory.loginResponseDto().toDomain(TestDataFactory.VALID_USERNAME)
        assertTrue(result is SealedResult.Success)
        val data = (result as SealedResult.Success).data
        assertEquals(TestDataFactory.FIXTURE_TOKEN, data.token)
        assertEquals(TestDataFactory.VALID_USERNAME, data.username)
        assertEquals(TestDataFactory.FUTURE_EXPIRY_MILLIS, data.expiresAtMillis)
    }

    @Test
    fun invalidExpiryReturnsUnexpected() {
        val dto = TestDataFactory.loginResponseDto(expiresAt = TestDataFactory.INVALID_EXPIRY_ISO)
        val result = dto.toDomain(TestDataFactory.VALID_USERNAME)
        assertTrue(result is SealedResult.Failure)
        assertTrue((result as SealedResult.Failure).error is DomainError.Unexpected)
    }

    @Test
    fun blankExpiryReturnsUnexpected() {
        val dto = TestDataFactory.loginResponseDto(expiresAt = "   ")
        val result = dto.toDomain(TestDataFactory.VALID_USERNAME)
        assertTrue(result is SealedResult.Failure)
    }

    @Test
    fun parseExpiryTrimsWhitespace() {
        assertEquals(TestDataFactory.FUTURE_EXPIRY_MILLIS, parseExpiryToMillis("  2030-01-01T00:00:00Z  "))
    }

    @Test
    fun parseExpiryRejectsGarbage() {
        assertNull(parseExpiryToMillis(TestDataFactory.INVALID_EXPIRY_ISO))
        assertNull(parseExpiryToMillis(""))
    }
}
