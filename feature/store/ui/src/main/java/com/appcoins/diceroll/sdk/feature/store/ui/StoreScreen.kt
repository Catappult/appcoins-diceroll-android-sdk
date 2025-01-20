package com.appcoins.diceroll.sdk.feature.store.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcoins.diceroll.sdk.core.ui.design.DiceRollIcons
import com.appcoins.sdk.billing.SkuDetails
import com.appcoins.sdk.billing.types.SkuType

@Composable
internal fun StoreRoute() {
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
                .verticalScroll(rememberScrollState())
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
            ListOfItems(items = getListOfItems())
        }
    }
}

@Composable
fun ListOfItems(items: ArrayList<SkuDetails>) {
    items.forEach {
        when (it.itemType) {
            SkuType.inapp.name -> ConsumableItem(skuDetails = it)
            SkuType.subs.name -> SubscriptionItem(skuDetails = it)
        }
    }
}

@Composable
fun NonConsumableItem(skuDetails: SkuDetails) {
}

@Composable
fun ConsumableItem(skuDetails: SkuDetails) {
    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(12.dp))
            .background(Color.Gray, RectangleShape)
            .padding(8.dp, 16.dp, 8.dp, 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(text = skuDetails.title)
            Text(modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp), text = skuDetails.price)
        }
        Image(
            imageVector = DiceRollIcons.arrowRight,
            contentDescription = "Details"
        )
    }
}

@Composable
fun SubscriptionItem(skuDetails: SkuDetails) {
}

@Preview(
    showSystemUi = true,
    // Use to test Landscape
    //device = "spec:width=411dp,height=891dp,dpi=420,isRound=false,chinSize=0dp,orientation=landscape"
)
@Composable
fun Preview() {
    StoreRoute()
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
