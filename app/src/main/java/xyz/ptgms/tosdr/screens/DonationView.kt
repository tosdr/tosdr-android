package xyz.ptgms.tosdr.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.billing.BillingManager

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
                .padding(16.dp)
        ) {
            item {
                Text(
                    "Support ToS;DR",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Your donation helps us maintain and improve our services. Choose an amount below:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when (val state = purchaseState) {
                is BillingManager.PurchaseState.ProductsAvailable -> {
                    items(state.products) { product ->
                        ElevatedButton(
                            onClick = {
                                billingManager.launchBillingFlow(
                                    context as androidx.activity.ComponentActivity,
                                    product
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(product.name)
                        }
                    }
                }
                is BillingManager.PurchaseState.PurchaseSuccessful -> {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Text(
                                "Thank you for your donation!",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                else -> {
                    item {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
