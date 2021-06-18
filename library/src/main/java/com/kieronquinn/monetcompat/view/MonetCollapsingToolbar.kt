package com.kieronquinn.monetcompat.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.internal.CollapsingTextHelper
import com.kieronquinn.monetcompat.R

/**
 *  (Optional - you can use a normal [CollapsingToolbarLayout] if you  don't want the x override)
 *  A [CollapsingToolbarLayout] with the [CollapsingTextHelper.currentDrawX] value always set to
 *  a static value (defaults to 16dp), which means the large title does not animate on the X-axis.
 *  Override the static value by setting [MonetAppBarLayout.largeTitlePaddingStart] in XML or code.
 */
class MonetCollapsingToolbar : CollapsingToolbarLayout {

    constructor(context: Context) : super(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(
        context,
        attributeSet,
        R.attr.collapsingToolbarLayoutStyle
    )

    constructor(context: Context, attributeSet: AttributeSet?, styleResId: Int) : super(
        context,
        attributeSet,
        styleResId
    )

    private val collapsingTextHelper by lazy {
        CollapsingToolbarLayout::class.java
            .getDeclaredField("collapsingTextHelper").apply {
                isAccessible = true
            }.get(this) as CollapsingTextHelper
    }

    private val currentDrawXField by lazy {
        CollapsingTextHelper::class.java
            .getDeclaredField("currentDrawX").apply {
                isAccessible = true
            }
    }

    private val largeTitlePaddingStartDefault by lazy {
        resources.getDimension(R.dimen.monet_collapsing_toolbar_padding_start_default)
    }

    internal var largeTitlePaddingStart: Float? = null
        get() {
            return field ?: largeTitlePaddingStartDefault
        }

    private var currentDrawX: Float?
        get() = currentDrawXField.get(collapsingTextHelper) as? Float
        set(value) = currentDrawXField.set(collapsingTextHelper, value)

    override fun onDraw(canvas: Canvas?) {
        //Override the collapsingTextHelper x pos
        currentDrawX = largeTitlePaddingStart
        super.onDraw(canvas)
    }

}