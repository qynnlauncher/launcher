package com.hg.qynnlauncher.ui2.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.webkit.WebView
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hg.qynnlauncher.QYNNLauncherApplication
import com.hg.qynnlauncher.api2.jstoqynn.JSToQYNNAPI
import com.hg.qynnlauncher.api2.qynntojs.QYNNToJSAPI
import com.hg.qynnlauncher.api2.server.QYNNServer
import com.hg.qynnlauncher.api2.webview.QYNNWebChromeClient
import com.hg.qynnlauncher.api2.webview.QYNNWebViewClient
import com.hg.qynnlauncher.services.QYNNServices
import com.hg.qynnlauncher.services.devconsole.DevConsoleMessagesHolder
import com.hg.qynnlauncher.services.displayshape.DisplayShapeHolder
import com.hg.qynnlauncher.services.lifecycleevents.LifecycleEventsHolder
import com.hg.qynnlauncher.services.perms.PermsHolder
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.setQYNNSetting
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.useQYNNSettingState
import com.hg.qynnlauncher.services.uimode.SystemUIModeHolder
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsHolder
import com.hg.qynnlauncher.ui2.home.composables.onQYNNWebViewCreated
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenuActions
import com.hg.qynnlauncher.ui2.home.qynnmenu.QYNNMenuState
import com.hg.qynnlauncher.utils.QYNNLauncherApplication
import com.hg.qynnlauncher.utils.collectAsStateButInViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HomeScreen2VM"

@OptIn(ExperimentalMaterialApi::class)
class HomeScreen2VM(
    private val _app: QYNNLauncherApplication,
    private val _permsHolder: PermsHolder,
    private val _qynnServer: QYNNServer,
    private val _systemUIModeHolder: SystemUIModeHolder,
    private val _consoleMessages: DevConsoleMessagesHolder,
    private val _jsToQYNNInterface: JSToQYNNAPI,
    private val _qynnToJSInterface: QYNNToJSAPI,
    private val _lifecycleEventsHolder: LifecycleEventsHolder,
    private val _windowInsetsHolder: WindowInsetsHolder,
    private val _displayShapeHolder: DisplayShapeHolder,
) : ViewModel()
{
    // Sheet State
    private val _appDrawerSheetState = MutableStateFlow(ModalBottomSheetValue.Hidden)
    val appDrawerSheetState = _appDrawerSheetState.asStateFlow()

    fun openAppDrawer() {
        viewModelScope.launch {
            _appDrawerSheetState.value = ModalBottomSheetValue.Expanded
        }
    }

    fun onAppDrawerSheetStateChange(newValue: ModalBottomSheetValue) {
        _appDrawerSheetState.value = newValue
    }

    fun hideAppDrawer() {
        viewModelScope.launch {
            _appDrawerSheetState.value = ModalBottomSheetValue.Hidden
        }
    }

    // SETTINGS STATE

    private val _currentProjDir by useQYNNSettingState(_app, QYNNSettings.currentProjDir)
    private val _drawSystemWallpaperBehindWebView by useQYNNSettingState(_app, QYNNSettings.drawSystemWallpaperBehindWebView)
    private val _statusBarAppearance by useQYNNSettingState(_app, QYNNSettings.statusBarAppearance)
    private val _navigationBarAppearance by useQYNNSettingState(_app, QYNNSettings.navigationBarAppearance)
    private val _drawWebViewOverscrollEffects = useQYNNSettingState(_app, QYNNSettings.drawWebViewOverscrollEffects)
    private val _showQYNNButton by useQYNNSettingState(_app, QYNNSettings.showQYNNButton)
    private val _showLaunchAppsWhenQYNNButtonCollapsed by useQYNNSettingState(_app, QYNNSettings.showLaunchAppsWhenQYNNButtonCollapsed)

    // we store the webview because we need to be able to refresh it
    @SuppressLint("StaticFieldLeak")
    private var webView: WebView? = null

    val qynnMenuActions = QYNNMenuActions(
        onWebViewRefreshRequest = { webView?.reload() },
        onHideQYNNButtonRequest = {
            viewModelScope.launch {
                _app.settingsDataStore.edit {
                    it.setQYNNSetting(QYNNSettings.showQYNNButton, false)
                }
            }
        },
        onRequestIsExpandedChange = { _qynnMenuIsExpandedStateFlow.value = it },
        onAppDrawerButtonPress = { openAppDrawer() }
    )

    val systemUIState = derivedStateOf {
        HomeScreenSystemUIState(
            drawSystemWallpaperBehindWebView = _drawSystemWallpaperBehindWebView,
            statusBarAppearance = _statusBarAppearance,
            navigationBarAppearance = _navigationBarAppearance,
        )
    }

    private val _isQYNNServerReadyToServeState = collectAsStateButInViewModel(_qynnServer.isReadyToServe, false)
    val projectState = derivedStateOf {

        val hasStoragePerms = _permsHolder.hasStoragePermsState.value
        val projDir = _currentProjDir

        if (!hasStoragePerms && projDir == null)
            IHomeScreenProjectState.FirstTimeLaunch

        else if (!hasStoragePerms)
            IHomeScreenProjectState.NoStoragePerm

        else if (projDir == null)
            IHomeScreenProjectState.NoProjectLoaded

        else if (!_isQYNNServerReadyToServeState.value)
            IHomeScreenProjectState.Initializing

        else
            IHomeScreenProjectState.ProjectLoaded(projDir)
    }

    private val _qynnMenuIsExpandedStateFlow = MutableStateFlow(false)
    private val _qynnMenuIsExpandedState = collectAsStateButInViewModel(_qynnMenuIsExpandedStateFlow)

    val qynnMenuState = derivedStateOf {
        QYNNMenuState(
            isShown = _showQYNNButton,
            isExpanded = _qynnMenuIsExpandedState.value,
            showAppDrawerButtonWhenCollapsed = _showLaunchAppsWhenQYNNButtonCollapsed,
        )
    }

    val webViewDeps = QYNNWebViewDeps(
        webViewClient = QYNNWebViewClient(_qynnServer),
        chromeClient = QYNNWebChromeClient(_consoleMessages),
        onCreated = {
            onQYNNWebViewCreated(it, _jsToQYNNInterface)
            webView = it
            _qynnToJSInterface.webView = it
            _jsToQYNNInterface.webView = it
        },
        onDispose = {
            webView = null
            _qynnToJSInterface.webView = null
            _jsToQYNNInterface.webView = null
        },
        drawOverscrollEffects = _drawWebViewOverscrollEffects
    )

    init {
        _jsToQYNNInterface.homeScreenVM = this
    }

    fun afterCreate(context: Context)
    {
        _jsToQYNNInterface.homeScreenContext = context
    }

    fun beforePause()
    {
        _lifecycleEventsHolder.notifyHomeScreenPaused()
    }

    fun onNewIntent()
    {
        _lifecycleEventsHolder.notifyHomeScreenReceivedNewIntent()
    }

    fun afterResume()
    {
        _lifecycleEventsHolder.notifyHomeScreenResumed()
        _permsHolder.notifyPermsMightHaveChanged()
    }

    fun onConfigurationChanged()
    {
        _systemUIModeHolder.onConfigurationChanged()
    }

    fun beforeDestroy()
    {
        _jsToQYNNInterface.homeScreenContext = null
    }

    val observerCallbacks = HomeScreenObserverCallbacks(
        onWindowInsetsChanged = { option, snapshot ->
            _windowInsetsHolder.notifyWindowInsetsChanged(option, snapshot)
        },
        onDisplayShapePathChanged = {
            _displayShapeHolder.notifyDisplayShapePathChanged(it)
        },
        onCutoutPathChanged = {
            _displayShapeHolder.notifyDisplayCutoutPathChanged(it)
        }
    )


    companion object
    {
        fun from(context: Application, serviceProvider: QYNNServices): HomeScreen2VM
        {
            with(serviceProvider)
            {
                return HomeScreen2VM(
                    _app = context.QYNNLauncherApplication,
                    _permsHolder = storagePermsHolder,
                    _qynnServer = qynnServer,
                    _consoleMessages = consoleMessagesHolder,
                    _jsToQYNNInterface = jsToQYNNInterface,
                    _qynnToJSInterface = qynnToJSInterface,
                    _windowInsetsHolder = windowInsetsHolder,
                    _lifecycleEventsHolder = lifecycleEventsHolder,
                    _displayShapeHolder = displayShapeHolder,
                    _systemUIModeHolder = systemUIModeHolder,
                )
            }
        }

        // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as QYNNLauncherApplication
                from(app, app.services)
            }
        }
    }
}