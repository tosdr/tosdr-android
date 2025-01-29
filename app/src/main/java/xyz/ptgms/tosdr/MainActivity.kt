package xyz.ptgms.tosdr

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
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
import xyz.ptgms.tosdr.screens.about.AboutScreen
import xyz.ptgms.tosdr.screens.about.GradesExplainedScreen
import xyz.ptgms.tosdr.screens.about.LibrariesScreen
import xyz.ptgms.tosdr.screens.about.PointsExplainedScreen
import xyz.ptgms.tosdr.screens.about.ServicesExplainedScreen
import xyz.ptgms.tosdr.ui.theme.ToSDRTheme
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween

class MainActivity : ComponentActivity() {
    private lateinit var database: ToSDRDatabase
    private val viewModel: ToSDRViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        database = ToSDRDatabase.getDatabase(this)
        
        lifecycleScope.launch {
            if (DatabaseUpdater.shouldUpdate(database)) {
                viewModel.refreshDatabase(database) {
                    if (it) Log.i("Updater", "Successfully auto-updated the DB")
                    else Log.i("Updater", "Could not auto-update the DB")
                }
            }
        }
        
        setContent {
            ToSDRTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ToSDRViewModel) {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Search.route,
            modifier = Modifier
                .padding(paddingValues)
                .navigationBarsPadding(),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable(Screen.Search.route) { SearchScreen(navController, viewModel) }
            composable(Screen.About.route) { AboutScreen(navController) }
            composable(Screen.Donate.route) { DonationScreen(navController) }
            composable(Screen.Team.route) { TeamScreen(navController) }
            composable(Screen.Settings.route) { SettingsScreen(navController, viewModel) }
            composable(Screen.ServiceDetails.route) { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId")?.toIntOrNull()
                if (serviceId != null) {
                    ServiceDetailsScreen(serviceId = serviceId, navController = navController, viewModel = viewModel)
                }
            }
            composable(Screen.PointView.route) { backStackEntry ->
                val pointId = backStackEntry.arguments?.getString("pointId")?.toIntOrNull()
                if (pointId != null) {
                    PointView(pointId = pointId, navController = navController, viewModel = viewModel)
                }
            }
            composable(Screen.GradesExplained.route) { GradesExplainedScreen(navController) }
            composable(Screen.PointsExplained.route) { PointsExplainedScreen(navController) }
            composable(Screen.ServicesExplained.route) { ServicesExplainedScreen(navController) }
            composable(Screen.Libraries.route) { LibrariesScreen(navController) }
        }
    }
}