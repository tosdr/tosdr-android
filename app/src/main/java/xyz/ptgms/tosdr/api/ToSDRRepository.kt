package xyz.ptgms.tosdr.api

import xyz.ptgms.tosdr.api.models.AppDbEntry
import xyz.ptgms.tosdr.api.models.ServiceBasic
import xyz.ptgms.tosdr.api.models.ServiceDetail

class ToSDRRepository {
    private val api = ApiClient.api
    
    suspend fun searchServices(query: String): Result<List<ServiceBasic>> = try {
        Result.success(api.searchServices(query).services)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getServiceDetails(id: Int): Result<ServiceDetail> = try {
        Result.success(api.getServiceDetails(id))
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getAppDb(apiKey: String): Result<List<AppDbEntry>> = try {
        Result.success(api.getAppDb(apiKey))
    } catch (e: Exception) {
        Result.failure(e)
    }
} 