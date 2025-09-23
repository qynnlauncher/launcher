package com.hg.qynnlauncher

import android.app.Application
import android.app.UiModeManager
import android.content.ComponentName
import android.util.Log
import androidx.core.content.ContextCompat
import com.hg.qynnlauncher.api2.qynntojs.QYNNToJSAPI
import com.hg.qynnlauncher.api2.jstoqynn.JSToQYNNAPI
import com.hg.qynnlauncher.api2.server.QYNNServer
import com.hg.qynnlauncher.services.QYNNServices
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder
import com.hg.qynnlauncher.services.devconsole.DevConsoleMessagesHolder
import com.hg.qynnlauncher.services.displayshape.DisplayShapeHolder
import com.hg.qynnlauncher.services.iconcache.IconCache
import com.hg.qynnlauncher.services.iconpackcache.IconPackCache
import com.hg.qynnlauncher.services.iconpackcache.InstalledIconPacksHolder
import com.hg.qynnlauncher.services.lifecycleevents.LifecycleEventsHolder
import com.hg.qynnlauncher.services.mockexport.MockExporter
import com.hg.qynnlauncher.services.perms.PermsHolder
import com.hg.qynnlauncher.services.system.QYNNButtonQSTileService
import com.hg.qynnlauncher.services.system.QYNNLauncherBroadcastReceiver
import com.hg.qynnlauncher.services.system.QYNNLauncherDeviceAdminReceiver
import com.hg.qynnlauncher.services.uimode.SystemUIModeHolder
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsHolder

private const val TAG = "Application"

class QYNNLauncherApplication : Application()
{
    lateinit var adminReceiverComponentName: ComponentName
    lateinit var qsTileServiceComponentName: ComponentName

    lateinit var services: QYNNServices

    override fun onCreate()
    {
        super.onCreate()
        Log.d(TAG, "super.onCreate(): OK")

        services = createServices()
        Log.d(TAG, "createServices(): OK")

        startup()
        Log.d(TAG, "startup(): OK")
    }

    private fun createServices(): QYNNServices
    {
        adminReceiverComponentName = ComponentName(this, QYNNLauncherDeviceAdminReceiver::class.java)
        qsTileServiceComponentName = ComponentName(this, QYNNButtonQSTileService::class.java)

        // this is deliberately set up like this so that whenver a service is added to the provider, the constructor below will complain
        // constructing the services ahead of time helps with manually resolving the dependency graph at compile time
        // yeah this probably could be done by some DI library but I'd rather explicitly know what is happening

        val pm = packageManager
        val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager

        val permsHolder = PermsHolder(this)

        val installedAppsHolder = InstalledAppsHolder(pm)
        val iconPackCache = IconPackCache()
        val appIconsCache = IconCache(pm, installedAppsHolder, iconPackCache)
        val installedIconPacksHolder = InstalledIconPacksHolder(
            _pm = pm,
            _apps = installedAppsHolder
        )

        val lifecycleEventsHolder = LifecycleEventsHolder()
        val windowInsetsHolder = WindowInsetsHolder()
        val displayShapeHolder = DisplayShapeHolder()
        val systemUIModeHolder = SystemUIModeHolder(
            _uiModeManager = uiModeManager
        )

        val qynnToJSAPI = QYNNToJSAPI(
            _app = this,
            _perms = permsHolder,
            _insets = windowInsetsHolder,
            _lifecycleEventsHolder = lifecycleEventsHolder,
            _apps = installedAppsHolder,
            _systemUIMode = systemUIModeHolder,
        )

        val jsToQYNNAPI = JSToQYNNAPI(
            _app = this,
            _windowInsetsHolder = windowInsetsHolder,
            _displayShapeHolder = displayShapeHolder,
        )

        val qynnServer = QYNNServer(
            this,
            installedAppsHolder,
            _iconPacks = installedIconPacksHolder,
        )

        val consoleMessagesHolder = DevConsoleMessagesHolder()


        val mockExporter = MockExporter(
            installedAppsHolder,
            installedIconPacksHolder
        )

        val broadcastReceiver = QYNNLauncherBroadcastReceiver(installedAppsHolder)

        return QYNNServices(
            // system
            packageManager = pm,
            uiModeManager = uiModeManager,
            broadcastReceiver = broadcastReceiver,

            // state holders
            storagePermsHolder = permsHolder,
            systemUIModeHolder = systemUIModeHolder,
            windowInsetsHolder = windowInsetsHolder,
            lifecycleEventsHolder = lifecycleEventsHolder,
            displayShapeHolder = displayShapeHolder,

            // apps & icon packs
            installedAppsHolder = installedAppsHolder,
            installedIconPacksHolder = installedIconPacksHolder,
            iconPackCache = iconPackCache,
            iconCache = appIconsCache,
            mockExporter = mockExporter,

            // webview
            consoleMessagesHolder = consoleMessagesHolder,
            qynnServer = qynnServer,
            qynnToJSInterface = qynnToJSAPI,
            jsToQYNNInterface = jsToQYNNAPI,
        )
    }

    private fun startup()
    {
        ContextCompat.registerReceiver(
            this,
            services.broadcastReceiver,
            QYNNLauncherBroadcastReceiver.intentFilter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        services.iconPackCache.startup()
        services.installedIconPacksHolder.startup()
        services.iconCache.startup()
        services.installedAppsHolder.startup()
        services.qynnToJSInterface.startup()
    }
}