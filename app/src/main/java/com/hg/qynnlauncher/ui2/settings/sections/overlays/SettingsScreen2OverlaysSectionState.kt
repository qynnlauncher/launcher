package com.hg.qynnlauncher.ui2.settings.sections.overlays

import com.hg.qynnlauncher.services.settings2.SystemBarAppearanceOptions

data class SettingsScreen2OverlaysSectionState(
    val statusBarAppearance: SystemBarAppearanceOptions,
    val navigationBarAppearance: SystemBarAppearanceOptions,
    val drawWebViewOverscrollEffects: Boolean,
)