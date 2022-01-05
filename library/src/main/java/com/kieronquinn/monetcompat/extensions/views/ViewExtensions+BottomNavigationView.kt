package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorControlNormal
import com.kieronquinn.monetcompat.extensions.isDarkMode
import com.kieronquinn.monetcompat.extensions.toArgb

/**
 *  Applies Monet colors to the BottomNavigationView, this will *not* work before MonetCompat is
 *  initialized
 *  @param setBackgroundColor Whether to set the background color of the BottomNavigation to the
 *  secondary background color ([MonetCompat.getBackgroundColor])
 *  @param md3Style Disable if you are not using the MD3 styled bottom navigation
 */
fun BottomNavigationView.applyMonet(setBackgroundColor: Boolean = false, md3Style: Boolean = true){
    val monet = MonetCompat.getInstance()
    val backgroundColor = if(setBackgroundColor){
        monet.getBackgroundColor(context)
    } else null
    val indicatorColor = if(md3Style){
        if (context.isDarkMode) {
            monet.getMonetColors().accent2[700]?.toArgb()
        } else {
            monet.getMonetColors().accent2[200]?.toArgb()
        }
    }else null
    setTint(monet.getAccentColor(context), backgroundColor, indicatorColor)
}

fun BottomNavigationView.setTint(@ColorInt iconColor: Int, @ColorInt backgroundColor: Int? = null, @ColorInt indicatorColor: Int? = null){
    val uncheckedColor = context.getColorControlNormal()
    itemIconTintList = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
        intArrayOf(iconColor, uncheckedColor)
    )
    itemTextColor = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
        intArrayOf(iconColor, uncheckedColor)
    )
    backgroundColor?.let {
        setBackgroundColor(it)
    }
    itemRippleColor = ColorStateList.valueOf(iconColor)
    indicatorColor?.let {
        itemActiveIndicatorColor = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf()),
            intArrayOf(indicatorColor, Color.TRANSPARENT)
        )
    }
}