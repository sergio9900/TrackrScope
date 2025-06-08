package com.example.trackrscope.services.models.match

import kotlinx.serialization.Serializable


@Serializable
data class ChampionMasteryDto(
    val championId: Int,
    val championLevel: Int,
    val championPoints: Int,
    val lastPlayTime: Long,
    val championPointsSinceLastLevel: Int,
    val championPointsUntilNextLevel: Int,
    val chestGranted: Boolean,
    val tokensEarned: Int,
    val summonerId: String
)

@Serializable
data class ChampionMastery(
    val id: String,
    val name: String,
    val imageUrl: String,
    val masteryLevel: Int,
    val masteryPoints: Int,
    val chestGranted: Boolean
)