import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.ptgms.tosdr.api.ToSDRApi

object ApiClient {
    var defaultUrl = "https://api.tosdr.org/"

    private var currentBaseUrl = defaultUrl

    fun updateBaseUrl(newUrl: String) {
        currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        retrofit = createRetrofit()
        api = retrofit.create(ToSDRApi::class.java)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    private var retrofit = createRetrofit()
    var api: ToSDRApi = retrofit.create(ToSDRApi::class.java)
}