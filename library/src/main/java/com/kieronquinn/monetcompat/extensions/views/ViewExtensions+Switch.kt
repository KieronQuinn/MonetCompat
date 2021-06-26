package com.kieronquinn.monetcompat.extensions.views

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.kieronquinn.monetcompat.R
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.isDarkMode
import com.kieronquinn.monetcompat.extensions.toArgb

/**
 *  Applies Monet colors to the SwitchCompat, this will *not* work before MonetCompat is
 *  initialized
 */
fun SwitchCompat.applyMonet(): SwitchCompat = apply {
    val monet = MonetCompat.getInstance()
    val checkedTrackColor = if(context.isDarkMode){
        monet.getMonetColors().accent1[400]?.toArgb()
    }else{
        monet.getMonetColors().accent1[300]?.toArgb()
    }
    setTint(checkedTrackColor, monet.getSecondaryColor(context), monet.getAccentColor(context))
}

fun SwitchCompat.setTint(@ColorInt checkedTrackColor: Int?, @ColorInt unCheckedTrackColor: Int?, @ColorInt thumbColor: Int){
    val defaultColor = ContextCompat.getColor(context, R.color.switch_thumb_normal_material)
    trackTintList = ColorStateList(
        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
        intArrayOf(checkedTrackColor ?: defaultColor, unCheckedTrackColor ?: defaultColor)
    )
    thumbTintList = ColorStateList.valueOf(thumbColor)
    overrideRippleColor(thumbColor)
}