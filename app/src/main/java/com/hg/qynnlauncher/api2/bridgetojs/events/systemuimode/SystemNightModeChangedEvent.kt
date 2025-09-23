package com.hg.qynnlauncher.api2.qynntojs.events.systemuimode

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import com.hg.qynnlauncher.api2.shared.SystemNightModeStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class SystemNightModeChangedEvent(
    val newValue: SystemNightModeStringOptions,
)
    : QYNNEventModel("systemNightModeChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}