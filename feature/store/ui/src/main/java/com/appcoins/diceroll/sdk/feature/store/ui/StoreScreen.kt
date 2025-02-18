package com.appcoins.diceroll.sdk.feature.store.ui

import android.content.Context
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
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.appcoins.diceroll.sdk.payments.data.models.InternalSkuType as SkuType
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
    onBuyClick: (Context, Item) -> Unit,
    subscriptionStatusStateFlow: (SkuDetails) -> StateFlow<Boolean>,
) {
    when (skuDetails.itemType) {
        SkuType.INAPP.name -> ConsumableItem(skuDetails = skuDetails, onBuyClick)
        SkuType.SUBS.name -> SubscriptionItem(
            skuDetails = skuDetails,
            onBuyClick,
            subscriptionStatusStateFlow
        )
    }
}

@Composable
fun ConsumableItem(skuDetails: SkuDetails, onBuyClick: (Context, Item) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.primaryContainer, RectangleShape
            )
            .padding(16.dp)
            .clickable {
                when (skuDetails.sku) {
                    Item.ATTEMPTS_SKU -> onBuyClick(context, Item.Attempts)
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
    onBuyClick: (Context, Item) -> Unit,
    subscriptionStatusStateFlow: (SkuDetails) -> StateFlow<Boolean>,
) {
    val context = LocalContext.current
    val isSubscriptionActive by subscriptionStatusStateFlow(skuDetails).collectAsStateWithLifecycle()
    if (isSubscriptionActive) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                Row(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp, 16.dp, 16.dp, 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = skuDetails.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = skuDetails.price,
                            fontSize = 16.sp
                        )
                        Text(
                            text = stringResource(R.string.store_screen_unsubscribe_button_text),
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
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
                Row(
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            RectangleShape
                        )
                        .border(
                            width = 1.dp,
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
            }
        }
    } else {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.primaryContainer, RectangleShape)
                .padding(16.dp)
                .clickable {
                    when (skuDetails.sku) {
                        Item.GOLD_DICE_SKU -> onBuyClick(context, GoldDice)
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
    Row(modifier = Modifier.padding(8.dp)) { }
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
                            { _: Context, _: Item -> },
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
            "subs",
            "golden_dice",
            "subs",
            "€ 1.0",
            1L,
            "EUR",
            "APPC 10.92",
            10.92.toLong(),
            "APPC",
            "$ 1.1",
            1.1.toLong(),
            "USD",
            "Golden Dice",
            "More attempts for the Diceroll",
        ),
        SkuDetails(
            "inapp",
            "attempts",
            "inapp",
            "€ 1.0",
            1L,
            "EUR",
            "APPC 10.92",
            10.92.toLong(),
            "APPC",
            "$ 1.1",
            1.1.toLong(),
            "USD",
            "Attempts",
            "More attempts for the Diceroll",
        ),
    )
