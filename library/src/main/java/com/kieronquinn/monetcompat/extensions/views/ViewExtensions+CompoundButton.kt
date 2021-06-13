package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.util.Log
import android.widget.CompoundButton
import androidx.annotation.ColorInt
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorControlNormal

/**
 *  Applies Monet colors to the CompoundButton, this will *not* work before MonetCompat is
 *  initialized
 */
fun CompoundButton.applyMonet(): CompoundButton = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getAccentColor(context))
}

fun CompoundButton.setTint(@ColorInt color: Int) {
    val uncheckedColor = context.getColorControlNormal()
    buttonTintList = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
        intArrayOf(color, uncheckedColor)
    )
    overrideRippleColor(color)
}