package com.hg.qynnlauncher.services

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class QYNNAction {
    object OpenAppDrawer : QYNNAction()
}

object QYNNActionsHolder {
    private val _actions = MutableSharedFlow<QYNNAction>()
    val actions = _actions.asSharedFlow()

    suspend fun perform(action: QYNNAction) {
        _actions.emit(action)
    }
}