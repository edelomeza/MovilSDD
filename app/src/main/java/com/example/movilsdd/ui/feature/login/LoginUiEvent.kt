package com.example.movilsdd.ui.feature.login

sealed interface LoginUiEvent {
    data class OnUsernameChanged(val value: String) : LoginUiEvent

    data class OnPasswordChanged(val value: String) : LoginUiEvent

    data object OnTogglePasswordVisibility : LoginUiEvent

    data object OnLoginClick : LoginUiEvent

    data class OnLoginSuccess(val username: String) : LoginUiEvent

    data class ShowSnackbar(val message: String) : LoginUiEvent
}
