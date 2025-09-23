package com.hg.qynnlauncher.services.settings2

import com.hg.qynnlauncher.utils.RawRepresentable

enum class QYNNThemeOptions(override val rawValue: Int) : RawRepresentable<Int>
{
    System(0),
    Light(1),
    Dark(2),
}