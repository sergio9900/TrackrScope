package com.example.trackrscope.services.module

import android.content.Context
import androidx.room.Room
import com.example.trackrscope.services.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de la app para proveer dependencias globales de la base de datos.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Proveedor de la base de datos.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "trackrscope_database"
        ).build()
    }

    /**
     * Proveedor de RankedDao de la base de datos.
     */
    @Provides
    fun provideRankedDao(db: AppDatabase) = db.summonerRankedDao()

    /**
     * Proveedor de SummonerProfileDao de la base de datos.
     */
    @Provides
    fun provideProfileDao(db: AppDatabase) = db.summonerProfileDao()

    /**
     * Proveedor de RiotAccountDao de la base de datos.
     */
    @Provides
    fun provideAccountDao(db: AppDatabase) = db.riotAccountDao()

    /**
     * Proveedor de SummonerFullDao de la base de datos.
     */
    @Provides
    fun provideFullDao(db: AppDatabase) = db.summonerFullDao()

    /**
     * Proveedor de ChampionDao de la base de datos.
     */
    @Provides
    fun provideChampionDao(db: AppDatabase) = db.championDao()
}