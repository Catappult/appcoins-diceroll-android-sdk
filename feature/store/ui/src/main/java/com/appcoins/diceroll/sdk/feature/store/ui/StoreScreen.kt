package com.appcoins.diceroll.sdk.feature.store.ui

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.ui.design.DiceRollIcons
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType.INAPP
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType.SUBS
import com.appcoins.diceroll.sdk.payments.data.models.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuDetails as SkuDetails

@Composable
internal fun StoreRoute(
    storeViewModel: StoreViewModel = hiltViewModel()
) {
    val purchasableItemsList = storeViewModel.purchasableItems
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 24.dp, 24.dp, 0.dp)
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_green_sdk_title),
            contentDescription = "Title"
        )
        Column(
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = R.string.store_screen_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = R.string.store_screen_description),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(purchasableItemsList) { sku ->
                    PurchasableItem(
                        sku,
                        storeViewModel::launchBillingSdkFlow,
                        storeViewModel::getSubscriptionStateForSKU
                    )
                }
            }
        }
    }
}

@Composable
fun PurchasableItem(
    skuDetails: SkuDetails,
    onBuyClick: (Activity, Item) -> Unit,
    subscriptionStatusStateFlow: (SkuDetails) -> StateFlow<Boolean>,
) {
    when (skuDetails.skuType) {
        INAPP -> ConsumableItem(skuDetails = skuDetails, onBuyClick)
        SUBS -> SubscriptionItem(
            skuDetails = skuDetails,
            onBuyClick,
            subscriptionStatusStateFlow
        )
    }
}

@Composable
fun ConsumableItem(skuDetails: SkuDetails, onBuyClick: (Activity, Item) -> Unit) {
    val context = LocalContext.current as Activity
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.primaryContainer, RectangleShape
            )
            .padding(16.dp)
            .clickable {
                Item.fromSku(skuDetails.sku)?.let {
                    onBuyClick(context, it)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = skuDetails.title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = skuDetails.price,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.primary, RectangleShape)
                .padding(4.dp),
        ) {
            Image(
                imageVector = DiceRollIcons.arrowRight,
                contentDescription = "Details",
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
    Row(modifier = Modifier.padding(8.dp)) { }
}

@Composable
fun SubscriptionItem(
    skuDetails: SkuDetails,
    onBuyClick: (Activity, Item) -> Unit,
    subscriptionStatusStateFlow: (SkuDetails) -> StateFlow<Boolean>,
) {
    val context = LocalContext.current as Activity
    val isSubscriptionActive by subscriptionStatusStateFlow(skuDetails).collectAsStateWithLifecycle()
    if (isSubscriptionActive) {
        ActiveSubscriptionItem(skuDetails, context)
    } else {
        InactiveSubscriptionItem(context, skuDetails, onBuyClick)
    }
    Row(modifier = Modifier.padding(8.dp)) { }
}

@Composable
fun InactiveSubscriptionItem(
    context: Activity,
    skuDetails: SkuDetails,
    onBuyClick: (Activity, Item) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer, RectangleShape)
            .padding(16.dp)
            .clickable {
                Item.fromSku(skuDetails.sku)?.let {
                    onBuyClick(context, it)
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                text = skuDetails.title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = skuDetails.price,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        }
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(100))
                .background(MaterialTheme.colorScheme.primary, RectangleShape)
                .padding(4.dp),
        ) {
            Image(
                imageVector = DiceRollIcons.arrowRight,
                contentDescription = "Details",
                colorFilter = ColorFilter.tint(Color.Black)
            )
        }
    }
}

@Composable
fun ActiveSubscriptionItem(skuDetails: SkuDetails, context: Activity) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Row(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(16.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RectangleShape
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                ) {
                    Text(
                        text = skuDetails.title,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = skuDetails.price,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 16.sp
                    )
                }
                Text(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(100, 0, 0, 100))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            RectangleShape
                        )
                        .padding(8.dp, 4.dp, 8.dp, 4.dp),
                    text = stringResource(R.string.store_screen_subscribed_text),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = stringResource(R.string.store_screen_unsubscribe_button_text),
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 8.dp)
                    .clickable {
                        Toast.makeText(
                            context,
                            "To unsubscribe go to the Wallet App.",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(
    showSystemUi = true,
    // Use to test Landscape
    //device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun Preview() {
    DiceRollTheme(darkTheme = true, goldenDiceTheme = true) {
        val purchasableItemsList = getListOfItems()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp, 24.dp, 24.dp, 0.dp)
        ) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_green_sdk_title),
                contentDescription = "Title"
            )
            Column(
                modifier = Modifier
                    .padding(0.dp, 24.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.store_screen_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(id = R.string.store_screen_description),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
                )
                LazyColumn(
                    modifier = Modifier.fillMaxHeight()
                ) {
                    items(purchasableItemsList) { sku ->
                        PurchasableItem(
                            sku,
                            { _: Activity, _: Item -> },
                            { skuDetails -> MutableStateFlow(true) })
                    }
                }
            }
        }
    }
}

fun getListOfItems(): ArrayList<SkuDetails> =
    arrayListOf(
        SkuDetails(
            "golden_dice",
            SUBS,
            "Golden Dice (AppCoins Diceroll SDK)",
            "€ 1.0",
        ),
        SkuDetails(
            "attempts",
            INAPP,
            "Attempts",
            "€ 1.0",
        ),
    )
