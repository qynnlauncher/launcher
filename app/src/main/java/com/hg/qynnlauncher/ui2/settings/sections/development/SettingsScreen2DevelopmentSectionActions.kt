package com.hg.qynnlauncher.ui2.settings.sections.development

data class SettingsScreen2DevelopmentSectionActions(
    val exportMockFolder: () -> Unit,
)
{
    companion object
    {
        fun empty() = SettingsScreen2DevelopmentSectionActions({})
    }
}