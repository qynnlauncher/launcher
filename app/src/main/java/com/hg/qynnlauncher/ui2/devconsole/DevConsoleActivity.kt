package com.hg.qynnlauncher.ui2.devconsole

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherTheme
import com.hg.qynnlauncher.ui2.devconsole.composables.DevConsoleScreen

class DevConsoleActivity : ComponentActivity()
{
    private val _devConsoleVM: DevConsoleVM by viewModels { DevConsoleVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            QYNNLauncherTheme {
                DevConsoleScreen(
                    vm = _devConsoleVM,
                    requestFinish = { finish() }
                )
            }
        }
    }
}
