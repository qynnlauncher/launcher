package com.hg.qynnlauncher.api2.qynntojs.events.settings

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class DrawSystemWallpaperBehindWebViewChangedEvent(
    val newValue: Boolean,
) : QYNNEventModel("drawSystemWallpaperBehindWebViewChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}