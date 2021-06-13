package com.kieronquinn.app.monetcompatsample

import android.app.Application
import com.kieronquinn.app.monetcompatsample.ui.screens.appcompat.AppCompatViewModel
import com.kieronquinn.app.monetcompatsample.ui.screens.container.ContainerSharedViewModel
import com.kieronquinn.app.monetcompatsample.ui.screens.container.ContainerSharedViewModelImpl
import com.kieronquinn.app.monetcompatsample.ui.screens.list.ListViewModel
import com.kieronquinn.app.monetcompatsample.ui.screens.list.ListViewModelImpl
import com.kieronquinn.app.monetcompatsample.ui.screens.material.MaterialViewModel
import com.kieronquinn.app.monetcompatsample.ui.screens.root.RootSharedViewModel
import com.kieronquinn.app.monetcompatsample.ui.screens.root.RootSharedViewModelImpl
import com.kieronquinn.monetcompat.core.MonetCompat
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class MonetCompatSample: Application() {

    private val viewModels = module {
        viewModel<RootSharedViewModel>{ RootSharedViewModelImpl() }
        viewModel<ContainerSharedViewModel>{ ContainerSharedViewModelImpl() }
        viewModel { MaterialViewModel() }
        viewModel { AppCompatViewModel() }
        viewModel<ListViewModel>{ ListViewModelImpl() }
    }

    override fun onCreate() {
        super.onCreate()
        /**
         *  Enables Android 8.0 and below support
         *  On some devices, the user will need to grant the READ_EXTERNAL_STORAGE permission
         *  for this to work, for convenience's sake in this sample we're just going to rely on
         *  them granting it themselves and restarting - it will simply use the default otherwise.
         */
        MonetCompat.enablePaletteCompat()
        MonetCompat.debugLog = true

        startKoin {
            androidContext(this@MonetCompatSample)
            modules(viewModels)
        }
    }

}