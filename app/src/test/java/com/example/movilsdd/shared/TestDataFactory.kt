package com.example.movilsdd.shared

import com.example.movilsdd.data.remote.dto.LoginRequestDto
import com.example.movilsdd.data.remote.dto.LoginResponseDto
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.model.LoginResponse
import com.example.movilsdd.domain.model.UserSession
import java.util.UUID

object TestDataFactory {
    fun id(): String = UUID.randomUUID().toString()
    fun name(prefix: String = "item"): String = "$prefix-${UUID.randomUUID().toString().take(8)}"
    val words: String = "lorem ipsum dolor sit amet"

    const val VALID_USERNAME: String = "test.user01"
    const val VALID_PASSWORD: String = "TestPassword123"
    const val FIXTURE_TOKEN: String = "fixture-token-not-real-0001"
    const val FUTURE_EXPIRY_ISO: String = "2030-01-01T00:00:00Z"
    const val PAST_EXPIRY_ISO: String = "2000-01-01T00:00:00Z"
    const val INVALID_EXPIRY_ISO: String = "not-a-date"
    const val FUTURE_EXPIRY_MILLIS: Long = 1893456000000L

    fun loginRequest(username: String = VALID_USERNAME, password: String = VALID_PASSWORD): LoginRequest =
        LoginRequest(username = username, password = password)

    fun loginRequestDto(username: String = VALID_USERNAME, password: String = VALID_PASSWORD): LoginRequestDto =
        LoginRequestDto(username = username, password = password)

    fun loginResponseDto(token: String = FIXTURE_TOKEN, expiresAt: String = FUTURE_EXPIRY_ISO): LoginResponseDto =
        LoginResponseDto(token = token, expiresAt = expiresAt)

    fun loginResponse(token: String = FIXTURE_TOKEN, username: String = VALID_USERNAME, expiresAtMillis: Long = FUTURE_EXPIRY_MILLIS): LoginResponse =
        LoginResponse(token = token, username = username, expiresAtMillis = expiresAtMillis)

    fun userSession(username: String = VALID_USERNAME, token: String = FIXTURE_TOKEN, expiresAtMillis: Long = FUTURE_EXPIRY_MILLIS): UserSession =
        UserSession(username = username, token = token, expiresAtMillis = expiresAtMillis)

    fun futureExpiresAtMillis(nowMillis: Long = System.currentTimeMillis()): Long = nowMillis + 3_600_000L

    fun pastExpiresAtMillis(nowMillis: Long = System.currentTimeMillis()): Long = nowMillis - 3_600_000L
}
