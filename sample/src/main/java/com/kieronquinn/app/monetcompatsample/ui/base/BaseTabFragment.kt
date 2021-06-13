package com.kieronquinn.app.monetcompatsample.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.ui.screens.container.ContainerFragment
import com.kieronquinn.app.monetcompatsample.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.monetcompatsample.utils.TransitionUtils
import com.kieronquinn.app.monetcompatsample.utils.extensions.navGraphViewModel

abstract class BaseTabFragment<T: ViewBinding>(inflate: (LayoutInflater, ViewGroup?, Boolean) -> T): BoundFragment<T>(inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        returnTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), false, rootViewId = R.id.root)
        reenterTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), false, rootViewId = R.id.root)
        exitTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), true, rootViewId = R.id.root)
        enterTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), true, rootViewId = R.id.root)
    }

    internal val containerSharedViewModel by lazy {
        navGraphViewModel<ContainerSharedViewModel>(R.id.nav_graph_main).value
    }

}