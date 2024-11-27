package com.appcoins.diceroll.sdk.feature.roll_game.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.sdk.core.utils.R
import com.appcoins.diceroll.sdk.feature.payments.ui.Item
import com.appcoins.diceroll.sdk.feature.roll_game.data.DEFAULT_ATTEMPTS_NUMBER
import com.appcoins.diceroll.sdk.feature.stats.data.model.DiceRoll
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import com.appcoins.diceroll.sdk.feature.roll_game.ui.R as GameR

@Composable
internal fun RollGameRoute(
  onBuyClick: (Item) -> Unit,
  viewModel: RollGameViewModel = hiltViewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  RollGameScreen(
    uiState,
    onBuyClick,
    viewModel::saveDiceRoll,
  )
}

@Composable
fun RollGameScreen(
  uiState: RollGameState,
  onBuyClick: (Item) -> Unit,
  onSaveDiceRoll: suspend (diceRoll: DiceRoll) -> Unit,
) {
  when (uiState) {
    is RollGameState.Loading -> {}
    is RollGameState.Error -> {}
    is RollGameState.Success -> {
      RollGameContent(
        attemptsLeft = uiState.attemptsLeft ?: DEFAULT_ATTEMPTS_NUMBER,
        onSaveDiceRoll = onSaveDiceRoll,
        onBuyClick = onBuyClick,
      )
    }
  }
}

@Composable
fun RollGameContent(
  attemptsLeft: Int,
  onSaveDiceRoll: suspend (diceRoll: DiceRoll) -> Unit,
  onBuyClick: (Item) -> Unit,
  viewModel: RollGameViewModel = hiltViewModel()
) {
  var diceValue by rememberSaveable { mutableIntStateOf(1) }
  var resultText by rememberSaveable { mutableStateOf("") }
  var betNumber by rememberSaveable { mutableStateOf("") }
  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    GameDice(diceValue, resultText, viewModel)
    Column(
      Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Text(
        text = stringResource(id = R.string.roll_game_info),
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
      )
      Row {
        Card(
          modifier = Modifier
            .padding(8.dp)
            .weight(2f),
        ) {
          TextField(
            value = betNumber,
            onValueChange = { newValue -> betNumber = convertedBetNumber(newValue) },
            label = { Text(stringResource(id = R.string.roll_game_guess_prompt)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          )
        }
        Button(
          onClick = {
            if (attemptsLeft > 0 && betNumber.isNotEmpty()) {
              val bet = betNumber
              diceValue = Random.nextInt(1, 7)
              if (bet.toInt() == diceValue) {
                resultText = "Correct!"
                runBlocking {
                  onSaveDiceRoll(
                    DiceRoll(
                      id = null,
                      rollWin = diceValue == betNumber.toInt(),
                      guessNumber = betNumber.toInt(),
                      resultNumber = diceValue,
                      attemptsLeft = DEFAULT_ATTEMPTS_NUMBER
                    )
                  )
                }
              } else {
                resultText = "Incorrect!"
                runBlocking {
                  onSaveDiceRoll(
                    DiceRoll(
                      id = null,
                      rollWin = diceValue == betNumber.toInt(),
                      guessNumber = betNumber.toInt(),
                      resultNumber = diceValue,
                      attemptsLeft = attemptsLeft - 1

                    )
                  )
                }
              }
            }
            betNumber = ""
          },
          Modifier
            .weight(1f)
            .align(Alignment.CenterVertically),
          enabled = attemptsLeft > 0 && betNumber.isNotEmpty()
        ) {
          Text(text = stringResource(id = R.string.roll_game_button))
        }
      }
      Text(
        text = stringResource(id = R.string.roll_game_attempts_left) + " $attemptsLeft",
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        textAlign = TextAlign.Center
      )
    }

    val sdkSetupState by viewModel.sdkConnectionState.collectAsStateWithLifecycle()

    val attemptsPrice by viewModel.attemptsPrice.collectAsStateWithLifecycle()
    val isBuyAttemptsButtonReady = sdkSetupState && attemptsPrice != null

    val goldDicePrice by viewModel.goldDicePrice.collectAsStateWithLifecycle()
    val isBuyGoldDiceButtonReady = sdkSetupState && goldDicePrice != null

    Button(
      onClick = { onBuyClick(Item.Attempts(attemptsLeft)) },
      enabled = isBuyAttemptsButtonReady
    ) {
      Text(
        text =
        if (isBuyAttemptsButtonReady) {
          stringResource(id = R.string.roll_game_buy_button) +
              (attemptsPrice?.let { " $it" } ?: "")
        } else {
          stringResource(id = R.string.payments_sdk_initializing)
        },
        textAlign = TextAlign.Center
      )
    }

    Button(
      onClick = { onBuyClick(Item.GoldDice) },
      enabled = isBuyGoldDiceButtonReady
    ) {
      Text(
        text =
        if (isBuyAttemptsButtonReady) {
          "Subscribe Golden Dice for" +
              (goldDicePrice?.let { " $it" } ?: "")
        } else {
          stringResource(id = R.string.payments_sdk_initializing)
        },
        textAlign = TextAlign.Center
      )
    }
  }
}

private fun convertedBetNumber(betNumber: String): String {
  return if (betNumber.toIntOrNull() != null && betNumber.toInt() in 1..6) {
    betNumber
  } else {
    ""
  }
}

@Composable
private fun GameDice(diceValue: Int, resultText: String, viewModel: RollGameViewModel) {
    val goldDiceSubscriptionActive by viewModel.goldDiceSubscriptionActive.collectAsStateWithLifecycle()
  val diceImages = if(goldDiceSubscriptionActive == true) {
      listOf(
          GameR.drawable.golden_dice_six_faces_one,
          GameR.drawable.golden_dice_six_faces_two,
          GameR.drawable.golden_dice_six_faces_three,
          GameR.drawable.golden_dice_six_faces_four,
          GameR.drawable.golden_dice_six_faces_five,
          GameR.drawable.golden_dice_six_faces_six,
      )
  } else {
      listOf(
          GameR.drawable.dice_six_faces_one,
          GameR.drawable.dice_six_faces_two,
          GameR.drawable.dice_six_faces_three,
          GameR.drawable.dice_six_faces_four,
          GameR.drawable.dice_six_faces_five,
          GameR.drawable.dice_six_faces_six,
      )
  }
  Box(
    modifier = Modifier.size(200.dp),
    contentAlignment = Alignment.Center
  ) {
    Crossfade(
      targetState = diceValue,
      animationSpec = tween(1000),
      label = "Dice roll crossfade"
    ) { targetDiceValue ->
      DiceImage(imageRes = diceImages[targetDiceValue - 1])
    }
    if (resultText.isNotEmpty()) {
      Text(
        text = resultText,
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.onPrimary
      )
    }
  }
}

@Composable
fun DiceImage(@DrawableRes imageRes: Int) {
  val image = painterResource(imageRes)
  Image(
    painter = image,
    contentDescription = "Dice Image",
  )
}

@Preview
@Composable
fun PreviewDiceRollScreen() {
  DiceRollTheme(darkTheme = true) {
    RollGameContent(
      attemptsLeft = 3,
      onSaveDiceRoll = {},
      onBuyClick = {},
    )
  }
}
