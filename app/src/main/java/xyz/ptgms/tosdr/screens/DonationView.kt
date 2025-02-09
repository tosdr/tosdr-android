package xyz.ptgms.tosdr.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import xyz.ptgms.tosdr.BuildConfig
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.components.settings.SettingsGroup
import xyz.ptgms.tosdr.components.settings.SettingsRow
import xyz.ptgms.tosdr.components.settings.SettingsTitle
import xyz.ptgms.tosdr.data.BillingManager
import xyz.ptgms.tosdr.data.BillingManagerInterface.PurchaseState

@Composable
fun DonationScreen(navController: NavController) {
    @Suppress("KotlinConstantConditions")
    if (BuildConfig.FLAVOR == "foss") {
        FossDonationScreen(navController)
    } else {
        GoogleDonationScreen(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FossDonationScreen(navController: NavController) {
    val clipboardManager = LocalClipboardManager.current
    var lastCopiedCrypto by remember { mutableStateOf<String?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text(stringResource(R.string.donate_title)) }
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
                SettingsTitle(text = stringResource(R.string.nongplay_donation_title))
                SettingsGroup {
                    SettingsRow(
                        title = {
                            Text(stringResource(R.string.nongplay_donation_desc))
                        }
                    )
                    SettingsRow(
                        title = { Text(stringResource(R.string.nongplay_donation_opencollective)) },
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://opencollective.com/tosdr"))
                            navController.context.startActivity(intent)
                        },
                        trailing = {
                            Icon(Icons.AutoMirrored.Rounded.KeyboardArrowRight, contentDescription = null)
                        }
                    )
                }
            }

            item {
                SettingsTitle(text = stringResource(R.string.nongplay_donation_crypto))
                SettingsGroup {
                    val cryptoWallets = listOf(
                        "BTC" to "bc1qnjcxk6xllv3dnzzxd74843lhp59njwd94u6yza",
                        "ETH" to "0xfcCAa73b6a17bCfED9998b20028172f1E7afb3ac",
                        "LTC" to "ltc1q0t89mvcxcuma04tm2z0r3gdzj7kpfy32q6gmm2",
                        "TON" to "UQCp0ZCuEGIvTEX3261ylE3APvBne_GmDopaHYNY-88JMI7e",
                        "BTC Cash" to "qrq7a52l58q383llcavr65cs493rzt90uu9g4rq0d3",
                        "Doge" to "DSeQaoY19hb9U6afzYELRvtRctGs73qUcm"
                    )

                    cryptoWallets.forEach { (currency, address) ->
                        OutlinedTextField(
                            value = address,
                            onValueChange = { },
                            enabled = false,
                            readOnly = true,
                            label = { Text(currency) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(24.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    clipboardManager.setText(AnnotatedString(address))
                                    lastCopiedCrypto = currency
                                }) {
                                    Icon(
                                        painterResource(R.drawable.ic_rounded_content_copy_24),
                                        contentDescription = "Copy address",
                                        tint = if (lastCopiedCrypto == currency)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleDonationScreen(navController: NavController) {
    val context = LocalContext.current
    val billingManager = remember { BillingManager(context) }
    val purchaseState by billingManager.purchaseState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                },
                title = { Text(stringResource(R.string.donate_title)) }
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
                SettingsTitle(text = stringResource(R.string.donate_support))
                SettingsGroup {
                    SettingsRow(
                        title = {
                            Text(
                                stringResource(R.string.donate_support_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }

            when (val state = purchaseState) {
                is PurchaseState.ProductsAvailable -> {
                    item {
                        SettingsTitle(text = stringResource(R.string.donate_amount))
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
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            FilledTonalButton(
                                                onClick = {
                                                    billingManager.launchBillingFlow(
                                                        context as ComponentActivity,
                                                        product
                                                    )
                                                }
                                            ) {
                                                Text(stringResource(R.string.donate_purchase))
                                            }
                                            Icon(
                                                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is PurchaseState.PurchaseSuccessful -> {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Rounded.Favorite,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    stringResource(R.string.donation_thanks),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    stringResource(R.string.donation_thanks_desc),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
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
