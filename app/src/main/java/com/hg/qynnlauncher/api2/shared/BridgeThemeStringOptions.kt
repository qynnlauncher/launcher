package com.hg.qynnlauncher.api2.shared

import com.hg.qynnlauncher.services.settings2.QYNNThemeOptions
import com.hg.qynnlauncher.utils.RawRepresentable
import com.hg.qynnlauncher.utils.q
import com.hg.qynnlauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class QYNNThemeStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    System("system"),
    Light("light"),
    Dark("dark"),
    ;

    companion object
    {
        fun fromQYNNTheme(theme: QYNNThemeOptions) = when (theme)
        {
            QYNNThemeOptions.System -> System
            QYNNThemeOptions.Light -> Light
            QYNNThemeOptions.Dark -> Dark
        }

        fun qynnThemeFromStringOrThrow(theme: String): QYNNThemeOptions
        {
            return when (theme)
            {
                System.rawValue -> QYNNThemeOptions.System
                Light.rawValue -> QYNNThemeOptions.Light
                Dark.rawValue -> QYNNThemeOptions.Dark
                else -> throw Exception("Argument \"theme\" must be one of ${q(System)}, ${q(Light)} or ${q(Dark)} (got ${q(theme)}).")
            }
        }
    }
}