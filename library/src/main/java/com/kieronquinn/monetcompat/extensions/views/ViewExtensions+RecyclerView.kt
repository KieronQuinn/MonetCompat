package com.kieronquinn.monetcompat.extensions.views

import android.os.Build
import android.widget.EdgeEffect
import androidx.annotation.ColorInt
import androidx.annotation.RestrictTo
import androidx.core.os.BuildCompat
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.widget.StretchEdgeEffect

/**
 *  Applies Monet colors to the RecyclerView overscroll, this will *not* work before MonetCompat is
 *  initialized
 */
fun RecyclerView.applyMonet(){
    val monet = MonetCompat.getInstance()
    setOverscrollTint(monet.getAccentColor(context))
}

fun RecyclerView.setOverscrollTint(@ColorInt color: Int){
    if(overScrollMode == RecyclerView.OVER_SCROLL_NEVER) return
    //Don't override bounce effect
    if(edgeEffectFactory is MonetEdgeEffectFactory) return
    edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            return EdgeEffect(view.context).apply { setColor(color) }
        }
    }
}

/**
 *  Adds an Android 12-esque stretch overscroll effect to the [RecyclerView], with an optional
 *  [stretchMaxScaleY] to apply to [StretchEdgeEffect.maxScaleY]
 *
 *  Doesn't do anything on Android 12+ as we don't need to apply to that
 */
fun RecyclerView.enableStretchOverscroll(stretchMaxScaleY: Float? = null){
    if(Build.VERSION.SDK_INT >= 31 || BuildCompat.isAtLeastS()) return
    edgeEffectFactory = MonetEdgeEffectFactory(stretchMaxScaleY)
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
class MonetEdgeEffectFactory(private val stretchMaxScaleY: Float? = null): RecyclerView.EdgeEffectFactory() {
    override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
        return StretchEdgeEffect(view.context, view, StretchEdgeEffect.Direction.fromRecyclerViewDirection(direction)).apply {
            if(stretchMaxScaleY != null) maxScaleY = stretchMaxScaleY
        }
    }
}