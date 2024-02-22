package com.orestisz.zettle

import android.os.Handler
import android.os.Looper
import android.app.Activity
import android.content.Intent
import android.content.Context
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.zettle.sdk.ZettleSDK
import com.zettle.sdk.ZettleSDKLifecycle
import com.zettle.sdk.config
import com.zettle.sdk.feature.cardreader.payment.TippingStyle
import com.zettle.sdk.feature.cardreader.payment.TransactionReference
import com.zettle.sdk.feature.cardreader.ui.CardReaderAction
import com.zettle.sdk.features.charge
import com.zettle.sdk.feature.cardreader.ui.CardReaderFeature
import com.zettle.sdk.feature.cardreader.ui.payment.CardPaymentResult
import com.zettle.sdk.config
import java.util.*
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.google.android.material.snackbar.Snackbar
import com.zettle.sdk.feature.cardreader.payment.Transaction
import com.zettle.sdk.feature.cardreader.payment.refunds.CardPaymentPayload
import com.zettle.sdk.feature.cardreader.payment.refunds.RefundPayload
import com.zettle.sdk.feature.cardreader.ui.RetrieveResult
import com.zettle.sdk.feature.cardreader.ui.refunds.RefundResult
import com.zettle.sdk.features.charge
import com.zettle.sdk.features.refund
import com.zettle.sdk.features.retrieve
import com.zettle.sdk.features.show
import com.zettle.sdk.ui.ZettleResult
import com.zettle.sdk.ui.zettleResult
import kotlin.math.abs

class ZettleManager(private val activity: Activity) {

    private lateinit var lastPaymentTraceId: MutableLiveData<String?>

    fun initialize(devMode: Boolean) {
        // Optionally place SDK initialization here if not already initialized in Application class
        val clientId = activity.getString(R.string.client_id)
        val scheme = activity.getString(R.string.redirect_url_scheme)
        val host = activity.getString(R.string.redirect_url_host)
        val redirectUrl = "$scheme://$host"

        val config = config(activity.applicationContext) {
            isDevMode = devMode
            auth {
                this.clientId = clientId
                this.redirectUrl = redirectUrl // For Managed Authentication
                this.tokenProvider = tokenProvider // New in SDK v2 (For Provided Authentication)
            }
            addFeature(CardReaderFeature.Configuration)
            // addFeature(PayPalQrcFeature.Configuration)
            // addFeature(VenmoQrcFeature.Configuration)
            // addFeature(ManualCardEntryFeature.Configuration)
        }
        ZettleSDK.configure(config)

        // Attach the SDKs lifecycle observer to your lifecycle. It allows the SDK to
        // manage bluetooth connection in a more graceful way
        Handler(Looper.getMainLooper()).post {
            ProcessLifecycleOwner.get().lifecycle.addObserver(ZettleSDKLifecycle())
        }
    }

    fun doLogin() {
        val color = ResourcesCompat.getColor(activity.resources, R.color.colorAccent, null)
        ZettleSDK.instance?.login(activity, color)
    }

    fun preparePaymentIntent(amount: Long, currency: String, tippingStyle: TippingStyle = TippingStyle.Default, enableInstallments: Boolean = true): Intent {
        val internalTraceId = UUID.randomUUID().toString()
        val reference = TransactionReference.Builder(internalTraceId)
            // .put("PAYMENT_EXTRA_INFO", "Started from home screen")
            // .paypalPartnerAttributionId("bnCode")
            .build()

        return CardReaderAction.Payment(
            amount = amount,
            reference = reference,
            tippingStyle = tippingStyle,
            enableInstallments = enableInstallments
        ).charge(context = activity)
    }

    // fun charge(amount: Long, currency: String, tippingStyle: TippingStyle = TippingStyle.Default, enableInstallments: Boolean = true) {
    //     val paymentIntent = preparePaymentIntent(amount, currency, tippingStyle, enableInstallments)
    //     activity.startActivityForResult(paymentIntent, PAYMENT_REQUEST_CODE)
    // }

    companion object {
        const val PAYMENT_REQUEST_CODE = 1001
    }
}
