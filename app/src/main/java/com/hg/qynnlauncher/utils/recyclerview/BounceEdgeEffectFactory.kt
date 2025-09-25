package com.hg.qynnlauncher.utils.recyclerview

import android.graphics.Canvas
import android.widget.EdgeEffect
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.RecyclerView

class BounceEdgeEffectFactory : RecyclerView.EdgeEffectFactory() {

    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return object : EdgeEffect(view.context) {

            private var translationY = 0f
            private var velocityY = 0f

            private val spring = SpringAnimation(view, SpringAnimation.TRANSLATION_Y)
                .setSpring(
                    SpringForce()
                        .setFinalPosition(0f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                        .setStiffness(SpringForce.STIFFNESS_LOW)
                )

            override fun onPull(deltaDistance: Float) {
                super.onPull(deltaDistance)
                handlePull(deltaDistance)
            }

            override fun onPull(deltaDistance: Float, displacement: Float) {
                super.onPull(deltaDistance, displacement)
                handlePull(deltaDistance)
            }

            private fun handlePull(deltaDistance: Float) {
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationYDelta = sign * view.height * deltaDistance * OVERSCROLL_TRANSLATION_MAGNITUDE
                view.translationY += translationYDelta
                translationY += translationYDelta
            }

            override fun onRelease() {
                super.onRelease()
                if (translationY != 0f) {
                    spring.start()
                }
            }

            override fun onAbsorb(velocity: Int) {
                super.onAbsorb(velocity)
                val sign = if (direction == DIRECTION_BOTTOM) -1 else 1
                val translationVelocity = sign * velocity * FLING_TRANSLATION_MAGNITUDE
                velocityY += translationVelocity
                spring.setStartVelocity(velocityY).start()
                velocityY = 0f
            }

            override fun draw(canvas: Canvas?): Boolean {
                return false
            }

            override fun isFinished(): Boolean {
                return !spring.isRunning
            }
        }
    }

    companion object {
        const val OVERSCROLL_TRANSLATION_MAGNITUDE = 0.2f
        const val FLING_TRANSLATION_MAGNITUDE = 0.5f
    }
}