package com.example.movilsdd.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.movilsdd.domain.repository.SessionManager
import com.example.movilsdd.domain.usecase.LogoutUseCase
import com.example.movilsdd.ui.feature.home.HomePlaceholderScreen
import com.example.movilsdd.ui.feature.login.LoginScreen
import kotlinx.coroutines.flow.first
import org.koin.compose.koinInject

@Composable
fun NavGraph(modifier: Modifier = Modifier, sessionManager: SessionManager = koinInject(), logoutUseCase: LogoutUseCase = koinInject()) {
    val controller = rememberNavController()
    var startRoute by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(sessionManager) {
        val session = sessionManager.sessionFlow.first()
        startRoute = if (session != null && sessionManager.isSessionValid()) {
            HomeRoute(username = session.username)
        } else {
            if (session != null) sessionManager.clearSession()
            LoginRoute()
        }
    }

    val route = startRoute
    if (route == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = controller, startDestination = route, modifier = modifier) {
            composable<LoginRoute> {
                LoginScreen(onLoginSuccess = { username ->
                    controller.navigate(HomeRoute(username = username)) {
                        popUpTo<LoginRoute> { inclusive = true }
                    }
                })
            }
            composable<HomeRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<HomeRoute>()
                HomePlaceholderScreen(username = args.username, onLogout = { logoutUseCase() })
            }
        }
    }
}
