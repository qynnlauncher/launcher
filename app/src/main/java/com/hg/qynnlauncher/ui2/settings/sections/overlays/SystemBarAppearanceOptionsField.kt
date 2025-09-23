package com.hg.qynnlauncher.ui2.settings.sections.overlays

import androidx.compose.runtime.Composable
import com.hg.qynnlauncher.services.settings2.SystemBarAppearanceOptions
import com.hg.qynnlauncher.ui2.shared.OptionsRow

@Composable
fun SystemBarAppearanceOptionsField(label: String, selectedOption: SystemBarAppearanceOptions, onChange: (SystemBarAppearanceOptions) -> Unit)
{
    OptionsRow(
        label = label,
        options = mapOf(
            SystemBarAppearanceOptions.Hide to "Hide",
            SystemBarAppearanceOptions.LightIcons to "Light icons",
            SystemBarAppearanceOptions.DarkIcons to "Dark icons",
        ),
        selectedOption = selectedOption,
        onChange = onChange
    )
}