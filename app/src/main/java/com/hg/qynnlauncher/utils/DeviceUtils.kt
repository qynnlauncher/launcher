package com.hg.qynnlauncher.utils

import android.os.Build

object DeviceUtils {
    fun isXiaomiDevice(): Boolean {
        return Build.MANUFACTURER.contains("Xiaomi", ignoreCase = true)
    }
}