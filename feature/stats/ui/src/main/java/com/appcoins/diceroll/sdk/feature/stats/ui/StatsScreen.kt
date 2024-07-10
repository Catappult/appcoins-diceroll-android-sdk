package com.appcoins.diceroll.sdk.feature.stats.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.sdk.core.utils.R
import com.appcoins.diceroll.sdk.core.utils.extensions.toPercent
import com.appcoins.diceroll.sdk.core.ui.widgets.LoadingAnimation
import com.appcoins.diceroll.sdk.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.sdk.feature.stats.ui.utils.*
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer

@Composable
internal fun StatsRoute(
  onDetailsClick: (List<DiceRoll>) -> Unit,
  viewModel: RollGameViewModel = hiltViewModel(),
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  StatsScreen(uiState = uiState, onDetailsClick)
}

@Composable
fun StatsScreen(
  uiState: StatsUiState,
  onDetailsClick: (List<DiceRoll>) -> Unit,
) {
  when (uiState) {
    StatsUiState.Loading -> {
      LoadingAnimation(stringResource(id = R.string.loading))
    }
    is StatsUiState.Success -> {
      StatsContent(
        diceRollList = uiState.diceRollList,
        onDetailsClick = onDetailsClick
      )
    }
  }
}

@Composable
fun StatsContent(diceRollList: List<DiceRoll>, onDetailsClick: (List<DiceRoll>) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState())
  ) {
    StatsHeaderContent(diceRollList, onDetailsClick)
    if (diceRollList.isNotEmpty()) {
      StatsDonutCharts(diceRollList)
//      StatsBarChart(diceRollList)
    }
  }
}

@Composable
fun StatsHeaderContent(diceRollList: List<DiceRoll>, onDetailsClick: (List<DiceRoll>) -> Unit) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onDetailsClick(diceRollList) },
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
    ) {
      StatsItem(
        statValue = diceRollList.size.toString(),
        statTitle = stringResource(id = R.string.stats_games_played)
      )
    }
    Row(
      modifier = Modifier
        .fillMaxWidth(),
    ) {
      Card(
        modifier = Modifier
          .padding(4.dp)
          .weight(1f)
      ) {
        StatsItem(
          statValue = diceRollList.filter { it.rollWin }.size.toString(),
          statTitle = stringResource(id = R.string.stats_games_won)
        )
      }
      Card(
        modifier = Modifier
          .padding(4.dp)
          .weight(1f)
      ) {
        StatsItem(
          statValue = winPercentage(diceRollList),
          statTitle = stringResource(id = R.string.stats_win_percentage)
        )
      }
    }
  }
}

@Composable
fun StatsItem(
  statValue: String,
  statTitle: String
) {
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = Alignment.Center
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = statValue,
        style = MaterialTheme.typography.headlineLarge
      )
      Text(
        text = statTitle,
        style = MaterialTheme.typography.bodyLarge
      )
    }
  }
}

fun createPieChartData(
  percentageDistribution: List<Float>,
  chartTones: List<Color>
): PieChartData {
  return PieChartData(
    percentageDistribution.zip(chartTones)
      .map { (percentage, chartTone) -> PieChartData.Slice(percentage, chartTone) }
  )
}

@Composable
fun StatsDonutCharts(diceRollList: List<DiceRoll>) {
  val resultPercentageDistribution =
    diceRollList.groupingBy { it.resultNumber }.eachCount().map { it.value.toFloat() }.toPercent()
  val guessPercentageDistribution =
    diceRollList.groupingBy { it.guessNumber }.eachCount().map { it.value.toFloat() }.toPercent()
  val chartTones = listOf(
    chart_tone1, chart_tone2, chart_tone3, chart_tone4, chart_tone5, chart_tone6
  )
  val resultPieChartData by remember {
    mutableStateOf(
      createPieChartData(
        resultPercentageDistribution,
        chartTones
      )
    )
  }

  val guessPieChartData by remember {
    mutableStateOf(
      createPieChartData(
        guessPercentageDistribution,
        chartTones
      )
    )
  }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .height(400.dp)
  ) {
    Card(
      modifier = Modifier
        .weight(1f)
        .padding(4.dp)
    ) {
      Text(
        modifier = Modifier.padding(8.dp),
        text = stringResource(id = R.string.stats_result_distribution),
        style = MaterialTheme.typography.titleSmall
      )
      PieChart(
        pieChartData = resultPieChartData,
        sliceDrawer = SimpleSliceDrawer(
          sliceThickness = 50f
        )
      )
    }
    Card(
      modifier = Modifier
        .weight(1f)
        .padding(4.dp)
    ) {
      Text(
        modifier = Modifier.padding(8.dp),
        text = stringResource(id = R.string.stats_guess_distribution),
        style = MaterialTheme.typography.titleSmall
      )
      PieChart(
        pieChartData = guessPieChartData,
        sliceDrawer = SimpleSliceDrawer(
          sliceThickness = 50f
        )
      )
    }
  }
}

private fun winPercentage(diceRollList: List<DiceRoll>): String {
  val winCount = diceRollList.filter { it.rollWin }.size
  val totalCount = diceRollList.size
  return if (totalCount == 0) {
    "0"
  } else {
    (winCount * 100 / totalCount).toString()
  }
}

@Preview
@Composable
private fun PreviewStatsContent() {
  DiceRollTheme(darkTheme = false) {
    StatsContent(
      diceRollList = listOf(
        DiceRoll(1, true, 1, 1, 2),
        DiceRoll(2, false, 1, 6, 2),
        DiceRoll(3, false, 1, 4, 2),
      ),
      onDetailsClick = { }
    )
  }
}