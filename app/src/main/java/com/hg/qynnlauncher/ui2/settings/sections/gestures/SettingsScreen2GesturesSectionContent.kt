package com.hg.qynnlauncher.ui2.settings.sections.gestures

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.R

@Composable
fun SettingsScreen2GesturesSectionContent() {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = context.getString(R.string.gesture_service_description))
        Button(
            onClick = {
                openAccessibilitySettings(context)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable Gesture Service")
        }
    }
}

private fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    context.startActivity(intent)
}