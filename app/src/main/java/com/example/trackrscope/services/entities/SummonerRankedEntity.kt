package com.example.trackrscope.services.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad de la base de datos para el perfil del jugador de la leaderboard
 */
@Entity(tableName = "summoner_ranked")
data class SummonerRankedEntity(
    @PrimaryKey val summonerId: String,
    val puuid: String,
    val leaguePoints: Int,
    val wins: Int,
    val losses: Int,
    val rank: String,
    val tier: String,
    val hotStreak: Boolean,
    val veteran: Boolean,
    val freshBlood: Boolean,
    val inactive: Boolean,
    val region: String
)