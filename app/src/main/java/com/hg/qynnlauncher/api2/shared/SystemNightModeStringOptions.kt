package com.hg.qynnlauncher.api2.shared

import android.app.UiModeManager
import android.util.Log
import com.hg.qynnlauncher.utils.RawRepresentable
import com.hg.qynnlauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

private const val TAG = "NightMode"

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class SystemNightModeStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    No("no"),
    Yes("yes"),
    Auto("auto"),
    Custom("custom"),
    Error("error"),
    ;

    companion object
    {
        fun fromUiModeManagerNightMode(nightMode: Int) = when (nightMode)
        {
            UiModeManager.MODE_NIGHT_NO -> No
            UiModeManager.MODE_NIGHT_YES -> Yes
            UiModeManager.MODE_NIGHT_AUTO -> Auto
            UiModeManager.MODE_NIGHT_CUSTOM -> Custom
            else -> Error.also { Log.e(TAG, "fromUiModeManagerNightMode: uiModeManager.nightMode returned unexpected value $nightMode") }
        }
    }

}