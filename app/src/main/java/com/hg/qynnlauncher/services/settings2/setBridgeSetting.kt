package com.hg.qynnlauncher.services.settings2

import androidx.datastore.preferences.core.MutablePreferences

fun <TPreference, TResult> MutablePreferences.setQYNNSetting(
    qynnSetting: QYNNSetting<TPreference, TResult>,
    newValue: TResult,
)
{
    qynnSetting.write(newValue).let {
        if (it == null)
            this.remove(qynnSetting.key)
        else
            this[qynnSetting.key] = it
    }
}