package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.slider.Slider
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorWithAlpha

/**
 *  Applies Monet colors to the Slider, this will *not* work before MonetCompat is
 *  initialized
 */
fun Slider.applyMonet(): Slider = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getSecondaryColor(context), monet.getAccentColor(context), monet.getPrimaryColor(context))
}

fun Slider.setTint(@ColorInt trackColor: Int, @ColorInt thumbColor: Int, @ColorInt tickInactiveColor: Int? = null){
    trackInactiveTintList = ColorStateList.valueOf(getColorWithAlpha(trackColor, 0.35f))
    trackActiveTintList = ColorStateList.valueOf(trackColor)
    thumbTintList = ColorStateList.valueOf(thumbColor)
    haloTintList = ColorStateList.valueOf(getColorWithAlpha(thumbColor, 0.35f))
    if(tickInactiveColor != null) {
        tickInactiveTintList = ColorStateList.valueOf(tickInactiveColor)
    }
}