package com.example.trackrscope.services.repository

import android.util.Log
import com.example.trackrscope.services.dao.ChampionDao
import com.example.trackrscope.services.entities.ChampionEntity
import com.example.trackrscope.services.models.game.toEntity
import com.example.trackrscope.services.service.DataDragonService
import com.example.trackrscope.services.utils.DataDragonRetrofit
import com.example.trackrscope.services.utils.LanguageProvider
import com.example.trackrscope.services.utils.VersionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio que gestiona la comunicación con la API de DataDragon, la cual obtiene los datos
 * de los campeones y los guarda en la base de datos para almacenarlos en caché.
 *
 * @param api Servicio que realiza las solicitudes a la API de DataDragon.
 * @param languageProvider Proporciona el idioma del dispositivo.
 * @param championDao Dao que gestiona la base de datos de los campeones.
 * @param versionManager Gestiona la versión de la API de DataDragon.
 */
@Singleton
class DataDragonRepository @Inject constructor(
    @DataDragonRetrofit private val api: DataDragonService,
    private val languageProvider: LanguageProvider,
    private val championDao: ChampionDao,
    private val versionManager: VersionManager
) {

    /**
     * Sincroniza los campeones con la última versión de la API de DataDragon.
     * Descarga los detalles de cada campeon si la versión es nueva o no hay campeones en la base de datos.
     */
    suspend fun syncChampions() = coroutineScope {
        try {
            // Obtengo los datos para sincronizar.
            val lastVersion = api.getVersions().first()
            val cachedVersion = versionManager.getVersion()
            val language = languageProvider.getSystemLanguage()

            val localChampions = championDao.getAllChampions()

            // Comprueba si es necesario actualizar los datos y la versión.
            if (lastVersion != cachedVersion || localChampions.isEmpty()) {
                val response = api.getChampions(version = lastVersion, language = language)
                val championKeys = response.data.keys

                val semaphore = Semaphore(15)

                val deferredChampions = championKeys.map { key ->
                    async {
                        semaphore.withPermit {
                            try {
                                val detailedResponse = api.getChampion(
                                    version = lastVersion,
                                    language = language,
                                    champion = key
                                )
                                val detailedChampion = detailedResponse.data[key]
                                detailedChampion?.toEntity()
                            } catch (e: Exception) {
                                Log.e(
                                    "DataDragonRepository",
                                    "Error fetching champion details: ${e.message}"
                                )
                                null
                            }
                        }
                    }
                }

                val detailedChampions = deferredChampions.awaitAll().filterNotNull()

                if (detailedChampions.isNotEmpty()) {
                    saveChampions(detailedChampions)
                    versionManager.setVersion(lastVersion)

                    Log.d("DataDragonRepository", "Version actualizada: $lastVersion")
                } else {
                    Log.d("DataDragonRepository", "No se encontraron champions")
                }
            } else {
                Log.d("DataDragonRepository", "No es necesario actualizar la version")
            }
        } catch (e: Exception) {
            Log.e("DataDragonRepository", "Error syncing champions: ${e.message}")
        }
    }

    /**
     * Guarda la lista de campeones en la base de datos.
     *
     * @param champions Lista de campeones a guardar.
     */
    suspend fun saveChampions(champions: List<ChampionEntity>) {
        try {
            championDao.insertAllChampions(champions)
        } catch (e: Exception) {
            Log.e("DataDragonRepository", "Error saving champions: ${e.message}")
        }
    }

    /**
     * Obtiene la lista de campeones desde la base de datos.
     *
     * @return Lista de [ChampionEntity]
     */
    suspend fun getAllChampions(): List<ChampionEntity> = withContext(Dispatchers.IO) {
        championDao.getAllChampions()
    }

    /**
     * Obtiene un campeon por su key.
     *
     * @param key Key del campeón.
     * @return Objeto [ChampionEntity]
     */
    suspend fun getChampionBykey(key: String): ChampionEntity? = withContext(Dispatchers.IO) {
        championDao.getChampionByKey(key)
    }
}