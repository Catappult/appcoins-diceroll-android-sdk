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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.sdk.billing.ResponseCode

@Composable
fun LoadingState() {
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = R.drawable.diceroll_icon),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "Waiting for the payment to be completed...",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ErrorState(responseCode: ResponseCode, onDismiss: () -> Unit) {
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = R.drawable.diceroll_icon),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "Payment $responseCode",
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "The user $responseCode",
        style = MaterialTheme.typography.bodySmall
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
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun SuccessState(sku: String, onDismiss: () -> Unit) {
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = R.drawable.diceroll_icon),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "Success!",
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = "You received $sku.",
        style = MaterialTheme.typography.bodySmall
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
            style = MaterialTheme.typography.bodySmall
        )
    }
}
