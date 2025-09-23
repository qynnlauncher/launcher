package com.hg.qynnlauncher.ui2.settings.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.utils.ComposableContent

@Composable
fun SettingsScreen2Section(label: String, iconResId: Int, content: ComposableContent)
{
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .fillMaxWidth(),
    )
    {
        SettingsScreen2SectionHeader(label = label, iconResId = iconResId)
        Column(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, bottom = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            content()
        }
    }
}