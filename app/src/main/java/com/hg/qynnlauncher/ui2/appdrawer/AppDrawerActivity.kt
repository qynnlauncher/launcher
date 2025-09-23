package com.hg.qynnlauncher.ui2.appdrawer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherTheme
import com.hg.qynnlauncher.ui2.appdrawer.composables.AppDrawerScreen

class AppDrawerActivity : ComponentActivity()
{
    private val _appDrawerVM: AppDrawerVM by viewModels { AppDrawerVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            QYNNLauncherTheme {
                AppDrawerScreen(
                    vm = _appDrawerVM,
                    requestFinish = { finish() }
                )
            }
        }
    }
}