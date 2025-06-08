package com.example.trackrscope.screens.game.components

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.trackrscope.R
import com.example.trackrscope.services.models.game.Champion
import com.example.trackrscope.services.models.game.ChampionSpell

/**
 * Composable para mostrar los detalles de un campeón en un diálogo.
 *
 * @param champion Objeto [Champion] que contiene los detalles del campeón.
 * @param version Versión de League of Legends.
 * @param onDismiss Función a ejecutar al cerrar el diálogo.
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChampionDetailsDialog(
    champion: Champion, version: String, onDismiss: () -> Unit
) {

    val pagerState = rememberPagerState(initialPage = 0) { champion.skins.size }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitle =
        listOf(stringResource(id = R.string.ally_tips), stringResource(id = R.string.enemy_tips))

    var showSpell by remember { mutableStateOf(false) }
    var showLore by remember { mutableStateOf(false) }
    var showTips by remember { mutableStateOf(false) }

    var selectedSpell by remember { mutableStateOf<ChampionSpell?>(null) }

    val configuration = LocalConfiguration.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Card(
                shape = CardDefaults.elevatedShape,
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {

                if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    LayoutHorizontal(
                        champion = champion,
                        version = version,
                        pagerState = pagerState,
                        selectedTabIndex = selectedTabIndex,
                        onTabChange = { selectedTabIndex = it },
                        onSpellClick = { spell ->
                            selectedSpell = spell
                            showSpell = true
                        },
                        onLoreClick = { showLore = true },
                        onTipsClick = { showTips = true },
                        tabTitles = tabTitle,
                        onDismiss = onDismiss
                    )
                } else if(configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    LayoutVertical(
                        champion = champion,
                        version = version,
                        pagerState = pagerState,
                        selectedTabIndex = selectedTabIndex,
                        onTabChange = { selectedTabIndex = it },
                        onSpellClick = { spell ->
                            selectedSpell = spell
                            showSpell = true
                        },
                        onLoreClick = { showLore = true },
                        onTipsClick = { showTips = true },
                        tabTitles = tabTitle,
                        onDismiss = onDismiss
                    )
                }

                if (showLore) {
                    ChampionComponents.ChampionLoreSheet(
                        champion = champion,
                        onDismiss = { showLore = false })
                }
                if (showSpell) {
                    ChampionComponents.ChampionSpellSheet(
                        champion = champion,
                        version = version,
                        spell = selectedSpell,
                        onDismiss = { selectedSpell = null })
                }
                if (showTips) {
                    ChampionComponents.ChampionTipsSheet(
                        tips = if (selectedTabIndex == 0) champion.allytips else champion.enemytips,
                        selected = if (selectedTabIndex == 0) tabTitle[0] else tabTitle[1],
                        onDismiss = { showTips = false })
                }
            }
        }
    }
}

@Composable
fun LayoutVertical(
    champion: Champion,
    version: String,
    pagerState: PagerState,
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit,
    onSpellClick: (ChampionSpell) -> Unit,
    onLoreClick: () -> Unit,
    onTipsClick: () -> Unit,
    tabTitles: List<String>,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ITEM SKINS
            item {
                ChampionComponents.ChampionsSkinsPager(
                    champion = champion,
                    pagerState = pagerState,
                    isWide = false,
                    onDismiss = onDismiss
                )
            }

            // ITEM CHAMPION INFO
            item {
                ChampionComponents.ChampionInfo(
                    champion = champion, onLoreClick = onLoreClick
                )

            }

            // ITEM TAGS
            item {
                ChampionComponents.ChampionTags(tags = champion.tags)
            }

            // ITEM SPELLS
            item {
                ChampionComponents.ChampionSpells(
                    champion = champion,
                    version = version,
                    onSpellClick = onSpellClick
                )
            }

            // ITEM TAB
            item {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { onTabChange(index) },
                            text = {
                                Text(
                                    text = title,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            })
                    }
                }
            }

            // ITEM TIP LIST
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    ChampionComponents.TipsList(
                        tips = if (selectedTabIndex == 0) champion.allytips else champion.enemytips,
                        onTipsClick = onTipsClick
                    )
                }
            }
        }
    }
}


@Composable
fun LayoutHorizontal(
    champion: Champion,
    version: String,
    pagerState: PagerState,
    selectedTabIndex: Int,
    onTabChange: (Int) -> Unit,
    onSpellClick: (ChampionSpell) -> Unit,
    onLoreClick: () -> Unit,
    onTipsClick: () -> Unit,
    tabTitles: List<String>,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // ITEM SKINS
            ChampionComponents.ChampionsSkinsPager(
                champion = champion, pagerState = pagerState, isWide = true, onDismiss = onDismiss
            )
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // ITEM CHAMPION INFO
            item {
                ChampionComponents.ChampionInfo(
                    champion = champion, onLoreClick = onLoreClick
                )
            }

            // ITEM TAGS
            item {
                ChampionComponents.ChampionTags(tags = champion.tags)
            }

            // ITEM SPELLS
            item {
                ChampionComponents.ChampionSpells(
                    champion = champion,
                    version = version,
                    onSpellClick = onSpellClick
                )
            }

            // ITEM TAB LAYOUT
            item {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { onTabChange(index) },
                            text = {
                                Text(
                                    text = title,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            })
                    }
                }
            }

            // ITEM TIP LIST
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    ChampionComponents.TipsList(
                        tips = if (selectedTabIndex == 0) champion.allytips else champion.enemytips,
                        onTipsClick = onTipsClick
                    )
                }
            }
        }
    }
}