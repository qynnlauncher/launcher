package com.hg.qynnlauncher.services.system

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.setQYNNSetting
import kotlinx.coroutines.runBlocking

class QYNNLauncherDeviceAdminReceiver : DeviceAdminReceiver()
{
    override fun onEnabled(context: Context, intent: Intent)
    {
        writeIsDeviceAdminEnabled(context, true)
    }

    override fun onDisabled(context: Context, intent: Intent)
    {
        writeIsDeviceAdminEnabled(context, false)
    }

    private fun writeIsDeviceAdminEnabled(context: Context, isEnabled: Boolean)
    {
        runBlocking {
            context.settingsDataStore.edit { prefs ->
                prefs.setQYNNSetting(QYNNSettings.isDeviceAdminEnabled, isEnabled)
            }
        }
    }
}