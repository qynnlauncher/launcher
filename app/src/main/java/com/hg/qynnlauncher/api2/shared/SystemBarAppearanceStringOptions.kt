package com.hg.qynnlauncher.api2.shared

import com.hg.qynnlauncher.services.settings2.SystemBarAppearanceOptions
import com.hg.qynnlauncher.utils.RawRepresentable
import com.hg.qynnlauncher.utils.q
import com.hg.qynnlauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class SystemBarAppearanceStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    Hide("hide"),
    LightIcons("light-fg"),
    DarkIcons("dark-fg"),
    ;

    companion object
    {
        fun fromSystemBarAppearance(it: SystemBarAppearanceOptions) = when (it)
        {
            SystemBarAppearanceOptions.Hide -> Hide
            SystemBarAppearanceOptions.LightIcons -> LightIcons
            SystemBarAppearanceOptions.DarkIcons -> DarkIcons
        }

        fun systemBarAppearanceFromStringOrThrow(appearance: String): SystemBarAppearanceOptions
        {
             return when (appearance)
             {
                 Hide.rawValue -> SystemBarAppearanceOptions.Hide
                 LightIcons.rawValue -> SystemBarAppearanceOptions.LightIcons
                 DarkIcons.rawValue -> SystemBarAppearanceOptions.DarkIcons
                 else ->  throw Exception("Argument \"appearance\" must be one of ${q(Hide)}, ${q(LightIcons)} or ${q(DarkIcons)} (got ${q(appearance)}).")
             }
        }
    }
}