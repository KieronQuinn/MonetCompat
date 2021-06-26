package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.ColorFilter
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import com.google.android.material.progressindicator.BaseProgressIndicator
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorControlNormal

/**
 *  Applies Monet colors to the ProgressBar, this will *not* work before MonetCompat is
 *  initialized
 */
fun ProgressBar.applyMonet() = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getSecondaryColor(context), monet.getAccentColor(context))
}

fun ProgressBar.setTint(@ColorInt trackColor: Int, @ColorInt barColor: Int){
    progressBackgroundTintList = ColorStateList.valueOf(trackColor)
    progressTintList = ColorStateList.valueOf(barColor)
    indeterminateDrawable.setTint(barColor)
}