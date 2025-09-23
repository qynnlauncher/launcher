package com.hg.qynnlauncher.api2.qynntojs.events.settings

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class QYNNButtonVisibilityChangedEvent(
    val newValue: com.hg.qynnlauncher.api2.shared.QYNNButtonVisibilityStringOptions,
) : QYNNEventModel("qynnButtonVisibilityChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}