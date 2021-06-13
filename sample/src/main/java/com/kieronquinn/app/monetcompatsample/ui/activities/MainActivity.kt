package com.kieronquinn.app.monetcompatsample.ui.activities

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.Window
import androidx.appcompat.view.menu.ListMenuItemView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.ActivityMainBinding
import com.kieronquinn.app.monetcompatsample.utils.TransitionUtils
import com.kieronquinn.monetcompat.app.MonetCompatActivity
import com.kieronquinn.monetcompat.core.MonetCompat
import com.kieronquinn.monetcompat.extensions.views.applyMonetRecursively
import dev.kdrag0n.monet.theme.DynamicColorScheme

class MainActivity : MonetCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        window.exitTransition = TransitionUtils.getMaterialSharedAxis(this, false)
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        lifecycleScope.launchWhenResumed {
            monet.awaitMonetReady()
            window.setBackgroundDrawable(ColorDrawable(monet.getBackgroundColor(this@MainActivity)))
            setContentView(R.layout.activity_main)
        }
    }

    override fun onMonetColorsChanged(
        monet: MonetCompat,
        monetColors: DynamicColorScheme,
        isInitialChange: Boolean
    ) {
        super.onMonetColorsChanged(monet, monetColors, isInitialChange)
        if(!isInitialChange){
            recreate()
        }
    }

}