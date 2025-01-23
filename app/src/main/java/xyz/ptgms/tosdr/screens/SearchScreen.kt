package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.navigation.Screen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import xyz.ptgms.tosdr.data.room.ToSDRDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: ToSDRViewModel = viewModel()
    val database = remember { ToSDRDatabase.getDatabase(context) }
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val preferServerSearch by viewModel.preferServerSearch.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    
    LaunchedEffect(Unit) {
        viewModel.loadPreferences(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                            viewModel.searchServices(it, database, preferServerSearch)
                        }
                    },
                    onSearch = { 
                        keyboardController?.hide()
                        if (preferServerSearch && it.length >= 2) {
                            viewModel.searchServices(it, database, preferServerSearch)
                        }
                    },
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search for services...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            },
            content = {}
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = searchResults) { service ->
                ElevatedCard(
                    onClick = { 
                        navController.navigate(Screen.ServiceDetails.createRoute(service.id))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        headlineContent = { Text(service.name) },
                        supportingContent = { Text("Rating: ${service.rating}") },
                        leadingContent = {
                            AsyncImage(
                                model = "https://s3.tosdr.org/logos/${service.id}.png",
                                contentDescription = "${service.name} logo",
                                modifier = Modifier.size(40.dp),
                                error = painterResource(id = R.drawable.ic_service_placeholder),
                                placeholder = painterResource(id = R.drawable.ic_service_placeholder)
                            )
                        },
                        trailingContent = {
                            Text(
                                if (service.is_comprehensively_reviewed) "Reviewed" else "Pending",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }
        }
    }
}
