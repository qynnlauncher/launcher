package com.hg.qynnlauncher.ui2.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherThemeStateless
import com.hg.qynnlauncher.utils.ComposableContent

@Composable
fun PreviewWithSurfaceAndPadding(content: ComposableContent)
{
    QYNNLauncherThemeStateless {
        Surface (
            color = MaterialTheme.colors.background,
        )
        {
            Box(modifier = Modifier.padding(16.dp))
            {
                content()
            }
        }
    }
}