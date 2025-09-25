package com.hg.qynnlauncher.services.gestures

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.core.content.getSystemService
import androidx.core.view.GestureDetectorCompat
import kotlin.math.abs

class GestureOverlayView(context: Context, private val pillView: GesturePillView) : View(context) {

    private val gestureDetector: GestureDetectorCompat
    private var velocityTracker: VelocityTracker? = null
    private val vibrator: Vibrator? = context.getSystemService()

    companion object {
        private const val LEFT_EDGE = 0
        private const val RIGHT_EDGE = 1
        private const val BOTTOM_EDGE = 2

        private const val EDGE_SENSITIVITY_DP = 30
        private const val BOTTOM_SENSITIVITY_DP = 40

        // Velocity thresholds in dp/second
        private const val VELOCITY_THRESHOLD_BACK = 1500f
        private const val VELOCITY_THRESHOLD_RECENTS = 800f

        // Distance and time thresholds for slow drag (Home)
        private const val DISTANCE_THRESHOLD_HOME_DP = 150f
        private const val DURATION_THRESHOLD_HOME_MS = 300L
    }

    private val edgeSizePx: Float
    private val bottomEdgeSizePx: Float
    private val distanceThresholdHomePx: Float

    init {
        setBackgroundColor(0x00000000) // Transparent
        val density = resources.displayMetrics.density
        edgeSizePx = EDGE_SENSITIVITY_DP * density
        bottomEdgeSizePx = BOTTOM_SENSITIVITY_DP * density
        distanceThresholdHomePx = DISTANCE_THRESHOLD_HOME_DP * density

        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

            private var initialTouch: MotionEvent? = null
            private var gestureZone: Int? = null

            override fun onDown(e: MotionEvent): Boolean {
                initialTouch = MotionEvent.obtain(e)
                velocityTracker?.clear()
                velocityTracker = velocityTracker ?: VelocityTracker.obtain()
                velocityTracker?.addMovement(e)

                gestureZone = when {
                    e.rawX < edgeSizePx -> LEFT_EDGE
                    e.rawX > width - edgeSizePx -> RIGHT_EDGE
                    e.rawY > height - bottomEdgeSizePx -> BOTTOM_EDGE
                    else -> null
                }

                if (gestureZone != null) {
                    pillView.onGestureStarted()
                    vibrate(VibrationEffect.EFFECT_TICK)
                }

                return gestureZone != null
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val startEvent = initialTouch ?: return false
                val zone = gestureZone ?: return false

                pillView.onGestureCompleted()
                vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)

                when (zone) {
                    LEFT_EDGE -> {
                        if (velocityX > VELOCITY_THRESHOLD_BACK && abs(velocityX) > abs(velocityY)) {
                            QynnGestureAccessibilityService.instance?.performGlobalActionBack()
                            return true
                        }
                    }
                    RIGHT_EDGE -> {
                        if (velocityX < -VELOCITY_THRESHOLD_BACK && abs(velocityX) > abs(velocityY)) {
                            QynnGestureAccessibilityService.instance?.performGlobalActionBack()
                            return true
                        }
                    }
                    BOTTOM_EDGE -> {
                        if (velocityY < -VELOCITY_THRESHOLD_RECENTS && abs(velocityY) > abs(velocityX)) {
                            QynnGestureAccessibilityService.instance?.performGlobalActionRecents()
                            return true
                        }
                    }
                }
                return false
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                val startEvent = initialTouch ?: return false
                val zone = gestureZone ?: return false
                val duration = e2.eventTime - startEvent.eventTime
                val verticalMove = startEvent.y - e2.y

                val progress = (verticalMove / distanceThresholdHomePx).coerceIn(0f, 1f)
                pillView.onGestureProgress(progress)
                if (progress > 0.5f) {
                    vibrate(VibrationEffect.EFFECT_CLICK)
                }

                if (zone == BOTTOM_EDGE && verticalMove > distanceThresholdHomePx && duration > DURATION_THRESHOLD_HOME_MS) {
                    pillView.onGestureCompleted()
                    vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
                    QynnGestureAccessibilityService.instance?.performGlobalActionHome()
                    initialTouch = null
                    gestureZone = null
                    return true
                }
                return false
            }
        }
        gestureDetector = GestureDetectorCompat(context, gestureListener)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        velocityTracker?.addMovement(event)
        val consumed = gestureDetector.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            pillView.onGestureCancelled()
            velocityTracker?.recycle()
            velocityTracker = null
        }

        return consumed || super.onTouchEvent(event)
    }

    private fun vibrate(effectId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
        }
    }
}