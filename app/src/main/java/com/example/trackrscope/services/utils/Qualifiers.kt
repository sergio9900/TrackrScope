package com.example.trackrscope.services.utils

import javax.inject.Qualifier

/**
 * Anotación para identificar el cliente HTTP de DataDragonRetrofit.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataDragonRetrofit

/**
 * Anotación para identificar el cliente HTTP de RiotRetrofit.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RiotRetrofit

/**
 * Anotación para identificar el cliente HTTP de DataDragonOkHttp.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DataDragonHttpClient

/**
 * Anotación para identificar el cliente HTTP de RiotOkHttp.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RiotHttpClient

/**
 * Anotación para identificar el cliente HTTP de RiotOkHttp.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RiotRetrofitAccount