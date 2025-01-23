package xyz.ptgms.tosdr.data.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services WHERE name LIKE :query OR url LIKE :query")
    fun searchServices(query: String): Flow<List<ServiceEntity>>
    
    @Query("SELECT MAX(lastUpdate) FROM services")
    suspend fun getLastUpdateTime(): Long?
    
    @Query("SELECT COUNT(*) FROM services")
    suspend fun getCount(): Int
    
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)
    
    @Query("DELETE FROM services")
    suspend fun clearAll()
} 