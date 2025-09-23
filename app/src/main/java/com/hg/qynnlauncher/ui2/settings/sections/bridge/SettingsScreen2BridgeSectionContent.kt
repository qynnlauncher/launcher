package com.hg.qynnlauncher.ui2.settings.sections.qynn

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.ui2.shared.Btn
import com.hg.qynnlauncher.ui2.shared.ResIcon
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.QYNNThemeOptions
import com.hg.qynnlauncher.ui2.shared.ActionCard
import com.hg.qynnlauncher.ui2.shared.CheckboxField
import com.hg.qynnlauncher.ui2.shared.OptionsRow
import com.hg.qynnlauncher.ui2.shared.PreviewWithSurfaceAndPadding
import com.hg.qynnlauncher.ui2.theme.borders

@Composable
fun SettingsScreen2QYNNSectionContent(
    state: SettingsScreen2QYNNSectionState,
    actions: SettingsScreen2QYNNSectionActions,
    modifier: Modifier = Modifier
)
{
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    )
    {
        OptionsRow(
            label = "Theme",
            options = mapOf(
                QYNNThemeOptions.System to "System",
                QYNNThemeOptions.Light to "Light",
                QYNNThemeOptions.Dark to "Dark",
            ),
            selectedOption = state.theme,
            onChange = { actions.changeTheme(it) },
//        onChange = { theme ->
//            vm.edit {
//                setQYNNSetting(QYNNSettings.theme, theme)
//            }
//        },
        )

        CheckboxField(
            label = QYNNSettings.showQYNNButton.displayName,
            isChecked = state.showQYNNButton,
            onCheckedChange = { actions.changeShowQYNNButton(it) }
        )

//                    ProvideTextStyle(value = MaterialTheme.typography.body2.copy(color = MaterialTheme.colors.textSec))
//                    {
//                        Tip("Tap and hold the button to move it.")
//                    }


        CheckboxField(
            label = QYNNSettings.showLaunchAppsWhenQYNNButtonCollapsed.displayName,
            isChecked = state.showLaunchAppsWhenQYNNButtonCollapsed,
            onCheckedChange = { actions.changeShowLaunchAppsWhenQYNNButtonCollapsed(it) }
        )

        if (!state.isQSTileAdded)
        {
//        if (CurrentAndroidVersion.supportsQSTilePrompt())
            if (state.isQSTilePromptSupported)
            {
                ActionCard(
                    title = "Quick settings tile",
                    description = "You can add a quick settings tile to unobtrusively toggle the QYNN button. Long-pressing the tile opens this settings screen."
                )
                {
//                val sbm = context.getSystemService(StatusBarManager::class.java)
//                val compName = ComponentName(context, QYNNButtonQSTileService::class.java)

                    Btn(
                        text = "Add tile",
                        suffixIcon = R.drawable.ic_plus,
                        onClick = { actions.requestQSTilePrompt() },
//                    onClick = {
//                    sbm.requestAddTileService(
//                        compName,
//                        "QYNN button",
//                        Icon.createWithResource(context, R.drawable.ic_qynn_white),
//                        {},
//                        {}
                    )
                }
            }
            else
            {
                ActionCard(
                    title = "Quick settings tile",
                    description = "You can add a quick settings tile to unobtrusively toggle the QYNN button. Long-pressing the tile opens this settings screen.\n"
                            + "\n"
                            + "Quick settings are the toggles in your notifications area that you use to toggle for example WiFi or Bluetooth. "
                            + "To add the QYNN button tile, expand the quick settings panel and look for an edit button."
                )
            }
        }
        else
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .border(MaterialTheme.borders.soft, MaterialTheme.shapes.medium)
                    .padding(start = 12.dp, top = 16.dp, bottom = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            )
            {
                ResIcon(R.drawable.ic_check, color = MaterialTheme.colors.primary)
                Text("Quick settings tile added.")
            }
        }
    }
}


// PREVIEWS

@Composable
fun SettingsScreen2QYNNSectionPreview(
    theme: QYNNThemeOptions = QYNNThemeOptions.System,
    showQYNNButton: Boolean = true,
    showLaunchAppsWhenQYNNButtonCollapsed: Boolean = true,
    isQSTileAdded: Boolean = false,
    isQSTilePromptSupported: Boolean = false,
)
{
    PreviewWithSurfaceAndPadding {
        SettingsScreen2QYNNSectionContent(
            state = SettingsScreen2QYNNSectionState(
                theme = theme,
                showQYNNButton = showQYNNButton,
                showLaunchAppsWhenQYNNButtonCollapsed = showLaunchAppsWhenQYNNButtonCollapsed,
                isQSTileAdded = isQSTileAdded,
                isQSTilePromptSupported = isQSTilePromptSupported,
            ),
            actions = SettingsScreen2QYNNSectionActions.empty(),
        )
    }
}

@Composable
@PreviewLightDark
fun SettingsScreen2QYNNSectionPreview01()
{
    SettingsScreen2QYNNSectionPreview(
        isQSTilePromptSupported = false
    )
}

@Composable
@PreviewLightDark
fun SettingsScreen2QYNNSectionPreview02()
{
    SettingsScreen2QYNNSectionPreview(
        theme = QYNNThemeOptions.Light,
        showQYNNButton = false,
        isQSTilePromptSupported = true,
    )
}

@Composable
@PreviewLightDark
fun SettingsScreen2QYNNSectionPreview03()
{
    SettingsScreen2QYNNSectionPreview(
        theme = QYNNThemeOptions.Dark,
        showQYNNButton = true,
        showLaunchAppsWhenQYNNButtonCollapsed = false,
        isQSTileAdded = true,
    )
}