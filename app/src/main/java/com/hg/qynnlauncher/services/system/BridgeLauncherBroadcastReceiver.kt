package com.hg.qynnlauncher.services.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder

private val TAG = "BroadcastReceiver"

class QYNNLauncherBroadcastReceiver(
    private val _apps: InstalledAppsHolder,
) : BroadcastReceiver()
{
    override fun onReceive(context: Context?, intent: Intent?)
    {
        if (context == null || intent == null) return

        Log.d(TAG, "onReceive: ${intent.action}")

        when (intent.action)
        {
            Intent.ACTION_PACKAGE_ADDED ->
            {
                val packageName = intent.data?.encodedSchemeSpecificPart
                val isReplacing = intent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false

                if (packageName != null && !isReplacing)
                {
                    _apps.notifyAppAdded(packageName)
                }
            }

            Intent.ACTION_PACKAGE_REPLACED ->
            {
                val packageName = intent.data?.encodedSchemeSpecificPart
                if (packageName != null)
                {
                    _apps.notifyAppChanged(packageName)
                }
            }

            Intent.ACTION_PACKAGE_REMOVED ->
            {
                val packageName = intent.data?.encodedSchemeSpecificPart
                val isReplacing = intent.extras?.getBoolean(Intent.EXTRA_REPLACING) ?: false
                if (packageName != null && !isReplacing)
                {
                    _apps.notifyAppRemoved(packageName)
                }
            }

        }
    }

    companion object
    {
        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }
    }
}

