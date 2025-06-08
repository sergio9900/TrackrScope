package com.example.trackrscope.services.models.summoner

import com.example.trackrscope.services.entities.RiotAccountEntity
import com.example.trackrscope.services.entities.SummonerProfileEntity
import com.example.trackrscope.services.entities.SummonerRankedEntity
import kotlinx.serialization.Serializable

@Serializable
data class SummonerRanked(
    val queueType: String,
    val summonerId: String,
    val puuid: String,
    val tier: String,
    val leaguePoints: Int,
    val wins: Int,
    val losses: Int,
    val rank: String,
    val hotStreak: Boolean,
    val veteran: Boolean,
    val freshBlood: Boolean,
    val inactive: Boolean,
)

@Serializable
data class SummonerTierResponse(
    val summoner: SummonerRanked?,
    val tier: String,
    val profile: SummonerProfile?,
    val account: RiotAccount?,
)

@Serializable
data class SummonerProfile(
    val id: String,
    val puuid: String,
    val accountId: String,
    val profileIconId: Int,
    val summonerLevel: Int,
    val revisionDate: Long,
)

@Serializable
data class RiotAccount(
    val puuid: String,
    val gameName: String,
    val tagLine: String,
)

fun SummonerTierResponse.toEntities(region: String): Triple<SummonerRankedEntity, SummonerProfileEntity, RiotAccountEntity>? {

    val ranked = summoner ?: return null
    val profile = profile ?: return null
    val account = account ?: return null

    val rankedEntity = SummonerRankedEntity(
        summonerId = ranked.summonerId,
        puuid = ranked.puuid,
        leaguePoints = ranked.leaguePoints,
        wins = ranked.wins,
        losses = ranked.losses,
        tier = tier,
        rank = ranked.rank,
        hotStreak = ranked.hotStreak,
        veteran = ranked.veteran,
        freshBlood = ranked.freshBlood,
        inactive = ranked.inactive,
        region = region,
    )

    val profileEntity = SummonerProfileEntity(
        id = profile.id,
        accountId = profile.accountId,
        puuid = profile.puuid,
        profileIconId = profile.profileIconId,
        summonerLevel = profile.summonerLevel,
        revisionDate = profile.revisionDate,
    )

    val accountEntity = RiotAccountEntity(
        puuid = account.puuid,
        gameName = account.gameName,
        tagLine = account.tagLine,
    )

    return Triple(rankedEntity, profileEntity, accountEntity)
}