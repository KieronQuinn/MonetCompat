package com.kieronquinn.app.monetcompatsample.ui.screens.material

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.callbacks.onShow
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.kieronquinn.app.monetcompatsample.R
import com.kieronquinn.app.monetcompatsample.databinding.FragmentMaterialBinding
import com.kieronquinn.app.monetcompatsample.ui.base.BaseTabFragment
import com.kieronquinn.app.monetcompatsample.ui.screens.container.ContainerFragment
import com.kieronquinn.monetcompat.extensions.applyMonet
import com.kieronquinn.monetcompat.extensions.views.MonetAutoThemeableViews
import com.kieronquinn.monetcompat.extensions.views.applyMonetRecursively
import com.kieronquinn.monetcompat.extensions.views.customThemeableView
import com.kieronquinn.monetcompat.extensions.views.enableStretchOverscroll
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class MaterialFragment: BaseTabFragment<FragmentMaterialBinding>(FragmentMaterialBinding::inflate) {

    companion object {
        private const val KEY_SCROLL_Y = "scroll_y"
    }

    private val viewModel by viewModel<MaterialViewModel>()

    private val textViewAccentTheming = customThemeableView<TextView>(TextView::class, AppCompatTextView::class, MaterialTextView::class){ textView, monet ->
        if(textView.id != R.id.tv_accent) return@customThemeableView
        textView.setTextColor(monet.getAccentColor(textView.context))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.applyMonetRecursively(*MonetAutoThemeableViews.ALL, customThemeables = listOf(textViewAccentTheming))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding){
            button.setOnClickListener {
                showDialog()
            }
            buttonSB.setOnClickListener {
                showSnackbar()
            }
            buttonMD.setOnClickListener {
                showMaterialDialog()
            }
            buttonMDBS.setOnClickListener {
                showMaterialDialogBottomSheet()
            }
            checkbox.isChecked = viewModel.checkboxChecked
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                viewModel.checkboxChecked = isChecked
            }
            radioGroup.check(viewModel.radioCheckedItem)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                viewModel.radioCheckedItem = checkedId
            }
            switch1.isChecked = viewModel.switchChecked
            switch1.setOnCheckedChangeListener { _, isChecked ->
                viewModel.switchChecked = isChecked
            }
            slider.value = viewModel.sliderProgress / 100f
            switch2.isChecked = viewModel.primarySwitchChecked
            switch2.setOnCheckedChangeListener { _, isChecked ->
                viewModel.primarySwitchChecked = isChecked
            }
        }
        setupInsets()
        setupScrollView(savedInstanceState?.getInt(KEY_SCROLL_Y) ?: 0)
    }

    private fun setupInsets(){
        val bottomNavPadding = resources.getDimension(R.dimen.bottom_nav_height)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root){ view, insets ->
            val bottomInset = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
            binding.root.updatePadding(bottom = (bottomInset + bottomNavPadding).roundToInt())
            insets
        }
    }

    private fun setupScrollView(savedScrollY: Int){
        binding.root.scrollTo(0, savedScrollY)
        binding.root.enableStretchOverscroll()
        lifecycleScope.launch {
            containerSharedViewModel.scrollToTopBus.collect {
                if(!isResumed) return@collect
                binding.root.smoothScrollTo(0, 0)
            }
        }
    }

    override fun getWrappedContext(): Context {
        return ContextThemeWrapper(requireContext(), R.style.Theme_MonetCompat)
    }

    private fun showDialog(){
        val dialog = AlertDialog.Builder(getWrappedContext()).apply {
            setTitle("Alert Dialog")
            setMessage("Content")
            setPositiveButton("Positive", null)
            setNeutralButton("Neutral", null)
            setNegativeButton("Negative", null)
        }.show().applyMonet()
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).isEnabled = false
    }

    private fun showMaterialDialog(){
        MaterialDialog(requireContext()).show {
            title(text = "MD Dialog")
            message(text = "Content")
            positiveButton(text = "Positive"){}
            negativeButton(text = "Negative"){}
            neutralButton(text = "Neutral"){}
            onShow {
                getActionButton(WhichButton.NEUTRAL).isEnabled = false
            }
            applyMonet()
        }
    }

    private fun showMaterialDialogBottomSheet(){
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = "MD Bottom Sheet Dialog")
            message(text = "Content")
            positiveButton(text = "Positive"){}
            negativeButton(text = "Negative"){}
            neutralButton(text = "Neutral"){}
            onShow {
                getActionButton(WhichButton.NEUTRAL).isEnabled = false
            }
            applyMonet(true)
        }
    }

    private fun showSnackbar(){
        val bottomNavigationView = (parentFragment?.parentFragment as? ContainerFragment)?.getBottomNavigationView() ?: return
        Snackbar.make(binding.root, "Snackbar", Snackbar.LENGTH_LONG).apply {
            setAction("Action"){}
            applyMonet()
            anchorView = bottomNavigationView
        }.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if(!isResumed) return
        outState.putInt(KEY_SCROLL_Y, binding.root.scrollY)
    }

}