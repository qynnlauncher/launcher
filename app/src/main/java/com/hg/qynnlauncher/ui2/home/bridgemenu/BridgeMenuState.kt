package com.hg.qynnlauncher.ui2.home.qynnmenu

import androidx.compose.runtime.Immutable

@Immutable
data class QYNNMenuState(
    val isShown: Boolean,
    val isExpanded: Boolean,
    val showAppDrawerButtonWhenCollapsed: Boolean
)