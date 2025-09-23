package com.hg.qynnlauncher.api2.qynntojs.events.apps

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AppRemovedEvent(
    val packageName: String,
) : QYNNEventModel("appRemoved")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}