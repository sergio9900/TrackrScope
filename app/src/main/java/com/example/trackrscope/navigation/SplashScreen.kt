package com.example.trackrscope.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.trackrscope.R

/**
 * Pantalla de splash.
 *
 * @param navController Controlador de navegación.
 * @param isAuthenticated Indica si el usuario está autenticado.
 */
@Composable
fun SplashScreen(navController: NavController, isAuthenticated: Boolean) {

    LaunchedEffect(Unit) {
        navController.navigate(if (isAuthenticated) Routes.HOME else Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Image(
                painter = if (isSystemInDarkTheme()) painterResource(id = R.drawable.logo_dark) else painterResource(
                    id = R.drawable.logo_light
                ),
                contentDescription = stringResource(id = R.string.app_name),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}