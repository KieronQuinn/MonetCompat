package com.kieronquinn.app.monetcompatsample.ui.screens.debugpalette

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.view.ContextThemeWrapper
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentDebugPaletteBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BoundFragment
import com.kieronquinn.app.monetcompatsample.utils.TransitionUtils
import com.kieronquinn.monetcompat.extensions.views.enableStretchOverscroll
import kotlin.math.roundToInt

class DebugPaletteFragment : BoundFragment<FragmentDebugPaletteBinding>(FragmentDebugPaletteBinding::inflate) {

    private val debugListPadding by lazy {
        resources.getDimension(R.dimen.debug_list_padding).toInt()
    }

    private val adapter by lazy {
        DebugPaletteAdapter(getWrappedContext())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        exitTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), true, rootViewId = R.id.root)
        enterTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), true, rootViewId = R.id.root)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView(savedInstanceState)
        view.setBackgroundColor(monet.getBackgroundColor(view.context))
    }

    override fun getWrappedContext(): Context {
        return ContextThemeWrapper(requireContext(), R.style.Theme_MonetCompat)
    }

    private fun setupToolbar() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentDebugPaletteToolbar) { view, insets ->
            getToolbarHeight()?.let {
                val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val toolbarPaddingEnd = resources.getDimension(R.dimen.toolbar_padding_end)
                val topInset = statusInsets.top
                view.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    height = it + topInset
                }
                view.updatePadding(
                    left = statusInsets.left,
                    top = topInset,
                    right = statusInsets.right
                )
                view.updatePaddingRelative(end = toolbarPaddingEnd.roundToInt())
            }
            insets
        }
        with(binding.fragmentDebugPaletteToolbar) {
            setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            setupWithScrollableView(binding.fragmentDebugPaletteRecyclerview)
        }
    }

    private fun setupRecyclerView(savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.fragmentDebugPaletteRecyclerview) { view, insets ->
            val bottomInset =
                insets.getInsets(WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.ime()).bottom
            view.updatePadding(bottom = bottomInset + debugListPadding)
            insets
        }
        with(binding.fragmentDebugPaletteRecyclerview) {
            layoutManager = LinearLayoutManager(context)
            adapter = this@DebugPaletteFragment.adapter
            enableStretchOverscroll()
        }
        adapter.getColors(requireContext())
        //Fixes a bug where the list will sometimes pop below the toolbar when created
        if(savedInstanceState == null){
            binding.fragmentDebugPaletteRecyclerview.run {
                post {
                    scrollToPosition(0)
                }
            }
        }
    }

    private fun getToolbarHeight(): Int? {
        val typedValue = TypedValue()
        return if (requireContext().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                typedValue,
                true
            )
        ) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
        } else null
    }

}