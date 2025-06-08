package com.example.trackrscope.screens.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trackrscope.services.entities.SummonerFullEntity
import com.example.trackrscope.services.models.match.ChampionMastery
import com.example.trackrscope.services.models.match.MatchDto
import com.example.trackrscope.services.models.match.ProfileStats
import com.example.trackrscope.services.models.match.SummonerUiProfile
import com.example.trackrscope.services.repository.SummonerRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de perfil.
 *
 * @param repository Repositorio [SummonerRepository].
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SummonerRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SummonerProfileUiState())
    val uiState: StateFlow<SummonerProfileUiState> = _uiState.asStateFlow()

    private val _version = mutableStateOf("")
    val version: State<String> = _version

    private val _favoritos = mutableStateMapOf<String, Boolean>()
    val favoritos: Map<String, Boolean> = _favoritos

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Inicializa el ViewModel.
     */
    init {
        viewModelScope.launch {
            _version.value = repository.getLastVersion()
        }
    }

    /**
     * Obtiene el perfil del summoner.
     *
     * @param gameName Nombre del juego.
     * @param tagLine Tag del summoner.
     */
    fun fetchSummonerProfile(gameName: String, tagLine: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val profile = repository.getSummonerUiProfile(gameName, tagLine)
            val stats = repository.getProfileStats(gameName, tagLine)
            val matches = repository.getLastMatches(gameName, tagLine)
            val champions = repository.getChampionMastery(gameName, tagLine, version.value)

            val kda = calcularKda(matches ?: emptyList(), profile?.puuid ?: "")
            val profileStats = stats?.copy(kda = kda)

            if (profile != null) {
                _uiState.value = SummonerProfileUiState(
                    isLoading = false,
                    profile = profile,
                    stats = profileStats,
                    matches = matches ?: emptyList(),
                    champions = champions ?: emptyList(),
                    error = null
                )
            } else {
                _uiState.value = SummonerProfileUiState(
                    isLoading = false,
                    profile = null,
                    error = "Error: No se pudo obtener el perfil."
                )
            }
        }
    }

    /**
     * Comprueba si el perfil del home esta en favoritos.
     *
     * @param summoner [SummonerUiProfile] a comprobar.
     */
    fun comprobarFavoritosPerfil(summoner: SummonerUiProfile) {
        val docId = "${summoner.name}#${summoner.tag}"

        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid

                if (userId != null && !FirebaseAuth.getInstance().currentUser?.isAnonymous!!) {
                    val db = FirebaseFirestore.getInstance().collection("Usuarios").document(userId)
                        .collection("Favoritos").document(docId)

                    db.get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val document = task.result
                            _favoritos[docId] = document.exists()
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("Error", "Error al comprobar favoritos")
                e.printStackTrace()
            }
        }
    }

    /**
     * Añade un summoner a los favoritos.
     *
     * @param profile [SummonerUiProfile] a añadir.
     */
    fun addFavoriteProfile(profile: SummonerUiProfile) {
        viewModelScope.launch {
            try {

                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null && !FirebaseAuth.getInstance().currentUser?.isAnonymous!!) {
                    val docId = "${profile.name}#${profile.tag}"

                    val favoriteData = hashMapOf(
                        "gameName" to profile.name,
                        "tagLine" to profile.tag,
                        "profileIconId" to profile.profileIconId,
                        "summonerLevel" to profile.summonerLevel,
                    )

                    FirebaseFirestore.getInstance()
                        .collection("Usuarios")
                        .document(userId)
                        .collection("Favoritos")
                        .document(docId)
                        .set(favoriteData)
                        .addOnSuccessListener {
                            Log.d("HomeViewModel", "Profile added to favorites successfully")
                            _favoritos[docId] = true
                        }
                        .addOnFailureListener { e ->
                            Log.e("HomeViewModel", "Error adding profile to favorites", e)
                        }
                }
            } catch (e: Exception) {
                Log.e("Error", "Error al añadir favorito")
                e.printStackTrace()
            }
        }
    }

    /**
     * Elimina un summoner de los favoritos.
     *
     * @param profile [SummonerUiProfile] a eliminar.
     */
    fun removeFavoriteProfile(profile: SummonerUiProfile) {
        viewModelScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                if (userId != null && !FirebaseAuth.getInstance().currentUser?.isAnonymous!!) {
                    val docId = "${profile.name}#${profile.tag}"

                    FirebaseFirestore.getInstance()
                        .collection("Usuarios")
                        .document(userId)
                        .collection("Favoritos")
                        .document(docId)
                        .delete()
                        .addOnSuccessListener {
                            Log.d("HomeViewModel", "Profile removed from favorites successfully")
                            _favoritos[docId] = false
                        }
                        .addOnFailureListener { e ->
                            Log.e("HomeViewModel", "Error removing profile from favorites", e)
                        }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error removing profile from favorites: ${e.message}")
            }
        }
    }

    /**
     * Añade un summoner a los favoritos.
     *
     * @param summoner [SummonerFullEntity] a añadir.
     */
    fun addFavorites(summoner: SummonerFullEntity) {

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val favoriteData = hashMapOf(
            "gameName" to summoner.account.gameName,
            "tagLine" to summoner.account.tagLine,
            "profileIconId" to summoner.profile.profileIconId,
            "summonerLevel" to summoner.profile.summonerLevel,
        )

        db.collection("Usuarios").document(userId).collection("Favoritos")
            .document("${summoner.account.gameName}#${summoner.account.tagLine}")
            .set(favoriteData).addOnSuccessListener {
                Log.d("Firestore", "Datos guardados correctamente")
            }.addOnFailureListener {
                Log.w("Firestore", "Error al guardar los datos", it)
            }
    }

    /**
     * Elimina un summoner de los favoritos.
     *
     * @param summoner [SummonerFullEntity] a eliminar.
     */
    fun removeFavorites(summoner: SummonerFullEntity) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val docId = "${summoner.account.gameName}#${summoner.account.tagLine}"

        db.collection("Usuarios").document(userId)
            .collection("Favoritos").document(docId).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Datos eliminados correctamente")
            }.addOnFailureListener {
                Log.w("Firestore", "Error al eliminar los datos")
            }
    }

    /**
     * Comprueba si un summoner está en los favoritos.
     *
     * @param summoner [SummonerFullEntity] a comprobar.
     */
    fun comprobarFavoritos(summoner: SummonerFullEntity) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()

        val docId = "${summoner.account.gameName}#${summoner.account.tagLine}"

        db.collection("Usuarios").document(userId)
            .collection("Favoritos").document(docId).get()
            .addOnSuccessListener {
                _favoritos[docId] = it.exists()
            }.addOnFailureListener {
                _favoritos[docId] = false
            }
    }
}

/**
 * Calcula la KDA de un summoner.
 *
 * @param matches Lista de [MatchDto].
 * @param summonerPuuid Puuid del summoner.
 * @return KDA del summoner.
 */
@SuppressLint("DefaultLocale")
fun calcularKda(matches: List<MatchDto>, summonerPuuid: String): String {
    val players = matches.mapNotNull { match ->
        match.info.participants.find { it.puuid == summonerPuuid }
    }

    if (players.isEmpty()) return "0.00:1"

    val kills = players.sumOf { it.kills }
    val deaths = players.sumOf { it.deaths }.coerceAtLeast(1)
    val assists = players.sumOf { it.assists }

    val kda = (kills + assists).toFloat() / deaths
    return String.format("%.2f", kda) + ":1"
}

/**
 * UiState del perfil del summoner.
 */
data class SummonerProfileUiState(
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = false,
    val profile: SummonerUiProfile? = null,
    val profileFull: SummonerFullEntity? = null,
    val error: String? = null,
    val stats: ProfileStats? = null,
    val matches: List<MatchDto> = emptyList(),
    val champions: List<ChampionMastery> = emptyList()
)