package com.example.trackrscope.services.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackrscope.services.entities.ChampionEntity

/**
 * Data Access Object para las champions.
 */
@Dao
interface ChampionDao {

    /**
     * Obtiene todas las champions de la base de datos.
     */
    @Query("SELECT * FROM champions")
    suspend fun getAllChampions(): List<ChampionEntity>

    /**
     * Obtiene una champion de la base de datos.
     */
    @Query("SELECT * FROM champions WHERE `key` = :championKey LIMIT 1")
    suspend fun getChampionByKey(championKey: String): ChampionEntity?

    /**
     * Inserta una lista de champions en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChampions(champions: List<ChampionEntity>)

    /**
     * Inserta una champion en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(champion: ChampionEntity)
}