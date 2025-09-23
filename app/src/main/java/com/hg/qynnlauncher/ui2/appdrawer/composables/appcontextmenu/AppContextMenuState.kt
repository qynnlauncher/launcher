package com.hg.qynnlauncher.ui2.appdrawer.composables.appcontextmenu

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.IntOffset
import com.hg.qynnlauncher.ui2.appdrawer.IAppDrawerApp

data class AppContextMenuState(
    val app: IAppDrawerApp,
    val offset: IntOffset,
    val alignment: Alignment,
)