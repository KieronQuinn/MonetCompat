package com.kieronquinn.monetcompat.view

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StyleableRes
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.kieronquinn.monetcompat.R
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.getColorWithAlpha
import com.kieronquinn.monetcompat.extensions.getTextColorPrimary
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.DynamicColorScheme

/**
 *  An AppBarLayout that is designed to look like the one in Android 12's Settings app.
 *
 *  It uses a custom title animation, fading between the [Toolbar] title and [CollapsingToolbarLayout]
 *  title when collapsed/expanded, and shows Monet's [MonetCompat.getBackgroundColorSecondary] on
 *  the [Toolbar] background when not expanded.
 *
 *  In XML, you should declare `app:toolbarId="<your toolbar ID>"`, of the child toolbar of this view
 *  You should also have a [CollapsingToolbarLayout] as a direct child to this view.
 *
 *  In code, you can set [toolbar] instead of declaring an ID.
 */
open class MonetAppBarLayout: AppBarLayout, MonetColorsChangedListener {

    companion object {
        /**
         *  The vertical height of the fade animation based on the scroll offset
         */
        private const val ANIMATION_HEIGHT = 25
    }

    private val monet by lazy {
        MonetCompat.getInstance()
    }

    /**
     *  When true, [MonetAppBarLayout] will automatically attach to the current [MonetCompat] instance
     *  and listen for color changes. If you set this to false, manually call [onMonetColorsChanged]
     *  when Monet updates
     */
    var attachToInstance = true

    /**
     *  The AppBarLayout's child toolbar. Setting this will override any IDs passed via
     *  [R.styleable.MonetAppBarLayout_toolbarId]
     *  Either a toolbar ID set in XML or setting this field is required, or it will crash.
     */
    var toolbar: Toolbar? = null
        get() {
            return if(field != null) field
            else referencedToolbar
        }
        set(value) {
            field = value
            //Update the title font
            toolbar?.setTitleTypeface(typeface)
        }

    private val _toolbar
        get() = toolbar!!

    private var toolbarId: Int? = null

    /**
     *  Finds the toolbar for a passed toolbar ID, if available. Only used if [toolbar] is null.
     */
    private val referencedToolbar by lazy {
        toolbarId?.let {
            val toolbar = findViewById<Toolbar>(it)
                ?: throw MonetAppBarToolbarNotFoundException(it, resources.getResourceName(it))
            toolbar.setTitleTypeface(typeface)
            toolbar
        } ?: run {
            throw MonetAppBarToolbarReferenceException()
        }
    }

    /**
     *  Finds the [CollapsingToolbarLayout] child of this View
     */
    private val collapsingToolbar by lazy {
        children.firstOrNull { it is CollapsingToolbarLayout } as? CollapsingToolbarLayout
            ?: throw MonetAppBarCollapsingToolbarNotFoundException()
    }

    /**
     *  Gets the default [Toolbar] height via [android.R.attr.actionBarSize]. Only used if a custom
     *  height isn't passed via XML ([R.styleable.MonetAppBarLayout_toolbarHeight]) or set in code
     *  ([toolbarHeight])
     */
    private val defaultToolbarHeight by lazy {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics).toFloat()
        }else 0f
    }

    /**
     *  The **raw** height of the child [Toolbar] which will be used to detect the point at which to
     *  switch title style. This should **not** include insets.
     */
    var toolbarHeight: Float = defaultToolbarHeight

    /**
     *  Whether the AppBar is collapsed
     */
    var isCollapsed: Boolean = false

    /**
     *  The [Typeface] to use for the collapsed [Toolbar]
     */
    var typeface: Typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        set(value) {
            field = value
            post {
                toolbar?.setTitleTypeface(typeface)
            }
        }

    /**
     *  The [Typeface] to use for the expanded [CollapsingToolbarLayout]
     */
    var typefaceExpanded = Typeface.DEFAULT
        set(value) {
            field = value
            post {
                collapsingToolbar.setExpandedTitleTypeface(field)
                collapsingToolbar.setCollapsedTitleTypeface(field)
            }
        }

    var stateChangeListener: ((AppBarState) -> Unit)? = null

    /**
     *  Text color for the title
     */
    private val textColorPrimary = context.getTextColorPrimary()

    constructor(context: Context): super(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet, R.attr.appBarLayoutStyle) {
        readAttributes(attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, styleResId: Int): super(context, attributeSet, styleResId){
        readAttributes(attributeSet)
    }

    private fun readAttributes(attributeSet: AttributeSet?){
        if(attributeSet == null) return
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.MonetAppBarLayout)
        attachToInstance = typedArray.getBoolean(R.styleable.MonetAppBarLayout_attachToInstance, true)
        toolbarId = typedArray.getResourceIdOrNull(R.styleable.MonetAppBarLayout_toolbarId)
        toolbarHeight = typedArray.getDimension(R.styleable.MonetAppBarLayout_toolbarHeight, defaultToolbarHeight)
        typedArray.getResourceIdOrNull(R.styleable.MonetAppBarLayout_typeface)?.let {
            typeface = ResourcesCompat.getFont(context, it) ?: typeface
        } ?: run {
            typedArray.getString(R.styleable.MonetAppBarLayout_typeface)?.let {
                typeface = Typeface.create(it, Typeface.NORMAL)
            }
        }
        typedArray.getResourceIdOrNull(R.styleable.MonetAppBarLayout_typefaceExpanded)?.let {
            typefaceExpanded = ResourcesCompat.getFont(context, it) ?: typefaceExpanded
        } ?: run {
            typedArray.getString(R.styleable.MonetAppBarLayout_typefaceExpanded)?.let {
                typefaceExpanded = Typeface.create(it, Typeface.NORMAL)
            }
        }
        typedArray.recycle()
    }

    /**
     *  The target height for switching title is calculated by taking the total height of
     *  the collapsing toolbar, and taking the full toolbar height (inc. padding) off it,
     *  then additionally subtracting the raw toolbar height to account for the overlap.
     */
    private val targetCollapseHeight by lazy {
        collapsingToolbar.measuredHeight - _toolbar.measuredHeight - toolbarHeight
    }

    /**
     *  The point at which to start the fade animation
     */
    private val targetAnimationStartHeight by lazy {
        targetCollapseHeight - ANIMATION_HEIGHT
    }

    private val offsetListener = OnOffsetChangedListener { appBarLayout, verticalOffset ->
        when {
            //Fully hide collapsing toolbar title
            -verticalOffset >= targetCollapseHeight -> {
                setCollapsingToolbarTitleColor(0f)
                stateChangeListener?.invoke(AppBarState.COLLAPSED)
                isCollapsed = true
            }
            //Animation by using offset
            -verticalOffset >= targetAnimationStartHeight -> {
                val fraction = (targetCollapseHeight - (-verticalOffset)) / ANIMATION_HEIGHT.toFloat()
                setCollapsingToolbarTitleColor(fraction)
                isCollapsed = false
            }
            //Fully expanded
            verticalOffset == 0 -> {
                stateChangeListener?.invoke(AppBarState.EXPANDED)
                setCollapsingToolbarTitleColor(1f)
                isCollapsed = false
            }
            //Fully show collapsing toolbar title
            else -> {
                stateChangeListener?.invoke(AppBarState.IDLE)
                setCollapsingToolbarTitleColor(1f)
                isCollapsed = false
            }
        }
        //Sets the Toolbar's title visibility via animation and also switches the current title
        setToolbarTitleVisible(-verticalOffset >= targetCollapseHeight)
        //Show the toolbar background if the collapsing toolbar is anything but fully expanded
        setToolbarBackgroundState(verticalOffset != 0)
    }


    private var isToolbarTitleVisible = false
    /**
     *  Sets the visibility of the [Toolbar] title by animating it to/from [Color.TRANSPARENT].
     *  In order for the [Toolbar] to have the title rather than the [CollapsingToolbarLayout],
     *  we use [CollapsingToolbarLayout.isTitleEnabled]` = false`
     */
    private fun setToolbarTitleVisible(visible: Boolean){
        if(visible == isToolbarTitleVisible) return
        if(visible){
            _toolbar.setToolbarTitleColor(Color.TRANSPARENT, textColorPrimary)
            collapsingToolbar.isTitleEnabled = false
        }else{
            _toolbar.setToolbarTitleColor(textColorPrimary, Color.TRANSPARENT){
                collapsingToolbar.isTitleEnabled = true
                null
            }
        }
        isToolbarTitleVisible = visible
    }

    /**
     *  Sets the alpha of the [CollapsingToolbarLayout]'s titles by setting their color alpha
     *  channel
     */
    private fun setCollapsingToolbarTitleColor(collapsingToolbarAlpha: Float){
        val collapsingToolbarTextColor = getColorWithAlpha(textColorPrimary, collapsingToolbarAlpha)
        collapsingToolbar.setExpandedTitleColor(collapsingToolbarTextColor)
        collapsingToolbar.setCollapsedTitleTextColor(collapsingToolbarTextColor)
    }

    private var isToolbarBackgroundVisible = false
    /**
     *  Shows/hides the child [Toolbar]'s background, it should only be hidden when the
     *  [CollapsingToolbarLayout] is fully expanded.
     *  @param force Ignore the global [isToolbarBackgroundVisible] (to update the color)
     */
    private fun setToolbarBackgroundState(visible: Boolean, force: Boolean = false){
        if((visible == isToolbarBackgroundVisible && !force) || isInEditMode) return
        isToolbarBackgroundVisible = visible
        if(visible) {
            setToolbarBackgroundColor(monet.getBackgroundColorSecondary(context) ?: monet.getBackgroundColor(context))
        }else{
            setToolbarBackgroundColor(monet.getBackgroundColor(context))
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(attachToInstance && !isInEditMode) {
            monet.addMonetColorsChangedListener(this, true)
        }
        addOnOffsetChangedListener(offsetListener)
        if(!isInEditMode) {
            setupCollapsingToolbar()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        monet.removeMonetColorsChangedListener(this)
        removeOnOffsetChangedListener(offsetListener)
    }

    /**
     *  Applies the Large Title text style to the collapsing toolbar's expanded **and** collapsed
     *  states, effectively disabling the collapse animation
     */
    private fun setupCollapsingToolbar(){
        collapsingToolbar.apply {
            setExpandedTitleTextAppearance(R.style.CollapsingToolbarTextLargeTitle)
            setCollapsedTitleTextAppearance(R.style.CollapsingToolbarTextLargeTitle)
            setExpandedTitleTypeface(typefaceExpanded)
            setCollapsedTitleTypeface(typefaceExpanded)
        }
    }

    override fun onMonetColorsChanged(
        monet: MonetCompat,
        monetColors: DynamicColorScheme,
        isInitialChange: Boolean
    ) {
        //Update toolbar background by re-calling state change and forcing it to take effect
        setToolbarBackgroundState(isToolbarBackgroundVisible, true)
        //Set the main background color
        setBackgroundColor(monet.getBackgroundColor(context))
    }

    private var colorAnimation: ValueAnimator? = null
    /**
     *  Animates the toolbar's background color to a given [color]. If the current background
     *  does not conform to [ColorDrawable], no animation will be run and the [color] will be set
     *  immediately.
     */
    private fun setToolbarBackgroundColor(@ColorInt color: Int) = _toolbar.apply {
        colorAnimation?.cancel()
        val currentColor = (background as? ColorDrawable)?.color ?: run {
            //No current background, run without animation
            setBackgroundColor(color)
            return@apply
        }
        colorAnimation = ValueAnimator.ofArgb(currentColor, color).apply {
            duration = 250L
            addUpdateListener {
                setBackgroundColor(it.animatedValue as Int)
            }
            start()
        }
    }

    private var toolbarTitleColorAnimation: ValueAnimator? = null
    /**
     *  Animates a [Toolbar]'s title text color from a given [previousColor] to a required [color].
     *  @param doOnFinish Block to run on [ValueAnimator]'s
     *  [Animator.AnimatorListener.onAnimationEnd]
     */
    private fun Toolbar.setToolbarTitleColor(@ColorInt previousColor: Int, @ColorInt color: Int, doOnFinish: (() -> Any?)? = null) {
        toolbarTitleColorAnimation?.cancel()
        toolbarTitleColorAnimation = ValueAnimator.ofArgb(previousColor, color).apply {
            duration = 150L
            addUpdateListener {
                setTitleTextColor(it.animatedValue as Int)
            }
            invokeOnComplete(doOnFinish)
            start()
        }
    }

    /**
     *  Helper function to run [block] in the [Animator]'s [Animator.AnimatorListener.onAnimationEnd]
     */
    private fun Animator.invokeOnComplete(block: (() -> Any?)?){
        addListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                block?.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }

        })
    }

    private fun TypedArray.getResourceIdOrNull(@StyleableRes index: Int): Int? {
        val value = getResourceId(index, 0)
        return if (value == 0) null
        else value
    }

    private fun Toolbar.setTitleTypeface(typeface: Typeface){
        (getChildAt(0) as? TextView)?.run {
            setTypeface(typeface)
        }
    }

    enum class AppBarState {
        COLLAPSED, EXPANDED, IDLE
    }

    private class MonetAppBarToolbarReferenceException: Exception("You must declare app:toolbarId for MonetAppBarLayout to point to your toolbar, or set MonetAppBarLayout.toolbar")
    private class MonetAppBarToolbarNotFoundException(resourceId: Int, resourceName: String): Exception("Toolbar $resourceName (ID $resourceId) not found as a child of this MonetAppBarLayout")
    private class MonetAppBarCollapsingToolbarNotFoundException: Exception("No CollapsingToolbarLayout found as child of MonetAppBarLayout")

}