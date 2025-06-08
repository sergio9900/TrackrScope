package com.example.trackrscope.services.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.trackrscope.services.models.game.ChampionImage
import com.example.trackrscope.services.models.game.ChampionSkin
import com.example.trackrscope.services.models.game.ChampionSpell
import com.example.trackrscope.services.utils.ChampionConverters

/**
 * Entidad de la base de datos para las champions.
 */
@Entity(tableName = "champions")
@TypeConverters(ChampionConverters::class)
data class ChampionEntity(
    @PrimaryKey val key: String,
    val id: String,
    val name: String,
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