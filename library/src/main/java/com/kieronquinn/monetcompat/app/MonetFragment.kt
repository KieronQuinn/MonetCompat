package com.kieronquinn.monetcompat.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.core.MonetFragmentAccessException
import com.kieronquinn.monetcompat.interfaces.MonetColorsChangedListener
import dev.kdrag0n.monet.theme.DynamicColorScheme

abstract class MonetFragment: Fragment, MonetColorsChangedListener {

    constructor(): super()
    constructor(layoutResId: Int): super(layoutResId)

    private var _monet: MonetCompat? = null
    val monet: MonetCompat
        get() {
            return if(_monet == null) throw MonetFragmentAccessException()
            else _monet!!
        }

    /**
     *  Set to true to call [MonetCompat.updateMonetColors] when the Fragment is created.
     *  If disabled, [MonetColorsChangedListener.onMonetColorsChanged] will still be called when
     *  the listener is attached, but using the current colors (if available)
     */
    open val updateOnCreate = false

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        MonetCompat.setup(requireContext())
        _monet = MonetCompat.getInstance()
        monet.addMonetColorsChangedListener(this, !updateOnCreate)
        if(updateOnCreate) monet.updateMonetColors()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        monet.removeMonetColorsChangedListener(this)
        _monet = null
    }

    /**
     *  Called when Monet compat has new colors for use, usually when the Fragment loads
     *  or the user changes their wallpaper while the app is running.
     */
    @CallSuper
    override fun onMonetColorsChanged(monet: MonetCompat, monetColors: DynamicColorScheme, isInitialChange: Boolean){
        //Left for future use if needed
    }

}