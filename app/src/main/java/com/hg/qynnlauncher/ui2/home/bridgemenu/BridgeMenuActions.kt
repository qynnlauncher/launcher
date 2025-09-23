package com.hg.qynnlauncher.ui2.home.qynnmenu

data class QYNNMenuActions(
    val onWebViewRefreshRequest: () -> Unit,
    val onHideQYNNButtonRequest: () -> Unit,
    val onRequestIsExpandedChange: (newIsExpanded: Boolean) -> Unit,
)
{
    companion object
    {
        fun empty() = QYNNMenuActions({}, {}, {})
    }
}