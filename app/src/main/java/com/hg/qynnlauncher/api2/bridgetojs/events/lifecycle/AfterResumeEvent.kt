package com.hg.qynnlauncher.api2.qynntojs.events.lifecycle

import com.hg.qynnlauncher.api2.qynntojs.QYNNEventModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class AfterResumeEvent : QYNNEventModel("afterResume")
{
    override fun getJson() = Json.encodeToString(serializer(), this)
}