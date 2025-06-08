package com.example.trackrscope.screens.auth.recoverpassword

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.trackrscope.R

/**
 * ViewModel para la pantalla de recuperación de contraseña.
 */
class RecoverPasswordViewModel : ViewModel() {

    val email: MutableState<String> = mutableStateOf("") // Estado para el email.

    // Estado para habilitar el botón de registro.
    val isEmailEnabled: MutableState<Boolean> = mutableStateOf(false)

    val isEmailValid: MutableState<Int> = mutableIntStateOf(R.color.secundario_login)

    /**
     * Inicializa el ViewModel.
     */
    init {
        email.value = ""
        updateEmailButtonState()
    }

    /**
     * Función para manejar formulario de registro.
     */
    private fun updateEmailButtonState() {
        isEmailEnabled.value = isEmailValid(email.value)
    }

    /**
     * Función para actualizar el valor del campo Email.
     *
     * @param email Nuevo valor del campo Email.
     */
    fun onEmailChanged(email: String) {
        if (email == "") {
            isEmailValid.value = R.color.secundario_login
        } else {
            if (isEmailValid(email)) {
                isEmailValid.value = R.color.green
            } else {
                isEmailValid.value = R.color.red
            }
        }
        this.email.value = email
        updateEmailButtonState()
    }

    /**
     * Función para comprobar si el campo es válido.
     *
     * @param email Campo a comprobar.
     * @return True si el campo es válido, false en caso contrario.
     */
    private fun isEmailValid(email: String): Boolean {
        val pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(pattern.toRegex())
    }
}