package com.example.trackrscope.screens.profile.profile

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.screens.home.HomeViewModel
import com.example.trackrscope.ui.theme.GameTheme
import com.example.trackrscope.viewmodels.GameThemeViewModel
import com.example.trackrscope.viewmodels.ThemeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(),
    themeViewModel: ThemeViewModel = viewModel(),
    gameThemeViewModel: GameThemeViewModel = viewModel(),
    homeViewModel: HomeViewModel
) {

    val auth = remember { FirebaseAuth.getInstance() }

    LaunchedEffect(Unit) {
        viewModel.cargarDatosUsuario(auth = auth)
        viewModel.cargarFavoritos(auth = auth)
    }

    val username by viewModel.username
    val email by viewModel.email

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var showLogout by remember { mutableStateOf(false) } // Diálogo para cerrar sesión.
    var showDelete by remember { mutableStateOf(false) } // Diálogo para la eliminación de la cuenta.

    var showTheme by remember { mutableStateOf(false) }

    // <editor-fold desc="Profile Screen">
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        /* Contenedor principal */
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = stringResource(id = R.string.profile))

                Box {

                    IconButton(
                        onClick = { expanded = !expanded },
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }

                    /* Menu desplegable para las opciones */
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        if (auth.currentUser?.isAnonymous == false) {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.edit_profile)) },
                                onClick = {
                                    expanded = false
                                    editProfile(navController, auth)
                                })
                        } else {
                            DropdownMenuItem(
                                text = { Text(stringResource(id = R.string.delete_account)) },
                                onClick = {
                                    showDelete = true
                                    expanded = false
                                })
                        }

                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.theme)) },
                            onClick = {
                                showTheme = true
                                expanded = false
                            }
                        )

                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.logout)) },
                            onClick = {
                                showLogout = true
                                expanded = false
                            })
                    }
                }
            }

            if (showDelete) {
                AlertDialog(
                    onDismissRequest = { showLogout = false },
                    title = {
                        Text(
                            stringResource(id = R.string.delete_account),
                        )
                    },
                    text = {
                        Text(
                            stringResource(id = R.string.confirm_delete),
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Lógica para cerrar sesión
                                deleteAccount(navController, auth)
                                navController.navigate(Routes.LOGIN)
                                showLogout = false
                            },
                        ) {
                            Text(stringResource(id = R.string.accept))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showLogout = false }, // Solo cerrar el diálogo
                        ) {
                            Text(stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            if (showLogout) {
                AlertDialog(
                    onDismissRequest = { showLogout = false },
                    title = {
                        Text(
                            stringResource(id = R.string.logout),
                        )
                    },
                    text = {
                        Text(
                            stringResource(id = R.string.confirm_logout),
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Lógica para cerrar sesión
                                logout(navController, auth)
                                showLogout = false
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        ) {
                            Text(stringResource(id = R.string.accept))
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = { showLogout = false }, // Solo cerrar el diálogo
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
                        ) {
                            Text(stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            if (showTheme) {
                AlertDialog(
                    containerColor = MaterialTheme.colorScheme.surface,
                    onDismissRequest = { showTheme = false },
                    title = { Text(stringResource(id = R.string.select_theme)) },
                    text = {
                        Column {
                            Button(
                                onClick = {
                                    themeViewModel.setTheme(GameTheme.LOL)
                                    gameThemeViewModel.saveGame("lol", "lol_theme")
                                    showTheme = false
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    stringResource(id = R.string.lol),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Button(
                                onClick = {
                                    themeViewModel.setTheme(GameTheme.VALORANT)
                                    gameThemeViewModel.saveGame(
                                        "valorant",
                                        "valorant_theme"
                                    )
                                    showTheme = false
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    stringResource(id = R.string.valorant),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Button(
                                onClick = {
                                    themeViewModel.setTheme(GameTheme.TFT)
                                    gameThemeViewModel.saveGame("tft", "tft_theme")
                                    showTheme = false
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    stringResource(id = R.string.tft),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                            Button(
                                onClick = {
                                    themeViewModel.setTheme(GameTheme.DEFAULT)
                                    gameThemeViewModel.saveGame("default", "default_theme")
                                    showTheme = false
                                },
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                            ) {
                                Text(
                                    stringResource(id = R.string.default_theme),
                                    color = MaterialTheme.colorScheme.onSecondary
                                )
                            }
                        }

                    },
                    confirmButton = {},
                    dismissButton = {
                        Button(
                            onClick = { showTheme = false },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Text(stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            /* Contenedor del formulario */
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                /* Contenedor de la foto de perfil */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center
                )
                {
                    val imageActual = remember(selectedImageUri, viewModel.image.value) {
                        selectedImageUri?.toString() ?: viewModel.image.value
                    }

                    if (imageActual.isNotEmpty()) {
                        AsyncImage(
                            model = imageActual,
                            contentDescription = stringResource(id = R.string.placeholder),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .shadow(4.dp, CircleShape),
                            placeholder = painterResource(R.drawable.profile_img_white),
                            error = painterResource(R.drawable.profile_img_white)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_img_white),
                            contentDescription = stringResource(id = R.string.placeholder),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape)
                                .shadow(4.dp, CircleShape)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = username,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = email,
                    )
                }

                if (viewModel.isAnonymous()) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = stringResource(id = R.string.login_as) + stringResource(id = R.string.anonymous_user),
                        )
                        /* Botón de Submit */
                        Button(
                            onClick = {
                                logout(navController, auth)
                            },
                            enabled = true,
                            modifier = Modifier
                                .width(250.dp)
                                .padding(horizontal = 15.dp),
                        ) {
                            Text(text = stringResource(id = R.string.login))
                        }
                    }
                } else {
                    if (viewModel.favoritos.isNotEmpty()) {

                        Text(
                            text = stringResource(id = R.string.favorites),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(viewModel.favoritos) { favorito ->

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            homeViewModel.fetchSummonerProfile(
                                                favorito.gameName,
                                                favorito.tagLine
                                            )
                                            navController.navigate(Routes.HOME)
                                        },
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                                ) {
                                    Box(modifier = Modifier.fillMaxWidth()) {
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
                                                            .data("https://ddragon.leagueoflegends.com/cdn/15.10.1/img/profileicon/${favorito.profileIconId}.png")
                                                            .crossfade(true)
                                                            .diskCachePolicy(CachePolicy.ENABLED)
                                                            .memoryCachePolicy(CachePolicy.ENABLED)
                                                            .build()
                                                    ),
                                                    contentDescription = favorito.gameName,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(CircleShape)
                                                        .align(Alignment.Center)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = favorito.gameName,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.size(4.dp))
                                                    Text(
                                                        text = "#${favorito.tagLine}",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.6f
                                                        )
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text(
                                                    text = favorito.summonerLevel.toString(),
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.6f
                                                    )
                                                )
                                            }
                                            Box {
                                                IconButton(
                                                    onClick = {
                                                        viewModel.eliminarFavorito(
                                                            auth,
                                                            favorito.gameName,
                                                            favorito.tagLine
                                                        )
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Favorite,
                                                        contentDescription = "Favorites"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // </editor-fold>
}


fun editProfile(navController: NavController, auth: FirebaseAuth) {

    val user = auth.currentUser

    if (user != null && !user.isAnonymous) {
        navController.navigate(Routes.EDITPROFILE)
    }
}

/**
 * Elimina la cuenta del usuario.
 *
 * @param navController Controlador de navegación.
 * @param auth Instancia de FirebaseAuth.
 */
fun deleteAccount(navController: NavController, auth: FirebaseAuth) {
    val user = auth.currentUser

    if (user != null && user.isAnonymous) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    navController.context,
                    "Se ha borrado la cuenta correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    navController.context,
                    "Error al eliminar la cuenta",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

/**
 * Función para cerrar sesión.
 *
 * @param navController Controlador de navegación.
 */
fun logout(navController: NavController, auth: FirebaseAuth) {
    navController.navigate(Routes.LOGIN) {
        popUpTo(Routes.PROFILE) { inclusive = true }
    }
    auth.signOut()
}
