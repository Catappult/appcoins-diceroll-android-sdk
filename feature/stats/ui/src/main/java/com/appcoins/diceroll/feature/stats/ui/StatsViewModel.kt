package com.appcoins.diceroll.feature.stats.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appcoins.diceroll.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.feature.stats.data.usecases.GetDiceRollsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class RollGameViewModel @Inject constructor(
  getDiceRollsUseCase: GetDiceRollsUseCase
) : ViewModel() {

  val uiState: StateFlow<StatsUiState> =
    getDiceRollsUseCase().map { diceRollList ->
      StatsUiState.Success(
        diceRollList = diceRollList
      )
    }
      .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = StatsUiState.Loading,
      )
}

sealed interface StatsUiState {
  object Loading : StatsUiState
  data class Success(
    val diceRollList: List<DiceRoll>,
  ) : StatsUiState
}
