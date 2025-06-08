package com.example.trackrscope.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.screens.home.components.HomeComponets.ImageIcon
import com.example.trackrscope.screens.home.components.HomeComponets.getSummonerSpell
import com.example.trackrscope.services.models.match.MatchDto
import com.example.trackrscope.services.models.match.ObjectivesDto
import com.example.trackrscope.services.models.match.ParticipantDto

/**
 * Muestra un diálogo con los detalles de un partido.
 */
@Composable
fun MatchDetailsDialog(
    match: MatchDto,
    version: String,
    navController: NavController,
    viewModel: HomeViewModel,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = HomeComponets.getGameMode(match.info.queueId),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = (match.info.gameDuration / 60).toString() + ":" + (match.info.gameDuration % 60).toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    IconButton(
                        onClick = onDismiss,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }


                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val participants = match.info.participants
                    val team1 = participants.filter { it.teamId == 100 }
                    val team2 = participants.filter { it.teamId == 200 }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TeamHeader(stringResource(id = R.string.blue_team), Color(0xFF1E88E5))
                            Text(
                                text = if (team1[0].win) stringResource(id = R.string.win) else stringResource(
                                    id = R.string.lose
                                ),
                                color = Color(0xFF1E88E5),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }

                    item {
                        ObjetivesData(match.info.teams[0].objectives, Color(0xFF1E88E5))
                    }

                    items(team1) { participant ->
                        PlayerCard(participant, version, viewModel, navController)
                    }

                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TeamHeader(stringResource(id = R.string.red_team), Color(0xFFE53935))
                            Text(
                                text = if (team2[0].win) stringResource(id = R.string.win) else stringResource(
                                    id = R.string.lose
                                ),
                                color = Color(0xFFE53935),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }

                    item {
                        ObjetivesData(match.info.teams[1].objectives, Color(0xFFE53935))
                    }

                    items(team2) { participant ->
                        PlayerCard(participant, version, viewModel, navController)
                    }
                }
            }
        }
    }
}

/**
 * Muestra los objetivos de un equipo.
 *
 * @param objetivos Los objetivos del equipo.
 */
@Composable
private fun ObjetivesData(objetivos: ObjectivesDto, color: Color) {

    val drakes = objetivos.dragon.kills
    val heralds = objetivos.riftHerald.kills
    val barons = objetivos.baron.kills
    val torres = objetivos.tower.kills
    val inibidores = objetivos.inhibitor.kills
    val horde = objetivos.horde.kills
    val atakhan = objetivos.atakhan.kills

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            GetObjetive(painterResource(id = R.drawable.ic_nashor), barons.toString(), color)

            GetObjetive(painterResource(id = R.drawable.ic_dragon), drakes.toString(), color)

            GetObjetive(painterResource(id = R.drawable.ic_herald), heralds.toString(), color)

            GetObjetive(painterResource(id = R.drawable.ic_atakhan), atakhan.toString(), color)

            GetObjetive(painterResource(id = R.drawable.ic_grubs), horde.toString(), color)

            GetObjetive(painterResource(id = R.drawable.ic_turret), torres.toString(), color)

            GetObjetive(painterResource(id = R.drawable.ic_inhib), inibidores.toString(), color)
        }
    }
}

/**
 * Muestra el icono del objetivo.
 *
 * @param image El icono del objetivo.
 * @param text El texto del objetivo.
 * @param color El color del icono.
 */
@Composable
private fun GetObjetive(image: Painter, text: String, color: Color) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = image,
            contentDescription = text.toString(),
            colorFilter = ColorFilter.tint(color)
        )
        Text(text = text.toString(), fontWeight = FontWeight.Bold)
    }
}

/**
 * Muestra el encabezado de un equipo.
 */
@Composable
private fun TeamHeader(teamName: String, color: Color) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 24.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = teamName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

/**
 * Muestra una tarjeta de jugador.
 */
@Composable
private fun PlayerCard(
    player: ParticipantDto,
    version: String,
    viewModel: HomeViewModel,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.fetchSummonerProfile(player.riotIdGameName, player.riotIdTagline)
                navController.navigate(Routes.HOME)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Sección izquierda: Champion + Info del jugador
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                ChampionSection(player, version)
                PlayerInfoSection(player)
            }

            // Sección centro: Summoner Spells
            SummonerSpellsSection(player, version)

            // Sección derecha: Items + Ward
            ItemsSection(player, version)
        }
    }
}

/**
 * Muestra la sección del champion del jugador.
 */
@Composable
private fun ChampionSection(player: ParticipantDto, version: String) {
    val championImage by remember(player.championName, version) {
        mutableStateOf(
            "https://ddragon.leagueoflegends.com/cdn/$version/img/champion/${
                HomeComponets.getChampionId(
                    player.championName
                )
            }.png"
        )
    }

    Box {
        ImageIcon(image = championImage, modifier = Modifier.size(38.dp))
        Text(
            text = player.champLevel.toString(),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.Black, shape = CircleShape)
                .padding(2.dp),
            color = Color.White
        )
    }
}

/**
 * Muestra la sección de información del jugador.
 */
@Composable
private fun PlayerInfoSection(player: ParticipantDto) {
    val minions = player.totalMinionsKilled + player.neutralMinionsKilled

    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = player.riotIdGameName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${player.kills}", color = Color(0xFF4CAF50)) // Verde para kills
            Text("/", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${player.deaths}", color = Color(0xFFFF5722)) // Rojo para deaths
            Text("/", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("${player.assists}", color = Color(0xFF2196F3)) // Azul para assists
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${player.goldEarned}g",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFFFB300), // Dorado para oro
                fontWeight = FontWeight.Medium
            )
            Text(
                text = " • ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${minions}cs",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF9E9E9E) // Gris para CS
            )
        }
    }
}

/**
 * Muestra la sección de Summoner Spells del jugador.
 */
@Composable
private fun SummonerSpellsSection(player: ParticipantDto, version: String) {
    val summonerSpells = listOf(player.summoner1Id, player.summoner2Id)

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        summonerSpells.forEach { spell ->
            val spellName = getSummonerSpell[spell]
            spellName?.let {
                val spellUrl = "https://ddragon.leagueoflegends.com/cdn/$version/img/spell/$it.png"
                ImageIcon(
                    image = spellUrl,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

/**
 * Muestra la sección de Items del jugador.
 */
@Composable
private fun ItemsSection(player: ParticipantDto, version: String) {
    val itemIds = listOf(
        player.item0, player.item1, player.item2,
        player.item3, player.item4, player.item5
    )
    val ward = player.item6

    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Items principales en columnas
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            itemIds.chunked(3).forEach { chunk ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    chunk.forEach { item ->
                        if (item != 0) {
                            val itemUrl =
                                "https://ddragon.leagueoflegends.com/cdn/$version/img/item/$item.png"
                            ImageIcon(
                                image = itemUrl,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        } else {
                            HomeComponets.EmptyItemSlot()
                        }
                    }
                }
            }
        }

        // Ward separado
        if (ward != 0) {
            val wardUrl = "https://ddragon.leagueoflegends.com/cdn/$version/img/item/$ward.png"
            ImageIcon(
                image = wardUrl,
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}