package xyz.ptgms.tosdr.data

import kotlinx.coroutines.flow.StateFlow
import android.app.Activity

interface BillingManagerInterface {
    val purchaseState: StateFlow<PurchaseState>
    
    fun launchBillingFlow(activity: Activity, product: ProductInfo)
    
    sealed class PurchaseState {
        object IDLE : PurchaseState()
        data class ProductsAvailable(val products: List<ProductInfo>) : PurchaseState()
        object PurchaseSuccessful : PurchaseState()
        data class Error(val message: String) : PurchaseState()
    }
    
    data class ProductInfo(
        val id: String,
        val name: String,
        val price: String
    )
} 