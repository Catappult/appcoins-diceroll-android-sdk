package com.appcoins.diceroll.sdk.feature.settings.ui

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
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
                onLaunchAppUpdate = viewModel::launchAppUpdateDialog,
                onUpdateThemeConfig = viewModel::updateThemeConfig
            )
        }
    }
}

@Composable
fun UserSettingsContent(
    settingsUiState: Success,
    onLaunchAppUpdate: (Context) -> Unit,
    onUpdateThemeConfig: (ThemeConfig) -> Unit
) {
    HeaderTitle("Billing Information")
    ShowSDKInformation()
    GeneralSpacer()
    HeaderTitle("App Information")
    ShowUpdateInformation { onLaunchAppUpdate(it) }
    /* Turn Off until Light color scheme is done
    HorizontalDivider()
    SettingsPanel(
        userPrefs = settingsUiState.userPrefs,
        onChangeThemeConfig = onUpdateThemeConfig,
    )*/
}

@Composable
fun SettingsContent(
    settingsUiState: Success,
    onLaunchAppUpdate: (Context) -> Unit,
    onUpdateThemeConfig: (ThemeConfig) -> Unit
) {
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
        Text(
            "Settings",
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(0.dp, 24.dp, 0.dp, 0.dp)
        )
        Column(
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 0.dp)
                .verticalScroll(rememberScrollState())
        ) {
            UserSettingsContent(settingsUiState, onLaunchAppUpdate, onUpdateThemeConfig)
            GeneralSpacer()
            HeaderTitle("Statistics")
            StatsContent(
                diceRollList = settingsUiState.diceRollList
            )
        }
    }
}

@Composable
fun ShowSDKInformation() {
    Column {
        Text(
            fontSize = 14.sp,
            text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.sdk_version_title),
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = BuildConfig.SDK_BILLING_LIBRARY_VERSION,
            fontSize = 12.sp,
        )

    }
}

@Composable
fun ShowUpdateInformation(onLaunchUpdateClick: (Context) -> Unit) {
    Column {
        Text(
            text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_title),
            fontSize = 12.sp,
        )
        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = com.appcoins.diceroll.sdk.core.ui.design.R.string.check_for_updates_button),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = com.appcoins.diceroll.sdk.feature.settings.ui.R.color.primary)
            )
            Spacer(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            val context = LocalContext.current
            Box(
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(100))
                    .background(MaterialTheme.colorScheme.primary, RectangleShape)
                    .padding(4.dp)
                    .clickable { onLaunchUpdateClick(context) },
            ) {
                Image(
                    imageVector = arrowRight,
                    contentDescription = "Details",
                    colorFilter = ColorFilter.tint(Color.Black)
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

@Composable
fun HeaderTitle(title: String) {
    Text(
        title,
        fontSize = 16.sp,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(bottom = 16.dp),
        fontWeight = FontWeight.Bold
    )
}

@Preview
@Composable
fun Preview() {
    SettingsContent(Success(UserPrefs(), emptyList()), {}, {})
}

@Composable
fun GeneralSpacer() {
    Spacer(modifier = Modifier.padding(top = 24.dp))
    HorizontalDivider()
    Spacer(modifier = Modifier.padding(top = 24.dp))
}
