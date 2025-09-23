package com.hg.qynnlauncher.api2.shared

import com.hg.qynnlauncher.utils.RawRepresentable
import com.hg.qynnlauncher.utils.q
import com.hg.qynnlauncher.utils.serialization.StringEnumWriteOnlySerializer
import kotlinx.serialization.Serializable

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = StringEnumWriteOnlySerializer::class)
enum class QYNNButtonVisibilityStringOptions(override val rawValue: String) : RawRepresentable<String>
{
    Shown("shown"),
    Hidden("hidden"),
    ;

    companion object
    {
        fun fromShowQYNNButton(it: Boolean) = when (it)
        {
            true -> Shown
            false -> Hidden
        }

        fun showQYNNButtonFromStringOrThrow(visibility: String): Boolean
        {
            return when (visibility)
            {
                Shown.rawValue -> true
                Hidden.rawValue -> false
                else -> throw Exception("Argument \"visibility\" must be either ${q(Shown)} or ${q(Hidden)} (got ${q(visibility)}).")
            }
        }
    }
}