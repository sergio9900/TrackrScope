package com.example.trackrscope.services.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.trackrscope.services.dao.ChampionDao
import com.example.trackrscope.services.dao.RiotAccountDao
import com.example.trackrscope.services.dao.SummonerFullDao
import com.example.trackrscope.services.dao.SummonerProfileDao
import com.example.trackrscope.services.dao.SummonerRankedDao
import com.example.trackrscope.services.entities.ChampionEntity
import com.example.trackrscope.services.entities.RiotAccountEntity
import com.example.trackrscope.services.entities.SummonerProfileEntity
import com.example.trackrscope.services.entities.SummonerRankedEntity
import com.example.trackrscope.services.utils.ChampionConverters

/**
 * Base de datos de la aplicación.
 */
@Database(
    entities = [
        SummonerRankedEntity::class,
        SummonerProfileEntity::class,
        RiotAccountEntity::class,
        ChampionEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(ChampionConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun summonerRankedDao(): SummonerRankedDao
    abstract fun summonerProfileDao(): SummonerProfileDao
    abstract fun riotAccountDao(): RiotAccountDao
    abstract fun summonerFullDao(): SummonerFullDao

    abstract fun championDao(): ChampionDao
}