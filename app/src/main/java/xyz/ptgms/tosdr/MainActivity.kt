package xyz.ptgms.tosdr

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.tools.data.Billing
import xyz.ptgms.tosdr.tools.data.NavigationItem
import xyz.ptgms.tosdr.ui.theme.ToSDRTheme
import xyz.ptgms.tosdr.ui.views.ToSDRAboutView
import xyz.ptgms.tosdr.ui.views.ToSDRDetailView.ToSDRDetailView
import xyz.ptgms.tosdr.ui.views.ToSDRDonateView
import xyz.ptgms.tosdr.ui.views.ToSDRHomeView
import xyz.ptgms.tosdr.ui.views.ToSDRLicensesView
import xyz.ptgms.tosdr.ui.views.ToSDRSearchView
import xyz.ptgms.tosdr.ui.views.ToSDRSettingsView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow app to be drawn behind the status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        billingSetup()

        Log.i("Main Activity", "Welcome! Running with locale ${Locale.current.language}")

        setContent {
            ToSDRTheme {
                DrawerLayout(billingClient)
            }
        }
    }

    private lateinit var billingClient: BillingClient

    private fun billingSetup() {
        Log.d("Billing", "Setting up billing client")
        billingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing", "Billing setup finished")
                    queryProductDetails()
                } else {
                    Log.e("Billing", "Billing setup failed")
                    Log.e("Billing", billingResult.debugMessage)
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d("Billing", "Billing service disconnected")

                billingClient.startConnection(this)
            }
        })
    }

    private fun queryProductDetails() {
        val skuList = listOf("1euro_donation", "5euro_donation", "10euro_donation")
        skuList.forEach {
            val query = QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        QueryProductDetailsParams.Product.newBuilder()
                            .setProductId(it)
                            .setProductType(BillingClient.ProductType.INAPP)
                            .build()
                    )
                )
                .build()

            billingClient.queryProductDetailsAsync(query) { billingResult,
                                                            productDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("Billing", "Product details query finished")

                    for (product in productDetailsList) {
                        Log.d("Billing", "Product: ${product.title}")
                    }
                    Billing.products.add(productDetailsList[0])
                } else {
                    Log.d(
                        "Billing",
                        "Product details query failed with code ${billingResult.responseCode}"
                    )
                    Log.d("Billing", billingResult.debugMessage)
                }
            }
        }
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.OK
                && purchases != null
            ) {
                for (purchase in purchases) {
                    Toast.makeText(
                        this,
                        getString(R.string.donation_thanks),
                        Toast.LENGTH_SHORT
                    ).show()
                    // Consume the purchase
                    billingClient.consumeAsync(ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken).build()) { billingConsumeResult, _ ->
                        if (billingConsumeResult.responseCode ==
                            BillingClient.BillingResponseCode.OK
                        ) {
                            Log.d("Billing", "Purchase consumed")
                        } else {
                            Log.d("Billing", "Purchase not consumed")
                        }
                    }
                }
            } else if (billingResult.responseCode ==
                BillingClient.BillingResponseCode.USER_CANCELED
            ) {
                Toast.makeText(
                    this,
                    getString(R.string.donation_cancelled),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.donation_failed),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DrawerLayout(billingClient: BillingClient) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val items: List<NavigationItem> = listOf(
            NavigationItem(stringResource(R.string.home), Icons.Default.Home, "home"),
            NavigationItem(stringResource(R.string.search), Icons.Default.Search, "search"),
            //NavigationItem("Test Search", Icons.Default.Search, "details/182/E"),
        )

        val settingItems: List<NavigationItem> = listOf(
            NavigationItem(stringResource(R.string.settings), Icons.Default.Settings, "settings"),
            NavigationItem(stringResource(R.string.about), Icons.Default.Face, "about"),
            NavigationItem(stringResource(R.string.donate), Icons.Default.Favorite, "donate"),
        )

        val selectedItem = remember { mutableStateOf(items[0]) }
        var title by rememberSaveable { mutableStateOf("Home") }
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Column(
                        modifier = Modifier
                            //.fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Display App icon and name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon),
                                contentDescription = stringResource(id = R.string.app_name),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(id = R.string.app_name),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        //Spacer(Modifier.height(12.dp))
                        Divider(modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.navigation),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        Spacer(Modifier.height(12.dp))
                        items.forEach { item ->
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(item.name) },
                                selected = item == selectedItem.value,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    selectedItem.value = item
                                    navController.navigate(item.page) {
                                        popUpTo(0)
                                    }
                                    title = item.name
                                },
                                modifier = Modifier
                                    .padding(NavigationDrawerItemDefaults.ItemPadding)
                                    .fillMaxWidth()
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Divider(modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = stringResource(id = R.string.settings),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                        Spacer(Modifier.height(12.dp))
                        settingItems.forEach { item ->
                            NavigationDrawerItem(
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(item.name) },
                                selected = item == selectedItem.value,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    selectedItem.value = item
                                    navController.navigate(item.page) {
                                        popUpTo(0)
                                    }
                                    title = item.name
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                            )
                        }
                    }
                }
            },
            content = {
                MainView(
                    drawerState,
                    scope,
                    navController = navController,
                    billingClient
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MainView(
        drawerState: DrawerState,
        scope: CoroutineScope,
        navController: NavHostController,
        billingClient: BillingClient,
        activity: Activity = this
    ) {
        Scaffold { padding ->
            Log.d("MainView", "Padding: $padding")
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        ToSDRHomeView.ToSDRHomeView(modifier = Modifier, scope, drawerState)
                    }
                    composable("search") {
                        ToSDRSearchView.ToSDRView(
                            modifier = Modifier,
                            scope = scope,
                            drawerState = drawerState,
                            navHostController = navController
                        )
                    }
                    composable("settings") {
                        ToSDRSettingsView.ToSDRSettingsView(modifier = Modifier, scope, drawerState, navController)
                    }
                    composable("about") {
                        ToSDRAboutView.ToSDRAboutView(modifier = Modifier, scope = scope, drawerState)
                    }
                    composable("licenses") {
                        ToSDRLicensesView.ToSDRLicensesView(modifier = Modifier, scope = scope, navController)
                    }
                    composable("donate") {
                        ToSDRDonateView.ToSDRDonateView(
                            modifier = Modifier,
                            billingClient = billingClient,
                            activity = activity,
                            scope = scope,
                            drawerState = drawerState
                        )
                    }
                    composable("details/{id}/{grade}") { backStackEntry ->
                        ToSDRDetailView(
                            modifier = Modifier,
                            page = backStackEntry.arguments?.getString("id") ?: "",
                            grade = backStackEntry.arguments?.getString("grade") ?: "None",
                            navController = navController
                        )
                    }

                    composable(
                        "details/{id}/{grade}",
                        deepLinks = listOf(navDeepLink {
                            uriPattern = "tosdr://xyz.ptgms.space/{id}/{grade}"
                        }),
                    ) { backStackEntry ->
                        ToSDRDetailView(
                            modifier = Modifier,
                            page = backStackEntry.arguments?.getString("id") ?: "",
                            grade = backStackEntry.arguments?.getString("grade") ?: "None",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
