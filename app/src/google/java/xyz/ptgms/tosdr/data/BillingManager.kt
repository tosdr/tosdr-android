package xyz.ptgms.tosdr.data

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(
    context: Context
) : PurchasesUpdatedListener, BillingClientStateListener, BillingManagerInterface {

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

    private val _purchaseState = MutableStateFlow<BillingManagerInterface.PurchaseState>(
        BillingManagerInterface.PurchaseState.IDLE
    )
    override val purchaseState: StateFlow<BillingManagerInterface.PurchaseState> = _purchaseState

    // Store the original ProductDetails objects
    private var productDetailsMap: Map<String, ProductDetails> = emptyMap()

    init {
        billingClient.startConnection(this)
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            queryAvailableProducts()
            consumeExistingPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        billingClient.startConnection(this)
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: List<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        }
    }

    private fun queryAvailableProducts() {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("1euro_donation")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("5euro_donation")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build(),
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("10euro_donation")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val sortedProducts = productDetailsList.sortedBy { 
                    it.oneTimePurchaseOfferDetails?.priceAmountMicros ?: 0L
                }
                // Store the ProductDetails for later use
                productDetailsMap = sortedProducts.associateBy { it.productId }
                
                val mappedProducts = sortedProducts.map { details ->
                    BillingManagerInterface.ProductInfo(
                        id = details.productId,
                        name = details.name,
                        price = details.oneTimePurchaseOfferDetails?.formattedPrice ?: ""
                    )
                }
                _purchaseState.value = BillingManagerInterface.PurchaseState.ProductsAvailable(mappedProducts)
            }
        }
    }

    private fun consumeExistingPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        ) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        val consumeParams = ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.purchaseToken)
                            .build()
                        
                        billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                            if (consumeResult.responseCode != BillingClient.BillingResponseCode.OK) {
                                _purchaseState.value = BillingManagerInterface.PurchaseState.Error("Failed to consume existing purchase")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun launchBillingFlow(activity: Activity, product: BillingManagerInterface.ProductInfo) {
        val productDetails = productDetailsMap[product.id]
        if (productDetails != null) {
            val billingFlowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetails)
                            .build()
                    )
                )
                .build()

            billingClient.launchBillingFlow(activity, billingFlowParams)
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED && !purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val consumeParams = ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    billingClient.consumeAsync(consumeParams) { consumeResult, _ ->
                        if (consumeResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            _purchaseState.value = BillingManagerInterface.PurchaseState.PurchaseSuccessful
                        }
                    }
                }
            }
        }
    }
}