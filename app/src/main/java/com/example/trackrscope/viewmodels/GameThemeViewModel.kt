package com.example.trackrscope.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackrscope.utils.GamePreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar la selección del juego y sus preferencias.
 *
 * @param application [Application] de Android.
 */
class GameThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application)

    val selectedGame: MutableStateFlow<Pair<String?, String?>> = MutableStateFlow(Pair(null, null))

    init {
        viewModelScope.launch {
            preferences.game.collect {
                selectedGame.value = it
            }
        }
    }

    /**
     * Guarda la selección del juego y sus preferencias.
     *
     * @param game Nombre del juego.
     * @param theme Tema del juego.
     */
    fun saveGame(game: String, theme: String) {
        viewModelScope.launch {
            preferences.saveSelectedGame(game, theme)
        }
    }
}