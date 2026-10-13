package com.example.movilsdd.ui.feature.home

import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.movilsdd.R
import com.example.movilsdd.common.SealedResult
import com.example.movilsdd.ui.theme.MovilSDDTheme
import kotlinx.coroutines.launch

private val ScreenPadding = 16.dp

@Composable
fun HomePlaceholderScreen(username: String, onLogout: suspend () -> SealedResult<Unit>, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize().padding(ScreenPadding)) {
        Text(text = "Bienvenido $username!!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "Usuario: $username", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.align(Alignment.Start))
        Button(
            onClick = {
                scope.launch {
                    onLogout()
                    (context as? Activity)?.finishAffinity()
                }
            },
            modifier = Modifier.padding(top = ScreenPadding)
        ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(R.string.home_logout_icon_desc))
            Text(text = stringResource(R.string.home_logout), modifier = Modifier.padding(start = ScreenPadding))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomePlaceholderPreview() {
    MovilSDDTheme {
        HomePlaceholderScreen(username = "demo.user", onLogout = { SealedResult.Success(Unit) })
    }
}
