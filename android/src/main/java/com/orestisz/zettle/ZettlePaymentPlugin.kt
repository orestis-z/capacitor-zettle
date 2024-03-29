package com.orestisz.zettle

import android.app.Activity
import android.content.Intent
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import com.getcapacitor.PluginMethod
import com.zettle.sdk.feature.cardreader.ui.CardReaderAction
import com.zettle.sdk.feature.cardreader.ui.payment.CardPaymentResult
import com.zettle.sdk.feature.cardreader.ui.refunds.RefundResult
import com.zettle.sdk.feature.cardreader.payment.TippingStyle
import com.zettle.sdk.ui.zettleResult
import com.zettle.sdk.ui.ZettleResult
import com.orestisz.zettle.ZettleManager
import com.zettle.sdk.features.show
import android.util.Log

@CapacitorPlugin(name = "ZettlePayment", requestCodes = [ZettleManager.PAYMENT_REQUEST_CODE, ZettleManager.REFUND_REQUEST_CODE])
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

    @PluginMethod
    fun initiateRefund(call: PluginCall) {
        val amount = call.getInt("amount")?.toLong() ?: run {
            call.reject("Amount is required")
            return
        }
       val taxAmount = call.getInt("taxAmount")?.toLong() ?: null
       val receiptNumber = call.getString("receiptNumber") ?: null

        // Prepare the refund intent from ZettleManager
        val refundIntent = zettleManager?.prepareRefundIntent(amount, taxAmount, receiptNumber)

        // Now use startActivityForResult to launch the refund activity
        // Make sure to store the call for later
        saveCall(call)

        if (refundIntent != null) {
            startActivityForResult(call, refundIntent, ZettleManager.REFUND_REQUEST_CODE)
        } else {
            call.reject("Failed to prepare refund intent.")
        }
    }

    @PluginMethod
    fun showCardReaderSettings(call: PluginCall) {
        context.startActivity(CardReaderAction.Settings.show(context))
        call.resolve()
    }

    override fun handleOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.handleOnActivityResult(requestCode, resultCode, data)

        if (requestCode == ZettleManager.PAYMENT_REQUEST_CODE) {
            val savedCall = savedCall ?: return

            if (resultCode == Activity.RESULT_OK) {
                handlePaymentResult(data?.zettleResult() ?: return, savedCall)
            } else {
                savedCall.reject("UnknownError")
            }
        }
        else if (requestCode == ZettleManager.REFUND_REQUEST_CODE) {
            val savedCall = savedCall ?: return

            if (resultCode == Activity.RESULT_OK) {
                handleRefundResult(data?.zettleResult() ?: return, savedCall)
            } else {
                savedCall.reject("UnknownError")
            }
        }
    }

    private fun handlePaymentResult(result: ZettleResult, call: PluginCall) {
        when (result) {
            is ZettleResult.Completed<*> -> {
                val payment: CardPaymentResult.Completed = CardReaderAction.fromPaymentResult(result)
                var ret = JSObject()
                ret.put("amount", payment.payload.amount);
                ret.put("referenceId", payment.payload.reference?.id);
                ret.put("gratuityAmount", payment.payload.gratuityAmount);
                ret.put("cardType", payment.payload.cardType);
                ret.put("cardPaymentEntryMode", payment.payload.cardPaymentEntryMode);
                ret.put("cardholderVerificationMethod", payment.payload.cardholderVerificationMethod);
                ret.put("tsi", payment.payload.tsi);
                ret.put("tvr", payment.payload.tvr);
                ret.put("applicationIdentifier", payment.payload.applicationIdentifier);
                ret.put("cardIssuingBank", payment.payload.cardIssuingBank);
                ret.put("maskedPan", payment.payload.maskedPan);
                ret.put("panHash", payment.payload.panHash);
                ret.put("applicationName", payment.payload.applicationName);
                ret.put("authorizationCode", payment.payload.authorizationCode);
                ret.put("installmentAmount", payment.payload.installmentAmount);
                ret.put("nrOfInstallments", payment.payload.nrOfInstallments);
                ret.put("mxFiid", payment.payload.mxFiid);
                ret.put("mxCardType", payment.payload.mxCardType);
                ret.put("referenceNumber", payment.payload.referenceNumber);
                ret.put("transactionId", payment.payload.transactionId);
                call.resolve(ret)
            }
            is ZettleResult.Cancelled -> call.reject("Cancelled")
            is ZettleResult.Failed -> call.reject(result.reason.javaClass.simpleName)
            null -> call.reject("UnknownError")
        }
    }

    private fun handleRefundResult(result: ZettleResult, call: PluginCall) {
        when (result) {
            is ZettleResult.Completed<*> -> {
                val refund: RefundResult.Completed = CardReaderAction.fromRefundResult(result)
                var ret = JSObject()
                ret.put("originalAmount", refund.payload.originalAmount);
                ret.put("refundedAmount", refund.payload.refundedAmount);
                ret.put("cardType", refund.payload.cardType);
                ret.put("maskedPan", refund.payload.maskedPan);
                ret.put("transactionId", refund.payload.transactionId);
                call.resolve(ret)
            }
            is ZettleResult.Cancelled -> call.reject("Cancelled")
            is ZettleResult.Failed -> call.reject(result.reason.javaClass.simpleName)
            null -> call.reject("UnknownError")
        }
    }

}
