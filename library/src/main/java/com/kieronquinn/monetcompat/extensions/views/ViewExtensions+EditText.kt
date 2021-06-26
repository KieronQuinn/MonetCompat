package com.kieronquinn.monetcompat.extensions.views

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.*
import android.os.Build
import android.util.TypedValue
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorWithAlpha
import java.lang.reflect.Field
import kotlin.math.sqrt

/**
 *  Applies Monet colors to the EditText, this will *not* work before MonetCompat is
 *  initialized
 */
fun EditText.applyMonet(){
    val monet = MonetCompat.getInstance()
    val accentColor = monet.getAccentColor(context)
    setUnderlineTint(accentColor)
    setCursorHandleTint(accentColor)
}

fun EditText.setUnderlineTint(@ColorInt color: Int){
    backgroundTintList = ColorStateList.valueOf(color)
}

/**
 *  Attempts to apply tints to the cursor, highlight and handles.
 *  This isn't guaranteed to work on all devices, but uses public APIs where possible, and catches
 *  reflection exceptions and ignores them, so at least we don't crash on devices that don't
 *  support it.
 */
fun EditText.setCursorHandleTint(@ColorInt color: Int){
    highlightColor = getColorWithAlpha(color, 0.5f)
    setCursorDrawableColor(this, color)
    setHandlesColor(this, color)
}

@Suppress("DEPRECATION")
private fun setCursorDrawableColor(editText: EditText, color: Int) {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
        editText.textCursorDrawable?.setTint(color)
        return
    }
    try {
        val fCursorDrawableRes: Field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        fCursorDrawableRes.isAccessible = true
        val mCursorDrawableRes: Int = fCursorDrawableRes.getInt(editText)
        val fEditor: Field = TextView::class.java.getDeclaredField("mEditor")
        fEditor.isAccessible = true
        val editor: Any = fEditor.get(editText) ?: return
        val clazz: Class<*> = editor.javaClass
        val fCursorDrawable: Field = clazz.getDeclaredField("mCursorDrawable")
        fCursorDrawable.isAccessible = true
        val drawables = arrayOfNulls<Drawable>(2)
        drawables[0] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
        drawables[1] = ContextCompat.getDrawable(editText.context, mCursorDrawableRes)
        drawables[0]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        drawables[1]?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        fCursorDrawable.set(editor, drawables)
    } catch (e: Throwable) {
        //Ignored, there's no other way of doing this
    }
}

private fun setHandlesColor(textView: TextView, @ColorInt color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val size = 22.spToPx(textView.context).toInt()
        val corner = size.toFloat() / 2
        val inset = 10.spToPx(textView.context).toInt()

        //left drawable
        val drLeft = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(color, color))
        drLeft.setSize(size, size)
        drLeft.cornerRadii = floatArrayOf(corner, corner, 0f, 0f, corner, corner, corner, corner)
        textView.setTextSelectHandleLeft(InsetDrawable(drLeft, inset, 0, inset, inset))

        //right drawable
        val drRight = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(color, color))
        drRight.setSize(size, size)
        drRight.cornerRadii = floatArrayOf(0f, 0f, corner, corner, corner, corner, corner, corner)
        textView.setTextSelectHandleRight(InsetDrawable(drRight, inset, 0, inset, inset))

        //middle drawable
        val drMiddle = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(color, color))
        drMiddle.setSize(size, size)
        drMiddle.cornerRadii = floatArrayOf(0f, 0f, corner, corner, corner, corner, corner, corner)
        val mInset = (sqrt(2f.toDouble()) * corner - corner).toInt()
        val insetDrawable = InsetDrawable(drMiddle, mInset, mInset, mInset, mInset)
        val rotateDrawable = RotateDrawable()
        rotateDrawable.drawable = insetDrawable
        rotateDrawable.toDegrees = 45f
        rotateDrawable.level = 10000
        textView.setTextSelectHandle(rotateDrawable)
        return
    }

    try {
        val editorField = try {
            TextView::class.java.getDeclaredField("mEditor")
                .apply { if (!isAccessible) isAccessible = true }
        } catch (t: Throwable) {
            null
        }
        val editor = if (editorField == null) textView else editorField[textView]
        val editorClass: Class<*> =
            if (editorField == null) TextView::class.java else editor.javaClass

        val handleNames = arrayOf(
            "mSelectHandleLeft",
            "mSelectHandleRight",
            "mSelectHandleCenter"
        )
        val resNames = arrayOf(
            "mTextSelectHandleLeftRes",
            "mTextSelectHandleRightRes",
            "mTextSelectHandleRes"
        )
        for (i in handleNames.indices) {
            val handleField = editorClass.getDeclaredField(handleNames[i])
                .apply { isAccessible = true }

            val handleDrawable = handleField.get(editor) as? Drawable?
                ?: TextView::class.java.getDeclaredField(resNames[i])
                    .apply { isAccessible = true }
                    .run { getInt(textView) }
                    .let { ContextCompat.getDrawable(textView.context, it) }

            if (handleDrawable != null) {
                val tinted = tintDrawable(handleDrawable, color)
                handleField.set(editor, tinted)
            }
        }
    } catch (e: Exception) {
        //Ignored as there's no other way of doing this
    }
}

private fun Number.spToPx(context: Context? = null): Float {
    val res = context?.resources ?: Resources.getSystem()
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), res.displayMetrics)
}

private fun tintDrawable(drawable: Drawable, @ColorInt color: Int): Drawable {
    (drawable as? VectorDrawableCompat)
        ?.apply { setTintList(ColorStateList.valueOf(color)) }
        ?.let { return it }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        (drawable as? VectorDrawable)
            ?.apply { setTintList(ColorStateList.valueOf(color)) }
            ?.let { return it }
    }

    val wrappedDrawable = DrawableCompat.wrap(drawable)
    DrawableCompat.setTint(wrappedDrawable, color)
    return DrawableCompat.unwrap(wrappedDrawable)
}