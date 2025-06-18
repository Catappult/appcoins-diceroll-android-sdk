package com.appcoins.diceroll.sdk

import android.app.KeyguardManager
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.appcoins.diceroll.sdk.MainActivityUiState.Loading
import com.appcoins.diceroll.sdk.MainActivityUiState.Success
import com.appcoins.diceroll.sdk.core.ui.design.theme.DiceRollTheme
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.Subscription
import com.appcoins.diceroll.sdk.feature.roll_game.data.model.SubscriptionPrefs
import com.appcoins.diceroll.sdk.feature.settings.data.model.ThemeConfig
import com.appcoins.diceroll.sdk.ui.DiceRollApp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private var uiState: MainActivityUiState by mutableStateOf(Loading)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        viewModel.requestPermissions(this)

        if (VERSION.SDK_INT >= VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        }

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

        viewModel.observePaymentState()
        viewModel.observeDiceSelectionDialogVisibilityState()

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                Loading -> true
                else -> false
            }
        }

        setContent {
            val darkTheme = shouldUseDarkTheme(uiState)
            val subscriptionTypeDiceTheme = subscriptionTypeDiceTheme(uiState)
            val subscriptionPrefs = subscriptionPrefs(uiState)
            DiceRollTheme(
                darkTheme = darkTheme,
                subscriptionTypeDiceTheme = subscriptionTypeDiceTheme
            ) {
                Box {
                    val paymentState by viewModel.paymentState.collectAsState()
                    val diceSelectionDialogVisibilityState by viewModel.diceSelectionDialogVisibilityState.collectAsState()
                    DiceRollApp(
                        paymentState,
                        viewModel::onPaymentDialogDismissed,
                        diceSelectionDialogVisibilityState,
                        subscriptionPrefs,
                        viewModel::onDiceSelectionDialogDismissed,
                        viewModel::onDiceSelected
                    )
                }
            }
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

@Composable
private fun subscriptionTypeDiceTheme(
    uiState: MainActivityUiState,
): Subscription = when (uiState) {
    Loading -> Subscription.DEFAULT
    is Success -> uiState.subscriptionPrefs.selectedSubscription
}

@Composable
private fun subscriptionPrefs(
    uiState: MainActivityUiState,
): SubscriptionPrefs = when (uiState) {
    Loading -> SubscriptionPrefs()
    is Success -> uiState.subscriptionPrefs
}
