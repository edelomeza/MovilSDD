package com.example.movilsdd.data.remote

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.data.remote.dto.LoginRequestDto
import com.example.movilsdd.data.remote.dto.LoginResponseDto
import com.example.movilsdd.util.Logger
import java.io.IOException
import java.net.SocketTimeoutException
import retrofit2.HttpException

private const val HTTP_UNAUTHORIZED = 401
private const val HTTP_FORBIDDEN = 403
private const val HTTP_NOT_FOUND = 404
private const val HTTP_CONFLICT = 409
private const val HTTP_TOO_MANY_REQUESTS = 429
private const val REMOTE_TAG = "AuthRemote"

interface AuthRemoteDataSource {
    suspend fun login(body: LoginRequestDto): SealedResult<LoginResponseDto>

    suspend fun logout(authorization: String): SealedResult<Unit>
}

class AuthRemoteDataSourceImpl(private val api: AuthApiService) : AuthRemoteDataSource {
    override suspend fun login(body: LoginRequestDto): SealedResult<LoginResponseDto> = try {
        val response = api.login(body)
        if (response.isSuccessful) {
            val payload = response.body()
            if (payload == null) SealedResult.Failure(DomainError.Unexpected()) else SealedResult.Success(payload)
        } else {
            SealedResult.Failure(mapHttpCode(response.code()))
        }
    } catch (error: HttpException) {
        SealedResult.Failure(mapHttpCode(error.code()))
    } catch (error: SocketTimeoutException) {
        Logger.w(REMOTE_TAG, "login timed out")
        SealedResult.Failure(DomainError.Unexpected(error))
    } catch (error: IOException) {
        Logger.w(REMOTE_TAG, "login network unavailable")
        SealedResult.Failure(DomainError.Unexpected(error))
    }

    override suspend fun logout(authorization: String): SealedResult<Unit> = try {
        val response = api.logout(authorization)
        if (response.isSuccessful) SealedResult.Success(Unit) else SealedResult.Failure(mapHttpCode(response.code()))
    } catch (error: HttpException) {
        SealedResult.Failure(mapHttpCode(error.code()))
    } catch (error: SocketTimeoutException) {
        Logger.w(REMOTE_TAG, "logout timed out")
        SealedResult.Failure(DomainError.Unexpected(error))
    } catch (error: IOException) {
        Logger.w(REMOTE_TAG, "logout network unavailable")
        SealedResult.Failure(DomainError.Unexpected(error))
    }

    private fun mapHttpCode(code: Int): DomainError = when (code) {
        HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> DomainError.Forbidden
        HTTP_NOT_FOUND -> DomainError.NotFound
        HTTP_CONFLICT, HTTP_TOO_MANY_REQUESTS -> DomainError.Conflict(code)
        else -> DomainError.Unexpected()
    }
}
