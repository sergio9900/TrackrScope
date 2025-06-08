package com.example.trackrscope.screens.leaderboard.components

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.services.entities.SummonerFullEntity

object LeaderboardComponents {

    /**
     * Botón de filtro.
     *
     * @param modifier el modificador.
     * @param text el texto del botón.
     * @param icon el icono del botón.
     * @param onClick la acción a realizar cuando se hace clic en el botón.
     */
    @Composable
    fun FilterButton(
        modifier: Modifier = Modifier,
        text: String,
        icon: Painter? = null,
        onClick: () -> Unit
    ) {
        Button(
            modifier = modifier.height(50.dp),
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            if (icon != null) {
                Image(
                    painter = icon,
                    contentDescription = text,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(text = text)
        }
    }

    /**
     * Muestra un diálogo de selección de filtros.
     *
     * @param title el título del diálogo.
     * @param options las opciones del diálogo.
     * @param selectedOption la opción seleccionada.
     * @param onOptionSelected la acción a realizar cuando se selecciona una opción.
     * @param onDismiss la acción a realizar cuando se cierra el diálogo.
     */
    @Composable
    fun FilterAlertDialog(
        title: String, options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit,
        onDismiss: () -> Unit,
        iconForOption: @Composable ((String) -> Painter)? = null
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            title = {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(text = title, modifier = Modifier.align(Alignment.Center))
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    options.forEach { option ->
                        FilterChip(
                            modifier = Modifier
                                .fillMaxWidth(),
                            leadingIcon = iconForOption?.let { getIcon ->
                                {
                                    val painter = getIcon(option)
                                    Image(
                                        painter = painter,
                                        contentDescription = option,
                                        modifier = Modifier.size(50.dp)
                                    )
                                }
                            },
                            selected = selectedOption == option,
                            onClick = {
                                onOptionSelected(option)
                                onDismiss()
                            },
                            label = { Text(text = option) }
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(8.dp)
        )
    }

    /**
     * Muestra la tarjeta de un jugador.
     *
     * @param summoner el jugador a mostrar.
     * @param index el índice de la tarjeta.
     * @param navController el controlador de navegación.
     * @param viewModel el ViewModel de la pantalla.
     * @param isAnonymous si el jugador es anónimo.
     */
    @Composable
    fun SummonerCard(
        summoner: SummonerFullEntity,
        index: Int,
        navController: NavController,
        viewModel: HomeViewModel,
        isAnonymous: Boolean
    ) {

        LaunchedEffect(Unit) {
            viewModel.comprobarFavoritos(summoner)
        }

        val docId = "${summoner.account.gameName}#${summoner.account.tagLine}"

        var selected by remember { mutableStateOf(false) }
        val isFavorite = viewModel.favoritos[docId] == true

        LaunchedEffect(isFavorite) {
            selected = isFavorite
        }

        val backgoundColor = when (index) {
            0 -> colorResource(id = R.color.primero)
            1 -> colorResource(id = R.color.segundo)
            2 -> colorResource(id = R.color.tercero)
            else -> MaterialTheme.colorScheme.surface
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.fetchSummonerProfile(
                        summoner.account.gameName,
                        summoner.account.tagLine
                    )
                    navController.navigate(Routes.HOME)
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = backgoundColor)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                /* NEW PLAYER */
                if (summoner.ranked.freshBlood) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_newplayer),
                        contentDescription = stringResource(id = R.string.new_player),
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(48.dp)) {
                        /* PROFILE ICON */
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://ddragon.leagueoflegends.com/cdn/15.10.1/img/profileicon/${summoner.profile.profileIconId}.png")
                                    .crossfade(true)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .memoryCachePolicy(CachePolicy.ENABLED)
                                    .build()
                            ),
                            contentDescription = summoner.account.gameName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .align(Alignment.Center)
                        )

                        if (summoner.ranked.inactive) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_inactive),
                                contentDescription = stringResource(id = R.string.inactive),
                                modifier = Modifier
                                    .size(16.dp)
                                    .align(Alignment.BottomEnd)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = summoner.account.gameName.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "#${summoner.account.tagLine}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            if (summoner.ranked.hotStreak) {
                                FireAnimation(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .align(Alignment.CenterVertically)
                                )
                            }
                        }
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            Text("${summoner.ranked.tier} ${summoner.ranked.rank} - ${summoner.ranked.leaguePoints} LP")
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Text("W: ${summoner.ranked.wins} / L: ${summoner.ranked.losses}")

                            Text(
                                "WR: ${getWinRate(summoner.ranked.wins, summoner.ranked.losses)}"
                            )
                        }
                    }
                    if (!isAnonymous) {
                        Box {
                            IconButton(
                                onClick = {
                                    selected = !selected
                                    if (selected) {
                                        viewModel.addFavorites(summoner)
                                    } else {
                                        viewModel.removeFavorites(summoner)
                                    }
                                    viewModel.favoritos as MutableMap<String, Boolean>
                                    viewModel.favoritos[docId] = selected
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (selected) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorites"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Obtiene el porcentaje de victorias
     */
    @SuppressLint("DefaultLocale")
    fun getWinRate(wins: Int, losses: Int): String {

        val total = wins + losses
        if (total == 0) {
            return "0.00%"
        }

        val winRate = wins.toFloat() / total * 100f

        return String.format("%.2f%%", winRate)
    }

    /**
     * Obtiene la imagen del tier
     */
    @DrawableRes
    fun getTierImage(tier: String): Int {
        return when (tier.uppercase()) {
            "IRON" -> R.drawable.tier_iron
            "BRONZE" -> R.drawable.tier_bronze
            "SILVER" -> R.drawable.tier_silver
            "GOLD" -> R.drawable.tier_gold
            "PLATINUM" -> R.drawable.tier_platinum
            "EMERALD" -> R.drawable.tier_emerald
            "DIAMOND" -> R.drawable.tier_diamond
            "MASTER" -> R.drawable.tier_master
            "GRANDMASTER" -> R.drawable.tier_grandmaster
            "CHALLENGER" -> R.drawable.tier_challenger
            else -> R.drawable.tier_unraked
        }
    }

    /**
     * Muestra la animación de fuego
     */
    @Composable
    fun FireAnimation(modifier: Modifier = Modifier) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset("fire.json"))
        val progress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever,
        )

        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = modifier
        )
    }
}