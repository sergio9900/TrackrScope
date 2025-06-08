package com.example.trackrscope.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Objeto para las claves de las preferencias de juego.
 */
object GamePreferencesKeys {
    val SELECTED_GAME = stringPreferencesKey("selected_game")
    val SELECTED_THEME = stringPreferencesKey("selected_theme")
}

/**
 * Clase para guardar y leer el juego y el tema seleccionado
 *
 * @param context Contexto de la aplicación
 */
class GamePreferences @Inject constructor(private val context: Context) {

    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "game_preferences")

    // Guardo el juego y su tema seleccionado
    suspend fun saveSelectedGame(game: String, theme: String) {
        context.datastore.edit { preferences ->
            preferences[GamePreferencesKeys.SELECTED_GAME] = game
            preferences[GamePreferencesKeys.SELECTED_THEME] = theme
        }
    }

    // Leo el juego y el tema seleccionado
    val game: Flow<Pair<String?, String?>> = context.datastore.data.map { preferences ->
        val game = preferences[GamePreferencesKeys.SELECTED_GAME]
        val theme = preferences[GamePreferencesKeys.SELECTED_THEME]
        Pair(game, theme)
    }
}
