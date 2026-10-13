package com.example.movilsdd.ui.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movilsdd.R
import com.example.movilsdd.ui.theme.MovilSDDTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

private val FormSpacing = 16.dp
private val LoadingIndicatorSize = 24.dp

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit, modifier: Modifier = Modifier, viewModel: LoginViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is LoginUiEvent.OnLoginSuccess -> onLoginSuccess(event.username)
                is LoginUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                else -> Unit
            }
        }
    }

    Scaffold(modifier = modifier.fillMaxSize(), snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { innerPadding ->
        LoginContent(
            uiState = uiState,
            onEvent = viewModel::onEvent,
            modifier = Modifier.padding(innerPadding).padding(FormSpacing).fillMaxSize()
        )
    }
}

@Composable
private fun LoginContent(uiState: LoginUiState, onEvent: (LoginUiEvent) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = stringResource(R.string.login_title), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = uiState.username,
            onValueChange = { onEvent(LoginUiEvent.OnUsernameChanged(it)) },
            label = { Text(text = stringResource(R.string.login_username_label)) },
            leadingIcon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = stringResource(R.string.login_username_icon_desc)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().padding(top = FormSpacing)
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = { onEvent(LoginUiEvent.OnPasswordChanged(it)) },
            label = { Text(text = stringResource(R.string.login_password_label)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = stringResource(R.string.login_password_icon_desc)) },
            trailingIcon = {
                val visibilityLabel = if (uiState.isPasswordVisible) {
                    stringResource(R.string.login_hide_password)
                } else {
                    stringResource(R.string.login_show_password)
                }
                IconButton(onClick = { onEvent(LoginUiEvent.OnTogglePasswordVisibility) }) {
                    val icon = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                    Icon(imageVector = icon, contentDescription = visibilityLabel)
                }
            },
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth().padding(top = FormSpacing)
        )
        Button(
            onClick = { onEvent(LoginUiEvent.OnLoginClick) },
            enabled = !uiState.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth().padding(top = FormSpacing)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(LoadingIndicatorSize))
            } else {
                Text(text = stringResource(R.string.login_submit))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentPreview() {
    MovilSDDTheme {
        LoginContent(uiState = LoginUiState(username = "demo.user", password = "secret"), onEvent = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginContentLoadingPreview() {
    MovilSDDTheme {
        LoginContent(uiState = LoginUiState(username = "demo.user", password = "secret", isLoading = true), onEvent = {})
    }
}
