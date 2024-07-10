package com.appcoins.diceroll.core.ui.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LoadingAnimation(
  titleMessage: String? = null,
  bodyMessage: String? = null,
) {
  StateAnimation(
    titleMessage = titleMessage,
    bodyMessage = bodyMessage,
    isSuccess = false,
    isFailed = false
  )
}

@Composable
fun SuccessAnimation(
  titleMessage: String? = null,
  bodyMessage: String? = null,
) {
  StateAnimation(
    titleMessage = titleMessage,
    bodyMessage = bodyMessage,
    isSuccess = true,
    isFailed = false
  )
}

@Composable
fun ErrorAnimation(
  titleMessage: String? = null,
  bodyMessage: String? = null,
) {
  StateAnimation(
    titleMessage = titleMessage,
    bodyMessage = bodyMessage,
    isSuccess = false,
    isFailed = true
  )
}

@Composable
private fun StateAnimation(
  modifier: Modifier = Modifier,
  titleMessage: String? = null,
  bodyMessage: String? = null,
  isSuccess: Boolean,
  isFailed: Boolean,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp)
      .padding(bottom = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly
  ) {
    val clipSpecs = LottieClipSpec.Progress(
      min = if (isSuccess) 0.20f else if (isFailed) 0.70f else 0.0f,
      max = if (isSuccess) 0.45f else if (isFailed) 0.95f else 0.282f
    )

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.anim_loading_success_failed))

    LottieAnimation(
      modifier = modifier,
      composition = composition,
      iterations = if (isSuccess || isFailed) 1 else LottieConstants.IterateForever,
      clipSpec = clipSpecs,
    )
    Spacer(modifier = Modifier.padding(8.dp))
    if (titleMessage != null) {
      Text(
        text = titleMessage,
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
      )
    }
    if (bodyMessage != null) {
      Spacer(modifier = Modifier.padding(8.dp))
      Text(
        text = bodyMessage,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center
      )
    }
  }
}
