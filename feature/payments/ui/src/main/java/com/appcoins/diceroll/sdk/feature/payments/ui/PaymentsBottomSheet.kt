package com.appcoins.diceroll.sdk.feature.payments.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.navigation.destinations.DestinationArgs
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.ui.widgets.ErrorAnimation
import com.appcoins.diceroll.sdk.core.ui.widgets.LoadingAnimation
import com.appcoins.diceroll.sdk.core.ui.widgets.components.DiceRollBottomSheet
import com.appcoins.diceroll.sdk.feature.payments.ui.result.PaymentsResult

@Composable
fun PaymentProcessBottomSheetRoute(
    onDismiss: () -> Unit,
    itemId: String,
    viewModel: PaymentsViewModel = hiltViewModel()
) {
    val context = LocalContext.current as Activity

    viewModel.savedStateHandle[DestinationArgs.ITEM_ID] = itemId

    val paymentProcessState by viewModel.paymentProcessState.collectAsStateWithLifecycle()
    val paymentResultState by viewModel.paymentResultState.collectAsStateWithLifecycle()

    DiceRollBottomSheet(onDismiss) {
        when (paymentProcessState) {
            is PaymentProcessUiState.Loading -> {
                LoadingAnimation()
            }

            is PaymentProcessUiState.Error -> {
                ErrorAnimation(
                    bodyMessage = stringResource(R.string.payments_sku_error_body)
                )
            }

            is PaymentProcessUiState.NotAvailable -> {
                ErrorAnimation(
                    bodyMessage = stringResource(viewModel.getNotAvailableMessageForItem(itemId))
                )
            }

            is PaymentProcessUiState.StartPayment -> {
                viewModel.launchInAppBillingSdkFlow(context)
            }

            is PaymentProcessUiState.PaymentInProgress -> {
                PaymentsResult(
                    itemId,
                    paymentResultState,
                    viewModel::handlePaymentFinished,
                )
            }
        }
    }
}
