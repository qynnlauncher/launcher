package com.hg.qynnlauncher.api2.qynntojs

import android.content.Context
import android.util.Log
import android.webkit.WebView
import com.hg.qynnlauncher.api2.qynntojs.events.apps.AppChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.apps.AppInstalledEvent
import com.hg.qynnlauncher.api2.qynntojs.events.apps.AppRemovedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.lifecycle.AfterResumeEvent
import com.hg.qynnlauncher.api2.qynntojs.events.lifecycle.BeforePauseEvent
import com.hg.qynnlauncher.api2.qynntojs.events.lifecycle.NewIntentEvent
import com.hg.qynnlauncher.api2.qynntojs.events.perms.CanLockScreenChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.perms.CanRequestSystemNightModeChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.settings.QYNNButtonVisibilityChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.settings.QYNNThemeChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.settings.DrawSystemWallpaperBehindWebViewChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.settings.NavigationBarAppearanceChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.settings.OverscrollEffectsChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.settings.StatusBarAppearanceChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.systemuimode.SystemNightModeChangedEvent
import com.hg.qynnlauncher.api2.qynntojs.events.windowinsets.WindowInsetsChangedEvent
import com.hg.qynnlauncher.api2.shared.QYNNButtonVisibilityStringOptions
import com.hg.qynnlauncher.api2.shared.QYNNThemeStringOptions
import com.hg.qynnlauncher.api2.shared.OverscrollEffectsStringOptions
import com.hg.qynnlauncher.api2.shared.SystemBarAppearanceStringOptions
import com.hg.qynnlauncher.services.apps.InstalledAppListChangeEvent
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder
import com.hg.qynnlauncher.services.lifecycleevents.LifecycleEventsHolder
import com.hg.qynnlauncher.services.perms.PermsHolder
import com.hg.qynnlauncher.services.settings2.QYNNSetting
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.useQYNNSettingStateFlow
import com.hg.qynnlauncher.services.uimode.SystemUIModeHolder
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private val TAG = QYNNToJSAPI::class.simpleName

class QYNNToJSAPI(
    private val _app: Context,
    private val _apps: InstalledAppsHolder,
    private val _perms: PermsHolder,
    private val _insets: WindowInsetsHolder,
    private val _systemUIMode: SystemUIModeHolder,
    private val _lifecycleEventsHolder: LifecycleEventsHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    var webView: WebView? = null

    private fun sendQYNNEvent(model: IQYNNEventModel)
    {
        try
        {
            when (val wv = webView)
            {
                null -> Log.w(TAG, "sendQYNNEvent(${model.name}): webView is null, ignoring event")
                else ->
                {
                    wv.evaluateJavascript("if (typeof onQYNNEvent === 'function') onQYNNEvent(${model.getJson()})") { }
                    // this leads to a lot of log spam from windowinsets changes
//                    Log.w(TAG, "sendQYNNEvent(${model.name}): OK")
                }
            }
        }
        catch (ex: Exception)
        {
            Log.e(QYNNToJSAPI::class.simpleName, "sendQYNNEvent(${model.name}): failure", ex)
        }
    }

    private fun startCollectingEvents() = _scope.launch {

        Log.d(TAG, "startCollectingEvents")

        with(_apps)
        {
            Log.d(TAG, "appListChangeEventFlow before onCollect")
            onCollect(appListChangeEventFlow) {
                Log.d(TAG, "appListChangeEventFlow collected: $it")
                when (it)
                {
                    is InstalledAppListChangeEvent.Added ->
                    {
                        if (!it.isFromInitialLoad)
                            AppInstalledEvent(it.newApp.toSerializable())
                        else
                            null
                    }

                    is InstalledAppListChangeEvent.Changed -> AppChangedEvent(it.newApp.toSerializable())
                    is InstalledAppListChangeEvent.Removed -> AppRemovedEvent(it.packageName)
                }
            }
        }

        with(QYNNSettings)
        {
            onCollectSetting(showQYNNButton) { QYNNButtonVisibilityChangedEvent(QYNNButtonVisibilityStringOptions.fromShowQYNNButton(it)) }
            onCollectSetting(drawSystemWallpaperBehindWebView) { DrawSystemWallpaperBehindWebViewChangedEvent(it) }
            onCollectSetting(drawWebViewOverscrollEffects) { OverscrollEffectsChangedEvent(OverscrollEffectsStringOptions.fromDrawWebViewOverscrollEffects(it)) }
            onCollectSetting(theme) { QYNNThemeChangedEvent(QYNNThemeStringOptions.fromQYNNTheme(it)) }
            onCollectSetting(statusBarAppearance) { StatusBarAppearanceChangedEvent(SystemBarAppearanceStringOptions.fromSystemBarAppearance(it)) }
            onCollectSetting(navigationBarAppearance) { NavigationBarAppearanceChangedEvent(SystemBarAppearanceStringOptions.fromSystemBarAppearance(it)) }
        }

        with(_perms)
        {
            onCollect(canSetSystemNightModeState) { CanRequestSystemNightModeChangedEvent(it) }
            onCollect(canProjectsLockScreen) { CanLockScreenChangedEvent(it) }
        }

        with(_systemUIMode)
        {
            onCollect(systemNightMode) { SystemNightModeChangedEvent(it) }
        }

        with(_insets)
        {
            stateFlowMap.forEach { (option, stateFlow) ->
                launch {
                    stateFlow.collect { snapshot ->
                        sendQYNNEvent(WindowInsetsChangedEvent.fromSnapshot(option, snapshot))
                    }
                }
            }
        }

        with(_lifecycleEventsHolder)
        {
            onCollect(homeScreenBeforePause) { BeforePauseEvent() }
            onCollect(homeScreenNewIntent) { NewIntentEvent() }
            onCollect(homeScreenAfterResume) { AfterResumeEvent() }
        }
    }

    private fun <T> CoroutineScope.onCollect(flow: Flow<T>, newValueToEvent: (newValue: T) -> QYNNEventModel?)
    {
        launch {
            flow.collect {
                newValueToEvent(it)?.let { ev ->
                    sendQYNNEvent(ev)
                }
            }
        }
    }

    private fun <TPreference, TResult> CoroutineScope.onCollectSetting(
        setting: QYNNSetting<TPreference, TResult>,
        newValueToEvent: (newValue: TResult) -> QYNNEventModel,
    )
    {
        val flow = useQYNNSettingStateFlow(_app.settingsDataStore, _scope, setting)
        launch {
            flow.collect {
                sendQYNNEvent(newValueToEvent(it))
            }
        }
    }

    fun startup()
    {
        startCollectingEvents()
    }
}