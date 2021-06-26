package com.kieronquinn.app.monetcompatsample.ui.screens.container

import android.app.ActivityOptions
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentContainerBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BoundFragment
import com.kieronquinn.app.monetcompatsample.ui.base.NavigationProvider
import com.kieronquinn.app.monetcompatsample.ui.screens.root.RootFragment
import com.kieronquinn.app.monetcompatsample.ui.screens.root.RootSharedViewModel
import com.kieronquinn.app.monetcompatsample.utils.TransitionUtils
import com.kieronquinn.app.monetcompatsample.utils.extensions.navGraphViewModel
import com.kieronquinn.monetcompat.extensions.views.setTint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ContainerFragment: BoundFragment<FragmentContainerBinding>(FragmentContainerBinding::inflate),
    TabLayout.OnTabSelectedListener {

    private val navHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val navController by lazy {
        navHostFragment.navController
    }

    private val containerSharedViewModel by lazy {
        navGraphViewModel<ContainerSharedViewModel>(R.id.nav_graph_main, navController).value
    }

    private val parentNavController by lazy {
        (requireParentFragment().requireParentFragment() as RootFragment).navController
    }

    private val rootSharedViewModel by lazy {
        navGraphViewModel<RootSharedViewModel>(R.id.nav_graph_root, parentNavController).value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        returnTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), false, rootViewId = R.id.root)
        reenterTransition = TransitionUtils.getMaterialSharedAxis(requireContext(), false, rootViewId = R.id.root)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        /*
         *  We could just call applyMonetRecursively here but we don't need it on every view
         *  and we want the background colors on tabs & bottomNav so will do it manually
         *  instead.
         */
        val background = monet.getBackgroundColor(requireContext())
        val secondaryBackground = monet.getBackgroundColorSecondary(requireContext()) ?: background
        val accent = monet.getAccentColor(requireContext())
        view?.setBackgroundColor(background)
        view?.findViewById<TabLayout>(R.id.tabs)?.setTint(accent)
        view?.findViewById<BottomNavigationView>(R.id.bottom_nav)?.setTint(accent, secondaryBackground)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNavigation()
        setupTabs()
        setupBottomNavigation()
        setupAppBar()
    }

    private fun setupNavigation(){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            containerSharedViewModel.notifyDestinationChanged(destination.id)
        }
        lifecycleScope.launchWhenResumed {
            containerSharedViewModel.navigationBus.collect {
                if(it.handled) return@collect
                it.handled = true
                when(it){
                    is NavigationProvider.Navigation.Directions -> {
                        val action = navController.graph.getAction(it.directions.actionId) ?: return@collect
                        if(!navController.popBackStack(action.destinationId, false)){
                            navController.navigate(it.directions)
                        }
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

    private fun setupTabs(){
        with(binding.tabs){
            addTab(newTab().apply {
                text = getString(R.string.bottom_nav_material)
                id = R.id.tab_material
            })
            addTab(newTab().apply {
                text = getString(R.string.bottom_nav_appcompat)
                id = R.id.tab_appcompat
            })
            addTab(newTab().apply {
                text = getString(R.string.bottom_nav_list)
                id = R.id.tab_list
            })
            setBackgroundColor(monet.getBackgroundColor(requireContext()))
        }
        lifecycleScope.launchWhenResumed {
            containerSharedViewModel.currentDestination.collect { id ->
                val selectedItem = when(id) {
                    R.id.materialFragment -> R.id.tab_material
                    R.id.appCompatFragment -> R.id.tab_appcompat
                    R.id.listFragment -> R.id.tab_list
                    else -> null
                } ?: return@collect
                val newTab = binding.tabs.findTabById(selectedItem) ?: return@collect
                //Prevent re-selection as it kills the slide animation
                if(binding.tabs.selectedTabPosition == newTab.position) return@collect
                binding.tabs.selectTab(newTab)
            }
        }
    }

    private fun setupBottomNavigation(){
        with(binding.bottomNav){
            setOnItemSelectedListener {
                if(it.itemId == selectedItemId) return@setOnItemSelectedListener true
                containerSharedViewModel.onTabSelected(it.itemId)
                containerSharedViewModel.scrollToTop()
                true
            }
            setOnItemReselectedListener {
                containerSharedViewModel.scrollToTop()
            }
            ViewCompat.setOnApplyWindowInsetsListener(this){ view, insets ->
                val navigationInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
                val bottomInset = navigationInsets.bottom
                val bottomNavHeight = resources.getDimension(R.dimen.bottom_nav_height)
                updatePadding(left = navigationInsets.left, right = navigationInsets.right, bottom = bottomInset)
                updateLayoutParams<CoordinatorLayout.LayoutParams> { height =
                    (bottomNavHeight + bottomInset).roundToInt()
                }
                insets
            }
        }
        lifecycleScope.launchWhenResumed {
            containerSharedViewModel.currentDestination.collect { id ->
                val selectedItem = when(id) {
                    R.id.materialFragment -> R.id.tab_material
                    R.id.appCompatFragment -> R.id.tab_appcompat
                    R.id.listFragment -> R.id.tab_list
                    else -> null
                } ?: return@collect
                //Prevent re-selection to stop flashing animation
                if(binding.bottomNav.selectedItemId == selectedItem) return@collect
                binding.bottomNav.selectedItemId = selectedItem
            }
        }
    }

    private fun setupAppBar(){
        ViewCompat.setOnApplyWindowInsetsListener(binding.collapsingToolbar){ view, insets ->
            view.updateLayoutParams<AppBarLayout.LayoutParams> {
                val appBarHeight = view.context.resources.getDimension(R.dimen.app_bar_height)
                height = (appBarHeight + insets.getInsets(WindowInsetsCompat.Type.statusBars()).top).roundToInt()
            }
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar){ view, insets ->
            getToolbarHeight()?.let {
                val statusInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
                val toolbarPaddingEnd = resources.getDimension(R.dimen.toolbar_padding_end)
                val topInset = statusInsets.top
                view.updateLayoutParams<CollapsingToolbarLayout.LayoutParams> {
                    height = it + topInset
                }
                view.updatePadding(left = statusInsets.left, top = topInset, right = statusInsets.right)
                view.updatePaddingRelative(end = toolbarPaddingEnd.roundToInt())
            }
            insets
        }
        binding.toolbar.setOnMenuItemClickListener {
            onMenuItemClicked(it)
        }
        binding.collapsingToolbar.run {
            setContentScrimColor(monet.getBackgroundColorSecondary(requireContext()) ?: monet.getBackgroundColor(requireContext()))
            setBackgroundColor(monet.getBackgroundColor(requireContext()))
        }
        lifecycleScope.launch {
            containerSharedViewModel.scrollToTopBus.collect {
                if(!isResumed) return@collect
                binding.appBar.setExpanded(true)
            }
        }
    }

    private fun onMenuItemClicked(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_debug_palette -> {
                rootSharedViewModel.navigateByDirections(
                    ContainerFragmentDirections.actionContainerFragmentToDebugPaletteFragment())
            }
            R.id.menu_color_picker -> {
                rootSharedViewModel.navigateByDirections(
                    ContainerFragmentDirections.actionContainerFragmentToColorPickerBottomSheetFragment()
                )
            }
        }
        return true
    }

    private fun getToolbarHeight(): Int? {
        val typedValue = TypedValue()
        return if (requireContext().theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)) {
            TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
        }else null
    }

    override fun onResume() {
        super.onResume()
        binding.tabs.addOnTabSelectedListener(this)
    }

    override fun onPause() {
        super.onPause()
        binding.tabs.removeOnTabSelectedListener(this)
    }

    internal fun getBottomNavigationView(): BottomNavigationView {
        return binding.bottomNav
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        containerSharedViewModel.onTabSelected(tab?.id ?: return)
        containerSharedViewModel.scrollToTop()
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        containerSharedViewModel.scrollToTop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(!isResumed) return
    }

    private fun TabLayout.findTabById(@IdRes id: Int): TabLayout.Tab? {
        for(i in 0 until tabCount){
            val tabI = getTabAt(i) ?: continue
            if(tabI.id == id) return tabI
        }
        return null
    }

}