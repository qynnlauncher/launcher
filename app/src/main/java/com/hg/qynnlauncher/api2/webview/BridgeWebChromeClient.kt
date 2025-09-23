package com.hg.qynnlauncher.api2.webview

import android.webkit.ConsoleMessage
import com.hg.qynnlauncher.services.devconsole.DevConsoleMessagesHolder
import com.hg.qynnlauncher.webview.AccompanistWebChromeClient

class QYNNWebChromeClient(
    private val _consoleMessageHolder: DevConsoleMessagesHolder,
) : AccompanistWebChromeClient()
{

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean
    {
        return when (consoleMessage)
        {
            null -> super.onConsoleMessage(null)
            else ->
            {
                _consoleMessageHolder.addMessage(consoleMessage)
                true
            }
        }
    }
}