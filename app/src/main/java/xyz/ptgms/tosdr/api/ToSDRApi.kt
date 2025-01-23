package xyz.ptgms.tosdr.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import xyz.ptgms.tosdr.api.models.AppDbEntry
import xyz.ptgms.tosdr.api.models.SearchResponse
import xyz.ptgms.tosdr.api.models.ServiceDetail

interface ToSDRApi {
    @GET("search/v5")
    suspend fun searchServices(@Query("query") query: String): SearchResponse
    
    @GET("service/v3")
    suspend fun getServiceDetails(@Query("id") id: Int): ServiceDetail
    
    @GET("appdb/version/v2")
    suspend fun getAppDb(@Header("apikey") apiKey: String): List<AppDbEntry>
} 