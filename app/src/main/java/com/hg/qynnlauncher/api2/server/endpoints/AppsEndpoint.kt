package com.hg.qynnlauncher.api2.server.endpoints

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import com.hg.qynnlauncher.api2.server.QYNNAPIEndpointAppsResponse
import com.hg.qynnlauncher.api2.server.IQYNNServerEndpoint
import com.hg.qynnlauncher.api2.server.jsonResponse
import com.hg.qynnlauncher.services.apps.InstalledAppsHolder
import kotlinx.serialization.json.Json

class AppsEndpoint(private val _installedApps: InstalledAppsHolder) : IQYNNServerEndpoint
{
    override suspend fun handle(req: WebResourceRequest): WebResourceResponse
    {
        return jsonResponse(
            Json.encodeToString(
                QYNNAPIEndpointAppsResponse.serializer(),
                QYNNAPIEndpointAppsResponse(
                    apps = _installedApps.packageNameToInstalledAppMap.values.map { it.toSerializable() },
                )
            )
        )
    }
}