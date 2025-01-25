package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsRow
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.data.room.ToSDRDatabase
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ToSDRViewModel = viewModel()
    val database = remember { ToSDRDatabase.getDatabase(context) }
    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    
    val dbStats by viewModel.dbStats.collectAsState()
    val preferServerSearch by viewModel.preferServerSearch.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadDbStats(database)
        viewModel.loadPreferences(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() }
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            SettingsTitle(text = "Search")
            SettingsGroup(
                modifier = Modifier.fillMaxWidth()
            ) {
                SettingsRow(
                    leading = { Icon(Icons.Default.Search, contentDescription = null) },
                    title = {
                        Column {
                            Text("Prefer Server Search")
                            Text(
                                "Always use online search instead of local database",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    },
                    trailing = {
                        Switch(
                            checked = preferServerSearch,
                            onCheckedChange = {
                                viewModel.setPreferServerSearch(context, it)
                            }
                        )
                    }
                )
            }

            SettingsTitle(text = "Database")

            SettingsGroup(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (dbStats.entryCount == 0) {
                    SettingsRow(
                        leading = { Icon(Icons.Default.Close, contentDescription = null) },
                        title = {
                            Column {
                                Text("Database not downloaded")
                                Text(
                                    "Download the database to enable offline functionality",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    )
                } else {
                    SettingsRow(
                        leading = { Icon(Icons.Default.DateRange, contentDescription = null) },
                        title = {
                            Column {
                                Text("Last Update")
                                Text(
                                    "When the database was last refreshed",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        trailing = {
                            Text(
                                SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                                    .format(Date(dbStats.lastUpdate))
                            )
                        }
                    )

                    SettingsRow(
                        leading = { Icon(Icons.Default.Build, contentDescription = null) },
                        title = {
                            Column {
                                Text("Services")
                                Text(
                                    "Number of services in the database",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        },
                        trailing = { Text(dbStats.entryCount.toString()) }
                    )
                }

                SettingsRow(
                    title = {
                        Button(
                            onClick = {
                                viewModel.refreshDatabase(database) { success ->
                                    isLoading = false
                                    if (!success) showErrorDialog = true
                                }
                                isLoading = true
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isLoading) "Refreshing..." else "Refresh Database")
                                if (isLoading) {
                                    Spacer(modifier = Modifier.width(4.dp))

                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    },
                    trailing = {
                        IconButton(
                            onClick = { viewModel.deleteDatabase(database) },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Database")
                        }
                    }
                )
            }
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text("Failed to update the database. Please try again.") },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
