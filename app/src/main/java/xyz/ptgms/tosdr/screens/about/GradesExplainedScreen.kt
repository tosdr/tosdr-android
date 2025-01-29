package xyz.ptgms.tosdr.screens.about

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.R
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
                title = { Text(stringResource(R.string.grades_title)) }
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
                SettingsTitle(text = stringResource(R.string.grades_title))
                SettingsGroup {
                    GradeRow(
                        grade = "A",
                        title = stringResource(R.string.grade_a_title),
                        description = stringResource(R.string.grade_a_desc),
                        color = ToSDRColorScheme.gradeA
                    )
                    GradeRow(
                        grade = "B",
                        title = stringResource(R.string.grade_b_title),
                        description = stringResource(R.string.grade_b_desc),
                        color = ToSDRColorScheme.gradeB
                    )
                    GradeRow(
                        grade = "C",
                        title = stringResource(R.string.grade_c_title),
                        description = stringResource(R.string.grade_c_desc),
                        color = ToSDRColorScheme.gradeC
                    )
                    GradeRow(
                        grade = "D",
                        title = stringResource(R.string.grade_d_title),
                        description = stringResource(R.string.grade_d_desc),
                        color = ToSDRColorScheme.gradeD
                    )
                    GradeRow(
                        grade = "E",
                        title = stringResource(R.string.grade_e_title),
                        description = stringResource(R.string.grade_e_desc),
                        color = ToSDRColorScheme.gradeE
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.grades_other))
                SettingsGroup {
                    GradeRow(
                        grade = "N/A",
                        title = stringResource(R.string.grade_na_title),
                        description = stringResource(R.string.grade_na_desc),
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
