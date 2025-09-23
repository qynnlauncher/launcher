package com.hg.qynnlauncher.ui2.appdrawer

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hg.qynnlauncher.QYNNLauncherApplication
import com.hg.qynnlauncher.services.QYNNServices
import com.hg.qynnlauncher.services.apps.InstalledApp
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder
import com.hg.qynnlauncher.services.iconcache.IconCache
import com.hg.qynnlauncher.services.iconpacks.IconPack

class AppDrawerVM(
    private val _apps: InstalledAppsHolder,
    private val _iconCache: IconCache,
) : ViewModel()
{
    private val _appList = mutableStateOf(_apps.packageNameToInstalledAppMap.values.toList())

    private val _searchString = mutableStateOf("")
    val searchString = _searchString as State<String>

    fun updateSearchStringRequest(newSearchString: String)
    {
        _searchString.value = newSearchString
    }

    val filteredApps = derivedStateOf {
        val s = InstalledApp.simplifyLabel(_searchString.value)
        _appList.value
            .filter { it.labelSimplified.contains(s) || it.packageName.contains(s) }
            .sortedBy { it.labelSimplified }
    }

    suspend fun getIcon(iconPack: IconPack?, app: InstalledApp): ImageBitmap
    {
        return _iconCache.getIcon(null, app.packageName, app.lastModifiedNanoTime)
    }

    companion object
    {
        fun from(context: Application, serviceProvider: QYNNServices): AppDrawerVM
        {
            with(serviceProvider)
            {
                return AppDrawerVM(
                    _apps = installedAppsHolder,
                    _iconCache = iconCache,
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