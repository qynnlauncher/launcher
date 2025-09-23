package com.hg.qynnlauncher.services.system

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import androidx.datastore.preferences.core.edit
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.setQYNNSetting
import kotlinx.coroutines.runBlocking


class QYNNLauncherAccessibilityService : AccessibilityService()
{
    companion object {
        var instance: QYNNLauncherAccessibilityService? = null
            private set
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?)
    {
    }

    override fun onInterrupt()
    {
    }

    override fun onServiceConnected()
    {
        instance = this
        writeIsAccessibilityServiceEnabled(true)
    }

    override fun onUnbind(intent: Intent?): Boolean
    {
        writeIsAccessibilityServiceEnabled(false)
        instance = null
        return super.onUnbind(intent)
    }

    private fun writeIsAccessibilityServiceEnabled(isEnabled: Boolean)
    {
        runBlocking {
            settingsDataStore.edit { prefs ->
                prefs.setQYNNSetting(QYNNSettings.isAccessibilityServiceEnabled, isEnabled)
            }
        }
    }
}