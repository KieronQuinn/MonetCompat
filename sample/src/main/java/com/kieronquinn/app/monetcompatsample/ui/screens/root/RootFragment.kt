package com.kieronquinn.app.monetcompatsample.ui.screens.root

import android.app.ActivityOptions
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentRootBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BoundFragment
import com.kieronquinn.app.monetcompatsample.ui.base.NavigationProvider
import com.kieronquinn.app.monetcompatsample.utils.extensions.navGraphViewModel
import kotlinx.coroutines.flow.collect

class RootFragment: BoundFragment<FragmentRootBinding>(FragmentRootBinding::inflate) {

    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
    }

    internal val navController by lazy {
        navHostFragment.navController
    }

    private val rootSharedViewModel by lazy {
        navGraphViewModel<RootSharedViewModel>(R.id.nav_graph_root, navController).value
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupBack()
    }

    private fun setupNavigation(){
        lifecycleScope.launchWhenResumed {
            rootSharedViewModel.navigationBus.collect {
                if(it.handled) return@collect
                it.handled = true
                when(it){
                    is NavigationProvider.Navigation.Directions -> {
                        navController.navigate(it.directions)
                    }
                    is NavigationProvider.Navigation.Up -> navController.navigateUp()
                    is NavigationProvider.Navigation.PopUpTo -> navController.popBackStack(it.id, it.inclusive)
                    is NavigationProvider.Navigation.Intent -> {
                        val bundle = if(it.sharedElement){
                            ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle()
                        }else null
                        startActivity(it.intent, bundle)
                    }
                }
            }
        }
    }

    private fun setupBack(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(!navController.navigateUp()){
                    requireActivity().finish()
                }
            }
        })
    }

}