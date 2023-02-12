package xyz.ptgms.tosdr.ui.views

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R

object ToSDRSettingsView {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRSettingsView(modifier: Modifier, scope: CoroutineScope, drawerState: DrawerState, navController: NavHostController) {
        val context = LocalContext.current
        val sharedPreference =  context.getSharedPreferences("settings", Context.MODE_PRIVATE)

        val hideNoGrade = remember { mutableStateOf(sharedPreference.getBoolean("hideNoGrade", false)) }
        val hideNotReviewed = remember { mutableStateOf(sharedPreference.getBoolean("hideNotReviewed", false)) }
        val localisePoints = remember { mutableStateOf(sharedPreference.getBoolean("localisePoints", false)) }
        val deepLSelected = remember { mutableStateOf(sharedPreference.getBoolean("deepLSelected", false)) }

        val packageNameShown = remember { mutableStateOf(sharedPreference.getBoolean("packageNameShown", false)) }
        //val showSeverity = remember { mutableStateOf(sharedPreference.getBoolean("showSeverity", false)) }

        val deepLkey = remember { mutableStateOf(sharedPreference.getString("deeplKey", "")) }

        // Experimental
        val allApps = remember { mutableStateOf(sharedPreference.getBoolean("allApps", false)) }

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.settings),
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
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                if (allApps.value) {
                    Text(
                        text = "Appearance Settings",
                        modifier = Modifier.padding(16.dp)
                    )
                    ElevatedCard(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(), onClick = {
                        packageNameShown.value = !packageNameShown.value
                        sharedPreference.edit().putBoolean("packageNameShown", packageNameShown.value).apply()
                    }) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Show package name in Installed Apps",
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(checked = packageNameShown.value, onCheckedChange = {
                                packageNameShown.value = it
                                sharedPreference.edit().putBoolean("packageNameShown", it).apply()
                            })
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.settings_search),
                    modifier = Modifier.padding(16.dp)
                )
                ElevatedCard(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(), onClick = {
                    hideNoGrade.value = !hideNoGrade.value
                    sharedPreference.edit().putBoolean("hideNoGrade", hideNoGrade.value).apply()
                }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_search_hidenograde),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = hideNoGrade.value, onCheckedChange = {
                            hideNoGrade.value = it
                            sharedPreference.edit().putBoolean("hideNoGrade", it).apply()
                        })
                    }
                }
                ElevatedCard(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(), onClick = {
                    hideNotReviewed.value = !hideNotReviewed.value
                    sharedPreference.edit().putBoolean("hideNotReviewed", hideNotReviewed.value)
                        .apply()
                }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_search_hidenoreview),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = hideNotReviewed.value, onCheckedChange = {
                            hideNotReviewed.value = it
                            sharedPreference.edit().putBoolean("hideNotReviewed", it).apply()
                        })
                    }
                }
                if (!Locale.current.language.startsWith("en")) {
                    Text(
                        text = stringResource(R.string.settings_localisation),
                        modifier = Modifier.padding(16.dp)
                    )
                    ElevatedCard(modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(), onClick = {
                        localisePoints.value = !localisePoints.value
                        sharedPreference.edit().putBoolean("localisePoints", localisePoints.value)
                            .apply()
                    }) {
                        Column {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = stringResource(R.string.settings_localise_points_in_services))
                                    if (deepLSelected.value) {
                                        Text(
                                            text = stringResource(R.string.settings_deepl_description),
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp
                                        )
                                    } else {
                                        Text(
                                            text = stringResource(R.string.settings_libretranslate_description),
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 12.sp
                                        )
                                    }

                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Switch(checked = localisePoints.value, onCheckedChange = {
                                    localisePoints.value = it
                                    sharedPreference.edit().putBoolean("localisePoints", it).apply()
                                })
                            }
                            if (localisePoints.value) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = deepLSelected.value,
                                            onClick = { deepLSelected.value = true },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (deepLSelected.value),
                                        onClick = null // null recommended for accessibility with screenreaders
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_deepl),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = !deepLSelected.value,
                                            onClick = { deepLSelected.value = false },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (!deepLSelected.value),
                                        onClick = null // null recommended for accessibility with screenreaders
                                    )
                                    Text(
                                        text = stringResource(R.string.settings_libretranslate),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                                if (deepLSelected.value) {
                                    TextField(
                                        value = deepLkey.value ?: "",
                                        onValueChange = {
                                            deepLkey.value = it
                                            sharedPreference.edit().putString("deepLkey", it).apply()
                                        },
                                        label = { Text(text = stringResource(R.string.settings_deepl_key)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
                ElevatedCard(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
                onClick = {
                    navController.navigate("licenses")
                }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.open_source_licenses),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Text(
                    text = stringResource(R.string.settings_experimental_features),
                    modifier = Modifier.padding(16.dp)
                )
                ElevatedCard(modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(), onClick = {
                    allApps.value = !allApps.value
                    sharedPreference.edit().putBoolean("allApps", allApps.value).apply()
                }) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.settings_allapps),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(checked = allApps.value, onCheckedChange = {
                            allApps.value = it
                            sharedPreference.edit().putBoolean("allApps", it).apply()
                            // Show a toast telling the user to restart the app
                            Toast.makeText(context, context.getString(R.string.restart_notice), Toast.LENGTH_LONG).show()
                        })
                    }
                }
            }
        }
    }
}