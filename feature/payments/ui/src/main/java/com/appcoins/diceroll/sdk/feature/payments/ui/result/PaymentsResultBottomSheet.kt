package com.appcoins.diceroll.sdk.feature.payments.ui.result

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.ui.widgets.ErrorAnimation
import com.appcoins.diceroll.sdk.core.ui.widgets.LoadingAnimation
import com.appcoins.diceroll.sdk.core.ui.widgets.SuccessAnimation
import com.appcoins.diceroll.sdk.feature.payments.ui.Item

@Composable
fun PaymentsResult(
    itemId: String,
    uiState: PaymentsResultUiState,
    onPaymentSuccess: suspend () -> Unit,
) {
    when (uiState) {
        is PaymentsResultUiState.Initialized -> {}
        is PaymentsResultUiState.Loading -> LoadingAnimation(bodyMessage = stringResource(id = R.string.payments_loading))
        is PaymentsResultUiState.UserCanceled -> ErrorAnimation(
            titleMessage = stringResource(R.string.payments_user_canceled_title),
            bodyMessage = stringResource(R.string.payments_user_canceled_body)
        )

        is PaymentsResultUiState.Failed -> {
            ErrorAnimation(
                titleMessage = stringResource(R.string.payments_failed_title),
                bodyMessage = stringResource(getFailedBodyMessage(itemId))
            )
        }

        is PaymentsResultUiState.Success -> SuccessContent(itemId, onPaymentSuccess)
    }
}

@Composable
fun SuccessContent(itemId: String, onPaymentSuccess: suspend () -> Unit) {
    LaunchedEffect(rememberCoroutineScope()) {
        onPaymentSuccess()
    }
    SuccessAnimation(
        titleMessage = stringResource(R.string.payments_success_title),
        bodyMessage = stringResource(getSuccessBodyMessage(itemId))
    )
}

fun getFailedBodyMessage(itemId: String): Int =
    when (itemId) {
        Item.GOLD_DICE_SKU -> R.string.payments_golden_dice_failed_body
        else -> R.string.payments_attempts_failed_body
    }

fun getSuccessBodyMessage(itemId: String): Int =
    when (itemId) {
        Item.GOLD_DICE_SKU -> R.string.payments_golden_dice_success_body
        else -> R.string.payments_attempts_success_body
    }
