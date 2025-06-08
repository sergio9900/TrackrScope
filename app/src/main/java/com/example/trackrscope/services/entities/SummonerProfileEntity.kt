package com.example.trackrscope.services.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de la base de datos para el perfil del jugador.
 */
@Entity(tableName = "summoner_profile")
data class SummonerProfileEntity(
    @PrimaryKey val id: String,
    val puuid: String,
    val accountId: String,
    val profileIconId: Int,
    val summonerLevel: Int,
    val revisionDate: Long
)
