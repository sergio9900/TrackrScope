package com.example.trackrscope.services.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackrscope.services.entities.SummonerProfileEntity

/**
 * Data Access Object para el perfil del jugador.
 */
@Dao
interface SummonerProfileDao {

    /**
     * Inserta un perfil del jugador en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: SummonerProfileEntity)

    /**
     * Inserta una lista de perfiles de jugadores en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<SummonerProfileEntity>)

    /*
     * Obtiene un perfil del jugador de la base de datos.
     */
    @Query("SELECT * FROM summoner_profile WHERE id = :id")
    suspend fun getById(id: String): SummonerProfileEntity?

}