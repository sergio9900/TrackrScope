package com.example.trackrscope.services.service

import com.example.trackrscope.services.models.match.ChampionMasteryDto
import com.example.trackrscope.services.models.match.MatchDto
import com.example.trackrscope.services.models.summoner.RiotAccount
import com.example.trackrscope.services.models.summoner.SummonerProfile
import com.example.trackrscope.services.models.summoner.SummonerRanked
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interfaz para las peticiones a la api de Riot.
 */
interface RiotApiService {

    // Obtengo la leaderboard por Cola, Tier y División.
    @GET("/lol/league-exp/v4/entries/{queue}/{tier}/{division}")
    suspend fun getLeaderboardByTier(
        @Path("queue") queue: String,
        @Path("tier") tier: String,
        @Path("division") division: String,
        @Query("page") page: Int = 1
    ): List<SummonerRanked>

    // Obtengo el perfil del jugador por su Puuid.
    @GET("/lol/summoner/v4/summoners/by-puuid/{encryptedPUUID}")
    suspend fun getSummonerProfile(
        @Path("encryptedPUUID") encryptedPUUID: String
    ): SummonerProfile

    // Obtengo la cuenta del jugador por su Puuid.
    @GET("/riot/account/v1/accounts/by-puuid/{puuid}")
    suspend fun getSummonerAccount(
        @Path("puuid") puuid: String
    ): RiotAccount

    // Obtengo la cuenta del jugador por su nombre y tag.
    @GET("/riot/account/v1/accounts/by-riot-id/{gameName}/{tagLine}")
    suspend fun getSummonerAccountByName(
        @Path("gameName") name: String,
        @Path("tagLine") tagLine: String
    ): RiotAccount

    // Obtengo los datos de las rankeds del jugador por su Puuid.
    @GET("/lol/league/v4/entries/by-puuid/{encryptedPUUID}")
    suspend fun getSummonerRanked(
        @Path("encryptedPUUID") encryptedPUUID: String
    ): List<SummonerRanked>

    // Obtengo los datos de las maestrias de los campeones del jugador por su Puuid.
    @GET("/lol/champion-mastery/v4/champion-masteries/by-puuid/{encryptedPUUID}")
    suspend fun getChampionMastery(
        @Path("encryptedPUUID") puuid: String
    ): List<ChampionMasteryDto>

    // Obtengo las ids de las últimas partidas de un jugador.
    @GET("lol/match/v5/matches/by-puuid/{puuid}/ids")
    suspend fun getMatchIdsByPuuid(
        @Path("puuid") puuid: String,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 10,
    ): List<String>

    // Obtengo los datos de una partida por su id.
    @GET("lol/match/v5/matches/{matchId}")
    suspend fun getMatchById(
        @Path("matchId") matchId: String
    ): MatchDto
}