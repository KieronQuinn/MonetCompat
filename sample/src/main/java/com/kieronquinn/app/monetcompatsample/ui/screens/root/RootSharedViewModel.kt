package com.kieronquinn.app.monetcompatsample.ui.screens.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.kieronquinn.app.monetcompatsample.ui.base.NavigationProvider
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class RootSharedViewModel: ViewModel(), NavigationProvider

class RootSharedViewModelImpl: RootSharedViewModel(){

    private val _navigationBus = Channel<NavigationProvider.Navigation>(Channel.BUFFERED)
    override val navigationBus = _navigationBus.receiveAsFlow()

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

}

