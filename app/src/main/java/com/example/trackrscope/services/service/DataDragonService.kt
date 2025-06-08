package com.example.trackrscope.services.service

import com.example.trackrscope.services.models.game.ChampionResponse
import com.example.trackrscope.services.models.game.ChampionsResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Peticiones a la api de DataDragon
 */
interface DataDragonService {

    // Obtengo las versiones de la api
    @GET("api/versions.json")
    suspend fun getVersions(): List<String>

    // Obtengo los campeones de una version
    @GET("cdn/{version}/data/{language}/champion.json")
    suspend fun getChampions(
        @Path("version") version: String,
        @Path("language") language: String
    ): ChampionsResponse

    // Obtengo un campeon de una version
    @GET("cdn/{version}/data/{language}/champion/{champion}.json")
    suspend fun getChampion(
        @Path("version") version: String,
        @Path("language") language: String,
        @Path("champion") champion: String
    ): ChampionResponse
}