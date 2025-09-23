package com.hg.qynnlauncher.ui2.settings.sections.qynn

import com.hg.qynnlauncher.services.settings2.QYNNThemeOptions

data class SettingsScreen2QYNNSectionState(
    val theme: QYNNThemeOptions,
    val showQYNNButton: Boolean,
    val showLaunchAppsWhenQYNNButtonCollapsed: Boolean,
    val isQSTileAdded: Boolean,
    val isQSTilePromptSupported: Boolean,
)