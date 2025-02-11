package com.appcoins.diceroll.sdk.feature.payments.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.appcoins.diceroll.sdk.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.sdk.core.utils.payments.models.PaymentState
import com.appcoins.diceroll.sdk.core.utils.payments.models.PaymentState.PaymentError
import com.appcoins.diceroll.sdk.core.utils.payments.models.PaymentState.PaymentIdle
import com.appcoins.diceroll.sdk.core.utils.payments.models.PaymentState.PaymentLoading
import com.appcoins.diceroll.sdk.core.utils.payments.models.PaymentState.PaymentSuccess
import com.appcoins.diceroll.sdk.feature.payments.ui.widgets.ErrorState
import com.appcoins.diceroll.sdk.feature.payments.ui.widgets.LoadingState
import com.appcoins.diceroll.sdk.feature.payments.ui.widgets.PaymentDialog
import com.appcoins.diceroll.sdk.feature.payments.ui.widgets.SuccessState
import com.appcoins.sdk.billing.ResponseCode.DEVELOPER_ERROR

@Composable
fun PaymentScreen(
    itemId: String,
    paymentState: PaymentState,
    onDismiss: () -> Unit
) {
    PaymentDialog(onDismiss) {
        when (paymentState) {
            is PaymentIdle -> Unit
            is PaymentLoading -> LoadingState()
            is PaymentError -> ErrorState(DEVELOPER_ERROR, onDismiss)
            is PaymentSuccess -> SuccessState(itemId, onDismiss)
        }
    }
}

@Preview
@Composable
private fun PreviewStatsContent() {
    DiceRollTheme(darkTheme = false) {
        PaymentScreen("Attempts", PaymentSuccess("attempts"), {})
    }
}
