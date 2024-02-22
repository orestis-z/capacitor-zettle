package com.orestisz.zettle

import android.app.Activity
import android.content.Intent
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.zettle.sdk.feature.cardreader.payment.TippingStyle
import com.zettle.sdk.ui.zettleResult
import com.zettle.sdk.ui.ZettleResult
import com.orestisz.zettle.ZettleManager
import android.util.Log

@CapacitorPlugin(name = "ZettlePayment", requestCodes = [ZettleManager.PAYMENT_REQUEST_CODE])
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
    fun initialize(call: PluginCall) {
        val devMode = call.getBoolean("devMode") ?: false
        zettleManager?.initialize(devMode)
        call.resolve()
    }

    @PluginMethod
    fun initiatePayment(call: PluginCall) {
        val amount = call.getInt("amount")?.toLong() ?: run {
            call.reject("Amount is required")
            return
        }
        val currency = call.getString("currency") ?: "USD"

        // Prepare the payment intent from ZettleManager
        val paymentIntent = zettleManager?.preparePaymentIntent(amount, currency, TippingStyle.Default, true)

        // Now use startActivityForResult to launch the payment activity
        // Make sure to store the call for later
        saveCall(call)

        if (paymentIntent != null) {
            startActivityForResult(call, paymentIntent, ZettleManager.PAYMENT_REQUEST_CODE)
        } else {
            call.reject("Failed to prepare payment intent.")
        }
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.handleOnActivityResult(requestCode, resultCode, data)

        if (requestCode == ZettleManager.PAYMENT_REQUEST_CODE) {
            val savedCall = savedCall ?: return

            if (resultCode == Activity.RESULT_OK) {
                when (val result = data?.zettleResult()) {
                    is ZettleResult.Completed<*> -> {
                        savedCall.resolve()
                     }
                    is ZettleResult.Cancelled -> {
                        savedCall.reject("Payment cancelled")
                     }
                    is ZettleResult.Failed -> {
                        savedCall.reject("Payment failed")
                     }
                    null -> savedCall.reject("Something went wrong")
                }
            } else {
                savedCall.reject("Something went wrong")
            }
        }
    }

}
