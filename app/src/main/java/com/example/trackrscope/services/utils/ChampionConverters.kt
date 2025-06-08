package com.example.trackrscope.services.utils

import androidx.room.TypeConverter
import com.example.trackrscope.services.models.game.ChampionImage
import com.example.trackrscope.services.models.game.ChampionSkin
import com.example.trackrscope.services.models.game.ChampionSpell
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Converters para la base de datos.
 */
class ChampionConverters {

    private val gson = Gson()

    /**
     * Convierte un objeto ChampionImage a una cadena JSON.
     */
    @TypeConverter
    fun fromChampionImage(value: ChampionImage): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON a un objeto ChampionImage.
     */
    @TypeConverter
    fun toChampionImage(value: String): ChampionImage =
        gson.fromJson(value, ChampionImage::class.java)

    /**
     * Convierte una lista de ChampionSpell a una cadena JSON.
     */
    @TypeConverter
    fun fromChampionSpellList(value: List<ChampionSpell>): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON a una lista de ChampionSpell.
     */
    @TypeConverter
    fun toChampionSpellList(value: String): List<ChampionSpell> {
        val listType = object : TypeToken<List<ChampionSpell>>() {}.type
        return gson.fromJson(value, listType)
    }

    /**
     * Convierte un objeto ChampionSpell a una cadena JSON.
     */
    @TypeConverter
    fun fromChampionSpell(value: ChampionSpell): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON a un objeto ChampionSpell.
     */
    @TypeConverter
    fun toChampionSpell(value: String): ChampionSpell =
        gson.fromJson(value, ChampionSpell::class.java)

    /**
     * Convierte una lista de ChampionSkin a una cadena JSON.
     */
    @TypeConverter
    fun fromChampionSkinList(value: List<ChampionSkin>): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON a una lista de ChampionSkin.
     */
    @TypeConverter
    fun toChampionSkinList(value: String): List<ChampionSkin> {
        val listType = object : TypeToken<List<ChampionSkin>>() {}.type
        return gson.fromJson(value, listType)
    }

    /**
     * Convierte un objeto ChampionSkin a una cadena JSON.
     */
    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    /**
     * Convierte una cadena JSON a una lista de Strings.
     */
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
}