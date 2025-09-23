package com.hg.qynnlauncher.api2.qynntojs.events.settings

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import com.hg.qynnlauncher.api2.shared.OverscrollEffectsStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class OverscrollEffectsChangedEvent(
    val newValue: OverscrollEffectsStringOptions,
) : QYNNEventModel("overscrollEffectsChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}