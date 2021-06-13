package com.kieronquinn.monetcompat.extensions.views

import android.widget.EdgeEffect
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.kieronquinn.monetcompat.core.MonetCompat

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
    edgeEffectFactory = object : RecyclerView.EdgeEffectFactory() {
        override fun createEdgeEffect(view: RecyclerView, direction: Int): EdgeEffect {
            return EdgeEffect(view.context).apply { setColor(color) }
        }
    }
}