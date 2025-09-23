package com.hg.qynnlauncher.ui2.settings.sections.qynn

import com.hg.qynnlauncher.services.settings2.QYNNThemeOptions

data class SettingsScreen2QYNNSectionActions(
    val changeTheme: (newValue: QYNNThemeOptions) -> Unit,
    val changeShowQYNNButton: (newValue: Boolean) -> Unit,
    val changeShowLaunchAppsWhenQYNNButtonCollapsed: (newValue: Boolean) -> Unit,
    val requestQSTilePrompt: () -> Unit,
)
{
    companion object
    {
        fun empty() = SettingsScreen2QYNNSectionActions({}, {}, {}, {})
    }
}
