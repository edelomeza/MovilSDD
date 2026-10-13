package com.example.movilsdd.common

sealed interface DomainError {
    data object NotFound : DomainError
    data object Forbidden : DomainError
    data class Conflict(val code: Int = 409) : DomainError
    data class Validation(val message: String) : DomainError
    data class Unexpected(val cause: Throwable? = null) : DomainError
}

sealed interface SealedResult<out T> {
    data class Success<T>(val data: T) : SealedResult<T>
    data class Failure(val error: DomainError) : SealedResult<Nothing>
}
