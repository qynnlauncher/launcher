package com.hg.qynnlauncher.services.settings2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.hg.qynnlauncher.utils.collectAsStateButInViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

fun <TPreference, TResult> useQYNNSettingFlow(
    dataStore: DataStore<Preferences>,
    qynnSetting: QYNNSetting<TPreference, TResult>,
): Flow<TResult>
{
    return dataStore.data.map { qynnSetting.read(it[qynnSetting.key]) }
}

fun <TPreference, TResult> useQYNNSettingStateFlow(
    dataStore: DataStore<Preferences>,
    coroutineScope: CoroutineScope,
    qynnSetting: QYNNSetting<TPreference, TResult>,
): StateFlow<TResult>
{
    return useQYNNSettingFlow(dataStore, qynnSetting)
        .stateIn(
            coroutineScope,
            SharingStarted.Eagerly,
            qynnSetting.read(null)
        )
}

fun <TPreference, TResult> ViewModel.useQYNNSettingStateFlow(
    context: Context,
    qynnSetting: QYNNSetting<TPreference, TResult>,
): StateFlow<TResult>
{
    return useQYNNSettingStateFlow(
        dataStore = context.settingsDataStore,
        coroutineScope = viewModelScope,
        qynnSetting = qynnSetting,
    )
}

fun <TPreference, TResult> ViewModel.useQYNNSettingState(
    context: Context,
    qynnSetting: QYNNSetting<TPreference, TResult>,
): State<TResult>
{
    return collectAsStateButInViewModel(
        useQYNNSettingStateFlow(
            dataStore = context.settingsDataStore,
            coroutineScope = viewModelScope,
            qynnSetting = qynnSetting,
        )
    )
}

@Composable
fun <TPreference, TResult> rememberQYNNSettingState(
    qynnSetting: QYNNSetting<TPreference, TResult>,
): State<TResult>
{
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val stateFlow = remember {
        useQYNNSettingStateFlow(
            dataStore = context.settingsDataStore,
            coroutineScope,
            qynnSetting
        )
    }
    return stateFlow.collectAsStateWithLifecycle()
}