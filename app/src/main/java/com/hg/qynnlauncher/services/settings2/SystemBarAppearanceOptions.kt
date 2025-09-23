package com.hg.qynnlauncher.services.settings2

import com.hg.qynnlauncher.utils.RawRepresentable

enum class SystemBarAppearanceOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    Hide(0),
    LightIcons(1),
    DarkIcons(2),
}