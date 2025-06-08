package com.example.trackrscope.services.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackrscope.services.entities.SummonerRankedEntity

/**
 * Data Access Object para el perfil del jugador de la leaderboard.
 */
@Dao
interface SummonerRankedDao {

    /**
     * Inserta un perfil del jugador en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ranked: SummonerRankedEntity)

    /**
     * Inserta una lista de perfiles de jugadores en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ranked: List<SummonerRankedEntity>)

    /**
     * Obtiene un perfil del jugador de la base de datos.
     */
    @Query("SELECT * FROM summoner_ranked WHERE region = :region AND tier = :tier AND rank = :rank ORDER BY leaguePoints DESC LIMIT :limit OFFSET :offset")
    suspend fun getByTierAndRank(
        region: String,
        tier: String,
        rank: String,
        limit: Int,
        offset: Int
    ): List<SummonerRankedEntity>

    /**
     * Obtiene un perfil del jugador de la base de datos.
     */
    @Query("DELETE FROM summoner_ranked WHERE region = :region AND tier = :tier AND rank = :rank")
    suspend fun deleteByTierAndRank(region: String, tier: String, rank: String)
}