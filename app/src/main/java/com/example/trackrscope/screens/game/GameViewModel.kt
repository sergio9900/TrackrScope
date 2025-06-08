package com.example.trackrscope.screens.game

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackrscope.services.entities.ChampionEntity
import com.example.trackrscope.services.repository.DataDragonRepository
import com.example.trackrscope.services.utils.VersionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de juego.
 *
 * @param repository Repositorio [DataDragonRepository].
 * @param versionManager Manager de versiones.
 */
@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: DataDragonRepository,
    private val versionManager: VersionManager
) : ViewModel() {

    private val _champions = mutableStateOf<List<ChampionEntity>>(emptyList())
    val champions: State<List<ChampionEntity>> = _champions

    private val _selectedChampion = mutableStateOf<ChampionEntity?>(null)
    val selectedChampion: State<ChampionEntity?> = _selectedChampion

    private val _version = mutableStateOf("")
    val version: State<String> = _version

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Inicializa el ViewModel.
     */
    init {
        viewModelScope.launch {
            versionManager.version.collect { version ->
                _version.value = version.orEmpty()
            }
        }
    }

    /**
     * Obtiene los datos de los campeones.
     */
    fun fetchData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.syncChampions()
                _champions.value = repository.getAllChampions()
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error fetching data: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Carga los detalles de un campeón.
     *
     * @param championKey La clave del campeón.
     */
    fun loadChampionDetails(championKey: String) {
        viewModelScope.launch {
            try {
                _selectedChampion.value = repository.getChampionBykey(championKey)
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading champion details: ${e.message}")
            }
        }
    }

    /**
     * Desmarca el campeón seleccionado.
     */
    fun dismissChampionDetails() {
        _selectedChampion.value = null
    }
}