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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appcoins.diceroll.sdk.core.ui.design.R
import com.appcoins.diceroll.sdk.core.ui.widgets.components.CircularLoadingBar
import com.appcoins.diceroll.sdk.payments.data.models.Item
import com.appcoins.diceroll.sdk.payments.data.models.Item.Companion.getGeneralErrorMessage
import com.appcoins.diceroll.sdk.payments.data.models.Item.Companion.getGeneralErrorTitle
import com.appcoins.diceroll.sdk.payments.data.models.InternalResponseCode as ResponseCode

@Composable
fun LoadingState() {
    CircularLoadingBar(50.dp)
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = stringResource(R.string.payment_dialog_waiting_title),
        color = MaterialTheme.colorScheme.onPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    )
}

@Composable
fun ErrorState(item: Item?, responseCode: ResponseCode, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Image(
        modifier = Modifier.size(50.dp),
        painter = painterResource(id = R.drawable.ic_error),
        contentDescription = null,
    )
    Text(
        modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = getGeneralErrorTitle(context, item, responseCode),
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onPrimary,
        fontSize = 16.sp
    )
    Text(
        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp),
        textAlign = TextAlign.Center,
        text = getGeneralErrorMessage(context, item, responseCode),
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
