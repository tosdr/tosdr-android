package xyz.ptgms.tosdr.screens

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.data.BillingManager
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsRow
import xyz.ptgms.tosdr.components.settings.SettingsTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(navController: NavController) {
    val context = LocalContext.current
    val billingManager = remember { BillingManager(context) }
    val purchaseState by billingManager.purchaseState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                title = { Text("Donate") }
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
                SettingsTitle(text = "Support ToS;DR")
                SettingsGroup {
                    SettingsRow(
                        title = {
                            Text(
                                "Your donation helps us maintain and improve our services. Choose an amount below:",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            when (val state = purchaseState) {
                is BillingManager.PurchaseState.ProductsAvailable -> {
                    item {
                        SettingsTitle(text = "Choose Amount")
                        SettingsGroup {
                            state.products.forEach { product ->
                                SettingsRow(
                                    leading = {
                                        Icon(
                                            Icons.Rounded.Star,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    title = { Text(product.name) },
                                    onClick = {
                                        billingManager.launchBillingFlow(
                                            context as ComponentActivity,
                                            product
                                        )
                                    },
                                    trailing = {
                                        Icon(
                                            Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                            contentDescription = null
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
                is BillingManager.PurchaseState.PurchaseSuccessful -> {
                    item {
                        SettingsGroup {
                            SettingsRow(
                                title = {
                                    Text(
                                        "Thank you for your donation!",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            )
                        }
                    }
                }
                else -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}
