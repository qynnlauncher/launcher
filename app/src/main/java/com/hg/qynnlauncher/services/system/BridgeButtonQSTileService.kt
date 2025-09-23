package com.hg.qynnlauncher.services.system

import android.content.ComponentName
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.datastore.preferences.core.edit
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.setQYNNSetting
import com.hg.qynnlauncher.services.settings2.useQYNNSettingStateFlow
import com.hg.qynnlauncher.utils.CurrentAndroidVersion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class QYNNButtonQSTileService : TileService()
{
    private var _job = SupervisorJob()
    private var _scope = CoroutineScope(Dispatchers.Main)
    private var _isListening = false
    private var _shouldShowActiveState = false

    private val _showQYNNButton = useQYNNSettingStateFlow(
        dataStore = settingsDataStore,
        coroutineScope = _scope,
        qynnSetting = QYNNSettings.showQYNNButton,
    )

    override fun onCreate()
    {
        _scope.launch {
            _showQYNNButton.collectLatest { showQYNNButton ->
                _shouldShowActiveState = showQYNNButton
                if (_isListening)
                {
                    updateTileIfListening()
                }
                else
                {
                    try
                    {
                        requestListeningState(applicationContext, ComponentName(applicationContext, QYNNButtonQSTileService::class.java))
                    }
                    catch (_: java.lang.Exception)
                    {

                    }
                }
            }
        }
    }

    override fun onTileAdded()
    {
        updateTileIsAdded(true)
    }

    private fun updateTileIsAdded(isAdded: Boolean)
    {
        _scope.launch {
            applicationContext.settingsDataStore.edit { prefs ->
                prefs.setQYNNSetting(QYNNSettings.isQSTileAdded, isAdded)
            }
        }
    }

    override fun onStartListening()
    {
        _isListening = true
        updateTileIfListening()
    }

    private fun updateTileIfListening()
    {
        if (!_isListening) return

        if (_shouldShowActiveState)
        {
            qsTile.state = Tile.STATE_ACTIVE

            if (CurrentAndroidVersion.supportsQSTileSubtitle())
                qsTile.subtitle = "Shown"
        }
        else
        {
            qsTile.state = Tile.STATE_INACTIVE

            if (CurrentAndroidVersion.supportsQSTileSubtitle())
                qsTile.subtitle = "Hidden"
        }

        qsTile.updateTile()
    }

    override fun onClick()
    {
        val showButton = qsTile.state != Tile.STATE_ACTIVE

        runBlocking {
            applicationContext.settingsDataStore.edit { prefs ->
                prefs.setQYNNSetting(QYNNSettings.showQYNNButton, showButton)
            }
        }
    }

    override fun onStopListening()
    {
        _isListening = false
    }

    override fun onDestroy()
    {
        _job.cancel()
    }

    override fun onTileRemoved()
    {
        updateTileIsAdded(false)
    }
}