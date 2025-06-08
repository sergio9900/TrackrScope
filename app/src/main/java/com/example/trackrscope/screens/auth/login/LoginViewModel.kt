package com.example.trackrscope.screens.auth.login

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * ViewModel para la pantalla de inicio de sesión.
 */
class LoginViewModel : ViewModel() {

    val username: MutableState<String> = mutableStateOf("") // Estado para el nombre de usuario.
    val password: MutableState<String> = mutableStateOf("") // Estado para la contraseña.

    // Estado para habilitar el botón de login.
    val isLoginEnabled: MutableState<Boolean> = mutableStateOf(false)
    val isEmailValid: MutableState<Int> = mutableIntStateOf(R.color.secundario_login)

    private val _signInStatus = MutableStateFlow<SignInStatus>(SignInStatus.Idle)
    val signInStatus: StateFlow<SignInStatus> = _signInStatus

    /**
     * Inicializa el ViewModel.
     */
    init {
        // Recalcular si el login está habilitado cada vez que se cambia username o password.
        username.value = ""
        password.value = ""
        updateLoginButtonState()
    }

    /**
     * Función para manejar el inicio de sesión.
     *
     * @param username Nombre de usuario.
     */
    fun onUsernameChanged(username: String) {
        if (username == "") {
            isEmailValid.value = R.color.secundario_login
        } else {
            if (validarEmail(username)) {
                isEmailValid.value = R.color.green
            } else {
                isEmailValid.value = R.color.red
            }
        }

        this.username.value = username
        updateLoginButtonState()
    }

    /**
     * Función para manejar el cambio de contraseña.
     *
     * @param password Nueva contraseña.
     */
    fun onPasswordChanged(password: String) {
        this.password.value = password
        updateLoginButtonState()
    }

    /**
     * Actualiza si el login está habilitado o no.
     */
    private fun updateLoginButtonState() {
        isLoginEnabled.value = validarEmail(username.value) && isValidPassword(password.value)
    }

    /**
     * Comprueba si la contraseña es válida.
     *
     * @param password Contraseña a comprobar.
     * @return True si la contraseña es válida, false en caso contrario.
     */
    private fun isValidPassword(password: String): Boolean {
        return password.isNotEmpty()
    }

    /**
     * Inicia sesión con Google.
     *
     * @param context Contexto de la aplicación.
     * @param auth Instancia de [FirebaseAuth].
     * @param navController Controlador de navegación.
     */
    fun signInWithGoogle(context: Context, auth: FirebaseAuth, navController: NavController) {

        val credentialManager = CredentialManager.Companion.create(context)

        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false).setServerClientId(
                context.getString(R.string.web_client_id)
            ).setAutoSelectEnabled(false).build()

        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        viewModelScope.launch {
            try {
                Log.d("GoogleLogin", "Iniciando Google Sign-In")
                val result = credentialManager.getCredential(context, request)
                Log.d("GoogleLogin", "Credential obtenida")

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential.Companion.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()

                if (authResult != null) {
                    _signInStatus.value = SignInStatus.Success
                    guardarDatosFirestore(auth)
                    Log.d("GoogleLogin", "Inicio de sesión con Google exitoso")
                    navController.navigate(Routes.HOME)
                } else {
                    _signInStatus.value = SignInStatus.Error("Error al iniciar sesión con Google")
                    Log.d("GoogleLogin", "Error al iniciar sesión con Google")
                }
            } catch (e: GetCredentialException) {
                if (e.message?.contains("CANCELED") == true || e.javaClass.simpleName == "UserCanceledException") {
                    Log.d("GoogleLogin", "Login cancelado por el usuario.")
                    _signInStatus.value = SignInStatus.Idle // no hacer nada especial
                } else {
                    Log.e("GoogleLogin", "Error en el login: ${e.message}")
                    _signInStatus.value = SignInStatus.Error("Error: ${e.message}")
                }
            } catch (e: Exception) {
                _signInStatus.value = SignInStatus.Error("Error inesperado: ${e.message}")
                Log.e("GoogleLogin", "Error inesperado: ${e.message}")
            }
        }
    }

    /**
     * Guarda los datos de la cuenta de Google en Firestore.
     *
     * @param auth Instancia de [FirebaseAuth].
     */
    fun guardarDatosFirestore(auth: FirebaseAuth) {

        val usuario = auth.currentUser

        usuario?.let {
            val uid = it.uid
            val nombre = it.displayName
            val email = it.email
            val image = it.photoUrl?.toString()

            val db = Firebase.firestore

            val datos = mapOf(
                "Username" to nombre,
                "Email" to email,
                "Image" to image
            )

            db.collection("Usuarios").document(uid).get().addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    // Guardo los datos de la cuenta de Google si no existe en la base de datos
                    db.collection("Usuarios").document(uid).set(datos).addOnSuccessListener {
                        Log.d("Firestore", "Datos guardados en Firestore")
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Error al guardar los datos en Firestore: ${e.message}")
                    }
                } else {
                    Log.d("Firestore", "Los datos ya existen en Firestore")
                }
            }.addOnFailureListener { e ->
                Log.e(
                    "Firestore",
                    "Error al comprobar la existencia del documento en Firestore: ${e.message}"
                )
            }
        }
    }

    /**
     * Función para válidar el email.
     *
     * @param email Email a validar.
     * @return True si el email es válido, false en caso contrario.
     */
    private fun validarEmail(email: String): Boolean {
        val pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(pattern.toRegex())
    }

    /**
     * Estados de inicio de sesión.
     */
    sealed class SignInStatus {
        object Idle : SignInStatus()
        object Success : SignInStatus()
        data class Error(val message: String) : SignInStatus()
    }
}