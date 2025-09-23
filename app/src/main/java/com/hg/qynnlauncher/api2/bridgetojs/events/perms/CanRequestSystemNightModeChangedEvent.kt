package com.hg.qynnlauncher.api2.qynntojs.events.perms

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class CanRequestSystemNightModeChangedEvent(
    val newValue: Boolean,
): QYNNEventModel("canRequestSystemNightModeChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}