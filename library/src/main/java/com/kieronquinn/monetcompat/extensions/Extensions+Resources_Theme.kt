package com.kieronquinn.monetcompat.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.content.res.use

/**
 *  Gets an attribute color for a given Resources.Theme theme
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@ColorRes
internal fun Resources.Theme.getAttributeColor(@AttrRes attribute: Int, @ColorInt defColor: Int = Color.TRANSPARENT): Int? {
    return obtainStyledAttributes(
        intArrayOf(attribute)
    ).use {
        it.getResourceId(0, defColor)
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ColorInt
internal fun Context.getColorControlNormal(): Int {
    return ContextCompat.getColor(this, theme.getAttributeColor(android.R.attr.colorControlNormal) ?: android.R.color.darker_gray)
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
@ColorInt
internal fun Context.getTextColorPrimary(): Int {
    return ContextCompat.getColor(this, theme.getAttributeColor(android.R.attr.textColorPrimary) ?: android.R.color.black)
}