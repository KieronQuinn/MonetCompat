package com.kieronquinn.monetcompat.extensions.views

import com.google.android.material.textfield.TextInputEditText
import com.kieronquinn.monetcompat.core.MonetCompat

/**
 *  Applies Monet colors to the TextInputEditText, this will *not* work before MonetCompat is
 *  initialized
 */
fun TextInputEditText.applyMonet() = apply {
    val monet = MonetCompat.getInstance()
    setCursorHandleTint(monet.getAccentColor(context))
}