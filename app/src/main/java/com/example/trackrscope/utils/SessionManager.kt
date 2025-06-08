package com.example.trackrscope.utils

import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Clase que maneja la sesión del usuario.
 */
object SessionManager {

    // Verifica si el usuario está autenticado.
    fun checkSession(
        navController: NavController,
    ) {
        // Obtengo la instancia de FirebaseAuth.
        val auth = FirebaseAuth.getInstance()
        // Verifico si el usuario está autenticado.
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                CoroutineScope(Dispatchers.Main).launch {
                    auth.signOut()
                    // Navego a la pantalla de inicio de sesión.
                    navController.navigate("login_screen") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}