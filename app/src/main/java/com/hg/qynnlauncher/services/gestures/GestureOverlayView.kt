package com.hg.qynnlauncher.services.gestures

import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.core.content.getSystemService
import androidx.core.view.GestureDetectorCompat
import java.util.LinkedList
import kotlin.math.abs

private const val LEFT_EDGE = 0
private const val RIGHT_EDGE = 1
private const val BOTTOM_EDGE = 2

class GestureOverlayView(
    context: Context,
    private val pillView: GesturePillView,
    private val edgeSensitivityDp: Float,
    private val backVelocityThreshold: Float,
    private val recentsVelocityThreshold: Float,
    private val homeDistanceThresholdDp: Float
) : View(context) {

    private val gestureDetector: GestureDetectorCompat
    private var velocityTracker: VelocityTracker? = null
    private val vibrator: Vibrator? = context.getSystemService()

    private val touchHistory = LinkedList<PointF>()
    private val historySize = 5
    private var isGestureInProgress = false
    private var pointerCount = 0

    private val edgeSizePx: Float
    private val bottomEdgeSizePx: Float
    private val distanceThresholdHomePx: Float

    init {
        setBackgroundColor(0x00000000) // Transparent
        val density = resources.displayMetrics.density
        edgeSizePx = edgeSensitivityDp * density
        bottomEdgeSizePx = 40f * density // Keep this constant for now
        distanceThresholdHomePx = homeDistanceThresholdDp * density

        val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

            private var initialTouch: MotionEvent? = null
            private var gestureZone: Int? = null

            override fun onDown(e: MotionEvent): Boolean {
                if (e.pointerCount > 1) return false

                isGestureInProgress = false
                touchHistory.clear()
                addEventToHistory(e)
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
                    isGestureInProgress = true
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
                if (!isGestureInProgress || e1 == null || e1.pointerCount > 1) return false
                val zone = gestureZone ?: return false

                pillView.onGestureCompleted()
                vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
                isGestureInProgress = false

                when (zone) {
                    LEFT_EDGE -> {
                        if (velocityX > backVelocityThreshold && abs(velocityX) > abs(velocityY)) {
                            QynnGestureAccessibilityService.instance?.performGlobalActionBack()
                            return true
                        }
                    }
                    RIGHT_EDGE -> {
                        if (velocityX < -backVelocityThreshold && abs(velocityX) > abs(velocityY)) {
                            QynnGestureAccessibilityService.instance?.performGlobalActionBack()
                            return true
                        }
                    }
                    BOTTOM_EDGE -> {
                        if (velocityY < -recentsVelocityThreshold && abs(velocityY) > abs(velocityX)) {
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
                if (!isGestureInProgress || e1 == null || e1.pointerCount > 1) return false
                addEventToHistory(e2)
                val smoothedPoint = getSmoothedPoint()
                val startEvent = initialTouch ?: return false
                val zone = gestureZone ?: return false
                val duration = e2.eventTime - startEvent.eventTime
                val verticalMove = startEvent.y - smoothedPoint.y

                if ((zone == LEFT_EDGE && distanceX < 0) || (zone == RIGHT_EDGE && distanceX > 0) || (zone == BOTTOM_EDGE && distanceY < 0)) {
                    isGestureInProgress = false
                    pillView.onGestureCancelled()
                    return true
                }

                val progress = (verticalMove / distanceThresholdHomePx).coerceIn(0f, 1f)
                pillView.onGestureProgress(progress)
                if (progress > 0.5f) {
                    vibrate(VibrationEffect.EFFECT_CLICK)
                }

                if (zone == BOTTOM_EDGE && verticalMove > distanceThresholdHomePx && duration > 300L) {
                    pillView.onGestureCompleted()
                    vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
                    QynnGestureAccessibilityService.instance?.performGlobalActionHome()
                    isGestureInProgress = false
                    return true
                }
                return false
            }
        }
        gestureDetector = GestureDetectorCompat(context, gestureListener)
    }

    private fun addEventToHistory(event: MotionEvent) {
        if (touchHistory.size >= 5) {
            touchHistory.removeFirst()
        }
        touchHistory.add(PointF(event.x, event.y))
    }

    private fun getSmoothedPoint(): PointF {
        var sumX = 0f
        var sumY = 0f
        for (point in touchHistory) {
            sumX += point.x
            sumY += point.y
        }
        return PointF(sumX / touchHistory.size, sumY / touchHistory.size)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        velocityTracker?.addMovement(event)

        if (event.pointerCount > 1) {
            return handleMultiTouch(event)
        }

        val consumed = gestureDetector.onTouchEvent(event)

        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            if (isGestureInProgress) {
                pillView.onGestureCancelled()
            }
            velocityTracker?.recycle()
            velocityTracker = null
            touchHistory.clear()
            isGestureInProgress = false
        }

        return consumed || super.onTouchEvent(event)
    }

    private fun handleMultiTouch(event: MotionEvent): Boolean {
        val action = event.actionMasked

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerCount = event.pointerCount
                velocityTracker?.addMovement(event)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                 if (event.pointerCount != pointerCount) {
                    pointerCount = event.pointerCount
                    return true
                }
                velocityTracker?.addMovement(event)
                velocityTracker?.computeCurrentVelocity(1000)
                val velocityY = velocityTracker?.yVelocity ?: 0f

                if (abs(velocityY) > 1000f) { // Hardcoded for now
                    when (pointerCount) {
                        2 -> QynnGestureAccessibilityService.instance?.performGlobalActionNotifications()
                        3 -> QynnGestureAccessibilityService.instance?.performGlobalActionQuickSettings()
                    }
                    vibrate(VibrationEffect.EFFECT_HEAVY_CLICK)
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                pointerCount = event.pointerCount - 1
            }
        }
        return true
    }

    private fun vibrate(effectId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
        }
    }
}