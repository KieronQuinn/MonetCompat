package com.kieronquinn.monetcompat.extensions

import android.content.res.ColorStateList
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.kieronquinn.monetcompat.core.MonetCompat

/**
 *  Applies Monet to a Snackbar using the newer Material theme (rounded)
 */
fun Snackbar.applyMonet() = apply {
    val monet = MonetCompat.getInstance()
    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).setTextColor(monet.getAccentColor(context, !context.isDarkMode))
    view.backgroundTintList = ColorStateList.valueOf(monet.getPrimaryColor(context, !context.isDarkMode))
}

/**
 *  Applies Monet to a Snackbar using the older Material theme (non-rounded)
 */
fun Snackbar.applyMonetLegacy() = apply {
    val monet = MonetCompat.getInstance()
    view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action).setTextColor(monet.getAccentColor(context, true))
    view.backgroundTintList = ColorStateList.valueOf(monet.getPrimaryColor(context, true))
}