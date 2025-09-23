package com.hg.qynnlauncher.api2.qynntojs.events.settings

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import com.hg.qynnlauncher.api2.shared.QYNNThemeStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class QYNNThemeChangedEvent(
    val newValue: QYNNThemeStringOptions,
) : QYNNEventModel("qynnThemeChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}