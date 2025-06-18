package com.appcoins.diceroll.sdk.feature.roll_game.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.sdk.feature.roll_game.data.streams.DiceSelectionDialogVisibilityStateStream
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetAttemptsUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.GetSubscriptionsPreferencesUseCase
import com.appcoins.diceroll.sdk.feature.roll_game.data.usecases.SaveAttemptsUseCase
import com.appcoins.diceroll.sdk.feature.settings.data.usecases.GetUserUseCase
import com.appcoins.diceroll.sdk.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.sdk.feature.stats.data.usecases.SaveDiceRollUseCase
import com.appcoins.diceroll.sdk.payments.billing.SdkManager
import com.appcoins.diceroll.sdk.payments.data.models.Item
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RollGameViewModel @Inject constructor(
    private val saveDiceRollUseCase: SaveDiceRollUseCase,
    private val saveAttemptsUseCase: SaveAttemptsUseCase,
    private val sdkManager: SdkManager,
    private val getUserUseCase: GetUserUseCase,
    getAttemptsUseCase: GetAttemptsUseCase,
    getSubscriptionsPreferencesUseCase: GetSubscriptionsPreferencesUseCase,
) : ViewModel() {

    internal val uiState: StateFlow<RollGameState> =
        combine(
            getAttemptsUseCase(),
            getSubscriptionsPreferencesUseCase()
        ) { attemptsLeft, subscriptionPrefs ->
            RollGameState.Success(attemptsLeft, subscriptionPrefs)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = RollGameState.Loading,
        )

    internal val sdkConnectionState: StateFlow<Boolean> get() = sdkManager._connectionState

    internal val attemptsPrice: StateFlow<String?> get() = sdkManager._attemptsPrice

    suspend fun saveDiceRoll(diceRoll: DiceRoll) {
        saveDiceRollUseCase(diceRoll).also { saveAttemptsUseCase(diceRoll.attemptsLeft) }
    }

    fun launchBillingSdkFlow(context: Activity, item: Item) {
        sdkManager.startPayment(
            context,
            item.sku,
            item.type,
            getUserUseCase().uuid
        )
    }

    fun openDiceSelectionDialog() {
        CoroutineScope(Dispatchers.IO).launch {
            DiceSelectionDialogVisibilityStateStream.publish(true)
        }
    }
}
