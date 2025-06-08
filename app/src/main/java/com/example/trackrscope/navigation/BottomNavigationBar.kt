package com.example.trackrscope.navigation


import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.trackrscope.R

/**
 * Bottom navigation bar con navegación basada en rutas.
 *
 * @param navController Controlador de navegación.
 */
@Composable
fun BottomNavigationBar(navController: NavController) {

    // lista de rutas de navegación para el BottomNavigationBar
    val items = listOf(
        Routes.NEWS,
        Routes.LEADERBOARD,
        Routes.HOME,
        Routes.GAME,
        Routes.PROFILE
    )

    // obtiene la ruta actual de la navegación
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Navegación inferior
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.height(70.dp)
    ) {
        items.forEach { screen ->
            // Nombre de la ruta
            val label = when (screen) {
                Routes.NEWS -> stringResource(R.string.news_screen)
                Routes.LEADERBOARD -> stringResource(R.string.leaderboard_screen)
                Routes.HOME -> stringResource(R.string.home_screen)
                Routes.GAME -> stringResource(R.string.game_screen)
                Routes.PROFILE -> stringResource(R.string.profile_screen)
                else -> ""
            }

            // Icono de la ruta
            val icon = when (screen) {
                Routes.NEWS -> Icons.Filled.Info
                Routes.LEADERBOARD -> Icons.Filled.Star
                Routes.HOME -> Icons.Filled.Home
                Routes.GAME -> Icons.Filled.PlayArrow
                Routes.PROFILE -> Icons.Filled.Person
                else -> Icons.Filled.Warning
            }

            val contentDescription = label

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                },
                label = {
                    Text(label, maxLines = 1, fontSize = 10.sp)
                },
                selected = screen == navController.currentBackStackEntryAsState().value?.destination?.route,
                onClick = {
                    if (screen != currentRoute) {
                        navController.navigate(screen) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}