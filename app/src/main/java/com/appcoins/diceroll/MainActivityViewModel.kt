package com.appcoins.diceroll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.feature.settings.data.model.UserPrefs
import com.appcoins.diceroll.feature.settings.data.repository.UserPrefsDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  userPrefs: UserPrefsDataSource,
) : ViewModel() {
  val uiState: StateFlow<MainActivityUiState> = userPrefs.getUserPrefs().map {
    MainActivityUiState.Success(it)
  }.stateIn(
    scope = viewModelScope,
    initialValue = MainActivityUiState.Loading,
    started = SharingStarted.WhileSubscribed(5_000),
  )
}

sealed interface MainActivityUiState {
  data object Loading : MainActivityUiState
  data class Success(val userPrefs: UserPrefs) : MainActivityUiState
}
