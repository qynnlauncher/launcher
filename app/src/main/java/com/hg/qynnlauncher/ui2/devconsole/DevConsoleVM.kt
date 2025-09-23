package com.hg.qynnlauncher.ui2.devconsole

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hg.qynnlauncher.QYNNLauncherApplication
import com.hg.qynnlauncher.services.QYNNServices
import com.hg.qynnlauncher.services.devconsole.DevConsoleMessagesHolder

class DevConsoleVM(
    private val _messagesHolder: DevConsoleMessagesHolder,
) : ViewModel()
{
    val messages get() = _messagesHolder.messages as List<IConsoleMessage>

    val actions = DevConsoleActions(
        clearMessages = {
            _messagesHolder.clearMessages()
        }
    )

    companion object
    {
        fun from(context: Application, serviceProvider: QYNNServices): DevConsoleVM
        {
            with(serviceProvider)
            {
                return DevConsoleVM(
                    _messagesHolder = consoleMessagesHolder,
                )
            }
        }

        // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
        val Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as QYNNLauncherApplication
                from(app, app.services)
            }
        }
    }
}