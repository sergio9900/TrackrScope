@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.trackrscope.screens.game.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.trackrscope.R
import com.example.trackrscope.services.models.game.Champion
import com.example.trackrscope.services.models.game.ChampionSpell
import com.example.trackrscope.utils.HtmlTextView

/**
 * Objeto que contiene los componentes relacionados con el juego.
 */
object ChampionComponents {


    /**
     * Componente para mostrar las habilidades de un campeón.
     *
     * @param champion Objeto [Champion] que contiene los detalles del campeón.
     * @param version Versión de League of Legends.
     * @param onSpellClick Función a ejecutar al hacer clic en una habilidad.
     */
    @Composable
    fun ChampionSpells(champion: Champion, version: String, onSpellClick: (ChampionSpell) -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .background(
                    MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpellIcon(
                    imageUrl = "https://ddragon.leagueoflegends.com/cdn/$version/img/passive/${champion.passive.image.full}",
                    name = champion.passive.name,
                    onClick = { onSpellClick(champion.passive) })

                champion.spells.forEach { spell ->
                    SpellIcon(
                        imageUrl = "https://ddragon.leagueoflegends.com/cdn/$version/img/spell/${spell.image.full}",
                        name = spell.name,
                        onClick = { onSpellClick(spell) })
                }
            }
        }
    }

    /**
     * Componente para mostrar un icono de una habilidad.
     *
     * @param imageUrl URL de la imagen de la habilidad.
     * @param name Nombre de la habilidad.
     * @param onClick Función a ejecutar al hacer clic en el icono.
     */
    @Composable
    fun SpellIcon(imageUrl: String, name: String, onClick: () -> Unit) {

        val context = LocalContext.current
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()

        Card(
            shape = CardDefaults.elevatedShape,
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier
                .size(56.dp)
                .clickable {
                    onClick()
                }) {
            Image(
                painter = rememberAsyncImagePainter(model = imageRequest),
                contentDescription = name,
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    /**
     * Componente para mostrar un ModalSheet.
     *
     * @param champion Objeto [Champion] que contiene los detalles del campeón.
     * @param spell Objeto [ChampionSpell] que contiene los detalles de la habilidad.
     * @param version Versión de League of Legends.
     * @param onDismiss Función a ejecutar al cerrar el ModalSheet.
     */
    @Composable
    fun ChampionSpellSheet(
        champion: Champion, spell: ChampionSpell?, version: String, onDismiss: () -> Unit
    ) {

        if (spell == null) return

        val rawDescription = spell.description
        val formatted = rawDescription
            .replace("<physicalDamage>", "<font color='#FF4500'><b>")
            .replace("</physicalDamage>", "</b></font>")
            .replace("<magicDamage>", "<font color='#1E90FF'><b>")
            .replace("</magicDamage>", "</b></font>")
            .replace("<trueDamage>", "<font color='#A020F0'><b>")
            .replace("</trueDamage>", "</b></font>")
            .replace("<br>", "<br/>")

        val isPassive = spell == champion.passive
        val imageUrl = if (isPassive) {
            "https://ddragon.leagueoflegends.com/cdn/$version/img/passive/${spell.image.full}"
        } else {
            "https://ddragon.leagueoflegends.com/cdn/$version/img/spell/${spell.image.full}"
        }

        val context = LocalContext.current
        val imageRequest = ImageRequest.Builder(context)
            .data(imageUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .crossfade(true)
            .build()

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(bottom = 24.dp, start = 24.dp, end = 24.dp, top = 16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = spell.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(imageRequest),
                    contentDescription = spell.name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                HtmlTextView(
                    html = formatted,
                    modifier = Modifier.fillMaxWidth(),
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

                if (!isPassive) {
                    Spacer(modifier = Modifier.height(16.dp))

                    spell.cooldown.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = "Cooldown: ${it.joinToString(" / ")}",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    spell.cost.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = "Cost: ${it.joinToString(" / ")}",
                            style = MaterialTheme.typography.labelLarge,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    /**
     * Componente para mostrar las skins de un campeón.
     *
     * @param champion Objeto [Champion] que contiene los detalles del campeón.
     * @param pagerState Estado del Pager.
     * @param isWide Indica si el Pager es ancho.
     * @param onDismiss Función a ejecutar al cerrar el Pager.
     */
    @Composable
    fun ChampionsSkinsPager(
        champion: Champion,
        pagerState: PagerState,
        isWide: Boolean,
        onDismiss: () -> Unit
    ) {

        var skinName: String by remember { mutableStateOf("") }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val skin = champion.skins[page]
                skinName = champion.skins[page].name

                val imageUrl =
                    "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${champion.id}_${skin.num}.jpg"

                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()
                    ),
                    contentDescription = champion.name,
                    modifier = Modifier.fillMaxSize()
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = if (isWide) Modifier.align(Alignment.TopStart) else Modifier.align(
                    Alignment.TopEnd
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .align(Alignment.BottomCenter)
            ) {
                repeat(champion.skins.size) { index ->
                    val color =
                        if (pagerState.currentPage == index) MaterialTheme.colorScheme.secondary else Color.White
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(color, shape = RoundedCornerShape(50))
                    )
                }
            }
        }

        if (isWide) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (skinName == "default") champion.name else champion.skins[pagerState.currentPage].name,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }
    }

    /**
     * Componente para mostrar la información del campeón.
     *
     * @param champion Objeto [Champion] que contiene los detalles del campeón.
     * @param onLoreClick Función a ejecutar al hacer clic en el botón de descripción.
     */
    @Composable
    fun ChampionInfo(champion: Champion, onLoreClick: () -> Unit) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = champion.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 24.sp,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(4.dp)
            )
            Text(
                text = champion.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)

            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = champion.lore,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLoreClick() },
                            onDoubleTap = { onLoreClick() }
                        )
                    }
                    .padding(8.dp)
            )
        }
    }

    /**
     * Componente para mostrar la descripción del campeón.
     *
     * @param champion Objeto [Champion] que contiene los detalles del campeón.
     * @param onDismiss Función a ejecutar al cerrar el ModalSheet.
     */
    @Composable
    fun ChampionLoreSheet(champion: Champion, onDismiss: () -> Unit) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = champion.name,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(4.dp)
                    )
                    Text(
                        text = champion.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(8.dp)
                    )
                    Text(
                        text = champion.lore,
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    ChampionTags(tags = champion.tags)
                }
            }
        }
    }

    /**
     * Componente para mostrar los roles de un campeón.
     *
     * @param tags Lista de roles del campeón.
     */
    @Composable
    fun ChampionTags(tags: List<String>) {

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.roles),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                tags.forEach { tag ->
                    AssistChip(
                        onClick = {},
                        label = { Text(tag) },
                        leadingIcon = { getRole(tag) },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    /**
     * Componente para mostrar el icono de un rol.
     *
     * @param tag Nombre del rol.
     * @return Componente [Image] con el icono del rol.
     */
    @Composable
    fun getRole(tag: String) = when (tag.lowercase()) {
        "fighter" -> Image(
            painter = painterResource(id = R.drawable.ic_fighter),
            contentDescription = "Fighter",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        "mage" -> Image(
            painter = painterResource(id = R.drawable.ic_mage),
            contentDescription = "Mage",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        "tank" -> Image(
            painter = painterResource(id = R.drawable.ic_tank),
            contentDescription = "Tank",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        "assassin" -> Image(
            painter = painterResource(id = R.drawable.ic_assassin),
            contentDescription = "Assassin",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        "marksman" -> Image(
            painter = painterResource(id = R.drawable.ic_marksman),
            contentDescription = "Marksman",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        "support" -> Image(
            painter = painterResource(id = R.drawable.ic_support),
            contentDescription = "Support",
            colorFilter = ColorFilter.tint(
                MaterialTheme.colorScheme.onSurface
            )
        )

        else -> Icons.Default.Refresh
    }

    /**
     * Componente para mostrar los tips de un campeón.
     *
     * @param tips Lista de tips del campeón.
     * @param onTipsClick Función a ejecutar al hacer clic en los tips.
     */
    @Composable
    fun TipsList(tips: List<String>, onTipsClick: () -> Unit) {
        if (tips.isEmpty()) {
            Text(
                "No hay consejos disponibles.", modifier = Modifier.padding(8.dp)
            )
        } else {
            Column(modifier = Modifier.padding(8.dp)) {
                tips.forEach { tip ->
                    Text(
                        "• $tip",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { onTipsClick() },
                                onDoubleTap = { onTipsClick() }
                            )
                        }
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }

    /**
     * Componente para mostrar un ModalSheet.
     *
     * @param tips Lista de tips del campeón.
     * @param selected Nombre del rol seleccionado.
     * @param onDismiss Función a ejecutar al cerrar el ModalSheet.
     */
    @Composable
    fun ChampionTipsSheet(
        tips: List<String>, selected: String, onDismiss: () -> Unit
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = selected,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                tips.forEach { tip ->
                    Text(
                        "• $tip",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
            }
        }
    }
}