package com.appcoins.diceroll.sdk.feature.store.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.appcoins.diceroll.sdk.core.ui.design.DiceRollIcons
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.Item.GoldDice
import com.appcoins.sdk.billing.SkuDetails
import com.appcoins.sdk.billing.types.SkuType

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
            imageVector = ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.drawable.ic_green_sdk_title),
            contentDescription = "Title"
        )
        Column(
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.store_screen_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.store_screen_description),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(purchasableItemsList) { sku ->
                    PurchasableItem(sku, storeViewModel::launchBillingSdkFlow)
                }
            }
        }
    }
}

@Composable
fun PurchasableItem(skuDetails: SkuDetails, onBuyClick: (Context, Item) -> Unit) {
    when (skuDetails.itemType) {
        SkuType.inapp.name -> ConsumableItem(skuDetails = skuDetails, onBuyClick)
        SkuType.subs.name -> SubscriptionItem(skuDetails = skuDetails, onBuyClick)
    }
    Row(modifier = Modifier.padding(8.dp)) { }
}

@Composable
fun NonConsumableItem(skuDetails: SkuDetails, onBuyClick: (Item) -> Unit) {
}

@Composable
fun ConsumableItem(skuDetails: SkuDetails, onBuyClick: (Context, Item) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color.Gray, RectangleShape)
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
            Text(text = skuDetails.title)
            Text(modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp), text = skuDetails.price)
        }
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(100))
                .background(Color.Green, RectangleShape)
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
fun SubscriptionItem(skuDetails: SkuDetails, onBuyClick: (Context, Item) -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(16.dp))
            .background(Color.Gray, RectangleShape)
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
            Text(text = skuDetails.title)
            Text(modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp), text = skuDetails.price)
        }
        Box(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(100))
                .background(Color.Green, RectangleShape)
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

@Preview(
    showSystemUi = true,
    // Use to test Landscape
    //device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun Preview() {
    val purchasableItemsList = getListOfItems()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp, 24.dp, 24.dp, 0.dp)
    ) {
        Image(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            imageVector = ImageVector.vectorResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.drawable.ic_green_sdk_title),
            contentDescription = "Title"
        )
        Column(
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.store_screen_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.store_screen_description),
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp),
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
            ) {
                items(purchasableItemsList) { sku ->
                    PurchasableItem(sku) { _: Context, _: Item -> }
                }
            }
        }
    }
}

fun getListOfItems(): ArrayList<SkuDetails> =
    arrayListOf(
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
        )
    )
