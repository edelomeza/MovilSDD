package com.example.movilsdd.util

import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult

private const val MIN_USERNAME_LENGTH = 3
private const val MAX_USERNAME_LENGTH = 50
private const val MIN_PASSWORD_LENGTH = 8
private const val MAX_PASSWORD_LENGTH = 128
private val UsernamePattern = Regex("^[A-Za-z0-9._-]{3,50}$")

fun validateUsername(username: String): SealedResult<Unit> {
    val inRange = username.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH
    if (!inRange || !UsernamePattern.matches(username)) {
        return SealedResult.Failure(DomainError.Validation("Usuario: usa 3–50 caracteres (letras, números, punto, guion)."))
    }
    return SealedResult.Success(Unit)
}

fun validatePassword(password: String): SealedResult<Unit> {
    if (password.length !in MIN_PASSWORD_LENGTH..MAX_PASSWORD_LENGTH) {
        return SealedResult.Failure(DomainError.Validation("Contraseña: usa 8–128 caracteres."))
    }
    return SealedResult.Success(Unit)
}
