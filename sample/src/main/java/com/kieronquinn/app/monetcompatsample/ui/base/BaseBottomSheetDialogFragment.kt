package com.kieronquinn.app.monetcompatsample.ui.base

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.monetcompat.core.MonetCompat

abstract class BaseBottomSheetDialogFragment<T: ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T): BottomSheetDialogFragment() {

    internal val monet by lazy {
        MonetCompat.getInstance()
    }

    private var _binding: T? = null
    internal val binding
        get() = _binding ?: throw NullPointerException("Binding is null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = inflate.invoke(layoutInflater, container, false)
        dialog?.setOnShowListener {
            (binding.root.parent as View).backgroundTintList = ColorStateList.valueOf(monet.getBackgroundColor(requireContext()))
        }
        //Light navigation background not supported on < 8.1
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
            dialog?.window?.navigationBarColor = monet.getBackgroundColor(requireContext())
        }else{
            dialog?.window?.navigationBarColor = Color.BLACK
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun getTheme(): Int {
        return if(requireContext().isDarkMode){
            R.style.BaseBottomSheetDialog_Dark
        }else{
            R.style.BaseBottomSheetDialog
        }
    }

    private val Context.isDarkMode: Boolean
        get() {
            return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                Configuration.UI_MODE_NIGHT_UNDEFINED -> false
                else -> false
            }
        }

}