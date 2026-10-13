package com.example.movilsdd.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestDto(@SerialName("strNombreUsuario") val username: String = "", @SerialName("strPWD") val password: String = "")

@Serializable
data class LoginResponseDto(@SerialName("strToken") val token: String = "", @SerialName("dteExpira") val expiresAt: String = "")
