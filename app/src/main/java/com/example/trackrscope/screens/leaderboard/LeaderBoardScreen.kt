package com.example.trackrscope.screens.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.trackrscope.R
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.screens.leaderboard.components.LeaderboardComponents
import com.example.trackrscope.utils.LoadingIndicator
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LeaderBoardScreen(navController: NavController, homeViewModel: HomeViewModel) {
    val viewModel: LeaderboardViewModel = hiltViewModel()

    val summoners by viewModel.paginatedSummoners.collectAsState()

    var showRegion by remember { mutableStateOf(false) }
    var showTier by remember { mutableStateOf(false) }
    var showDivision by remember { mutableStateOf(false) }

    val selectedRegion by viewModel.selectedRegion.collectAsState()
    val selectedTier by viewModel.selectedTier.collectAsState()
    val selectedDivision by viewModel.selectedDivision.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            viewModel.refreshData()
        }
    )

    val isAnonymous = FirebaseAuth.getInstance().currentUser?.isAnonymous

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // FILTROS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LeaderboardComponents.FilterButton(
                    text = selectedRegion,
                    modifier = Modifier.weight(1f),
                    onClick = { showRegion = true })

                LeaderboardComponents.FilterButton(
                    text = "",
                    icon = painterResource(id = LeaderboardComponents.getTierImage(selectedTier)),
                    modifier = Modifier.weight(1f),
                    onClick = { showTier = true })

                LeaderboardComponents.FilterButton(
                    text = selectedDivision,
                    modifier = Modifier.weight(1f),
                    onClick = { showDivision = true })
            }

            if (isLoading) {
                // PANTALLA DE CARGA
                LoadingIndicator()
            } else {
                // LISTA DE JUGADORES
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    itemsIndexed(summoners) { index, summoner ->
                        LeaderboardComponents.SummonerCard(
                            summoner = summoner,
                            index = index,
                            navController = navController,
                            viewModel = homeViewModel,
                            isAnonymous = isAnonymous == true
                        )
                    }
                }
            }
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        if (showRegion) {
            LeaderboardComponents.FilterAlertDialog(
                title = stringResource(id = R.string.select_region),
                options = viewModel.regions,
                selectedOption = selectedRegion,
                onOptionSelected = { region ->
                    viewModel.updateRegion(region)

                },
                onDismiss = { showRegion = false }
            )
        }

        if (showTier) {
            LeaderboardComponents.FilterAlertDialog(
                title = stringResource(id = R.string.select_tier),
                options = viewModel.tiers,
                selectedOption = selectedTier,
                onOptionSelected = { tier ->
                    viewModel.updateTier(tier)

                },
                onDismiss = { showTier = false },
                iconForOption = { tier ->
                    painterResource(
                        id = LeaderboardComponents.getTierImage(
                            tier
                        )
                    )
                }
            )
        }

        if (showDivision) {
            LeaderboardComponents.FilterAlertDialog(
                title = stringResource(id = R.string.select_division),
                options = viewModel.divisions,
                selectedOption = selectedDivision,
                onOptionSelected = { division ->
                    viewModel.updateDivision(division)

                },
                onDismiss = { showDivision = false }
            )
        }

    }
}