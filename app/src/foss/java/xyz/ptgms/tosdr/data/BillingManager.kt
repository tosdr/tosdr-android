package xyz.ptgms.tosdr.data

import android.app.Activity
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BillingManager(context: Context) : BillingManagerInterface {
    private val _purchaseState = MutableStateFlow<BillingManagerInterface.PurchaseState>(
        BillingManagerInterface.PurchaseState.IDLE
    )
    override val purchaseState: StateFlow<BillingManagerInterface.PurchaseState> = _purchaseState
    override fun launchBillingFlow(
        activity: Activity,
        product: BillingManagerInterface.ProductInfo
    ) {
        // No-op in FOSS version
    }
} 