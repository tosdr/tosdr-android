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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailsScreen(serviceId: Int) {
    val viewModel: ToSDRViewModel = viewModel()
    val serviceDetails by viewModel.serviceDetails.collectAsState()

    LaunchedEffect(serviceId) {
        viewModel.getServiceDetails(serviceId)
    }

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
                                    "A" -> MaterialTheme.colorScheme.primary
                                    "B" -> MaterialTheme.colorScheme.secondary
                                    "C" -> MaterialTheme.colorScheme.tertiary
                                    "D" -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.error
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
//                        AssignmentChip(
//                            selected = true,
//                            onClick = { },
//                            label = { Text(point.case.classification) }
//                        )
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
