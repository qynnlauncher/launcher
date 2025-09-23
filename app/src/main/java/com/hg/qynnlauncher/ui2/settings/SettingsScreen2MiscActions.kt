package com.hg.qynnlauncher.ui2.settings

data class SettingsScreen2MiscActions(
    val permissionsChanged: (areGranted: Map<String, Boolean>) -> Unit,
)
{
    companion object {
        fun empty() = SettingsScreen2MiscActions({})
    }
}