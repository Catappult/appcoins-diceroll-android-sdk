package com.appcoins.diceroll.sdk.payments.data.models

import com.appcoins.sdk.billing.ResponseCode
import com.appcoins.sdk.billing.types.SkuType

/**
 * Payment item in game to represent and match a given SKU.
 */
sealed class Item(
    open val sku: String,
    open val type: String,
    open val successPurchaseMessage: String,
) {
    sealed class ConsumableItem(
        override val sku: String,
        override val successPurchaseMessage: String
    ) : Item(sku, SkuType.inapp.toString(), successPurchaseMessage)

    sealed class SubscriptionItem(
        override val sku: String,
        override val successPurchaseMessage: String
    ) : Item(sku, SkuType.subs.toString(), successPurchaseMessage) {
        abstract fun getExpirationMessage(): String
    }

    data object Attempts : ConsumableItem(ATTEMPTS_SKU, ATTEMPTS_SUCCESS_MESSAGE) {
        override fun getErrorMessage(responseCode: ResponseCode): String =
            when (responseCode) {
                ResponseCode.USER_CANCELED -> "Purchase for Attempts was cancelled."
                ResponseCode.SERVICE_UNAVAILABLE -> "The Billing Service is not available."
                ResponseCode.DEVELOPER_ERROR -> "There was an error when creating the Purchase.\nPlease contact us."
                else -> "There was an error processing the Purchase. $responseCode"
            }

        override fun getRefundMessage(): String =
            "Your Purchase for Attempts was Refunded."
    }

    data object GoldDice : SubscriptionItem(GOLD_DICE_SKU, GOLD_DICE_SUCCESS_MESSAGE) {
        override fun getErrorMessage(responseCode: ResponseCode): String =
            when (responseCode) {
                ResponseCode.USER_CANCELED -> "Purchase for Golden Dice was cancelled."
                ResponseCode.SERVICE_UNAVAILABLE -> "The Billing Service is not available."
                ResponseCode.DEVELOPER_ERROR -> "There was an error when creating the Purchase.\nPlease contact us."
                else -> "There was an error processing the Purchase. $responseCode"
            }

        override fun getExpirationMessage(): String =
            "Your subscription for the Golden Dice was expired.\nPurchase again to get it!"

        override fun getRefundMessage(): String =
            "Your Subscription for the Golden Dice was Refunded."
    }

    abstract fun getErrorMessage(responseCode: ResponseCode): String

    abstract fun getRefundMessage(): String

    open fun getErrorTitle(responseCode: ResponseCode): String =
        when (responseCode) {
            ResponseCode.USER_CANCELED -> "Purchase cancelled"
            else -> "Purchase Failed"
        }

    companion object {
        const val ATTEMPTS_SKU = "attempts"
        const val ATTEMPTS_SUCCESS_MESSAGE = "Purchase successful!\nYou have received 3 attempts."

        const val GOLD_DICE_SKU = "golden_dice"
        const val GOLD_DICE_SUCCESS_MESSAGE = "Purchase successful!\nYou have received 3 attempts."

        fun fromSku(sku: String): Item =
            when (sku) {
                ATTEMPTS_SKU -> Attempts
                GOLD_DICE_SKU -> GoldDice
                else -> throw Exception()
            }
    }
}
