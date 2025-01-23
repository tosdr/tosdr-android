import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.ptgms.tosdr.api.ToSDRApi

object ApiClient {
    private const val BASE_URL = "https://api.tosdr.org/"
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()
        
    val api: ToSDRApi = retrofit.create(ToSDRApi::class.java)
} 