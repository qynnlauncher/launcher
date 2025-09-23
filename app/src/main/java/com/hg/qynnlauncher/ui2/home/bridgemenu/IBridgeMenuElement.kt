package com.hg.qynnlauncher.ui2.home.qynnmenu

sealed interface IQYNNMenuElement
{
    data object Divider : IQYNNMenuElement
    data class Button(val iconResId: Int, val text: String, val action: () -> Unit) : IQYNNMenuElement
}