package com.orestisz.zettle

import android.content.Intent
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.zettle.sdk.feature.cardreader.payment.TippingStyle
import com.orestisz.zettle.ZettleManager

@CapacitorPlugin(name = "ZettlePayment")
class ZettlePaymentPlugin : Plugin() {

    private var zettleManager: ZettleManager? = null

    override fun load() {
        super.load()
        // Ensure the activity is available before instantiating ZettleManager
        activity?.let {
            zettleManager = ZettleManager(it)
        }
    }

    @PluginMethod
    fun initiatePayment(call: PluginCall) {
        val amount = call.getLong("amount") ?: run {
            call.reject("Amount is required")
            return
        }
        val currency = call.getString("currency") ?: "EUR"

        zettleManager?.let {
            it.initiatePayment(amount, currency, TippingStyle.None, true, object : ZettleManager.ZettleOperationCallback {
                override fun onSuccess(result: Any?) {
                    val jsResult = JSObject().apply {
                        put("success", true)
                        result?.let { res -> put("result", res) }
                    }
                    call.resolve(jsResult)
                }

                override fun onFailure(error: String) {
                    call.reject(error)
                }
            })
        } ?: call.reject("ZettleManager is not initialized")
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.handleOnActivityResult(requestCode, resultCode, data)
        zettleManager?.handleActivityResult(requestCode, resultCode, data)
    }
}
