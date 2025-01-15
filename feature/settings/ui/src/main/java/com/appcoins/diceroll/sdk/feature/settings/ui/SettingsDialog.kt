package com.appcoins.diceroll.sdk.feature.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appcoins.diceroll.sdk.core.ui.design.DiceRollIcons.arrowRight
import com.appcoins.diceroll.sdk.core.ui.widgets.BuildConfig
import com.appcoins.diceroll.sdk.core.ui.widgets.LoadingAnimation
import com.appcoins.diceroll.sdk.core.utils.R
import com.appcoins.diceroll.sdk.feature.settings.data.model.ThemeConfig
import com.appcoins.diceroll.sdk.feature.settings.data.model.UserPrefs
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsUiState.Loading
import com.appcoins.diceroll.sdk.feature.settings.ui.SettingsUiState.Success
import com.appcoins.diceroll.sdk.feature.stats.ui.StatsContent

@Composable
fun SettingsRoute(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
) {
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = settingsUiState,
        viewModel = settingsViewModel,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    viewModel: SettingsViewModel
) {
    when (uiState) {
        Loading -> {
            LoadingAnimation(stringResource(id = R.string.loading))
        }

        is Success -> {
            SettingsContent(
                settingsUiState = uiState,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun UserSettingsContent(
    settingsUiState: Success,
    viewModel: SettingsViewModel,
) {
    val context = LocalContext.current
    ShowSDKInformation()
    GeneralSpacer()
    ShowUpdateInformation { viewModel.launchAppUpdateDialog(context) }
    HorizontalDivider()
    SettingsPanel(
        userPrefs = settingsUiState.userPrefs,
        onChangeThemeConfig = viewModel::updateThemeConfig,
    )
}

@Composable
fun SettingsContent(
    settingsUiState: Success,
    viewModel: SettingsViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        UserSettingsContent(settingsUiState, viewModel)
        HorizontalDivider()
        StatsContent(
            diceRollList = settingsUiState.diceRollList,
            onDetailsClick = { }
        )
    }
}

@Composable
fun ShowSDKInformation() {
    Column {
        Text(
            fontSize = 16.sp,
            text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.sdk_version_title),
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = BuildConfig.SDK_BILLING_LIBRARY_VERSION,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            fontSize = 12.sp,
        )

    }
}

@Composable
fun ShowUpdateInformation(onLaunchUpdateClick: () -> Unit) {
    Column {
        Text(
            text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_title),
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            fontSize = 12.sp,
        )
        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_button),
                fontSize = 12.sp,
                color = colorResource(id = com.appcoins.diceroll.sdk.feature.settings.ui.R.color.primary)
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            Button(
                shape = ButtonDefaults.outlinedShape,
                onClick = { onLaunchUpdateClick() },
            ) {
                Icon(
                    imageVector = arrowRight,
                    contentDescription = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_button)
                )
            }
        }
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

@Preview
@Composable
fun Preview() {
    Column {
        ShowSDKInformation()
        GeneralSpacer()
        ShowUpdateInformation({})
    }
}

@Composable
fun GeneralSpacer() {
    Spacer(modifier = Modifier.padding(all = 22.dp))
}
