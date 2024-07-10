package com.appcoins.diceroll.sdk

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appcoins.diceroll.sdk.MainActivityUiState.*
import com.appcoins.diceroll.sdk.core.ui.design.theme.*
import com.appcoins.diceroll.sdk.core.utils.ActivityResultStream
import com.appcoins.diceroll.sdk.feature.settings.data.model.ThemeConfig
import com.appcoins.diceroll.sdk.payments.appcoins_sdk.SdkResult
import com.appcoins.diceroll.sdk.ui.DiceRollApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

  private val viewModel: MainActivityViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    val splashScreen = installSplashScreen()
    super.onCreate(savedInstanceState)

    var uiState: MainActivityUiState by mutableStateOf(Loading)

    // Update the uiState
    lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.uiState
          .onEach {
            uiState = it
          }
          .collect()
      }
    }

    splashScreen.setKeepOnScreenCondition {
      when (uiState) {
        Loading -> true
        is Success -> false
      }
    }

    setContent {
      val darkTheme = shouldUseDarkTheme(uiState)
      DiceRollTheme(darkTheme = darkTheme) {
        DiceRollApp()
      }
    }
  }

  @Deprecated(message = "ActivityResultContracts should be used instead")
  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    @Suppress("DEPRECATION") super.onActivityResult(requestCode, resultCode, data)
    lifecycleScope.launch {
      ActivityResultStream.publish(SdkResult(requestCode, resultCode, data))
    }
  }
}

@Composable
private fun shouldUseDarkTheme(
  uiState: MainActivityUiState,
): Boolean = when (uiState) {
  Loading -> isSystemInDarkTheme()
  is Success -> when (uiState.userPrefs.themeConfig) {
    ThemeConfig.FOLLOW_SYSTEM -> isSystemInDarkTheme()
    ThemeConfig.LIGHT -> false
    ThemeConfig.DARK -> true
  }
}
