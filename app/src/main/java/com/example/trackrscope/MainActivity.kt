package com.example.trackrscope

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.example.trackrscope.navigation.NavigationGraph
import com.example.trackrscope.ui.theme.TrackrScopeTheme
import com.example.trackrscope.viewmodels.AuthViewModel
import com.example.trackrscope.viewmodels.ThemeViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

/**
 * Actividad principal de la aplicación.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels() // ViewModel para autenticación
    private val themeViewModel: ThemeViewModel by viewModels() // ViewModel para tema

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        FirebaseApp.initializeApp(this)

        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            val isDarkTheme = isSystemInDarkTheme()

            TrackrScopeTheme(gameTheme = currentTheme, isDarkTheme = isDarkTheme) {
                UpdateSystemBarStyle()
                Surface {
                    AppNavigation(authViewModel, themeViewModel)
                }
            }
        }
    }
}

/**
 * Actualiza el estilo de la barra de estado de la aplicación.
 */
@Composable
fun UpdateSystemBarStyle() {

    val view = LocalView.current
    val window = (view.context as? Activity)?.window ?: return
    val isDarkTheme = isSystemInDarkTheme()

    SideEffect {
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
    }
}

/**
 * Navegación de la aplicación.
 */
@Composable
fun AppNavigation(authViewModel: AuthViewModel, themeViewModel: ThemeViewModel) {
    // Observo el estado de autenticación del ViewModel
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    NavigationGraph(isAuthenticated = isAuthenticated, themeViewModel = themeViewModel)
}
