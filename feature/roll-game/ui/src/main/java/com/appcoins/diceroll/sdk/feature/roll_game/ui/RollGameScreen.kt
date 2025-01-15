package com.appcoins.diceroll.sdk.feature.roll_game.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                attemptsLeft = DEFAULT_ATTEMPTS_NUMBER,
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
    var diceValue by rememberSaveable { mutableIntStateOf(-1) }
    var resultText by rememberSaveable { mutableStateOf(0) }
    var betNumber by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        val goldDiceSubscriptionActiveState by viewModel.goldDiceSubscriptionActive.collectAsStateWithLifecycle()
        GameDice(attemptsLeft, diceValue, resultText, goldDiceSubscriptionActiveState)
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
                                resultText = 1
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
                                resultText = -1
                                runBlocking {
                                    onSaveDiceRoll(
                                        DiceRoll(
                                            id = null,
                                            rollWin = diceValue == betNumber.toInt(),
                                            guessNumber = betNumber.toInt(),
                                            resultNumber = diceValue,
                                            attemptsLeft = 3

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
            onClick = { onBuyClick(Item.Attempts) },
            enabled = isBuyAttemptsButtonReady
        ) {
            Text(
                text =
                if (isBuyAttemptsButtonReady) {
                    stringResource(id = R.string.roll_game_attempts_buy_button) +
                        (attemptsPrice?.let { " $it" } ?: "")
                } else {
                    stringResource(id = R.string.payments_sdk_initializing)
                },
                textAlign = TextAlign.Center
            )
        }

        if (isBuyGoldDiceButtonReady) {
            Button(onClick = { onBuyClick(Item.GoldDice) }) {
                Text(
                    text = stringResource(id = R.string.roll_game_golden_dice_buy_button) +
                        (goldDicePrice?.let { " $it" } ?: ""),
                    textAlign = TextAlign.Center
                )
            }
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
private fun GameDice(
    attemptsLeft: Int,
    diceValue: Int,
    result: Int,
    goldDiceSubscriptionActive: Boolean?
) {
    val baseDice =
        if (goldDiceSubscriptionActive == true) {
            GameR.drawable.ic_base_golden_dice
        } else {
            GameR.drawable.ic_base_dice
        }
    val diceImages = if (goldDiceSubscriptionActive == true) {
        listOf(
            GameR.drawable.ic_dice_1_golden,
            GameR.drawable.ic_dice_2_golden,
            GameR.drawable.ic_dice_3_golden,
            GameR.drawable.ic_dice_4_golden,
            GameR.drawable.ic_dice_5_golden,
            GameR.drawable.ic_dice_6_golden,
        )
    } else {
        listOf(
            GameR.drawable.ic_dice_1,
            GameR.drawable.ic_dice_2,
            GameR.drawable.ic_dice_3,
            GameR.drawable.ic_dice_4,
            GameR.drawable.ic_dice_5,
            GameR.drawable.ic_dice_6,
        )
    }
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 32.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                    .height(300.dp)
                    .background(Color.Blue)
                    .fillMaxWidth(),
            ) {
                if (result == 1 || result == -1) {
                    Image(
                        imageVector = ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_radiant_shadows),
                        contentDescription = "Diceroll Winning shadows",
                        Modifier
                            .height(300.dp)
                            .fillMaxWidth()
                    )
                }
                Column {
                    Image(
                        modifier = Modifier
                            .padding(0.dp, 24.dp, 0.dp, 0.dp)
                            .align(Alignment.CenterHorizontally),
                        imageVector = ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_green_sdk_title),
                        contentDescription = "Title"
                    )
                    Box(
                        modifier = Modifier
                            .clip(shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally),
                    ) {

                        if (result == 0 || result == -1) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(0.dp, 40.dp, 0.dp, 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Crossfade(
                                    targetState = diceValue,
                                    animationSpec = tween(1000),
                                    label = "Dice roll crossfade"
                                ) { targetDiceValue ->
                                    val imageRes =
                                        if (targetDiceValue > 0) {
                                            diceImages[targetDiceValue - 1]
                                        } else {
                                            baseDice
                                        }
                                    DiceImage(imageRes = imageRes)
                                }
                            }
                        }

                        if (result == 1) {
                            Image(
                                modifier = Modifier
                                    .padding(0.dp, 65.dp, 0.dp, 0.dp),
                                imageVector = ImageVector.vectorResource(com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_success_dice_1),
                                contentDescription = "Success Dice 1"
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Image(
                                    modifier = Modifier
                                        .padding(0.dp, 150.dp, 0.dp, 0.dp),
                                    imageVector = ImageVector.vectorResource(com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_success_dice_2),
                                    contentDescription = "Success Dice 2"
                                )
                            }
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                imageVector = ImageVector.vectorResource(com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_success_image),
                                contentDescription = "Success Image"
                            )
                        }

                        if (result == -1) {
                            Image(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(100.dp, 0.dp, 0.dp, 60.dp)
                                    .align(Alignment.Center),
                                imageVector = ImageVector.vectorResource(com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_failure),
                                contentDescription = "Failure Image"
                            )
                        }
                    }
                }
            }
        }
        if (result == 0) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(0.dp, 0.dp, 0.dp, 12.dp)
                    .shadow(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(100.dp))
                        .background(Color.Blue),
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 12.dp),
                        text = "attempts left: $attemptsLeft",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        if (result == -1) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(0.dp, 0.dp, 0.dp, 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(100.dp))
                        .background(Color.Red)
                        .shadow(18.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(12.dp, 6.dp, 12.dp, 0.dp),
                        text = "YOU FAIL!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally)
                            .padding(12.dp, 0.dp, 12.dp, 12.dp),
                        text = "attempts left: $attemptsLeft",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
        if (result == 1) {
            Column(
                modifier = Modifier
                    .align(alignment = Alignment.BottomCenter)
                    .padding(0.dp, 0.dp, 0.dp, 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(100.dp))
                        .background(Color.White),
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 12.dp),
                        text = "YOU WON!",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
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
fun Preview() {
    GameDice(
        attemptsLeft = 3,
        diceValue = 0,
        result = 0,
        goldDiceSubscriptionActive = false,
    )
}
