package com.appcoins.diceroll.sdk.feature.store.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor() : ViewModel() {

    internal val uiState: StateFlow<StoreState> =
        MutableStateFlow(StoreState.Loading)
}