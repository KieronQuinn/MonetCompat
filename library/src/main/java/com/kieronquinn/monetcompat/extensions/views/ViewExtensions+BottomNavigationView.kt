package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorControlNormal
import com.kieronquinn.monetcompat.extensions.getTextColorPrimary

/**
 *  Applies Monet colors to the BottomNavigationView, this will *not* work before MonetCompat is
 *  initialized
 *  @param setBackgroundColor Whether to set the background color of the BottomNavigation to the
 *  secondary background color ([MonetCompat.getBackgroundColor])
 */
fun BottomNavigationView.applyMonet(setBackgroundColor: Boolean = false){
    val monet = MonetCompat.getInstance()
    val backgroundColor = if(setBackgroundColor){
        monet.getBackgroundColor(context)
    } else null
    setTint(monet.getAccentColor(context), backgroundColor)
}

fun BottomNavigationView.setTint(@ColorInt iconColor: Int, @ColorInt backgroundColor: Int? = null){
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
}