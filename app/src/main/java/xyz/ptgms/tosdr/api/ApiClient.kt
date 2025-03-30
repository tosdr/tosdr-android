import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import xyz.ptgms.tosdr.api.ToSDRApi
import android.content.Context

object ApiClient {
    var defaultUrl = "https://api.tosdr.org/"

    private var currentBaseUrl = defaultUrl
    private val listeners = mutableListOf<() -> Unit>()

    fun addBaseUrlChangeListener(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun removeBaseUrlChangeListener(listener: () -> Unit) {
        listeners.remove(listener)
    }

    fun initialize(context: Context) {
        val prefs = context.getSharedPreferences("tosdr_prefs", Context.MODE_PRIVATE)
        val savedUrl = prefs.getString("base_url", defaultUrl) ?: defaultUrl
        updateBaseUrl(savedUrl)
    }

    fun updateBaseUrl(newUrl: String) {
        currentBaseUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        retrofit = createRetrofit()
        api = retrofit.create(ToSDRApi::class.java)
        listeners.forEach { it() }
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