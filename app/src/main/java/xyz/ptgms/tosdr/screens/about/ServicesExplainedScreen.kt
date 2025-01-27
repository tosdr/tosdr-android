package xyz.ptgms.tosdr.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.ui.theme.BadgeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesExplainedScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Services") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                SettingsTitle(text = "Service Badges")
                SettingsGroup {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = BadgeColors.green,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                    Text(
                                        text = "Review Status",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = Color.White
                                    )
                                }
                                Text(
                                    text = "In ToS;DR classically referred to as 'Comprehensively Reviewed', meaning this service has enough curated points to be deemed accurate enough for an everyday rating.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            item {
                SettingsTitle(text = "Service Contents")
                SettingsGroup {
                    Text(
                        text = "Each Service includes Points that determine a final Grade, Links to all policies that are relevant to ToS;DR and other useful information.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}