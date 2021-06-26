package com.kieronquinn.monetcompat.app

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ListMenuItemView
import com.kieronquinn.monetcompat.core.MonetActivityAccessException
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.DynamicColorScheme

abstract class MonetCompatActivity : AppCompatActivity(), MonetColorsChangedListener {

    private var _monet: MonetCompat? = null
    val monet: MonetCompat
        get() {
            return if(_monet == null) throw MonetActivityAccessException()
            else _monet!!
        }

    /**
     *  Set to true to automatically apply the Monet background color to the Window's background
     *  Requires [recreateMode] to be `false`
     */
    open val applyBackgroundColorToWindow = false

    /**
     *  When s to true to automatically apply the Monet background color to a Toolbar's dropdown menu
     *  background
     */
    open val applyBackgroundColorToMenu = true

    /**
     *  Set to true to call [MonetCompat.updateMonetColors] when the Activity is created.
     *  If disabled, [MonetColorsChangedListener.onMonetColorsChanged] will still be called when
     *  the listener is attached, but using the current colors (if available)
     */
    open val updateOnCreate = true

    /**
     *  When set to true, you can use [onMonetColorsChanged] to create a Configuration change style
     *  setup - if `isInitialChange` is `false`, simply call `recreate()` to recreate the activity.
     *  Please note that as [recreate] recreates the entire activity, this is not recommended unless
     *  you have *already* got Monet colors, for example using
     */
    open val recreateMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //This only sets up if needed, otherwise it'll do nothing
        MonetCompat.setup(this)
        _monet = MonetCompat.getInstance()
        monet.addMonetColorsChangedListener(this, (!updateOnCreate && !recreateMode))
        if(updateOnCreate) monet.updateMonetColors()
    }

    override fun onDestroy() {
        super.onDestroy()
        monet.removeMonetColorsChangedListener(this)
        _monet = null
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (applyBackgroundColorToMenu && name == ListMenuItemView::class.java.name) {
            //Toolbar dropdown menu list item
            (parent?.parent as? View)?.run {
                val background = monet.getBackgroundColorSecondary(context)
                    ?: monet.getBackgroundColor(context)
                backgroundTintList = ColorStateList.valueOf(background)
            }
        }
        val view = super.onCreateView(parent, name, context, attrs)
        return view
    }

    /**
     *  Called when Monet compat has new colors for use, usually when the Activity loads
     *  or the user changes their wallpaper while the app is running.
     */
    @CallSuper
    override fun onMonetColorsChanged(monet: MonetCompat, monetColors: DynamicColorScheme, isInitialChange: Boolean) {
        if(recreateMode && !isInitialChange){
            recreate()
            return
        }
        if(applyBackgroundColorToWindow){
            window.setBackgroundDrawable(ColorDrawable(monet.getBackgroundColor(this)))
        }
    }

}