package com.appcoins.diceroll.sdk.feature.payments.ui.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcoins.diceroll.sdk.core.ui.widgets.components.CircularLoadingBar
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.Item.Companion.getGeneralErrorMessage
import com.appcoins.diceroll.sdk.payments.data.models.Item.Companion.getGeneralErrorTitle
import com.appcoins.sdk.billing.ResponseCode

@Composable
fun LoadingState() {
    CircularLoadingBar(50.dp)
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "Waiting for the payment to be completed...",
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

@Composable
fun ErrorState(item: Item?, responseCode: ResponseCode, onDismiss: () -> Unit) {
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = com.appcoins.diceroll.sdk.feature.payments.ui.R.drawable.ic_error),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = getGeneralErrorTitle(item, responseCode),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp
    )
    Text(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = getGeneralErrorMessage(item, responseCode),
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
            text = "OK",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun SuccessState(item: Item, onDismiss: () -> Unit) {
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = com.appcoins.diceroll.sdk.feature.payments.ui.R.drawable.ic_success),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "Success!",
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp
    )
    Text(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = item.successPurchaseMessage,
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
            text = "OK",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.background
        )
    }
}
