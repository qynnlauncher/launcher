package com.hg.qynnlauncher.services.gestures

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class QynnGestureAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "GestureService"
        var instance: QynnGestureAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.i(TAG, "Service connected.")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Not needed for global actions, but must be implemented.
    }

    override fun onInterrupt() {
        Log.w(TAG, "Service interrupted.")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.i(TAG, "Service disconnected.")
        instance = null
        return super.onUnbind(intent)
    }

    fun performGlobalActionBack() {
        performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun performGlobalActionHome() {
        performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun performGlobalActionRecents() {
        performGlobalAction(GLOBAL_ACTION_RECENTS)
    }
}