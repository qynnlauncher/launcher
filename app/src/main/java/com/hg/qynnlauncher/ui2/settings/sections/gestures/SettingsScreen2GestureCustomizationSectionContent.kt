package com.hg.qynnlauncher.ui2.settings.sections.gestures

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.ui2.settings.composables.SettingsScreenSlider

@Composable
fun SettingsScreen2GestureCustomizationSectionContent(
    state: SettingsScreen2GestureCustomizationSectionState,
    actions: SettingsScreen2GestureCustomizationSectionActions,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        SettingsScreenSlider(
            title = "Edge Sensitivity",
            value = state.edgeSensitivity,
            onValueChange = actions.changeEdgeSensitivity,
            valueRange = 1f..100f,
            step = 1f,
        )
        SettingsScreenSlider(
            title = "Back Velocity",
            value = state.backVelocity,
            onValueChange = actions.changeBackVelocity,
            valueRange = 100f..5000f,
            step = 100f,
        )
        SettingsScreenSlider(
            title = "Recents Velocity",
            value = state.recentsVelocity,
            onValueChange = actions.changeRecentsVelocity,
            valueRange = 100f..5000f,
            step = 100f,
        )
        SettingsScreenSlider(
            title = "Home Distance",
            value = state.homeDistance,
            onValueChange = actions.changeHomeDistance,
            valueRange = 50f..500f,
            step = 10f,
        )
    }
}