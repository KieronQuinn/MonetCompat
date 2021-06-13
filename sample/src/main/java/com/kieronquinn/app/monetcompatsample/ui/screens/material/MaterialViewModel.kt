package com.kieronquinn.app.monetcompatsample.ui.screens.material

import androidx.annotation.IdRes
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.ui.base.BaseSampleTabViewModel

class MaterialViewModel: BaseSampleTabViewModel() {

    override var checkboxChecked = false
    @IdRes
    override var radioCheckedItem = R.id.radio_1
    override var switchChecked = false
    override var sliderProgress = 50
    override var primarySwitchChecked = false

}