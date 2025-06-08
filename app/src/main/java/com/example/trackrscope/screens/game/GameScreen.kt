package com.example.trackrscope.screens.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.trackrscope.screens.game.components.ChampionDetailsDialog
import com.example.trackrscope.services.models.game.toChampion
import com.example.trackrscope.utils.LoadingChampions
import com.example.trackrscope.utils.OutlinedTextFieldFilter
import com.example.trackrscope.utils.RolesFilter

/**
 * Composable que representa la pantalla de juego.
 */
@Composable
fun GameScreen() {

    val viewModel: GameViewModel = hiltViewModel()
    val champions by viewModel.champions
    val version by viewModel.version

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    val focusManager = LocalFocusManager.current
    val busqueda = remember { mutableStateOf("") }
    val roles =
        remember { listOf("Fighter", "Mage", "Assassin", "Marksman", "Support", "Tank") }
    var selectedRoles by remember { mutableStateOf(listOf<String>()) }
    var showRole by remember { mutableStateOf(false) }

    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {

        /* Contenedor principal */
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            /* Contenedor del formulario */
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                /* BUSCADOR */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextFieldFilter(
                        label = "",
                        value = busqueda.value,
                        onValueChange = { busqueda.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    )
                    Button(
                        onClick = {
                            showRole = true
                            focusManager.clearFocus()
                        }, colors = ButtonDefaults.buttonColors(
                            MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(if (selectedRoles.isEmpty()) "Roles" else selectedRoles.size.toString())
                    }
                }

                if (showRole) {
                    RolesFilter(
                        roles = roles,
                        selectedRole = selectedRoles,
                        onRoleSelected = { role ->
                            selectedRoles = if (role in selectedRoles) {
                                selectedRoles - role
                            } else {
                                selectedRoles + role
                            }
                        },
                        onClearRoles = { selectedRoles = emptyList() },
                        onDismiss = { showRole = false },
                    )
                }

                Spacer(modifier = Modifier.size(10.dp))
                /* CONTENIDO */
                val filteredList = champions.filter { champion ->
                    champion.name.contains(
                        busqueda.value,
                        ignoreCase = true
                    ) && (selectedRoles.isEmpty() || selectedRoles.all { role ->
                        role in champion.tags
                    })
                }

                if (isLoading) {
                    LoadingChampions()
                } else {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredList, key = { it.id }) { item ->
                            GameGridItem(
                                name = item.name,
                                imageUrl = "https://ddragon.leagueoflegends.com/cdn/${version}/img/champion/${item.image.full}",
                                onClick = { viewModel.loadChampionDetails(item.key) }
                            )
                        }
                    }
                }
            }
        }
        /* DIALOG PARA MOSTRAR UN CAMPEÓN */
        viewModel.selectedChampion.value?.let { championEntity ->
            ChampionDetailsDialog(
                champion = championEntity.toChampion(),
                version = version,
                onDismiss = { viewModel.dismissChampionDetails() }
            )
        }
    }
}

/**
 * Composable para mostrar los campeones en una cuadrícula.
 *
 * @param name Nombre del campeón.
 * @param imageUrl URL de la imagen del campeón.
 * @param onClick Función a ejecutar al hacer clic en el ítem.
 */
@Composable
fun GameGridItem(name: String, imageUrl: String?, onClick: () -> Unit = {}) {
    Card(
        shape = CardDefaults.elevatedShape,
        onClick = onClick,
        modifier = Modifier
            .size(90.dp)
            .height(110.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (imageUrl != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .build()
                    ),
                    contentDescription = name,
                    modifier = Modifier.size(64.dp)
                )
            }
            Text(
                text = name,
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}