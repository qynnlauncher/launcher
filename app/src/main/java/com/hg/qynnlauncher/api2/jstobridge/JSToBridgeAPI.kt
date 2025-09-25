package com.hg.qynnlauncher.api2.jstoqynn

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.UiModeManager
import android.app.WallpaperManager
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import com.hg.qynnlauncher.QYNNLauncherApplication
import com.hg.qynnlauncher.api2.server.QYNNServer
import com.hg.qynnlauncher.api2.server.endpoints.AppIconsEndpoint
import com.hg.qynnlauncher.api2.server.endpoints.IconPackContentEndpoint
import com.hg.qynnlauncher.api2.server.endpoints.IconPacksEndpoint
import com.hg.qynnlauncher.api2.server.getQYNNApiEndpointURL
import com.hg.qynnlauncher.api2.shared.QYNNButtonVisibilityStringOptions
import com.hg.qynnlauncher.api2.shared.QYNNThemeStringOptions
import com.hg.qynnlauncher.api2.shared.OverscrollEffectsStringOptions
import com.hg.qynnlauncher.api2.shared.SystemBarAppearanceStringOptions
import com.hg.qynnlauncher.api2.shared.SystemNightModeStringOptions
import com.hg.qynnlauncher.services.displayshape.DisplayShapeHolder
import com.hg.qynnlauncher.services.settings2.QYNNSetting
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.getIsQYNNAbleToLockTheScreen
import com.hg.qynnlauncher.services.settings2.setQYNNSetting
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.useQYNNSettingStateFlow
import com.hg.qynnlauncher.services.system.QYNNLauncherAccessibilityService
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsHolder
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsOptions
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsSnapshot
import com.hg.qynnlauncher.ui2.home.HomeScreen2VM
import com.hg.qynnlauncher.utils.CurrentAndroidVersion
import com.hg.qynnlauncher.utils.getIsSystemInNightMode
import com.hg.qynnlauncher.utils.launchApp
import com.hg.qynnlauncher.utils.messageOrDefault
import com.hg.qynnlauncher.utils.openAppInfo
import com.hg.qynnlauncher.utils.q
import com.hg.qynnlauncher.utils.requestAppUninstall
import com.hg.qynnlauncher.utils.showErrorToast
import com.hg.qynnlauncher.utils.startAndroidSettingsActivity
import com.hg.qynnlauncher.utils.startQYNNSettingsActivity
import com.hg.qynnlauncher.utils.startDevConsoleActivity
import com.hg.qynnlauncher.utils.startWallpaperPickerActivity
import com.hg.qynnlauncher.utils.toPx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

private const val TAG = "JSToQYNN"

class JSToQYNNAPI(
    private val _app: QYNNLauncherApplication,
    private val _windowInsetsHolder: WindowInsetsHolder,
    private val _displayShapeHolder: DisplayShapeHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _pm = _app.packageManager
    private val _wallman = _app.getSystemService(Context.WALLPAPER_SERVICE) as WallpaperManager
    private val _modeman = _app.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
    private val _dpman = _app.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    var webView: WebView? = null
    var homeScreenContext: Context? = null
    var homeScreenVM: HomeScreen2VM? = null


    // SETTING STATES

    private fun <TPreference, TResult> s(setting: QYNNSetting<TPreference, TResult>) = useQYNNSettingStateFlow(_app.settingsDataStore, _scope, setting)
    private val _isDeviceAdminEnabled = s(QYNNSettings.isDeviceAdminEnabled)
    private val _isAccessibilityServiceEnabled = s(QYNNSettings.isAccessibilityServiceEnabled)
    private val _theme = s(QYNNSettings.theme)
    private val _allowProjectsToTurnScreenOff = s(QYNNSettings.allowProjectsToTurnScreenOff)
    private val _statusBarAppearance = s(QYNNSettings.statusBarAppearance)
    private val _navigationBarAppearance = s(QYNNSettings.navigationBarAppearance)
    private val _showQYNNButton = s(QYNNSettings.showQYNNButton)
    private val _drawSystemWallpaperBehindWebView = s(QYNNSettings.drawSystemWallpaperBehindWebView)
    private val _drawWebViewOverscrollEffects = s(QYNNSettings.drawWebViewOverscrollEffects)

    private var _lastException: Exception? = null
        set(value)
        {
            field = value.also { Log.e(TAG, "Caught exception", value) }
        }


    // region system

    @JavascriptInterface
    fun getAndroidAPILevel() = Build.VERSION.SDK_INT


    @JavascriptInterface
    fun getQYNNVersionCode() = _pm
        .getPackageInfo(_app.packageName, 0)
        .run {
            if (CurrentAndroidVersion.supportsPackageInfoLongVersionCode())
                longVersionCode
            else
                @Suppress("DEPRECATION")
                versionCode.toLong()
        }

    @JavascriptInterface
    fun getQYNNVersionName(): String = _pm.getPackageInfo(_app.packageName, 0).versionName ?: ""


    @JavascriptInterface
    fun getLastErrorMessage() = _lastException?.messageOrDefault()

    // endregion


    // region fetch

    @JavascriptInterface
    fun getProjectURL() = QYNNServer.PROJECT_URL

    @JavascriptInterface
    fun getAppsURL() = getQYNNApiEndpointURL(QYNNServer.ENDPOINT_APPS)

    // endregion


    // region apps

    @JvmOverloads
    @JavascriptInterface
    fun requestAppUninstall(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { requestAppUninstall(packageName) }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenAppInfo(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { openAppInfo(packageName) }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestLaunchApp(packageName: String, showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { launchApp(packageName) }
    }

    // endregion


    // region icon packs

    @JavascriptInterface
    fun getIconPacksURL(includeItems: Boolean = false): String =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_ICON_PACKS,
            IconPacksEndpoint.QUERY_INCLUDE_ITEMS to includeItems,
        )

    @JavascriptInterface
    fun getIconPackInfoURL(iconPackPackageName: String, includeItems: Boolean = false) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_ICON_PACKS,
            IconPacksEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPacksEndpoint.QUERY_INCLUDE_ITEMS to includeItems,
        )

    @JavascriptInterface
    fun getIconPackAppFilterXMLURL(iconPackPackageName: String, includeItems: Boolean = false) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_ICON_PACKS,
            IconPacksEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPacksEndpoint.QUERY_INCLUDE_ITEMS to includeItems,
        )

    // endregion


    // region icons

    @JavascriptInterface
    fun getDefaultAppIconURL(packageName: String) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_APP_ICONS,
            AppIconsEndpoint.QUERY_PACKAGE_NAME to packageName,
        )

    @JvmOverloads
    @JavascriptInterface
    fun getAppIconURL(appPackageName: String, iconPackPackageName: String? = null) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_APP_ICONS,
            AppIconsEndpoint.QUERY_PACKAGE_NAME to appPackageName,
            AppIconsEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            AppIconsEndpoint.QUERY_NOT_FOUND_BEHAVIOR to AppIconsEndpoint.IconNotFoundBehaviors.Default,
        )

    @JavascriptInterface
    fun getIconPackAppIconURL(iconPackPackageName: String, appPackageName: String) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_APP_ICONS,
            AppIconsEndpoint.QUERY_PACKAGE_NAME to appPackageName,
            AppIconsEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            AppIconsEndpoint.QUERY_NOT_FOUND_BEHAVIOR to AppIconsEndpoint.IconNotFoundBehaviors.Error,
        )

    @JavascriptInterface
    fun getIconPackAppItemURL(iconPackPackageName: String, itemName: String) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_ICON_PACK_CONTENT,
            IconPackContentEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPackContentEndpoint.QUERY_ITEM_NAME to itemName,
        )


    @JavascriptInterface
    fun getIconPackDrawableURL(iconPackPackageName: String, drawableName: String) =
        getQYNNApiEndpointURL(
            QYNNServer.ENDPOINT_ICON_PACK_CONTENT,
            IconPackContentEndpoint.QUERY_ICON_PACK_PACKAGE_NAME to iconPackPackageName,
            IconPackContentEndpoint.QUERY_DRAWABLE_NAME to drawableName,
        )

    // endregion


    // region wallpaper

    @JavascriptInterface
    fun setWallpaperOffsetSteps(xStep: Float, yStep: Float)
    {
        _wallman.setWallpaperOffsetSteps(xStep, yStep)
    }

    @JavascriptInterface
    fun setWallpaperOffsets(x: Float, y: Float)
    {
        val token = webView?.applicationWindowToken

        if (token != null)
        {
            _wallman.setWallpaperOffsets(token, x, y)
        }
    }

    @JvmOverloads
    @JavascriptInterface
    fun sendWallpaperTap(x: Float, y: Float, z: Float = 0f)
    {
        val token = webView?.applicationWindowToken
        if (token != null)
        {
            val metrics = _app.resources.displayMetrics
            _wallman.sendWallpaperCommand(
                token,
                WallpaperManager.COMMAND_TAP,
                metrics.toPx(x).toInt(),
                metrics.toPx(y).toInt(),
                metrics.toPx(z).toInt(),
                Bundle.EMPTY
            )
        }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestChangeSystemWallpaper(showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { startWallpaperPickerActivity() }
    }

    // endregion


    // region qynn button

    @JavascriptInterface
    fun getQYNNButtonVisibility(): String
    {
        return QYNNButtonVisibilityStringOptions.fromShowQYNNButton(_showQYNNButton.value).rawValue
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetQYNNButtonVisibility(visibility: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setQYNNSetting(
                QYNNSettings.showQYNNButton,
                QYNNButtonVisibilityStringOptions.showQYNNButtonFromStringOrThrow(visibility),
            )
        }
    }

    // endregion


    // region draw system wallpaper behind webview

    @JavascriptInterface
    fun getDrawSystemWallpaperBehindWebViewEnabled(): Boolean
    {
        return _drawSystemWallpaperBehindWebView.value
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetDrawSystemWallpaperBehindWebViewEnabled(enable: Boolean, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setQYNNSetting(QYNNSettings.drawSystemWallpaperBehindWebView, enable)
        }
    }

    // endregion


    // region overscroll effects

    @JavascriptInterface
    fun getOverscrollEffects(): String
    {
        return OverscrollEffectsStringOptions.fromDrawWebViewOverscrollEffects(_drawWebViewOverscrollEffects.value).rawValue
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetOverscrollEffects(effects: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setQYNNSetting(
                QYNNSettings.drawWebViewOverscrollEffects,
                OverscrollEffectsStringOptions.drawWebViewOverscrollEffectsOrThrow(effects),
            )
        }
    }

    // endregion


    // region system night mode

    @JavascriptInterface
    fun getSystemNightMode(): String
    {
        return SystemNightModeStringOptions.fromUiModeManagerNightMode(_modeman.nightMode).rawValue
    }

    @JavascriptInterface
    fun resolveIsSystemInDarkTheme(): Boolean
    {
        return _app.getIsSystemInNightMode()
    }

    @JavascriptInterface
    fun getCanSetSystemNightMode(): Boolean
    {
        return ActivityCompat.checkSelfPermission(_app, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(_app, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("WrongConstant")
    @JvmOverloads
    @JavascriptInterface
    fun requestSetSystemNightMode(mode: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed)
        {
            Log.d(TAG, "requestSetSystemNightMode: $mode")

            val modeInt = when (mode)
            {
                "no" -> UiModeManager.MODE_NIGHT_NO
                "yes" -> UiModeManager.MODE_NIGHT_YES
                "auto" -> UiModeManager.MODE_NIGHT_AUTO

                "custom" -> if (CurrentAndroidVersion.supportsNightModeCustom())
                    UiModeManager.MODE_NIGHT_CUSTOM
                else
                    throw Exception("\"custom\" requires API level 30 (Android 11).")

                else -> throw Exception("Mode must be one of ${q("no")}, ${q("yes")}, ${q("auto")} or, from API level 30 (Android 11), ${q("custom")} (got ${q(mode)}).")
            }

            val hasModifyPerm = ActivityCompat.checkSelfPermission(_app, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED

            if (hasModifyPerm)
            {
                _modeman.nightMode = modeInt
            }
            else
            {
                val hasWriteSecureSettingsPerm = ActivityCompat.checkSelfPermission(_app, Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

                if (hasWriteSecureSettingsPerm)
                {
                    // shoutouts to joaomgcd (Tasker dev) for this workaround!
                    Settings.Secure.putInt(_app.contentResolver, "ui_night_mode", modeInt)
                    _modeman.enableCarMode(UiModeManager.ENABLE_CAR_MODE_ALLOW_SLEEP)
                    _modeman.disableCarMode(0)
                }
                else
                {
                    Toast
                        .makeText(
                            _app,
                            "To set system night mode, QYNN needs the WRITE_SECURE_SETTINGS permission, which can be granted via ADB. "
                                    + "Check the documentation for more information.",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }
        }
    }

    // endregion


    // region QYNN theme

    @JavascriptInterface
    fun getQYNNTheme(): String
    {
        return QYNNThemeStringOptions.fromQYNNTheme(_theme.value).rawValue
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetQYNNTheme(theme: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setQYNNSetting(
                QYNNSettings.theme,
                QYNNThemeStringOptions.qynnThemeFromStringOrThrow(theme),
            )
        }
    }

    // endregion


    // region system bars

    @JavascriptInterface
    fun getStatusBarAppearance(): String
    {
        return SystemBarAppearanceStringOptions.fromSystemBarAppearance(_statusBarAppearance.value).rawValue
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetStatusBarAppearance(appearance: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setQYNNSetting(
                QYNNSettings.statusBarAppearance,
                SystemBarAppearanceStringOptions.systemBarAppearanceFromStringOrThrow(appearance)
            )
        }
    }


    @JavascriptInterface
    fun getNavigationBarAppearance(): String
    {
        return SystemBarAppearanceStringOptions.fromSystemBarAppearance(_navigationBarAppearance.value).rawValue
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestSetNavigationBarAppearance(appearance: String, showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryEditPrefs(showToastIfFailed)
        {
            it.setQYNNSetting(
                QYNNSettings.navigationBarAppearance,
                SystemBarAppearanceStringOptions.systemBarAppearanceFromStringOrThrow(appearance)
            )
        }
    }

    // endregion


    // region screen locking

    @JavascriptInterface
    fun getCanLockScreen(): Boolean
    {
        return getIsQYNNAbleToLockTheScreen(
            isAccessibilityServiceEnabled = _isAccessibilityServiceEnabled.value,
            isDeviceAdminEnabled = _isDeviceAdminEnabled.value,
            allowProjectsToTurnScreenOff = _allowProjectsToTurnScreenOff.value,
        )
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestLockScreen(showToastIfFailed: Boolean = true): Boolean
    {
        return _app.tryRun(showToastIfFailed)
        {
            if (!CurrentAndroidVersion.supportsAccessiblityServiceScreenLock() && !_isDeviceAdminEnabled.value)
            {
                throw Exception("QYNN is not a device admin. Visit QYNN Settings to resolve the issue.")
            }
            else if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock() && !_isAccessibilityServiceEnabled.value)
            {
                throw Exception("QYNN Accessibility Service is not enabled. Visit QYNN Settings to resolve the issue.")
            }

            if (!_allowProjectsToTurnScreenOff.value)
            {
                throw Exception("Projects are not allowed to lock the screen. Visit QYNN Settings to resolve the issue.")
            }

            if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
            {
                if (QYNNLauncherAccessibilityService.instance == null)
                {
                    throw Exception("Cannot access the QYNN Accessibility Service instance. This is a bug.")
                }
                else
                {
                    QYNNLauncherAccessibilityService.instance?.performGlobalAction(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
                }
            }
            else
            {
                _dpman.lockNow()
            }
        }
    }

    // endregion


    // region misc actions

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenQYNNSettings(showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { startQYNNSettingsActivity() }
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenQYNNAppDrawer(showToastIfFailed: Boolean = true): Boolean
    {
        homeScreenVM?.openAppDrawer()
        return true
    }

    @JvmOverloads
    @JavascriptInterface
    fun requestOpenDeveloperConsole(showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { startDevConsoleActivity() }
    }

    // https://stackoverflow.com/a/15582509/6796433
    @JvmOverloads
    @SuppressLint("WrongConstant")
    @JavascriptInterface
    fun requestExpandNotificationShade(showToastIfFailed: Boolean = true): Boolean
    {
        try
        {
            val sbservice: Any = _app.getSystemService("statusbar")
            val statusbarManager = Class.forName("android.app.StatusBarManager")
            val showsb = statusbarManager.getMethod("expandNotificationsPanel")
            showsb.invoke(sbservice)

            return true
        }
        catch (ex: Exception)
        {
            _lastException = ex

            if (showToastIfFailed)
                _app.showErrorToast(ex)

            return false
        }
    }


    @JvmOverloads
    @JavascriptInterface
    fun requestOpenAndroidSettings(showToastIfFailed: Boolean = true): Boolean
    {
        return tryRunInHomescreenContext(showToastIfFailed) { startAndroidSettingsActivity() }
    }

    // endregion


    // region toast

    @JvmOverloads
    @JavascriptInterface
    fun showToast(message: String, long: Boolean = false)
    {
        Toast.makeText(_app, message, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
    }

    // endregion


    // region window insets & cutouts

    private fun getWindowInsetsJson(option: WindowInsetsOptions) = Json.encodeToString(WindowInsetsSnapshot.serializer(), _windowInsetsHolder.stateFlowMap[option]!!.value)

    @JavascriptInterface
    fun getStatusBarsWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.StatusBars)

    @JavascriptInterface
    fun getStatusBarsIgnoringVisibilityWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.StatusBarsIgnoringVisibility)


    @JavascriptInterface
    fun getNavigationBarsWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.NavigationBars)

    @JavascriptInterface
    fun getNavigationBarsIgnoringVisibilityWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.NavigationBarsIgnoringVisibility)


    @JavascriptInterface
    fun getCaptionBarWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.CaptionBar)

    @JavascriptInterface
    fun getCaptionBarIgnoringVisibilityWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.CaptionBarIgnoringVisibility)


    @JavascriptInterface
    fun getSystemBarsWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.SystemBars)

    @JavascriptInterface
    fun getSystemBarsIgnoringVisibilityWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.SystemBarsIgnoringVisibility)


    @JavascriptInterface
    fun getImeWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.Ime)

    @JavascriptInterface
    fun getImeAnimationSourceWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.ImeAnimationSource)

    @JavascriptInterface
    fun getImeAnimationTargetWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.ImeAnimationTarget)


    @JavascriptInterface
    fun getTappableElementWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.TappableElement)

    @JavascriptInterface
    fun getTappableElementIgnoringVisibilityWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.TappableElementIgnoringVisibility)


    @JavascriptInterface
    fun getSystemGesturesWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.SystemGestures)

    @JavascriptInterface
    fun getMandatorySystemGesturesWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.MandatorySystemGestures)


    @JavascriptInterface
    fun getDisplayCutoutWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.DisplayCutout)

    @JavascriptInterface
    fun getWaterfallWindowInsets() = getWindowInsetsJson(WindowInsetsOptions.Waterfall)


    @JavascriptInterface
    fun getDisplayCutoutPath() = _displayShapeHolder.displayCutoutPath

    @JavascriptInterface
    fun getDisplayShapePath() = _displayShapeHolder.displayShapePath

    // endregion


    // region helpers

    private fun Context.tryRun(showToastIfFailed: Boolean, f: Context.() -> Unit): Boolean
    {
        return try
        {
            f()
            true
        }
        catch (ex: Exception)
        {
            if (showToastIfFailed)
                showErrorToast(ex)

            _lastException = ex

            false
        }
    }

    private fun tryRunInHomescreenContext(showToastIfFailed: Boolean, f: Context.() -> Unit): Boolean
    {
        return when(val context = homeScreenContext)
        {
            null -> false.also { if (showToastIfFailed) _app.showErrorToast("homeScreenContext is null") }
            else -> context.tryRun(showToastIfFailed, f)
        }
    }

    private fun Context.tryEditPrefs(showToastIfFailed: Boolean, f: (MutablePreferences) -> Unit): Boolean
    {
        return tryRun(showToastIfFailed)
        {
            runBlocking {
                settingsDataStore.edit(f)
            }
        }
    }

    // endregion
}