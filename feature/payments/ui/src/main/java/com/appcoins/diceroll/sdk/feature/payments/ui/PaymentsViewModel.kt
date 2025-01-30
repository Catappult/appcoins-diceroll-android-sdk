package com.appcoins.diceroll.sdk.feature.payments.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.sdk.core.navigation.destinations.DestinationArgs
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.utils.PurchaseResultStream
import com.appcoins.diceroll.sdk.core.utils.listen
import com.appcoins.diceroll.sdk.feature.payments.ui.result.PaymentsResultUiState
import com.appcoins.diceroll.sdk.feature.roll_game.data.DEFAULT_ATTEMPTS_NUMBER
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetAttemptsUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.ResetAttemptsUseCase
import com.appcoins.diceroll.sdk.feature.settings.data.usecases.GetUserUseCase
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager
import com.appcoins.sdk.billing.ResponseCode
import com.appcoins.sdk.billing.listeners.PurchaseResponse
import com.appcoins.sdk.billing.types.SkuType
import com.appcoins.sdk.billing.types.SkuType.inapp
import com.appcoins.sdk.billing.types.SkuType.subs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
    private val resetAttemptsUseCase: ResetAttemptsUseCase,
    val savedStateHandle: SavedStateHandle,
    private val sdkManager: SdkManager,
    private val getAttemptsUseCase: GetAttemptsUseCase,
    private val getUserUseCase: GetUserUseCase
) : ViewModel() {

    private val itemId = savedStateHandle.get<String>(DestinationArgs.ITEM_ID)

    private val _paymentProcessState =
        MutableStateFlow<PaymentProcessUiState>(PaymentProcessUiState.Loading)
    internal val paymentProcessState: StateFlow<PaymentProcessUiState> get() = _paymentProcessState

    private val _paymentResultState =
        MutableStateFlow<PaymentsResultUiState>(PaymentsResultUiState.Initialized)
    internal val paymentResultState: StateFlow<PaymentsResultUiState> get() = _paymentResultState

    init {
        if (itemId != null) {
            when (itemId) {
                Item.ATTEMPTS_SKU -> setupAttemptsPaymentProcessState()
                Item.GOLD_DICE_SKU -> setupGoldDicePaymentProcessState()
            }
        } else {
            _paymentProcessState.value = PaymentProcessUiState.Error
        }
    }

    fun getNotAvailableMessageForItem(itemId: String): Int =
        when (itemId) {
            Item.GOLD_DICE_SKU -> R.string.payments_golden_dice_error_body
            else -> R.string.payments_attempts_error_body
        }

    fun handlePaymentFinished(itemId: String) {
        when (itemId) {
            Item.ATTEMPTS_SKU -> resetAttemptsLeft()
        }
    }

    fun launchBillingSdkFlow(context: Context) {
        _paymentProcessState.value = PaymentProcessUiState.PaymentInProgress
        _paymentResultState.value = PaymentsResultUiState.Loading
        observeSdkResult()
        sdkManager.startPayment(
            context,
            itemId.toString(),
            getItemType(itemId),
            getUserUseCase().uuid
        )
    }

    private fun setupAttemptsPaymentProcessState() {
        val attempts = runBlocking(Dispatchers.IO) { getAttemptsUseCase().firstOrNull() }
        when (attempts) {
            null -> {
                _paymentProcessState.value = PaymentProcessUiState.Error
            }

            DEFAULT_ATTEMPTS_NUMBER -> {
                _paymentProcessState.value = PaymentProcessUiState.NotAvailable
            }

            else -> {
                _paymentProcessState.value = PaymentProcessUiState.StartPayment
            }
        }
    }

    private fun setupGoldDicePaymentProcessState() {
        if (sdkManager._goldDiceSubscriptionActive.value == true) {
            _paymentProcessState.value = PaymentProcessUiState.NotAvailable
        } else {
            _paymentProcessState.value = PaymentProcessUiState.StartPayment
        }
    }

    private fun observeSdkResult() {
        viewModelScope.launch {
            PurchaseResultStream.listen<PurchaseResponse>().collect {
                val paymentState = when (it.responseCode) {
                    ResponseCode.OK.value -> PaymentsResultUiState.Success
                    ResponseCode.USER_CANCELED.value -> PaymentsResultUiState.UserCanceled
                    else -> PaymentsResultUiState.Failed
                }
                _paymentResultState.value = paymentState
            }
        }
    }

    private fun getItemType(itemId: String?): SkuType =
        when (itemId) {
            Item.GOLD_DICE_SKU -> subs
            else -> inapp
        }

    private fun resetAttemptsLeft() {
        CoroutineScope(Dispatchers.IO).launch {
            resetAttemptsUseCase()
        }
    }
}
