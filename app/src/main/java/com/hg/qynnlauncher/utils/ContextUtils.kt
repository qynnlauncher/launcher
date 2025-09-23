package com.hg.qynnlauncher.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.hg.qynnlauncher.QYNNLauncherApplication

val Context.QYNNLauncherApplication get() = applicationContext as QYNNLauncherApplication

fun Context.getIsSystemInNightMode(): Boolean
{
    return if (CurrentAndroidVersion.supportsNightMode())
    {
        resources.configuration.isNightModeActive
    }
    else
    {
        resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}


fun Context.checkCanSetSystemNightMode() = ActivityCompat.checkSelfPermission(this, "android.permission.MODIFY_DAY_NIGHT_MODE") == PackageManager.PERMISSION_GRANTED
        || checkSelfPermission(Manifest.permission.WRITE_SECURE_SETTINGS) == PackageManager.PERMISSION_GRANTED

fun Context.checkStoragePerms(): Boolean
{
    return if (CurrentAndroidVersion.supportsScopedStorage())
    {
        // we need a special permission on Android 11 and up
        Environment.isExternalStorageManager()
    }
    else
    {
        ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.showErrorToast(ex: Exception)
{
    showErrorToast(ex.messageOrDefault())
}

fun Context.showErrorToast(message: String?)
{
    Toast.makeText(this, message ?: "Exception with no message.", Toast.LENGTH_LONG).show()
}

//fun Context.checkCanLockScreen() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//    Context.checkIsAccessibilityServiceEnabled()
//else
//    Context.checkIsDeviceAdminEnabled()