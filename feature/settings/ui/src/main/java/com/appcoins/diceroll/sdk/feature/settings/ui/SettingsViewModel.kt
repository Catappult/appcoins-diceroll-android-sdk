package com.appcoins.diceroll.sdk.feature.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.sdk.feature.settings.data.model.ThemeConfig
import com.appcoins.diceroll.sdk.feature.settings.data.model.UserPrefs
import com.appcoins.diceroll.sdk.feature.settings.data.repository.UserPrefsDataSource
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsUiState.Loading
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsUiState.Success
import com.appcoins.diceroll.sdk.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.sdk.feature.stats.data.usecases.GetDiceRollsUseCase
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPrefsDataSource: UserPrefsDataSource,
    private val sdkManager: SdkManager,
    private val getDiceRollsUseCase: GetDiceRollsUseCase
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState> =
        getDiceRollsUseCase()
            .zip(userPrefsDataSource.getUserPrefs()) { diceRollList, userPrefs ->
                Pair(diceRollList, userPrefs)
            }.map {
                Success(it.second, it.first)
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = Loading,
            )

    fun updateThemeConfig(themeConfig: ThemeConfig) {
        viewModelScope.launch {
            userPrefsDataSource.saveThemeConfig(themeConfig)
        }
    }

    fun launchAppUpdateDialog(context: Context) {
        sdkManager.launchAppUpdateDialog(context)
    }
}

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    data class Success(
        val userPrefs: UserPrefs,
        val diceRollList: List<DiceRoll>,
    ) : SettingsUiState
}
