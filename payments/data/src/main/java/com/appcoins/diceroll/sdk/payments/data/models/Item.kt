package com.appcoins.diceroll.sdk.payments.data.models

import android.content.Context
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.feature.payments.data.Skus
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.DEVELOPER_ERROR
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.SERVICE_UNAVAILABLE
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode.USER_CANCELED
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode as ResponseCode
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType as SkuType

/**
 * Payment item in game to represent and match a given SKU.
 */
sealed class Item(
    open val sku: String,
    open val type: String,
) {
    sealed class ConsumableItem(override val sku: String) : Item(sku, SkuType.INAPP.value)

    sealed class SubscriptionItem(override val sku: String) : Item(sku, SkuType.SUBS.value) {
        abstract fun getExpirationMessage(context: Context): String
    }

    data object Attempts : ConsumableItem(Skus.ATTEMPTS) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_attempts_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_attempts_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_attempts_success_message)
    }

    data object NonConsumableAttempts : ConsumableItem(Skus.NON_CONSUMABLE_ATTEMPTS) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_non_consumable_attempts_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_non_consumable_attempts_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_non_consumable_attempts_success_message)
    }

    data object GoldDice : SubscriptionItem(Skus.GOLDEN_DICE) {
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

    data object GoldDicePremium : SubscriptionItem(Skus.GOLDEN_DICE_PREMIUM) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_golden_dice_premium_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getExpirationMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_golden_dice_premium_expiration_message)

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_golden_dice_premium_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_golden_dice_premium_success_message)
    }

    data object TrialDice : SubscriptionItem(Skus.TRIAL_DICE) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_trial_dice_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getExpirationMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_trial_dice_expiration_message)

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_trial_dice_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_trial_dice_success_message)
    }

    data object PrepaidDice : SubscriptionItem(Skus.PREPAID_DICE) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_prepaid_dice_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getExpirationMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_trial_dice_expiration_message)

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_trial_dice_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_trial_dice_success_message)
    }

    data object SingleDiscountDice : SubscriptionItem(Skus.SINGLE_DISCOUNT_DICE) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_single_discount_dice_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getExpirationMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_single_discount_dice_expiration_message)

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_single_discount_dice_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_single_discount_dice_success_message)
    }

    data object RecurringDiscountDice : SubscriptionItem(Skus.RECURRING_DISCOUNT_DICE) {
        override fun getErrorMessage(context: Context, responseCode: ResponseCode): String =
            when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_recurring_discount_dice_error_message_user_cancelled)
                SERVICE_UNAVAILABLE -> context.resources.getString(R.string.payment_item_general_error_message_service_unavailable)
                DEVELOPER_ERROR -> context.resources.getString(R.string.payment_item_general_error_message_developer_error)
                else -> context.resources.getString(R.string.payment_item_general_error_message_unknown)
            }

        override fun getExpirationMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_recurring_discount_dice_expiration_message)

        override fun getRefundMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_recurring_discount_dice_refund_message)

        override fun getSuccessMessage(context: Context): String =
            context.resources.getString(R.string.payment_item_recurring_discount_dice_success_message)
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

        fun fromSku(sku: String): Item? =
            when (sku) {
                Skus.ATTEMPTS -> Attempts
                Skus.NON_CONSUMABLE_ATTEMPTS -> NonConsumableAttempts
                Skus.GOLDEN_DICE -> GoldDice
                Skus.GOLDEN_DICE_PREMIUM -> GoldDicePremium
                Skus.TRIAL_DICE -> TrialDice
                Skus.PREPAID_DICE -> PrepaidDice
                Skus.SINGLE_DISCOUNT_DICE -> SingleDiscountDice
                Skus.RECURRING_DISCOUNT_DICE -> RecurringDiscountDice
                else -> null
            }

        fun getGeneralErrorMessage(
            context: Context,
            item: Item?,
            responseCode: ResponseCode
        ): String =
            item?.getErrorMessage(context, responseCode) ?: when (responseCode) {
                USER_CANCELED -> context.resources.getString(R.string.payment_item_general_error_message_user_cancelled)
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
