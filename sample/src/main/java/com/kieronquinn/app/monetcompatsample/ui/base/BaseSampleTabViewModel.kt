package com.kieronquinn.app.monetcompatsample.ui.base

import androidx.lifecycle.ViewModel

abstract class BaseSampleTabViewModel: ViewModel() {

    abstract var checkboxChecked: Boolean
    abstract var radioCheckedItem: Int
    abstract var switchChecked: Boolean
    abstract var sliderProgress: Int
    abstract var primarySwitchChecked: Boolean

}