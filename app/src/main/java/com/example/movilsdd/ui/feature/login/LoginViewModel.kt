package com.example.movilsdd.ui.feature.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.usecase.LoginUseCase
import java.io.IOException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TOO_MANY_REQUESTS_CODE = 429

class LoginViewModel(private val loginUseCase: LoginUseCase) : ViewModel() {
    private val states = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = states.asStateFlow()

    private val outcomes = MutableSharedFlow<LoginUiEvent>()
    val events: SharedFlow<LoginUiEvent> = outcomes.asSharedFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnUsernameChanged -> states.update { it.copy(username = event.value, errorMessage = null) }
            is LoginUiEvent.OnPasswordChanged -> states.update { it.copy(password = event.value, errorMessage = null) }
            LoginUiEvent.OnTogglePasswordVisibility -> states.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginUiEvent.OnLoginClick -> submitLogin()
            is LoginUiEvent.OnLoginSuccess -> Unit
            is LoginUiEvent.ShowSnackbar -> Unit
        }
    }

    private fun submitLogin() {
        val current = states.value
        if (current.isLoading) return
        states.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = loginUseCase(LoginRequest(username = current.username, password = current.password))
            when (result) {
                is SealedResult.Success -> {
                    states.update { it.copy(isLoading = false) }
                    outcomes.emit(LoginUiEvent.OnLoginSuccess(result.data.username))
                }
                is SealedResult.Failure -> {
                    val message = result.error.toUserMessage()
                    states.update { it.copy(isLoading = false, errorMessage = message) }
                    outcomes.emit(LoginUiEvent.ShowSnackbar(message))
                }
            }
        }
    }

    private fun DomainError.toUserMessage(): String = when (this) {
        is DomainError.Validation -> message
        DomainError.Forbidden -> INVALID_CREDENTIALS_MESSAGE
        is DomainError.Conflict -> if (code == TOO_MANY_REQUESTS_CODE) TOO_MANY_REQUESTS_MESSAGE else SERVER_ERROR_MESSAGE
        DomainError.NotFound -> SERVER_ERROR_MESSAGE
        is DomainError.Unexpected -> if (cause is IOException) NETWORK_ERROR_MESSAGE else SERVER_ERROR_MESSAGE
    }

    private companion object {
        const val INVALID_CREDENTIALS_MESSAGE = "Credenciales inválidas"
        const val TOO_MANY_REQUESTS_MESSAGE = "Demasiados intentos. Intenta en unos minutos"
        const val SERVER_ERROR_MESSAGE = "Error del servidor. Intenta más tarde"
        const val NETWORK_ERROR_MESSAGE = "No logramos conectarnos. Por favor, verifica tu conexión a internet o inténtalo más tarde."
    }
}
