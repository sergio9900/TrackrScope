package com.example.trackrscope.services.module

import com.example.trackrscope.services.service.DataDragonService
import com.example.trackrscope.services.utils.DataDragonHttpClient
import com.example.trackrscope.services.utils.DataDragonRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo de la app para proveer dependencias globales de la api de DataDragon.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataDragonModule {

    private const val BASE_URL_DATADRAGON = "https://ddragon.leagueoflegends.com/"

    /**
     * Proveedor del cliente HTTP de DataDragon.
     */
    @Provides
    @Singleton
    @DataDragonHttpClient
    fun provideDataDragonHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    }

    /**
     * Proveedor del cliente Retrofit de DataDragon.
     */
    @Provides
    @Singleton
    @DataDragonRetrofit
    fun provideDataDragonRetrofit(@DataDragonHttpClient client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL_DATADRAGON).client(client).addConverterFactory(
            GsonConverterFactory.create()
        ).build()
    }

    /**
     * Proveedor del servicio de DataDragon.
     */
    @Provides
    @Singleton
    @DataDragonRetrofit
    fun provideDataDragonService(@DataDragonRetrofit retrofit: Retrofit): DataDragonService {
        return retrofit.create(DataDragonService::class.java)
    }
}