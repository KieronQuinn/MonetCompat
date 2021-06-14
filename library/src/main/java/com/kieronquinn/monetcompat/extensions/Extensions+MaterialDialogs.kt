package com.kieronquinn.monetcompat.extensions

import android.R
import android.content.res.ColorStateList
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.callbacks.onShow
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.DynamicColorScheme

/**
 *  Applies Monet colors to a MaterialDialog, this will *not* work before MonetCompat is
 *  initialized
 *  @param applyNavigationBarColor Set to true to apply the [MonetCompat.getBackgroundColor] color
 *  to the window navigation bar color (useful for Bottom Sheets)
 */
fun MaterialDialog.applyMonet(applyNavigationBarColor: Boolean = false){
    val monet = MonetCompat.getInstance()
    val updateBackground = {
        val backgroundColor = monet.getBackgroundColor(context)
        view.titleLayout.setBackgroundColor(backgroundColor)
        view.contentLayout.setBackgroundColor(backgroundColor)
        view.buttonsLayout?.setBackgroundColor(backgroundColor)
        if(applyNavigationBarColor){
            window!!.navigationBarColor = backgroundColor
        }
    }
    val updateButtons = {
        val accentColor = monet.getAccentColor(context)
        val disabledColor = monet.getSecondaryColor(context) ?: monet.getPrimaryColor(context)
        val textColor = ColorStateList(
            arrayOf(intArrayOf(R.attr.state_enabled), intArrayOf()),
            intArrayOf(accentColor, disabledColor)
        )
        getActionButton(WhichButton.POSITIVE)?.setTextColor(textColor)
        getActionButton(WhichButton.NEGATIVE)?.setTextColor(textColor)
        getActionButton(WhichButton.NEUTRAL)?.setTextColor(textColor)
    }
    val changeListener = object: MonetColorsChangedListener {
        override fun onMonetColorsChanged(monet: MonetCompat, monetColors: DynamicColorScheme, isInitialChange: Boolean) {
            updateBackground.invoke()
            updateButtons.invoke()
        }
    }
    monet.addMonetColorsChangedListener(changeListener)
    updateBackground.invoke()
    onShow {
        view.post {
            updateButtons.invoke()
        }
    }
    onDismiss {
        monet.removeMonetColorsChangedListener(changeListener)
    }
}