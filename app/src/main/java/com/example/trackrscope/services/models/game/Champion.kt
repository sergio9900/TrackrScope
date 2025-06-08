package com.example.trackrscope.services.models.game

import com.example.trackrscope.services.entities.ChampionEntity
import kotlinx.serialization.Serializable

@Serializable
data class ChampionsResponse(
    val data: Map<String, Champion>
)


@Serializable
data class ChampionResponse(
    val data: Map<String, Champion>
)

@Serializable
data class Champion(
    val id: String,
    val name: String,
    val key: String,
    val title: String,
    val lore: String,
    val image: ChampionImage,
    val spells: List<ChampionSpell>,
    val passive: ChampionSpell,
    val allytips: List<String>,
    val enemytips: List<String>,
    val tags: List<String>,
    val skins: List<ChampionSkin>,
)

@Serializable
data class ChampionImage(
    val full: String,
    val sprite: String,
)

@Serializable
data class ChampionSpell(
    val id: String,
    val name: String,
    val description: String,
    val image: ChampionImage,
    val cooldown: List<Float>,
    val cost: List<Int>,
)

@Serializable
data class ChampionSkin(
    val id: String,
    val num: Int,
    val name: String,
)

fun Champion.toEntity(): ChampionEntity {
    return ChampionEntity(
        key = this.key,
        id = this.id,
        name = this.name,
        title = this.title,
        lore = this.lore,
        image = this.image,
        spells = this.spells,
        passive = this.passive,
        allytips = this.allytips,
        enemytips = this.enemytips,
        tags = this.tags,
        skins = this.skins,
    )
}

fun ChampionsResponse.toEntityList(): List<ChampionEntity> {
    return data.values.map { it.toEntity() }
}

fun ChampionEntity.toChampion(): Champion {
    return Champion(
        id = id,
        name = name,
        key = key,
        title = title,
        lore = lore,
        image = image,
        spells = spells,
        passive = passive,
        allytips = allytips,
        enemytips = enemytips,
        tags = tags,
        skins = skins,
    )
}