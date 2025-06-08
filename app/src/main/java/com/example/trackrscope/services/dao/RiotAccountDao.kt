package com.example.trackrscope.services.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.trackrscope.services.entities.RiotAccountEntity

/**
 * Data Access Object para la cuenta de un jugador.
 */
@Dao
interface RiotAccountDao {

    /**
     * Inserta una cuenta de jugador en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: RiotAccountEntity)

    /**
     * Inserta una lista de cuentas de jugadores en la base de datos.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<RiotAccountEntity>)

    /**
     * Obtiene una cuenta de jugador de la base de datos.
     */
    @Query("SELECT * FROM riot_account WHERE puuid = :puuid")
    suspend fun getByPuuid(puuid: String): RiotAccountEntity?
}