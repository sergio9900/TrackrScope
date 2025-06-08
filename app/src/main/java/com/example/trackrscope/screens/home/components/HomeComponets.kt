package com.example.trackrscope.screens.home.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.trackrscope.R
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.services.models.match.ChampionMastery
import com.example.trackrscope.services.models.match.MatchDto
import com.example.trackrscope.services.models.match.ProfileStats
import com.example.trackrscope.services.models.match.SummonerUiProfile

object HomeComponets {

    /**
     * Componente para mostrar los datos del jugador.
     * @param profile los datos del jugador.
     */
    @Composable
    fun ProfileData(
        profile: SummonerUiProfile, favoriteChampion: String, version: String
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {

            /* CAMPEÓN FAVORITO */
            AsyncImage(
                model = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${favoriteChampion}_0.jpg",
                contentDescription = favoriteChampion,
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                modifier = Modifier.height(150.dp)
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 0f,
                            endY = 350f
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = rememberAsyncImagePainter("https://ddragon.leagueoflegends.com/cdn/$version/img/profileicon/${profile.profileIconId}.png"),
                    contentDescription = profile.name,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row {
                            Text(
                                profile.name,
                                style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                            )
                            Text(
                                "#${profile.tag}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.LightGray
                            )
                        }
                        Text(
                            profile.tier + " " + profile.rank,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = colorResource(id = getRankColor(profile.tier))
                            )
                        )
                        Text(
                            profile.summonerLevel.toString(),
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )

                    }
                }
            }
        }
    }

    /**
     * Componente para mostrar las estadísticas del jugador.
     * @param stats las estadísticas a mostrar.
     */
    @Composable
    fun ProfileStats(stats: ProfileStats) {
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
                StatItem(
                    title = stringResource(id = R.string.games),
                    value = stats.totalGames.toString()
                )
                StatItem(title = stringResource(id = R.string.LP), value = stats.lp.toString())
                StatItem(title = stringResource(id = R.string.KDA), value = stats.kda)
            }

            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    title = stringResource(id = R.string.wins),
                    value = stats.wins.toString(),
                    color = colorResource(id = R.color.victorias)
                )
                StatItem(
                    title = stringResource(id = R.string.losses),
                    value = stats.losses.toString(),
                    color = colorResource(id = R.color.derrotas)
                )
                StatItem(
                    title = stringResource(id = R.string.winrate),
                    value = stats.winRate,
                    color = getWinRateColor(stats.winRate)
                )
            }
        }
    }

    /**
     * Componente para mostrar una lista de partidos.
     */
    @Composable
    fun RecentMatches(
        matches: List<MatchDto>,
        version: String,
        summonerPuuid: String,
        navController: NavController,
        viewModel: HomeViewModel
    ) {
        var selectedMatch by remember { mutableStateOf<MatchDto?>(null) }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(matches, key = { it.metadata.matchId }) { match ->
                val player = remember(match) {
                    match.info.participants.find {
                        it.puuid.equals(
                            summonerPuuid,
                            ignoreCase = true
                        )
                    }
                } ?: return@items

                val championImage =
                    "https://ddragon.leagueoflegends.com/cdn/$version/img/champion/${
                        getChampionId(
                            player.championName
                        )
                    }.png"
                val summonerSpells = listOf(player.summoner1Id, player.summoner2Id)
                val itemIds = listOf(
                    player.item0,
                    player.item1,
                    player.item2,
                    player.item3,
                    player.item4,
                    player.item5,
                )
                val ward = player.item6

                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    onClick = { selectedMatch = match }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Imagen del campeón
                        Box {
                            ImageIcon(image = championImage, modifier = Modifier.size(48.dp))
                            Text(
                                text = player.champLevel.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .background(Color.Black, shape = CircleShape)
                                    .padding(2.dp)

                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = player.championName,
                                style = MaterialTheme.typography.bodyMedium,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                            Text(
                                text = "${player.kills}/${player.deaths}/${player.assists}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = if (player.win == true) stringResource(id = R.string.win) else stringResource(
                                    id = R.string.lose
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (player.win == true) colorResource(id = R.color.victorias) else colorResource(
                                    id = R.color.derrotas
                                )
                            )
                        }
                        val minions = player.totalMinionsKilled + player.neutralMinionsKilled
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = getGameMode(match.info.queueId),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                ->
                                InfoText(R.drawable.ic_minion, minions.toString())
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                InfoText(R.drawable.ic_coins, player.goldEarned.toString())
                            }
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            summonerSpells.forEach { spell ->
                                val spellName = getSummonerSpell[spell]
                                spellName?.let {
                                    val spellUrl =
                                        "https://ddragon.leagueoflegends.com/cdn/$version/img/spell/$it.png"
                                    ImageIcon(
                                        image = spellUrl,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            itemIds.chunked(3).forEach { chunk ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    chunk.forEach { item ->
                                        if (item != 0) {
                                            val itemUrl =
                                                "https://ddragon.leagueoflegends.com/cdn/$version/img/item/$item.png"
                                            ImageIcon(
                                                itemUrl,
                                                Modifier
                                                    .size(24.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                            )
                                        } else {
                                            EmptyItemSlot()
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(4.dp))
                        if (ward != 0) {
                            val wardUrl =
                                "https://ddragon.leagueoflegends.com/cdn/$version/img/item/$ward.png"
                            ImageIcon(
                                image = wardUrl,
                                Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }
            }
        }
        selectedMatch?.let {
            MatchDetailsDialog(
                match = it,
                version = version,
                navController = navController,
                viewModel = viewModel,
                onDismiss = { selectedMatch = null })
        }
    }

    @Composable
    fun ImageIcon(image: String, modifier: Modifier = Modifier) {

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(image)
                .crossfade(true)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .build()
        )

        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
        )
    }

    @Composable
    fun InfoText(@DrawableRes icon: Int, text: String) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(10.dp),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

    @Composable
    fun EmptyItemSlot(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
        )
    }


    /**
     * Componente para mostrar una lista de champions con su nivel de maestría.
     */
    @Composable
    fun ChampionsPlayed(masteries: List<ChampionMastery>) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(masteries) { mastery ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        // Imagen del campeón
                        ImageIcon(mastery.imageUrl, Modifier.size(72.dp))

                        Text(text = mastery.name, style = MaterialTheme.typography.bodyLarge)

                        // Imagen del nivel de maestría (ejemplo)
                        Image(
                            painter = painterResource(id = getMasteryImage(mastery.masteryLevel)),
                            contentDescription = "${stringResource(id = R.string.mastery)} ${mastery.masteryLevel}",
                            modifier = Modifier
                                .size(84.dp)
                        )

                        // Nombre y puntos
                        Text(
                            text = "${mastery.masteryPoints} ${stringResource(id = R.string.points)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }

    /**
     * Devuelve la imagen correspondiente a un nivel de maestría dado.
     */
    @DrawableRes
    fun getMasteryImage(mastery: Int): Int {
        if (mastery >= 10) return R.drawable.mastery_10

        return when (mastery) {
            0 -> R.drawable.mastery_0
            1 -> R.drawable.mastery_1
            2 -> R.drawable.mastery_2
            3 -> R.drawable.mastery_3
            4 -> R.drawable.mastery_4
            5 -> R.drawable.mastery_5
            6 -> R.drawable.mastery_6
            7 -> R.drawable.mastery_7
            8 -> R.drawable.mastery_8
            9 -> R.drawable.mastery_9
            else -> R.drawable.mastery_0
        }
    }

    /**
     * Componente para mostrar un item de estadística en el perfil.
     */
    @Composable
    fun StatItem(title: String, value: String, color: Color = Color.Gray) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleMedium)
            Text(title, style = MaterialTheme.typography.bodySmall, color = color)
        }
    }

    /**
     * Devuelve el color correspondiente a un rank dado.
     */
    fun getRankColor(rank: String): Int {
        return when (rank) {
            "IRON" -> R.color.hierro
            "BRONZE" -> R.color.bronce
            "SILVER" -> R.color.plata
            "GOLD" -> R.color.oro
            "PLATINUM" -> R.color.platino
            "EMERALD" -> R.color.esmeralda
            "DIAMOND" -> R.color.esmeralda
            "MASTER" -> R.color.master
            "GRANDMASTER" -> R.color.grandmaster
            "CHALLENGER" -> R.color.challenger
            else -> R.color.white
        }
    }

    /**
     * Devuelve el color correspondiente a un winRate dado.
     */
    fun getWinRateColor(winRate: String): Color {
        val rate = winRate.removeSuffix("%").toFloat() / 100f

        return when {
            rate <= 0.3f -> Color(1f, 0f, 0f)
            rate >= 0.7f -> Color(0f, 1f, 0f)
            else -> {
                val t = (rate - 0.3f) / 0.4f
                val red = 1f - t
                val green = t
                Color(red, green, 0f)
            }
        }
    }

    val getSummonerSpell = mapOf(
        1 to "SummonerBoost",
        3 to "SummonerExhaust",
        4 to "SummonerFlash",
        6 to "SummonerHaste",
        7 to "SummonerHeal",
        11 to "SummonerSmite",
        12 to "SummonerTeleport",
        13 to "SummonerMana",
        14 to "SummonerDot",
        21 to "SummonerBarrier",
        32 to "SummonerSnowball"
    )

    fun getChampionId(name: String): String {
        val championNameMap = mapOf(
            "Aurelion Sol" to "AurelionSol",
            "Cho'Gath" to "ChoGath",
            "Dr. Mundo" to "DrMundo",
            "Jarvan IV" to "JarvanIV",
            "Kai'Sa" to "Kaisa",
            "Kha'Zix" to "Khazix",
            "Kog'Maw" to "KogMaw",
            "Lee Sin" to "LeeSin",
            "Master Yi" to "MasterYi",
            "FiddleSticks" to "Fiddlesticks",
            "Miss Fortune" to "MissFortune",
            "Wukong" to "MonkeyKing",
            "Nunu & Willump" to "Nunu",
            "Rek'Sai" to "RekSai",
            "Tahm Kench" to "TahmKench",
            "Twisted Fate" to "TwistedFate",
            "Vel'Koz" to "Velkoz",
            "Xin Zhao" to "XinZhao",
            "LeBlanc" to "Leblanc"
        )

        return championNameMap[name] ?: name.replace(" ", "").replace(".", "").replace("'", "")
    }

    /**
     * Devuelve el modo de juego correspondiente a un queueId dado.
     *
     * @param queueId El queueId del modo de juego.
     * @return El modo de juego correspondiente.
     */
    fun getGameMode(queueId: Int): String {
        return when (queueId) {
            420 -> "Ranked Solo/Duo"
            440 -> "Ranked Flex"
            450 -> "ARAM"
            400 -> "Normal Draft"
            430 -> "Normal Blind"
            700 -> "Clash"
            830 -> "Co-op vs AI Intro"
            840 -> "Co-op vs AI Beginner"
            850 -> "Co-op vs AI Intermediate"
            900 -> "URF"
            1020 -> "One for All"
            1300 -> "Nexus Blitz"
            1400 -> "Ultimate Spellbook"
            1700 -> "Arena"
            1900 -> "URF"
            2000 -> "Tutorial"
            2010 -> "Tutorial"
            2020 -> "Tutorial"
            else -> "Modo Personalizado"
        }
    }
}