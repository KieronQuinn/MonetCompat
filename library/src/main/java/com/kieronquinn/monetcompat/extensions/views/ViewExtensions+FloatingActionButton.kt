package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.isDarkMode

/**
 *  Applies Monet colors to the FloatingActionButton, this will *not* work before MonetCompat is
 *  initialized
 */
fun FloatingActionButton.applyMonet(): FloatingActionButton = apply {
    val monet = MonetCompat.getInstance()
    //The FAB clashes with backgrounds due to its single icon color, so we adapt based on light/dark
    if(context.isDarkMode) {
        setTint(monet.getPrimaryColor(context))
    }else{
        setTint(monet.getAccentColor(context))
    }
}

fun FloatingActionButton.setTint(@ColorInt color: Int){
    backgroundTintList = ColorStateList.valueOf(color)
}