package com.kieronquinn.monetcompat.extensions.views

import android.os.Build
import android.util.Log
import android.view.View
import android.widget.EdgeEffect
import android.widget.HorizontalScrollView
import android.widget.ScrollView
import androidx.annotation.ColorInt
import androidx.core.widget.NestedScrollView
import com.kieronquinn.monetcompat.core.MonetCompat

/**
 *  Applies Monet colors to the ScrollView overscroll, this will *not* work before MonetCompat is
 *  initialized. Requires Android Q+, otherwise it will do nothing.
 */
fun ScrollView.applyMonet(){
    val monet = MonetCompat.getInstance()
    setOverscrollTint(monet.getAccentColor(context))
}

fun ScrollView.setOverscrollTint(@ColorInt color: Int){
    if(overScrollMode == ScrollView.OVER_SCROLL_NEVER) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        topEdgeEffectColor = color
        bottomEdgeEffectColor = color
    }
}

/**
 *  Applies Monet colors to the NestedScrollView overscroll, this will *not* work before MonetCompat
 *  is initialized.
 */
fun NestedScrollView.applyMonet(){
    val monet = MonetCompat.getInstance()
    setOverscrollTint(monet.getAccentColor(context))
}

/**
 *  **Note:** Uses reflection to set the [EdgeEffect]s. There's no official API for this.
 *  By iterating `declaredFields` and checking `type`, we can still change the effect while
 *  obfuscated, albeit with a slight performance degradation.
 */
fun NestedScrollView.setOverscrollTint(@ColorInt color: Int){
    if(overScrollMode == View.OVER_SCROLL_NEVER) return
    val edgeGlowFields =
        NestedScrollView::class.java.declaredFields.filter { it.type == EdgeEffect::class.java }
    edgeGlowFields.forEach {
        it.isAccessible = true
        it.set(this, EdgeEffect(context).apply {
            setColor(color)
        })
    }
}

/**
 *  Applies Monet colors to the ScrollView overscroll, this will *not* work before MonetCompat is
 *  initialized. Requires Android Q+, otherwise it will do nothing.
 */
fun HorizontalScrollView.applyMonet(){
    val monet = MonetCompat.getInstance()
    setOverscrollTint(monet.getAccentColor(context))
}

fun HorizontalScrollView.setOverscrollTint(@ColorInt color: Int){
    if(overScrollMode == ScrollView.OVER_SCROLL_NEVER) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        leftEdgeEffectColor = color
        rightEdgeEffectColor = color
    }
}