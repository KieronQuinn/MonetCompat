package com.kieronquinn.monetcompat.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Switch
import androidx.annotation.ColorInt
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import com.kieronquinn.monetcompat.R
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.toArgb
import com.kieronquinn.monetcompat.extensions.views.overrideRippleColor
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.ColorScheme
import dev.kdrag0n.monet.theme.DynamicColorScheme

/**
 *  A full-width Switch designed to look like the primary ones in Android 12's Settings app. It has
 *  its own background, tinted to Monet's colors, with the [Switch] thumb set to the same color,
 *  and the track a darker color. The background/track color changes depending on the switch state.
 */
open class MonetSwitch: SwitchCompat, MonetColorsChangedListener {

    constructor(context: Context): super(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet, R.attr.switchStyle) {
        readAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, styleResId: Int): super(context, attributeSet, styleResId){
        readAttributes(attributeSet)
    }

    private fun readAttributes(attributeSet: AttributeSet?){
        if(attributeSet == null) return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MonetSwitch)
        var textAppearance = typedArray.getResourceId(R.styleable.MonetSwitch_android_textAppearance, R.style.TextAppearance_AppCompat_Medium)
        //Sometimes the field will default to TextAppearance.Material so we need to counter that
        if(textAppearance == android.R.style.TextAppearance_Material) textAppearance = R.style.TextAppearance_AppCompat_Medium
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setTextAppearance(textAppearance)
        }else{
            setTextAppearance(context, textAppearance)
        }
        val textColor = typedArray.getColor(R.styleable.MonetSwitch_android_textColor, Color.BLACK)
        setTextColor(textColor)
        val text = typedArray.getText(R.styleable.MonetSwitch_android_text) ?: ""
        setText(text)
        typedArray.recycle()
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private val monetSwitchPadding by lazy {
        context.resources.getDimension(R.dimen.monet_switch_padding).toInt()
    }

    private val monetSwitchPaddingStart by lazy {
        context.resources.getDimension(R.dimen.monet_switch_padding_start).toInt()
    }

    private val monetSwitchPaddingEnd by lazy {
        context.resources.getDimension(R.dimen.monet_switch_padding_end).toInt()
    }

    init {
        textOn = ""
        textOff = ""
        isClickable = true
        isFocusable = true
        gravity = Gravity.CENTER_VERTICAL
        minHeight = resources.getDimension(R.dimen.monet_switch_height).toInt()
        if(layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            setPadding(
                monetSwitchPaddingEnd,
                monetSwitchPadding,
                monetSwitchPaddingStart,
                monetSwitchPadding
            )
        }else{
            setPadding(
                monetSwitchPaddingStart,
                monetSwitchPadding,
                monetSwitchPaddingEnd,
                monetSwitchPadding
            )
        }
        background = ContextCompat.getDrawable(context, R.drawable.switch_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = ContextCompat.getDrawable(context, R.drawable.switch_foreground)
        }
        trackDrawable = ContextCompat.getDrawable(context, R.drawable.switch_track)
        thumbDrawable = ContextCompat.getDrawable(context, R.drawable.switch_thumb)
        addRipple()
    }

    private fun View.addRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(!isInEditMode) {
            monet.addMonetColorsChangedListener(this, true)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        monet.removeMonetColorsChangedListener(this)
    }

    override fun onMonetColorsChanged(
        monet: MonetCompat,
        monetColors: ColorScheme,
        isInitialChange: Boolean
    ) {
        applyMonet()
    }

    private fun applyMonet() = with(monet) {
        val uncheckedTrackColor = monet.getMonetColors().accent1[600]?.toArgb() ?: monet.getAccentColor(context, false)
        val checkedTrackColor = monet.getMonetColors().accent1[300]?.toArgb() ?: uncheckedTrackColor
        val checkedThumbColor = monet.getPrimaryColor(context, false)
        val uncheckedThumbColor = monet.getSecondaryColor(context, false)
        setTint(checkedTrackColor, uncheckedTrackColor, uncheckedThumbColor, checkedThumbColor)
    }

    private fun setTint(@ColorInt checkedTrackColor: Int, @ColorInt unCheckedTrackColor: Int, @ColorInt uncheckedThumbColor: Int, @ColorInt checkedThumbColor: Int){
        trackTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(checkedTrackColor, unCheckedTrackColor)
        )
        val bgTintList = ColorStateList(
            arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
            intArrayOf(checkedThumbColor, uncheckedThumbColor)
        )
        thumbTintList = bgTintList
        backgroundTintList = bgTintList
        backgroundTintMode = PorterDuff.Mode.SRC_ATOP
        overrideRippleColor(colorStateList = bgTintList)
    }

}