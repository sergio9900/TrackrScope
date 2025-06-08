package com.example.trackrscope.services.models.match

import kotlinx.serialization.Serializable

@Serializable
data class MatchDto(
    val metadata: MetadataDto,
    val info: InfoDto
)

@Serializable
data class MetadataDto(
    val dataVersion: String,
    val matchId: String,
    val participants: List<String>
)

@Serializable
data class InfoDto(
    val gameCreation: Long,
    val gameDuration: Long,
    val gameEndTimestamp: Long,
    val gameId: Long,
    val gameMode: String,
    val gameName: String,
    val gameStartTimestamp: Long,
    val gameType: String,
    val mapId: Int,
    val participants: List<ParticipantDto>,
    val queueId: Int,
    val teams: List<TeamDto>,
)

@Serializable
data class ParticipantDto(
    val summonerName: String,
    val puuid: String,
    val championName: String,
    val championId: Int,
    val kills: Int,
    val deaths: Int,
    val assists: Int,
    val totalMinionsKilled: Int,
    val neutralMinionsKilled: Int,
    val champLevel: Int,
    val win: Boolean,
    val goldEarned: Int,
    val teamPosition: String,
    val summoner1Id: Int,
    val summoner2Id: Int,
    val role: String,
    val riotIdGameName: String,
    val riotIdTagline: String,
    val teamId: Int,
    val item0: Int,
    val item1: Int,
    val item2: Int,
    val item3: Int,
    val item4: Int,
    val item5: Int,
    val item6: Int,
    val perks: PerksDto,
    val lane: String
)

@Serializable
data class PerksDto(
    val styles: List<PerkStyle>
)

@Serializable
data class PerkStyle(
    val description: String,
    val selections: List<PerkStyleSelection>,
    val style: Int
)

@Serializable
data class PerkStyleSelection(
    val perk: Int,
    val var1: Int,
    val var2: Int,
    val var3: Int
)

@Serializable
data class TeamDto(
    val teamid: Int,
    val win: Boolean,
    val bans: List<BanDto>,
    val objectives: ObjectivesDto
)

@Serializable
data class ObjectivesDto(
    val atakhan: ObjectiveDto,
    val baron: ObjectiveDto,
    val champion: ObjectiveDto,
    val dragon: ObjectiveDto,
    val inhibitor: ObjectiveDto,
    val horde: ObjectiveDto,
    val riftHerald: ObjectiveDto,
    val tower: ObjectiveDto
)

@Serializable
data class ObjectiveDto(
    val first: Boolean,
    val kills: Int
)

@Serializable
data class BanDto(
    val championId: Int,
    val pickTurn: Int
)