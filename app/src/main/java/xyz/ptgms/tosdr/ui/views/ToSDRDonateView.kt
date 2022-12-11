package xyz.ptgms.tosdr.ui.views

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ProductDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import xyz.ptgms.tosdr.R
import xyz.ptgms.tosdr.tools.data.Billing
import xyz.ptgms.tosdr.ui.views.elements.ExpandableCard

object ToSDRDonateView {

    private fun makePurchase(position: Int, billingClient: BillingClient, activity: Activity) {
        Log.d("Billing", "Making purchase for ${Billing.products[position].productId}")

        val products = mutableListOf("1euro_donation", "5euro_donation", "10euro_donation")

        var product: ProductDetails? = null

        Billing.products.forEach {
            if (it.productId == products[position]) {
                product = it
            }
        }

        if (product == null) {
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(product!!)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            Toast.makeText(activity, activity.getString(R.string.donation_error), Toast.LENGTH_SHORT).show()
        }

    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToSDRDonateView(modifier: Modifier, billingClient: BillingClient, activity: Activity, scope: CoroutineScope, drawerState: DrawerState) {
        val iapEnabled = Billing.products.isNotEmpty()

        Scaffold(topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.donate),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch { drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(R.string.menu)
                        )
                    }
                }
            )
        }) { padding ->
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
            ) {
                Text(
                    stringResource(R.string.donation_info),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(8.dp)
                )

                ExpandableCard(
                    title = stringResource(R.string.donation_info_card_title),
                    description = stringResource(
                        R.string.donation_info_card_description
                    ),
                    modifier = Modifier.padding(8.dp)
                )

                Divider(modifier = Modifier.padding(8.dp))

                ElevatedCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("1€", style = MaterialTheme.typography.titleLarge)
                            Text(
                                stringResource(R.string.donation_thanks_1euro),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Button(onClick = {
                            makePurchase(0, billingClient, activity)
                        }, enabled = iapEnabled) {
                            Text(stringResource(R.string.donate))
                        }
                    }
                }
                ElevatedCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("5€", style = MaterialTheme.typography.titleLarge)
                            Text(
                                stringResource(R.string.donation_thanks_5euro),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Button(onClick = {
                            makePurchase(1, billingClient, activity)
                        }, enabled = iapEnabled) {
                            Text(stringResource(R.string.donate))
                        }
                    }
                }
                ElevatedCard(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("10€", style = MaterialTheme.typography.titleLarge)
                            Text(
                                stringResource(R.string.donation_thanks_10euro),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Button(onClick = {
                            makePurchase(2, billingClient, activity)
                        }, enabled = iapEnabled) {
                            Text(stringResource(R.string.donate))
                        }
                    }
                }
            }
        }
    }
}