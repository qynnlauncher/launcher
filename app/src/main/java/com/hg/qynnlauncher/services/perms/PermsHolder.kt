package com.hg.qynnlauncher.services.perms

import android.content.Context
import android.util.Log
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.useQYNNSettingStateFlow
import com.hg.qynnlauncher.utils.CurrentAndroidVersion
import com.hg.qynnlauncher.utils.checkCanSetSystemNightMode
import com.hg.qynnlauncher.utils.checkStoragePerms
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private val TAG = PermsHolder::class.simpleName

class PermsHolder(
    private val _context: Context,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main)

    private val _hasStoragePermsState = MutableStateFlow(_context.checkStoragePerms())
    val hasStoragePermsState = _hasStoragePermsState.asStateFlow()

    private val _canSetSystemNightModeState = MutableStateFlow(_context.checkCanSetSystemNightMode())
    val canSetSystemNightModeState = _canSetSystemNightModeState.asStateFlow()

    private val _isAccessibilityServiceEnabled = useQYNNSettingStateFlow(_context.settingsDataStore, _scope, QYNNSettings.isAccessibilityServiceEnabled)
    private val _isDeviceAdminEnabled = useQYNNSettingStateFlow(_context.settingsDataStore, _scope, QYNNSettings.isDeviceAdminEnabled)
    private val _allowProjectsToTurnScreenOff = useQYNNSettingStateFlow(_context.settingsDataStore, _scope, QYNNSettings.allowProjectsToTurnScreenOff)

    private fun _getCanProjectsLockScreen(acc: Boolean, adm: Boolean, allow: Boolean): Boolean
    {
        return if (CurrentAndroidVersion.supportsAccessiblityServiceScreenLock())
            acc && allow
        else
            adm && allow
    }

    val canProjectsLockScreen = combine(
        _isAccessibilityServiceEnabled,
        _isDeviceAdminEnabled,
        _allowProjectsToTurnScreenOff
    )
    { acc, adm, allow -> _getCanProjectsLockScreen(acc, adm, allow) }
        .stateIn(
            _scope,
            SharingStarted.Eagerly,
            _getCanProjectsLockScreen(
                _isAccessibilityServiceEnabled.value,
                _isDeviceAdminEnabled.value,
                _allowProjectsToTurnScreenOff.value
            )
        )


    // intended to be called from onResume() - there is no API to listen for permission changes, so checks in onResume it is
    fun notifyPermsMightHaveChanged()
    {
        val hasStoragePerms = _context.checkStoragePerms()
        val canSetSystemNightMode = _context.checkCanSetSystemNightMode()
        Log.d(TAG, "notifyPermsMightHaveChanged: hasStoragePerms = $hasStoragePerms, canSetSystemNightMode = $canSetSystemNightMode")
        _hasStoragePermsState.value = hasStoragePerms
        _canSetSystemNightModeState.value = canSetSystemNightMode
    }
}