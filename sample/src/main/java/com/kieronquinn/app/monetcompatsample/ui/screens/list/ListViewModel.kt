package com.kieronquinn.app.monetcompatsample.ui.screens.list

import androidx.lifecycle.ViewModel

abstract class ListViewModel: ViewModel() {

    internal abstract val items: List<ListAdapter.Item>

}

class ListViewModelImpl: ListViewModel(){

    override val items by lazy {
        ArrayList<ListAdapter.Item>().apply {
            for (i in 1 until 41) {
                add(ListAdapter.Item("Item $i", false))
            }
        }
    }

}