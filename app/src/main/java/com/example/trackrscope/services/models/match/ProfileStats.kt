package com.example.trackrscope.services.models.match

import kotlinx.serialization.Serializable

@Serializable
data class ProfileStats(
    val totalGames: Int,
    val lp: Int,
    val kda: String,
    val winRate: String,
    val wins: Int,
    val losses: Int
)

data class SummonerUiProfile(
    val puuid: String,
    val name: String,
    val tag: String,
    val profileIconId: Int,
    val summonerLevel: Int,
    val tier: String,
    val rank: String,
    val lp: Int,
    val hotStreak: Boolean,
    val veteran: Boolean,
    val freshBlood: Boolean,
    val inactive: Boolean
)
