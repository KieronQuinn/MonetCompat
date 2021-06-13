package com.kieronquinn.app.monetcompatsample.ui.base

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import kotlinx.coroutines.flow.Flow

interface NavigationProvider {

    val currentDestination: Flow<Int>
    val navigationBus: Flow<Navigation>

    fun notifyDestinationChanged(@IdRes id: Int)

    fun navigateByDirections(directions: NavDirections)
    fun navigateUp()
    fun navigatePopUpTo(id: Int, inclusive: Boolean = false)

    sealed class Navigation(var handled: Boolean = false) {
        data class Directions(val directions: NavDirections): Navigation()
        object Up: Navigation()
        data class PopUpTo(val id: Int, val inclusive: Boolean): Navigation()
        data class Intent(val intent: android.content.Intent, val sharedElement: Boolean = false): Navigation()
    }

}