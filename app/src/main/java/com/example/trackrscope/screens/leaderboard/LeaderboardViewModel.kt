package com.example.trackrscope.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackrscope.services.entities.SummonerFullEntity
import com.example.trackrscope.services.repository.LeaderboardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de leaderboard.
 *
 * @param repository Repositorio [LeaderboardRepository].
 */
@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val repository: LeaderboardRepository
) : ViewModel() {

    private val _summoners = MutableStateFlow<List<SummonerFullEntity>>(emptyList())
    val summoners: StateFlow<List<SummonerFullEntity>> = _summoners

    // LISTA
    val regions =
        listOf("EUW1", "BR1", "EUN1", "JP1", "KR", "LA1", "LA2", "NA1", "OC1", "TR1", "RU")
    val divisions = listOf("I", "II", "III", "IV")
    val tiers = listOf(
        "CHALLENGER",
        "GRANDMASTER",
        "MASTER",
        "DIAMOND",
        "EMERALD",
        "PLATINUM",
        "GOLD",
        "SILVER",
        "BRONZE",
        "IRON"
    )

    // FILTROS
    private val _selectedRegion = MutableStateFlow(regions.first())
    val selectedRegion: StateFlow<String> = _selectedRegion

    private val _selectedTier = MutableStateFlow(tiers.first())
    val selectedTier: StateFlow<String> = _selectedTier

    private val _selectedDivision = MutableStateFlow<String>(divisions.first())
    val selectedDivision: StateFlow<String> = _selectedDivision

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage

    private val usuariosPorPagina = 25

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isrefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isrefreshing.asStateFlow()

    /**
     * Lista de summoners paginados.
     */
    val paginatedSummoners: StateFlow<List<SummonerFullEntity>> =
        combine(summoners, _currentPage) { allSummoners, page ->
            val fromIndex = page * usuariosPorPagina
            val toIndex = minOf(fromIndex + usuariosPorPagina, allSummoners.size)

            if (fromIndex >= allSummoners.size) {
                emptyList()
            } else {
                allSummoners.subList(fromIndex, toIndex)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeFilters()
    }

    /**
     * Observador de los filtros.
     */
    @OptIn(FlowPreview::class)
    fun observeFilters() {
        viewModelScope.launch {
            combine(_selectedRegion, _selectedTier, _selectedDivision) { region, tier, division ->
                Triple(region, tier, division)
            }.debounce(500L).distinctUntilChanged().collect { (region, tier, division) ->
                delay(300)
                loadSummoners(region, tier, division)
            }
        }
    }

    /**
     * Carga los summoners desde el repositorio.
     *
     * @param region La región.
     * @param tier El tier.
     * @param division La división.
     * @param refresh Si se debe refrescar.
     * @param loading Si se debe mostrar el loading.
     */
    fun loadSummoners(
        region: String,
        tier: String,
        division: String,
        refresh: Boolean = false,
        loading: Boolean = true
    ) {
        viewModelScope.launch {
            if (loading) _isLoading.value = true
            try {
                if (refresh) {
                    repository.fetchLeaderboard(region, tier, division, limit = 25)
                    val updated = repository.getLeaderboard(region, tier, division, 25, 0)
                    _summoners.value = updated
                } else {
                    val cached = repository.getLeaderboard(region, tier, division, 25, 0)

                    if (cached.isNotEmpty()) {
                        _summoners.value = cached
                    } else {
                        repository.fetchLeaderboard(region, tier, division, limit = 25)
                        val updated = repository.getLeaderboard(region, tier, division, 25, 0)
                        _summoners.value = updated
                    }
                }
                _currentPage.value = 0
            } catch (e: Exception) {
                e.printStackTrace()
                _summoners.value = emptyList()
            } finally {
                if(loading) _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza la región.
     *
     * @param region La región.
     */
    fun updateRegion(region: String) {
        _selectedRegion.value = region
    }

    /**
     * Actualiza el tier.
     *
     * @param tier El tier.
     */
    fun updateTier(tier: String) {
        if (_selectedTier.value != tier) {
            _selectedTier.value = tier
            if (tier in listOf("CHALLENGER", "GRANDMASTER", "MASTER")) {
                _selectedDivision.value = "I"
            } else {
                _selectedDivision.value = divisions.first()
            }
        }
    }

    /**
     * Actualiza la división.
     *
     * @param division La división.
     */
    fun updateDivision(division: String) {
        if(_selectedTier.value != "CHALLENGER" && _selectedTier.value != "GRANDMASTER" && _selectedTier.value != "MASTER"){
            if (division != _selectedDivision.value) {
                _selectedDivision.value = division
            }
        } else {
            _selectedDivision.value = "I"
        }
    }

    fun nextPage() {
        _currentPage.value++
    }

    fun previousPage() {
        if (_currentPage.value > 0) {
            _currentPage.value--
        }
    }

    /**
     * Refresca los datos.
     */
    fun refreshData() {
        viewModelScope.launch {
            _isrefreshing.value = true
            try {
                loadSummoners(
                    region = _selectedRegion.value,
                    tier = _selectedTier.value,
                    division = _selectedDivision.value,
                    refresh = true
                )
            } finally {
                _isrefreshing.value = false
            }
        }
    }
}