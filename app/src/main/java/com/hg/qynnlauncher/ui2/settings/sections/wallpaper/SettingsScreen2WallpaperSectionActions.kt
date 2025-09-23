package com.hg.qynnlauncher.ui2.settings.sections.wallpaper

data class SettingsScreen2WallpaperSectionActions(
    val changeSystemWallpaper: () -> Unit,
    val changeDrawSystemWallpaperBehindWebView: (newValue: Boolean) -> Unit,
)
{
    companion object
    {
        fun empty() = SettingsScreen2WallpaperSectionActions({}, {})
    }
}