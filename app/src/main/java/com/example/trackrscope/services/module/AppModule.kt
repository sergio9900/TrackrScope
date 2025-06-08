package com.example.trackrscope.services.module

import android.content.Context
import com.example.trackrscope.services.utils.LanguageProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo de la app para proveer dependencias globales.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Proveedor de idioma.
     */
    @Provides
    @Singleton
    fun provideLanguageProvider(@ApplicationContext context: Context): LanguageProvider {
        return LanguageProvider(context)
    }
}