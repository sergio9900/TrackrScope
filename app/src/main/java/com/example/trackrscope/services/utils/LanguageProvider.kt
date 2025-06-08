package com.example.trackrscope.services.utils

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Clase para obtener el idioma del sistema.
 *
 * @param context Contexto de la aplicación.
 */
@Singleton
class LanguageProvider @Inject constructor(@ApplicationContext private val context: Context) {

    /**
     * Obtiene el idioma del sistema.
     */
    fun getSystemLanguage(): String {
        val locale = Locale.getDefault()

        val language = when (locale.language){
            "en" -> if(locale.country == "US")  "en_US" else "en_GB"
            "es" -> "es_ES"
            "fr" -> "fr_FR"
            "de" -> "de_DE"
            else -> "es_ES"
        }

        Log.d("LanguageProvider", "Language: $language")
        return language
    }
}