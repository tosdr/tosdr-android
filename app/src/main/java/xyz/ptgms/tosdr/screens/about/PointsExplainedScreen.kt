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
import androidx.compose.ui.res.stringResource
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
                title = { Text(stringResource(R.string.points_title)) }
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
                SettingsTitle(text = stringResource(R.string.points_classifications))
                SettingsGroup {
                    ClassificationRow(
                        title = stringResource(R.string.points_blocker_title),
                        description = stringResource(R.string.points_blocker_desc),
                        icon = painterResource(R.drawable.ic_rounded_block_24),
                        color = BadgeColors.red
                    )
                    ClassificationRow(
                        title = stringResource(R.string.points_bad_title),
                        description = stringResource(R.string.points_bad_desc),
                        icon = painterResource(R.drawable.ic_rounded_thumb_down_24),
                        color = BadgeColors.orange
                    )
                    ClassificationRow(
                        title = stringResource(R.string.points_good_title),
                        description = stringResource(R.string.points_good_desc),
                        icon = painterResource(R.drawable.ic_rounded_thumb_up_24),
                        color = BadgeColors.green
                    )
                    ClassificationRow(
                        title = stringResource(R.string.points_neutral_title),
                        description = stringResource(R.string.points_neutral_desc),
                        icon = painterResource(R.drawable.ic_rounded_info_24),
                        color = BadgeColors.gray
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.points_grade_calculation))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        stringResource(R.string.points_grade_intro),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.points_grade_a),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.points_grade_b),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.points_grade_c),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.points_grade_d),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        stringResource(R.string.points_grade_e),
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