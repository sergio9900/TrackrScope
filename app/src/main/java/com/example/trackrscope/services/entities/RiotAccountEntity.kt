package com.example.trackrscope.services.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de la base de datos para la cuenta de un jugador.
 */
@Entity(tableName = "riot_account")
data class RiotAccountEntity(
    @PrimaryKey val puuid: String,
    val gameName: String,
    val tagLine: String,
)
