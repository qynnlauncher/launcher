package com.hg.qynnlauncher.services.gestures

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat

class GestureOverlayView(context: Context) : View(context) {

    private val gestureDetector: GestureDetectorCompat

    init {
        // Make the view transparent
        setBackgroundColor(0x00000000)

        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
            private val edgeSizeDp = 30
            private val edgeSizePx = edgeSizeDp * resources.displayMetrics.density

            override fun onDown(e: MotionEvent): Boolean {
                // We need to return true here to ensure onFling is called.
                // We only care about gestures starting at the edge.
                return e.x < edgeSizePx
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false

                // Check if the fling started at the left edge
                if (e1.x < edgeSizePx) {
                    // Check if it was a rightward swipe with significant velocity
                    if (e2.x > e1.x && velocityX > 1000) {
                        QynnGestureAccessibilityService.instance?.performGlobalActionBack()
                        return true
                    }
                }
                return false
            }
        }

        gestureDetector = GestureDetectorCompat(context, gestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }
}