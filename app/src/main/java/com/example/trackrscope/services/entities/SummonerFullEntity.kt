package com.example.trackrscope.services.entities

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Entidad de la base de datos para el perfil del jugador.
 */
data class SummonerFullEntity(
    @Embedded val ranked: SummonerRankedEntity,

    @Relation(
        parentColumn = "summonerId",
        entityColumn = "id"
    )
    val profile: SummonerProfileEntity,

    @Relation(
        parentColumn = "puuid",
        entityColumn = "puuid"
    )
    val account: RiotAccountEntity
)
