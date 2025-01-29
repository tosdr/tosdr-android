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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController, viewModel: ToSDRViewModel) {
    val context = LocalContext.current
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
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.nav_back)
                        )
                    }
                },
                title = { Text(stringResource(R.string.settings)) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            SettingsTitle(text = stringResource(R.string.settings_header_search))
            SettingsGroup(
                modifier = Modifier.fillMaxWidth()
            ) {
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
                            onCheckedChange = {
                                viewModel.setPreferServerSearch(context, it)
                            }
                        )
                    }
                )
            }

            SettingsTitle(text = stringResource(R.string.settings_header_database))

            SettingsGroup(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (dbStats.entryCount == 0) {
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
                } else {
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
                                )
                                    .format(Date(dbStats.lastUpdate))
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
                                Text(
                                    if (isLoading) stringResource(R.string.settings_database_refreshing) else stringResource(
                                        R.string.settings_database_refresh
                                    )
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
                            onClick = { viewModel.deleteDatabase(database) },
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
        }
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(stringResource(R.string.dialog_error)) },
            text = { Text(stringResource(R.string.dialog_update_db_error_desc)) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text(stringResource(R.string.dialog_ok))
                }
            }
        )
    }
}
