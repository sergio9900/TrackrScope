package com.example.trackrscope.screens.auth.login

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun LoginScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: LoginViewModel = viewModel(),
) {
    // CoroutineScope para lanzar mensajes
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val isLoginEnabled by viewModel.isLoginEnabled

    val passwordVisible = remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val secondFocusRequest = remember { FocusRequester() }

    var showButtons = remember { mutableStateOf(false) }

    val signInStatus by viewModel.signInStatus.collectAsState()

    AskNotificationPermission(context)

    // <editor-fold desc="Login Screen">

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
                .padding(bottom = 80.dp),
        ) {
            /* Contenedor formulario */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                /* Logo de la aplicación */
                LogoAppLogin()
                Spacer(modifier = Modifier.height(50.dp))

                /* Campo de texto para el nombre de usuario */
                OutlinedTextField(
                    value = viewModel.username.value,
                    onValueChange = { viewModel.onUsernameChanged(it) },
                    placeholder = { Text(text = stringResource(id = R.string.Email)) },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(vertical = 8.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { secondFocusRequest.requestFocus() }
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    },
                    singleLine = true
                )

                /* Campo de texto para la contraseña */
                OutlinedTextField(
                    value = viewModel.password.value,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    placeholder = { Text(text = stringResource(id = R.string.password)) },
                    modifier = Modifier
                        .width(300.dp)
                        .padding(vertical = 8.dp)
                        .focusRequester(secondFocusRequest),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.outline,
                    ),
                    visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible.value = !passwordVisible.value }) {
                            val iconRes = R.drawable.ic_mostrar
                            val icon = painterResource(id = iconRes)
                            Icon(
                                painter = icon,
                                contentDescription = null,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = { keyboardController?.hide() }
                    ),
                    singleLine = true
                )

                /* Botones: "Register Here" y "Forgot Password?" */
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    /* Botón de registro */
                    TextButton(onClick = { navController.navigate(Routes.REGISTER) }) {
                        Text(
                            text = stringResource(id = R.string.register_here),
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    /* Botón de olvidar contraseña */
                    TextButton(onClick = { navController.navigate(Routes.RECOVERPASSWORD) }) {
                        Text(
                            text = stringResource(id = R.string.forgot_password),
                        )
                    }
                }
                /* Botones de inicio de sesión */
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        /* Botón de Submit */
                        Button(
                            onClick = {
                                if (isLoginEnabled) {
                                    signInWithEmail(
                                        viewModel.username.value,
                                        viewModel.password.value,
                                        navController,
                                        snackbarHostState,
                                        coroutineScope
                                    )
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Error: Campos vacíos")
                                    }
                                }
                            },
                            enabled = viewModel.isLoginEnabled.value,
                            modifier = Modifier
                                .width(250.dp)
                                .align(Alignment.Center)
                                .padding(horizontal = 15.dp),
                        ) {
                            Text(text = stringResource(id = R.string.submit))
                        }

                        val rotationAngle by animateFloatAsState(
                            targetValue = if (showButtons.value) 180f else 0f,
                            animationSpec = tween(durationMillis = 300),
                        )

                        IconButton(
                            onClick = { showButtons.value = !showButtons.value },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.rotate(rotationAngle)
                            )
                        }
                    }

                    LoginMenu(
                        viewModel,
                        navController,
                        showButtons = showButtons.value,
                        snackbarHostState
                    )
                }
            }
        }
    }
    // </editor-fold>
}

@Composable
fun LoginMenu(
    viewModel: LoginViewModel,
    navController: NavController,
    showButtons: Boolean,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedVisibility(
            visible = showButtons,
            enter = slideInVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Button(
                onClick = {
                    viewModel.signInWithGoogle(
                        context,
                        FirebaseAuth.getInstance(),
                        navController
                    )
                },
                modifier = Modifier
                    .width(250.dp)
                    .padding(vertical = 8.dp)
                    .padding(horizontal = 15.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.btn_google))
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        modifier = Modifier.padding(end = 25.dp),
                        text = stringResource(id = R.string.login_google),
                    )
                }
            }
        }
        // Animación para el botón de Invitado
        AnimatedVisibility(
            visible = showButtons,
            enter = slideInVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Button(
                onClick = {
                    Log.d("Login", "Intentando iniciar sesión como invitado")
                    signInAsGuest(
                        onSuccess = { user ->
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                            Log.d("Login", "Inicio de sesión como invitado exitoso ${user.uid}")
                        },
                        onFailure = { exception ->
                            Log.d(
                                "Login",
                                "Error al iniciar sesión como invitado: ${exception.message}"
                            )
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Error: Ya has iniciado sesión como invitado hoy")
                            }
                        }
                    )
                },
                modifier = Modifier
                    .width(250.dp)
                    .padding(horizontal = 15.dp),
                colors = ButtonDefaults.buttonColors(colorResource(id = R.color.btn_guest))
            ) {
                Text(
                    text = stringResource(id = R.string.login_guest),
                )
            }
        }
    }
}

/**
 * Logo de la aplicación.
 */
@Composable
fun LogoAppLogin() {
    val isDarkTheme = isSystemInDarkTheme()

    val logo = if (isDarkTheme) {
        painterResource(id = R.drawable.logo_dark)
    } else {
        painterResource(id = R.drawable.logo_light)
    }

    Image(
        painter = logo,
        contentDescription = null,
        modifier = Modifier.size(200.dp)
    )
}

/**
 * Inicia sesión como invitado.
 *
 * @param onSuccess Callback que se ejecutará si la sesión es exitosa.
 * @param onFailure Callback que se ejecutará si ocurre un error.
 */
fun signInAsGuest(
    onSuccess: (FirebaseUser) -> Unit,
    onFailure: (Exception) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    if (auth.currentUser != null) {
        if (auth.currentUser?.isAnonymous == true) {
            onSuccess(auth.currentUser!!)
        } else {
            onFailure(Exception("Usuario no es anonimo"))
        }
        return
    }

    // Obtengo el ID único del dispositivo.
    FirebaseInstallations.getInstance().id.addOnSuccessListener { deviceId ->
        if (deviceId == null) {
            onFailure(Exception("Error al obtener el ID del dispositivo"))
            return@addOnSuccessListener
        }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Dispositivos").whereEqualTo("deviceId", deviceId)
            .whereEqualTo("fecha", today).get().addOnSuccessListener { doc ->
                if (!doc.isEmpty) {
                    onFailure(Exception("Ya has creado una cuenta anónima hoy"))
                    return@addOnSuccessListener
                }

                auth.signInAnonymously().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        user?.let {
                            val data = mapOf(
                                "deviceId" to deviceId,
                                "uid" to it.uid,
                                "fecha" to today,
                                "timestamp" to FieldValue.serverTimestamp()
                            )

                            firestore.collection("Dispositivos").add(data).addOnSuccessListener {
                                Log.d("Login", "Registro anónimo guardado")
                            }.addOnFailureListener { e ->
                                Log.d("Login", "Error al guardar el registro anónimo", e)
                            }
                            onSuccess(it)
                        }
                        Log.d("Login", "Inicio de sesión como invitado exitoso ${user?.uid}")
                    } else {
                        Log.e("Login", "Error al iniciar sesión como invitado", task.exception)
                        onFailure(task.exception ?: Exception("Error desconocido"))
                    }
                }
            }
    }.addOnFailureListener { e ->
        onFailure(Exception("Error al obtener Installation Id: ${e.message}"))
    }
}

/**
 * Inicia sesión con email y contraseña.
 *
 * @param email Email del usuario.
 * @param password Contraseña del usuario.
 * @param navController Controlador de navegación.
 */
fun signInWithEmail(
    email: String,
    password: String,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope,
) {
    val firebaseAuth = FirebaseAuth.getInstance()

    Log.d("Login", firebaseAuth.currentUser?.uid.toString())

    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val user = firebaseAuth.currentUser

            if (user != null && user.isEmailVerified) {
                navController.navigate(Routes.HOME)
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("No se ha verificado el email")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Error: Credenciales no válidas")
            }
        }
    }
}

/**
 * Solicita el permiso de notificación.
 *
 * @param context Contexto de la aplicación.
 */
@Composable
fun AskNotificationPermission(context: Context) {

    // Crea un lanzador de actividad para manejar la solicitud del permiso de notificación.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Notificaciones", "Permiso concedido")
        } else {
            Log.d("Notificaciones", "Permiso denegado")
        }
    }
    // Verifica si la versión del sistema operativo es igual o superior a Android 13 (API 33)
    // Verifica si ya se ha concedido el permiso de notificación.
    val hasPermissions = ContextCompat.checkSelfPermission(
        context, Manifest.permission.POST_NOTIFICATIONS
    ) == PackageManager.PERMISSION_GRANTED

    // Si no se ha concedido el permiso, solicita el permiso.
    if (!hasPermissions) {
        // Solicita el permiso de notificación.
        LaunchedEffect(Unit) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    } else {
        Log.d("Notificaciones", "Permiso concedido")
    }
}