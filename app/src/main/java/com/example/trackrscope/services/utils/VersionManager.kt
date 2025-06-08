package com.example.trackrscope.services.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Objeto para las claves de las preferencias de versión de champion.
 */
object VersionPreferences {
    val SELECTED_VERSION = stringPreferencesKey("champion_version")
}

/**
 * Clase para guardar y leer la versión de champion seleccionada.
 *
 * @param context Contexto de la aplicación.
 */
@Singleton
class VersionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore by preferencesDataStore(name = "data_dragon")

    val version: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[VersionPreferences.SELECTED_VERSION]
    }

    suspend fun setVersion(version: String) {
        context.dataStore.edit { preferences ->
            preferences[VersionPreferences.SELECTED_VERSION] = version
        }
    }

    suspend fun getVersion(): String? = version.firstOrNull()
}