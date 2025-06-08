package com.example.trackrscope.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trackrscope.screens.auth.login.LoginScreen
import com.example.trackrscope.screens.auth.recoverpassword.RecoverPasswordScreen
import com.example.trackrscope.screens.auth.register.RegisterScreen
import com.example.trackrscope.screens.game.GameScreen
import com.example.trackrscope.screens.home.HomeScreen
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.screens.leaderboard.LeaderBoardScreen
import com.example.trackrscope.screens.news.NewsScreen
import com.example.trackrscope.screens.profile.editprofile.EditProfileScreen
import com.example.trackrscope.screens.profile.profile.ProfileScreen
import com.example.trackrscope.ui.theme.GameTheme
import com.example.trackrscope.viewmodels.GameThemeViewModel
import com.example.trackrscope.viewmodels.ThemeViewModel

/**
 * Navegación de la aplicación.
 *
 * @param isAuthenticated Indica si el usuario está autenticado.
 * @param themeViewModel [ThemeViewModel] para el tema de la aplicación.
 */
@Composable
fun NavigationGraph(isAuthenticated: Boolean, themeViewModel: ThemeViewModel) {
    // Defino el controlador de navegación y el estado del Snackbar
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val viewModel: HomeViewModel = hiltViewModel()

    // Obtengo la ruta actual
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    // Actualizo el tema según la ruta actual
    LaunchedEffect(currentDestination) {
        when (currentDestination) {
            Routes.LOGIN -> themeViewModel.setTheme(GameTheme.DEFAULT)
        }
    }

    val gameThemeViewModel: GameThemeViewModel = viewModel()
    val gameAndTheme by gameThemeViewModel.selectedGame.collectAsState()

    LaunchedEffect(currentDestination, gameAndTheme) {
        val game = gameAndTheme.first
        val theme = gameAndTheme.second
        if (currentDestination != Routes.LOGIN && currentDestination != Routes.REGISTER && currentDestination != Routes.SPLASH &&
            currentDestination != Routes.RECOVERPASSWORD && game != null && theme != null
        ) {
            when (theme) {
                "lol_theme" -> themeViewModel.setTheme(GameTheme.LOL)
                "valorant_theme" -> themeViewModel.setTheme(GameTheme.VALORANT)
                "tft_theme" -> themeViewModel.setTheme(GameTheme.TFT)
                "default_theme" -> themeViewModel.setTheme(GameTheme.DEFAULT)
                else -> themeViewModel.setTheme(GameTheme.DEFAULT)
            }
        }
    }

    val fabRoutes = listOf(Routes.HOME)

    val appRoutes = listOf(
        Routes.HOME,
        Routes.PROFILE,
        Routes.NEWS,
        Routes.GAME,
        Routes.LEADERBOARD
    )

    // Construyo la estructura de la aplicación
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = { if (currentDestination in appRoutes) BottomNavigationBar(navController = navController) },
        floatingActionButton = { if (currentDestination in fabRoutes) FloatingMenu(viewModel) }
    ) { innerPadding ->
        // Defino la navegación entre las pantallas
        NavHost(
            navController = navController,
            startDestination = if (isAuthenticated) Routes.HOME else Routes.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(navController = navController, snackbarHostState = snackbarHostState)
            }
            composable(Routes.REGISTER) {
                RegisterScreen(navController = navController)
            }
            composable(Routes.HOME) {
                HomeScreen(viewModel, navController)
            }
            composable(Routes.RECOVERPASSWORD) {
                RecoverPasswordScreen(
                    navController = navController,
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    navController = navController,
                    themeViewModel = themeViewModel,
                    gameThemeViewModel = gameThemeViewModel,
                    homeViewModel = viewModel
                )
            }
            composable(Routes.NEWS) {
                NewsScreen()
            }
            composable(Routes.GAME) {
                GameScreen()
            }
            composable(Routes.LEADERBOARD) {
                LeaderBoardScreen(navController = navController, viewModel)
            }
            composable(Routes.EDITPROFILE) {
                EditProfileScreen(navController = navController)
            }
            composable(Routes.SPLASH) {
                SplashScreen(navController = navController, isAuthenticated = isAuthenticated)
            }
        }
    }
}