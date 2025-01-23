import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import xyz.ptgms.tosdr.api.ToSDRRepository
import xyz.ptgms.tosdr.api.models.AppDbEntry
import xyz.ptgms.tosdr.api.models.ServiceBasic
import xyz.ptgms.tosdr.api.models.ServiceDetail
import kotlinx.coroutines.Dispatchers
import xyz.ptgms.tosdr.data.DatabaseUpdater
import xyz.ptgms.tosdr.data.room.ToSDRDatabase

class ToSDRViewModel : ViewModel() {
    private val repository = ToSDRRepository()
    
    private val _searchResults = MutableStateFlow<List<ServiceBasic>>(emptyList())
    val searchResults: StateFlow<List<ServiceBasic>> = _searchResults
    
    private val _serviceDetails = MutableStateFlow<ServiceDetail?>(null)
    val serviceDetails: StateFlow<ServiceDetail?> = _serviceDetails
    
    private val _appDb = MutableStateFlow<List<AppDbEntry>>(emptyList())
    val appDb: StateFlow<List<AppDbEntry>> = _appDb
    
    data class DbStats(
        val lastUpdate: Long = 0L,
        val entryCount: Int = 0
    )

    private val _dbStats = MutableStateFlow(DbStats())
    val dbStats: StateFlow<DbStats> = _dbStats
    
    private val _preferServerSearch = MutableStateFlow(false)
    val preferServerSearch: StateFlow<Boolean> = _preferServerSearch
    
    fun searchServices(query: String, database: ToSDRDatabase, preferServerSearch: Boolean = false) {
        viewModelScope.launch {
            if (preferServerSearch) {
                repository.searchServices(query).onSuccess {
                    _searchResults.value = it
                }
            } else {
                database.serviceDao().searchServices("%$query%")
                    .collect { services ->
                        val mappedResults = services.map { service ->
                            ServiceBasic(
                                id = service.id,
                                name = service.name,
                                urls = listOf(service.url),
                                rating = service.rating,
                                is_comprehensively_reviewed = true,
                                updated_at = "",
                                created_at = "",
                                slug = ""
                            )
                        }
                        _searchResults.value = mappedResults
                    }
            }
        }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
    
    fun getServiceDetails(id: Int) {
        viewModelScope.launch {
            repository.getServiceDetails(id).onSuccess {
                _serviceDetails.value = it
            }
        }
    }
    
    fun getAppDb(apiKey: String) = flow {
        emit(repository.getAppDb(apiKey))
    }.flowOn(Dispatchers.IO)

    fun loadDbStats(database: ToSDRDatabase) {
        viewModelScope.launch {
            val stats = DbStats(
                lastUpdate = database.serviceDao().getLastUpdateTime() ?: 0L,
                entryCount = database.serviceDao().getCount()
            )
            _dbStats.value = stats
        }
    }

    fun refreshDatabase(appDatabase: ToSDRDatabase, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            DatabaseUpdater.updateDatabase(this@ToSDRViewModel, appDatabase).collect { result ->
                result.onSuccess {
                    loadDbStats(appDatabase)
                    onComplete(true)
                }.onFailure {
                    onComplete(false)
                }
            }
        }
    }

    fun deleteDatabase(database: ToSDRDatabase) {
        viewModelScope.launch {
            database.serviceDao().clearAll()
            loadDbStats(database)
        }
    }

    fun loadPreferences(context: Context) {
        val prefs = context.getSharedPreferences("tosdr_preferences", Context.MODE_PRIVATE)
        _preferServerSearch.value = prefs.getBoolean("prefer_server_search", false)
    }

    fun setPreferServerSearch(context: Context, value: Boolean) {
        context.getSharedPreferences("tosdr_preferences", Context.MODE_PRIVATE)
            .edit()
            .putBoolean("prefer_server_search", value)
            .apply()
        _preferServerSearch.value = value
    }
} 