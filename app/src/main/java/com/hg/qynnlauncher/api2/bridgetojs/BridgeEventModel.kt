package com.hg.qynnlauncher.api2.qynntojs

import kotlinx.serialization.Serializable

@Serializable
abstract class QYNNEventModel(
    override val name: String,
) : IQYNNEventModel
{
    abstract override fun getJson(): String
}

