package com.example.movilsdd.ui.feature.login

import app.cash.turbine.test
import com.example.movilsdd.common.DomainError
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.domain.model.LoginRequest
import com.example.movilsdd.domain.usecase.LoginUseCase
import com.example.movilsdd.shared.TestDataFactory
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    private lateinit var loginUseCase: LoginUseCase

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        loginUseCase = mockk()
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsEmpty() = runTest {
        LoginViewModel(loginUseCase = loginUseCase).uiState.test {
            assertEquals(LoginUiState(), awaitItem())
        }
    }

    @Test
    fun usernameInputUpdatesState() = runTest {
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.uiState.test {
            assertEquals(LoginUiState(), awaitItem())
            viewModel.onEvent(LoginUiEvent.OnUsernameChanged(TestDataFactory.VALID_USERNAME))
            assertEquals(TestDataFactory.VALID_USERNAME, awaitItem().username)
        }
    }

    @Test
    fun passwordInputUpdatesState() = runTest {
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.uiState.test {
            assertEquals(LoginUiState(), awaitItem())
            viewModel.onEvent(LoginUiEvent.OnPasswordChanged(TestDataFactory.VALID_PASSWORD))
            assertEquals(TestDataFactory.VALID_PASSWORD, awaitItem().password)
        }
    }

    @Test
    fun togglePasswordVisibilityFlipsFlag() = runTest {
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.uiState.test {
            assertEquals(LoginUiState(), awaitItem())
            viewModel.onEvent(LoginUiEvent.OnTogglePasswordVisibility)
            assertTrue(awaitItem().isPasswordVisible)
            viewModel.onEvent(LoginUiEvent.OnTogglePasswordVisibility)
            assertFalse(awaitItem().isPasswordVisible)
        }
    }

    @Test
    fun loginSuccessEmitsNavigation() = runTest {
        val response = TestDataFactory.loginResponse()
        coEvery { loginUseCase(any()) } returns SealedResult.Success(response)
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.onEvent(LoginUiEvent.OnUsernameChanged(TestDataFactory.VALID_USERNAME))
        viewModel.onEvent(LoginUiEvent.OnPasswordChanged(TestDataFactory.VALID_PASSWORD))
        viewModel.events.test {
            viewModel.onEvent(LoginUiEvent.OnLoginClick)
            assertEquals(LoginUiEvent.OnLoginSuccess(TestDataFactory.VALID_USERNAME), awaitItem())
        }
        assertFalse(viewModel.uiState.value.isLoading)
        val expected = LoginRequest(username = TestDataFactory.VALID_USERNAME, password = TestDataFactory.VALID_PASSWORD)
        coVerify(exactly = 1) { loginUseCase(expected) }
    }

    @Test
    fun loginErrorEmitsSnackbarAndMessage() = runTest {
        coEvery { loginUseCase(any()) } returns SealedResult.Failure(DomainError.Forbidden)
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.events.test {
            viewModel.onEvent(LoginUiEvent.OnLoginClick)
            val event = awaitItem()
            assertTrue(event is LoginUiEvent.ShowSnackbar)
            assertEquals("Credenciales inválidas", (event as LoginUiEvent.ShowSnackbar).message)
        }
        assertEquals("Credenciales inválidas", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun validationErrorSurfacesFieldMessage() = runTest {
        val message = "Usuario: usa 3–50 caracteres (letras, números, punto, guion)."
        coEvery { loginUseCase(any()) } returns SealedResult.Failure(DomainError.Validation(message))
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.events.test {
            viewModel.onEvent(LoginUiEvent.OnLoginClick)
            assertEquals(LoginUiEvent.ShowSnackbar(message), awaitItem())
        }
        assertEquals(message, viewModel.uiState.value.errorMessage)
    }

    @Test
    fun loadingGuardAllowsSingleCall() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        coEvery { loginUseCase(any()) } returns SealedResult.Success(TestDataFactory.loginResponse())
        val viewModel = LoginViewModel(loginUseCase = loginUseCase)
        viewModel.onEvent(LoginUiEvent.OnLoginClick)
        viewModel.onEvent(LoginUiEvent.OnLoginClick)
        assertTrue(viewModel.uiState.value.isLoading)
        advanceUntilIdle()
        coVerify(exactly = 1) { loginUseCase(any()) }
        assertFalse(viewModel.uiState.value.isLoading)
    }
}
