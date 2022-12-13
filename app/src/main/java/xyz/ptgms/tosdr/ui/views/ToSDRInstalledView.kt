package xyz.ptgms.tosdr.ui.views

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.tools.data.App
import xyz.ptgms.tosdr.tools.data.api.API
import kotlin.concurrent.thread

object ToSDRInstalledView {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRInstalledView(modifier: Modifier, scope: CoroutineScope, drawerState: DrawerState, navController: NavHostController) {

        val sharedPreference =
            LocalContext.current.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val packageNameShown = sharedPreference.getBoolean("packageNameShown", false)
        val resolutionInfo = remember { mutableStateOf(sharedPreference.getBoolean("resolutionInfo", true)) }

        val topAppBarState = rememberTopAppBarState()
        val topAppBarBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)

        val context = LocalContext.current
        @Suppress("DEPRECATION") // The API is deprecated, but older versions of Android still need it
        val packageManager =  context.packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            .filter { context.packageManager.getLaunchIntentForPackage(it.packageName) != null }

        // Parse into list of App objects
        var apps: List<App> = mutableListOf()
        thread {
            apps = packageManager.map { packageInfo ->
                App(
                    name = packageInfo.applicationInfo.loadLabel(context.packageManager).toString(),
                    packageName = packageInfo.packageName,
                    icon = packageInfo.applicationInfo.loadIcon(context.packageManager)
                )
            }
        }


        Scaffold(topBar = {
            TopAppBar(
                scrollBehavior = topAppBarBehavior,
                title = {
                    Text(
                        text = "Installed Apps",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu)
                        )
                    }
                }
            )
        }) { padding ->
            LazyColumn(
                modifier = modifier
                    //.verticalScroll(rememberScrollState())
                    .padding(padding)
                    .fillMaxWidth()
                    .nestedScroll(topAppBarBehavior.nestedScrollConnection),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (apps.isNotEmpty()) {
                    if (resolutionInfo.value) item {
                        ElevatedCard(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 0.dp, 8.dp, 0.dp))
                        {
                            Text(modifier= Modifier.padding(16.dp), text =
                            "Some of these Apps will not have a ToS;DR page, the App will try to " +
                                    "resolve them and if it fails it will display an error.\n" +
                                    "The App may display the wrong page entirely, if this happens, " +
                                    "please open a GitHub issue and I will try to fix it.")
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                TextButton(onClick = {
                                    resolutionInfo.value = false
                                    sharedPreference.edit().putBoolean("resolutionInfo", false).apply()
                                }) {
                                    Text(text = "Don't show again")
                                }
                            }
                        }
                    }
                    items(apps) { app ->
                        ElevatedCard(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp, 0.dp, 8.dp, 0.dp),
                        onClick = {
                            // Search for the app using the package name
                            thread {
                                var searchResult = API.searchPage(
                                    app.packageName,
                                    sharedPreference.getBoolean("hideGrade", false),
                                    sharedPreference.getBoolean("hideNotReviewed", false)
                                )

                                if (searchResult.isEmpty()) { // Fallback if package name search fails
                                    Log.d("ToSDR", "Using fallback search")
                                    searchResult = API.searchPage(
                                        app.name,
                                        sharedPreference.getBoolean("hideGrade", false),
                                        sharedPreference.getBoolean("hideNotReviewed", false)
                                    )

                                    if (searchResult.isEmpty()) {
                                        // Make Alert that the app could not be found
                                        Toast.makeText(context, "Could not find ${app.name}", Toast.LENGTH_SHORT).show()
                                        return@thread
                                    }
                                }
                                Log.d("ToSDR", "Found ${searchResult.size} results for ${app.name}")

                                // Open the first result in UI thread
                                (context as Activity).runOnUiThread {
                                    if (searchResult[0].grade == "N/A") {
                                        navController.navigate("details/${searchResult[0].page}/None")
                                    } else {
                                        navController.navigate("details/${searchResult[0].page}/${searchResult[0].grade}")
                                    }
                                }
                            }
                        }) {
                            // Display app with icon and name and package name
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier= Modifier
                                        .width(48.dp)
                                        .height(48.dp),
                                    painter = rememberAsyncImagePainter(app.icon),
                                    contentDescription = app.name
                                )
                                Column {
                                    Text(
                                        text = app.name,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                    if (packageNameShown) Text(
                                        text = app.packageName,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // If no apps are found, display a message
                    item {
                        ElevatedCard(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)) {
                            Text(modifier = Modifier.padding(8.dp), text = "Loading apps...")
                        }
                    }
                }
            }
        }
    }
}