package com.example.movilsdd.domain.model

data class LoginRequest(val username: String = "", val password: String = "")

data class LoginResponse(val token: String = "", val username: String = "", val expiresAtMillis: Long = 0L)

data class UserSession(val username: String = "", val token: String = "", val expiresAtMillis: Long = 0L)
