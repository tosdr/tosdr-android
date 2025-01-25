package xyz.ptgms.tosdr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.lifecycleScope
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.data.DatabaseUpdater
import xyz.ptgms.tosdr.data.room.ToSDRDatabase
import xyz.ptgms.tosdr.navigation.Screen
import xyz.ptgms.tosdr.screens.*
import xyz.ptgms.tosdr.ui.theme.ToSDRTheme
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel

class MainActivity : ComponentActivity() {
    private lateinit var database: ToSDRDatabase
    private val viewModel: ToSDRViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        database = ToSDRDatabase.getDatabase(this)
        
        lifecycleScope.launch {
            if (DatabaseUpdater.shouldUpdate(database)) {
                DatabaseUpdater.updateDatabase(viewModel, database)
            }
        }
        
        setContent {
            ToSDRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Search.route) { SearchScreen(navController) }
            composable(Screen.About.route) { AboutScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.ServiceDetails.route) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId")?.toIntOrNull()
                if (serviceId != null) {
                    ServiceDetailsScreen(serviceId = serviceId, navController = navController)
                }
            }
        }
    }
}