package com.example.trackrscope.services.repository

import android.annotation.SuppressLint
import android.util.Log
import com.example.trackrscope.services.models.game.Champion
import com.example.trackrscope.services.models.match.ChampionMastery
import com.example.trackrscope.services.models.match.ChampionMasteryDto
import com.example.trackrscope.services.models.match.MatchDto
import com.example.trackrscope.services.models.match.ProfileStats
import com.example.trackrscope.services.models.match.SummonerUiProfile
import com.example.trackrscope.services.models.summoner.SummonerTierResponse
import com.example.trackrscope.services.service.DataDragonService
import com.example.trackrscope.services.service.RiotApiService
import com.example.trackrscope.services.utils.DataDragonRetrofit
import com.example.trackrscope.services.utils.LanguageProvider
import com.example.trackrscope.services.utils.RiotRetrofit
import com.example.trackrscope.services.utils.RiotRetrofitAccount
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio que gestiona la comunicación con la API de Riot, la cual obtiene los datos para el
 * perfil del jugador, estadísticas, partidas recientes y maestrías.
 *
 * @param apiService Servicio que realiza las solicitudes a la API de Riot.
 * @param riotApiServiceAccount Servicio que realiza las solicitudes a la API de Riot.
 * @param api Servicio que realiza las solicitudes a la API de DataDragon.
 */
@Singleton
class SummonerRepository @Inject constructor(
    @RiotRetrofit private val apiService: RiotApiService,
    @RiotRetrofitAccount private val riotApiServiceAccount: RiotApiService,
    private val languageProvider: LanguageProvider,
    @DataDragonRetrofit private val api: DataDragonService,
) {

    // Caché para evitar realizar solicitudes innecesarias
    private val summonerCache = mutableMapOf<String, SummonerTierResponse>()
    private val matchIdsCache = mutableMapOf<String, List<String>>()
    private val matchDetailsCache = mutableMapOf<String, MatchDto>()
    private var championMap: Map<Int, Champion>? = null
    private var cachedChampionVersion: String? = null

    /**
     * Obtiene la última versión de la API de DataDragon.
     *
     * @return Versión de la API de DataDragon.
     */
    suspend fun getLastVersion(): String {
        return api.getVersions().first()
    }


    /**
     * Obtiene el perfil, la cuenta y la clasificación del jugador desde la API DE Riot.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     *
     * @return Objeto [SummonerTierResponse] con los datos del jugador.
     */
    suspend fun getSummonerTierResponse(gameName: String, tagLine: String): SummonerTierResponse? {
        return try {
            // Obtiene los datos de la cuenta con el nombre y el tag del jugador.
            val account = try {
                riotApiServiceAccount.getSummonerAccountByName(gameName, tagLine)
            } catch (e: HttpException) {
                if (e.code() == 404) return null else throw e
            }
            val profile = apiService.getSummonerProfile(account.puuid)
            val rankedList = apiService.getSummonerRanked(account.puuid)
            val ranked = rankedList.firstOrNull { it.queueType == "RANKED_SOLO_5x5" }
            val tier = ranked?.tier ?: "UNRANKED"

            SummonerTierResponse(
                summoner = ranked,
                tier = tier,
                profile = profile,
                account = account
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Caché de [getSummonerTierResponse] para mejorar el rendiemiento de la aplicación.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     */
    suspend fun getSummonerTierResponseCached(
        gameName: String,
        tagLine: String
    ): SummonerTierResponse? {
        val cacheKey = "$gameName#$tagLine"

        return summonerCache[cacheKey] ?: getSummonerTierResponse(gameName, tagLine)?.also {
            summonerCache[cacheKey] = it
        }
    }

    /**
     * Construye el objeto [SummonerUiProfile] con los datos del jugador.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     */
    suspend fun getSummonerUiProfile(gameName: String, tagLine: String): SummonerUiProfile? {
        val response = getSummonerTierResponseCached(gameName, tagLine) ?: return null
        val ranked = response.summoner
        val profile = response.profile
        val account = response.account

        return if (profile != null && account != null) {
            SummonerUiProfile(
                puuid = account.puuid,
                name = account.gameName,
                tag = account.tagLine,
                profileIconId = profile.profileIconId,
                summonerLevel = profile.summonerLevel,
                tier = response.tier,
                rank = ranked?.rank ?: "-",
                lp = ranked?.leaguePoints ?: 0,
                hotStreak = ranked?.hotStreak == true,
                veteran = ranked?.veteran == true,
                freshBlood = ranked?.freshBlood == true,
                inactive = ranked?.inactive == true
            )
        } else {
            null
        }
    }

    /**
     * Devuelve las estadísticas del jugador.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     * @return Objeto [ProfileStats] con las estadísticas del jugador.
     */
    @SuppressLint("DefaultLocale")
    suspend fun getProfileStats(gameName: String, tagLine: String): ProfileStats? {
        val response = getSummonerTierResponseCached(gameName, tagLine) ?: return null
        val puuid = response.account?.puuid ?: return null
        val ranked = response.summoner

        if (ranked != null) {
            val totalGames = ranked.wins + ranked.losses
            val winRate = if (totalGames > 0) "${(ranked.wins * 100 / totalGames)}%" else "0%"
            return ProfileStats(
                totalGames,
                ranked.leaguePoints,
                "0.00:1",
                winRate,
                ranked.wins,
                ranked.losses
            )
        }

        val matchIds = matchIdsCache[puuid] ?: try {
            riotApiServiceAccount.getMatchIdsByPuuid(puuid, count = 10).also {
                matchIdsCache[puuid] = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val matches = matchIds.mapNotNull {
            matchDetailsCache[it] ?: try {
                riotApiServiceAccount.getMatchById(it)
                    .also { match -> matchDetailsCache[it] = match }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        val playerStats = matches.mapNotNull { match ->
            match.info.participants.find { it.puuid == puuid }
        }

        val wins = playerStats.count { it.win }
        val losses = playerStats.size - wins
        val totalGames = playerStats.size

        val winRate = if (totalGames > 0) "${(wins * 100 / totalGames)}%" else "0%"
        val totalKills = playerStats.sumOf { it.kills }
        val totalDeaths = playerStats.sumOf { it.deaths }.coerceAtLeast(1)
        val totalAssists = playerStats.sumOf { it.assists }
        val kda = String.format("%.2f:1", (totalKills + totalAssists).toDouble() / totalDeaths)

        return ProfileStats(totalGames, 0, kda, winRate, wins, losses)
    }


    /**
     * Devuelve las últimas 10 partidas del jugador desde la API de Riot.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     * @return Lista de objetos [MatchDto] con las últimas 10 partidas del jugador.
     */
    suspend fun getLastMatches(gameName: String, tagLine: String): List<MatchDto>? {
        val response = getSummonerTierResponseCached(gameName, tagLine) ?: return null
        val puuid = response.account?.puuid ?: return null

        val matchIds = matchIdsCache[puuid] ?: try {
            riotApiServiceAccount.getMatchIdsByPuuid(puuid).also {
                matchIdsCache[puuid] = it
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        return matchIds.mapNotNull {
            matchDetailsCache[it] ?: try {
                riotApiServiceAccount.getMatchById(it)
                    .also { match -> matchDetailsCache[it] = match }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Devuelve la lista de las maestrías de campeones del jugador.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     * @return Lista de objetos [ChampionMasteryDto] con las maestrías de campeones del jugador.
     */
    suspend fun getMasteries(gameName: String, tagLine: String): List<ChampionMasteryDto>? {
        val response = getSummonerTierResponseCached(gameName, tagLine) ?: return null
        val puuid = response.account?.puuid ?: return null

        return try {
            apiService.getChampionMastery(puuid)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Combina la información de las maestrías y campeones para mostrar en el perfil.
     *
     * @param gameName Nombre del jugador.
     * @param tagLine Tag del jugador.
     * @param version Versión de la API de DataDragon.
     * @return Lista de objetos [ChampionMastery] con la información de las maestrías de campeones del jugador.
     */
    suspend fun getChampionMastery(
        gameName: String,
        tagLine: String,
        version: String
    ): List<ChampionMastery>? {
        val masteries = getMasteries(gameName, tagLine) ?: return null
        val champions = getChampionMap(version)

        return masteries.mapNotNull { mastery ->
            val champion = champions[mastery.championId] ?: return@mapNotNull null
            ChampionMastery(
                name = champion.name,
                imageUrl = "https://ddragon.leagueoflegends.com/cdn/$version/img/champion/${champion.id}.png",
                masteryLevel = mastery.championLevel,
                masteryPoints = mastery.championPoints,
                chestGranted = mastery.chestGranted,
                id = champion.id
            )
        }
    }

    /**
     * Obtiene el mapa de campeones desde la API de DataDragon.
     *
     * @param version Versión de la API de DataDragon.
     * @return Mapa de objetos [Champion] con el mapa de campeones.
     */
    suspend fun getChampionMap(version: String): Map<Int, Champion> {
        val language = languageProvider.getSystemLanguage()

        if (championMap == null || cachedChampionVersion != version) {
            Log.d("Language", "Language: $language")
            val response = api.getChampions(version, language)
            championMap = response.data.mapKeys { it.value.key.toInt() }
            cachedChampionVersion = version
        }
        return championMap!!
    }
}