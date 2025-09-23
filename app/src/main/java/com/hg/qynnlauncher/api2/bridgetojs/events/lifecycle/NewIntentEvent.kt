package com.hg.qynnlauncher.api2.qynntojs.events.lifecycle

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class NewIntentEvent : QYNNEventModel("newIntent")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}