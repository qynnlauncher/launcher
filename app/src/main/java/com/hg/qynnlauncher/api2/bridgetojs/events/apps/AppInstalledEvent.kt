package com.hg.qynnlauncher.api2.qynntojs.events.apps

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import com.hg.qynnlauncher.services.apps.SerializableInstalledApp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AppInstalledEvent(
    val app: SerializableInstalledApp,
) : QYNNEventModel("appInstalled")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}