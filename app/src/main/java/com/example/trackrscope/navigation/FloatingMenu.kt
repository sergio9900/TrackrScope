package com.example.trackrscope.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.utils.PlayerDialog

/**
 * Botón flotante para mostrar el menú de opciones de juego.
 *
 * @param viewModel [HomeViewModel] de la pantalla principal.
 */
@Composable
fun FloatingMenu(viewModel: HomeViewModel) {

    var showSearch by remember { mutableStateOf(false) }

    if (showSearch) {
        PlayerDialog(
            onDismiss = { showSearch = false },
            onSearch = { gameName, tagLine ->
                showSearch = false
                viewModel.fetchSummonerProfile(gameName, tagLine)
            })
    }

    FloatingActionButton(
        onClick = { showSearch = true },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
        )
    }
}