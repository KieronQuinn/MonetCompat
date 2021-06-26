package dev.kdrag0n.monet.colors

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

interface Lch {
    val L: Double
    val C: Double
    val h: Double

    companion object {
        internal fun Lab.toLch(): DoubleArray {
            val hDeg = Math.toDegrees(atan2(b, a))

            return doubleArrayOf(
                L,
                sqrt(a*a + b*b),
                // Normalize the angle, as many will be negative
                if (hDeg < 0) hDeg + 360 else hDeg,
            )
        }

        internal fun Lch.toLab(): DoubleArray {
            val hRad = Math.toRadians(h)

            return doubleArrayOf(
                L,
                C * cos(hRad),
                C * sin(hRad),
            )
        }
    }
}
