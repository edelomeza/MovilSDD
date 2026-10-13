package com.example.movilsdd.data.remote

import com.example.movilsdd.data.remote.dto.LoginRequestDto
import com.example.movilsdd.data.remote.dto.LoginResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/v1/Auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<LoginResponseDto>

    @POST("api/v1/Auth/logout")
    suspend fun logout(@Header("Authorization") authorization: String): Response<Unit>
}
