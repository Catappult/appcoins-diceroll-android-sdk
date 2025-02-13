package com.appcoins.diceroll.sdk.feature.store.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.appcoins.diceroll.sdk.feature.settings.data.usecases.GetUserUseCase
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.sdk.billing.SkuDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val sdkManager: SdkManager,
    private val getUserUseCase: GetUserUseCase,
) : ViewModel() {

    internal val uiState: StateFlow<StoreState> =
        MutableStateFlow(StoreState.Loading)

    internal val purchasableItems: List<SkuDetails> get() = sdkManager._purchasableItems

    fun launchBillingSdkFlow(context: Context, item: Item) {
        sdkManager.startPayment(
            context,
            item.sku,
            item.type,
            getUserUseCase().uuid
        )
    }
}
