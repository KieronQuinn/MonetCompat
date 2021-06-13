package com.kieronquinn.app.monetcompatsample.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kieronquinn.monetcompat.app.MonetFragment

abstract class BoundFragment<T: ViewBinding>(private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T): MonetFragment() {

    private var _binding: T? = null
    internal val binding
        get() = _binding ?: throw NullPointerException("Binding is null")

    open fun getWrappedContext(): Context {
        return requireContext()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val wrappedInflater = inflater.cloneInContext(getWrappedContext())
        _binding = inflate.invoke(wrappedInflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}