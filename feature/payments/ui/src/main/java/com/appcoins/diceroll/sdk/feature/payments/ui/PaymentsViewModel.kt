package com.appcoins.diceroll.sdk.feature.payments.ui

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.sdk.core.navigation.destinations.DestinationArgs
import com.appcoins.diceroll.sdk.core.utils.PurchaseResultStream
import com.appcoins.diceroll.sdk.core.utils.listen
import com.appcoins.diceroll.sdk.feature.payments.ui.result.PaymentsResultUiState
import com.appcoins.diceroll.sdk.feature.roll_game.data.DEFAULT_ATTEMPTS_NUMBER
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.ResetAttemptsUseCase
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager
import com.appcoins.sdk.billing.ResponseCode
import com.appcoins.sdk.billing.listeners.PurchaseResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentsViewModel @Inject constructor(
  private val resetAttemptsUseCase: ResetAttemptsUseCase,
  val savedStateHandle: SavedStateHandle,
  private val sdkManager: SdkManager,
) : ViewModel() {

  private val itemId = savedStateHandle.get<String>(DestinationArgs.ITEM_ID)
  private val attempts = savedStateHandle.get<String>(DestinationArgs.ATTEMPTS_LEFT)

  private val _paymentProcessState =
    MutableStateFlow<PaymentProcessUiState>(PaymentProcessUiState.Loading)
  internal val paymentProcessState: StateFlow<PaymentProcessUiState> get() = _paymentProcessState

  private val _paymentResultState =
    MutableStateFlow<PaymentsResultUiState>(PaymentsResultUiState.Initialized)
  internal val paymentResultState: StateFlow<PaymentsResultUiState> get() = _paymentResultState

  init {
    when {
      itemId == null || attempts == null -> {
        _paymentProcessState.value = PaymentProcessUiState.Error
      }

      attempts == DEFAULT_ATTEMPTS_NUMBER.toString() -> {
        _paymentProcessState.value = PaymentProcessUiState.NotAvailable
      }

      else -> {
        _paymentProcessState.value = PaymentProcessUiState.StartPayment
      }
    }
  }

  suspend fun resetAttemptsLeft() {
    resetAttemptsUseCase()
  }

  fun launchBillingSdkFlow(context: Context) {
    _paymentProcessState.value = PaymentProcessUiState.PaymentInProgress
    _paymentResultState.value = PaymentsResultUiState.Loading
    observeSdkResult()
    sdkManager.startPayment(context, itemId.toString(), """{"user":"user12345","purchaseNo":1}""")
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
}
