package com.hg.qynnlauncher.utils.compose

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * A modifier that provides an iOS/MIUI-style elastic overscroll effect.
 * This is a pure-Compose implementation that uses a `NestedScrollConnection`
 * to intercept scroll events and apply a translation to the content.
 */
fun Modifier.elasticOverscroll(): Modifier = composed {
    val scope = rememberCoroutineScope()
    val translationY = remember { Animatable(0f) }

    val connection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                if (source == NestedScrollSource.Drag && abs(available.y) > 0) {
                    scope.launch {
                        translationY.snapTo(translationY.value + available.y * 0.3f) // Resistance
                    }
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                if (translationY.value != 0f) {
                    scope.launch {
                        translationY.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = 200f))
                    }
                }
                // Don't consume any velocity, let the fling happen normally
                return Velocity.Zero
            }
        }
    }

    this
        .clipToBounds()
        .nestedScroll(connection)
        .graphicsLayer {
            this.translationY = translationY.value
        }
}