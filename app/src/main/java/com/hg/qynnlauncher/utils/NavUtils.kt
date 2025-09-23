package com.hg.qynnlauncher.utils

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.hg.qynnlauncher.services.apps.InstalledApp
import com.hg.qynnlauncher.ui2.appdrawer.AppDrawerActivity
import com.hg.qynnlauncher.ui2.devconsole.DevConsoleActivity
import com.hg.qynnlauncher.ui2.settings.SettingsScreenActivity

// UTILS

fun Context.tryStartActivity(intent: Intent) = tryOrShowErrorToast {
    startActivity(intent)
}

fun Context.tryOrShowErrorToast(action: Context.() -> Unit)
{
    try
    {
        action()
    }
    catch (ex: Exception)
    {
        showErrorToast(ex)
    }
}

suspend fun Context.suspendTryOrShowErrorToast(action: suspend Context.() -> Unit)
{
    try
    {
        action()
    }
    catch (ex: Exception)
    {
        showErrorToast(ex)
    }
}


// QYNN ACTIVITIES

fun Context.startQYNNSettingsActivity() = startActivity(Intent(this, SettingsScreenActivity::class.java))
fun Context.tryStartQYNNSettingsActivity() = tryOrShowErrorToast { startQYNNSettingsActivity() }

fun Context.startQYNNAppDrawerActivity() = startActivity(Intent(this, AppDrawerActivity::class.java))
fun Context.tryStartQYNNAppDrawerActivity() = tryOrShowErrorToast { startQYNNAppDrawerActivity() }

fun Context.startDevConsoleActivity() = startActivity(Intent(this, DevConsoleActivity::class.java))
fun Context.tryStartDevConsoleActivity() = tryOrShowErrorToast { startDevConsoleActivity() }


// APPLICATIONS

fun Context.tryOpenAppInfo(packageName: String) = tryOrShowErrorToast { openAppInfo(packageName) }
fun Context.openAppInfo(packageName: String)
{
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${packageName}")
        )
    )
}

fun Context.tryRequestAppUninstall(packageName: String) = tryOrShowErrorToast { requestAppUninstall(packageName) }
fun Context.requestAppUninstall(packageName: String)
{
    val packageURI = Uri.parse("package:${packageName}")
    val uninstallIntent = Intent(Intent.ACTION_DELETE, packageURI)
    startActivity(uninstallIntent)
}

fun Context.tryLaunchApp(app: InstalledApp) = tryOrShowErrorToast { launchApp(app) }
fun Context.launchApp(app: InstalledApp) = launchApp(packageName)

fun Context.tryLaunchApp(packageName: String) = tryOrShowErrorToast { launchApp(packageName) }
fun Context.launchApp(packageName: String)
{
    val intent = packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null)
    {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    else
        throw Exception("Launch intent not found.")
}


// SYSTEM

/** Open Android settings */
fun Context.tryStartAndroidSettingsActivity() = tryOrShowErrorToast { startAndroidSettingsActivity() }
fun Context.startAndroidSettingsActivity()
{
    startActivity(Intent(Settings.ACTION_SETTINGS))
}

/** Change system wallpaper*/
fun Context.tryStartWallpaperPickerActivity() = tryOrShowErrorToast { startWallpaperPickerActivity() }
fun Context.startWallpaperPickerActivity()
{
    startActivity(Intent(Intent.ACTION_SET_WALLPAPER))
}

/** Switch away from QYNN */
fun Context.tryStartAndroidHomeSettingsActivity() = tryOrShowErrorToast { startAndroidHomeSettingsActivity() }
fun Context.startAndroidHomeSettingsActivity()
{
    startActivity(
        Intent(Settings.ACTION_HOME_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
}

fun Context.tryStartExtStorageManagerPermissionActivity() = tryOrShowErrorToast { startExtStorageManagerPermissionActivity() }
fun Context.startExtStorageManagerPermissionActivity()
{
    if (CurrentAndroidVersion.supportsScopedStorage())
    {
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.parse("package:${packageName}")
            )
        )
    }
}

fun Context.tryStartAndroidAccessibilitySettingsActivity() = tryOrShowErrorToast { startAndroidAccessiblitySettingsActivity() }
fun Context.startAndroidAccessiblitySettingsActivity()
{
    startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
}

fun Context.tryStartAndroidAddDeviceAdminActivity()
{
    tryStartActivity(
        Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(
                DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                QYNNLauncherApplication.adminReceiverComponentName
            )
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "QYNN Launcher needs this permission so projects can request the screen to be locked."
            )
        }
    )
}

fun Context.launchViewURIActivity(uriString: String) = launchViewURIActivity(Uri.parse(uriString))
fun Context.launchViewURIActivity(uri: Uri) = startActivity(Intent(Intent.ACTION_VIEW, uri))
