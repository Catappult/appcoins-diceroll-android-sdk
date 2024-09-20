package com.appcoins.diceroll.sdk.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.ui.widgets.BuildConfig
import com.appcoins.diceroll.sdk.core.utils.R
import com.appcoins.diceroll.sdk.feature.settings.data.model.ThemeConfig
import com.appcoins.diceroll.sdk.feature.settings.data.model.UserPrefs
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsUiState.Loading
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsUiState.Success

@Composable
fun SettingsRoute(
  onDismiss: () -> Unit,
  viewModel: SettingsViewModel = hiltViewModel(),
) {
  val settingsUiState by viewModel.uiState.collectAsStateWithLifecycle()
  SettingsDialog(
    onDismiss = onDismiss,
    settingsUiState = settingsUiState,
    viewModel = viewModel,
  )
}

@Composable
fun SettingsDialog(
  settingsUiState: SettingsUiState,
  onDismiss: () -> Unit,
  viewModel: SettingsViewModel,
) {
  AlertDialog(
    properties = DialogProperties(usePlatformDefaultWidth = false),
    modifier = Modifier.widthIn(max = LocalConfiguration.current.screenWidthDp.dp - 80.dp),
    onDismissRequest = { onDismiss() },
    title = {
      Text(
        text = stringResource(R.string.settings_title),
        style = MaterialTheme.typography.titleLarge,
      )
    },
    text = {
      HorizontalDivider()
      Column(Modifier.verticalScroll(rememberScrollState())) {
        ShowSDKInformation()
        ShowUpdateInformation(viewModel)
        when (settingsUiState) {
          Loading -> {
            Text(
              text = stringResource(R.string.loading),
              modifier = Modifier.padding(vertical = 16.dp),
            )
          }

          is Success -> {
            SettingsPanel(
              userPrefs = settingsUiState.userPrefs,
              onChangeThemeConfig = viewModel::updateThemeConfig,
            )
          }
        }
      }
    },
    confirmButton = {
      Text(
        text = stringResource(R.string.confirm),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
          .padding(horizontal = 8.dp)
          .clickable { onDismiss() },
      )
    },
  )
}

@Composable
fun ShowSDKInformation() {
  Text(
    text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.sdk_version_title) +
            BuildConfig.SDK_BILLING_LIBRARY_VERSION,
    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
  )
}

@Composable
fun ShowUpdateInformation(settingsViewModel: SettingsViewModel) {
  val context = LocalContext.current
  Text(
    text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_title),
    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
  )
  Button(onClick = {
    settingsViewModel.launchAppUpdateDialog(context)
  }) {
    Text(text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_button))
  }
}

@Composable
private fun SettingsPanel(
  userPrefs: UserPrefs,
  onChangeThemeConfig: (themeConfig: ThemeConfig) -> Unit,
) {
  SettingsDialogSectionTitle(text = stringResource(R.string.settings_theme))
  Column(Modifier.selectableGroup()) {
    SettingsChooserRow(
      text = stringResource(R.string.settings_theme_system),
      selected = userPrefs.themeConfig == ThemeConfig.FOLLOW_SYSTEM,
      onClick = { onChangeThemeConfig(ThemeConfig.FOLLOW_SYSTEM) },
    )
    SettingsChooserRow(
      text = stringResource(R.string.settings_theme_light),
      selected = userPrefs.themeConfig == ThemeConfig.LIGHT,
      onClick = { onChangeThemeConfig(ThemeConfig.LIGHT) },
    )
    SettingsChooserRow(
      text = stringResource(R.string.settings_theme_dark),
      selected = userPrefs.themeConfig == ThemeConfig.DARK,
      onClick = { onChangeThemeConfig(ThemeConfig.DARK) },
    )
  }
}

@Composable
private fun SettingsDialogSectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
  )
}

@Composable
fun SettingsChooserRow(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
) {
  Row(
    Modifier
      .fillMaxWidth()
      .selectable(
        selected = selected,
        role = Role.RadioButton,
        onClick = onClick,
      )
      .padding(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    RadioButton(
      selected = selected,
      onClick = null,
    )
    Spacer(Modifier.width(8.dp))
    Text(text)
  }
}
