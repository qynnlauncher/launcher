package com.hg.qynnlauncher.ui2.devconsole

data class DevConsoleActions(
    val clearMessages: () -> Unit,
)
{
    companion object
    {
        fun empty() = DevConsoleActions({})
    }
}