package xyz.ptgms.tosdr.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.ui.theme.BadgeColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsExplainedScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Points") }
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
                SettingsTitle(text = "Classifications")
                SettingsGroup {
                    ClassificationRow(
                        title = "Blocker",
                        description = "This point has severe effects on your user rights and/or privacy. This point immediately classifies a service as having the worst grade.",
                        icon = painterResource(R.drawable.ic_rounded_block_24),
                        color = BadgeColors.red
                    )
                    ClassificationRow(
                        title = "Bad",
                        description = "This point negatively impacts your user rights and/or privacy. Be advised.",
                        icon = painterResource(R.drawable.ic_rounded_thumb_down_24),
                        color = BadgeColors.orange
                    )
                    ClassificationRow(
                        title = "Good",
                        description = "This point stands out as being valuable and good for your user rights and/or privacy!",
                        icon = painterResource(R.drawable.ic_rounded_thumb_up_24),
                        color = BadgeColors.green
                    )
                    ClassificationRow(
                        title = "Neutral",
                        description = "This point is neither good nor bad for your user rights and/or privacy.",
                        icon = painterResource(R.drawable.ic_rounded_info_24),
                        color = BadgeColors.gray
                    )
                }
            }

            item {
                SettingsTitle(text = "Grade Calculation")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "The grade for an Service is calculated based on how many points it has and what type they are. These calculations change frequently, however, the general idea is as follows:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Services with many great points for the user should be classified as an A",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Services with many great points and some negative ones should be classified as an B",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Services with some great points and some negative ones should be classified as an C",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Services with few great points and many negative ones should be classified as an D",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Services with a blocker should be classified as an E",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ClassificationRow(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.painter.Painter,
    color: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color,
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
                        painter = icon,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}