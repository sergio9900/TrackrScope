package com.example.trackrscope.utils

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.trackrscope.R
import com.example.trackrscope.screens.game.components.ChampionComponents

/**
 * Componente para mostrar un indicador de carga.
 *
 */
@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
        }
    }
}

/**
 * Componente para mostrar un indicador de carga de champions.
 */
@Composable
fun LoadingChampions() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.loading_champions),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.3f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Componente para mostrar un campo de entrada de texto con un botón de contraseña.
 *
 * @param label Etiqueta del campo de entrada.
 * @param value Valor actual del campo de entrada.
 * @param onValueChange Función para manejar cambios en el valor del campo de entrada.
 * @param imeAction Acción de entrada del teclado.
 * @param focusRequester Requeseador de enfoque para el campo de entrada.
 * @param onImeAction Función para manejar la acción de entrada del teclado.
 */
@Composable
fun FormInputPassword(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    focusRequester: FocusRequester? = null,
    onImeAction: (() -> Unit)? = null
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    val passwordVisible = remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value, // ViewModel
        onValueChange = onValueChange,
        placeholder = { Text(text = label) },
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp)
            .focusRequester(focusRequester ?: FocusRequester.Default),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.outline,
        ),
        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                val iconRes = R.drawable.ic_mostrar
                val icon = painterResource(id = iconRes)
                Icon(painter = icon, contentDescription = null, modifier = Modifier.size(26.dp))
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onImeAction?.invoke()
            },
            onDone = {
                keyboardController?.hide()
            }
        )
    )
}

/**
 * Componente para mostrar un campo de entrada de texto.
 *
 * @param label Etiqueta del campo de entrada.
 * @param value Valor actual del campo de entrada.
 * @param onValueChange Función para manejar cambios en el valor del campo de entrada.
 * @param imeAction Acción de entrada del teclado.
 * @param focusRequester Requeseador de enfoque para el campo de entrada.
 * @param onImeAction Función para manejar la acción de entrada del teclado.
 */
@Composable
fun FormInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    focusRequester: FocusRequester? = null,
    onImeAction: (() -> Unit)? = null,
) {

    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = value, // ViewModel
        onValueChange = onValueChange,
        placeholder = { Text(text = label) },
        modifier = Modifier
            .width(300.dp)
            .padding(8.dp)
            .focusRequester(focusRequester ?: FocusRequester.Default),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.outline,
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                onImeAction?.invoke()
            },
            onDone = {
                keyboardController?.hide()
            }
        )
    )
}

/**
 * Componente para mostrar un campo de entrada de texto filtrable.
 *
 * @param label Etiqueta del campo de entrada.
 * @param value Valor actual del campo de entrada.
 * @param modifier Modificador para personalizar el componente.
 * @param onValueChange Función para manejar cambios en el valor del campo de entrada.
 */
@Composable
fun OutlinedTextFieldFilter(
    label: String,
    value: String,
    modifier: Modifier,
    onValueChange: (String) -> Unit
) {

    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedTextField(
        placeholder = { Text(text = label) },
        value = value,
        modifier = modifier,
        singleLine = true,
        interactionSource = interactionSource,
        onValueChange = onValueChange,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
            cursorColor = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            capitalization = KeyboardCapitalization.Sentences
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
            },
        ),
    )
}

/**
 * Componente para mostrar un diálogo de roles.
 *
 * @param roles Lista de roles.
 * @param selectedRole Lista de roles seleccionados.
 * @param onRoleSelected Función para manejar la selección de roles.
 * @param onClearRoles Función para manejar la limpieza de roles.
 * @param onDismiss Función para manejar la acción de cerrar el diálogo.
 */
@Composable
fun RolesFilter(
    roles: List<String>,
    selectedRole: List<String>,
    onRoleSelected: (String) -> Unit,
    onClearRoles: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        title = { stringResource(id = R.string.roles) },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(text = "Cerrar")
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                FilterChip(
                    selected = selectedRole.isEmpty(),
                    onClick = { onClearRoles() },
                    label = { Text("All") })

                roles.forEach { role ->
                    FilterChip(
                        selected = role in selectedRole,
                        onClick = { onRoleSelected(role) },
                        leadingIcon = { ChampionComponents.getRole(role) },
                        label = { Text(role) },
                    )
                }
            }
        },
    )
}

/**
 * Componente para mostrar un diálogo de búsqueda de jugadores.
 *
 * @param onDismiss Función para manejar la acción de cerrar el diálogo.
 * @param onSearch Función para manejar la acción de búsqueda.
 */
@Composable
fun PlayerDialog(
    onDismiss: () -> Unit,
    onSearch: (gameName: String, tagLine: String) -> Unit,
) {

    var gameName by remember { mutableStateOf("") }
    var tagLine by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.search_player),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextFieldFilter(
                    label = "Name",
                    value = gameName,
                    onValueChange = { gameName = it },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextFieldFilter(
                    label = "#Tag",
                    value = tagLine,
                    onValueChange = { tagLine = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                onClick = {
                    onSearch(gameName.trim(), tagLine.trim())
                },
            ) {
                Text("Buscar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
            ) {
                Text("Cancelar")
            }
        }
    )
}