package com.kieronquinn.monetcompat.extensions.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat

/**
 *  Overrides the ripple drawable background for a view to be a specific color
 *  @param color The color the ripple should be
 */
fun View.overrideRippleColor(@ColorInt color: Int? = null, colorStateList: ColorStateList? = null){
    val backgroundRipple = background as? RippleDrawable ?: foregroundCompat as? RippleDrawable ?: return
    if(colorStateList != null){
        backgroundRipple.setColor(colorStateList)
    }else if(color != null){
        backgroundRipple.setColor(ColorStateList.valueOf(color))
    }
}

private val View.foregroundCompat: Drawable?
    get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground
        }else null
    }

/**
 *  Gets the default [android.R.attr.selectableItemBackground] but with a ripple color
 *  specified by [color]
 *  @param color The color the ripple should be
 */
fun Context.getRippleBackground(@ColorInt color: Int): Drawable? {
    val resourceId = with(TypedValue()) {
        theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        resourceId
    }
    return ContextCompat.getDrawable(this, resourceId).apply {
        (this as? RippleDrawable)?.setColor(ColorStateList.valueOf(color))
    }
}