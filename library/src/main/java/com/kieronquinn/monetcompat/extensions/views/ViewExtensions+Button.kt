package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import android.widget.Button
import androidx.annotation.ColorInt
import com.google.android.material.button.MaterialButton
import com.kieronquinn.monetcompat.core.MonetCompat

/**
 *  Applies Monet colors to the Button, this will *not* work before MonetCompat is
 *  initialized
 */
fun Button.applyMonet(): Button = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getPrimaryColor(context))
}

/**
 *  Applies Monet colors to the MaterialButton, this will *not* work before MonetCompat is
 *  initialized
 */
fun MaterialButton.applyMonet(): MaterialButton = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getAccentColor(context))
}

fun Button.setTint(@ColorInt color: Int){
    backgroundTintList = ColorStateList.valueOf(color)
}