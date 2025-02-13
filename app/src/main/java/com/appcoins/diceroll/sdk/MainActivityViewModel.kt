package com.appcoins.diceroll.sdk

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.sdk.core.utils.listen
import com.appcoins.diceroll.sdk.feature.settings.data.model.UserPrefs
import com.appcoins.diceroll.sdk.feature.settings.data.repository.UserPrefsDataSource
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentIdle
import com.appcoins.diceroll.sdk.payments.data.models.PaymentState.PaymentLoading
import com.appcoins.diceroll.sdk.payments.data.streams.PurchaseStateStream
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val userPrefs: UserPrefsDataSource,
) : ViewModel() {
    val uiState: StateFlow<MainActivityUiState> = userPrefs.getUserPrefs().map {
        MainActivityUiState.Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = MainActivityUiState.Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )

    val paymentState: MutableStateFlow<PaymentState> =
        MutableStateFlow(PaymentIdle)

    private val pendingPaymentStates: MutableList<PaymentState> = mutableListOf()

    fun observePaymentState() {
        CoroutineScope(Dispatchers.IO).launch {
            PurchaseStateStream.listen<PaymentState>().collect {
                Log.i("SdkManager", "observePaymentState: $it ")
                pendingPaymentStates.add(it)
                if (paymentState.value == PaymentIdle || paymentState.value == PaymentLoading) {
                    popNewPaymentState()
                }
            }
        }
    }

    fun onPaymentDialogDismissed() {
        paymentState.value = PaymentIdle
        popNewPaymentState()
    }

    private fun popNewPaymentState() {
        if (pendingPaymentStates.size > 0) {
            paymentState.value = pendingPaymentStates[0]
            pendingPaymentStates.removeAt(0)
        }
    }
}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userPrefs: UserPrefs) : MainActivityUiState
}
