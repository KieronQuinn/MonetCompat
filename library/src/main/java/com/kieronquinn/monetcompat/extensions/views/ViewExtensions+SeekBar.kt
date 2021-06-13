package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import android.widget.ProgressBar
import android.widget.SeekBar
import androidx.annotation.ColorInt
import com.kieronquinn.monetcompat.core.MonetCompat

fun SeekBar.applyMonet() = apply {
    val monet = MonetCompat.getInstance()
    (this as ProgressBar).applyMonet()
    setTint(monet.getAccentColor(context))
}

fun SeekBar.setTint(@ColorInt thumbTint: Int){
    thumbTintList = ColorStateList.valueOf(thumbTint)
}