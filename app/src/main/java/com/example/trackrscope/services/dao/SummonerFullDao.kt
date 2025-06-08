package com.example.trackrscope.services.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.example.trackrscope.services.entities.SummonerFullEntity

/**
 * Data Access Object para el perfil del jugador.
 */
@Dao
interface SummonerFullDao {

    /**
     * Obtiene un perfil del jugador de la base de datos.
     */
    @Transaction
    @Query("SELECT * FROM summoner_ranked WHERE region = :region AND tier = :tier AND rank = :rank ORDER BY leaguePoints DESC LIMIT :limit OFFSET :offset")
    suspend fun getFullLeaderboard(
        region: String,
        tier: String,
        rank: String,
        limit: Int,
        offset: Int
    ): List<SummonerFullEntity>

    /**
     * Obtiene un perfil del jugador de la base de datos.
     */
    @Query("SELECT * FROM summoner_ranked WHERE puuid = :puuid LIMIT 1")
    suspend fun getSummonerByPuuid(puuid: String): SummonerFullEntity?

}