package com.hg.qynnlauncher.utils

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.hg.qynnlauncher.R
import com.hg.qynnlauncher.services.apps.InstalledApp
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
fun Context.launchApp(app: InstalledApp) = launchApp(app.packageName)


private fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Launches an application in a robust, clean, and user-friendly way.
 *
 * This function handles finding the correct launch intent, ensuring the app is brought to the foreground,
 * providing smooth animations, and handling a variety of errors gracefully.
 *
 * @param packageName The package name of the application to launch.
 *
 * @see [Directive #1] New intent flags for a clean launch.
 * @see [Directive #2] Comprehensive error handling and logging.
 * @see [Directive #3] Custom launch animation.
 * @see [Directive #4] Main thread enforcement.
 * @see [Directive #7] Validation for exported activities on Android 12+.
 * @see [Directive #8] Fallback scan for launchable activities.
 * @see [Directive #12] This documentation block.
 */
fun Context.launchApp(packageName: String) {
    // Directive #4: Enforce that startActivity() runs on the UI thread.
    if (Looper.myLooper() != Looper.getMainLooper()) {
        Log.e("NavUtils", "launchApp called from a background thread.", IllegalStateException())
        // Optionally, you could use runOnUiThread here, but it's better to fix the call site.
        // For now, we'll show a toast and log, then return.
        Toast.makeText(this, "Error: Launch attempt from background thread.", Toast.LENGTH_SHORT).show()
        return
    }

    try {
        // Directive #1 & #8: Try to get the launch intent, with a fallback.
        var launchIntent = packageManager.getLaunchIntentForPackage(packageName)

        if (launchIntent == null) {
            Log.w("NavUtils", "getLaunchIntentForPackage() returned null for $packageName. Falling back to queryIntentActivities().")
            val intent = Intent(Intent.ACTION_MAIN, null)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            intent.setPackage(packageName)
            val activities = packageManager.queryIntentActivities(intent, 0)
            if (activities.isNotEmpty()) {
                val activityInfo = activities[0].activityInfo
                launchIntent = Intent(Intent.ACTION_MAIN).apply {
                    setClassName(activityInfo.packageName, activityInfo.name)
                }
            }
        }

        if (launchIntent == null) {
            throw Exception("Could not find any launchable activity for package: $packageName")
        }

        // Directive #7: Respect Android 12+ exported requirements.
        val resolveInfo = packageManager.resolveActivity(launchIntent, PackageManager.MATCH_DEFAULT_ONLY)
        if (resolveInfo != null && !resolveInfo.activityInfo.exported) {
            throw SecurityException("Cannot launch app $packageName: main activity is not exported.")
        }

        // Directive #1: Use clean and robust intent flags.
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
            Intent.FLAG_ACTIVITY_SINGLE_TOP
        )

        val activity = findActivity()
        if (activity != null) {
            activity.startActivity(launchIntent)
            // Directive #3: Apply a customizable launch animation.
            activity.overridePendingTransition(R.anim.fade_in, 0)
        } else {
            // Fallback for non-activity contexts. Animation is not possible here.
            startActivity(launchIntent)
        }

    } catch (e: Exception) {
        // Directive #2: Comprehensive error handling and logging.
        Log.e("NavUtils", "Failed to launch app: $packageName", e)
        val errorMessage = when (e) {
            is SecurityException -> "App cannot be launched due to security restrictions."
            else -> "Could not open this application."
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }
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