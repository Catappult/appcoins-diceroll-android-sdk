package com.appcoins.diceroll.feature.stats.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.core.utils.R
import com.appcoins.diceroll.core.ui.widgets.LoadingAnimation
import com.appcoins.diceroll.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.feature.stats.ui.utils.result_text_loss
import com.appcoins.diceroll.feature.stats.ui.utils.result_text_win

@Composable
internal fun RollDetailsStatsRoute(
  viewModel: RollGameViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  RollDetailsStatsScreen(uiState = uiState)
}

@Composable
fun RollDetailsStatsScreen(
  uiState: StatsUiState
) {
  when (uiState) {
    StatsUiState.Loading -> {
      LoadingAnimation(stringResource(id = R.string.loading))
    }
    is StatsUiState.Success -> {
      RollDetailsStatsContent(
        diceRollList = uiState.diceRollList,
      )
    }
  }
}

@Composable
fun RollDetailsStatsContent(diceRollList: List<DiceRoll>) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    items(diceRollList.reversed()) { roll ->
      DiceRollItem(roll)
    }
  }
}

@Composable
fun DiceRollItem(roll: DiceRoll) {
  var isExpanded by rememberSaveable { mutableStateOf(false) }
  Card(
    shape = MaterialTheme.shapes.large,
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp)
      .clickable {
        isExpanded = !isExpanded
      },
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        modifier = Modifier
          .padding(start = 16.dp),
        text = "#${roll.id.toString()}",
        style = MaterialTheme.typography.headlineSmall
      )
      Text(
        modifier = Modifier
          .padding(end = 16.dp),
        text = if (roll.rollWin) {
          stringResource(id = R.string.stats_details_win)
        } else {
          stringResource(id = R.string.stats_details_loss)
        },
        style = MaterialTheme.typography.bodyLarge,
        color = if (roll.rollWin) {
          result_text_win
        } else {
          result_text_loss
        }
      )
    }
    AnimatedVisibility(visible = isExpanded) {
      RollDetails(roll)
    }
  }
}

@Composable
fun RollDetails(roll: DiceRoll) {
  Column(
    Modifier
      .padding(16.dp)
  ) {
    DetailsRow(
      rollKey = stringResource(id = R.string.stats_details_guess),
      rollContent = roll.guessNumber.toString()
    )
    DetailsRow(
      rollKey = stringResource(id = R.string.stats_details_result),
      rollContent = roll.resultNumber.toString()
    )
    DetailsRow(
      rollKey = stringResource(id = R.string.stats_details_attempts_left),
      rollContent = roll.attemptsLeft.toString()
    )
  }
}

@Composable
fun DetailsRow(
  rollKey: String,
  rollContent: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = rollKey,
      style = MaterialTheme.typography.bodySmall
    )
    Text(
      text = rollContent,
      style = MaterialTheme.typography.bodyMedium
    )
  }
}

@Preview
@Composable
private fun PreviewRollDetailsStatsContent() {
  DiceRollTheme(darkTheme = false) {
    RollDetailsStatsContent(
      diceRollList = listOf(
        DiceRoll(1, true, 1, 1, 2),
        DiceRoll(2, false, 1, 6, 2),
        DiceRoll(3, false, 1, 4, 2),
      )
    )
  }
}

@Preview
@Composable
private fun PreviewRollDatailsItemContent() {
  DiceRollTheme(darkTheme = false) {
    RollDetails(
      DiceRoll(1, true, 1, 1, 2),
    )
  }
}