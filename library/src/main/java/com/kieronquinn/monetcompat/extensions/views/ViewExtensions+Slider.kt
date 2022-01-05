package com.kieronquinn.monetcompat.extensions.views

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.slider.Slider
import com.google.android.material.tooltip.TooltipDrawable
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorWithAlpha

/**
 *  Applies Monet colors to the Slider, this will *not* work before MonetCompat is
 *  initialized
 */
fun Slider.applyMonet(): Slider = apply {
    val monet = MonetCompat.getInstance()
    val accent = monet.getAccentColor(context)
    setTint(monet.getSecondaryColor(context), accent, monet.getPrimaryColor(context), accent)
}

fun Slider.setTint(@ColorInt trackColor: Int, @ColorInt thumbColor: Int, @ColorInt tickInactiveColor: Int? = null, @ColorInt tooltipColor: Int? = null){
    trackInactiveTintList = ColorStateList.valueOf(getColorWithAlpha(trackColor, 0.35f))
    trackActiveTintList = ColorStateList.valueOf(trackColor)
    thumbTintList = ColorStateList.valueOf(thumbColor)
    haloTintList = ColorStateList.valueOf(getColorWithAlpha(thumbColor, 0.35f))
    if(tickInactiveColor != null) {
        tickInactiveTintList = ColorStateList.valueOf(tickInactiveColor)
    }
    if(tooltipColor != null){
        setTooltipColor(tooltipColor)
    }
}

@SuppressLint("RestrictedApi")
fun Slider.setTooltipColor(color: Int) {
    runCatching {
        val baseSlider = Slider::class.java.superclass ?: return
        val labels = baseSlider.getDeclaredField("labels").apply {
            isAccessible = true
        }
        (labels.get(this) as List<TooltipDrawable>).forEach {
            it.setTint(color)
        }
    }
}