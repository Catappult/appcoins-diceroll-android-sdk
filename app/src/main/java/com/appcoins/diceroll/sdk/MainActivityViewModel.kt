package com.appcoins.diceroll.sdk

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.sdk.core.permissions.PermissionsManager
import com.appcoins.diceroll.sdk.core.utils.listen
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetGoldenDiceStatusUseCase
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val userPrefs: UserPrefsDataSource,
    getGoldenDiceStatusUseCase: GetGoldenDiceStatusUseCase,
    val permissionsManager: PermissionsManager
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> =
        combine(
            userPrefs.getUserPrefs(),
            getGoldenDiceStatusUseCase()
        ) { userPrefs, goldenDiceState ->
            MainActivityUiState.Success(userPrefs, goldenDiceState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MainActivityUiState.Loading,
        )

    val paymentState: MutableStateFlow<PaymentState> =
        MutableStateFlow(PaymentIdle)

    private val pendingPaymentStates: MutableList<PaymentState> = mutableListOf()

    fun observePaymentState() {
        CoroutineScope(Dispatchers.IO).launch {
            PurchaseStateStream.listen<PaymentState>().collect {
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

    fun requestPermissions(activity: Activity) {
        permissionsManager.requestPermissions(activity)
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
    data class Success(
        val userPrefs: UserPrefs,
        val goldenDiceStatus: Boolean
    ) : MainActivityUiState
}
