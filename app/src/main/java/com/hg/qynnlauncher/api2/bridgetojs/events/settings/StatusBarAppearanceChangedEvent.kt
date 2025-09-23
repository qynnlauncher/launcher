package com.hg.qynnlauncher.api2.qynntojs.events.settings

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import com.hg.qynnlauncher.api2.shared.SystemBarAppearanceStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class StatusBarAppearanceChangedEvent(
    val newValue: SystemBarAppearanceStringOptions,
) : QYNNEventModel("statusBarAppearanceChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}