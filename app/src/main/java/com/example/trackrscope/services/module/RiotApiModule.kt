package com.example.trackrscope.services.module

import com.example.trackrscope.BuildConfig
import com.example.trackrscope.services.module.RiotApiModule.BASE_URL_API_RIOT
import com.example.trackrscope.services.module.RiotApiModule.BASE_URL_API_RIOT_ACCOUNT
import com.example.trackrscope.services.service.RiotApiService
import com.example.trackrscope.services.utils.RiotHttpClient
import com.example.trackrscope.services.utils.RiotRetrofit
import com.example.trackrscope.services.utils.RiotRetrofitAccount
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Módulo de la app para proveer dependencias globales de la api de Riot.
 */
@Module
@InstallIn(SingletonComponent::class)
object RiotApiModule {

    private const val BASE_URL_API_RIOT = "https://euw1.api.riotgames.com/"
    private const val BASE_URL_API_RIOT_ACCOUNT = "https://europe.api.riotgames.com/"

    /**
     * Proveedor del cliente HTTP de Riot.
     */
    @Provides
    @Singleton
    @RiotHttpClient
    fun provideRiotHttpClient(): OkHttpClient {
        val apiKeyInterceptor = Interceptor { chain ->
            val request = chain.request()
            val newUrl =
                request.url.newBuilder().addQueryParameter("api_key", BuildConfig.RIOT_API_KEY)
                    .build()
            val newRequest = request.newBuilder().url(newUrl).build()
            chain.proceed(newRequest)
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder().addInterceptor(apiKeyInterceptor)
            .addInterceptor(loggingInterceptor).build()
    }

    /**
     * Proveedor del cliente Retrofit de Riot [BASE_URL_API_RIOT].
     */
    @Provides
    @Singleton
    @RiotRetrofit
    fun provideRiotRetrofit(@RiotHttpClient client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL_API_RIOT).client(client).addConverterFactory(
            GsonConverterFactory.create()
        ).build()
    }

    /**
     * Proveedor del cliente Retrofit de Riot [BASE_URL_API_RIOT_ACCOUNT].
     */
    @Provides
    @Singleton
    @RiotRetrofitAccount
    fun provideRetrofitAaccount(@RiotHttpClient client: OkHttpClient): Retrofit {
        return Retrofit.Builder().baseUrl(BASE_URL_API_RIOT_ACCOUNT).client(client)
            .addConverterFactory(
                GsonConverterFactory.create()
            ).build()
    }

    /**
     * Proveedor del servicio de Riot [BASE_URL_API_RIOT].
     */
    @Provides
    @Singleton
    @RiotRetrofit
    fun provideRiotApiService(@RiotRetrofit retrofit: Retrofit): RiotApiService {
        return retrofit.create(RiotApiService::class.java)
    }

    /**
     * Proveedor del servicio de Riot [BASE_URL_API_RIOT_ACCOUNT].
     */
    @Provides
    @Singleton
    @RiotRetrofitAccount
    fun provideRiotApiServiceAccount(@RiotRetrofitAccount retrofit: Retrofit): RiotApiService {
        return retrofit.create(RiotApiService::class.java)
    }
}