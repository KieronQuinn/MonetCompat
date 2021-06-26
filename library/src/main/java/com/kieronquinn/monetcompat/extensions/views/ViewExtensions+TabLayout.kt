package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.tabs.TabLayout
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getTextColorPrimary

/**
 *  Applies Monet colors to the TabLayout, this will *not* work before MonetCompat is
 *  initialized
 *  @param setBackgroundColor Whether to set the background color of the TabLayout to the
 *  secondary background color ([MonetCompat.getBackgroundColor])
 */
fun TabLayout.applyMonet(setBackgroundColor: Boolean = false){
    val monet = MonetCompat.getInstance()
    val backgroundColor = if(setBackgroundColor){
        monet.getBackgroundColor(context)
    } else null
    setTint(monet.getAccentColor(context), backgroundColor)
}

fun TabLayout.setTint(@ColorInt tabAccentColor: Int, @ColorInt backgroundColor: Int? = null){
    post {
        val uncheckedColor = context.getTextColorPrimary()
        //Nb: This doesn't seem to work without changing the text color too
        setSelectedTabIndicatorColor(tabAccentColor)
        tabTextColors = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(tabAccentColor, uncheckedColor)
        )
        backgroundColor?.let {
            setBackgroundColor(it)
        }
    }
    tabRippleColor = ColorStateList.valueOf(tabAccentColor)
}