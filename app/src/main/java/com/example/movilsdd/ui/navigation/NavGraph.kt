package com.example.movilsdd.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.movilsdd.ui.theme.MovilSDDTheme

@Composable
fun NavGraph(modifier: Modifier = Modifier) {
    val controller = rememberNavController()
    NavHost(navController = controller, startDestination = Home, modifier = modifier) {
        composable<Home> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "MovilSDD")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavGraphPreview() {
    MovilSDDTheme {
        NavGraph()
    }
}
