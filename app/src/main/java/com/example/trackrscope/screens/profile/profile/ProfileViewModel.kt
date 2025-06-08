package com.example.trackrscope.screens.profile.profile

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * ViewModel para la pantalla de perfil.
 */
class ProfileViewModel : ViewModel() {

    val username: MutableState<String> =
        mutableStateOf("") // Estado para el nombre de usuario.
    val email: MutableState<String> = mutableStateOf("") // Estado para el email.
    val image: MutableState<String> = mutableStateOf("") // Estado para la imagen de perfil.
//    val password: MutableState<String> = mutableStateOf("") // Estado para la contraseña.

    private val _favoritos = mutableStateListOf<Favorito>()
    val favoritos: List<Favorito> = _favoritos

    /**
     * Verifica si el usuario está autenticado como anónimo.
     *
     * @return `true` si el usuario está autenticado como anónimo, `false` en caso contrario.
     */
    fun isAnonymous(): Boolean {
        val auth = FirebaseAuth.getInstance()
        return auth.currentUser?.isAnonymous == true
    }

    /**
     * Carga los datos del usuario actual desde Firestore.
     *
     * @param auth Instancia de [FirebaseAuth] para obtener el ID del usuario actual.
     */
    fun cargarDatosUsuario(auth: FirebaseAuth) {

        val userId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(userId ?: "").get().addOnSuccessListener { doc ->
            if (doc.exists()) {

                username.value = doc.getString("Username") ?: ""
                email.value = doc.getString("Email") ?: ""
                image.value = doc.getString("Image") ?: ""

                Log.d("PerfilViewModel", "Datos cargados correctamente")
            } else {
                Log.w("PerfilViewModel", "El documento no existe")
            }
        }.addOnFailureListener { e ->
            Log.e("PerfilViewModel", "Error al cargar los datos: ${e.message}")
        }
    }

    /**
     * Carga los favoritos del usuario actual desde Firestore.
     *
     * @param auth Instancia de [FirebaseAuth] para obtener el ID del usuario actual.
     */
    fun cargarFavoritos(auth: FirebaseAuth) {

        val userId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        db.collection("Usuarios").document(userId.toString())
            .collection("Favoritos").get()
            .addOnSuccessListener {
                _favoritos.clear()
                for (document in it) {
                    val favorito = document.toObject(Favorito::class.java)
                    _favoritos.add(favorito)
                }
            }.addOnFailureListener {
                Log.e("PerfilViewModel", "Error al cargar los datos: ${it.message}")
            }
    }

    /**
     * Elimina un favorito del usuario actual en Firestore.
     *
     * @param auth Instancia de [FirebaseAuth] para obtener el ID del usuario actual.
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     */
    fun eliminarFavorito(auth: FirebaseAuth, gameName: String, tagLine: String) {

        val userId = auth.currentUser?.uid
        val db = FirebaseFirestore.getInstance()

        val docId = "${gameName}#${tagLine}"

        db.collection("Usuarios").document(userId.toString())
            .collection("Favoritos").document(docId).delete()
            .addOnSuccessListener {
                _favoritos.removeIf { it.gameName == gameName && it.tagLine == tagLine }
                Log.d("Firestore", "Datos eliminados correctamente")
            }.addOnFailureListener {
                Log.w("Firestore", "Error al eliminar los datos")
            }
    }
}

/**
 * Modelo de datos para representar un favorito.
 */
data class Favorito(
    val gameName: String = "",
    val tagLine: String = "",
    val profileIconId: Long = 0,
    val summonerLevel: Int = 0,
)