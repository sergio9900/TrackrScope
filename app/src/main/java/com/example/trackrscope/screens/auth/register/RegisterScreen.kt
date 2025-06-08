package com.example.trackrscope.screens.auth.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.utils.FormInputField
import com.example.trackrscope.utils.FormInputPassword
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.firestore


/**
 * Composable que representa la pantalla de registro.
 *
 * @param navController El controlador de navegación de Jetpack Compose.
 * @param viewModel [RegisterViewModel] asociado a la pantalla de registro.
 */
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {

    val context = LocalContext.current

//    val isRegisterEnabled by viewModel.isRegisterEnabled

    val buttonColors = ButtonDefaults.buttonColors(
        if (viewModel.isRegisterEnabled.value) Color.Cyan else Color.Gray,
    )

    val focusManager = LocalFocusManager.current

    val firstFocusRequest = remember { FocusRequester() }
    val secondFocusRequest = remember { FocusRequester() }
    val thirdFocusRequest = remember { FocusRequester() }
    val fourthFocusRequest = remember { FocusRequester() }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador de selección de imagen.
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
            }
        }

    // <editor-fold desc="Register Screen">
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
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp),
        ) {
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
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                )
                {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = stringResource(id = R.string.placeholder),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.profile_img_white),
                            contentDescription = stringResource(id = R.string.placeholder),
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_PICK).apply {
                                type = "image/*"
                            }
                            launcher.launch(intent)
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                        )
                    }
                }

                /* Contenedor de los campos de texto */
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    /* Campo username */
                    FormInputField(
                        label = stringResource(id = R.string.username),
                        value = viewModel.username.value,
                        onValueChange = { viewModel.onUsernameChanged(it) },
                        imeAction = ImeAction.Next,
                        focusRequester = firstFocusRequest,
                        onImeAction = { secondFocusRequest.requestFocus() }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    /* Campo email */
                    FormInputField(
                        label = stringResource(id = R.string.email),
                        value = viewModel.email.value,
                        onValueChange = { viewModel.onEmailChanged(it) },
                        imeAction = ImeAction.Next,
                        focusRequester = secondFocusRequest,
                        onImeAction = { thirdFocusRequest.requestFocus() },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    /* Campo password */
                    FormInputPassword(
                        label = stringResource(id = R.string.password),
                        value = viewModel.password.value,
                        onValueChange = { viewModel.onPasswordChanged(it) },
                        imeAction = ImeAction.Next,
                        focusRequester = thirdFocusRequest,
                        onImeAction = { fourthFocusRequest.requestFocus() }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    /* Campo confirm password */
                    FormInputPassword(
                        label = stringResource(id = R.string.confirm_password),
                        value = viewModel.confirmPassword.value,
                        onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                        imeAction = ImeAction.Done,
                        focusRequester = fourthFocusRequest
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                /* Botones de registro y volver a login */
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    /* Botón de registro */
                    Button(
                        onClick = {
                            registerValid(
                                viewModel.email.value,
                                viewModel.username.value,
                                viewModel.password.value,
                                navController,
                                context
                            )
                        },
                        modifier = Modifier
                            .height(75.dp)
                            .width(250.dp)
                            .padding(horizontal = 15.dp, vertical = 10.dp),
                        enabled = viewModel.isRegisterEnabled.value,
                        colors = buttonColors
                    ) {
                        Text(text = stringResource(id = R.string.register))
                    }
                    /* Botón de volver a login */
                    Button(
                        onClick = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.REGISTER) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier
                            .height(75.dp)
                            .width(250.dp)
                            .padding(horizontal = 15.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.back_to_login),
                        )
                    }
                }
            }
        }
    }
    // </editor-fold>
}


/**
 * Función para comprobar los datos de registro.
 *
 * @param email El correo electrónico del usuario.
 * @param username El nombre de usuario del usuario.
 * @param password La contraseña del usuario.
 * @param navController El controlador de navegación de Jetpack Compose.
 * @param context El contexto de la aplicación.
 */
fun registerValid(
    email: String,
    username: String,
    password: String,
    navController: NavController,
    context: Context
) {
    val firebaseAuth = FirebaseAuth.getInstance()

    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = firebaseAuth.currentUser
            sendEmailVerification(firebaseAuth)
            user?.let {
                guardarDatosFirestore(
                    userId = it.uid,
                    username = username,
                    email = email,
                    image = it.photoUrl.toString()
                )
            }
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.REGISTER) { inclusive = true }
            }
            Toast.makeText(context, "Cuenta registrada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            val exception = task.exception
            if (exception is FirebaseAuthUserCollisionException) {
                Toast.makeText(
                    context, "La cuenta ya existe con este correo", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context, "La contraseña debe más de 5 caracteres", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}

/**
 * Función para guardar los datos del usuario en Firestore.
 *
 * @param userId El ID del usuario.
 * @param username El nombre de usuario del usuario.
 * @param email El correo electrónico del usuario.
 * @param image La URL de la imagen del usuario.
 */
fun guardarDatosFirestore(
    userId: String, username: String, email: String, image: String,
) {

    val db = Firebase.firestore
    val userMap = hashMapOf(
        "Username" to username,
        "Email" to email,
        "Image" to image.toString(),
    )
    db.collection("Usuarios").document(userId).set(userMap)
        .addOnSuccessListener { Log.d("Firestore", "Datos guardados correctamente") }
        .addOnFailureListener { e -> Log.w("Firestore", "Error al guardar los datos", e) }
}

fun sendEmailVerification(firebaseAuth: FirebaseAuth) {
    val user = firebaseAuth.currentUser!!
    user.sendEmailVerification().addOnCompleteListener { }
}