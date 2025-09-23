package com.hg.qynnlauncher.api2.server

import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.hg.qynnlauncher.QYNNLauncherApplication
import com.hg.qynnlauncher.api2.server.endpoints.AppIconsEndpoint
import com.hg.qynnlauncher.api2.server.endpoints.AppsEndpoint
import com.hg.qynnlauncher.api2.server.endpoints.QYNNFileServer
import com.hg.qynnlauncher.api2.server.endpoints.IconPackContentEndpoint
import com.hg.qynnlauncher.api2.server.endpoints.IconPacksEndpoint
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder
import com.hg.qynnlauncher.services.apps.SerializableInstalledApp
import com.hg.qynnlauncher.services.iconpackcache.InstalledIconPacksHolder
import com.hg.qynnlauncher.services.settings2.QYNNSetting
import com.hg.qynnlauncher.services.settings2.QYNNSettings
import com.hg.qynnlauncher.services.settings2.settingsDataStore
import com.hg.qynnlauncher.services.settings2.useQYNNSettingStateFlow
import com.hg.qynnlauncher.utils.URLWithQueryBuilder
import com.hg.qynnlauncher.utils.q
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.plus
import kotlinx.serialization.Serializable

private const val TAG = "ReqHandler"

fun getQYNNApiEndpointURL(endpoint: String, vararg queryParams: Pair<String, Any?>): String
{
    return URLWithQueryBuilder("https://${QYNNServer.HOST}/${QYNNServer.API_PATH_ROOT}/$endpoint")
        .addParams(queryParams.asIterable())
        .build()
}

@Serializable
data class QYNNAPIEndpointAppsResponse(
    val apps: List<SerializableInstalledApp>,
)

class QYNNServer(
    private val _app: QYNNLauncherApplication,
    private val _apps: InstalledAppsHolder,
    private val _iconPacks: InstalledIconPacksHolder,
)
{
    private val _scope = CoroutineScope(Dispatchers.Main) + SupervisorJob()

    // SETTINGS
    private fun <TPreference, TResult> s(setting: QYNNSetting<TPreference, TResult>) = useQYNNSettingStateFlow(_app.settingsDataStore, _scope, setting)
    private val _currentProjDir = s(QYNNSettings.currentProjDir)

    val isReadyToServe = _currentProjDir.map { it != null }

    private val _fileServer = QYNNFileServer(
        _currentProjDir = _currentProjDir,
    )

    private val _endpoints = mapOf(
        ENDPOINT_APPS to AppsEndpoint(_apps),
        ENDPOINT_APP_ICONS to AppIconsEndpoint(_apps, _iconPacks),
        ENDPOINT_ICON_PACKS to IconPacksEndpoint(_iconPacks),
        ENDPOINT_ICON_PACK_CONTENT to IconPackContentEndpoint(_iconPacks),
    )

    suspend fun handle(req: WebResourceRequest): WebResourceResponse?
    {
        val host = req.url.host?.lowercase()

        if (host != HOST)
            return null

        Log.i(TAG, "received request to ${req.url}")

        try
        {
            val path = req.url.path
            val apiPrefix = "/$API_PATH_ROOT/"

            return if (path != null && path.startsWith(apiPrefix))
            {
                val endpointStr = path.substring(apiPrefix.length)
                val endpoint = _endpoints[endpointStr]

                endpoint?.handle(req)
                    ?: errorResponse(HTTPStatusCode.BadRequest, "There is no API endpoint at ${q(endpointStr)}.")
            }
            else
            {
                _fileServer.handle(req)
            }
        }
        catch (ex: HttpResponseException)
        {
            return errorResponse(ex.respStatusCode, ex.respMessage)
        }
        catch (ex: Exception)
        {
            Log.e(TAG, "Unexpected error:", ex)
            return errorResponse(HTTPStatusCode.InternalServerError, "Unexpected error: $ex")
        }
    }

    companion object
    {
        const val HOST = "hg.is-great.org"
        const val PROJECT_URL = "https://$HOST/"
        const val API_PATH_ROOT = ":"

        const val ENDPOINT_ICON_PACK_CONTENT = "iconpacks/content"
        const val ENDPOINT_APPS = "apps"
        const val ENDPOINT_APP_ICONS = "appicons"
        const val ENDPOINT_ICON_PACKS = "iconpacks"
    }
}