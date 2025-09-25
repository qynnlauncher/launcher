package com.hg.qynnlauncher.services.gestures

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce

class GesturePillView(context: Context) : View(context) {

    private val paint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private val pillRect = RectF()
    private val restingWidth = 120f * resources.displayMetrics.density
    private val restingHeight = 4f * resources.displayMetrics.density
    private var currentWidth = restingWidth

    private val widthProperty = object : FloatPropertyCompat<GesturePillView>("width") {
        override fun getValue(view: GesturePillView): Float = view.currentWidth
        override fun setValue(view: GesturePillView, value: Float) {
            view.currentWidth = value
            view.invalidate()
        }
    }

    private val widthAnimation = SpringAnimation(this, widthProperty).setSpring(
        SpringForce()
            .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
            .setStiffness(SpringForce.STIFFNESS_LOW)
    )

    init {
        // Initially invisible
        alpha = 0f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        val left = (viewWidth - currentWidth) / 2
        val top = viewHeight - restingHeight - (16 * resources.displayMetrics.density)
        val right = left + currentWidth
        val bottom = top + restingHeight

        pillRect.set(left, top, right, bottom)

        val cornerRadius = restingHeight / 2
        canvas.drawRoundRect(pillRect, cornerRadius, cornerRadius, paint)
    }

    fun onGestureStarted() {
        animate().alpha(1f).setDuration(100).start()
    }

    fun onGestureProgress(progress: Float) {
        val targetWidth = restingWidth + (restingWidth * 0.5f * progress)
        widthAnimation.animateToFinalPosition(targetWidth)
    }

    fun onGestureCompleted() {
        animate().alpha(0f).setDuration(200).start()
        widthAnimation.animateToFinalPosition(restingWidth)
    }

    fun onGestureCancelled() {
        animate().alpha(0f).setDuration(200).start()
        widthAnimation.animateToFinalPosition(restingWidth)
    }
}