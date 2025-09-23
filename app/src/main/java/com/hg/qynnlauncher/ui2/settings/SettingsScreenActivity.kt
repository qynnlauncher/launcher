package com.hg.qynnlauncher.ui2.settings

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.hg.qynnlauncher.ui2.settings.composables.SettingsScreen2
import com.hg.qynnlauncher.ui2.theme.QYNNLauncherTheme
import com.hg.qynnlauncher.utils.QYNNLauncherApplication

private val TAG = SettingsScreenActivity::class.simpleName

class SettingsScreenActivity : ComponentActivity()
{
    private val _settingsScreenVM: SettingsScreenVM by viewModels { SettingsScreenVM.Factory }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        setContent {
            QYNNLauncherTheme {
                SettingsScreen2(
                    _settingsScreenVM,
                    requestFinish = { finish() }
                )
            }
        }
    }

    override fun onResume()
    {
        QYNNLauncherApplication.services.storagePermsHolder.notifyPermsMightHaveChanged()
        super.onResume()
    }

}