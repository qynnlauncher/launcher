package com.hg.qynnlauncher.ui2.settings.sections.gestures

data class SettingsScreen2GestureCustomizationSectionActions(
    val changeEdgeSensitivity: (Float) -> Unit,
    val changeBackVelocity: (Float) -> Unit,
    val changeRecentsVelocity: (Float) -> Unit,
    val changeHomeDistance: (Float) -> Unit,
) {
    companion object {
        fun empty() = SettingsScreen2GestureCustomizationSectionActions(
            changeEdgeSensitivity = {},
            changeBackVelocity = {},
            changeRecentsVelocity = {},
            changeHomeDistance = {},
        )
    }
}