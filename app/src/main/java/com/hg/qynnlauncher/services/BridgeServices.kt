package com.hg.qynnlauncher.services

import android.app.UiModeManager
import android.content.pm.PackageManager
import com.hg.qynnlauncher.api2.qynntojs.QYNNToJSAPI
import com.hg.qynnlauncher.api2.jstoqynn.JSToQYNNAPI
import com.hg.qynnlauncher.api2.server.QYNNServer
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder
import com.hg.qynnlauncher.services.devconsole.DevConsoleMessagesHolder
import com.hg.qynnlauncher.services.displayshape.DisplayShapeHolder
import com.hg.qynnlauncher.services.iconcache.IconCache
import com.hg.qynnlauncher.services.iconpackcache.IconPackCache
import com.hg.qynnlauncher.services.iconpackcache.InstalledIconPacksHolder
import com.hg.qynnlauncher.services.lifecycleevents.LifecycleEventsHolder
import com.hg.qynnlauncher.services.mockexport.MockExporter
import com.hg.qynnlauncher.services.perms.PermsHolder
import com.hg.qynnlauncher.services.system.QYNNLauncherBroadcastReceiver
import com.hg.qynnlauncher.services.uimode.SystemUIModeHolder
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsHolder

data class QYNNServices(
    // system
    val packageManager: PackageManager,
    val uiModeManager: UiModeManager,
    val broadcastReceiver: QYNNLauncherBroadcastReceiver,

    // state holders
    val storagePermsHolder: PermsHolder,
    val systemUIModeHolder: SystemUIModeHolder,
    val windowInsetsHolder: WindowInsetsHolder,
    val lifecycleEventsHolder: LifecycleEventsHolder,
    val displayShapeHolder: DisplayShapeHolder,

// apps & icon packs
    val installedAppsHolder: InstalledAppsHolder,
    val installedIconPacksHolder: InstalledIconPacksHolder,
    val iconPackCache: IconPackCache,
    val iconCache: IconCache,
    val mockExporter: MockExporter,

    // webview
    val consoleMessagesHolder: DevConsoleMessagesHolder,
    val qynnServer: QYNNServer,
    val qynnToJSInterface: QYNNToJSAPI,
    val jsToQYNNInterface: JSToQYNNAPI,
)
