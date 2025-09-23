package com.hg.qynnlauncher.services.settings2

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hg.qynnlauncher.utils.RawRepresentable
import com.hg.qynnlauncher.utils.intToEnumOrDefault
import java.io.File

data class QYNNSetting<TPreference, TResult>(
    val key: Preferences.Key<TPreference>,
    val resettable: Boolean,
    val displayName: String = key.name,
    val read: (value: TPreference?) -> TResult,
    val write: (value: TResult) -> TPreference?,
)
{
    companion object
    {
        fun systemBool(
            key: String,
            defaultValue: Boolean = false,
        ): QYNNSetting<Boolean, Boolean>
        {
            return QYNNSetting(
                booleanPreferencesKey("SettingsState.$key"),
                resettable = false,
                read = { it ?: defaultValue },
                write = { it },
            )
        }


        fun bool(
            key: String,
            defaultValue: Boolean = false,
            displayName: String = key,
        ): QYNNSetting<Boolean, Boolean>
        {
            return QYNNSetting(
                booleanPreferencesKey("SettingsState.$key"),
                resettable = true,
                displayName = displayName,
                read = { it ?: defaultValue },
                write = { it },
            )
        }


        fun file(
            key: String,
        ): QYNNSetting<String, File?>
        {
            return QYNNSetting(
                stringPreferencesKey("SettingsState.$key"),
                resettable = true,
                read = { it?.let { File(it) } },
                write = { it?.canonicalPath },
            )
        }


        inline fun <reified TEnum> enum(
            key: String,
            defaultValue: TEnum,
            displayName: String = key,
        )
                : QYNNSetting<Int, TEnum>
                where TEnum : Enum<TEnum>, TEnum : RawRepresentable<Int>
        {
            return QYNNSetting(
                intPreferencesKey("SettingsState.$key"),
                resettable = true,
                displayName = displayName,
                read = { intToEnumOrDefault(it, defaultValue) },
                write = { it.rawValue }
            )
        }
    }
}