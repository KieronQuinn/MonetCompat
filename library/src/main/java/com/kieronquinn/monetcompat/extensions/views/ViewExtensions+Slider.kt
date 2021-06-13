package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import android.util.Log
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
    setTint(monet.getSecondaryColor(context) ?: monet.getPrimaryColor(context), monet.getAccentColor(context))
}

fun Slider.setTint(@ColorInt trackColor: Int, @ColorInt thumbColor: Int){
    trackInactiveTintList = ColorStateList.valueOf(getColorWithAlpha(trackColor, 0.35f))
    trackActiveTintList = ColorStateList.valueOf(trackColor)
    thumbTintList = ColorStateList.valueOf(thumbColor)
    haloTintList = ColorStateList.valueOf(getColorWithAlpha(thumbColor, 0.35f))
}