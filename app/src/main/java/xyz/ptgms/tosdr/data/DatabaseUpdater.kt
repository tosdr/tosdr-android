package xyz.ptgms.tosdr.data

import androidx.annotation.Keep
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import xyz.ptgms.tosdr.data.room.ServiceEntity
import xyz.ptgms.tosdr.data.room.ToSDRDatabase
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel

object DatabaseUpdater {
    private const val SEVEN_DAYS_IN_MILLIS = 7 * 24 * 60 * 60 * 1000L
    private const val API_KEY = "congrats on getting the key :P"

    suspend fun shouldUpdate(database: ToSDRDatabase): Boolean {
        val lastUpdate = database.serviceDao().getLastUpdateTime() ?: 0L
        val currentTime = System.currentTimeMillis()
        return lastUpdate == 0L || currentTime - lastUpdate >= SEVEN_DAYS_IN_MILLIS
    }

    @Keep
    fun updateDatabase(
        viewModel: ToSDRViewModel,
        database: ToSDRDatabase
    ): Flow<Result<List<ServiceEntity>>> = flow {
        viewModel.getAppDb(API_KEY).collect { result ->
            result.onSuccess { entries ->
                val serviceEntities = entries.map { entry ->
                    ServiceEntity(
                        id = entry.id,
                        name = entry.name,
                        url = entry.url,
                        rating = entry.rating
                    )
                }
                database.serviceDao().insertAll(serviceEntities)
                emit(Result.success(serviceEntities))
            }.onFailure {
                emit(Result.failure(it))
            }
        }
    }
} 