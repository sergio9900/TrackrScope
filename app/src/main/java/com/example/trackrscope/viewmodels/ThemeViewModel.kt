package com.example.trackrscope.viewmodels

import androidx.lifecycle.ViewModel
import com.example.trackrscope.ui.theme.GameTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para el tema del juego.
 */
class ThemeViewModel : ViewModel() {

    // Estado para el tema actual
    private val _currentTheme = MutableStateFlow(GameTheme.DEFAULT)
    val currentTheme: StateFlow<GameTheme> = _currentTheme

    /**
     * Establece el tema actual.
     *
     * @param theme Tema a establecer.
     */
    fun setTheme(theme: GameTheme) {
        _currentTheme.value = theme
    }
}