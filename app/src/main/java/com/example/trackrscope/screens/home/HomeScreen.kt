package com.example.trackrscope.screens.home

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.trackrscope.R
import com.example.trackrscope.screens.home.components.HomeComponets
import com.example.trackrscope.utils.LoadingIndicator
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    navController: NavController
) {

    val state by viewModel.uiState.collectAsState()
    val version by viewModel.version

    val favoriteChampion = state.champions.firstOrNull()

    var selectedTab by remember { mutableStateOf("matches") }

    val isLoading by viewModel.isLoading.collectAsState()

    val isAnonymous = FirebaseAuth.getInstance().currentUser?.isAnonymous

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = {
            viewModel.fetchSummonerProfile(state.profile?.name ?: "", state.profile?.tag ?: "")
        }
    )

    var isProfileFavorite by remember { mutableStateOf(false) }
    val currentProfile = state.profile
    val docId = if (currentProfile != null) "${currentProfile.name}#${currentProfile.tag}" else ""

    LaunchedEffect(currentProfile) {
        if (currentProfile != null) {
            viewModel.comprobarFavoritosPerfil(currentProfile)
        }
    }

    LaunchedEffect(viewModel.favoritos[docId]) {
        isProfileFavorite = viewModel.favoritos[docId] == true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {

        /* Contenedor principal */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {

            if (state.error != null) {
                Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
            }

            if (!state.isLoading && state.profile == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = if (isSystemInDarkTheme()) painterResource(id = R.drawable.logo_dark) else painterResource(
                                id = R.drawable.logo_light
                            ),
                            contentDescription = "logo",
                            modifier = Modifier.size(360.dp)
                        )
                        Spacer(modifier = Modifier.size(24.dp))
                        Text(
                            text = stringResource(id = R.string.search_player_start),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }
                }
                return@Column
            }

            if (state.isLoading) {
                LoadingIndicator()
            }

            /* DATOS DEL PERFIL */
            state.profile?.let { profile ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    HomeComponets.ProfileData(
                        profile = profile,
                        favoriteChampion = favoriteChampion?.id ?: "Aatrox",
                        version = version,
                    )

                    if (isAnonymous != true) {
                        IconButton(
                            onClick = {
                                isProfileFavorite = !isProfileFavorite
                                if (isProfileFavorite) {
                                    viewModel.addFavoriteProfile(profile)
                                } else {
                                    viewModel.removeFavoriteProfile(profile)
                                }
                                // Actualizar el mapa de favoritos
                                (viewModel.favoritos as MutableMap<String, Boolean>)[docId] =
                                    isProfileFavorite
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                imageVector = if (isProfileFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

            }
            Spacer(modifier = Modifier.height(8.dp))
            /* ESTADISTICAS DEL JUGADOR */
            state.stats?.let {
                HomeComponets.ProfileStats(stats = it)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = stringResource(id = R.string.recent_matches),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            selectedTab = "matches"
                        },
                    color = if (selectedTab == "matches") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(id = R.string.champions_played),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }) {
                            selectedTab = "champions"
                        },
                    color = if (selectedTab == "champions") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Crossfade(targetState = selectedTab, label = "tab-content") { tab ->
                when (tab) {
                    "matches" -> HomeComponets.RecentMatches(
                        matches = state.matches,
                        version = version,
                        summonerPuuid = state.profile?.puuid ?: "",
                        navController = navController,
                        viewModel = viewModel
                    )

                    "champions" -> HomeComponets.ChampionsPlayed(
                        masteries = state.champions
                    )
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}