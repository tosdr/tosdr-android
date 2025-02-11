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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsRow
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.data.room.ToSDRDatabase
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.navigation.NavController
import xyz.ptgms.tosdr.R
import android.content.Context
import androidx.compose.ui.res.painterResource
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel.DbStats
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import xyz.ptgms.tosdr.BuildConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: ToSDRViewModel) {
    val context = LocalContext.current
    val database = remember { ToSDRDatabase.getDatabase(context) }
    var isLoading by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var showCustomUrlDialog by remember { mutableStateOf(false) }

    val dbStats by viewModel.dbStats.collectAsState()
    val preferServerSearch by viewModel.preferServerSearch.collectAsState()
    val currentBaseUrl by viewModel.baseUrl.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDbStats(database)
        viewModel.loadPreferences(context)
    }

    Scaffold(
        topBar = {
            SettingsTopBar(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            SearchSettings(
                preferServerSearch = preferServerSearch,
                onPreferServerSearchChange = { viewModel.setPreferServerSearch(context, it) }
            )

            ApiSettings(
                currentBaseUrl = currentBaseUrl,
                onBaseUrlChange = { viewModel.setBaseUrl(context, it) },
                onShowCustomUrlDialog = { showCustomUrlDialog = true }
            )

            DatabaseSettings(
                dbStats = dbStats,
                isLoading = isLoading,
                context = context,
                onRefreshDatabase = {
                    viewModel.refreshDatabase(database) { success ->
                        isLoading = false
                        if (!success) showErrorDialog = true
                    }
                    isLoading = true
                },
                onDeleteDatabase = { viewModel.deleteDatabase(database) }
            )

            AboutAppSettings()
        }
    }

    if (showErrorDialog) {
        DatabaseErrorDialog(onDismiss = { showErrorDialog = false })
    }

    if (showCustomUrlDialog) {
        CustomUrlDialog(
            currentUrl = currentBaseUrl,
            onUrlChange = { viewModel.setBaseUrl(context, it) },
            onDismiss = { showCustomUrlDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(navController: NavController) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.nav_back)
                )
            }
        },
        title = { Text(stringResource(R.string.settings)) }
    )
}

@Composable
private fun SearchSettings(
    preferServerSearch: Boolean,
    onPreferServerSearchChange: (Boolean) -> Unit
) {
    SettingsTitle(text = stringResource(R.string.settings_header_search))
    SettingsGroup(modifier = Modifier.fillMaxWidth()) {
        SettingsRow(
            leading = { Icon(Icons.Default.Search, contentDescription = null) },
            title = {
                Column {
                    Text(stringResource(R.string.settings_server_search))
                    Text(
                        stringResource(R.string.settings_server_search_desc),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            trailing = {
                Switch(
                    checked = preferServerSearch,
                    onCheckedChange = onPreferServerSearchChange
                )
            }
        )
    }
}

@Composable
private fun ApiSettings(
    currentBaseUrl: String,
    onBaseUrlChange: (String) -> Unit,
    onShowCustomUrlDialog: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val urls = listOf(
        "https://api.tosdr.org/",
        "https://api.staging.tosdr.org/"
    )

    SettingsTitle(text = "API Settings")
    SettingsGroup(modifier = Modifier.fillMaxWidth()) {
        SettingsRow(
            leading = { Icon(painterResource(R.drawable.ic_rounded_api_24), contentDescription = null) },
            title = {
                Column {
                    Text("API Endpoint")
                    Text(
                        currentBaseUrl,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            trailing = {
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select API endpoint")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        urls.forEach { url ->
                            DropdownMenuItem(
                                text = { Text(url) },
                                onClick = {
                                    onBaseUrlChange(url)
                                    expanded = false
                                }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Custom URL...") },
                            onClick = {
                                expanded = false
                                onShowCustomUrlDialog()
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun DatabaseSettings(
    dbStats: DbStats,
    isLoading: Boolean,
    context: Context,
    onRefreshDatabase: () -> Unit,
    onDeleteDatabase: () -> Unit
) {
    SettingsTitle(text = stringResource(R.string.settings_header_database))
    SettingsGroup(modifier = Modifier.fillMaxWidth()) {
        if (dbStats.entryCount == 0) {
            EmptyDatabaseInfo()
        } else {
            DatabaseStats(dbStats, context)
        }
        DatabaseActions(
            isLoading = isLoading,
            onRefreshDatabase = onRefreshDatabase,
            onDeleteDatabase = onDeleteDatabase
        )
    }
}

@Composable
private fun EmptyDatabaseInfo() {
    SettingsRow(
        leading = { Icon(Icons.Default.Close, contentDescription = null) },
        title = {
            Column {
                Text(stringResource(R.string.settings_database_none))
                Text(
                    stringResource(R.string.settings_database_none_desc),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    )
}

@Composable
private fun DatabaseStats(dbStats: DbStats, context: Context) {
    SettingsRow(
        leading = { Icon(Icons.Default.DateRange, contentDescription = null) },
        title = {
            Column {
                Text(stringResource(R.string.settings_database_lastupdate))
                Text(
                    stringResource(R.string.settings_database_lastupdate_desc),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        trailing = {
            Text(
                SimpleDateFormat(
                    context.getString(R.string.date_format),
                    Locale.getDefault()
                ).format(Date(dbStats.lastUpdate))
            )
        }
    )

    SettingsRow(
        leading = { Icon(Icons.Default.Build, contentDescription = null) },
        title = {
            Column {
                Text(stringResource(R.string.settings_database_services))
                Text(
                    stringResource(R.string.settings_database_services_desc),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        trailing = { Text(dbStats.entryCount.toString()) }
    )
}

@Composable
private fun DatabaseActions(
    isLoading: Boolean,
    onRefreshDatabase: () -> Unit,
    onDeleteDatabase: () -> Unit
) {
    SettingsRow(
        title = {
            Button(
                onClick = onRefreshDatabase,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (isLoading) stringResource(R.string.settings_database_refreshing)
                        else stringResource(R.string.settings_database_refresh)
                    )
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
                onClick = onDeleteDatabase,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.settings_database_delete)
                )
            }
        }
    )
}


@Composable
private fun AboutAppSettings() {
    SettingsTitle(text = stringResource(R.string.settings_about))
    SettingsGroup(modifier = Modifier.fillMaxWidth()) {
        SettingsRow(
            leading = { Icon(Icons.Default.Info, contentDescription = null) },
            title = {
                Column {
                    Text("ToS;DR; Version ${BuildConfig.VERSION_NAME}")
                    Text(
                        "You are running the variant '${BuildConfig.FLAVOR}'.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        )
    }
}

@Composable
private fun CustomUrlDialog(
    currentUrl: String,
    onUrlChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf(currentUrl) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Custom API URL") },
        text = {
            Column {
                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        error = null
                    },
                    label = { Text("API URL") },
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (!url.startsWith("http://") && !url.startsWith("https://")) {
                        error = "URL must start with http:// or https://"
                        return@TextButton
                    }
                    onUrlChange(url)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DatabaseErrorDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_error)) },
        text = { Text(stringResource(R.string.dialog_update_db_error_desc)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_ok))
            }
        }
    )
}
