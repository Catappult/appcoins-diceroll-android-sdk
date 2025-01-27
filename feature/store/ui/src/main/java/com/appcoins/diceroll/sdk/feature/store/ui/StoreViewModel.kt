package com.appcoins.diceroll.sdk.feature.store.ui

import androidx.lifecycle.ViewModel
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager
import com.appcoins.sdk.billing.SkuDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val sdkManager: SdkManager,
) : ViewModel() {

    internal val uiState: StateFlow<StoreState> =
        MutableStateFlow(StoreState.Loading)

    internal val purchasableItems: List<SkuDetails> get() = sdkManager._purchasableItems
}