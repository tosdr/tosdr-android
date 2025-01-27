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
                title = { Text("About") }
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
                SettingsTitle(text = "Welcome!")
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_celebration_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text("Welcome to ToS;DR!") }
                    )
                    
                    SettingsRow(
                        title = {
                            Text(
                                "This will guide you through everything there is to know about ToS;DR! Feel free to click anything below to learn more!",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            item {
                SettingsTitle(text = "Organization")
                SettingsGroup {
                    SettingsRow(
                        title = {
                            Text(
                                "Terms of Service; Didn't Read\" (short: ToS;DR) is a young project started in June 2012 to help fix the \"biggest lie on the web\": almost no one really reads the terms of service we agree to all the time. We aim at rating popular web services Terms of Service and Privacy Policies by summarizing them in \"convenient\" grades from A to E with so called \"Points\".",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                    
                    SettingsRow(
                        title = {
                            Text(
                                "ToS;DR is a non-profit organization, and all of our team members and contributors do their work as volunteers, with payment being rare. We rely on donations to keep our infrastructure and operations up, and our finances are laid out through our website and collective websites.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            item {
                SettingsTitle(text = "Terminology")
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                painterResource(R.drawable.ic_rounded_school_24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text("Grades") },
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
                        title = { Text("Points") },
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
                        title = { Text("Services") },
                        trailing = {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                        },
                        onClick = { navController.navigate(Screen.ServicesExplained.route) }
                    )
                }
            }

            item {
                SettingsTitle(text = "Contribute")
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text("Curate Terms of Service") },
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
                SettingsTitle(text = "This App")
                SettingsGroup {
                    SettingsRow(
                        leading = {
                            Icon(
                                Icons.Rounded.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text("Libraries") },
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
