package com.appcoins.diceroll.sdk.feature.roll_game.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.ui.design.theme.darkAppColorScheme
import com.appcoins.diceroll.sdk.core.ui.design.theme.darkGoldenDiceAppColorScheme
import com.appcoins.diceroll.sdk.core.ui.design.theme.darkTrialDiceAppColorScheme
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.DEFAULT
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.GOLDEN_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription.TRIAL_DICE
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.SubscriptionPrefs
import com.appcoins.diceroll.sdk.payments.data.models.Item

@Composable
fun DiceSelectionDialog(
    subscriptionPrefs: SubscriptionPrefs,
    onDismissRequest: () -> Unit,
    onChangeSelectedSubscription: (subscription: Subscription) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background.copy(alpha = 0.8F),
                RectangleShape
            )
            .clickable { onDismissRequest() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp, 0.dp, 24.dp, 0.dp)
                .clip(shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .wrapContentHeight(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp, 48.dp, 24.dp, 48.dp)
                    .wrapContentHeight()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SubscriptionSelectionPanel(subscriptionPrefs, onChangeSelectedSubscription)
            }
        }
    }
}

@Composable
fun SuccessState(item: Item, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = R.drawable.ic_success),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = stringResource(R.string.payment_dialog_success_title),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp
    )
    Text(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = item.getSuccessMessage(context),
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 14.sp
    )
    Button(
        modifier = Modifier
            .padding(0.dp, 24.dp, 0.dp, 0.dp)
            .clip(shape = RoundedCornerShape(24.dp, 24.dp, 24.dp, 24.dp))
            .fillMaxWidth(),
        onClick = onDismiss
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = stringResource(R.string.ok),
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
private fun SubscriptionSelectionPanel(
    subscriptionPrefs: SubscriptionPrefs,
    onChangeSelectedSubscription: (subscription: Subscription) -> Unit,
) {
    Text(
        text = "Select your Subscription skin:",
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
    Column(Modifier.selectableGroup()) {
        SettingsChooserRow(
            text = "Default",
            color = darkAppColorScheme.tertiary,
            selected = subscriptionPrefs.selectedSubscription == DEFAULT,
            onClick = { onChangeSelectedSubscription(DEFAULT) },
        )
        if (subscriptionPrefs.availableSubscriptions.contains(GOLDEN_DICE)) {
            SettingsChooserRow(
                text = "Golden Dice",
                color = darkGoldenDiceAppColorScheme.tertiary,
                selected = subscriptionPrefs.selectedSubscription == GOLDEN_DICE,
                onClick = { onChangeSelectedSubscription(GOLDEN_DICE) },
            )
        }

        if (subscriptionPrefs.availableSubscriptions.contains(TRIAL_DICE)) {
            SettingsChooserRow(
                text = "Trial Dice",
                color = darkTrialDiceAppColorScheme.tertiary,
                selected = subscriptionPrefs.selectedSubscription == TRIAL_DICE,
                onClick = { onChangeSelectedSubscription(TRIAL_DICE) },
            )
        }
    }
}

@Composable
fun SettingsChooserRow(
    text: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(100))
                .width(20.dp)
                .background(color = color, RectangleShape),
        )
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color.White)
    }
}

@Preview
@Composable
fun Preview() {
    DiceSelectionDialog(SubscriptionPrefs(listOf(TRIAL_DICE), TRIAL_DICE), {}, {})
}
