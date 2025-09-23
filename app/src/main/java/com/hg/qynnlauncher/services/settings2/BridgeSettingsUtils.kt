package com.hg.qynnlauncher.services.settings2

import android.os.Build
import android.os.Build.VERSION_CODES

fun getIsQYNNAbleToLockTheScreen(
    isAccessibilityServiceEnabled: Boolean,
    isDeviceAdminEnabled: Boolean,
    allowProjectsToTurnScreenOff: Boolean,
): Boolean
{
    return (
            if (Build.VERSION.SDK_INT >= VERSION_CODES.P)
                isAccessibilityServiceEnabled
            else
                isDeviceAdminEnabled
            )
            && allowProjectsToTurnScreenOff
}