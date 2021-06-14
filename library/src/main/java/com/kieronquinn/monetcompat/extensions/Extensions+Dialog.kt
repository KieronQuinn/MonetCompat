package com.kieronquinn.monetcompat.extensions

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AlertDialog
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.core.MonetDialogException
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.DynamicColorScheme

/**
 *  Applies Monet colors to an AlertDialog, this will *not* work before MonetCompat is
 *  initialized
 *  @param onDismiss Block to invoke when the dialog is dismissed. [applyMonet] uses
 *  [AlertDialog.setOnDismissListener], so if you also need the listener you can use this to run
 *  your own code in addition.
 */
fun AlertDialog.applyMonet(onDismiss: ((DialogInterface) -> Unit)? = null) = apply {
    if(!isShowing) throw MonetDialogException()
    val monet = MonetCompat.getInstance()
    val update = {
        val accentColor = monet.getAccentColor(context)
        val disabledColor = monet.getSecondaryColor(context) ?: monet.getPrimaryColor(context)
        val textColor = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_enabled), intArrayOf()),
            intArrayOf(accentColor, disabledColor)
        )
        getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(textColor)
        getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(textColor)
        getButton(AlertDialog.BUTTON_NEUTRAL)?.setTextColor(textColor)
        window!!.decorView.background.apply {
            colorFilter = PorterDuffColorFilter(monet.getBackgroundColor(context), PorterDuff.Mode.SRC_ATOP)
        }
    }
    val listener = object: MonetColorsChangedListener {
        override fun onMonetColorsChanged(monet: MonetCompat, monetColors: DynamicColorScheme, isInitialChange: Boolean) {
            update.invoke()
        }
    }
    update.invoke()
    monet.addMonetColorsChangedListener(listener)
    setOnDismissListener {
        monet.removeMonetColorsChangedListener(listener)
        onDismiss?.invoke(it)
    }
}