package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsRow
import xyz.ptgms.tosdr.data.room.ToSDRDatabase
import xyz.ptgms.tosdr.navigation.Screen
import xyz.ptgms.tosdr.ui.theme.ToSDRColorScheme
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: ToSDRViewModel) {
    val context = LocalContext.current
    val database = remember { ToSDRDatabase.getDatabase(context) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val preferServerSearch by viewModel.preferServerSearch.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(Unit) {
        viewModel.loadPreferences(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("ToS;DR")
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Rounded.Settings, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (searchQuery.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Quick Actions",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )

                        SettingsGroup {
                            SettingsRow(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navController.navigate(Screen.About.route) },
                                leading = {
                                    Icon(
                                        Icons.Rounded.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                title = {
                                    Text("About ToS;DR")
                                },
                                trailing = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            )
                            SettingsRow(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navController.navigate(Screen.Team.route) },
                                leading = {
                                    Icon(
                                        Icons.Rounded.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                title = {
                                    Text("Team")
                                },
                                trailing = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            )
                            SettingsRow(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { navController.navigate(Screen.Donate.route) },
                                leading = {
                                    Icon(
                                        Icons.Rounded.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                title = {
                                    Text("Donate")
                                },
                                trailing = {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                        contentDescription = null
                                    )
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(
                            items = searchResults,
                            key = { it.id }
                        ) { service ->
                            Surface(
                                onClick = { 
                                    navController.navigate(Screen.ServiceDetails.createRoute(service.id))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(
                                    topStart = if (searchResults.firstOrNull() == service) 24.dp else 8.dp,
                                    topEnd = if (searchResults.firstOrNull() == service) 24.dp else 8.dp,
                                    bottomStart = if (searchResults.lastOrNull() == service) 24.dp else 8.dp,
                                    bottomEnd = if (searchResults.lastOrNull() == service) 24.dp else 8.dp
                                ),
                                tonalElevation = 4.dp
                            ) {
                                ListItem(
                                    headlineContent = { 
                                        Text(
                                            service.name,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    },
                                    leadingContent = {
                                        AsyncImage(
                                            model = "https://s3.tosdr.org/logos/${service.id}.png",
                                            contentDescription = "${service.name} logo",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(25.dp)),
                                            error = painterResource(id = R.drawable.ic_service_placeholder),
                                            placeholder = painterResource(id = R.drawable.ic_service_placeholder)
                                        )
                                    },
                                    trailingContent = {
                                        AssistChip(
                                            onClick = { },
                                            label = {
                                                Text(
                                                    service.rating,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                )
                                            },
                                            border = null,
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = when(service.rating) {
                                                    "A" -> ToSDRColorScheme.gradeA
                                                    "B" -> ToSDRColorScheme.gradeB
                                                    "C" -> ToSDRColorScheme.gradeC
                                                    "D" -> ToSDRColorScheme.gradeD
                                                    "E" -> ToSDRColorScheme.gradeE
                                                    else -> ToSDRColorScheme.gradeNA
                                                },
                                                labelColor = Color.White,
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.BottomCenter)
                    .imePadding()
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = false,
                    onExpandedChange = {},
                    inputField = { 
                        SearchBarDefaults.InputField(
                            query = searchQuery,
                            onQueryChange = { 
                                searchQuery = it
                                if (it.isEmpty()) {
                                    viewModel.clearSearchResults()
                                } else if (!preferServerSearch && it.length >= 2) {
                                    viewModel.searchServices(it, database, false)
                                }
                            },
                            onSearch = { 
                                keyboardController?.hide()
                                if (preferServerSearch && it.length >= 2) {
                                    viewModel.searchServices(it, database, true)
                                }
                            },
                            expanded = false,
                            onExpandedChange = {},
                            placeholder = { Text("Search for services...") },
                            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        searchQuery = ""
                                        viewModel.clearSearchResults()
                                    }) {
                                        Icon(
                                            Icons.Rounded.Close,
                                            contentDescription = "Clear search"
                                        )
                                    }
                                }
                            }
                        )
                    },
                    content = {}
                )
            }
        }
    }
}
