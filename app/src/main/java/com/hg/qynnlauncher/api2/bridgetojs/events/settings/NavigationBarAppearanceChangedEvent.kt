package com.hg.qynnlauncher.api2.qynntojs.events.settings

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import com.hg.qynnlauncher.api2.shared.SystemBarAppearanceStringOptions
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class NavigationBarAppearanceChangedEvent(
    val newValue: SystemBarAppearanceStringOptions,
) : QYNNEventModel("navigationBarAppearanceChanged")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}