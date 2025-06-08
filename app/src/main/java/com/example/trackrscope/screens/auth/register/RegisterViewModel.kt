package com.example.trackrscope.screens.auth.register

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.trackrscope.R

/**
 * ViewModel para la pantalla de registro.
 */
class RegisterViewModel : ViewModel() {

    val username: MutableState<String> = mutableStateOf("") // Estado para el nombre de usuario.
    val email: MutableState<String> = mutableStateOf("") // Estado para el email.
    val password: MutableState<String> = mutableStateOf("") // Estado para la contraseña.
    val confirmPassword: MutableState<String> =
        mutableStateOf("") // Estado para la confirmación de la contraseña.

    val isEmailValid: MutableState<Int> = mutableIntStateOf(R.color.secundario_login)

    // Estado para habilitar el botón de registro.
    val isRegisterEnabled: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Inicializa el ViewModel.
     */
    init {
        username.value = ""
        email.value = ""
        password.value = ""
        confirmPassword.value = ""
        updateRegisterButtonState()
    }

    /**
     * Función para manejar formulario de registro.
     */
    private fun updateRegisterButtonState() {
        isRegisterEnabled.value =
            isValid(username.value) && validarEmail(email.value) && isValid(password.value) && isValid(
                confirmPassword.value
            ) && doPasswordsMatch()
    }

    /**
     * Función para comprobar si las contraseñas coinciden.
     *
     * @return True si las contraseñas coinciden, false en caso contrario.
     */
    private fun doPasswordsMatch(): Boolean {
        return password.value == confirmPassword.value
    }

    /**
     * Función para actualizar el valor del campo Username.
     *
     * @param username Nuevo valor del campo Username.
     */
    fun onUsernameChanged(username: String) {
        this.username.value = username
        updateRegisterButtonState()
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
            if (validarEmail(email)) {
                isEmailValid.value = R.color.green
            } else {
                isEmailValid.value = R.color.red
            }
        }
        this.email.value = email
        updateRegisterButtonState()
    }

    /**
     * Función para actualizar el valor del campo ConfirmPassword.
     *
     * @param confirmPassword Nuevo valor del campo ConfirmPassword.
     */
    fun onConfirmPasswordChanged(confirmPassword: String) {
        this.confirmPassword.value = confirmPassword
        updateRegisterButtonState()
    }

    /**
     * Función para actualizar el valor del campo Password.
     *
     * @param password Nuevo valor del campo Password.
     */
    fun onPasswordChanged(password: String) {
        this.password.value = password
        updateRegisterButtonState()
    }

    /**
     * Función para comprobar si el campo esta vacío.
     *
     * @param valor Campo a comprobar.
     * @return True si el campo esta vacío, false en caso contrario.
     */
    private fun isValid(valor: String): Boolean {
        return valor.isNotEmpty()
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
}