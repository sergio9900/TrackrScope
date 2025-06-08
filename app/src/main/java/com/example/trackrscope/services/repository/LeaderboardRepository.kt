package com.example.trackrscope.services.repository

import android.util.Log
import com.example.trackrscope.services.dao.RiotAccountDao
import com.example.trackrscope.services.dao.SummonerFullDao
import com.example.trackrscope.services.dao.SummonerProfileDao
import com.example.trackrscope.services.dao.SummonerRankedDao
import com.example.trackrscope.services.entities.SummonerFullEntity
import com.example.trackrscope.services.models.summoner.SummonerTierResponse
import com.example.trackrscope.services.models.summoner.toEntities
import com.example.trackrscope.services.service.RiotApiService
import com.example.trackrscope.services.utils.RiotRetrofit
import com.example.trackrscope.services.utils.RiotRetrofitAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Repositorio que gestiona la comunicación con la API de Riot, la cual obtiene los datos de los
 * mejores jugadores de cada división y los guarda en la base de datos para almacenarlos en caché.
 *
 * @param apiService Servicio que realiza las solicitudes a la API de Riot.
 * @param riotApiServiceAccount Servicio que realiza las solicitudes a la API de Riot.
 * @param rankedDao Dao que realiza las operaciones de base de datos para los mejores jugadores.
 * @param profileDao Dao que realiza las operaciones de base de datos para los perfiles de los jugadores.
 * @param accountDao Dao que realiza las operaciones de base de datos para las cuentas de los jugadores.
 * @param summonerFullDao Dao que realiza las operaciones de base de datos para los jugadores completos.
 */
class LeaderboardRepository @Inject constructor(
    @RiotRetrofit private val apiService: RiotApiService,
    @RiotRetrofitAccount private val riotApiServiceAccount: RiotApiService,
    private val rankedDao: SummonerRankedDao,
    private val profileDao: SummonerProfileDao,
    private val accountDao: RiotAccountDao,
    private val summonerFullDao: SummonerFullDao
) {


    /**
     * Obtiene el leaderboard desde la API de Riot, los guarda en la base de datos y los devuelve.
     *
     * @param region Región del servidor de Riot.
     * @param tier Tier del leaderboard.
     * @param division División del leaderboard.
     * @param queue Tipo de partida del leaderboard.
     * @param page Página del leaderboard.
     * @param limit Cantidad de jugadores por página.
     */
    suspend fun fetchLeaderboard(
        region: String,
        tier: String,
        division: String,
        queue: String = "RANKED_SOLO_5x5",
        page: Int = 1,
        limit: Int = 25
    ) {
        try {
            // Obtiene la lista de jugadores del leaderboard desde la API de Riot.
            val rankedList = apiService.getLeaderboardByTier(queue, tier, division, page)
            val rankedListLimit = rankedList.take(limit)
            val semaphore = Semaphore(5)

            // Realiza peticiones para obtener el perfil y la cuenta de cada jugador.
            val fullResponse = coroutineScope {
                rankedListLimit.map { ranked ->
                    async {
                        semaphore.withPermit {
                            try {
                                val profile = apiService.getSummonerProfile(ranked.puuid)
                                val account = riotApiServiceAccount.getSummonerAccount(ranked.puuid)

                                SummonerTierResponse(
                                    summoner = ranked,
                                    profile = profile,
                                    account = account,
                                    tier = tier
                                )
                            } catch (e: Exception) {
                                Log.e(
                                    "LeaderboardRepository",
                                    "Error fetching summoner: ${e.message}"
                                )
                                null
                            }
                        }
                    }
                }.mapNotNull { it.await() } // Filtra las posibles respuestas nulas
            }
            // Guarda los datos en la base de datos
            saveLeaderboard(fullResponse, region)

        } catch (e: Exception) {
            Log.e("LeaderboardRepository", "Error fetching leaderboard: ${e.message}")
        }
    }

    /**
     * Guarda los datos de los jugadores en la base de datos.
     *
     * @param response Lista de objetos [SummonerTierResponse] que contienen los datos de los jugadores.
     * @param region Región del servidor de Riot.
     */
    suspend fun saveLeaderboard(response: List<SummonerTierResponse>, region: String) {
        response.mapNotNull { it.toEntities(region) }.forEach { (ranked, profile, account) ->
            rankedDao.insert(ranked)
            profileDao.insert(profile)
            accountDao.insert(account)
        }
    }

    /**
     * Obtiene la lista completa de jugadores del leaderboard desde la base de datos.
     *
     * @param region Región del servidor de Riot.
     * @param tier Tier del leaderboard.
     * @param division División del leaderboard.
     * @param limit Cantidad de jugadores por página.
     * @param offset Desplazamiento en la lista de jugadores. - No esta implementado de momento.
     * @return Lista de objetos [SummonerFullEntity] que contienen los datos de los jugadores.
     */
    suspend fun getLeaderboard(
        region: String, tier: String, division: String, limit: Int, offset: Int
    ): List<SummonerFullEntity> = withContext(Dispatchers.IO) {
        summonerFullDao.getFullLeaderboard(region, tier, division, limit, offset)
    }
}