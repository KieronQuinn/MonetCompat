package com.kieronquinn.app.monetcompatsample.ui.screens.container

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.ui.base.NavigationProvider
import com.kieronquinn.app.monetcompatsample.ui.screens.appcompat.AppCompatFragmentDirections
import com.kieronquinn.app.monetcompatsample.ui.screens.list.ListFragmentDirections
import com.kieronquinn.app.monetcompatsample.ui.screens.material.MaterialFragmentDirections
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class ContainerSharedViewModel: ViewModel(), NavigationProvider {
    abstract val scrollToTopBus: Flow<Unit>

    abstract fun onTabSelected(id: Int)
    abstract fun scrollToTop()
}

class ContainerSharedViewModelImpl: ContainerSharedViewModel() {

    private val _navigationBus = Channel<NavigationProvider.Navigation>(Channel.BUFFERED)
    override val navigationBus = _navigationBus.receiveAsFlow()

    private val _scrollToTopBus = MutableSharedFlow<Unit>()
    override val scrollToTopBus = _scrollToTopBus.asSharedFlow()

    private val _currentDestination = MutableStateFlow(0)
    override val currentDestination = _currentDestination.asSharedFlow()

    override fun notifyDestinationChanged(id: Int) {
        viewModelScope.launch {
            _currentDestination.emit(id)
        }
    }

    override fun navigateByDirections(directions: NavDirections) {
        viewModelScope.launch {
            _navigationBus.send(NavigationProvider.Navigation.Directions(directions))
        }
    }

    override fun navigateUp() {
        viewModelScope.launch {
            _navigationBus.send(NavigationProvider.Navigation.Up)
        }
    }

    override fun navigatePopUpTo(id: Int, inclusive: Boolean) {
        viewModelScope.launch {
            _navigationBus.send(NavigationProvider.Navigation.PopUpTo(id, inclusive))
        }
    }

    override fun onTabSelected(id: Int) {
        viewModelScope.launch {
            //Prevent firing when the tab is already selected (unique case as we have two sets of "tabs")
            val newTab = when(id){
                R.id.tab_material -> R.id.materialFragment
                R.id.tab_appcompat -> R.id.appCompatFragment
                R.id.tab_list -> R.id.listFragment
                else -> null
            }
            if(_currentDestination.value == newTab) return@launch
            when(id){
                R.id.tab_material -> navigateByDirections(MaterialFragmentDirections.actionGlobalMaterialFragment())
                R.id.tab_appcompat -> navigateByDirections(AppCompatFragmentDirections.actionGlobalAppCompatFragment())
                R.id.tab_list -> navigateByDirections(ListFragmentDirections.actionGlobalListFragment())
            }
        }
    }

    override fun scrollToTop() {
        viewModelScope.launch {
            _scrollToTopBus.emit(Unit)
        }
    }

}