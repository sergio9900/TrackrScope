package com.example.trackrscope.screens.profile.editprofile

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel para la pantalla de edición de perfil.
 */
class EditProfileViewModel : ViewModel() {

    val username: MutableState<String> = mutableStateOf("")
    val profileImageUri: MutableState<String> = mutableStateOf("")

    val newPassword: MutableState<String> = mutableStateOf("")
    val confirmNewPassword: MutableState<String> = mutableStateOf("")

    val isSaveEnabled: MutableState<Boolean> = mutableStateOf(false)

    var originalUsername: String = ""

    val isEmailPasswordUser: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Inicializa el ViewModel.
     */
    init {
        checkIfEmailPasswordUser()
        updateSaveButtonState()
    }

    /**
     * Comprueba si el usuario autenticado es un usuario con email y contraseña.
     */
    private fun checkIfEmailPasswordUser() {

        val user = FirebaseAuth.getInstance().currentUser
        user?.providerData?.forEach { userInfo ->
            if (userInfo.providerId == EmailAuthProvider.PROVIDER_ID) {
                isEmailPasswordUser.value = true
            }
        }
    }

    /**
     * Actualiza el nombre de usuario.
     *
     * @param username Nuevo nombre de usuario.
     */
    fun onUsernameChanged(username: String) {
        this.username.value = username
        updateSaveButtonState()
    }

    /**
     * Actualiza la contraseña.
     *
     * @param newPassword Nueva contraseña.
     */
    fun onNewPasswordChanged(newPassword: String) {
        this.newPassword.value = newPassword
        updateSaveButtonState()
    }

    /**
     * Actualiza la confirmación de la contraseña.
     *
     * @param confirmNewPassword Confirmación de la nueva contraseña.
     */
    fun onConfirmNewPasswordChanged(confirmNewPassword: String) {
        this.confirmNewPassword.value = confirmNewPassword
        updateSaveButtonState()
    }

    private fun isValid(valor: String): Boolean {
        return valor.isNotEmpty()
    }

    /**
     * Actualiza el estado del botón de guardado.
     */
    private fun updateSaveButtonState() {

        val hasChanges =
            username.value != originalUsername || (newPassword.value.isNotEmpty() && newPassword.value == confirmNewPassword.value)

        val isPasswordChangeValid =
            newPassword.value.isEmpty() || (newPassword.value.length >= 6 && newPassword.value == confirmNewPassword.value)

        isSaveEnabled.value = hasChanges && isValid(username.value) && isPasswordChangeValid
    }

    /**
     * Actualiza los datos del usuario en la base de datos.
     *
     * @param imageUri Nueva imagen de perfil.
     * @param context Contexto de la aplicación.
     * @param navController Controlador de navegación.
     */
    fun updateUserData(
        imageUri: Uri?,
        context: Context,
        navController: NavController
    ) {

        val user = FirebaseAuth.getInstance().currentUser
        val firestore = FirebaseFirestore.getInstance()

        if (user == null) {
            Toast.makeText(context, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val updateTasks = mutableListOf<Task<Void>>()

        val userDoc = firestore.collection("Usuarios").document(user.uid)

        if (newPassword.value.isNotEmpty() && newPassword.value == confirmNewPassword.value) {
            updateTasks.add(user.updatePassword(newPassword.value))
        }

        Tasks.whenAllComplete(updateTasks).addOnSuccessListener {

            val updates = mutableMapOf<String, Any>()
            updates["Username"] = username.value

            if (imageUri != null) {
                // TODO: Subir imagen de perfil
                updates["Image"] = imageUri.toString()
            }

            userDoc.update(updates).addOnSuccessListener {
                Toast.makeText(context, "Datos actualizados correctamente", Toast.LENGTH_SHORT)
                    .show()
                navController.navigate("profile_screen")
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }

        }
    }

    /**
     * Carga los datos del usuario de la base de datos.
     *
     * @param onFailure Función a ejecutar en caso de error.
     */
    fun loadUserData(onFailure: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        val firestore = FirebaseFirestore.getInstance()

        if (user == null) {
            onFailure("Usuario no autenticado")
            return
        }

        val userDoc = firestore.collection("Usuarios").document(userId ?: "")

        userDoc.get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                username.value = doc.getString("Username") ?: ""
                profileImageUri.value = doc.getString("Image") ?: ""
            } else {
                onFailure("No se encontraron los datos del usuario")
            }
        }.addOnFailureListener { e ->
            onFailure(e.message ?: "Error al cargar los datos")
        }
    }
}