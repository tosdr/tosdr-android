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
import androidx.activity.compose.BackHandler
import androidx.compose.ui.res.stringResource
import xyz.ptgms.tosdr.api.models.ServiceBasic
import xyz.ptgms.tosdr.components.lists.getAdaptiveRoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, viewModel: ToSDRViewModel) {
    val context = LocalContext.current
    val database = remember { ToSDRDatabase.getDatabase(context) }
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val preferServerSearch by viewModel.preferServerSearch.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    BackHandler(enabled = searchQuery.isNotEmpty()) {
        viewModel.setSearchQuery("")
        viewModel.clearSearchResults()
        keyboardController?.hide()
    }

    LaunchedEffect(Unit) {
        viewModel.loadPreferences(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Rounded.Settings, contentDescription = stringResource(R.string.topbar_settings))
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
            SearchContent(
                searchQuery = searchQuery,
                searchResults = searchResults,
                navController = navController
            )

            BottomSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { 
                    viewModel.setSearchQuery(it)
                    if (it.isEmpty()) {
                        viewModel.clearSearchResults()
                    } else if (!preferServerSearch && it.length >= 2) {
                        viewModel.searchServices(it, database, false)
                    }
                },
                onSearch = { query ->
                    keyboardController?.hide()
                    if (preferServerSearch && query.length >= 2) {
                        viewModel.searchServices(query, database, true)
                    }
                },
                onClearSearch = {
                    viewModel.setSearchQuery("")
                    viewModel.clearSearchResults()
                }
            )
        }
    }
}

@Composable
private fun SearchContent(
    searchQuery: String,
    searchResults: List<ServiceBasic>,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (searchQuery.isEmpty()) {
            QuickActions(navController)
        } else {
            SearchResults(searchResults = searchResults, navController = navController)
        }
    }
}

@Composable
private fun BoxScope.BottomSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .align(Alignment.BottomCenter)
            .imePadding()
    ) {
        ServiceSearchBar(
            searchQuery = searchQuery,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            onClearSearch = onClearSearch
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        modifier = modifier.fillMaxWidth(),
        expanded = false,
        onExpandedChange = {},
        inputField = { 
            SearchBarDefaults.InputField(
                query = searchQuery,
                onQueryChange = onQueryChange,
                onSearch = { onSearch(searchQuery) },
                expanded = false,
                onExpandedChange = {},
                placeholder = { Text(stringResource(R.string.home_search_placeholder)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = onClearSearch) {
                            Icon(
                                Icons.Rounded.Close,
                                contentDescription = stringResource(R.string.home_clear_search)
                            )
                        }
                    }
                }
            )
        },
        content = {}
    )
}

@Composable
fun QuickActions(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            stringResource(R.string.home_quick_actions),
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
                    Text(stringResource(R.string.home_about_tos_dr))
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
                    Text(stringResource(R.string.team_title))
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
                    Text(stringResource(R.string.donate_title))
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
}
@Composable
fun SearchResults(
    searchResults: List<ServiceBasic>,
    navController: NavController
) {
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
                shape = getAdaptiveRoundedCornerShape(
                    index = searchResults.indexOf(service),
                    lastIndex = searchResults.lastIndex
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
