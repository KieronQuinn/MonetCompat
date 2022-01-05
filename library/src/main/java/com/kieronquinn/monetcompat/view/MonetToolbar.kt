package com.kieronquinn.monetcompat.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ScrollingView
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.kieronquinn.monetcompat.R
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorWithAlpha
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.ColorScheme

/**
 *  A [MaterialToolbar] that has Monet's [MonetCompat.getBackgroundColor] when the scrollable view on-screen
 *  is not scrolled, and [MonetCompat.getBackgroundColorSecondary] when scrolled. The secondary
 *  background color has transparency of [secondaryBackgroundAlpha] (default 85%), allowing you to
 *  see the scrolled list content behind it. Elevation is also disabled.
 */
open class MonetToolbar: MaterialToolbar, MonetColorsChangedListener {

    /**
     *  The alpha ratio of the [Toolbar] when there is a scrolled list showing under it. Defaults to
     *  `0.85f` (85%)
     */
    var secondaryBackgroundAlpha = 0.95f

    /**
     *  Whether to automatically apply padding to the attached [ScrollingView] of the height of
     *  this Toolbar. This is useful to make the list scroll underneath the [Toolbar] without
     *  clipping. Defaults to true.
     */
    var applyPaddingToScrollingView = true

    /**
     *  Extra top padding to add to the value applied to the attached [ScrollingView]. Use this to
     *  add space between your [Toolbar] and content. Only applies if [applyPaddingToScrollingView]
     *  is set to `true`, default value is `0`.
     */
    var extraTopPadding = 0

    /**
     *  Set a custom [NestedScrollView.OnScrollChangeListener]. This is required as [MonetToolbar]
     *  uses an internal one of its own and only one can be attached at a time. If you need to
     *  remove [MonetToolbar] at runtime, call [teardown] which will re-attach this listener to
     *  the [scrollableView] ([NestedScrollView]) as part of the process.
     */
    var customNestedScrollViewListener: NestedScrollView.OnScrollChangeListener? = null

    constructor(context: Context): super(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet, R.attr.toolbarStyle) {
        readAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, styleResId: Int): super(context, attributeSet, styleResId){
        readAttributes(attributeSet)
    }

    private fun readAttributes(attributeSet: AttributeSet?){
        if(attributeSet == null) return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MonetToolbar)
        extraTopPadding = typedArray.getDimension(R.styleable.MonetToolbar_extraPadding, extraTopPadding.toFloat()).toInt()
        secondaryBackgroundAlpha = typedArray.getFloat(R.styleable.MonetToolbar_secondaryBackgroundAlpha, secondaryBackgroundAlpha)
        applyPaddingToScrollingView = typedArray.getBoolean(R.styleable.MonetToolbar_applyPaddingToScrollingView, applyPaddingToScrollingView)
        typedArray.recycle()
    }

    init {
        outlineProvider = null
        elevation = resources.getDimension(R.dimen.monet_toolbar_fake_elevation)
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    private val recyclerViewScrollingListener = object: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            onScroll(recyclerView.computeVerticalScrollOffset())
        }
    }

    private val nestedScrollViewScrollingListener = NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
        customNestedScrollViewListener?.onScrollChange(v, scrollX, scrollY, oldScrollX, oldScrollY)
        onScroll(scrollY)
    }

    /**
     *  The scrollable view attached to this [MonetToolbar]. Can be a [RecyclerView] or
     *  [NestedScrollView]
     */
    private var scrollableView: ScrollingView? = null
        set(value) {
            if(field != null){
                field?.removeScrollingListener()
            }
            field = value
            field?.addScrollingListener()
            (field as? View)?.setupPadding()
        }

    /**
     *  Set up [MonetToolbar] with a [ScrollingView]. Supported scrolling views:
     *  - [RecyclerView]
     *
     *  - [NestedScrollView]
     */
    fun setupWithScrollableView(scrollingView: ScrollingView){
        this.scrollableView = scrollingView
    }

    /**
     *  Teardown the attachment to the [ScrollingView]. If you are using a [NestedScrollView]
     *  and have set a [customNestedScrollViewListener], your [customNestedScrollViewListener]
     *  will be directly attached to the [NestedScrollView] as part of this process
     */
    fun teardown(){
        //If it's a NestedScrollView & we have a custom listener, don't disconnect it
        if(scrollableView is NestedScrollView && customNestedScrollViewListener != null){
            val localScrollableView = scrollableView
            this.scrollableView = null
            (localScrollableView as NestedScrollView).setOnScrollChangeListener(customNestedScrollViewListener)
        }else{
            this.scrollableView = null
        }
        customNestedScrollViewListener = null
    }

    private var isSecondaryBackground = false
    private var backgroundAnimation: ValueAnimator? = null
    private fun setSecondaryBackground(enabled: Boolean, force: Boolean = false){
        if((enabled == isSecondaryBackground && !force) || isInEditMode) return
        isSecondaryBackground = enabled
        backgroundAnimation?.cancel()
        val newBackground = if(enabled){
            getColorWithAlpha(monet.getBackgroundColorSecondary(context) ?: monet.getBackgroundColor(context), secondaryBackgroundAlpha)
        }else{
            monet.getBackgroundColor(context)
        }
        val currentBackground = (background as? ColorDrawable)?.color ?: monet.getBackgroundColor(context)
        backgroundAnimation = ValueAnimator.ofArgb(currentBackground, newBackground).apply {
            addUpdateListener {
                val color = ColorDrawable(it.animatedValue as Int)
                background = color
                (parent as? AppBarLayout)?.background = color
            }
            duration = 250
            start()
        }
    }

    private fun onScroll(scrollY: Int){
        setSecondaryBackground(scrollY > 0)
    }

    override fun onMonetColorsChanged(
        monet: MonetCompat,
        monetColors: ColorScheme,
        isInitialChange: Boolean
    ) {
        setSecondaryBackground(isSecondaryBackground, true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(isInEditMode) return
        monet.addMonetColorsChangedListener(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        monet.removeMonetColorsChangedListener(this)
        //Remove the listener from the scrolling view
        teardown()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        if(changed){
            //Update the Toolbar padding
            (scrollableView as? View)?.setupPadding()
        }
    }

    /**
     *  Adds the [recyclerViewScrollingListener] for a [RecyclerView] and the
     *  [nestedScrollViewScrollingListener] for a [NestedScrollView]
     */
    private fun ScrollingView.addScrollingListener(){
        when(this){
            is RecyclerView -> addOnScrollListener(recyclerViewScrollingListener)
            is NestedScrollView -> setOnScrollChangeListener(nestedScrollViewScrollingListener)
        }
    }

    /**
     *  Nullifies the scroll listener from a [RecyclerView], and sets an empty one to
     *  [NestedScrollView]
     */
    private fun ScrollingView.removeScrollingListener(){
        when(this){
            is RecyclerView -> removeOnScrollListener(recyclerViewScrollingListener)
            is NestedScrollView -> NestedScrollView.OnScrollChangeListener { _, _, _, _, _ -> }
        }
    }

    private fun View.setupPadding(){
        if(applyPaddingToScrollingView){
            clipToPadding = false
            updatePadding(top = this@MonetToolbar.measuredHeight + extraTopPadding)
        }
    }

}
