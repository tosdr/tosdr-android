package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.ptgms.tosdr.ui.theme.ToSDRColorScheme
import xyz.ptgms.tosdr.viewmodels.ToSDRViewModel
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(serviceId: Int, navController: NavController) {
    val viewModel: ToSDRViewModel = viewModel()
    val serviceDetails by viewModel.serviceDetails.collectAsState()

    LaunchedEffect(serviceId) {
        viewModel.getServiceDetails(serviceId)
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
                title = { Text(serviceDetails?.name ?: "Loading...") }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            serviceDetails?.let { service ->
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = service.name,
                                style = MaterialTheme.typography.headlineLarge,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Rating: ${service.rating}",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = when(service.rating) {
                                            "A" -> ToSDRColorScheme.gradeA
                                            "B" -> ToSDRColorScheme.gradeB
                                            "C" -> ToSDRColorScheme.gradeC
                                            "D" -> ToSDRColorScheme.gradeD
                                            "E" -> ToSDRColorScheme.gradeE
                                            else -> ToSDRColorScheme.gradeNA
                                        }
                                    )
                                    Text(
                                        text = if (service.is_comprehensively_reviewed) 
                                            "Comprehensively Reviewed" 
                                        else 
                                            "Review Pending",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    items(service.points) { point ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = point.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = point.analysis,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                AssistChip(
                                    onClick = { },
                                    label = { Text(point.case.classification) }
                                )
                            }
                        }
                    }
                }
            } ?: run {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
