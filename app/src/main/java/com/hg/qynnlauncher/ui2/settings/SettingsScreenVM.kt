package com.hg.qynnlauncher.ui2.settings

import android.app.Application
import android.app.StatusBarManager
import android.graphics.drawable.Icon
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hg.qynnlauncher.QYNNLauncherApplication
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.services.QYNNServices
import com.hg.qynnlauncher.services.mockexport.MockExportProgressState
import com.hg.qynnlauncher.services.mockexport.MockExporter
import com.hg.qynnlauncher.services.perms.PermsHolder
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.ResettableQYNNSettings
import com.hg.qynnlauncher.services.settings2.resetQYNNSetting
import com.hg.qynnlauncher.services.settings2.setQYNNSetting
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.useQYNNSettingState
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerActions
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerMode
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerRealDirectory
import com.hg.qynnlauncher.ui2.dirpicker.DirectoryPickerState
import com.hg.qynnlauncher.ui2.progressdialog.MockExportProgressDialogActions
import com.hg.qynnlauncher.ui2.settings.sections.gestures.SettingsScreen2GestureCustomizationSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.gestures.SettingsScreen2GestureCustomizationSectionState
import com.hg.qynnlauncher.ui2.settings.sections.qynn.SettingsScreen2QYNNSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.qynn.SettingsScreen2QYNNSectionState
import com.hg.qynnlauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.development.SettingsScreen2DevelopmentSectionState
import com.hg.qynnlauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.overlays.SettingsScreen2OverlaysSectionState
import com.hg.qynnlauncher.ui2.settings.sections.project.ScreenLockingMethodOptions
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionState
import com.hg.qynnlauncher.ui2.settings.sections.project.SettingsScreen2ProjectSectionStateProjectInfo
import com.hg.qynnlauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.reset.SettingsScreen2ResetSectionState
import com.hg.qynnlauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionActions
import com.hg.qynnlauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionContent
import com.hg.qynnlauncher.ui2.settings.sections.wallpaper.SettingsScreen2WallpaperSectionState
import com.hg.qynnlauncher.utils.CurrentAndroidVersion
import com.hg.qynnlauncher.utils.QYNNLauncherApplication
import com.hg.qynnlauncher.utils.collectAsStateButInViewModel
import com.hg.qynnlauncher.utils.suspendTryOrShowErrorToast
import com.hg.qynnlauncher.utils.tryOrShowErrorToast
import com.hg.qynnlauncher.utils.tryStartWallpaperPickerActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import kotlin.test.assertNotNull

private val TAG = SettingsScreenVM::class.simpleName

class SettingsScreenVM(
    private val _app: QYNNLauncherApplication,
    private val _permsHolder: PermsHolder,
    private val _mockExporter: MockExporter,
) : ViewModel()
{
    private val _statusBarManager = if (CurrentAndroidVersion.supportsQSTilePrompt())
        _app.getSystemService(StatusBarManager::class.java)
    else
        null

    // SETTING STATES

    private val _isDeviceAdminEnabled by useQYNNSettingState(_app, QYNNSettings.isDeviceAdminEnabled)
    private val _isAccessibilityServiceEnabled by useQYNNSettingState(_app, QYNNSettings.isAccessibilityServiceEnabled)
    private val _isQSTileAdded by useQYNNSettingState(_app, QYNNSettings.isQSTileAdded)

    private val _currentProjDir by useQYNNSettingState(_app, QYNNSettings.currentProjDir)
    private val _lastMockExportDir by useQYNNSettingState(_app, QYNNSettings.lastMockExportDir)

    private val _theme by useQYNNSettingState(_app, QYNNSettings.theme)
    private val _allowProjectsToTurnScreenOff by useQYNNSettingState(_app, QYNNSettings.allowProjectsToTurnScreenOff)
    private val _drawSystemWallpaperBehindWebView by useQYNNSettingState(_app, QYNNSettings.drawSystemWallpaperBehindWebView)
    private val _statusBarAppearance by useQYNNSettingState(_app, QYNNSettings.statusBarAppearance)
    private val _navigationBarAppearance by useQYNNSettingState(_app, QYNNSettings.navigationBarAppearance)
    private val _drawWebViewOverscrollEffects by useQYNNSettingState(_app, QYNNSettings.drawWebViewOverscrollEffects)
    private val _showQYNNButton by useQYNNSettingState(_app, QYNNSettings.showQYNNButton)
    private val _showLaunchAppsWhenQYNNButtonCollapsed by useQYNNSettingState(_app, QYNNSettings.showLaunchAppsWhenQYNNButtonCollapsed)

    // Gesture Settings
    val edgeSensitivity by useQYNNSettingState(_app, QYNNSettings.edgeSensitivity)
    val backVelocity by useQYNNSettingState(_app, QYNNSettings.backVelocity)
    val recentsVelocity by useQYNNSettingState(_app, QYNNSettings.recentsVelocity)
    val homeDistance by useQYNNSettingState(_app, QYNNSettings.homeDistance)

    fun setEdgeSensitivity(value: Float) = updateSettings { setQYNNSetting(QYNNSettings.edgeSensitivity, value) }
    fun setBackVelocity(value: Float) = updateSettings { setQYNNSetting(QYNNSettings.backVelocity, value) }
    fun setRecentsVelocity(value: Float) = updateSettings { setQYNNSetting(QYNNSettings.recentsVelocity, value) }
    fun setHomeDistance(value: Float) = updateSettings { setQYNNSetting(QYNNSettings.homeDistance, value) }

    // PROJECT

    val projectSectionState = derivedStateOf()
    {
        val screenLockingMethod = when (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
        {
            true -> ScreenLockingMethodOptions.AccessibilityService
            false -> ScreenLockingMethodOptions.DeviceAdmin
        }

        SettingsScreen2ProjectSectionState(
            projectInfo = _currentProjDir?.run {
                SettingsScreen2ProjectSectionStateProjectInfo(
                    name = name
                )
            },
            hasStoragePerms = _permsHolder.hasStoragePermsState.value,
            allowProjectsToTurnScreenOff = _allowProjectsToTurnScreenOff,
            screenLockingMethod = screenLockingMethod,
            canQYNNTurnScreenOff = when (screenLockingMethod)
            {
                ScreenLockingMethodOptions.DeviceAdmin -> _isDeviceAdminEnabled
                ScreenLockingMethodOptions.AccessibilityService -> _isAccessibilityServiceEnabled
            }
        )
    }

    val projectSectionActions = SettingsScreen2ProjectSectionActions(
        changeProject = { openDirectoryPicker(DirectoryPickerMode.LoadProject) },
        changeAllowProjectsToTurnScreenOff = { updateSettings { setQYNNSetting(QYNNSettings.allowProjectsToTurnScreenOff, it) } },
    )


    // WALLPAPER

    val wallpaperSectionState = derivedStateOf()
    {
        SettingsScreen2WallpaperSectionState(
            drawSystemWallpaperBehindWebView = _drawSystemWallpaperBehindWebView,
        )
    }

    val wallpaperSectionActions = SettingsScreen2WallpaperSectionActions(
        changeSystemWallpaper = { _app.tryStartWallpaperPickerActivity() },
        changeDrawSystemWallpaperBehindWebView = { updateSettings { setQYNNSetting(QYNNSettings.drawSystemWallpaperBehindWebView, it) } }
    )


    // OVERLAYS

    val overlaysSectionState = derivedStateOf()
    {
        Log.d(TAG, "overlaysSectionState reevaluating")
        SettingsScreen2OverlaysSectionState(
            statusBarAppearance = _statusBarAppearance,
            navigationBarAppearance = _navigationBarAppearance,
            drawWebViewOverscrollEffects = _drawWebViewOverscrollEffects,
        )
    }

    val overlaysSectionActions = SettingsScreen2OverlaysSectionActions(
        changeStatusBarAppearance = {
            Log.d(TAG, "changeStatusBarAppearance called")
            updateSettings { setQYNNSetting(QYNNSettings.statusBarAppearance, it) }
        },
        changeNavigationBarAppearance = {
            Log.d(TAG, "changeNavigationBarAppearance called")
            updateSettings { setQYNNSetting(QYNNSettings.navigationBarAppearance, it) }
        },
        changeDrawWebViewOverscrollEffects = {
            Log.d(TAG, "changeDrawWebViewOverscrollEffects called: $it")
            updateSettings { setQYNNSetting(QYNNSettings.drawWebViewOverscrollEffects, it) }
        },
    )


    // QYNN

    val qynnSectionState = derivedStateOf()
    {
        SettingsScreen2QYNNSectionState(
            theme = _theme,
            showQYNNButton = _showQYNNButton,
            showLaunchAppsWhenQYNNButtonCollapsed = _showLaunchAppsWhenQYNNButtonCollapsed,
            isQSTileAdded = _isQSTileAdded,
            isQSTilePromptSupported = CurrentAndroidVersion.supportsQSTilePrompt()
        )
    }

    val qynnSectionActions = SettingsScreen2QYNNSectionActions(
        changeTheme = { updateSettings { setQYNNSetting(QYNNSettings.theme, it) } },
        changeShowQYNNButton = { updateSettings { setQYNNSetting(QYNNSettings.showQYNNButton, it) } },
        changeShowLaunchAppsWhenQYNNButtonCollapsed = { updateSettings { setQYNNSetting(QYNNSettings.showLaunchAppsWhenQYNNButtonCollapsed, it) } },
        requestQSTilePrompt = {
            if (CurrentAndroidVersion.supportsQSTilePrompt())
            {
                assertNotNull(_statusBarManager, "Current Android version supports QS tile prompt, but statusBarManager was null.")
                _statusBarManager.requestAddTileService(
                    _app.qsTileServiceComponentName,
                    "QYNN button",
                    Icon.createWithResource(_app, R.drawable.ic_qynn_white),
                    {},
                    {}
                )
            }
        },
    )

    val gestureCustomizationSectionState by derivedStateOf {
        SettingsScreen2GestureCustomizationSectionState(
            edgeSensitivity = edgeSensitivity,
            backVelocity = backVelocity,
            recentsVelocity = recentsVelocity,
            homeDistance = homeDistance,
        )
    }

    val gestureCustomizationSectionActions = SettingsScreen2GestureCustomizationSectionActions(
        changeEdgeSensitivity = ::setEdgeSensitivity,
        changeBackVelocity = ::setBackVelocity,
        changeRecentsVelocity = ::setRecentsVelocity,
        changeHomeDistance = ::setHomeDistance,
    )


    // DEVELOPMENT

    val developmentSectionState = derivedStateOf {
        SettingsScreen2DevelopmentSectionState(
            // TODO: disabled until apps, icons and icon packs are done loading?
            isExportDisabled = false,
        )
    }

    val developmentSectionActions = SettingsScreen2DevelopmentSectionActions(
        exportMockFolder = { openDirectoryPicker(DirectoryPickerMode.MockExport) }
    )


    // DIRECTORY PICKER

    private val _directoryPickerStateFlow = MutableStateFlow<DirectoryPickerState?>(null)
    val directoryPickerState = collectAsStateButInViewModel(_directoryPickerStateFlow)

    private var _mockExportJob: Job? = null

    private val _mockExportProgressStateFlow = MutableStateFlow<MockExportProgressState?>(null)
    val mockExportProgressState = collectAsStateButInViewModel(_mockExportProgressStateFlow)

    val mockExportProgressDialogActions = MockExportProgressDialogActions(
        dismiss = {
            _mockExportJob?.cancel()
            _mockExportProgressStateFlow.value = null
            _mockExportJob = null
        }
    )

    private fun currentProjDirOrDefault() = _currentProjDir.let {
        if (it?.canRead() == true)
            it
        else
            Environment.getExternalStorageDirectory()
    }

    private fun lastMockExportDirOrDefault() = _lastMockExportDir.let {
        if (it?.canRead() == true)
            it
        else
            currentProjDirOrDefault()
    }

    private fun observeStoragePermissionState()
    {
        viewModelScope.launch {
            _permsHolder.hasStoragePermsState.collectLatest { hasStoragePermission ->
                _directoryPickerStateFlow.value.let { currState ->
                    Log.d(TAG, "observeStoragePermissionState: _permsManager.hasStoragePermsState.collectLatest called, hasStoragePermission = $hasStoragePermission, currState = $currState")
                    if (hasStoragePermission)
                    {
                        if (currState is DirectoryPickerState.NoStoragePermission)
                        {
                            // permission granted while dialog open
                            Log.d(TAG, "observeStoragePermissionState: NoStoragePermission -> HasStoragePermission")
                            _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                                mode = currState.mode,
                                directory = currentProjDirOrDefault(),
                            )
                        }
                    }
                    else
                    {
                        if (currState is DirectoryPickerState.HasStoragePermission)
                        {
                            // permission revoked while dialog open
                            Log.d(TAG, "observeStoragePermissionState: HasStoragePermission -> NoStoragePermission")
                            _directoryPickerStateFlow.value = DirectoryPickerState.NoStoragePermission(currState.mode)
                        }
                    }
                }
            }
        }
    }

    private fun openDirectoryPicker(mode: DirectoryPickerMode)
    {
        if (_permsHolder.hasStoragePermsState.value)
        {
            _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                mode = mode,
                directory = when (mode)
                {
                    DirectoryPickerMode.LoadProject -> currentProjDirOrDefault()
                    DirectoryPickerMode.MockExport -> lastMockExportDirOrDefault()
                }
            )
        }
        else
        {
            _directoryPickerStateFlow.value = DirectoryPickerState.NoStoragePermission(mode)
        }
    }

    val directoryPickerActions = DirectoryPickerActions(
        dismiss = { _directoryPickerStateFlow.value = null },
        navigateToDirectory = {
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && it is DirectoryPickerRealDirectory && it.file.canRead())
                {
                    _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                        mode = currState.mode,
                        directory = it.file,
                    )
                }
            }
        },
        selectCurrentDirectory = {
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && currState.currentDirectory is DirectoryPickerRealDirectory)
                {
                    when (currState.mode)
                    {
                        DirectoryPickerMode.LoadProject ->
                        {
                            // hide directory picker
                            _directoryPickerStateFlow.value = null
                            updateSettings { setQYNNSetting(QYNNSettings.currentProjDir, currState.currentDirectory.file) }
                        }

                        DirectoryPickerMode.MockExport -> if (_mockExportJob == null)
                        {
                            // hide directory picker
                            _directoryPickerStateFlow.value = null
                            // start exporting
                            _mockExportProgressStateFlow.value = MockExportProgressState()

                            _mockExportJob = viewModelScope.launch {
                                _mockExporter.exportToDirectory(currState.currentDirectory.file, _mockExportProgressStateFlow)
                                _app.settingsDataStore.edit { it.setQYNNSetting(QYNNSettings.lastMockExportDir, currState.currentDirectory.file) }
                            }
                        }
                    }
                }
            }
        },
        requestFilterOrCreateDirectoryTextChange = { newText ->
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && currState.currentDirectory is DirectoryPickerRealDirectory)
                {
                    _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                        mode = currState.mode,
                        directory = currState.currentDirectory.file,
                        filterOrCreateDirectoryText = newText,
                    )
                }
            }
        },
        createSubdirectory = {
            _directoryPickerStateFlow.value.let { currState ->
                if (currState is DirectoryPickerState.HasStoragePermission && currState.currentDirectory is DirectoryPickerRealDirectory)
                {
                    if (currState.filterOrCreateDirectoryText.isNotBlank())
                    {
                        _app.tryOrShowErrorToast {
                            val newDir = File(currState.currentDirectory.file, currState.filterOrCreateDirectoryText)
                            newDir.mkdirs()

                            // navigate to created directory with a clear filter
                            _directoryPickerStateFlow.value = DirectoryPickerState.HasStoragePermission.fromDirectoryAndFilter(
                                mode = currState.mode,
                                directory = newDir,
                                filterOrCreateDirectoryText = "",
                            )
                        }
                    }

                }
            }
        },
    )

    // RESET SETTINGS

    private val _isSettingsResetInProgressState = mutableStateOf(false)

    val resetSectionActions = SettingsScreen2ResetSectionActions(
        onResetRequest = {
            val isResetInProgress = _isSettingsResetInProgressState.value
            if (!isResetInProgress)
            {
                viewModelScope.launch {
                    _isSettingsResetInProgressState.value = true

                    _app.suspendTryOrShowErrorToast {
                        _app.settingsDataStore.edit { prefs ->
                            ResettableQYNNSettings.forEach { prefs.resetQYNNSetting(it) }
                        }
                        Toast.makeText(_app, "Reset finished.", Toast.LENGTH_SHORT).show()
                    }

                    _isSettingsResetInProgressState.value = false
                }
            }
        },
    )

    val resetSectionState = derivedStateOf {
        SettingsScreen2ResetSectionState(
            isResetInProgress = _isSettingsResetInProgressState.value,
        )
    }


    // MISC ACTIONS

    val miscActions = SettingsScreen2MiscActions(
        permissionsChanged = { _permsHolder.notifyPermsMightHaveChanged() },
    )

    // utils

    private fun updateSettings(edits: MutablePreferences.() -> Unit)
    {
        viewModelScope.launch {
            _app.settingsDataStore.edit { it.edits() }
        }
    }

    init
    {
        observeStoragePermissionState()
    }

    companion object
    {
        fun from(context: Application, serviceProvider: QYNNServices): SettingsScreenVM
        {
            with(serviceProvider)
            {
                return SettingsScreenVM(
                    _app = context.QYNNLauncherApplication,
                    _permsHolder = storagePermsHolder,
                    _mockExporter = mockExporter,
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