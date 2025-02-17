package com.appcoins.diceroll.sdk.payments.data.models

import android.content.Context
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.sdk.billing.ResponseCode
import com.appcoins.sdk.billing.ResponseCode.DEVELOPER_ERROR
import com.appcoins.sdk.billing.ResponseCode.SERVICE_UNAVAILABLE
import com.appcoins.sdk.billing.ResponseCode.USER_CANCELED
import com.appcoins.sdk.billing.types.SkuType

/**
 * Payment item in game to represent and match a given SKU.
 */
sealed class Item(
    open val sku: String,
    open val type: String,
) {
    sealed class ConsumableItem(override val sku: String) : Item(sku, SkuType.inapp.toString())

    sealed class SubscriptionItem(override val sku: String) : Item(sku, SkuType.subs.toString()) {
        abstract fun getExpirationMessage(context: Context): String
    }

    data object Attempts : ConsumableItem(ATTEMPTS_SKU) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_attempts_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_attempts_error_message_user_cancelled)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_attempts_error_message_user_cancelled)
    }

    data object GoldDice : SubscriptionItem(GOLD_DICE_SKU) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_golden_dice_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getExpirationMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_golden_dice_expiration_message)

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_golden_dice_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_golden_dice_success_message)
    }

    abstract fun getSuccessMessage(context: Context): String

    abstract fun getErrorMessage(context: Context, responseCode: ResponseCode): String

    abstract fun getRefundMessage(context: Context): String

    open fun getErrorTitle(context: Context, responseCode: ResponseCode): String =
        when (responseCode) {
            USER_CANCELED -> context.resources.getString(R.string.payment_item_general_error_title_user_cancelled)
            else -> context.resources.getString(R.string.payment_item_general_error_title_unknown)
        }

    companion object {
        const val ATTEMPTS_SKU = "attempts"

        const val GOLD_DICE_SKU = "golden_dice"

        fun fromSku(sku: String): Item =
            when (sku) {
                ATTEMPTS_SKU -> Attempts
                GOLD_DICE_SKU -> GoldDice
                else -> throw Exception()
            }

        fun getGeneralErrorMessage(
            context: Context,
            item: Item?,
            responseCode: ResponseCode
        ): String =
            item?.getErrorMessage(context, responseCode) ?: when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_attempts_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)

                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        fun getGeneralErrorTitle(
            context: Context,
            item: Item?,
            responseCode: ResponseCode
        ): String =
            item?.getErrorTitle(context, responseCode) ?: if (responseCode == USER_CANCELED) {
                context.resources.getString(R.string.payment_item_general_error_title_user_cancelled)
            } else {
                context.resources.getString(R.string.payment_item_general_error_title_unknown)
            }
    }
}
