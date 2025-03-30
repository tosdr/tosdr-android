package xyz.ptgms.tosdr.api

import xyz.ptgms.tosdr.api.models.AppDbEntry
import xyz.ptgms.tosdr.api.models.ServiceBasic
import xyz.ptgms.tosdr.api.models.ServiceDetail

class ToSDRRepository {
    private var api = ApiClient.api
    
    init {
        ApiClient.addBaseUrlChangeListener {
            api = ApiClient.api
        }
    }
    
    suspend fun searchServices(query: String): Result<List<ServiceBasic>> = try {
        Result.success(api.searchServices(query).services)
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getServiceDetails(id: Int): Result<ServiceDetail> = try {
        var lang: String?
        val deviceLanguage = java.util.Locale.getDefault().language
        lang = when (deviceLanguage) {
            "de", "nl", "es", "fr" -> deviceLanguage
            else -> null
        }
        Result.success(api.getServiceDetails(id, lang))
    } catch (e: Exception) {
        Result.failure(e)
    }
    
    suspend fun getAppDb(apiKey: String): Result<List<AppDbEntry>> = try {
        Result.success(api.getAppDb(apiKey))
    } catch (e: Exception) {
        Result.failure(e)
    }
} 