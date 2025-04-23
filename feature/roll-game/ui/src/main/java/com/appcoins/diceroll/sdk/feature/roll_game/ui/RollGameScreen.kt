package com.appcoins.diceroll.sdk.feature.roll_game.ui

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.sdk.core.utils.tuples.Quadruple
import com.appcoins.diceroll.sdk.feature.roll_game.data.DEFAULT_ATTEMPTS_NUMBER
import com.appcoins.diceroll.sdk.feature.stats.data.model.DiceRoll
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.Item.Attempts
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import com.appcoins.diceroll.sdk.feature.roll_game.ui.R as GameR

@Composable
internal fun RollGameRoute(
    viewModel: RollGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    RollGameScreen(
        uiState,
        viewModel::launchBillingSdkFlow,
        viewModel::saveDiceRoll,
    )
}

@Composable
fun RollGameScreen(
    uiState: RollGameState,
    onBuyClick: (Activity, Item) -> Unit,
    onSaveDiceRoll: suspend (diceRoll: DiceRoll) -> Unit,
    viewModel: RollGameViewModel = hiltViewModel(),
) {
    when (uiState) {
        is RollGameState.Loading -> {}
        is RollGameState.Error -> {}
        is RollGameState.Success -> {
            val sdkSetupState by viewModel.sdkConnectionState.collectAsStateWithLifecycle()
            val attemptsPrice by viewModel.attemptsPrice.collectAsStateWithLifecycle()

            RollGameContent(
                attemptsLeft = uiState.attemptsLeft ?: DEFAULT_ATTEMPTS_NUMBER,
                goldenDiceActive = uiState.goldenDiceStatus,
                onSaveDiceRoll = onSaveDiceRoll,
                onBuyClick = onBuyClick,
                sdkSetupState,
                attemptsPrice,
            )
        }
    }
}

@Composable
fun RollGameContent(
    attemptsLeft: Int,
    goldenDiceActive: Boolean,
    onSaveDiceRoll: suspend (diceRoll: DiceRoll) -> Unit,
    onBuyClick: (Activity, Item) -> Unit,
    sdkSetupState: Boolean,
    attemptsPrice: String?
) {
    var diceValue by rememberSaveable { mutableIntStateOf(-1) }
    var resultValue by rememberSaveable { mutableIntStateOf(0) }
    var betDice by rememberSaveable { mutableIntStateOf(0) }
    val context = LocalContext.current as Activity
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameDice(attemptsLeft, diceValue, resultValue, goldenDiceActive)
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.roll_game_info),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
            )

            if (resultValue == 1) {
                Button(
                    onClick = {
                        resultValue = 0
                    },
                    Modifier.fillMaxWidth(),
                ) {
                    Text(text = stringResource(id = R.string.roll_game_again_button))
                }
            } else {
                DicesRow(betDice) { diceClicked -> betDice = diceClicked }
                Button(
                    onClick = {
                        if (attemptsLeft > 0 && betDice != 0) {
                            diceValue = Random.nextInt(1, 7)
                            if (betDice == diceValue) {
                                resultValue = 1
                                runBlocking {
                                    onSaveDiceRoll(
                                        DiceRoll(
                                            id = null,
                                            rollWin = diceValue == betDice,
                                            guessNumber = betDice,
                                            resultNumber = diceValue,
                                            attemptsLeft = attemptsLeft + DEFAULT_ATTEMPTS_NUMBER
                                        )
                                    )
                                }
                            } else {
                                resultValue = -1
                                runBlocking {
                                    onSaveDiceRoll(
                                        DiceRoll(
                                            id = null,
                                            rollWin = diceValue == betDice,
                                            guessNumber = betDice,
                                            resultNumber = diceValue,
                                            attemptsLeft = attemptsLeft - 1
                                        )
                                    )
                                }
                            }
                        }
                        betDice = 0
                    },
                    Modifier.fillMaxWidth(),
                    enabled = attemptsLeft > 0 && betDice != 0
                ) {
                    Text(
                        text = stringResource(id = R.string.roll_game_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }

        val isBuyAttemptsButtonReady = sdkSetupState && attemptsPrice != null

        Text(
            modifier =
            if (isBuyAttemptsButtonReady) {
                Modifier.clickable {
                    onBuyClick(context, Attempts)
                }
            } else {
                Modifier
            }.fillMaxWidth(),
            text = if (isBuyAttemptsButtonReady) {
                stringResource(id = R.string.roll_game_attempts_buy_button) +
                    (attemptsPrice?.let { " $it" } ?: "")
            } else {
                stringResource(id = R.string.payments_sdk_initializing)
            },
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            color = if (isBuyAttemptsButtonReady) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onPrimary
            },
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DicesRow(betDice: Int, onDiceClicked: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .weight(
                    if (betDice == 1) {
                        1.2f
                    } else {
                        1f
                    }
                )
                .padding(4.dp)
                .clickable { onDiceClicked(1) },
            imageVector = if (betDice == 1) {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_1_selected)
            } else {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_1)
            },
            contentDescription = "Title",
        )
        Image(
            modifier = Modifier
                .weight(
                    if (betDice == 2) {
                        1.2f
                    } else {
                        1f
                    }
                )
                .weight(1f)
                .padding(4.dp)
                .clickable {
                    onDiceClicked(2)
                },
            imageVector = if (betDice == 2) {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_2_selected)
            } else {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_2)
            },
            contentDescription = "Title"
        )
        Image(
            modifier = Modifier
                .weight(
                    if (betDice == 3) {
                        1.2f
                    } else {
                        1f
                    }
                )
                .weight(1f)
                .padding(4.dp)
                .clickable { onDiceClicked(3) },
            imageVector = if (betDice == 3) {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_3_selected)
            } else {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_3)
            },
            contentDescription = "Title"
        )
        Image(
            modifier = Modifier
                .weight(
                    if (betDice == 4) {
                        1.2f
                    } else {
                        1f
                    }
                )
                .weight(1f)
                .padding(4.dp)
                .clickable { onDiceClicked(4) },
            imageVector = if (betDice == 4) {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_4_selected)
            } else {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_4)
            },
            contentDescription = "Title"
        )
        Image(
            modifier = Modifier
                .weight(
                    if (betDice == 5) {
                        1.2f
                    } else {
                        1f
                    }
                )
                .weight(1f)
                .padding(4.dp)
                .clickable { onDiceClicked(5) },
            imageVector = if (betDice == 5) {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_5_selected)
            } else {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_5)
            },
            contentDescription = "Title"
        )
        Image(
            modifier = Modifier
                .weight(
                    if (betDice == 6) {
                        1.2f
                    } else {
                        1f
                    }
                )
                .weight(1f)
                .padding(4.dp)
                .clickable { onDiceClicked(6) },
            imageVector = if (betDice == 6) {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_6_selected)
            } else {
                ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_dice_6)
            },
            contentDescription = "Title"
        )
    }
}

@Composable
private fun GameDice(
    attemptsLeft: Int,
    diceValue: Int,
    result: Int,
    goldenDiceActive: Boolean
) {
    val (baseDice, diceImages, radiantImage, titleImage) =
        getGameResources(goldenDiceActive)

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 0.dp, 0.dp, 32.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                    .height(300.dp)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .fillMaxWidth(),
            ) {
                if (result == 1 || result == -1) {
                    Image(
                        imageVector = radiantImage,
                        contentDescription = "Diceroll Winning shadows",
                        Modifier.fillMaxWidth()
                    )
                }
                Column {
                    Image(
                        modifier = Modifier
                            .padding(0.dp, 24.dp, 0.dp, 0.dp)
                            .align(Alignment.CenterHorizontally),
                        imageVector = titleImage,
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
                    }
                }
            }
        }
        if (result == 0) {
            GameReady(attemptsLeft)
        }
        if (result == -1) {
            GameLost(attemptsLeft)
        }
        if (result == 1) {
            GameWon()
        }
    }
}

@Composable
fun BoxScope.GameReady(attemptsLeft: Int) {
    Column(
        modifier = Modifier
            .align(alignment = Alignment.BottomCenter)
            .padding(0.dp, 0.dp, 0.dp, 12.dp)
    ) {
        Box(
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(100))
                .clip(shape = RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.tertiary),
        ) {
            Text(
                modifier = Modifier.padding(24.dp, 12.dp, 24.dp, 12.dp),
                text = stringResource(R.string.roll_game_attempts_left) + " $attemptsLeft",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun BoxScope.GameLost(attemptsLeft: Int) {
    Column(
        modifier = Modifier
            .align(alignment = Alignment.BottomCenter)
            .padding(0.dp, 0.dp, 0.dp, 12.dp)
    ) {
        Column(
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(100))
                .clip(shape = RoundedCornerShape(100.dp))
                .background(MaterialTheme.colorScheme.error)
        ) {
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(24.dp, 6.dp, 24.dp, 0.dp),
                text = stringResource(R.string.roll_game_result_failure),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                lineHeight = 10.sp,
                color = Color.White
            )
            Text(
                modifier = Modifier
                    .align(alignment = Alignment.CenterHorizontally)
                    .padding(24.dp, 0.dp, 24.dp, 6.dp),
                text = stringResource(R.string.roll_game_attempts_left) + " $attemptsLeft",
                fontSize = 12.sp,
                lineHeight = 10.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun BoxScope.GameWon() {
    Column(
        modifier = Modifier
            .align(alignment = Alignment.BottomCenter)
            .padding(0.dp, 0.dp, 0.dp, 12.dp)
    ) {
        Box(
            modifier = Modifier
                .shadow(20.dp, RoundedCornerShape(100))
                .clip(shape = RoundedCornerShape(100.dp))
                .background(Color.White),
        ) {
            Text(
                modifier = Modifier.padding(24.dp, 12.dp, 24.dp, 12.dp),
                text = stringResource(R.string.roll_game_result_success),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.background
            )
        }
    }
}

@Composable
fun getGameResources(goldenDiceActive: Boolean) =
    if (goldenDiceActive) {
        Quadruple(
            GameR.drawable.ic_base_golden_dice,
            listOf(
                GameR.drawable.ic_dice_1_golden,
                GameR.drawable.ic_dice_2_golden,
                GameR.drawable.ic_dice_3_golden,
                GameR.drawable.ic_dice_4_golden,
                GameR.drawable.ic_dice_5_golden,
                GameR.drawable.ic_dice_6_golden,
            ),
            ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_golden_radiant_shadows),
            ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_golden_dice_sdk_title)
        )
    } else {
        Quadruple(
            GameR.drawable.ic_base_dice,
            listOf(
                GameR.drawable.ic_dice_1_blue,
                GameR.drawable.ic_dice_2_blue,
                GameR.drawable.ic_dice_3_blue,
                GameR.drawable.ic_dice_4_blue,
                GameR.drawable.ic_dice_5_blue,
                GameR.drawable.ic_dice_6_blue,
            ),
            ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_radiant_shadows),
            ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.feature.roll_game.ui.R.drawable.ic_green_sdk_title)
        )
    }

@Composable
fun DiceImage(@DrawableRes imageRes: Int) {
    val image = painterResource(imageRes)
    Image(
        painter = image,
        contentDescription = "Dice Image",
    )
}

@Preview(
    showSystemUi = true,
    // Use to test Landscape
    //device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun Preview() {
    DiceRollTheme(darkTheme = true, goldenDiceTheme = true) {
        RollGameContent(
            attemptsLeft = 3,
            goldenDiceActive = true,
            onSaveDiceRoll = {},
            onBuyClick = { context, item -> },
            sdkSetupState = true,
            attemptsPrice = "€ 1.0"
        )
    }
}
