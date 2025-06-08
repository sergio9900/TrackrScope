package com.example.trackrscope.screens.auth.recoverpassword

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RecoverPasswordScreen(
    navController: NavController,
    viewModel: RecoverPasswordViewModel = viewModel()
) {
    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    val isEmailEnabled by viewModel.isEmailEnabled

    val buttonColors = ButtonDefaults.buttonColors(
        if (isEmailEnabled) Color.Cyan else Color.White,
    )

    // <editor-fold desc="Forgot Password Screen">
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
            ) {
                focusManager.clearFocus()
            }
    ) {
        /* Contenedor principal */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp),
        ) {
            /* Contenedor formulario */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center

            ) {
                Spacer(modifier = Modifier.height(40.dp))
                /* Logo de la aplicación */
                LogoApp()
                /* Campo de email para recuperar contraseña */
                OutlinedTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    placeholder = { Text(text = stringResource(id = R.string.email)) },
                    modifier = Modifier
                        .width(300.dp)
                        .height(60.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    ),
                    singleLine = true, leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (viewModel.email.value.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onEmailChanged("") }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                /* Botón para recuperar contraseña */
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    /* Botón para recuperar contraseña */
                    Button(
                        onClick = {
                            if (isEmailEnabled) {
                                recoverPassword(
                                    viewModel.email.value,
                                    navController,
                                    context
                                )
                            } else {
                                Toast.makeText(context, "Introduce un correo", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        enabled = viewModel.isEmailEnabled.value,
                        modifier = Modifier
                            .width(250.dp)
                            .padding(horizontal = 15.dp),
                        colors = buttonColors
                    ) {
                        Text(
                            text = stringResource(id = R.string.recover_password),
                            color = Color.Black
                        )
                    }
                    /* Botón para volver a login */
                    Button(
                        onClick = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.RECOVERPASSWORD) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .width(250.dp)
                            .padding(horizontal = 15.dp),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(
                            text = stringResource(id = R.string.back_to_login),
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
    // </editor-fold>
}


@Composable
fun LogoApp() {
    val isDarkTheme = isSystemInDarkTheme()

    val logo = if (isDarkTheme) {
        painterResource(id = R.drawable.logo_dark)
    } else {
        painterResource(id = R.drawable.logo_light)
    }

    Image(
        painter = logo,
        contentDescription = stringResource(id = R.string.placeholder),
        modifier = Modifier
            .padding(bottom = 70.dp)
            .wrapContentSize()
    )
}

/**
 * Función para recuperar la contraseña de un usuario.
 *
 * @param email El correo electrónico del usuario.
 * @param navController El controlador de navegación de Jetpack Compose.
 * @param context El contexto de la aplicación.
 */
fun recoverPassword(
    email: String, navController: NavController, context: Context
) {
    val firebaseAuth = FirebaseAuth.getInstance()

    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.RECOVERPASSWORD) { inclusive = true }
            }
            Toast.makeText(
                context,
                "Si tienes la cuenta registrada, se te enviara el correo a $email",
                Toast.LENGTH_LONG
            ).show()
        } else {
            Toast.makeText(context, "Error al enviar el correo a $email", Toast.LENGTH_LONG).show()
        }
    }
}