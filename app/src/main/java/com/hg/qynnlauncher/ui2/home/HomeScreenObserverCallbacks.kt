package com.hg.qynnlauncher.ui2.home

import com.hg.qynnlauncher.services.displayshape.OnPathChangeFunc
import com.hg.qynnlauncher.services.windowinsetsholder.OnWindowInsetsChangedFunc
import com.hg.qynnlauncher.services.windowinsetsholder.emptyOnWIndowInsetsChangedFunc

data class HomeScreenObserverCallbacks(
    val onDisplayShapePathChanged: OnPathChangeFunc,
    val onCutoutPathChanged: OnPathChangeFunc,
    val onWindowInsetsChanged: OnWindowInsetsChangedFunc,
)
{
    companion object
    {
        fun empty() = HomeScreenObserverCallbacks({}, {}, emptyOnWIndowInsetsChangedFunc)
    }
}
