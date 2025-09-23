package com.hg.qynnlauncher.services.settings2

import androidx.datastore.preferences.core.MutablePreferences

fun <TPreference, TResult> MutablePreferences.resetQYNNSetting(
    qynnSetting: QYNNSetting<TPreference, TResult>,
)
{
    this.remove(qynnSetting.key)
}