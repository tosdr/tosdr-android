package xyz.ptgms.tosdr.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsRow
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text(stringResource(R.string.about_title)) }
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
                SettingsTitle(text = stringResource(R.string.about_welcome))
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_celebration_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text(stringResource(R.string.about_welcome_title)) }
                    )
                    
                    SettingsRow(
                        title = {
                            Text(
                                stringResource(R.string.about_welcome_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.about_organization))
                SettingsGroup {
                    SettingsRow(
                        title = {
                            Text(
                                stringResource(R.string.about_organization_desc1),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                    
                    SettingsRow(
                        title = {
                            Text(
                                stringResource(R.string.about_organization_desc2),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.about_terminology))
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_school_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text(stringResource(R.string.about_grades)) },
                        trailing = {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { navController.navigate(Screen.GradesExplained.route) }
                    )

                    SettingsRow(
                        leading = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_format_quote_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text(stringResource(R.string.about_points)) },
                        trailing = {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { navController.navigate(Screen.PointsExplained.route) }
                    )

                    SettingsRow(
                        leading = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_home_storage_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text(stringResource(R.string.about_services)) },
                        trailing = {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { navController.navigate(Screen.ServicesExplained.route) }
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.about_contribute))
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text(stringResource(R.string.about_curate)) },
                        trailing = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_open_in_browser_24),
                                contentDescription = null
                            )
                        },
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://edit.tosdr.org"))
                            context.startActivity(intent)
                        }
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.about_this_app))
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                Icons.Rounded.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text(stringResource(R.string.about_libraries)) },
                        trailing = {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { navController.navigate(Screen.Libraries.route) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
