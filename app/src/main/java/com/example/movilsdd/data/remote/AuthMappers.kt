package com.example.movilsdd.data.remote

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.data.remote.dto.LoginRequestDto
import com.example.movilsdd.data.remote.dto.LoginResponseDto
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.model.LoginResponse
import java.time.Instant

fun LoginRequest.toDto(): LoginRequestDto = LoginRequestDto(username = username, password = password)

fun LoginResponseDto.toDomain(username: String): SealedResult<LoginResponse> {
    val expiresAtMillis = parseExpiryToMillis(expiresAt)
    if (expiresAtMillis == null) return SealedResult.Failure(DomainError.Unexpected())
    return SealedResult.Success(LoginResponse(token = token, username = username, expiresAtMillis = expiresAtMillis))
}

fun parseExpiryToMillis(isoValue: String): Long? = runCatching { Instant.parse(isoValue.trim()).toEpochMilli() }.getOrNull()
