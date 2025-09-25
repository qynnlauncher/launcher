package com.hg.qynnlauncher.services.settings2

object QYNNSettings
{
    val isQSTileAdded = QYNNSetting.systemBool("isQSTileAdded")
    val isDeviceAdminEnabled = QYNNSetting.systemBool("isDeviceAdminEnabled")
    val isAccessibilityServiceEnabled = QYNNSetting.systemBool("isAccessibilityServiceEnabled")
    val isExternalStorageManager = QYNNSetting.systemBool("isExternalStorageManager")

    val currentProjDir = QYNNSetting.file("currentProjDir")
    val lastMockExportDir = QYNNSetting.file("lastMockExportDir")

    val theme = QYNNSetting.enum(
        key = "theme",
        defaultValue = QYNNThemeOptions.System,
    )

    val allowProjectsToTurnScreenOff = QYNNSetting.bool(
        key = "allowProjectsToTurnScreenOff",
        displayName = "Allow projects to turn the screen off",
    )

    val drawSystemWallpaperBehindWebView = QYNNSetting.bool(
        key = "Draw system wallpaper behind WebView",
        defaultValue = true,
        displayName = "Draw system wallpaper behind WebView",
    )

    val statusBarAppearance = QYNNSetting.enum(
        key = "statusBarAppearance",
        defaultValue = SystemBarAppearanceOptions.DarkIcons,
        displayName = "Status bar",
    )

    val navigationBarAppearance = QYNNSetting.enum(
        key = "navigationBarAppearance",
        defaultValue = SystemBarAppearanceOptions.DarkIcons,
        displayName = "Navigation bar",
    )

    val drawWebViewOverscrollEffects = QYNNSetting.bool(
        key = "drawWebViewOverscrollEffects",
        displayName = "Draw WebView overscroll effects",
    )

    val showQYNNButton = QYNNSetting.bool(
        key = "showQYNNButton",
        defaultValue = true,
        displayName = "Show QYNN button",
    )

    val showLaunchAppsWhenQYNNButtonCollapsed = QYNNSetting.bool(
        key = "showLaunchAppsWhenQYNNButtonCollapsed",
        displayName = "Show \"Launch apps\" button when the QYNN menu is collapsed",
    )

    // Gesture Settings
    val edgeSensitivity = QYNNSetting.float("edgeSensitivity", 30f, "Edge Sensitivity (dp)")
    val backVelocity = QYNNSetting.float("backVelocity", 1500f, "Back Gesture Velocity (dp/s)")
    val recentsVelocity = QYNNSetting.float("recentsVelocity", 800f, "Recents Gesture Velocity (dp/s)")
    val homeDistance = QYNNSetting.float("homeDistance", 150f, "Home Gesture Distance (dp)")
}

val ResettableQYNNSettings = with(QYNNSettings)
{
    listOf(
        currentProjDir,
        lastMockExportDir,
        theme,
        allowProjectsToTurnScreenOff,
        drawSystemWallpaperBehindWebView,
        statusBarAppearance,
        navigationBarAppearance,
        drawWebViewOverscrollEffects,
        showQYNNButton,
        showLaunchAppsWhenQYNNButtonCollapsed,
        edgeSensitivity,
        backVelocity,
        recentsVelocity,
        homeDistance,
    )
}