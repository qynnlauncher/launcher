package com.hg.qynnlauncher.api2.qynntojs.events.windowinsets

import com.hg.qynnlauncher.api2.qynntojs.IQYNNEventModel
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsOptions
import com.hg.qynnlauncher.services.windowinsetsholder.WindowInsetsSnapshot
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
class WindowInsetsChangedEvent(
    override val name: String,
    val insets: WindowInsetsSnapshot,
) : IQYNNEventModel
{
    override fun getJson() = Json.encodeToString(this)

    companion object
    {
        fun fromSnapshot(option: WindowInsetsOptions, snapshot: WindowInsetsSnapshot) = WindowInsetsChangedEvent(
            name = "${option.name}WindowInsetsChanged",
            insets = snapshot,
        )
    }
}