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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.ui.theme.ToSDRColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesExplainedScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Grades") }
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
                SettingsTitle(text = "Grades")
                SettingsGroup {
                    GradeRow(
                        grade = "A",
                        title = "Excellent",
                        description = "Our best grade: This service respects your privacy.",
                        color = ToSDRColorScheme.gradeA
                    )
                    GradeRow(
                        grade = "B",
                        title = "Good",
                        description = "A pretty good grade: This service are fair for the user and could use minor adjustments.",
                        color = ToSDRColorScheme.gradeB
                    )
                    GradeRow(
                        grade = "C",
                        title = "Okay",
                        description = "This service is okay. The terms are okay, but some issues need your consideration.",
                        color = ToSDRColorScheme.gradeC
                    )
                    GradeRow(
                        grade = "D",
                        title = "Bad",
                        description = "This service's terms are uneven or there are some issues that need your attention.",
                        color = ToSDRColorScheme.gradeD
                    )
                    GradeRow(
                        grade = "E",
                        title = "Awful",
                        description = "Our worst grade: This service raises some serious concerns regarding privacy.",
                        color = ToSDRColorScheme.gradeE
                    )
                }
            }

            item {
                SettingsTitle(text = "Other")
                SettingsGroup {
                    GradeRow(
                        grade = "N/A",
                        title = "Not Available",
                        description = "This service has not received enough curated points to display an accurate grade. Feel free to contribute!",
                        color = ToSDRColorScheme.gradeNA
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
private fun GradeRow(
    grade: String,
    title: String,
    description: String,
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
            Text(
                text = grade,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(end = 16.dp)
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}
