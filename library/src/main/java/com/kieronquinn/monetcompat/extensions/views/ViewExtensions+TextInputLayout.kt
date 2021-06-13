package com.kieronquinn.monetcompat.extensions.views

import androidx.annotation.ColorInt
import com.google.android.material.textfield.TextInputLayout
import com.kieronquinn.monetcompat.core.MonetCompat

/**
 *  Applies Monet colors to the TextInputLayout, this will *not* work before MonetCompat is
 *  initialized
 */
fun TextInputLayout.applyMonet(): TextInputLayout = apply {
    val monet = MonetCompat.getInstance()
    setTint(monet.getAccentColor(context))
}

fun TextInputLayout.setTint(@ColorInt color: Int){
    boxStrokeColor = color
}