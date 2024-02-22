package com.orestisz.zettle

import android.app.Activity
import android.content.Intent
import androidx.core.content.res.ResourcesCompat
import com.zettle.sdk.ZettleSDK
import com.zettle.sdk.feature.cardreader.payment.TippingStyle
import com.zettle.sdk.feature.cardreader.payment.TransactionReference
import com.zettle.sdk.feature.cardreader.ui.CardReaderAction
import com.zettle.sdk.features.charge
import java.util.*

class ZettleManager(private val activity: Activity) {

    interface ZettleOperationCallback {
        fun onSuccess(result: Any? = null)
        fun onFailure(error: String)
    }

    var paymentCallback: ZettleOperationCallback? = null // Correctly define the paymentCallback here


    fun initialize() {
        // Optionally place SDK initialization here if not already initialized in Application class
    }

    fun doLogin() {
        val color = ResourcesCompat.getColor(activity.resources, R.color.colorAccent, null)
        ZettleSDK.instance?.login(activity, color)
    }

    fun initiatePayment(amount: Long, currency: String, tippingStyle: TippingStyle = TippingStyle.None, enableInstallments: Boolean = true, callback: ZettleOperationCallback) {
        this.paymentCallback = callback // Save the callback

        val internalTraceId = UUID.randomUUID().toString()
        val reference = TransactionReference.Builder(internalTraceId)
            .put("PAYMENT_EXTRA_INFO", "Started from home screen")
            .paypalPartnerAttributionId("bnCode")
            .build()

        val intent: Intent = CardReaderAction.Payment(
            amount = amount,
            reference = reference,
            tippingStyle = tippingStyle,
            enableInstallments = enableInstallments
        ).charge(context = activity)

        activity.startActivityForResult(intent, PAYMENT_REQUEST_CODE)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PAYMENT_REQUEST_CODE) {
            // Example handling, adjust as needed based on the SDK's documentation
            when (resultCode) {
                Activity.RESULT_OK -> paymentCallback?.onSuccess(data?.getStringExtra("result"))
                Activity.RESULT_CANCELED -> paymentCallback?.onFailure("Payment cancelled")
                else -> paymentCallback?.onFailure("Payment failed")
            }
        }
    }

    companion object {
        const val PAYMENT_REQUEST_CODE = 1001
    }
}
