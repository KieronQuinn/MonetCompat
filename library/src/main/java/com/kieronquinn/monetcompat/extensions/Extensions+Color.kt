package com.kieronquinn.monetcompat.extensions

import android.graphics.Color
import kotlin.math.roundToInt
import dev.kdrag0n.monet.colors.Color as MonetColor

/**
 *  To avoid editing the core Monet code by kdrag0n, these are extensions instead
 */
fun MonetColor.toArgb(): Int {
    return toLinearSrgb().toSrgb().quantize8()
}

internal fun getColorWithAlpha(color: Int, ratio: Float): Int {
    return Color.argb((Color.alpha(color) * ratio).roundToInt(), Color.red(color), Color.green(color), Color.blue(color))
}