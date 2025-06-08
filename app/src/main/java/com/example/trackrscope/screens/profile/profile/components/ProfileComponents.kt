package com.example.trackrscope.screens.profile.profile.components

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.screens.profile.profile.Favorito
import com.example.trackrscope.ui.theme.GameTheme
import com.google.firebase.auth.FirebaseAuth

object ProfileComponents {

    /**
     * Composable para la cabecera del perfil.
     */
    @Composable
    fun ProfileHeader(
        expanded: Boolean,
        onExpandChange: (Boolean) -> Unit,
        onEditProfile: () -> Unit,
        onDeleteAccount: () -> Unit,
        onTheme: () -> Unit,
        onLogout: () -> Unit,
        isAnonymous: Boolean
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = stringResource(id = R.string.profile))

            Box {
                IconButton(onClick = { onExpandChange(!expanded) }) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandChange(false) }
                ) {
                    if (!isAnonymous) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.edit_profile)) },
                            onClick = {
                                onExpandChange(false)
                                onEditProfile()
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.delete_account)) },
                            onClick = {
                                onExpandChange(false)
                                onDeleteAccount()
                            }
                        )
                    }

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.theme)) },
                        onClick = {
                            onExpandChange(false)
                            onTheme()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.logout)) },
                        onClick = {
                            onExpandChange(false)
                            onLogout()
                        }
                    )
                }
            }
        }
    }

    /**
     * Muestra la imagen de perfil del usuario.
     */
    @Composable
    fun ProfileImage(
        imageUrl: String,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current
        val placeholder = painterResource(R.drawable.profile_img_white)

        val painter = rememberAsyncImagePainter(
            model = imageUrl.ifEmpty { null },
            placeholder = placeholder,
            error = placeholder
        )

        Image(
            painter = painter,
            contentDescription = stringResource(id = R.string.placeholder),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .shadow(4.dp, CircleShape)
        )
    }

    /**
     * Muestra la información del perfil del usuario.
     */
    @Composable
    fun ProfileInfo(username: String, email: String) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = username)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = email)
        }
    }

    @Composable
    fun FavoritesSection(
        favoritos: List<Favorito>,
        onItemClick: (Favorito) -> Unit,
        onRemoveClick: (Favorito) -> Unit
    ) {
        Column {
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
                items(favoritos) { favorito ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(favorito) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    model = "https://ddragon.leagueoflegends.com/cdn/15.10.1/img/profileicon/${favorito.profileIconId}.png"
                                ),
                                contentDescription = favorito.gameName,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
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
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = favorito.summonerLevel.toString(),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(
                                onClick = { onRemoveClick(favorito) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Remove Favorite"
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Muestra la sección de inicio de sesión anónimo.
     */
    @Composable
    fun AnonymousLogin(
        onLoginClick: () -> Unit
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.login_as) + stringResource(id = R.string.anonymous_user),
            )
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .width(250.dp)
                    .padding(horizontal = 15.dp)
            ) {
                Text(text = stringResource(id = R.string.login))
            }
        }
    }


    /**
     * Edita el perfil del usuario.
     */
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

    /**
     * Muestra un diálogo de confirmación para cerrar sesión.
     */
    @Composable
    fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(id = R.string.logout)) },
            text = { Text(stringResource(id = R.string.confirm_logout)) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(stringResource(id = R.string.accept))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    /**
     * Muestra un diálogo de confirmación para eliminar la cuenta.
     */
    @Composable
    fun DeleteAccountDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(id = R.string.delete_account)) },
            text = { Text(stringResource(id = R.string.confirm_delete)) },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(stringResource(id = R.string.accept))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }

    /**
     * Muestra un diálogo para seleccionar un tema de juego.
     */
    @Composable
    fun ThemeSelector(
        onDismiss: () -> Unit,
        onThemeSelected: (GameTheme) -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(id = R.string.select_theme)) },
            text = {
                Column {
                    listOf(
                        GameTheme.LOL to R.string.lol,
                        GameTheme.VALORANT to R.string.valorant,
                        GameTheme.TFT to R.string.tft,
                        GameTheme.DEFAULT to R.string.default_theme
                    ).forEach { (theme, resId) ->
                        Button(
                            onClick = { onThemeSelected(theme) },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
                        ) {
                            Text(stringResource(id = resId))
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel))
                }
            }
        )
    }


}