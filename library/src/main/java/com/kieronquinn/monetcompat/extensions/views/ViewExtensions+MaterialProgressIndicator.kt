package com.kieronquinn.monetcompat.extensions.views

import androidx.annotation.ColorInt
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorControlNormal
import com.kieronquinn.monetcompat.extensions.getColorWithAlpha

fun BaseProgressIndicator<*>.applyMonet() = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getSecondaryColor(context) ?: context.getColorControlNormal(), monet.getAccentColor(context))
}

fun BaseProgressIndicator<*>.setTint(@ColorInt trackColor: Int, @ColorInt indicatorColor: Int) {
    if(this !is CircularProgressIndicator) {
        setTrackColor(getColorWithAlpha(trackColor, 0.35f))
    }
    setIndicatorColor(indicatorColor)
}