package com.hg.qynnlauncher.utils

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import com.hg.qynnlauncher.api2.server.QYNNAPIEndpointAppsResponse
import com.hg.qynnlauncher.services.apps.InstalledApp
import com.hg.qynnlauncher.services.iconpacks.InstalledIconPackHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.serialization.json.Json
import java.io.File

fun saveAsPNG(file: File, drawable: Drawable)
{
    file.outputStream().use { stream ->
        drawable.toBitmap(config = Bitmap.Config.ARGB_8888)
            .compress(Bitmap.CompressFormat.PNG, 90, stream)
    }
}

suspend fun exportForMockAsync(
    installedApps: Iterable<InstalledApp>,
    iconPacks: Iterable<InstalledIconPackHolder>,
    dir: File,
    onJobStarted: (jobCount: Int) -> Unit,
    onJobFinished: (jobCount: Int) -> Unit
)
{
    // using a supervisor scope instead of coroutine scope to allow the export to go through partially
    // even if one particular file fails to export
    supervisorScope()
    {

        var startedJobs = 0
        var completedJobs = 0

        fun startJobAndNotify(job: suspend CoroutineScope.() -> Unit)
        {
            launch {
                startedJobs++
                onJobStarted(startedJobs)
                try
                {
                    job()
                }
                finally
                {
                    completedJobs++
                    onJobFinished(completedJobs)
                }
            }
        }

        fun startSavingAsPNGAndNotify(file: File, drawable: Drawable)
        {
            startJobAndNotify {
                saveAsPNG(file, drawable)
            }
        }

        // save what the API would normally respond with to apps.json
        startJobAndNotify {
            val apps = installedApps.map { it.toSerializable() }
            val resp = QYNNAPIEndpointAppsResponse(apps)
            val appsFile = File(dir, "apps.json")
            val appsStr = Json.encodeToString(QYNNAPIEndpointAppsResponse.serializer(), resp)
            appsFile.writeText(appsStr)
        }

        // create a directory for icons
        val iconsDir = File(dir, "icons")
        iconsDir.mkdir()

        // create a directory for default icons
        val defIconsDir = File(iconsDir, "default")
        defIconsDir.mkdir()

        // create a directory for each icon pack
        for (iconPack in iconPacks)
        {
            val iconPackDir = File(iconsDir, iconPack.packageName)
            iconPackDir.mkdir()

            // copy entire appfilter.xml to a file in the icon pack folder
            startJobAndNotify {
                iconPack.getRawAppFilterXmlStream().use { readStream ->
                    val appFilterXmlFile = File(iconPackDir, "appfilter.xml")
                    appFilterXmlFile.outputStream().use { writeStream ->
                        readStream.copyTo(writeStream)
                    }
                }
            }
        }

        for (app in installedApps)
        {
            startSavingAsPNGAndNotify(File(defIconsDir, "${app.packageName}.png"), app.defaultIcon)

            for (iconPack in iconPacks)
            {
                val iconPackDir = File(iconsDir, iconPack.packageName)
                val iconPackIcon = iconPack.getIconForOrNull(app.packageName)
                if (iconPackIcon != null)
                {
                    startSavingAsPNGAndNotify(
                        File(iconPackDir, "${app.packageName}.png"),
                        iconPackIcon,
                    )
                }
            }
        }
    }
}